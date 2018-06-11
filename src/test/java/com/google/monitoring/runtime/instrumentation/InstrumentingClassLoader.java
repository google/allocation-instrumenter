package com.google.monitoring.runtime.instrumentation;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Helper for testing AllocationInstrumenter. Gives a way to get at an instrumented version of a
 * class without having the pesky classloader load it uninstrumented for us.
 */
public class InstrumentingClassLoader extends ClassLoader {

  /** Call this to get an instrumented version of Instrumentee. */
  public static Class<?> getInstrumenteeClass(
      String recordingClassName, String recordingMethodName, String targetClassName) {
    InstrumentingClassLoader loader =
        new InstrumentingClassLoader(
            recordingClassName,
            recordingMethodName,
            InstrumentingClassLoader.class.getClassLoader());

    return loader.findClass(targetClassName);
  }

  private final String recordingClassName;
  private final String recordingMethodName;

  public InstrumentingClassLoader(
      String recordingClassName, String recordingMethodName, ClassLoader parent) {
    super(parent);
    this.recordingClassName = recordingClassName;
    this.recordingMethodName = recordingMethodName;
  }

  @Override
  public Class<?> findClass(String name) {
    String classFileName = name.replace('.', '/') + ".class";
    InputStream originalStream = getResourceAsStream(classFileName);
    Preconditions.checkNotNull(originalStream, classFileName);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      ByteStreams.copy(originalStream, baos);
      baos.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    byte[] originalBytes = baos.toByteArray();
    byte[] instrumentedBytes;
    ClassLoader loader = InstrumentingClassLoader.class.getClassLoader();
    if (recordingClassName != null && recordingMethodName != null) {
      instrumentedBytes =
          AllocationInstrumenter.instrument(
              originalBytes, recordingClassName, recordingMethodName, loader);
    } else {
      instrumentedBytes = AllocationInstrumenter.instrument(originalBytes, loader);
    }

    return defineClass(name, instrumentedBytes, 0, instrumentedBytes.length);
  }
}
