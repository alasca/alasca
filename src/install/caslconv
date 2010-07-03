#!/bin/sh

CASL_BIN_PATH="`dirname "$0"`"
CASL_BIN_FILENAME="`cd \"${CASL_BIN_PATH}\" 2>/dev/null && pwd || echo \"${CASL_BIN_PATH}\"`/`basename "$0"`"
CASL_ROOT_PATH="`dirname ${CASL_BIN_FILENAME}`"
CASL_ROOT_PATH="`dirname ${CASL_ROOT_PATH}`"
CASL_LIB_PATH="${CASL_ROOT_PATH}/lib"

# For JDK < 1.5
#export CASL_LIB_PATH
#cd "${CASL_DIR}"

CASL_JAR="`find ${CASL_LIB_PATH} -name *.jar -print | perl -p0e 's/\n/:/g;s/:$//g'`"
java -cp ${CASL_JAR} net.aepik.casl.plugin.schemaconverter.SCTool $*

