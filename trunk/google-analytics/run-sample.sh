#!/bin/bash
# script to ease compiling by inlcuding class path
MYPATH=`pwd`
OUTPUT_PATH=$MYPATH/output/analytics-report.xml
EMAIL=google.analytics@ausstage.edu.au
PASS=#######
ACTION=list
cd $MYPATH/bin
#/bin/rm $OUTPUT_PATH
/usr/bin/java -cp .:$MYPATH/lib/* GoogleAnalytics -email $EMAIL -password $PASS -action $ACTION
