#!/bin/bash
# get the current path
MY_PATH=`pwd`
#
# delete any existing files
/bin/rm -rf $MY_PATH/dist/*
#
#
# copy the library to the dist directory
/bin/cp $MY_PATH/lib/commons-lang-2.5.jar $MY_PATH/dist
#
# make a temp directory
if [ -d $MY_PATH/dist/tmp ]; then
	/bin/rm -rf $MY_PATH/dist/tmp
fi
/bin/mkdir $MY_PATH/dist/tmp
#
# copy the class files to the tmp directory
/bin/cp $MY_PATH/bin/*.class $MY_PATH/dist/tmp/
#
# copy the license & readme files to the dist directory
/bin/cp $MY_PATH/LICENSE.txt $MY_PATH/dist/
/bin/cp $MY_PATH/README.txt $MY_PATH/dist/
#
# build the jar file
cd $MY_PATH
/usr/bin/jar cfm ./dist/AbsDataFix.jar ./src/jar-manifest.txt  -C ./dist/tmp/ .
#
# tidy up
if [ -d $MY_PATH/dist/tmp ]; then
        /bin/rm -rf $MY_PATH/dist/tmp
fi
# change to the dist directory and create a new zip file
cd $MY_PATH/dist
/usr/bin/zip AbsDataFix.zip *.jar LICENSE.txt README.txt
