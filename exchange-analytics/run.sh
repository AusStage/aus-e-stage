#!/bin/bash
# script to ease compiling by inlcuding class path
MYPATH=`pwd`
OUTPUT_PATH=/opt/local/persistent_data/root-web/exchange-analytics.xml
cd $MYPATH/bin
/bin/rm $OUTPUT_PATH
/usr/bin/java -cp .:$MYPATH/lib/* ExchangeAnalytics $MYPATH/log-files $MYPATH/output/database $OUTPUT_PATH
