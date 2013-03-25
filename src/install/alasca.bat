@echo off

setLocal EnableDelayedExpansion

rem ---------------------------------------------------------------------------
rem AUTOLOAD
rem ---------------------------------------------------------------------------

rem Set ALASCA_BIN_PATH
set ALASCA_BIN_PATH=%cd%
rem echo %ALASCA_BIN_PATH%

rem Set ALASCA_ROOT_PATH
set ALASCA_ROOT_PATH=%cd:~0,-3%
rem echo %ALASCA_ROOT_PATH%

rem Set ALASCA_LIB_PATH
set ALASCA_LIB_PATH=%ALASCA_ROOT_PATH%lib
rem echo %ALASCA_LIB_PATH%

rem Find JAVA version
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
	set JAVA_VERSION=%%g
)
rem echo %JAVA_VERSION%

rem Set empty ALASCA_JAR
set ALASCA_JAR=''

rem ---------------------------------------------------------------------------
rem RUN
rem ---------------------------------------------------------------------------

set ALASCA_JAR=''
for /R %ALASCA_LIB_PATH% %%g in (*.jar) do (
	set ALASCA_JAR=!ALASCA_JAR!;%%g
)
set ALASCA_JAR=!ALASCA_JAR!"
set ALASCA_JAR=%ALASCA_JAR:~3,-1%

cd %ALASCA_ROOT_PATH%
java.exe -cp %ALASCA_JAR% net.aepik.alasca.Launcher %*
