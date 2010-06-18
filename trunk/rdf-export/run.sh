#!/bin/bash
# script to ease compiling by inlcuding class path
MYPATH=`pwd`
#
# change to the build directory
cd $MYPATH/jar
# print current date / time
date
# execute the appropriate command
/usr/bin/java -Xmx2028m -XX:-UseGCOverheadLimit -jar AusStageRdfExport.jar -tasktype build-network-data -properties $MYPATH/default.properties
# print current date / time
date
