#!/bin/bash
# get the current path
MY_PATH=`pwd`
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
# copy the license file to the dist directory
/bin/cp $MY_PATH/LICENSE.txt $MY_PATH/dist/
#
# build the jar file
cd $MY_PATH
/usr/bin/jar cfm ./dist/AbsDataFix.jar ./src/jar-manifest.txt  -C ./dist/tmp/ .
#
# tidy up
if [ -d $MY_PATH/dist/tmp ]; then
        /bin/rm -rf $MY_PATH/dist/tmp
fi
#
# delete any existing zip file
if [ -f $MY_PATH/dist/AbsDataFix.zip ]; then
        /bin/rm -rf $MY_PATH/dist/AbsDataFix.zip
fi
#
# change to the dist directory and create a new zip file
cd $MY_PATH/dist
/usr/bin/zip AbsDataFix.zip *.jar LICENSE.txt
