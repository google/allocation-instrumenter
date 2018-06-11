package com.google.monitoring.runtime.instrumentation;

import static org.junit.Assert.assertEquals;
import static org.junit.runner.JUnitCore.runClasses;

import java.lang.instrument.UnmodifiableClassException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Test the constructor-instrumentation functionality of the bytecode rewriting agent. */
@RunWith(JUnit4.class)
public class ConstructorInstrumenterTest {

  public static void main(String[] args) throws Exception {
    runClasses(ConstructorInstrumenterTest.class);
  }

  static class BasicFunctions {
    int count;
  }

  static class SubclassOfBasicFunctions extends BasicFunctions {}

  @Test
  public void testThreads() throws UnmodifiableClassException {
    final BasicFunctions bf = new BasicFunctions();
    ConstructorInstrumenter.instrumentClass(
        Thread.class,
        new ConstructorCallback<Thread>() {
          @Override
          public void sample(Thread t) {
            bf.count++;
          }
        });
    int numThreads = 10;
    for (int i = 0; i < numThreads; i++) {
      Thread unused = new Thread();
    }
    assertEquals("Did not see correct number of Threads", numThreads, bf.count);
  }

  @Test
  public void testBasicFunctionality() throws UnmodifiableClassException {
    final BasicFunctions bf = new BasicFunctions();
    ConstructorInstrumenter.instrumentClass(
        BasicFunctions.class,
        new ConstructorCallback<BasicFunctions>() {
          @Override
          public void sample(BasicFunctions newBf) {
            bf.count++;
          }
        });
    int numBFs = 10;
    for (int i = 0; i < numBFs; i++) {
      BasicFunctions unused = new BasicFunctions();
    }
    assertEquals("Did not see correct number of BasicFunctions", numBFs, bf.count);
  }

  @Test
  public void testSubclassesAlso() throws UnmodifiableClassException {
    final BasicFunctions bf = new BasicFunctions();
    ConstructorInstrumenter.instrumentClass(
        BasicFunctions.class,
        new ConstructorCallback<BasicFunctions>() {
          @Override
          public void sample(BasicFunctions newBf) {
            bf.count++;
          }
        });

    int numBFs = 2;
    new SubclassOfBasicFunctions();
    new BasicFunctions() {};
    assertEquals("Did not see correct number of BasicFunctions", numBFs, bf.count);
  }
}
