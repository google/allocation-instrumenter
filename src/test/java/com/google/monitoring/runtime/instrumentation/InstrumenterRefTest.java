package com.google.monitoring.runtime.instrumentation;

import com.google.common.testing.GcFinalization;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Additional tests for the allocation instrumenter. */
@RunWith(JUnit4.class)
public class InstrumenterRefTest {
  static class ObjectHolder {
    Object o;

    ObjectHolder(Object o) {
      this.o = o;
    }
  }

  // The instrumenter creates local references to objects that are
  // passed to constructors.  Even though the liveness ranges of said
  // variables appear to be correct in the bytecode, the JVM does not
  // always seem to respect them.  The agent therefore nulls out those
  // references explicitly.  This test ensures that that happens
  // correctly.
  @Test
  public void testInstrumentationDoesNotRetainLocalReference() throws InterruptedException {
    final CountDownLatch c = new CountDownLatch(1);
    Object o =
        new Object() {
          @Override
          public void finalize() {
            c.countDown();
          }
        };
    ObjectHolder unused = new ObjectHolder(o);
    unused = null;
    o = null;
    GcFinalization.await(c);
  }
}
