<h1>Lookup Information</h1>

The intention of the Lookup Information API is to provide a mechanism to lookup information about various items in the system. The items are detailed below.



# Base URL #

http://beta.ausstage.edu.au/mobile/lookup?

# Request Type #

HTTP get request

# Lookup System Properties #

## Parameters ##

| **Name** | **Required Value** | **Optional** | **Default** |
|:---------|:-------------------|:-------------|:------------|
| task   | system-property | No |  |
| id     | feedback-source-types | No |  |
| callback | function name to use for a JSONP request | Yes |  |

### feedback-source-types ###

Retrieves a list of valid source types for feedback.

```
[ { "description" : "Messages (tweets) collected from http://twitter.com",
    "id" : "1",
    "name" : "Twitter"
  },
  { "description" : "SMS messages sent to the AusStage GSM SMS Gateway",
    "id" : "2",
    "name" : "SMS"
  },
  { "description" : "Feedback collected via the mobile web interface",
    "id" : "3",
    "name" : "Mobile Web"
  }
]
```

# Lookup Data Fields #

## Parameters ##

| **Name** | **Required Value** | **Optional** | **Default** |
|:---------|:-------------------|:-------------|:------------|
| task   | performance, question | No |  |
| id     | unique id | No |  |
| callback | function name to use for a JSONP request | Yes |  |

### performance ###
An object describing a performance

```
{ "endDateTime" : "30-SEP-2010 22:00:00",
  "event" : "Be Your Self",
  "eventUrl" : "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&amp;f_event_id=86534",
  "id" : 4,
  "organisation" : "Australian Dance Theatre",
  "organisationUrl" : "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&amp;f_org_id=102",
  "question" : "What makes you, you?",
  "tag" : "beyourself",
  "startDateTime" : "30-SEP-2010 19:30:00",
  "venue" : "Golden Grove Recreation and Arts Centre",
  "venueUrl" : "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&amp;f_venue_id=3447"
}
```

_Note: Dates and times are reported in [South Australian local time](http://en.wikipedia.org/wiki/South_Australia)_

### question ###
An object describing a question used to solicit feedback

```
{ "id" : 2,
  "notes" : "Question used with first live test of the system",
  "text" : "What makes you, you?"
}
```

# Location Lookup Data Fields #

## Parameters ##

| **Name**   | **Required Value**     | **Optional** | **Default** |
|:-----------|:-----------------------|:-------------|:------------|
| task     | location             | No |  |
| lat      | Latitude as a float  | No |  |
| lng      | Longitude as a float | No |  |
| distance | Metres as an int     | No |  |
| callback | function name to use for a JSONP request | Yes |  |

## Result data ##

A successful request will produces a [JSON](http://en.wikipedia.org/wiki/JSON) encoded object that contains metadata about the performances.

The fields of the object are as follows:

| **Field Name** | **Purpose**|
|:---------------|:|
| event | the name of the event |
| eventUrl | the persistent link for the event in AusStage |
| question | the text of the question asked of the audience |
| tag | the tag is being used for this performance |
| organisation | the name of the organisation holding the event |
| organisationUrl | the persistent link for the organisation in AusStage |
| venue | the name of the venue where the event is being held |
| venueUrl | the persistent link for the venue in AusStage |
| date | the date that the event is occurring in the "Day DD Month, YYYY" format |

# Date Lookup Data Fields #

Looks up performances that occur within the date range.

If the enddate parameter is missing the API will return performances that have an end date in the database greater than or equal to the supplied start date.

## Parameters ##

| **Name**   | **Required Value**     | **Optional** | **Default** |
|:-----------|:-----------------------|:-------------|:------------|
| task     | date             | No |  |
| startdate     | Start date in the format of yyyy-mm-dd | No |  |
| enddate     | End date in the format of yyyy-mm-dd | YES |  |
| callback | function name to use for a JSONP request | Yes |  |

## Result data ##

A successful request will produces a [JSON](http://en.wikipedia.org/wiki/JSON) encoded object that contains metadata about the performances.

The fields of the object are as follows:

| **Field Name** | **Purpose**|
|:---------------|:|
| event | the name of the event |
| eventUrl | the persistent link for the event in AusStage |
| question | the text of the question asked of the audience |
| tag | the tag is being used for this performance |
| organisation | the name of the organisation holding the event |
| organisationUrl | the persistent link for the organisation in AusStage |
| venue | the name of the venue where the event is being held |
| venueUrl | the persistent link for the venue in AusStage |
| date | the date that the event is occurring in the "Day DD Month, YYYY" format |
| totalFeedbackCount | How many pieces of feedback for this performance have been received  |

# Form Validation #

Many forms use the [jQuery Validation Plugin](http://docs.jquery.com/Plugins/validation) and the lookup API supports the validation of the following fields via the [remote validation method](http://docs.jquery.com/Plugins/Validation/Methods/remote#options):

  * performance id
  * question id