/*
 * Copyright (C) 2011 Google Inc.
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

/**
 * This interface describes a function that is used to sample a constructor. It is intended to be
 * invoked every time a constructor for class T is invoked. This will not be invoked when subclasses
 * of T are instantiated.
 *
 * <p>This mechanism works independently of whether the class is part of the JDK core library.
 *
 * @param <T> The class that will be sampled with this ConstructorCallback
 */
public interface ConstructorCallback<T> {
  /**
   * When an object implementing interface <code>ConstructorCallback</code> is passed to {@link
   * ConstructorInstrumenter#instrumentClass(Class, ConstructorCallback)}, it will get executed
   * whenever a constructor for type T is invoked.
   *
   * @param newObj the new <code>Object</code> whose construction we're recording. The object is not
   *     fully constructed; any references to this object that are stored in this callback are
   *     subject to the memory model constraints related to such objects.
   */
  public void sample(T newObj);
}
