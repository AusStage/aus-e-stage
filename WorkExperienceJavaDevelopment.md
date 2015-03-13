<h1> Java Development Specification </h1>

This document outlines a specification for the [Java](http://www.java.com/en/) based development to be undertaken as part of a work experience opportunity in 2011. The classes, once developed, will be included as part of the [AusStage Utilities Package](UtilsPackage.md).



# General Development Guidelines #

The classes and other code produced as part of this specification must:

  1. Method definitions must include appropriate comments such as those outlined in the definition sections of this specification
  1. Parameters **must** be validated using methods in the [InputUtils class](http://aus-e-stage.googlecode.com/svn/trunk/utils-lib/docs/api/au/edu/ausstage/utils/InputUtils.html) such as the [isValid(java.lang.String)](http://aus-e-stage.googlecode.com/svn/trunk/utils-lib/docs/api/au/edu/ausstage/utils/InputUtils.html#isValid(java.lang.String)) method
  1. Other classes in the [AusStage Utilities Package](UtilsPackage.md) that **must** be used include:
    1. [CommandLineParser](http://aus-e-stage.googlecode.com/svn/trunk/utils-lib/docs/api/au/edu/ausstage/utils/CommandLineParser.html) for managing parameters
    1. [FileUtils](http://aus-e-stage.googlecode.com/svn/trunk/utils-lib/docs/api/au/edu/ausstage/utils/FileUtils.html) for validating paths, and writing files
  1. XML generated by the classes **must** prove to be [well-formed](http://www.w3.org/TR/REC-xml/#sec-well-formed) and [valid](http://en.wikipedia.org/wiki/XML#Schemas_and_validation).
  1. XML **must** be generated using the classes in the [org.w3c.dom](http://download.oracle.com/javase/6/docs/api/org/w3c/dom/package-summary.html) package
  1. XML **must** be formatted for readability using a [technique like this](http://techxplorer.com/2010/05/20/indenting-xml-output-in-java/)
  1. All code produced will be released under the terms of version 3 of the [GNU General Public License](http://www.gnu.org/licenses/gpl.html) and as such care should be taken in copying code or methodologies

# AusStage CSS Processing Classes #

The first task is the development of a class that can be used to process the [AusStage Colour Scheme](AusStageColourScheme.md) CSS into a variety of different formats.

## CSS Processing Class ##

A class is required to process a base CSS into three different outputs. The class must have the following three public methods:

  1. getForegroundCSS
  1. getBackgroundCSS
  1. getKMLStyles

### Class definition ###

The definitions for the three required methods are as follows:

```
/**
 * A method to process the base CSS and produce the foregound CSS
 *
 * @param baseCSS the base CSS that will be processed
 *
 * @return        the processed CSS
 */
public String getForegroundCSS(String baseCSS) {

}
```

```
/**
 * A method to process the base CSS and produce the background CSS
 *
 * @param baseCSS the base CSS that will be processed
 *
 * @return        the processed CSS
 */
public String getBackgroundCSS(String baseCSS) {

}
```

```
/**
 * A method to process the base CSS and produce the KML styles
 *
 * @param baseCSS the base CSS that will be processed
 *
 * @return        the KML styles
 */
public String getBackgroundCSS(String baseCSS) {

}
```

### Sample Input and Output ###

#### Base CSS ####

```
.1 {

	color: #4eb8ad;

}



.2 {

	color: #004d35;

}



.3 {

	color: #66c547;

}



.4 {

	color: #ffc11f;

}
```

#### Foreground CSS ####

```
.f-1 {
  color: #4eb8ad;
}

.f-2 {
  color: #004d35;
}

.f-3 {
  color: #66c547;
}

.f-4 {
  color: #ffc11f;
}
```

#### Background CSS ####

```
.b-1 {
  background-color: #4eb8ad;
}

.b-2 {
  background-color: #004d35;
}

.b-3 {
  background-color: #66c547;
}

.b-4 {
  background-color: #ffc11f;
}
```

#### KML Style ####

```

```

## CSS Test Class ##

A simple command line driven application is required to test the [CSS Processing Class](WorkExperienceJavaDevelopment#CSS_Processing_Class.md).

This application must take as parameters:

  1. task - A parameter that defines the task to be undertaken
  1. input - The path to a file that contains the base CSS
  1. output - The path to a file to be used as output

Valid task parameters are:

  1. foreground - create the foreground CSS
  1. background - create the background CSS
  1. kml-style  - create the KML styles

The application must use the [CSS Processing Class](WorkExperienceJavaDevelopment#CSS_Processing_Class.md) to process the CSS found in the file specified at the input path and write the new CSS, or KML Styles, into the output file as specified by the task parameter

All parameters must be validated using the classes and methods outlined in the [General Development Guidelines](WorkExperienceJavaDevelopment#General_Development_Guidelines.md).

An example of the way the application should be structured is available in the [WebsiteAnalytics class](http://aus-e-stage.googlecode.com/svn/trunk/website-analytics/src/au/edu/ausstage/websiteanalytics/WebsiteAnalytics.java)

# GraphML Manager Classes #

The [Navigating Networks Service](NetworkService.md) is required to export data in a variety of different formats. One of the main formats that will be used is [GraphML](http://graphml.graphdrawing.org/). !GraphML is a XML based format use for representing networks.

## GraphML Manager Class ##

The class **must** provide methods that will:

  1. Create a !GraphML document
  1. Add attribute definitions to the document
  1. Add nodes and edges to the document
  1. Add attributes to nodes and edges

Example methods include, but are not limited to, the following:

```
/**
 * A constant that defines a directed graph
 */
public final int DIRECTED_GRAPH = 1;

/**
 * A constant that defines an undirected graph
 */
public final int UNDIRECTED_GRAPH = 2;

/**
 * A constant that defines the boolean attribute type
 */
public final int ATTRIBUTE_TYPE_BOOLEAN = 1;

/**
 * A constant that defines the int attribute type
 */
public final int ATTRIBUTE_TYPE_INT = 2;

// other attributes would also need to be defined 

/**
 * A method to construct a new GraphML document
 *
 * @param id the unique identifier for the graph
 * @param type the type of graph either directed or undirected
 *
 */
public void createDocument(String id, int type) {

}

/**
 * A method to add a node to the GraphML document
 *
 * @param name the name of the node
 * @param id   the unique identifier of the node
 *
 * @return     a reference to the newly created element
 */
public Element addNode(String name, String id) {

}

/**
 * A method to add an edge to the GraphML document
 *
 * @param name   the name of the edge
 * @param id     the unique identifier for the edge
 * @param source the source node for the edge
 * @param target the target node for the edge
 *
 * @return       a reference to the newly created element
 */
public Element addEdge(String name, String id, String source, String target) {

} 

/**
 * A convenience method to declare a GraphML attribute without a default value
 *
 * @param id     the unique identifier of the attribute
 * @param domain the domain of the attribute
 * @param name   the name of the attribute
 * @param type   the type of the attribute
 *
 * @return a reference to the newly created element
 */
public Element addAttribute(String id, String domain, String name, String type) {

}

/**
 * A method to declare a GraphML attribute
 *
 * @param id      the unique identifier of the attribute
 * @param domain  the domain of the attribute
 * @param name    the name of the attribute
 * @param type    the type of the attribute
 * @param default the default value for this attribute
 *
 * @return a reference to the newly created element
 */
public Element addAttribute(String id, String domain, String name, String type, String value) {

}

/**
 * A method to add an attribute to a node
 *
 * @param node  the Element representing this node
 * @param id    the id of the attribute to add
 * @param value the value of the attribute 
 */
public void addAttributeToNode(Element node, String id, String value) {

}

```

## GraphML Manager Test Class ##

A simple command line driven application is required to test the [GraphML Manager](WorkExperienceJavaDevelopment#GraphML_Manager_Class.md) class.

This application will use the WorkExperienceJavaDevelopment#GraphML\_Manager\_Class GraphML Manager] class to create a simple GrahML file, including adding nodes edges and attributes and write the XML out to a file.