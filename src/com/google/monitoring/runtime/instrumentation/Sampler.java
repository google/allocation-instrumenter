// Copyright 2008 Google Inc. All Rights Reserved.

package com.google.monitoring.runtime.instrumentation;

/**
 * This interface describes a function that is used to sample an allocation. 
 * 
 * @author jeremymanson@google.com (Jeremy Manson)
 */
public interface Sampler {
  /**
   * Determines whether the object currently being allocated, with the given
   * size, should be traced.
   * 
   * <b>CAUTION: DO NOT DO ALLOCATION IN THIS METHOD WITHOUT ENSURING THAT
   * THE SAMPLER WILL NOT BE INVOKED ON THE RESULTING ALLOCATION.</b>
   * Otherwise, you will get an infinite regress of calls to the sampler.
   * 
   * @param count the <code>int</code> count of how many instances are being
   *     allocated.  -1 means a simple new to distinguish from a 1-element array.  0
   *     shows up as a value here sometimes; one reason is T[] toArray()-type
   *     methods that require an array type argument (see ArrayList.toArray() for
   *     example).
   * @param desc the <code>String</code> descriptor of the class/primitive type
   *     being allocated.
   * @param newObj the new <code>Object</code> whose allocation we're
   *     recording.
   * @param size the size of the object being allocated.
   */
  public void sampleAllocation(int count, String desc,
      Object newObj, long size);
}
