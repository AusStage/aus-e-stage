#!/bin/bash
# script to ease compiling by inlcuding class path
# get the full path
MYPATH=`pwd`
/usr/bin/javac -cp .:$MYPATH/lib/* -d $MYPATH/bin/ -s $MYPATH/src/ $MYPATH/src/*.java
