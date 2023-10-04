# External programs
JAVA	?= java
JAVAC	?= javac

export JAVA

# Make the build silent by default
V =
ifeq ($(strip $(V)),)
	E = @echo
	Q = @
else
	E = @\#
	Q =
endif
export E Q

CLASSES	+= src/malva/TestCase.class
CLASSES	+= src/malva/java/lang/ClassLoaderTest.class
CLASSES	+= src/malva/java/lang/DoubleTest.class
CLASSES	+= src/malva/java/lang/FloatTest.class
CLASSES	+= src/malva/java/lang/LongTest.class
CLASSES	+= src/malva/java/lang/MathTest.class
CLASSES	+= src/malva/java/lang/StringTest.class
CLASSES += src/malva/java/io/PrintStreamTest.class
CLASSES += src/malva/java/lang/ClassTest.class
CLASSES += src/malva/java/lang/ObjectTest.class
CLASSES += src/malva/java/lang/ProcessTest.class
CLASSES += src/malva/java/lang/RuntimeTest.class
CLASSES += src/malva/java/lang/SystemTest.class
CLASSES += src/malva/java/lang/ThreadTest.class
CLASSES += src/malva/java/lang/ThrowableTest.class
CLASSES += src/malva/java/lang/reflect/FieldTest.class
CLASSES += src/malva/java/net/InetAddressTest.class
CLASSES += src/malva/java/net/NetworkInterfaceTest.class
CLASSES += src/malva/java/nio/DirectByteBufferTest.class
CLASSES += src/malva/java/text/DecimalFormatTest.class
CLASSES += src/malva/java/util/HashMapTest.class
CLASSES += src/malva/java/util/regex/PatternTest.class

SRC	:= $(patsubst %.class,%.java,$(CLASSES))

compile-classes: $(SRC)
	$(E) "  JAVAC   " $?
	$(Q) $(JAVAC) -g -Xlint:unchecked -source 1.6 -target 1.6 -encoding utf8 -cp src $?
	$(Q) touch compile-classes

all: compile-classes

check: all
	$(E) "  CHECK"
	$(Q) ./scripts/suite

clean:
	$(E) "  CLEAN"
	$(Q) - find src/ -name "*.class" | xargs rm -f
	$(Q) rm -f compile-classes

.PHONY: all check clean
