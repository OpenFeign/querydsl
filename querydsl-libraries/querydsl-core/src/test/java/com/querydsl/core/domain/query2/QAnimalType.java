/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.core.domain.query2;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.domain.Animal;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.TimePath;
import java.io.Serial;
import java.sql.Time;
import java.util.Date;

/** QAnimal is a Querydsl query type for Animal */
public class QAnimalType extends EntityPathBase<Animal> {

  @Serial private static final long serialVersionUID = 781156670;

  public static final QAnimalType animal = new QAnimalType("animal");

  public final BooleanPath alive = createBoolean("alive");

  public final DateTimePath<Date> birthdate = createDateTime("birthdate", java.util.Date.class);

  public final NumberPath<Double> bodyWeight = createNumber("bodyWeight", Double.class);

  public final DatePath<java.sql.Date> dateField = createDate("dateField", java.sql.Date.class);

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final StringPath name = createString("name");

  public final TimePath<Time> timeField = createTime("timeField", java.sql.Time.class);

  public final NumberPath<Integer> toes = createNumber("toes", Integer.class);

  public final NumberPath<Integer> weight = createNumber("weight", Integer.class);

  public QAnimalType(String variable) {
    super(Animal.class, forVariable(variable));
  }

  public QAnimalType(BeanPath<? extends Animal> entity) {
    super(entity.getType(), entity.getMetadata());
  }

  public QAnimalType(PathMetadata metadata) {
    super(Animal.class, metadata);
  }
}
