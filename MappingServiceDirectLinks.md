<h1>Syntax for Direct Map Links</h1>

A direct link for a map is a link that when used by a user automatically constructs a map and populates it with data. There are two different types of direct links:

  1. Simple Map Links - where only one record type and unique identifier are used
  1. Complex Map Links - where a total of up 100 unique identifiers for all of the record types can be used

The simple syntax is used by the [Map Bookmarklet](http://beta.ausstage.edu.au/?tab=extras&section=bookmarklet) to provide an easy way to navigate from a record in AusStage to the Mapping Service.

The complex syntax us used to provide the map bookmark functionality.



# Base URL #

http://beta.ausstage.edu.au/mapping2/?

Combining the base URL with the parameters outlined below constructs a call to the API.

# Simple Link Parameters #

| **Name** | **Possible Values** | **Optional** |
|:---------|:--------------------|:-------------|
| simple-map | true | No |
| type | organisation, contributor, venue, event | No |
| id | unique identifier of the record specified in the type parameter | No |

## simple-map ##
Specify that this is a request for a simple map

## type ##
Specify that this is a request for a simple map that will be populated by marker data for one of the four record types

## id ##
The unique identifier of the parent record that will be used to source the marker data

# Complex Link Parameters #

| **Name** | **Possible Values** | **Optional** |
|:---------|:--------------------|:-------------|
| complex-map | true | No |
| c | a list of contributor record identifiers | Yes |
| o | a list of organisation record identifiers | Yes |
| v | a list of venue record identifiers | Yes |
| e | a list of event record identifiers | Yes |

_Note:_ the list of identifiers must use a - character to separate the items in the list

## complex-map ##
Specify that this is a request for a complex map

## c ##
Specify a list of contributor record identifiers with each item separated by a - character

## o ##
Specify a list of organisation record identifiers with each item separated by a - character

## v ##
Specify a list of venue record identifiers with each item separated by a - character

## e ##
Specify a list of event record identifiers with each item separated by a - character