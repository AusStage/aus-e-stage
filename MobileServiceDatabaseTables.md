<h1>Mobile Service Database Tables</h1>

The following is a list of tables added to the AusStage relational database to support the MobileService.

_Please Note:_

  1. Tables will be listed here only **after** they have been added to the relational database
  1. The table specification may differ from that found in the MobileServiceSpecification in response to issues discovered during implementation
  1. The name of the datatypes match those [implemented by Oracle](http://download.oracle.com/docs/cd/B19306_01/server.102/b14220/datatype.htm) which may differ from other database platforms.
  1. Importantly _no datatype_ is Oracle specific
  1. All new tables that are created specifically for the MobileService have the prefix `mob_`



# Performances #

Details of the individual performances that have sought feedback using the MobileService

## Table Name ##

`mob_performances`

## Columns ##

| **Name** | **Description** | **Datatype** | **Key / Indexed** |
|:---------|:----------------|:-------------|:------------------|
| performance\_id | Used as a primary key for the table | number | Primary |
| event\_id | link this performance to the existing AusStage event table | number | FK (events) |
| start\_date\_time | the start of the period during which valid feedback can be received | date |  |
| end\_date\_time | the end of the period during which valid feedback can be received | date |  |
| question\_id | the id of the question asked at this performance used to solicit feedback | number | FK (mob\_questions) |
| hash\_tag | the unique hash tag for this performance | varchar(40) | Unique |
| deprecated\_hash\_tag | a field to indicate that this hash tag has been deprecated | varchar(1) - default 'N' |  |

## Notes ##

  1. For simplicity during initial development each performance can only have on question associated with it. This can be expanded to multiple questions using an intermediary table that linked a performance to multiple questions
  1. The `events` table already exists in the AusStage database
  1. Date and time fields are entered and stored using [Adelaide Local Time](http://en.wikipedia.org/wiki/Adelaide)
  1. The Twitter API imposes a limit on the number of keywords (hashtags) that can be used with the streaming API. As performance specific identifiers do not have a date / time restriction it is necessary to have an alternate method of identifying hashtags that are no longer being used. This is why the deprecated\_hash\_tag field is used. If set to 'Y' the hash tag is considered to be deprecated and will not be used when connecting to the streaming API.

# Organisations #

This table stores information about organisations using the MobileService

## Table Name ##

`mob_organisations`

## Columns ##

| **Name** | **Description** | **Datatype** | **Key / Indexed** |
|:---------|:----------------|:-------------|:------------------|
| organisation\_id | Unique identifier of the organisation in AusStage | number | FK (organisionid in the organisation table) |
| twitter\_hash\_tag | Specify the organisation specific hash tag | varchar(64) | Yes (No duplicates) |
| mobile\_phone\_hash | Specify hashes of phones of organisation employees for filtering | varchar(64) |  |

## Notes ##

  1. Organisation specific hashtags are used to identify twitter messages that are of interest
  1. Mobile phone hashes are stored in order to filter out feedback that is sent from people associated with the organisation, for example employees testing the service

# Questions #

This table stores details of the questions that have been used to solicit feedback

## Table Name ##

`mob_questions`

## Columns ##

| **Name** | **Description** | **Datatype** | **Key / Indexed** |
|:---------|:----------------|:-------------|:------------------|
| question\_id | Unique identifier for questions | number | Primary |
| question | Text of the question | varchar(512) |  |
| question\_notes | Notes about the question used internally by AusStage | varchar(4000) |  |

# Feedback #

This is a table used to store feedback sourced from Twitter, using the TwitterGatherer application as well as SMS messages. The table is designed to be extensible to support additional columns / content types as required.

## Table Name ##

`mob_feedback`

## Columns ##

| **Name** | **Description** | **Datatype** | **Key / Indexed** |
|:---------|:----------------|:-------------|:------------------|
| feedback\_id | Used as a primary key for the table | number | Primary |
| performance\_id | Link this feedback to a performance | number | FK (mob\_performance table) |
| question\_id | Link this feedback to a specific question | number | FK (mob\_questions) |
| source\_type | Identify the source of feedback | number | FK (mob\_source\_types) |
| received\_date\_time | The date and time that the feedback was sent | date |  |
| received\_from | A cryptographic hash of the unique identifier of the user as defined by the source type | varchar(64) | yes |
| source\_id | A cryptographic hash of the unique identifer for the piece of feedback in the source system | varchar(64) | yes |
| short\_content | The content of the feedback | varchar(4000) |  |
| long\_content  | The content of the feedback when longer than 4000 characters | clob |  |
| content\_uri   | A URI for content stored outside the system | varchar(4000) |  |
| public\_display | Indicates if the feedback should be publicly displayed | char(1) | yes |

## Notes ##

  1. The [SHA-2 algorithm](http://en.wikipedia.org/wiki/Sha256), specifically sha256, is used to compute cryptographic hashes
  1. Date and time fields are entered and stored using [Adelaide Local Time](http://en.wikipedia.org/wiki/Adelaide).
  1. The `public_display` field by default is set to 'Y'

# Source Types #

This table stores information about the various source types that are supported by the MobileService

## Table Name ##

`mob_source_types`

## Columns ##

| **Name** | **Description** | **Datatype** | **Key / Indexed** |
|:---------|:----------------|:-------------|:------------------|
| source\_type | Unique identifier for the various sources | number | Primary |
| source\_name | The name of the source | varchar(255) |  |
| source\_description | The description of the source | varchar(512) |  |

# Other Database Objects #

## Sequences ##

A sequence is an object in Oracle that is used to generate a number sequence. The following sequences are used to create a unique number to act as a primary key.

| **Table** | **Column** | **Sequence** |
|:----------|:-----------|:-------------|
| mob\_performances | performance\_id | mob\_performance\_id\_seq |
| mob\_questions | question\_id | mob\_question\_id\_seq |
| mob\_feedback | feedback\_id | mob\_feedback\_id\_seq |
| mob\_source\_types | type\_id | mob\_source\_type\_id\_seq |

# Database Diagram #

A copy of the database diagram is [available for download](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/mobile-impl-database-diagram.jpeg).

