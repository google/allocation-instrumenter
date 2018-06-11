package com.google.monitoring.runtime.instrumentation;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

/** Helper class for testing AllocationInstrumenter. Does various allocations. */
public class Instrumentee implements Cloneable {
  /** */
  public static class Generic<T> {
    private final T unused;

    public Generic(T foo) {
      this.unused = foo;
    }
  }

  public Instrumentee() {}

  public Generic<String> allocateGenericString() {
    return new Generic<String>("Yo!");
  }

  public <T> T allocateGeneric(Class<T> cls) {
    try {
      return cls.newInstance();
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    }
  }

  public Object allocateObject() {
    return new Object();
  }

  public long[] allocateLongArray(int count) {
    return new long[count];
  }

  public Object allocateObjectArray(int count) {
    return new Object[count];
  }

  public Object allocateMultiDimLongArray() {
    return new long[13][15][0][17];
  }

  public Object allocateMultiDimObjectArray() {
    return new Object[3][5][7];
  }

  public Object allocateMultiDimObjectArrayWithAZeroDim() {
    return new Object[3][5][0][7];
  }

  public Object allocateMultiDimObjArrayWithAMissingDim() {
    return new Object[3][5][7][];
  }

  public Object cloneMultiDimObjArrayWithTwoMissingDims(Object[][][] os) {
    return os.clone();
  }

  public Object allocateArrayViaReflection(int count, Class<?> cls) {
    return Array.newInstance(cls, count);
  }

  public Object allocateMultiDimArrayViaReflection(int[] dims, Class<?> cls) {
    return Array.newInstance(cls, dims);
  }

  public Object allocateClone() {
    try {
      return (Instrumentee) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  public Object allocateConstructor(Class[] params, Object[] o) {
    try {
      Constructor<?> c = Instrumentee.class.getConstructor(params);
      return c.newInstance(o);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Object allocateArrayClone(int[] arr) {
    return arr.clone();
  }

  public Object allocateMultiArrayClone(int[][] arr) {
    return arr.clone();
  }

  private int[][] twoDarray;

  public Object assignFieldMultiArrayClone(int[][] twoDarray) {
    this.twoDarray = twoDarray.clone();
    return this.twoDarray;
  }
}
