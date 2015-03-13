<h1>Marker Data</h1>

Components of the MappingService are exposed as an [API](http://en.wikipedia.org/wiki/Application_programming_interface). The intention of the API is to make it easier for developers to customise their own front end interfaces and to split the work equitably amongst the software engineers working on the Aus-e-Stage project.

The Marker Data API exposes methods and data elements that can be used to construct a map on a web page.



# Base URL #

http://beta.ausstage.edu.au/mapping2/markers?

Combining the base URL with the parameters outlined below constructs a call to the API.

# Parameters #

| **Name**   | **Possible Values** | **Optional** | **Default** |
|:-----------|:--------------------|:-------------|:------------|
| type     | state, suburb, venue, organisation, contributor, event | No |  |
| id       | Unique contributor or organisation id | No |  |
| callback | function name to use for a JSONP request | Yes |  |

## type ##

The type parameter specifies whether the call to the API is for data on venues at the state, suburb or specific venue. As well as data on venues associated with an organisation or a contributor

### id for states ###

Valid State Identifiers are:

| **id** | **State / Region** |
|:-------|:-------------------|
| 1 | SA |
| 2 | WA |
| 3 | NSW |
| 4 | QLD |
| 5 | TAS |
| 6 | VIC |
| 7 | ACT |
| 8 | NT |
| 99 | Australia |
| 999 | Outside Australia |

### id for suburbs ###

If the task type is set to suburb the parameter is is expected to be encoded as follows:

state-id\_suburb-name

For example:

1\_Adelaide

### id for contributors, organisations, venues and events ###

For all other task types the id parameter is a comma separated list of unique identifiers.

_Note: It is not possible to mix types of ids in a single call to the API. Therefore if the type is set to venue only venue ids must be specified, conversely if the type is set to contributor only contributor ids must be specified._

## callback ##
The name of the callback function used when a request is made for JSONP encoded data