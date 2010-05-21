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
# build the jar file
cd $MY_PATH
/usr/bin/jar cvfm ./dist/AbsDataFix.jar ./src/jar-manifest.txt  LICENSE.txt -C ./dist/tmp/ .
#
# tidy up
if [ -d $MY_PATH/dist/tmp ]; then
        /bin/rm -rf $MY_PATH/dist/tmp
fi

