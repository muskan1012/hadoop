/*
// * Licensed to the Apache Software Foundation (ASF) under one
// * or more contributor license agreements.  See the NOTICE file
// * distributed with this work for additional information
// * regarding copyright ownership.  The ASF licenses this file
// * to you under the Apache License, Version 2.0 (the
// * "License"); you may not use this file except in compliance
// * with the License.  You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
*/
package org.apache.hadoop.classification.tools;

import javax.lang.model.SourceVersion;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Set;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;


public class ExcludePrivateAnnotationsJDiffDoclet implements Doclet {

  @Override
  public void init(Locale locale, Reporter reporter) {
    try {
      Method printMethod = reporter.getClass().getMethod("print", javax.tools.Diagnostic.Kind.class, String.class);
      printMethod.invoke(reporter, javax.tools.Diagnostic.Kind.NOTE, ExcludePrivateAnnotationsJDiffDoclet.class.getSimpleName());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getName() {
    return ExcludePrivateAnnotationsJDiffDoclet.class.getSimpleName();
  }

  @Override
  public Set<? extends Option> getSupportedOptions() {
    try {
      return (Set<? extends Option>) Class.forName("jdiff.JDiff").getMethod("getSupportedOptions").invoke(null);
    } catch (Exception e) {
      e.printStackTrace();
      return Set.of();
    }
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latest();
  }

  @Override
  public boolean run(DocletEnvironment docEnv) {
    try {
      return start(docEnv);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public static boolean start(Object root) {
    try {
      System.out.println(ExcludePrivateAnnotationsJDiffDoclet.class.getSimpleName());
      Class<?> rootDocClass;
      Method processMethod;
      Method startMethod;
      if (isJdk9OrLater()) {
        // Use the JDK 9+ Doclet API
        rootDocClass = Class.forName("jdk.javadoc.doclet.DocletEnvironment");
        processMethod = ExcludePrivateAnnotationsJDiffDoclet.class.getDeclaredMethod("processJdk9", rootDocClass);
        startMethod = Class.forName("jdiff.JDiff").getMethod("run", rootDocClass);
      } else {
        // Use the JDK 8 Doclet API
        rootDocClass = Class.forName("com.sun.javadoc.RootDoc");
        processMethod = ExcludePrivateAnnotationsJDiffDoclet.class.getDeclaredMethod("processJdk8", rootDocClass);
        startMethod = Class.forName("jdiff.JDiff").getMethod("start", rootDocClass);
      }
      Object processedRoot = processMethod.invoke(null, root);
      return (boolean) startMethod.invoke(null, processedRoot);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  public static int optionLength(String option) {
    try {
      Method optionLengthMethod = Class.forName("StabilityOptions").getMethod("optionLength", String.class);
      Integer length = (Integer) optionLengthMethod.invoke(null, option);
      if (length != null) {
        return length;
      }
      return (int) Class.forName("jdiff.JDiff").getMethod("optionLength", String.class).invoke(null, option);
    } catch (Exception e) {
      e.printStackTrace();
      return -1;
    }
  }
  public static boolean validOptions(String[][] options, Object reporter) {
    try {
      Method validOptionsMethod = Class.forName("StabilityOptions").getMethod("validOptions", String[][].class, reporter.getClass());
      validOptionsMethod.invoke(null, options, reporter);
      Method filterOptionsMethod = Class.forName("StabilityOptions").getMethod("filterOptions", String[][].class);
      String[][] filteredOptions = (String[][]) filterOptionsMethod.invoke(null, options);
      return (boolean) Class.forName("jdiff.JDiff").getMethod("validOptions", String[][].class, reporter.getClass()).invoke(null, filteredOptions, reporter);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  private static boolean isJdk9OrLater() {
    return Runtime.version().feature() >= 9;
  }
  private static Object processJdk8(Object root) {
    try {
      return Class.forName("org.apache.hadoop.classification.tools.RootDocProcessor").getMethod("process", Class.forName("com.sun.javadoc.RootDoc")).invoke(null, root);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  private static Object processJdk9(Object root) {
    try {
      return root;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
