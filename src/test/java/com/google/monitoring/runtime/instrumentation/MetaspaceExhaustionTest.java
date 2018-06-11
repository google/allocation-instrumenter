package com.google.monitoring.runtime.instrumentation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

/**
 * Test that creates a lot of classes and then leaves them to be garbage. The point of this is that
 * the agent was holding references to classes for a while, so they weren't getting garbage
 * collected.
 */
@RunWith(JUnit4.class)
public class MetaspaceExhaustionTest {
  static class LocalLoader extends ClassLoader {
    public Class<?> findClass(String name, byte[] ap) {
      return defineClass(name, ap, 0, ap.length);
    }
  }

  @Test
  public void testExhaustion() {
    // 2^16 is a lot of classes.  This should exhaust the metaspace
    // (set on the command line to 32MB, if there is a class leak.
    final int classCount = 65536;

    ClassWriter cw = new ClassWriter(0);
    cw.visit(
        Opcodes.V1_6,
        Opcodes.ACC_PUBLIC,
        "com/google/monitoring/runtime/agentdetection/AgentPresent",
        null,
        "java/lang/Object",
        null);
    cw.visitEnd();
    byte[] classBytes = cw.toByteArray();

    // Uncommenting the lines related to the Class<?> array will cause
    // metaspace exhaustion.  This can be used for testing.
    // Class<?>[] cs = new Class<?>[classCount];
    for (int i = 0; i < classCount; i++) {
      // Load the perfectly legitimate class in its own class loader
      // (so as to prevent the class loader from complaining about
      // name clashes).
      LocalLoader l = new LocalLoader();
      // cs[i] =
      l.findClass("com.google.monitoring.runtime.agentdetection.AgentPresent", classBytes);
    }
  }
}
