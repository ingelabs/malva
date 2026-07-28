# External programs
JAVA	?= java
JAVAC	?= javac
CC	?= cc

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
NATIVE_SRC := $(wildcard native/*.c)
NATIVE_OUTPUT_DIR := build/native
NATIVE_HELPERS := $(patsubst native/%.c,$(NATIVE_OUTPUT_DIR)/%,$(NATIVE_SRC))

MALVA_NATIVE_DIR := $(abspath $(NATIVE_OUTPUT_DIR))
export MALVA_NATIVE_DIR

all: compile-classes native-helpers

compile-classes: $(SRC)
	$(E) "  JAVAC   " $?
	$(Q) $(JAVAC) -g -Xlint:unchecked -source 1.6 -target 1.6 -encoding utf8 -cp src $?
	$(Q) touch compile-classes

$(NATIVE_OUTPUT_DIR)/%: native/%.c | $(NATIVE_OUTPUT_DIR)
	$(E) "  CC      " $<
	$(Q) $(CC) $(CPPFLAGS) $(CFLAGS) $(LDFLAGS) -o $@ $< $(LDLIBS)

$(NATIVE_OUTPUT_DIR):
	$(Q) mkdir -p $@

native-helpers: $(NATIVE_HELPERS)

check: all
	$(E) "  CHECK"
	$(Q) ./scripts/suite

checkall: all
	$(E) "  CHECK"
	$(Q) SUITE_RUN_ALL=1 ./scripts/suite

clean:
	$(E) "  CLEAN"
	$(Q) - find src/ -name "*.class" | xargs rm -f
	$(Q) rm -f compile-classes
	$(Q) rm -rf build

.PHONY: all native-helpers check checkall clean
