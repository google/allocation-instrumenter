/*
 * Copyright (C) 2008 Google Inc.
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

/** This interface describes a function that is used to sample an allocation. */
public interface Sampler {
  /**
   * Determines whether the object currently being allocated, with the given size, should be traced.
   *
   * <p><b>CAUTION: DO NOT DO ALLOCATION IN THIS METHOD WITHOUT ENSURING THAT THE SAMPLER WILL NOT
   * BE INVOKED ON THE RESULTING ALLOCATION.</b> Otherwise, you will get an infinite regress of
   * calls to the sampler.
   *
   * @param count the <code>int</code> count of how many instances are being allocated. -1 means a
   *     simple new to distinguish from a 1-element array. 0 shows up as a value here sometimes; one
   *     reason is T[] toArray()-type methods that require an array type argument (see
   *     ArrayList.toArray() for example).
   * @param desc the <code>String</code> descriptor of the class/primitive type being allocated.
   * @param newObj the new <code>Object</code> whose allocation we're recording.
   * @param size the size of the object being allocated.
   */
  public void sampleAllocation(int count, String desc, Object newObj, long size);
}
