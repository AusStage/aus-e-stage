

# Basic Search API #

The basic search API allows for searches to be undertaken of the [dataset](NavigatingNetworksDataset.md) it is used primarily to provide search capabilities via AJAX for other parts of the [Navigating Networks Service](NetworkService.md) such as the [Graph Export](NavigatingNetworksExportGraphs.md) page.

## Base URL ##

The base URL for the Lookup API is as follows:

http://beta.ausstage.edu.au/networks/search?

## Request Type ##

Get Request

## Available Parameters ##

| **Name** | **Possible Values** | **Optional** | **Default** |
|:---------|:--------------------|:-------------|:------------|
| task | collaborator | No |  |
| query | search terms | No |  |
| sort | name | Yes | name |
| format | json | Yes | json |
| limit | integer between 5 and 25 | Yes | 5 |
| callback | function name to use for a JSONP request | Yes |  |

## Parameter Definitions ##

### Task: collaborator ###

Undertake a collaborator search

### Query ###

A list of search terms. Currently the search is a case insensitive substring match using the collaborator name

### Sort: name ###

Sort the search results in name order

### Format: json ###

Format the search result as a JSON array

## Limit: the number of results to return ##

A maximum limit of 25 is imposed by the API as a way to reduce resource consumption and to reinforce that the search API is designed to be supplementary to other discovery mechanisms that will be made available in the future.

## Callback: the name of the callback function ##
When data is requested using the [JSON](http://en.wikipedia.org/wiki/JSON) format the callback parameter specifies the function name to use for a [JSONP](http://en.wikipedia.org/wiki/JSON#JSONP) request.

## Sample Output ##

```
[{
  "id":414726,
  "functions":["Properties Master"],
  "name":"Anni Archer",
  "familyName":"Archer",
  "collaborations":1,
  "givenName":"Anni",
  "url":"http:\/\/www.ausstage.edu.au\/indexdrilldown.jsp?xcid=59&f_contrib_id=414726"
 },
 {
  "id":253687,
  "functions":["Actor"],
  "name":"Brett Archer",
  "familyName":"Archer",
  "collaborations":3,
  "givenName":"Brett",
  "url":"http:\/\/www.ausstage.edu.au\/indexdrilldown.jsp?xcid=59&f_contrib_id=253687"
 }
]
```