<h1>Input Gatherer</h1>

The intention of the Input Gatherer is to provide a standardised way that feedback can be added to the mobile service. This API is supplemented with specific applications such as the TwitterGatherer as required.

# Adding an SMS message #

An SMS message can be submitted for processing using the following API

## Base URL ##

TBA

<a href='Hidden comment: 
http://beta.ausstage.edu.au/mobile/gatherer?
'></a>

Combining the base URL with the parameters outlined below constructs a call to the API.

## Request Type ##

Both get and post requests are supported

## Parameters ##

| **Name** | **Required Value** | **Optional** | **Default** |
|:---------|:-------------------|:-------------|:------------|
| type   | sms              | No         |  |
| caller | the mobile device number | No |  |
| time   | the time that the message was sent | No |  |
| date   | the date that the message was sent | No |  |
| message| the text of the feedback message | No |  |

## Additional Information ##

Only hosts that are from a list of predetermine IP addresses are allowed to add an SMS message

An overview of the way SMS messages are integrated with AusStage is [available here](http://techxplorer.com/2010/09/10/integrating-ausstage-with-sms-messages/).

A diagram providing an overview of the workflow involved in processing SMS messages is [also available](http://aus-e-stage.googlecode.com/svn-history/trunk/wiki-assets/incoming-sms-workflow.png).

# Adding Feedback from the Mobile Web #

An web message can be submitted for processing using the following API

## Base URL ##

TBA

<a href='Hidden comment: 
http://beta.ausstage.edu.au/mobile/gatherer?
'></a>

Combining the base URL with the parameters outlined below constructs a call to the API.

## Request Type ##

Both get and post requests are supported

## Parameters ##

| **Name** | **Required Value** | **Optional** | **Default** | **Example** |
|:---------|:-------------------|:-------------|:------------|:------------|
| type   | mobile-web              | No        |  |  |
| performance  | The unique performance ID | No |  |  |
| message| the text of the feedback message | No |  |  |

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