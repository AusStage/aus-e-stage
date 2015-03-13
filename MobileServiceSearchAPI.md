<h1>Proposed Search API - NOT implemented</h1>

**NOTE: This is only a proposed API and hasn't actually been implemented**

Components of the Mobile Service are exposed as an [API](http://en.wikipedia.org/wiki/Application_programming_interface). The intention of the API is to make it easier for developers to customise their own front end interfaces and to split the work equitably amongst the software engineers working on the Aus-e-Stage project.

The Search API exposes methods and data elements that can be used to search for contributors, organisations, etc.

_Note 1: During development of the service revisions the API will use a temporary URL which will be changed once the service revisions are complete._

_Note 2: The search interface provided by the mapping service is designed to be supplementary to the existing text based search interface. Once the new AusStage website is in place this API is expected to be deprecated as the moblie service will be integrated with the new AusStage website._



# Base URL #

http://beta.ausstage.edu.au/mobile/search?

Combining the base URL with the parameters outlined below constructs a call to the API.

# Parameters #

| **Name** | **Possible Values** | **Optional** | **Default** |
|:---------|:--------------------|:-------------|:------------|
| task   | organisation, venue, event, question, hashtag, feedback | No |  |
| type   | name, id     | No |  |
| query  | the search query | No |  |
| format | json | Yes | json |
| limit  | an integer between 5 and 25 | Yes | 5 |
| callback | function name to use for a JSONP request | Yes |  |

## task ##
The type of search task to undertake for example a search for organisation or fdata

## type ##
The type of search to undertake either by name or by id number

## query ##
The search query, either a list of search terms that must be present in the name or the id number to search for

_Note:_ If a name search is undertaken the query is sanitised by:

  * removing white-space at the start and end of the query
  * remove all occurrences of the words and, or, not

Records containing all of the search terms are returned within the record limit specified

## format ##
The type of data format used to prepare the search results. At the time of writing only [JSON](http://en.wikipedia.org/wiki/JSON) is a supported format

## limit ##
The maximum number of result records to be returned. By default only 5 records are returned

## callback ##
The name of the callback function used when a request is made for JSONP encoded data

# Sample Output #

The search result is formatted as a JSON array with zero, one, or more result objects.

### Organisation, Venue, Event, Question, Hashtag ###
Conduct a search for performances that contain the words "What"  "makes"  "you" in their hashtag using the default record limit

URL: http://beta.ausstage.edu.au/mobile/search?task=hash&type=name&query=What+makes+you
```
{ "endDateTime" : "30-SEP-2010 22:00:00",
  "event" : "Be Your Self",
  "eventUrl" : "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&amp;f_event_id=86534",
  "id" : 4,
  "organisation" : "Australian Dance Theatre",
  "organisationUrl" : "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&amp;f_org_id=102",
  "question" : "What makes you, you?",
  "tag" : "beyourself",
  "totalFeedbackCount": "6",   
  "startDateTime" : "30-SEP-2010 19:30:00",
  "venue" : "Golden Grove Recreation and Arts Centre",
  "venueUrl" : "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&amp;f_venue_id=3447"
}
```

### Feedback ###
Feedback Name Search
Conduct a search for performances that contain the words "What"  "makes"  "you" in their hashtag using the default record limit

URL: http://beta.ausstage.edu.au/mobile/search?task=feedback&type=name&query=dna

```
   "date": "Thursday 30 September, 2010",
   "event": "Be Your Self".
 "id" : 4,

   "eventUr": "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&amp;f_event_id=86534",
   "feedback:  [
     {
      "content: "My dna #adt",
      "date: "Thursday 30 September, 2010",
       "id: "93",
       "time: "21:05:31",
       "type: "SMS"},

],

  "organisation: "Australian Dance Theatre",
  "organisationUrl: "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&amp;f_org_id=102",
  "question: "Test Development Question",
  "venue: "Golden Grove Recreation and Arts Centre",
  "venueUrl: "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&amp;f_venue_id=3447",
}
```