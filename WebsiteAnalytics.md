<h1> AusStage Website Analytics </h1>

The AusStage Website Analytics app automatically queries the [Google Analytics](http://www.google.com/analytics) service and creates reports that are displayed on the main [Aus-e-Stage project website](http://beta.ausstage.edu.au/) under the "Analytics" tab.

These reports can contain up to four different sections:

  1. Visits for the current month, by day
  1. Visits for the previous month, by day
  1. Visits for the current year, by month
  1. Visits for the previous year, by month

Other metrics could be added to the reports by modifying the Web Site Analytics application.



# Website Analytics Properties File #

The Website Analytics program is configured using a properties file. This file must contain the following fields

| **Field Name** | **Description** |
|:---------------|:----------------|
| config-dir   | directory containing report configuration template files |
| username     | the username used to access the Google Analytics service |
| password     | the password used to access the Google Analytics service |
| output-dir   | the directory where the generated report files will be written |

## Sample Properties File ##

```
# properties for the WebsiteAnalytics app
#
# directory containing the report configurations
config-dir = /path/to/config/files/
# username used to access the google analytics service
username = google-account
# password used to access the google analytics service
password = password
# directory where the report files will be written
output-dir = /path/to/output/directory/
```

# Using the Website Analytics Application #

The Website Analytics application is installed on the AusStage production server in the standard aus-e-stage directory. The application is set to run once a week every Monday morning. It can be run manually by logging into the server via SSH and executing the run.sh script in the main website-analytics directory

# Report Configuration Template #

Reports are configured using a simple XML based file that is based on the following template:

## Report Configuration XML ##

```
<?xml version="1.0" encoding="UTF-8"?>
<website-analytics>
  <config table-id="" url-pattern="" title=""/>
  <description title="">
    <![CDATA[
      
    ]]>
  </description>
  <sections>
    <section type="current-month"/>
    <section type="previous-month"/>
    <section type="current-year"/>
    <section type="previous-year"/>
  </sections>
</website-analytics>
```

## XML Field Descriptions ##

| **Element Name**    | **Description**                   | **Attribute Name** | **Description** |
|:--------------------|:----------------------------------|:-------------------|:----------------|
| website-analytics | root element                    |                  |               |
| config            | items that configure the report |                  |               |
|                   |                                 | table-id         | the identifier of the table containing data in the Google Analytics system |
|                   |                                 | url-pattern      | a regular expression of a pattern to match against to find pages that are of interest |
|                   |                                 | title            | the title of the report |
| description       | the description for this report |                  |                         |
|                   |                                 | title            | the title for the description section |
| sections          | list of sections that are to be included in the report |  |  |
| section           | a section to be added to the report |  |  |
|                   |                                 | type | the type of section |

### Valid Section Types ###

| **Section Type** | **Description** |
|:-----------------|:----------------|
| current-month  | visits to pages matching the url-pattern for the current month  |
| previous-month | visits to pages matching the url-pattern for the previous month |
| current-year   | visits to pages matching the url-pattern for the current year   |
| current-month  | visits to pages matching the url-pattern for the previous year  |

## Sample Configuration ##

Below is a sample configuration file for the [AusStage Mapping Service](MappingService.md)

```
<?xml version="1.0" encoding="UTF-8"?>
<website-analytics>
  <config table-id="private-table-id" url-pattern="^/mapping/" title="Visits to the AusStage Mapping Service"/>
  <description title="About These Analytics">
    <![CDATA[
      <p>Description goes here</p>
    ]]>
  </description>
  <sections>
    <section type="current-month"/>
    <section type="previous-month"/>
    <section type="current-year"/>
    <!--<section type="previous-year"/>-->
  </sections>
</website-analytics>
```

# Source Code #

The source code is available for download from our [repository here](http://code.google.com/p/aus-e-stage/source/browse/#svn%2Ftrunk%2Fwebsite-analytics).