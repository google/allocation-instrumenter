#!/bin/sh
# Checks to make sure that the build data is stripped.  Our agents
# should not have build data associated, so this makes sure that the
# program which is designed to strip it actually works.
#
# Also makes sure that org.objectweb.asm classes are no longer
# contained in the final agent

"${1}" --singlejar --jvm_flags="${2}"
