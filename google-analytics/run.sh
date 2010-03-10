#!/bin/bash
# script to ease compiling by inlcuding class path
MYPATH=`pwd`
OUTPUT_PATH=$MYPATH/output/analytics-report.xml
cd $MYPATH/bin
/bin/rm $OUTPUT_PATH
/usr/bin/java -cp .:$MYPATH/lib/* GoogleAnalytics $OUTPUT_PATH
