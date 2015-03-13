# About the SPARQL Endpoint #

The NetworkService uses an [RDF](http://en.wikipedia.org/wiki/Resource_Description_Framework) based dataset that is a subset of the main AusStage dataset. This dataset is constructed using the RdfExport application.

To enhance access to the dataset a [SPARQL](http://en.wikipedia.org/wiki/SPARQL) endpoint is currently undergoing development, testing and evaluation.

The endpoint is available at the following URL:

http://beta.ausstage.edu.au/networks/sparql

A simple web based form that can be used to send simple SPARQL queries to the endpoint is also available here:

http://beta.ausstage.edu.au/networks/sparql.jsp

_Note:_ This service is not intended as a way to export large amounts of data from the dataset. Export files updated periodically are [available here](http://code.google.com/p/aus-e-stage/downloads/list).

The SPARQL endpoint is powered by the [Joseki SPARQL Server](http://www.joseki.org/) that is accessing a [TDB](http://openjena.org/wiki/TDB) based datastore. Both of these are components of the [Jena Semantic Web Framework](http://openjena.org/) for Java.