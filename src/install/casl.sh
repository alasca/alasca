#!/bin/sh

D="`dirname "$0"`"
SCRIPTNAME="`basename "$0"`"
FILENAME="`cd \"${D}\" 2>/dev/null && pwd || echo \"${D}\"`/${SCRIPTNAME}"
unset D

CASL_DIR="`dirname ${FILENAME}`"
CASL_DIR="`dirname ${CASL_DIR}`"

cd "${CASL_DIR}"
java -jar ${CASL_DIR}/lib/casl-*.jar

