<h1>Updating an Aus-e-Stage Webapp</h1>

This document outlines the basic procedure that needs to be undertaken to update an Aus-e-Stage webapp on the [beta.ausstage.edu.au](http://beta.ausstage.edu.au) server.



# Prerequisites #

The procedures outlined in this document assume that the following prerequisites are met.

## Skill Prerequisites ##

  * Experience in developing [Java](http://www.java.com) based applications, including diagnosing and troubleshooting compiler errors
  * Familiarity with the with the [Apache Tomcat](http://tomcat.apache.org/) server software
  * Familiarity with the [Subversion](http://subversion.apache.org/) version control client for your operating system
  * Experience in editing and authoring XML documents
  * Familiarity with the [Apache Ant](http://ant.apache.org/) build system
  * Experience in executing commands in a command line based environment

## Software Prerequisites ##

Ensure the following software packages are installed on your system

  * The latest version of the 1.6 branch of the [Java SE Development Kit](http://www.oracle.com/technetwork/java/index.html) (Java SE JDK) for your platform
  * The latest version of the 6.0 branch of the [Apache Tomcat](http://tomcat.apache.org/) server software. Ensure the version of Tomcat includes the Host Manager webapp
  * The latest version of the 1.8 branch of the [Apache Ant](http://ant.apache.org/) build system
  * A [Subversion](http://subversion.apache.org/) client for your operating system (Note a CLI version of Subversion is assumed by this document)

## Java Library Dependencies ##

All Aus-e-Stage webapps, excluding the root website, require the [Oracle JDBC](http://www.oracle.com/technetwork/database/features/jdbc/index-091264.html) library to be available in the Java classpath.

## Other Prerequisites ##

In order to checkout code from the Aus-e-Stage code repository reliably you must have committer level access or above.

# Prepare Your System #

In order to successfully compile and update a webapp your system must be prepared by undertaking the following tasks.

## Software Installation ##

Ensure that the software as listed in the [Software Prerequisites](UpdateTomcatWebapp#Software_Prerequisites.md) section has been installed and configured appropriately. As operating systems are different no specific guidelines can be provided. You are referred to the operating system specific documentation for each software package.

# Webapp Source Code #

All source code related to Aus-e-Stage webapps is stored in the Aus-e-Stage code repository. The base URL for the repository is as follows:

https://aus-e-stage.googlecode.com/svn/trunk/

The URLs for each of the four main webapps are as follows:

| **Webapp Name**                                     | **SVN Repository URL**                                   |
|:----------------------------------------------------|:---------------------------------------------------------|
| Aus-e-Stage Beta root website                     | https://aus-e-stage.googlecode.com/svn/trunk/root-web/ |
| AusStage Audience Participation (Mobile) Service | https://aus-e-stage.googlecode.com/svn/trunk/mobile/   |
| AusStage Mapping Service                         | https://aus-e-stage.googlecode.com/svn/trunk/mapping2/ |
| AusStage Navigating Networks Service             | https://aus-e-stage.googlecode.com/svn/trunk/networks/ |

**Note:** The current development version of the AusStage Mapping Service is currently stored in the mapping2 directory, it is anticipated that before the end of the project the webapp will be migrated to the mapping directory which currently contains a version of the mapping service that was used for Alpha testing in early 2010.

If you have not previously checked out a copy of the source code for the webapp you are updating follow the [checkout](UpdateTomcatWebapp#Checking_out_the_Webapp_Source_Code.md) procedure, if you only need to update your copy of the sourcecode follow the [update](UpdateTomcatWebapp#Updating_your_Working_Copy_of_the_Webapp_Source_Code.md) procedure.

## Checking out the Webapp Source Code ##

To Checkout a copy of the source code using your subversion client and the username and password that you use to access this website. For example using a CLI version of subversion the following command would be used to checkout the mapping service into the current directory.

Replace {username} with your username and {password} with your password

  1. Open a command line window
  1. Change to appropriate directory on your computer
  1. Issue the following command
> > `svn checkout https://aus-e-stage.googlecode.com/svn/trunk/mapping2/ mapping2 --username {username} --password {password}`
  1. Wait while the source code is downloaded and any external items are included

## Updating your Working Copy of the Webapp Source Code ##

To update your working copy using the CLI version of subversion the procedure is:

  1. Open a command line window
  1. Change to the root directory of the webapp
  1. Update the source code by issuing the following command
> > `svn update`
  1. Wait while the updated source code is downloaded and any external items are updated

## Webapp Dependencies ##

A webapp will rely on one or more third party libraries. The details of the required libraries are store in the `lib/README.txt` file. This file details which libraries are required including the minimum required version. All webapps assume that the Oracle JDBC library is already installed and available in the Java classpath.

Some webapps will rely on the same libraries as other webapps. The reason for using seperate `lib/` directories is to ensure that if different versions are required by one webapp and not another that conflicts between versions will not occur.

Whether updating your working copy, or checking out the source code for the first time, it is advisable to check the `README.txt` file for details of any new libraries, or changes to existing libraries.

# Configure the Webapp #

All of the webapps require configuration. This is achieved using the `web/WEB-INF/web.xml` file. As this file contains sensitive information, such as database connection strings, a `web/WEB-INF/web.xml.sample` file is included in the source code repository. This file is `web.xml` file without any of the parameters set.

## Creating a new web.xml file ##

To create a new `web.xml` file undertake the following procedure

  1. Copy the existing `web.xml.sample` file to a new `web.xml` file
  1. Open the newly created `web.xml` file and populate the `<param-value>` elements with appropriate values.

## Updating an existing web.xml file ##

To update an existing `web.xml` file compare the list of `<context-param>` elements in the `web.xml.sample` file with the existing `web.xml` file and make any additions / deletions as appropriate. Remember to populate any new `<param-value>` elements with appropriate values.

## Ant build.properties file ##

The Ant build script for each webapp is stored in a `build.xml` file in the root directory of each app. This script relies in values in a `build.properties` file which is co-located with `build.xml` file. The basic layout is as follows, the values for each of the properties is platform / webapp specific

```
#Context path to install this application on
app.path=

# App Specific options
app.name=
app.version=

# Tomcat 6 installation directory
catalina.home=

# Tomcat 6 URL
manager.url=

# Manager webapp username and password
manager.username=
manager.password=
```

# Compiling and Packaging the Webapp #

Compiling and Packaging the Webapp assumes that the following conditions are met:

  1. All required Java libraries are in the `lib/` directory
  1. The `web.xml` file is in place
  1. The `build.properties` file is in place

To compile and package the website:

  1. Open a command line window
  1. Change to the root directory of the webapp
  1. Clean the build directories by executing the `clean` ant task using a command as follows
> > `ant clean`
  1. Compile the source code using the `compile` ant task using a command as follows
> > `ant compile`
  1. If no compile errors are reported, package the using the `dist` ant task using a command as follows
> > `ant dist`
  1. The packaged webapp will now be available as a war file in the `dist/` directory
  1. If compile errors are reports, resolve the errors before continuing

# Uploading a New Version of the webapp #

**Note:** The username and password for uploading a new webapp can be obtained by contacting the AusStage Project Manager.

**Note:** When a webapp is undeployed it is deleted from the server and is unavailable to all users until the replacement webapp is successfully deployed.

To upload a new version of the compiled webapp follow this procedure:

  1. Enter the following URL into the address bar of your browser
> > http://beta.ausstage.edu.au/manager/html
  1. When prompted enter the appropriate username and password
  1. Click the _Undeply_ link to the right of the name of the webapp you wish to replace
  1. Click the _Browse_ button under the _WAR file to deploy_ heading at the bottom of the page
  1. Browse and select the war file created as the result of the [Compiling and Packaging procedure](UpdateTomcatWebapp#Compiling_and_Packaging_the_Webapp.md)
  1. Click the _Deploy_ button
  1. Ensure the webapp successfully deployed by clicking the link in the _Path_ column of the table to the left of the webapp name and interacting with the webapp