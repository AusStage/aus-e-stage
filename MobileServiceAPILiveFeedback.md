<h1>Live Feedback</h1>

The intention of the Live Feedback API is to provide a mechanism for a web page to retrieve feedback on a performance and check for new feedback periodically.

The API is comprised in two parts. The first is used to retrieve the initial batch of feedback as well as metadata about the performance. The second is used to check for new feedback periodically for display.



# Initial Request #

The initial request is used to retrieve metadata about the performance as well any feedback that already be in the system.

## Base URL ##

http://beta.ausstage.edu.au/mobile/feedback?

## Request Type ##

HTTP get request

## Parameters ##

| **Name** | **Required Value** | **Optional** | **Default** |
|:---------|:-------------------|:-------------|:------------|
| task | initial | No |  |
| performance | the unique performance ID | No |  |
| callback | function name to use for a JSONP request | Yes |  |

## Result data ##

A successful request will produce a [JSON](http://en.wikipedia.org/wiki/JSON) encoded object that contains metadata about the performance and an array containing a series of feedback objects.

The fields of the object are as follows:

| **Field Name** | **Purpose**|
|:---------------|:|
| event | the name of the event |
| eventUrl | the persistent link for the event in AusStage |
| question | the text of the question asked of the audience |
| organisation | the name of the organisation holding the event |
| organisationUrl | the persistent link for the organisation in AusStage |
| venue | the name of the venue where the event is being held |
| venueUrl | the persistent link for the venue in AusStage |
| tag | the tag is being used for this performance |
| date | the date that the event is occurring in the "Day DD Month, YYYY" format |
| feedback | an array of feedback objects |

A feedback object contains the following fields:

| **Field Name** | **Purpose**|
|:---------------|:|
| id | the unique id for this piece of feedback |
| content | the content of the feedback |
| type | the type of feedback. One of either Twitter / SMS / Mobile Web |
| date | the date that feedback was sent in the "Day DD Month, YYYY" format |
| time | the time that the feedback was sent |

_Note: Dates and times are reported in [South Australian local time](http://en.wikipedia.org/wiki/South_Australia)_

# Update Request #

The updates request is used to retrieve feedback that has been recorded by the system since the last check for feedback.

Using this API requires to parameters, the performance id and the unique identifier of the last piece of feedback retrieved. The result will be an array of feedback objects that have been stored in the system after the piece of feedback identified by the lastid parameter.

## Base URL ##

http://beta.ausstage.edu.au/mobile/feedback?

## Request Type ##

HTTP get request

## Parameters ##

| **Name** | **Required Value** | **Optional** | **Default** |
|:---------|:-------------------|:-------------|:------------|
| task | update | No |  |
| performance | the unique performance ID | No |  |
| lastid | the unique identifier of the last piece of feedback | No |  |
| callback | function name to use for a JSONP request | Yes |  |

## Result data ##

A successful request will produce a [JSON](http://en.wikipedia.org/wiki/JSON) encoded array containing a series of feedback objects.

A feedback object contains the following fields:

| **Field Name** | **Purpose**|
|:---------------|:|
| id | the unique id for this piece of feedback |
| content | the content of the feedback |
| type | the type of feedback. One of either Twitter / SMS / Mobile Web |
| date | the date that feedback was sent in the "Day DD Month, YYYY" format |
| time | the time that the feedback was sent |

_Note: Dates and times are reported in [South Australian local time](http://en.wikipedia.org/wiki/South_Australia)_