/*
 * Copyright 2015, The FluentQ Team (http://www.fluentq.com/team)
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
package fluentq.apt;

/**
 * APT options supported by FluentQ
 *
 * @author tiwe
 */
public final class APTOptions {

  /** set whether default variables are created (default: true) */
  public static final String FLUENTQ_CREATE_DEFAULT_VARIABLE = "fluentq.createDefaultVariable";

  /** set the prefix for query types (default: Q) */
  public static final String FLUENTQ_PREFIX = "fluentq.prefix";

  /** set a suffix for query types (default: empty) */
  public static final String FLUENTQ_SUFFIX = "fluentq.suffix";

  /** set a suffix for query type packages (default: empty) */
  public static final String FLUENTQ_PACKAGE_SUFFIX = "fluentq.packageSuffix";

  /** enable accessors for direct key based map access (default: false) */
  public static final String FLUENTQ_MAP_ACCESSORS = "fluentq.mapAccessors";

  /** enable accessors for direct indexed list access (default: false) */
  public static final String FLUENTQ_LIST_ACCESSORS = "fluentq.listAccessors";

  /** enable reference field accessors (default: false) */
  public static final String FLUENTQ_ENTITY_ACCESSORS = "fluentq.entityAccessors";

  /** Set whether fields are used as metadata source (default: true) */
  public static final String FLUENTQ_USE_FIELDS = "fluentq.useFields";

  /** Set whether accessors are used as metadata source (default: true) */
  public static final String FLUENTQ_USE_GETTERS = "fluentq.useGetters";

  /** comma separated list of packages to be excluded from code generation (default: none) */
  public static final String FLUENTQ_EXCLUDED_PACKAGES = "fluentq.excludedPackages";

  /** comma separated list of class names to be excluded from code generation (default: none) */
  public static final String FLUENTQ_EXCLUDED_CLASSES = "fluentq.excludedClasses";

  /** comma separated list of packages to be included into code generation (default: all) */
  public static final String FLUENTQ_INCLUDED_PACKAGES = "fluentq.includedPackages";

  /** comma separated list of class names to be included into code generation (default: all) */
  public static final String FLUENTQ_INCLUDED_CLASSES = "fluentq.includedClasses";

  /** set whether unknown non-annotated classes should be treated as embeddable (default: false) */
  public static final String FLUENTQ_UNKNOWN_AS_EMBEDDABLE = "fluentq.unknownAsEmbeddable";

  /** set the variable name function class */
  public static final String FLUENTQ_VARIABLE_NAME_FUNCTION_CLASS =
      "fluentq.variableNameFunctionClass";

  /** set whether info level messages should be written to stdout (default: false) */
  public static final String FLUENTQ_LOG_INFO = "fluentq.logInfo";

  /**
   * the class instance of the <em>Single-Element Annotation</em> (with {@code String} element) to
   * be used on the generated classes. (default: depending on java version:
   * javax.annotation.Generated or javax.annotation.processing.Generated)
   *
   * @see <a
   *     href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.7.3">Single-Element
   *     Annotation</a>
   */
  public static final String FLUENTQ_GENERATED_ANNOTATION_CLASS =
      "fluentq.generatedAnnotationClass";

  private APTOptions() {}
}
