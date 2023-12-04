package com.querydsl.collections;

import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.group.GroupBy.map;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.core.group.GroupBy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class GroupBy2Test {

  //    select u1.id,u1.name,r1.id,r1.name,s1.name from users u1
  //    join roles r1 on u1.role = r1.id
  //    join security_groups s1 on r1.secgroup = s1.id

  @QueryEntity
  public static class User {
    public Long id;
    public String name;
    public List<Role> roles;
  }

  @QueryEntity
  public static class Role {
    public Long id;
    public String name;
    public List<SecurityGroup> groups;
  }

  @QueryEntity
  public static class SecurityGroup {
    public Long id;
    public String name;

    public SecurityGroup(Long id, String name) {
      this.id = id;
      this.name = name;
    }
  }

  public static class UserDto {
    public Long id;
    public String name;
    public List<Long> roleIds;
    public List<String> roleNames;
    public List<Long> secIds;

    @QueryProjection
    public UserDto(
        Long id, String name, List<Long> roleIds, List<String> roleNames, List<Long> secIds) {
      this.id = id;
      this.name = name;
      this.roleIds = roleIds;
      this.roleNames = roleNames;
      this.secIds = secIds;
    }

    @QueryProjection
    public UserDto(Long id, String name, Map<Long, String> roles, Map<Long, String> groups) {
      this.id = id;
      this.name = name;
      this.roleIds = new ArrayList<>(roles.keySet());
      this.roleNames = new ArrayList<>(roles.values());
      this.secIds = new ArrayList<>(groups.keySet());
    }
  }

  private List<User> users;

  @Before
  public void setUp() {
    Role r1 = new Role();
    r1.id = 1L;
    r1.name = "User";
    r1.groups = Arrays.asList(new SecurityGroup(1L, "User 1"));

    Role r2 = new Role();
    r2.id = 2L;
    r2.name = null; // NOTE this is null on purpose
    r2.groups = Arrays.asList(new SecurityGroup(2L, "Admin 1"), new SecurityGroup(3L, "Admin 2"));

    User u1 = new User();
    u1.id = 3L;
    u1.name = "Bob";
    u1.roles = Arrays.asList(r1);

    User u2 = new User();
    u2.id = 32L;
    u2.name = "Ann";
    u2.roles = Arrays.asList(r1, r2);

    users = Arrays.asList(u1, u2);
  }

  @Test
  public void test() {
    QGroupBy2Test_User user = QGroupBy2Test_User.user;
    QGroupBy2Test_Role role = QGroupBy2Test_Role.role;
    QGroupBy2Test_SecurityGroup group = QGroupBy2Test_SecurityGroup.securityGroup;

    Map<Long, UserDto> userDtos =
        CollQueryFactory.from(user, users)
            .innerJoin(user.roles, role)
            .innerJoin(role.groups, group)
            .transform(
                GroupBy.groupBy(user.id)
                    .as(
                        new QGroupBy2Test_UserDto(
                            user.id, user.name, list(role.id), list(role.name), list(group.id))));

    UserDto dto1 = userDtos.get(3L);
    assertThat(dto1.roleIds).hasSize(1);
    assertThat(dto1.roleNames).hasSize(1);
    assertThat(dto1.secIds).hasSize(1);

    UserDto dto2 = userDtos.get(32L);
    assertThat(dto2.roleIds).hasSize(3);
    assertThat(dto2.roleNames).hasSize(1);
    assertThat(dto2.secIds).hasSize(3);
  }

  @Test
  public void test2() {
    QGroupBy2Test_User user = QGroupBy2Test_User.user;
    QGroupBy2Test_Role role = QGroupBy2Test_Role.role;
    QGroupBy2Test_SecurityGroup group = QGroupBy2Test_SecurityGroup.securityGroup;

    Map<Long, UserDto> userDtos =
        CollQueryFactory.from(user, users)
            .innerJoin(user.roles, role)
            .innerJoin(role.groups, group)
            .transform(
                GroupBy.groupBy(user.id)
                    .as(
                        new QGroupBy2Test_UserDto(
                            user.id,
                            user.name,
                            map(role.id, role.name),
                            map(group.id, group.name))));

    UserDto dto1 = userDtos.get(3L);
    assertThat(dto1.roleIds).hasSize(1);
    assertThat(dto1.roleNames).hasSize(1);
    assertThat(dto1.secIds).hasSize(1);

    UserDto dto2 = userDtos.get(32L);
    assertThat(dto2.roleIds).hasSize(2);
    assertThat(dto2.roleNames).hasSize(2);
    assertThat(dto2.secIds).hasSize(3);
  }
}
