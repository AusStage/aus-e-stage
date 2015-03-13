# AusStage Visualisation Service #

The Visualisation Service is designed to display the statistical information on AusStage data in variant charts.

The way of retrieving data is by using the chart API. Then [d3.js](http://mbostock.github.com/d3/) is used to create charts.More examples of charts can be found at [d3.js:Examples of Basic Charts](http://www.verisi.com/resources/d3-tutorial-basic-charts.htm)

<h1> Chart API </h1>

The Chart API provide the functionality to retrieve data for a given task. The details of the API are as follows:

## Base URL ##

The base URL for the Chart API is as follows:

http://beta.ausstage.edu.au/visualisation/chart?

Combining this URL with the parameters outlines below retrieves data from AusStage database and RDF datastore.

## Available Parameters ##

| **Name** | **Possible Values** | **Optional** | **Default** |
|:---------|:--------------------|:-------------|:------------|
| task   | EventsByStatusAndPrimaryGenre, EventsByStateAndPrimaryGenre | No |  |
| format |  CSV / JSON | No |  |
| start  | 2000 | yes | 2000 |
| end    | 2011 | yes | 2011 |

### All Possible Values for parameter task ###

  * EventsByStatusAndPrimaryGenre
  * EventsByStateAndPrimaryGenre
  * EventsByStateAndStatus
  * EventsCountByYear
  * EventsCountByYearAndState
  * EventsCountBySecondaryGenreAndYear
  * RecordCountNodeEdgeWeight
  * DistributionOfDegree
  * EventsCount
  * ResourceCount
  * ADSJoin
  * OnStageJoin
  * BooksJoin
  * ResourceTypeCount
  * RecordCountsInFiveTables
  * NumOfConsByFunction
  * NumOfEvtsBySecGenre
  * NumOfEvtsByContentIndi