#!/bin/bash
# Builds an enormous Java file to become an enormous class file.
# Enormous class files can't really be instrumented properly, because
# adding instrumentation causes them to be too large per the JVM spec.

cat > "$1" <<EOF
package com.google.monitoring.runtime.instrumentation; 


/** 
 * A class that is much too big, for the purposes of testing that 
 * classes that are much too big get rejected by the instrumenters.
 *
 * 6004 is the number which results in the maximum legal length for a method.
 * 6005 makes javac barf.
 *
 */
public class MuchTooBig {
  /** */
  public long[] allocateLongArray(int count) {
    return new long[count];
  }

  public void longThing() {
$(for i in {0..6003}; do echo "    Object a$i = new Object();"; done)
  }
}
EOF
