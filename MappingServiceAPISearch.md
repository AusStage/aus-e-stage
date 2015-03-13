<h1>Search API</h1>

Components of the MappingService are exposed as an [API](http://en.wikipedia.org/wiki/Application_programming_interface). The intention of the API is to make it easier for developers to customise their own front end interfaces and to split the work equitably amongst the software engineers working on the Aus-e-Stage project.

The Search API exposes methods and data elements that can be used to search for contributors, organisations, etc.

_Note:_ The search interface provided by the mapping service is designed to be supplementary to the existing text based search interface. Once the new AusStage website is in place this API is expected to be deprecated as the mapping service will be integrated with the new AusStage website.



# Base URL #

http://beta.ausstage.edu.au/mapping2/search?

Combining the base URL with the parameters outlined below constructs a call to the API.

# Parameters #

| **Name** | **Possible Values** | **Optional** | **Default** |
|:---------|:--------------------|:-------------|:------------|
| task   | organisation, contributor, venue, event | No |  |
| type   | name, id     | No |  |
| query  | the search query | No |  |
| format | json | Yes | json |
| limit  | an integer between 5 and 25 | Yes | 5 |
| sort   | name, id | yes | name |
| callback | function name to use for a JSONP request | Yes |  |

## task ##
The type of search task to undertake for example a search for organisation or contributor data

## type ##
The type of search to undertake either by name or by id number

## query ##
The search query, either a list of search terms that must be present in the name or the id number to search for

_Note:_

If a name search is undertaken the query is sanitised by:

  * removing white-space at the start and end of the query
  * remove all occurrences of the words and, or, not

For example the search _Australian dance theatre_ becomes

%australian% and %dance% and %theatre%

If a name search is undertaken and they query is surrounded by double quotation marks, for example _"andrew bovell"_ the exact phrase is used in the search.

## format ##
The type of data format used to prepare the search results. At the time of writing only [JSON](http://en.wikipedia.org/wiki/JSON) is a supported format

## limit ##
The maximum number of result records to be returned. By default only 5 records are returned

## sort ##
Sort the result records by name or id **after** the limit has been applied to the results

## callback ##
The name of the callback function used when a request is made for JSONP encoded data