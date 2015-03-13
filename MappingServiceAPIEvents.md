<h1>Event API</h1>

Components of the MappingService are exposed as an [API](http://en.wikipedia.org/wiki/Application_programming_interface). The intention of the API is to make it easier for developers to customise their own front end interfaces and to split the work equitably amongst the software engineers working on the Aus-e-Stage project.

The Event API exposes methods that can be used to retrieve details of events that occurred at a venue for use in populating [infoWindows](http://code.google.com/apis/maps/documentation/javascript/reference.html#InfoWindow) on a map.



# Base URL #

http://beta.ausstage.edu.au/mapping2/events?

Combining the base URL with the parameters outlined below constructs a call to the API.

# Parameters #

| **Name** | **Possible Values** | **Optional** | **Default** |
|:---------|:--------------------|:-------------|:------------|
| task   | organisation, contributor, venue | No |  |
| id     | unique identifier use with the task | No |  |
| venue  | specify the unique identifier of the venue when undertaking an organisation or contributor lookup | yes |  |
| format | json | Yes | json |
| callback | function name to use for a JSONP request | Yes |  |

## task ##
The type of search task to undertake for example a search for events that occurred at a venue, or the events associated with an organisation that occurred at the specified venue.

### organisation ###

A list of events that occurred at a venue associated with the specified organisation

### contributor ###

A list of events that occurred at a venue associated with the specified contributor

### venue ###

A list of events that have occurred at the specified venue

## id ##

The unique identifier of for a record matching the type specified by the task parameter

## venue ##

The unique identifier of a venue when undertaking a lookup for events associated with an organisation or contributor

### format ###
The type of data format used to prepare the search results. At the time of writing only [JSON](http://en.wikipedia.org/wiki/JSON) is a supported format

## callback ##
The name of the callback function used when a request is made for JSONP encoded data