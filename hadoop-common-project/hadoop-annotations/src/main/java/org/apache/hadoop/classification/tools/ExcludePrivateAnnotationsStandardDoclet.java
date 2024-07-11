/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.classification.tools;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import javax.lang.model.SourceVersion;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

/**
 * A {@link Doclet} for excluding elements that are annotated with
 * {@link org.apache.hadoop.classification.InterfaceAudience.Private} or
 * {@link org.apache.hadoop.classification.InterfaceAudience.LimitedPrivate}.
 * It delegates to the Standard Doclet, and takes the same options.
 */
public class ExcludePrivateAnnotationsStandardDoclet implements Doclet {

  private Reporter reporter;
  @Override
  public void init(Locale locale, Reporter reporter) {
    this.reporter = reporter;
    reporter.print(javax.tools.Diagnostic.Kind.NOTE, ExcludePrivateAnnotationsStandardDoclet.class.getSimpleName());
  }
  @Override
  public String getName() {
    return ExcludePrivateAnnotationsStandardDoclet.class.getSimpleName();
  }
  @Override
  public Set<? extends Option> getSupportedOptions() {
    try {
      Class<?> standardClass = Class.forName("com.sun.tools.doclets.standard.Standard");
      java.lang.reflect.Method getSupportedOptionsMethod = standardClass.getMethod("getSupportedOptions");
      Object result = getSupportedOptionsMethod.invoke(null);
      if (result instanceof Set) {
        @SuppressWarnings("unchecked")
        Set<? extends Option> options = (Set<? extends Option>) result;
        return options;
      } else {
        return Collections.emptySet();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return Collections.emptySet();
    }
  }
  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latest();
  }
  @Override
  public boolean run(DocletEnvironment docEnv) {
    try {
      Class<?> standardClass = Class.forName("com.sun.tools.doclets.standard.Standard");
      java.lang.reflect.Method startMethod = standardClass.getMethod("start", DocletEnvironment.class);
      return (boolean) startMethod.invoke(null, docEnv);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Returns the option length for the given option.
   */
  public static int optionLength(String option) {
    Integer length = StabilityOptions.optionLength(option);
    if (length != null) {
      return length;
    }
    try {
      Class<?> standardClass = Class.forName("com.sun.tools.doclets.standard.Standard");
      java.lang.reflect.Method optionLengthMethod = standardClass.getMethod("optionLength", String.class);
      return (int) optionLengthMethod.invoke(null, option);
    } catch (Exception e) {
      e.printStackTrace();
      return -1;
    }
  }

  /**
   * Validate the given options.
   */
  public boolean validOptions(String[][] options) {
    StabilityOptions.validOptions(options, this.reporter);
    String[][] filteredOptions = StabilityOptions.filterOptions(options);
    try {
      Class<?> standardClass = Class.forName("com.sun.tools.doclets.standard.Standard");
      java.lang.reflect.Method validOptionsMethod = standardClass.getMethod("validOptions", String[][].class, Reporter.class);
      return (boolean) validOptionsMethod.invoke(null, filteredOptions, this.reporter);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
