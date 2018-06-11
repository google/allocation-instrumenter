package com.google.monitoring.runtime.instrumentation;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A simple class that reports on the correctness of the Agent's JAR file.
 *
 * <p>Right now, it tests two things:
 *
 * <p>
 *
 * <ul>
 *   <li>Whether the correct build data is available or not. This is to test our "build data
 *       stripping" functionality.
 *   <li>That the ASM classes are not located in org/objectweb/asm, where they can conflict with
 *       other classes loaded by the user.
 * </ul>
 */
class AgentIntegrity {
  void testBuildProperties() throws IOException {
    ClassLoader cl = AgentIntegrity.class.getClassLoader();
    InputStream is = cl.getResourceAsStream("build-data.properties");
    BufferedReader br = new BufferedReader(new InputStreamReader(is, UTF_8));
    String line = br.readLine();
    while (line != null) {
      if (line.startsWith("build.target")) {
        if (!line.endsWith("AgentIntegrity_deploy.jar")) {
          throw new IllegalStateException("Wrong build target found");
        }
        break;
      }
      line = br.readLine();
    }
  }

  void testAsmMoved() {
    try {
      // All agents seem to use this.
      Class.forName("org.objectweb.asm.ClassReader");
      throw new IllegalStateException("ASM class found");
    } catch (ClassNotFoundException e) {
      // Expected
    }
  }

  public static void main(String[] args) throws Exception {
    AgentIntegrity suite = new AgentIntegrity();
    suite.testAsmMoved();
    suite.testBuildProperties();
  }
}
