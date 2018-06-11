package com.google.monitoring.runtime.instrumentation;

import org.apache.bcel.Repository;
import org.apache.bcel.util.ClassLoaderRepository;
import org.apache.bcel.verifier.Verifier;

/**
 * A main driver for combining BCEL's JustIce verifier with the AllocationInstrumenter for
 * convenient troubleshooting of verification errors with instrumented classes.
 */
public class AllocationInstrumenterVerifier {

  private AllocationInstrumenterVerifier() {}

  /**
   * A dummy function we instrument with; it does nothing and is only public so that the
   * instrumentation works correctly.
   */
  public static void dummyRecorder(int count, String type, Object obj) {}

  /**
   * Given a list of fully-qualified (dotted) classnames, instrument each using the
   * AllocationInstrumenter and verify each with BCEL's JustIce verifier.
   */
  public static final void main(String[] args) {
    InstrumentingClassLoader loader =
        new InstrumentingClassLoader(
            AllocationInstrumenterVerifier.class.getName().replace('.', '/'),
            "dummyRecorder",
            AllocationInstrumenterVerifier.class.getClassLoader());
    Repository.setRepository(new ClassLoaderRepository(loader));
    Verifier.main(args);
  }
}
