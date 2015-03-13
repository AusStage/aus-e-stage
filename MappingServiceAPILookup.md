<h1>Lookup API</h1>

Components of the MappingService are exposed as an [API](http://en.wikipedia.org/wiki/Application_programming_interface). The intention of the API is to make it easier for developers to customise their own front end interfaces and to split the work equitably amongst the software engineers working on the Aus-e-Stage project.

The Lookup API exposes methods that can be used to lookup a variety of data elements.



# Base URL #

http://beta.ausstage.edu.au/mapping2/lookup?

Combining the base URL with the parameters outlined below constructs a call to the API.

# Parameters #

| **Name** | **Possible Values** | **Optional** | **Default** |
|:---------|:--------------------|:-------------|:------------|
| task   | state-list, suburb-list, suburb-venue-list, organisation, contributor, venue, map-colour-list | No |  |
| id     | unique identifier use with the lookup task | No |  |
| format | json | Yes | json |
| callback | function name to use for a JSONP request | Yes |  |

_Note:_ id is not required for a state-list or map-colour-list lookup task

## task ##
The type of lookup task to undertake for example a lookup of organisation or contributor data

### state-list ###

A list of objects that can be used to construct UI elements that allow the user to select a state or other geographic area in which to narrow their browse activity

### suburb-list ###

A list of objects that can be used to construct UI elements that allow the user to select one of the suburbs that are located in the state or country as identified by the id parameter.

### suburb-venue-list ###

A list of venues that are located in the state and suburb combination as identified by the id parameter

### organisation ###

The details of an organisation

### contributor ###

The details for a contributor

### venue ###

The details for a venue

## id ##

The unique identifier used to lookup the data as identified by the task parameter

### State identifiers ###
Valid State Identifiers are:

| **id** | **State** |
|:-------|:----------|
| 1 | SA |
| 2 | WA |
| 3 | NSW |
| 4 | QLD |
| 5 | TAS |
| 6 | VIC |
| 7 | ACT |
| 8 | NT |

### Suburb identifiers ###

The id parameter is expected to be encoded as follows:

state-id\_suburb-name

For example:

1\_Adelaide

## format ##
The type of data format used to prepare the search results. At the time of writing only [JSON](http://en.wikipedia.org/wiki/JSON) is a supported format

## callback ##
The name of the callback function used when a request is made for JSONP encoded data