<h1>Creating the Navigating Networks Dataset</h1>

The creation of the dataset for use by the Navigating Networks service is achieved using the RdfExport application. This application, written in Java, creates an RDF based datastore that contains all data necessary to create network diagrams.

More information about the application such as the tools and ontologies used, is available on the RdfExportMoreInfo page.

_Note:_ It is strongly advised to only run this application on a 64bit operating system.

The application undertakes the following tasks:



# Building the Dataset #

The current dataset contains the elements listed on the NavigatingNetworksDataset page.

To build the dataset follow these steps:

  1. Ensure the application is installed by [following these directions](RdfExportMoreInfo#Installing_the_Application.md)
  1. Ensure the application is configured by [following these directions](RdfExportMoreInfo#Configuring_the_Application.md)
  1. Change to the _jar_ directory created when the application was installed
  1. Run the application using the following command where
    1. properties-file: is the location of the properties file created in step 2
> > `java -Xmx2048m -Xms512m -jar AusStageRdfExport.jar -task build-network-data -properties properties-file`
  1. The application will take some time to complete

# Exporting the Dataset #

The dataset can be exported into a variety of different serialised data formats and they are:

  * RDF/XML - http://en.wikipedia.org/wiki/RDF/XML
  * N-TRIPLE - http://en.wikipedia.org/wiki/N-Triples
  * TURTLE - http://en.wikipedia.org/wiki/Turtle_(syntax)
  * N3 - http://en.wikipedia.org/wiki/Notation3

To export the entire dataset follow these steps:

  1. Ensure the application is installed by [following these directions](RdfExportMoreInfo#Installing_the_Application.md)
  1. Ensure the application is configured by [following these directions](RdfExportMoreInfo#Configuring_the_Application.md)
  1. Ensure the dataset has been created by following the [Building the Dataset](RdfExport#Building_the_Dataset.md) steps
  1. Change to the _jar_ directory created when the application was installed
  1. Run the application using the following command where
    1. properties-file: is the location of the properties file created in step 2
    1. format-type: is one of the listed formats above
    1. output-file: is the name of the output file to create (including path information)
> > `java -Xmx1024m -Xms512m -jar AusStageRdfExport.jar -task export-network-data -properties properties-file -format format-type -output output-file`
  1. If no format is specified the default format 'RDF/XML' will be used
  1. The application will take a short time to complete and the output will look like this:

# Querying the Dataset #

Querying the dataset is now supported via the [SPARQL Endpoint](NavigatingNetworksSparqlEndpoint.md)

# Exporting the Dataset as an Edge List File #

Exporting the dataset as an Edge List File is now supported via the [Navigating Networks Service API](NavigatingNetworksDatasetAPIexportEdges.md)