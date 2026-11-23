@QueryEntities({
  A.class,
  Tenant.class,
  DefaultRevisionEntity.class,
  Delegate3Test.Point.class,
  Delegate3Test.Polygon.class
})
package fluentq.apt.domain;

import org.hibernate.envers.DefaultRevisionEntity;

import fluentq.core.annotations.QueryEntities;
import fluentq.core.domain.A;
import fluentq.core.domain.Tenant;
