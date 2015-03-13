<h1>Navigating Networks Service Component Overview</h1>

The [Navigating Networks Service](NetworkService.md) is made up of a number of different components that combined provide the functionality required by the [Navigating Networks Specification](NavigatingNetworksSpecification.md).

The purpose of this Wiki page is to provide an overview of the components and the functionality that they provide.

The structure of the NetworkService is loosely based on the [MVC](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller) approach to system design. When viewed from this perspective the Java Server Pages (in green) are the views, the various classes (in dark blue) are controllers, and the Java Servlets (in turquoise) are the controllers.

# Data used by the Network Service #

Data that is used by the NetworkService is sourced from two locations.

The primary source of data is the RDF based datastore (in brown). This datastore is constructed by the RdfExport application and represents an export of data from the AusStage database.

The aim of the RDF based datastore is to represent all of the data that is required to build visualisations of collaboration networks. Where necessary small amounts of data may be sourced directly from the database. This has not proven necessary as yet, but the capability is available.

# Third Party Libraries #

The NetworkService uses a number of third party libraries that can be divided into two main categories. Java based libraries are used by the Java Server Pages and Java Servlets, and JavaScript libraries which provide functionality used in the display of data and interaction with the user in the web browser.

## Java Libraries ##

### Open Source Libraries ###

  * [Jena](http://openjena.org) - the basis for all RDF processing
  * [TDB](http://openjena.org/wiki/TDB) - persistent datastore of RDF data
  * [ARQ](http://jena.sourceforge.net/ARQ/) - SPARQL query engine
  * [Joseki](http://www.joseki.org/) - SPARQL Server

### Proprietary Libraries ###

  * [Oracle JDBC Drivers](http://www.oracle.com/technology/software/tech/java/sqlj_jdbc/index.html) - access the AusStage database

## JavaScript Libraries ##

# Component Overview Diagram #
![![](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/networks-service-component-overview-small.jpeg)](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/networks-service-component-overview.jpeg)

[Click this link](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/networks-service-component-overview.jpeg)

(Click image for larger version)