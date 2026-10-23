#!/bin/bash

# The deep-stack target thread needs a very large stack. Some VM
# integrations ignore the stack size hint passed to the Thread
# constructor (for example JamVM with the OpenJDK class library),
# so pass the size on the command line as well. -Xss must match
# STACK_SIZE in ThreadDeepStackTrace.java.

# Expand $JAVA like scripts/suite, where it may include VM arguments
exec $JAVA -Xss64m -cp src malva.java.lang.ThreadDeepStackTrace
