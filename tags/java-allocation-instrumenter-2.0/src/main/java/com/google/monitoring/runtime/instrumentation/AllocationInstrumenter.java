// Copyright 2007 Google Inc. All Rights Reserved.

package com.google.monitoring.runtime.instrumentation;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Instruments bytecodes that allocate heap memory to call a recording hook.
 * This will add a static invocation to a recorder function to any bytecode that
 * looks like it will be allocating heap memory allowing users to implement heap
 * profiling schemes.
 *
 * @author Ami Fischman
 * @author Jeremy Manson
 */
public class AllocationInstrumenter implements ClassFileTransformer {
  static final Logger logger =
      Logger.getLogger(AllocationInstrumenter.class.getName());

  // We can rewrite classes loaded by the bootstrap class loader
  // iff the agent is loaded by the bootstrap class loader.  It is
  // always *supposed* to be loaded by the bootstrap class loader, but
  // this relies on the Boot-Class-Path attribute in the JAR file always being
  // set to the name of the JAR file that contains this agent, which we cannot
  // guarantee programmatically.
  private static volatile boolean canRewriteBootstrap;

  // No instantiating me except in premain() or in {@link JarClassTransformer}.
  AllocationInstrumenter() { }

  public static void premain(String agentArgs, Instrumentation inst) {
    AllocationRecorder.setInstrumentation(inst);

    inst.addTransformer(new AllocationInstrumenter(),
        inst.isRetransformClassesSupported());
    
    if (!inst.isRetransformClassesSupported()) {
      System.err.println("Some JDK classes are already loaded and " +
          "will not be instrumented.");
      return;
    }

    // Don't try to rewrite classes loaded by the bootstrap class
    // loader if this class wasn't loaded by the bootstrap class
    // loader.
    if (AllocationRecorder.class.getClassLoader() != null) {
      canRewriteBootstrap = false;
      // The loggers aren't installed yet, so we use println.
      System.err.println("Class loading breakage: " +
          "Will not be able to instrument JDK classes");
      return;
    }

    canRewriteBootstrap = true;

    // Get the set of already loaded classes that can be rewritten.
    Class<?>[] classes = inst.getAllLoadedClasses();
    ArrayList<Class<?>> classList = new ArrayList<Class<?>>();
    for (int i = 0; i < classes.length; i++) {
      if (inst.isModifiableClass(classes[i])) {
        classList.add(classes[i]);
      }
    }

    // Reload classes, if possible.
    Class<?>[] workaround = new Class<?>[classList.size()];
    try {
      inst.retransformClasses(classList.toArray(workaround));
    } catch (UnmodifiableClassException e) {
      System.err.println(
          "AllocationInstrumenter was unable to retransform early loaded classes.");
    }
  }

  public byte[] transform(ClassLoader loader, String className,
                          Class<?> classBeingRedefined,
                          ProtectionDomain protectionDomain,
                          byte[] origBytes) {
    // There are two conditions under which we don't rewrite:
    //  1. If className was loaded by the bootstrap class loader and
    //  the agent wasn't (in which case the class being rewritten
    //  won't be able to call agent methods).
    //  2. If it is java.lang.ThreadLocal, which can't be rewritten because the
    //  JVM depends on its structure.
    if (((loader == null) && !canRewriteBootstrap) ||
        className.startsWith("java/lang/ThreadLocal")) {
      return null;
    }
    // third_party/java/webwork/*/ognl.jar contain bad class files.  Ugh.
    if (className.startsWith("ognl/")) {
      return null;
    }

    return instrument(origBytes);
  }

  /**
   * Given the bytes representing a class, go through all the bytecode in it and
   * instrument any occurences of new/newarray/anewarray/multianewarray with
   * pre- and post-allocation hooks.  Even more fun, intercept calls to the
   * reflection API's Array.newInstance() and instrument those too.
   *
   * @param originalBytes the original <code>byte[]</code> code.
   * @param recorderClass the <code>String</code> internal name of the class
   * containing the recorder method to run.
   * @param recorderMethod the <code>String</code> name of the recorder method
   * to run.
   * @return the instrumented <code>byte[]</code> code.
   */
  public static byte[] instrument(byte[] originalBytes, String recorderClass,
      String recorderMethod) {
    try {
      ClassReader cr = new ClassReader(originalBytes);
      ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
      VerifyingClassAdapter vcw =
        new VerifyingClassAdapter(cw, originalBytes, cr.getClassName());
      ClassAdapter adapter =
          new AIClassAdapter(vcw, recorderClass, recorderMethod);

      cr.accept(adapter, ClassReader.SKIP_FRAMES);

      return vcw.toByteArray();
    } catch (RuntimeException e) {
      logger.log(Level.WARNING, "Failed to instrument class.", e);
      throw e;
    } catch (Error e) {
      logger.log(Level.WARNING, "Failed to instrument class.", e);
      throw e;
    }
  }

  /**
   * @see #instrument(byte[], String, String) documentation for the 3-arg
   * version.  This is a convenience version that uses the recorder in this
   * class.
   */
  public static byte[] instrument(byte[] originalBytes) {
    return instrument(
        originalBytes,
        "com/google/monitoring/runtime/instrumentation/AllocationRecorder",
        "recordAllocation");
  }

  /**
   * A <code>ClassAdapter</code> that processes methods with a
   * <code>AIMethodAdapter</code> to instrument heap allocations.
   */
  private static class AIClassAdapter extends ClassAdapter {
    private final String recorderClass;
    private final String recorderMethod;

    public AIClassAdapter(ClassVisitor cv, String recorderClass,
                          String recorderMethod) {
      super(cv);
      this.recorderClass = recorderClass;
      this.recorderMethod = recorderMethod;
    }

    /**
     * For each method in the class being instrumented, <code>visitMethod</code>
     * is called and the returned MethodVisitor is used to visit the method.
     * Note that a new MethodVisitor is constructed for each method.
     */
    @Override
    public MethodVisitor visitMethod(int access, String base, String desc,
                                     String signature, String[] exceptions) {
      MethodVisitor mv =
        cv.visitMethod(access, base, desc, signature, exceptions);

      if (mv != null) {
        AllocationMethodAdapter aimv =
            new AllocationMethodAdapter(mv, recorderClass, recorderMethod);
        LocalVariablesSorter lvs = new LocalVariablesSorter(access, desc, aimv);
        aimv.lvs = lvs;
        mv = lvs;
      }
      return mv;
    }
  }
}
