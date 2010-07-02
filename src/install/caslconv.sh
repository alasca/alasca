#!/bin/sh

D="`dirname "$0"`"
SCRIPTNAME="`basename "$0"`"
FILENAME="`cd \"${D}\" 2>/dev/null && pwd || echo \"${D}\"`/${SCRIPTNAME}"
unset D

CASL_DIR="`dirname ${FILENAME}`"
CASL_DIR="`dirname ${CASL_DIR}`"
CASL_JAR="`find ${CASL_DIR}/lib/ -name *.jar -print | perl -p0e 's/\n/:/g;s/:$//g'`"

cd "${CASL_DIR}"
java -cp ${CASL_JAR} net.aepik.casl.plugin.schemaconverter.SCTool $*

