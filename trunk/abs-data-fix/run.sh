#!/bin/bash
# script to ease compiling by inlcuding class path
MYPATH=`pwd`
# age by sex task
#FIX_TYPE=agebysex
#INPUT_PATH=$MYPATH/input/act-age-by-sex.txt
#OUTPUT_PATH=$MYPATH/output/act-age-by-sex.xml
# use output of a prep task, as above, to build a new data file
FIX_TYPE=databuilder
INPUT_PATH=$MYPATH/input/act-age-by-sex.xml
OUTPUT_PATH=$MYPATH/output/abs-data-act.xml
if [ -e "$OUTPUT_PATH" ]
then 
  /bin/rm $OUTPUT_PATH
fi
cd $MYPATH/bin
/usr/bin/java -cp .:$MYPATH/lib/* AbsDataFix -fixtype $FIX_TYPE -input $INPUT_PATH -output $OUTPUT_PATH

