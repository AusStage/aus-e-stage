#!/bin/bash
# script to ease execution of the app
# declare some variables
MYPATH=`pwd`
TASK_TYPE=export-network-data
OUTPUT_PATH=$MYPATH/output/network-export.xml
DATA_FORMAT=
#
# delete the output file if it already exists
if [ -e "$OUTPUT_PATH" ]; then 
  /bin/rm $OUTPUT_PATH
fi
#
# change to the build directory
cd $MYPATH/jar
#
# print current date / time
date
#
# execute the appropriate command
if [ -z "$OUTPUT_PATH" ]; then
  /usr/bin/java -Xmx2028m -XX:-UseGCOverheadLimit -jar AusStageRdfExport.jar -tasktype $TASK_TYPE -properties $MYPATH/default.properties
else 
  /usr/bin/java -Xmx2028m -XX:-UseGCOverheadLimit -jar AusStageRdfExport.jar -tasktype $TASK_TYPE -properties $MYPATH/default.properties -output $OUTPUT_PATH
fi
#
# print current date / time
date
