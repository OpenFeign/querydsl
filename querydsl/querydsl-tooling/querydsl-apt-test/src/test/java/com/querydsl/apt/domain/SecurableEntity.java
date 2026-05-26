package com.querydsl.apt.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.TableGenerator;

/**
 * This is an example of using system ACL function. Note, field id is required, abstract function
 * getId must also be implemented.
 */
@Entity
public class SecurableEntity extends AbstractSecurable<Long, Long> {

  private static final long serialVersionUID = 3197097608363811501L;

  @Id
  @TableGenerator(name = "SECUREENTITY_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "SECUREENTITY_SEQ")
  private Long securableEntityId;

  @Column(length = 50)
  private String name;

  // public Long getId() {
  // return getSecurableEntityId();
  // }

}
