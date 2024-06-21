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
//import com.sun.javadoc.DocErrorReporter;
//import com.sun.javadoc.LanguageVersion;
//import com.sun.javadoc.RootDoc;

///**
// * A <a href="http://java.sun.com/javase/6/docs/jdk/api/javadoc/doclet/">Doclet</a>
// * that only includes class-level elements that are annotated with
// * {@link org.apache.hadoop.classification.InterfaceAudience.Public}.
// * Class-level elements with no annotation are excluded.
// * In addition, all elements that are annotated with
// * {@link org.apache.hadoop.classification.InterfaceAudience.Private} or
// * {@link org.apache.hadoop.classification.InterfaceAudience.LimitedPrivate}
// * are also excluded.
// * It delegates to the Standard Doclet, and takes the same options.
// */
//public class IncludePublicAnnotationsStandardDoclet {
//
//  public static LanguageVersion languageVersion() {
//    return LanguageVersion.JAVA_1_5;
//  }
//
//  public static boolean start(RootDoc root) {
//    System.out.println(
//        IncludePublicAnnotationsStandardDoclet.class.getSimpleName());
//    RootDocProcessor.treatUnannotatedClassesAsPrivate = true;
//    return Standard.start(RootDocProcessor.process(root));
//  }
//
//  public static int optionLength(String option) {
//    Integer length = StabilityOptions.optionLength(option);
//    if (length != null) {
//      return length;
//    }
//    return StandardDoclet.optionLength(option);
//  }
//
//  public static boolean validOptions(String[][] options,
//      DocErrorReporter reporter) {
//    StabilityOptions.validOptions(options, reporter);
//    String[][] filteredOptions = StabilityOptions.filterOptions(options);
//    return Standard.validOptions(filteredOptions, reporter);
//  }
//}

package org.apache.hadoop.classification.tools;
import java.util.Locale;
import java.util.Set;
import javax.lang.model.SourceVersion;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.StandardDoclet;
import javax.lang.model.element.Element;
//import static org.apache.hadoop.classification.tools.RootDocProcessor.treatUnannotatedClassesAsPrivate;
//import static org.apache.hadoop.classification.tools.RootDocProcessor.process;

/**
 * A Doclet that only includes class-level elements that are annotated with
 * {@link org.apache.hadoop.classification.InterfaceAudience.Public}.
 * Class-level elements with no annotation are excluded.
 * In addition, all elements that are annotated with
 * {@link org.apache.hadoop.classification.InterfaceAudience.Private} or
 * {@link org.apache.hadoop.classification.InterfaceAudience.LimitedPrivate}
 * are also excluded.
 * It delegates to the Standard Doclet, and takes the same options.
 */
public class IncludePublicAnnotationsStandardDoclet implements Doclet {
  private final StandardDoclet standardDoclet = new StandardDoclet();
  @Override
  public void init(Locale locale, Reporter reporter) {
    standardDoclet.init(locale, reporter);
  }

  @Override
  public String getName() {
    return IncludePublicAnnotationsStandardDoclet.class.getSimpleName();
  }

  @Override
  public Set<? extends Option> getSupportedOptions() {
    return standardDoclet.getSupportedOptions();
  }
  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latest();
  }
  @Override
  public boolean run(DocletEnvironment environment) {
    System.out.println(IncludePublicAnnotationsStandardDoclet.class.getSimpleName());
//    treatUnannotatedClassesAsPrivate = true;
//    Set<? extends Element> specifiedElements = environment.getSpecifiedElements();
//    Set<? extends Element> filteredElements = (Set<? extends Element>) process((DocletEnvironment) specifiedElements);
    return standardDoclet.run(environment);
  }
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
  public static boolean validOptions(String[][] options, Reporter reporter) {
    StabilityOptions.validOptions(options, reporter);
    String[][] filteredOptions = StabilityOptions.filterOptions(options);
    // Validate options for the standard doclet
    try {
      Class<?> standardClass = Class.forName("com.sun.tools.doclets.standard.Standard");
      java.lang.reflect.Method validOptionsMethod = standardClass.getMethod("validOptions", String[][].class, Reporter.class);
      return (boolean) validOptionsMethod.invoke(null, filteredOptions, reporter);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  public static boolean start(DocletEnvironment environment) {
    return new IncludePublicAnnotationsStandardDoclet().run(environment);
  }
}
