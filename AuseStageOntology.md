<h1> Summary of the Aus-e-Stage Ontology </h1>

The Aus-e-Stage ontology is used by the RdfExport application to prepare the  NavigatingNetworksDataset. It is used to expedite the construction of the dataset for the NetworkService and should not be considered to be a definitive ontology.

Work will be undertaken as part of the AusStage Phase 4 project on flattening the AusStage dataset and constructing appropriate ontologies. Once this work is complete the RdfExport and NetworkService should be updated to use these ontologies.

The Aus-e-Stage Ontology contains the following properties grouped by use




# Contributor #

As defined by a [foaf:Person](http://xmlns.com/foaf/spec/#term_Person) object.

## nationality ##

Used to represent the nationality of a contributor

## function ##

The function that a contributor has had when they have been associated with an event. For example:

  * Actor
  * Playwright
  * Director
  * Composer

There is the capability in RDF to turn this into a proper ontology and not store literals for function values. This is outside the scope of the Aus-e-Stage project.

## collaboratorCount ##

The number of contributors that a contributor has collaborated with

## hasCollaboration ##

Links a contributor to a [collaboration](AuseStageOntology#collaboration.md)

## otherNames ##

Lists other names from the contributors.other\_names field


# Collaborations #

## collaboration ##

Holds the details of a group of collaborations between two contributors

## collaborator ##

A property of [collaboration](AuseStageOntology#collaboration.md) and stores the ID of the contributors involved in this collaboration

## collaborationCount ##

A property of [collaboration](AuseStageOntology#collaboration.md) and stores the number of times the contributors involved in this collaboration have collaborated.

_Note:_ Must be typed as an Integer Literal

## collaborationFirstDate ##

The first date that this collaboration occurred

## collaborationLastDate ##

The last date that this collaboration occurred

## hasCollaboration ##

Links a contributor to a collaboration event.

# Events #

## functionAtEvent ##

The function a contributor took when participating in an event

## atEvent ##

Specify the event at which the role or function was undertaken

## undertookFunction ##

Specify the type of function that the contributor undertook

## roleAtEvent ##

The role an organisation had at an event

## undertookRole ##

The role that the organisation undertook

# RDF Dataset Metadata #

## rdfMetadata ##

An object to hold metadata elements about the RDF dataset

## tdbCreateDateTime ##

The Date and Time that the underlying TDB dataset was created