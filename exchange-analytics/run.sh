#!/bin/bash
# script to ease compiling by inlcuding class path
MYPATH=`pwd`
OUTPUT_PATH=$MYPATH/output/exchange-analytics.xml
cd $MYPATH/bin
/bin/rm $OUTPUT_PATH
/usr/bin/java -cp .:$MYPATH/lib/hsqldb.jar ExchangeAnalytics $MYPATH/log-files $MYPATH/output/database $OUTPUT_PATH
