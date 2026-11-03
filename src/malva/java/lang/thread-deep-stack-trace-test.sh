#!/bin/bash

# The deep-stack target thread needs a very large Java stack. Some
# VM integrations ignore the stack size hint passed to the Thread
# constructor (for example JamVM with the OpenJDK class library),
# so pass the size on the command line as well. -Xss must match
# STACK_SIZE in ThreadDeepStackTrace.java.

# Set the process's stack size limit (RLIMIT_STACK) to 1 MB; on
# Linux, glibc uses the limit as the default native stack size for
# new threads.
ulimit -s 1024 2>/dev/null

# Expand $JAVA like scripts/suite, where it may include VM arguments
exec $JAVA -Xss64m -cp src malva.java.lang.ThreadDeepStackTrace
