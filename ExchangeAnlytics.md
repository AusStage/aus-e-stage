<h1> AusStage Exchange Analytics </h1>

The AusStage Exchange Analytics app parses the log files created by the [Data Exchange](http://beta.ausstage.edu.au/exchange/) service to create a report that provides an overview of the usage of the service.

This report is available via the Aus-e-Stage website under the "Analytics Tab". A [direct link](http://beta.ausstage.edu.au/?tab=analytics&section=exchange) is also available.

The report contains the following sections:

  1. Requests for the current month
  1. Requests for the previous month
  1. Requests for the year
  1. Requests by record type
  1. Requests for event data by request type
  1. Requests for resource data by request type
  1. Requests for event data by output type
  1. Requests for resource data by output type
  1. Requests for feedback data by output type

The report is updated automatically every Monday morning, and can be run manually at other times.



# Exchange Analytics Properties File #

The Exchange Analytics app is configured using a properties file. This file must contain the following fields

| **Field Name** | **Description** |
|:---------------|:----------------|
| input-dir    | the directory containing the log files to process |
| output-dir   | the directory that will contain the generated xml report |
| database-dir | the directory that contains the [Apache Derby](http://db.apache.org/derby/) database |

## Sample Properties File ##

```
# properties for the ExchangeAnalytics app
#
input-dir = /full/path/to/the/input/
output-dir = /full/path/to/the/output/
database-dir = /full/path/to/the/database/
```

# Using the  Exchange Analytics App #

The Exchange Analytics ap is installed on the AusStage production server in the standard aus-e-stage directory. The application is set to run once a week every Monday morning. It can be run manually by logging into the server via SSH and executing the run.sh script in the main exchange-analytics directory

# Exchange Analytics Database #

The raw log files produced by the [Data Exchange](http://beta.ausstage.edu.au/exchange/) service are parsed and the relevant information is stored in an [Apache Derby](http://db.apache.org/derby/) database. The log files contain details of errors, starts and stops of the service, and other information which is not of relevance to the production of analytics. The database serves two purposes:

  1. It makes it easier to mine the data to produce the report
  1. It stores the data in an easy to reuse format, additional analytics do not need to parse the log files again

Any application capable of reading an http://db.apache.org/derby/ Apache Derby] database can query the data to provide additional information that isn't already in the report. Alternatively the app itself can be extended to provide the additional information

# Application Workflow #

The steps that the app, and supporting scripts undertake are:

  1. Copy new log files out of the [Apache Tomcat](http://tomcat.apache.org/) directory into the input directory
  1. Parse any new log files adding data to the database
  1. Compile the data for an updated report
  1. Write a new report file
  1. Rename the processed log files so that they are not processed again

In this way historic log files are stored outside the [Apache Tomcat](http://tomcat.apache.org/) directory which means different data retention policies can be applied to the log files. For example keeping a minimum amount of logging in the tomcat directory and keeping log files for longer in the input directory.

The database can also be used to compile different one off reports, and the application can be extended to create reports with different requirements without the need to parse log files again.

# Source Code #

The source code is available for download from our [repository here](http://code.google.com/p/aus-e-stage/source/browse/#svn%2Ftrunk%2Fexchange-analytics).