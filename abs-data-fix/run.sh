#!/bin/bash
# script to ease compiling by inlcuding class path
MYPATH=`pwd`
# age by sex task
#FIX_TYPE=agebysex
#INPUT_PATH=$MYPATH/input/act-age-by-sex.txt
#OUTPUT_PATH=$MYPATH/output/act-age-by-sex.xml
# use output of a prep task, as above, to build a new data file
#FIX_TYPE=databuilder
#INPUT_PATH=$MYPATH/input/act-age-by-sex.xml
#OUTPUT_PATH=$MYPATH/output/abs-data-act.xml
# append collection district code to an existing data file
FIX_TYPE=appendcdinfo
INPUT_PATH=$MYPATH/input/abs-data-act.xml
OUTPUT_PATH=$MYPATH/output/abs-data-act-with-cd-info.xml
CODES_PATH=$MYPATH/input/act-collection-district-list.txt
# delete the output file if it already exists
if [ -e "$OUTPUT_PATH" ]; then 
  /bin/rm $OUTPUT_PATH
fi
# change to the binary directory
cd $MYPATH/bin
# execute the appropriate command
if [ -z "$CODES_PATH" ]; then
  /usr/bin/java -cp .:$MYPATH/lib/* AbsDataFix -fixtype $FIX_TYPE -input $INPUT_PATH -output $OUTPUT_PATH
else 
  /usr/bin/java -cp .:$MYPATH/lib/* AbsDataFix -fixtype $FIX_TYPE -input $INPUT_PATH -output $OUTPUT_PATH -codes $CODES_PATH
fi
