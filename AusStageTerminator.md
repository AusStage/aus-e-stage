# AusStage Terminator Service #

The Terminator Service is used internally by the AusStage team to run scripts on the production AusStage server.

The most notable scripts are those that start and stop the AusStage website.

The service is designed to be extensible and will allow the running of any script on the server that it is configured to use. Access to the service is via an authentication token to ensure only authorised team members can access it.