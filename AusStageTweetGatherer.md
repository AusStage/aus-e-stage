<h1>AusStage Tweet Gatherer</h1>


---

_NOTE:_ This procedure document is deprecated due to a change in direction for the collection of feedback for the MobileService

---


The AusStage Tweet Gatherer Application is designed to collect messages from [Twitter](http://twitter.com) that are sent by audience participants either during, or immediately after a performance. The messages will be aggregated, and made part of the dataset used by the [Mobile Service](MobileService.md).

The AusStage Tweet Gatherer application replaces the [Twitter Gatherer](TwitterGatherer.md) application. This new application supports the [OAuth](http://dev.twitter.com/pages/oauth_faq) protocol via an updated version of the [tweetStream4J](http://github.com/seejohnrun/tweetStream4J) library which uses the [Twitter Streaming API](http://dev.twitter.com/pages/streaming_api).



# Identifying Messages #

The AusStage Tweet Gatherer identifies messages intended as feedback on a performance in one of two ways:

  1. Using company specific identifiers
    * Determining the date and time that the message was sent
    * Identifying performances that are occurring at that time or events that ended within two hours of the message being sent
    * Identifying the company mentioned in the message using a [hash tag](http://en.wikipedia.org/wiki/Hash_tag#Hash_tags)
    * Matching the company specific hash tag to that listed with a performance
  1. Using performance specific identifiers
    * Identifying the performance mentioned in the message using a [hash tag](http://en.wikipedia.org/wiki/Hash_tag#Hash_tags)
    * Matching the company specific hash tag to that listed with a performance

If a performance can not be identified the message is stored and an exception report is generated. This report is sent via email to AusStage personnel for manual action.

# De-Identifying Information #

It is a requirement of the project that all messages be de-identified. In this way it is possible to identify messages that have been sent by the same Twitter user, however it is not possible to identify specific individuals.

In order to de-identify messages the following fields in a Twitter Message are removed:

| **Field Name**      | **Description** |
|:--------------------|:----------------|
| url               | the URL associated with the twitter users account, for example their website |
| profile\_image\_url | the URL to the twitter users profile image |
| screen\_name       | the twitter users user name |
| profile\_background\_image\_url | the url to the image used in the background of the users profile page |
| name              | the users name |
| description       | the description associated with the user |

The following fields are obfuscated by means of a [cryptographic hash](http://en.wikipedia.org/wiki/SHA-2)

| **Field Name** | **Description** |
|:---------------|:----------------|
| id           | the unique identifier for the message |
| user\_id      | the unique identifier for the user |
| in\_reply\_to\_user\_id | if this message is in reply to another user that users id is stored in this field |
| in\_reply\_to\_status\_id | if the message is in reply to another message the id of the original message is stored in this field |
| retweeted\_status | if the message is a retweet the id of the original message is stored in this field |

In this way relationships between messages, and the grouping of messages by user can be achieved without identifying any individual

# Libraries Used in Development #
  * [tweetStream4J](http://github.com/seejohnrun/tweetStream4J)
    * Used to connect to the Twitter Stream API
    * Information [on compiling](http://techxplorer.com/2010/10/13/exploring-tweetstream4j-2/) the tweetStream4J package
  * [Joda-Time](http://joda-time.sourceforge.net/)
    * Used for parsing, comparing and formatting date and time values
  * [twitter-text-java](http://github.com/mzsanford/twitter-text-java)
    * Used to extract the hash tags from the twitter messages
    * Information [on compiling](http://techxplorer.com/2010/08/16/exploring-twitter-text-java/) the twitter-text-java package

# Documentation #

Details of the classes and methods available is contained in the standard Javadoc files available via the [source repository](http://aus-e-stage.googlecode.com/svn/trunk/tweet-gatherer/docs/api/index.html).

An overview of the way the application works is [available here](http://techxplorer.com/2010/09/09/integrating-ausstage-with-twitter/).

A diagram providing an overview of the application workflow is [available here](http://aus-e-stage.googlecode.com/svn-history/trunk/wiki-assets/tweet-gatherer-workflow.png). .