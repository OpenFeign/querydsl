/*
 * Copyright 2021, The Querydsl Team (http://www.querydsl.com/team)
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
package com.querydsl.codegen;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

/**
 * creates files with {@link javax.annotation.processing.Filer}.
 *
 * @author f43nd1r
 */
public class DefaultFiler implements Filer {
  @Override
  public Writer createFile(
      ProcessingEnvironment processingEnvironment,
      String classname,
      Collection<? extends Element> elements)
      throws IOException {
    return processingEnvironment
        .getFiler()
        .createSourceFile(classname, elements.toArray(new Element[0]))
        .openWriter();
  }
}
