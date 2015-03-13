<h1>More information on the RdfExport Application</h1>

This page provides further detailed information on the RdfExport application. If you wish to know how to use the application please see the RdfExport page.

This page provides information on the following topics



# Third Party Libraries Used #

The following third party libraries are used by the application and the JAR files must be in the _lib_ directory. They are not distributed with the application to keep the archive file size to a minimum:

  * [TDB](http://openjena.org/TDB/) - Used to provide a persistent RDF based datastore
  * [GNU Trove](http://trove4j.sourceforge.net/) - Used for a more memory efficient collection of contributor IDs
  * [Oracle JDBC Driver](http://www.oracle.com/technology/software/tech/java/sqlj_jdbc/index.html) - Used to access the AusStage database

# RDF Ontologies Used #

See the NavigatingNetworksDataset page

# AusStage Unique Id Encoding Scheme #

To assist in the clarity of AusStage unique identifiers in the dataset the following encoding scheme is used:

  * A unique identifier is prefixed with _ausstage:_
  * If the unique identifier is for a contributor _c:_ is then added
  * If the unique identifier is for an event _e:_ is then added

The [AusStageURI class](http://code.google.com/p/aus-e-stage/source/browse/trunk/rdf-export/src/au/edu/ausstage/vocabularies/AusStageURI.java) in the au.edu.ausstage.vocabularies package provides convenience methods for encoding identifiers, and converting identifiers into the persistent URLs used by the AusStage website

The class also provides functionality that encodes a unique identifier for the collaboration objects in the NavigatingNetworksDataset the identifier takes the form:

ausstage:rel:id1-id2

Where _id1_ and _id2_ are contributor ids. The URI is resolved to a URL in the Navigating Networks site that can provide additional information about the collaboration relationship.

# Installing the Application #

To install the RdfExport application follow these steps:

  1. Ensure you have the latest version of Java installed
  1. Download the latest version of the RdfExport archive from the [Downloads area](http://code.google.com/p/aus-e-stage/downloads/list)
  1. Extract the contents of the archive into a new directory
  1. Ensure the JAR files from the [list of third party libraries](RdfExportMoreInfo#Third_Party_Libraries_Used.md) are copied to the lib directory
  1. Create a properties file by following the steps in the next section

# Configuring the Application #

The RdfExport application is configured using two mechanisms. The first is via a number of command line switches that are detailed on the RdfExport page. The second is via a properties file.

The properties file contains the following information

| **Property Key** | **Purpose** |
|:-----------------|:------------|
| db-connection-string | The connection string to use to connect to the Oracle database |
| tdb-datastore | The full path to the directory where the TDB datastore is located |

The properties file follows the standard [.properties](http://en.wikipedia.org/wiki/.properties) file format used by Java.

As an example the properties file may look like this:

```
# database related properties
db-connection-string = jdbc:oracle:thin:my_schema/myuser@localhost:1521:shakespeare
# full path to the TDB datastore
tdb-datastore = /full/path/to/tdb/datastore
```