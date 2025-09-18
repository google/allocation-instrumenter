#!/bin/sh
# Checks to make sure that the build data is stripped.  Our agents
# should not have build data associated, so this makes sure that the
# program which is designed to strip it actually works.
#
# Also makes sure that org.objectweb.asm classes are no longer
# contained in the final agent

# -Xverify:remote prevents test failure under fastdebug JVM with java_test's default -Xverify:none.
"${1}" --singlejar --jvm_flags="${2} -Xverify:remote"
