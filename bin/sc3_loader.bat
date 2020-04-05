rem @echo off

rem
rem  Supercars 3 run script
rem
rem  written by JOTD
rem

set OLDCD=%CD%
cd %0\..\..
set SC3_ROOT_DIR=%CD%

rem increase heap size if necessary

set HEAP_SIZE=100m

rem echo %SC3_ROOT_DIR%


start javaw -Djava.library.path="%SC3_ROOT_DIR%/bin" -classpath "%SC3_ROOT_DIR%\classes;%SC3_ROOT_DIR%\lib\golden_0_2_3.jar" -DSC3_ROOT_DIR="%SC3_ROOT_DIR%" -Xmx%HEAP_SIZE% %1 %2 %3 %4
cd %OLDCD%
