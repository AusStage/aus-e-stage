#!/bin/bash
# script to ease compiling by inlcuding class path
MYPATH=`pwd`
#
# change to the build directory
cd $MYPATH/build
# execute the appropriate command
/usr/bin/java -cp .:$MYPATH/lib/* RdfExport -tasktype build-network-data -properties $MYPATH/default.properties
