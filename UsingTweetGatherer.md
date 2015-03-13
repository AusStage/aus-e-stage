<h1> Gathering Twitter Messages </h1>

This procedure document outlines how to use the [AusStage Tweet Gatherer](AusStageTweetGatherer.md) application to gather messages from [Twitter](http://twitter.com).



# 0. Procedure Requirements #

_Before attempting this procedure please ensure that the following requirements are met_

  1. The computer that you intend on running the software on has access to the AusStage database either via the Flinders University Network or via the [CSEM Virtual Private Network (VPN)](https://vpn.csem.flinders.edu.au/)
  1. The latest version of [Java](http://java.com/en/) is installed on your system. At the time of writing the latest version was 1.6.x
  1. You have downloaded the latest version of the [AusStage Tweet Gatherer](AusStageTweetGatherer.md) application from the [Downloads](http://code.google.com/p/aus-e-stage/downloads/list) area
  1. You have experience in using applications via the [Command Line Interface](http://en.wikipedia.org/wiki/Command-line_interface)

# 1. Prepare the Application #

  1. Extract the contents of the zip file downloaded from the [Downloads](http://code.google.com/p/aus-e-stage/downloads/list) area
  1. Ensure that the following directories have been created
    * `tweet-gatherer` - The parent directory
      * `bin` - a sub directory containing the main application files
      * `lib` - a sub directory containing many of the required libraries
  1. Create a sub directory called `logs` under the parent `tweet-gatherer` directory
  1. Download the `ojdbc6.jar` file from the [Oracle JDBC Drivers](http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-112010-090769.html) page
  1. Copy the `ojdbc6.jar` file into the lib directory

# 2. Create a properties file #

  1. Create an empty text file in the parent directory, as defined in step 1 above
  1. Name the file `default.properties`
  1. Add the following lines to the text file, replacing the text in the { } brackets with the appropriate values
```
# Properties file for the TweetGatherer Application
# Any line starting with a '#' is a comment and is ignored

# Twitter OAuth properties
consumer-key = {Tweet Gatherer Consumer Key}
consumer-secret = {Tweet Gatherer Secret Key}
oauth-token = {OAuth Token}
oauth-secret = {OAuth Secret}

# database connection string
db-connection-string = {Connection string for the AusStage Production Datatabase}

# log file directory
log-dir = {full path to the log directory created in step 1.3}

# send via Gmail
mail-host = {Outgoing SMTP email server host name}
mail-user = {Outgoing SMTP user name}
mail-password = {Outgoing SMTP password}
mail-ssl = {yes / no flag to use SSL for sending email}
mail-tls = {yes / no flag to use TLS for sending email}
mail-port = {Outgoing SMTP Server Port}
mail-from-address = {Address to send email from}
mail-to-address = {Address(s) to send email to separated by commas}
```
  1. If you are unsure about any values for the above options, contact the AusStage team
  1. Save the file, ensure it is saved as plain text

# 3. Start the Tweet Gatherer Application #

  1. Open a new terminal window
  1. Navigate to the parent  directory, as defined in step 1 above
  1. Navigate to the `bin` sub directory
  1. Start the application using the following command <br />`java -jar TweetGatherer.jar -properties ../default.properties`
  1. The output from the application should be as follows _Note:_ The list of tags and version number may be different
```
AusStage Tweet Gatherer - Gather Performance Feedback from Twitter
Version: 1.0.0 Build Date: 2010-10-18
More Info: http://code.google.com/p/aus-e-stage/wiki/TweetGatherer

INFO: Application Started: Monday, 18 October 2010 3:10:49 PM CST
INFO: Connecting to the database
INFO: Connection established
INFO: Tracking the following hash tags
      #adt
```
  1. If an error occurs, check for a possible resolution in step 6
  1. If no error occurs, continue with step 4

# 4. Monitor the Collection of Messages #

  1. Monitor the output from the application to ensure that messages are being recorded successfully
  1. If an error occurs, check the email address for the exception report to manually process the message

# 5. Saving the Output From the Application #

Undertake the following tasks once all feedback has been gathered, for example the day after a performance

  1. Create an archive of the log directory, such a zip file
  1. Email this file to the AusStage team

# 6. Common Errors #

## Unable to Connect to the Database ##

If the TwitterGatherer Application is unable to connect to the database:

  1. Check that you can connect to the database using an application such as [Oracle SQL Developer](http://www.oracle.com/technetwork/developer-tools/sql-developer/index.html)
  1. Confirm that you are using a computer that can access the Database Server such as those in the drama department or via the [CSEM Virtual Private Network (VPN)](https://vpn.csem.flinders.edu.au/)
  1. Confirm that you are using the correct database connection details

## Unable to Connect to Twitter ##

If the application is unable to connect to Twitter:

  1. Confirm that you are using the right authentication details in the properties file
  1. Confirm that Twitter is up and running by visiting the [Twitter](http://twitter.com) homepage
  1. Ensure that no issue are listed on the [Twitter Status](http://status.twitter.com/) page
  1. Ensure that you can connect to the Internet via the VPN, if not reconfigure the VPN client to allow outgoing connections to the Internet as well as connections through the VPN

# 7. Managing Exception Reports #

If an exception report is generated use the following steps as a guide to determine the appropriate course of action:

  1. Manually match the feedback to a performance using the hash tag and your own judgement about the date and time that it was sent by matching the hash tag to a company hash tag and matching the date and time with an entry in the `mob_performance` table
  1. Manually add the feedback to the AusStage system using the Manual Feedback Entry page

# 8. Further Information #
## Twitter and OAuth Authentication ##
The Tweet Gatherer application authenticates to Twitter using [OAuth](http://oauth.net/). Conceptually, for the purposes of this discussion, this means that the Tweet Gatherer application does not authenticate to Twitter using a username and password. Rather it uses four security tokens which are called:

  * consumer-key
  * consumer-secret
  * oauth-token
  * oauth-secret

These tokens are kept by the AusStage project manager. If however they are no longer available follow the procedure below to generate new tokens.

### Generating New Consumer Tokens ###

  1. Login to http://twitter.com using the @ausstage account
  1. Click on the _Profile_ link
  1. Click on the _Edit your profile_ link
  1. Click on the _Connections_ link
  1. Click on the _Revoke Access_ link under the _AusStage Tweet Gatherer_ entry in the _Applications_ list
  1. Enter the following URL in the address bar of your browser:<br />http://twitter.com/apps
  1. Click on the heading for the _AusStage Tweet Gatherer_ entry
  1. Click the _Reset Consumer Key/Secret button
  1. Make a note of the new_Consumer Key_and_Consumer Secret_1. Store these values in the `default.properties` file as the_consumer-key_and_consumer-secret_entries respectively_

### Generating new OAuth tokens ###

  1. # Open a new terminal window
  1. Navigate to the parent  directory, as defined in step 1 above
  1. Navigate to the `bin` sub directory
  1. Start the application using the following command <br />`java -jar TweetGatherer.jar -properties ../default.properties -getauthtoken=yes`
  1. Copy the URL provided by the application into the address bar of your same browser you used to access Twitter
  1. Make a note of the PIN displayed on the page
  1. Enter the PIN into the terminal window
  1. Make a note of the new _oauth-token_ and _oauth-secret_ values
  1. Store these values in the `default.properties` file as the oauth-token_and_oauth-secret_values entries respectively_
