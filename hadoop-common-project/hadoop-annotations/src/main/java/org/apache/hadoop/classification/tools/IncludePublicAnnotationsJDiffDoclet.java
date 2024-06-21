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

//import com.sun.javadoc.DocErrorReporter;
//import com.sun.javadoc.LanguageVersion;
//import com.sun.javadoc.RootDoc;
//import jdiff.JDiff;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.Reporter;
import javax.lang.model.SourceVersion;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.StandardDoclet;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A <a href="http://java.sun.com/javase/6/docs/jdk/api/javadoc/doclet/">Doclet</a>
 * that only includes class-level elements that are annotated with
 * {@link org.apache.hadoop.classification.InterfaceAudience.Public}.
 * Class-level elements with no annotation are excluded.
 * In addition, all elements that are annotated with
 * {@link org.apache.hadoop.classification.InterfaceAudience.Private} or
 * {@link org.apache.hadoop.classification.InterfaceAudience.LimitedPrivate}
 * are also excluded.
 * It delegates to the JDiff Doclet, and takes the same options.
 */
public class IncludePublicAnnotationsJDiffDoclet implements Doclet {

  private Reporter reporter;
  private StandardDoclet standardDoclet = new StandardDoclet();

//  public static SourceVersion SourceVersion() {
//    return SourceVersion.latest();
//  }
//
//  public static boolean start(RootDoc root) {
//    System.out.println(
//        IncludePublicAnnotationsJDiffDoclet.class.getSimpleName());
//    RootDocProcessor.treatUnannotatedClassesAsPrivate = true;
//    return JDiff.start(RootDocProcessor.process(root));
//  }
//
//  public static int optionLength(String option) {
//   Integer length = StabilityOptions.optionLength(option);
//    if (length != null) {
//      return length;
//    }
//    return JDiff.optionLength(option);
//  }
//
//  public static boolean validOptions(String[][] options,
//      DocErrorReporter reporter) {
//    StabilityOptions.validOptions(options, reporter);
//    String[][] filteredOptions = StabilityOptions.filterOptions(options);
//    return JDiff.validOptions(filteredOptions, reporter);
//  }

  @Override
  public void init(Locale locale, Reporter reporter) {
    this.reporter = reporter;
    reporter.print(Kind.NOTE, "IncludePublicAnnotationsJDiffDoclet initialized");

  }
  @Override
  public String getName() {
    return "IncludePublicAnnotationsJDiffDoclet";
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
    Elements elementUtils = environment.getElementUtils();
    Set<? extends Element> elements = environment.getIncludedElements();
    Set<? extends Element> filteredElements = elements.stream()
            .filter(this::isPublicInterfaceAudience)
            .collect(Collectors.toSet());
    reporter.print(Kind.NOTE, "Filtered elements: " + filteredElements);

    //Generate documentation or perform actions based on the filtered elements
    try {
      generateDocumentation(filteredElements);
    } catch (IOException e) {
      reporter.print(Kind.ERROR, "Error generating documentation: " + e.getMessage());
      return false;
    }
    return true;
  }

  private boolean isPublicInterfaceAudience(Element element) {
    return element.getAnnotationMirrors().stream()
            .anyMatch(annotation -> {
              String annotationName = annotation.getAnnotationType().toString();
              return annotationName.equals("org.apache.hadoop.classification.InterfaceAudience.Public");
            });
  }

  private void generateDocumentation(Set<? extends Element> elements) throws IOException {
    // Placeholder method to handle the filtered element
    for (Element element : elements) {
      String elementDoc = "Element: " + element.toString();
      reporter.print(Kind.NOTE, "Documenting element: " + elementDoc);
      // Here, we can write the element's documentation to a file or perform other processing
      Files.write(Paths.get("documentation.txt"), elementDoc.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }
  }
}
