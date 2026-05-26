package fluentq.jpa.domain5;

import fluentq.core.annotations.QueryInit;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class MyEntity extends MyMappedSuperclass {

  @Id private int id;

  @Embedded
  @QueryInit("*")
  private MyEmbeddedAttribute embeddedAttribute;
}
