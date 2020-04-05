#!/bin/sh

#  Supercars 3 run script
#
#  written by JOTD
#

SC3_ROOT_DIR=$(cd `dirname $0`/..;pwd)
HEAP_SIZE=100m

java -classpath "$SC3_ROOT_DIR/classes:$SC3_ROOT_DIR/lib/golden_0_2_3.jar" -DSC3_ROOT_DIR="$SC3_ROOT_DIR" -Xmx$HEAP_SIZE $*

