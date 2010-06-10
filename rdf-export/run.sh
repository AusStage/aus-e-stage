#!/bin/bash
# script to ease compiling by inlcuding class path
MYPATH=`pwd`
#
# change to the build directory
cd $MYPATH/jar
# execute the appropriate command
/usr/bin/java -jar AusStageRdfExport.jar -tasktype build-network-data -properties $MYPATH/default.properties
