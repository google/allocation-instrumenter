/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.monitoring.runtime.instrumentation;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.jar.JarFile;

/**
 * Implementation of the {@code Instrumentation} interface that does nothing.
 * Intended to be used in {@link AllocationRecorder} to work around a bug in certain JVMs
 * where calling {@code Instrumentation.getObjectSize()} during JVM shutdown triggers a
 * JVM-crashing assert.
 */
class NullInstrumentation implements Instrumentation {
  @Override
  public void addTransformer(final ClassFileTransformer transformer, final boolean canRetransform) {
  }

  @Override
  public void addTransformer(final ClassFileTransformer transformer) {
  }

  @Override
  public boolean removeTransformer(final ClassFileTransformer transformer) {
    return false;
  }

  @Override
  public boolean isRetransformClassesSupported() {
    return false;
  }

  @Override
  public void retransformClasses(final Class<?>... classes) throws UnmodifiableClassException {
  }

  @Override
  public boolean isRedefineClassesSupported() {
    return false;
  }

  @Override
  public void redefineClasses(final ClassDefinition... definitions)
        throws ClassNotFoundException, UnmodifiableClassException {
  }

  @Override
  public boolean isModifiableClass(final Class<?> theClass) {
    return false;
  }

  @Override
  public Class[] getAllLoadedClasses() {
    return new Class[0];
  }

  @Override
  public Class[] getInitiatedClasses(final ClassLoader loader) {
    return new Class[0];
  }

  @Override
  public long getObjectSize(final Object objectToSize) {
    return 0;
  }

  @Override
  public void appendToBootstrapClassLoaderSearch(final JarFile jarfile) {
  }

  @Override
  public void appendToSystemClassLoaderSearch(final JarFile jarfile) {
  }

  @Override
  public boolean isNativeMethodPrefixSupported() {
    return false;
  }

  @Override
  public void setNativeMethodPrefix(final ClassFileTransformer transformer, final String prefix) {
  }
}
