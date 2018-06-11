package com.google.monitoring.runtime.instrumentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit test for the bytecode allocation instrumenter. */
@RunWith(JUnit4.class)
public class AllocationInstrumenterTest {

  // needs to be static so recorder can use it, which prevents parallelization
  // of test cases.
  private static List<Event> actualEventList;
  private static List<Event> expectedEventList;

  @Before
  public void setUp() throws Exception {
    expectedEventList = new ArrayList<Event>();
    actualEventList = new ArrayList<Event>();
  }

  /**
   * This method tests to make sure that a class that is too big to get instrumented doesn't get
   * instrumented.
   */
  @Test
  public void testTooBig() throws Exception {
    Class<?> instrumenteeClass =
        InstrumentingClassLoader.getInstrumenteeClass(
            AllocationInstrumenterTest.class.getName().replace('.', '/'),
            "recorder",
            "com.google.monitoring.runtime.instrumentation.MuchTooBig");

    Object tempo = instrumenteeClass.newInstance();
    Method m;

    // Just to make sure nothing happens.
    m = instrumenteeClass.getMethod("allocateLongArray", Integer.TYPE);
    assertNotNull(m);
    long[] ia = (long[]) m.invoke(tempo, 27);
    expectedEventList.add(new Event(27, "long", ia));

    assertTrue(
        "List contains: " + actualEventList + " but should be empty", actualEventList.isEmpty());
  }

  @Test
  public void testInstrumentation() throws Exception {
    Class<?> instrumenteeClass =
        InstrumentingClassLoader.getInstrumenteeClass(
            AllocationInstrumenterTest.class.getName().replace('.', '/'),
            "recorder",
            "com.google.monitoring.runtime.instrumentation.Instrumentee");

    Object tempo = instrumenteeClass.newInstance();

    Method m;

    m = instrumenteeClass.getMethod("allocateObject");
    assertNotNull(m);
    Object o = m.invoke(tempo);
    expectedEventList.add(new Event(-1, "java/lang/Object", o));

    m = instrumenteeClass.getMethod("allocateLongArray", Integer.TYPE);
    assertNotNull(m);
    long[] ia = (long[]) m.invoke(tempo, 27);
    expectedEventList.add(new Event(27, "long", ia));

    m = instrumenteeClass.getMethod("allocateObjectArray", Integer.TYPE);
    Object oa = m.invoke(tempo, 13);
    expectedEventList.add(new Event(13, "java/lang/Object", oa));

    m = instrumenteeClass.getMethod("allocateMultiDimLongArray");
    Object mdia = m.invoke(tempo);
    expectedEventList.add(new Event(13 * 15, "[[[[J", mdia));

    m = instrumenteeClass.getMethod("allocateMultiDimObjectArray");
    Object mda = m.invoke(tempo);
    expectedEventList.add(new Event(3 * 5 * 7, "java/lang/Object", mda));

    m = instrumenteeClass.getMethod("allocateMultiDimObjectArrayWithAZeroDim");
    Object mda0 = m.invoke(tempo);
    expectedEventList.add(new Event(3 * 5, "java/lang/Object", mda0));

    m = instrumenteeClass.getMethod("allocateMultiDimObjArrayWithAMissingDim");
    Object mdamissing = m.invoke(tempo);
    expectedEventList.add(new Event(3 * 5 * 7, "java/lang/Object", mdamissing));

    m =
        instrumenteeClass.getMethod(
            "cloneMultiDimObjArrayWithTwoMissingDims",
            new Class<?>[] {new Object[0][0][0].getClass()});
    Object[][][] os = new Object[3][5][];
    os[0] = null;
    Object mdaTwoMissing = m.invoke(tempo, new Object[] {os});
    expectedEventList.add(new Event(3, "java/lang/Object", mdaTwoMissing));

    // For reflection calls, the object classes are provided in '.' form and not '/'.
    m =
        instrumenteeClass.getMethod(
            "allocateArrayViaReflection", new Class<?>[] {Integer.TYPE, Class.class});
    Object lar = m.invoke(tempo, 5, List.class);
    expectedEventList.add(new Event(5, "java.util.List", lar));

    m =
        instrumenteeClass.getMethod(
            "allocateMultiDimArrayViaReflection",
            new Class<?>[] {(new int[0]).getClass(), Class.class});
    Object mdlr = m.invoke(tempo, new int[] {0, 7, 9, 11}, List.class);
    expectedEventList.add(new Event(7 * 9 * 11, "java.util.List", mdlr));

    m =
        instrumenteeClass.getMethod(
            "allocateMultiDimArrayViaReflection",
            new Class<?>[] {(new int[0]).getClass(), Class.class});
    Object mdlr0 = m.invoke(tempo, new int[] {7, 9, 11}, List.class);
    expectedEventList.add(new Event(7 * 9 * 11, "java.util.List", mdlr0));

    m = instrumenteeClass.getMethod("allocateGenericString");
    assertNotNull(m);
    Object gs = m.invoke(tempo);
    expectedEventList.add(
        new Event(
            -1, "com/google/monitoring/runtime/instrumentation/" + "Instrumentee$Generic", gs));

    m = instrumenteeClass.getMethod("allocateGeneric", Class.class);
    assertNotNull(m);
    String g = (String) m.invoke(tempo, String.class);
    expectedEventList.add(new Event(-1, "java/lang/String", g));

    m = instrumenteeClass.getMethod("allocateClone");
    assertNotNull(m);
    Object cloned = m.invoke(tempo);
    expectedEventList.add(
        new Event(-1, "com/google/monitoring/runtime/instrumentation/Instrumentee", cloned));

    m = instrumenteeClass.getMethod("allocateConstructor", Class[].class, Object[].class);
    assertNotNull(m);
    Class<?>[] params = new Class<?>[0];
    Object[] otherParams = new Object[0];
    Object constructed = m.invoke(tempo, params, otherParams);
    expectedEventList.add(
        new Event(-1, "com/google/monitoring/runtime/instrumentation/Instrumentee", constructed));

    m = instrumenteeClass.getMethod("allocateArrayClone", int[].class);
    assertNotNull(m);
    int[] arrClone = new int[3];
    Object ac = m.invoke(tempo, arrClone);
    expectedEventList.add(new Event(3, "I", ac));

    m = instrumenteeClass.getMethod("allocateMultiArrayClone", int[][].class);
    assertNotNull(m);
    int[][] multiArrClone = new int[3][7];
    Object mac = m.invoke(tempo, (Object) multiArrClone);
    expectedEventList.add(new Event(3 * 7, "I", mac));

    m = instrumenteeClass.getMethod("assignFieldMultiArrayClone", int[][].class);
    assertNotNull(m);
    mac = m.invoke(tempo, (Object) multiArrClone);
    expectedEventList.add(new Event(3 * 7, "I", mac));

    assertEquals(expectedEventList, actualEventList);
  }

  public static void recorder(int count, String desc, Object newObj) {
    actualEventList.add(new Event(count, desc, newObj));
  }

  public static void recorder(Class<?> cls, Object newObj) {
    String typename = cls.getName().replace(".", "/");
    actualEventList.add(new Event(-1, typename, newObj));
  }

  private static class Event {
    public final int count;
    public final String desc;
    public final Object newObj;

    public Event(int count, String desc, Object newObj) {
      this.count = count;
      this.desc = desc;
      this.newObj = newObj;
    }

    public boolean equals(Object other) {
      if (!(other instanceof Event)) {
        return false;
      }
      Event evother = (Event) other;
      boolean ret = count == evother.count && desc.equals(evother.desc) && newObj == evother.newObj;
      if (!ret) {
        // as long as all users of equals in this test expect equality, may as
        // well be informative about failures.
        System.err.println(this + " != " + evother);
      }
      return ret;
    }

    public String toString() {
      return "<" + count + "," + desc + "," + newObj + ">";
    }

    public int hashCode() {
      return count ^ desc.hashCode() ^ newObj.hashCode();
    }
  }
}
