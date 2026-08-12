#!/bin/bash

property=gnu.lang.process.posixSpawn
tests=(
  malva.java.lang.ProcessTest
  malva.java.lang.ProcessBuilderTest
  malva.java.lang.ProcessClosedStdioTest
)

case "$(uname -s)" in
  Linux|Darwin)
    ;;
  *)
    echo "SKIP: $(basename "$0"): supported only on Linux and macOS"
    exit 0
    ;;
esac

properties=$($JAVA -cp src malva.DumpSystemProperties)
if [ "$?" != "0" ]; then
  exit 1
fi

grep -qF 'gnu.classpath.version=' <<< "$properties"
if [ "$?" != "0" ]; then
  echo "SKIP: $(basename "$0"): requires GNU Classpath"
  exit 0
fi

for value in false true; do
  printf 'Running process tests with [%s=%s]: ' "$property" "$value"

  for test in "${tests[@]}"; do
    # Expand $JAVA like scripts/suite, where it may include VM arguments
    # Capture all output for sub-tests, only show it on failure
    output=$($JAVA "-D$property=$value" -cp src "$test" 2>&1)

    if [ "$?" != "0" ]; then
      # Convert package name to path, as in scripts/suite
      name=${test//./\/}
      printf '%s FAILED\n' "$name"
      if [ -n "$output" ]; then
        printf '%s\n' "$output"
      fi
      exit 1
    fi
  done

  printf 'OK\n'
done
