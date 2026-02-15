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

SRC := $(shell find src -name '*.java')

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
