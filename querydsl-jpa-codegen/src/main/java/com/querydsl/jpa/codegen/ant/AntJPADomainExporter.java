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
package com.querydsl.jpa.codegen.ant;

import com.querydsl.jpa.codegen.JPADomainExporter;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.metamodel.Metamodel;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * {@code AntJPADomainExporter} exports JPA 2 metamodels to Querydsl expression types
 *
 * @author 200003548
 */
public class AntJPADomainExporter {

  /** Additional property to use when creating the JPA entity manager factory */
  public static class Property {
    private String name;
    private String value;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }

  /** Set of additional properties to use when creating the JPA entity manager factory */
  public static class Configuration {
    private Map<String, String> properties = new HashMap<String, String>();

    public Map<String, String> getProperties() {
      return properties;
    }

    public void addConfiguredProperty(Property property) {
      properties.put(property.getName(), property.getValue());
    }
  }

  /**
   * Set of additional properties to use when creating the JPA entity manager factory (no default -
   * not required)
   */
  private Configuration configuration;

  /** Name prefix for generated query types (default: "Q") */
  private String namePrefix = "Q";

  /** Name suffix for generated query types (default: "") */
  private String nameSuffix = "";

  /** Target folder in which to generate query types (no default - required) */
  private String targetFolder;

  /** Name of persistence unit from which to generate query types (no default - required) */
  private String persistenceUnitName;

  private Set<File> generatedFiles = Collections.emptySet();

  public Configuration getConfiguration() {
    return configuration;
  }

  public void addConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

  public String getNamePrefix() {
    return namePrefix;
  }

  public void setNamePrefix(String namePrefix) {
    this.namePrefix = namePrefix;
  }

  public String getNameSuffix() {
    return nameSuffix;
  }

  public void setNameSuffix(String nameSuffix) {
    this.nameSuffix = nameSuffix;
  }

  public String getTargetFolder() {
    return targetFolder;
  }

  public void setTargetFolder(String targetFolder) {
    this.targetFolder = targetFolder;
  }

  public String getPersistenceUnitName() {
    return persistenceUnitName;
  }

  public void setPersistenceUnitName(String persistenceUnitName) {
    this.persistenceUnitName = persistenceUnitName;
  }

  public Set<File> getGeneratedFiles() {
    return generatedFiles;
  }

  /**
   * Exports the named persistence unit's metamodel to Querydsl query types. Expects to be called by
   * Ant via name convention using a method with signature public void execute().
   */
  public void execute() {
    // We can assume we have the named persistence unit and its mapping file in our classpath,
    // but we may have to allow some properties in that persistence unit to be overridden before
    // we can successfully get that persistence unit's metamodel.
    Map<String, String> properties = (configuration != null) ? configuration.getProperties() : null;
    EntityManagerFactory emf =
        Persistence.createEntityManagerFactory(persistenceUnitName, properties);

    // Now we can get the persistence unit's metamodel and export it to Querydsl query types.
    Metamodel configuration = emf.getMetamodel();
    JPADomainExporter exporter =
        new JPADomainExporter(namePrefix, nameSuffix, new File(targetFolder), configuration);
    try {
      exporter.execute();
      generatedFiles = exporter.getGeneratedFiles();
    } catch (IOException e) {
      throw new RuntimeException("Error in JPADomainExporter", e);
    }
  }
}
