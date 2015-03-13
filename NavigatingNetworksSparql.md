<h1>SPARQL and the Navigating Networks Service</h1>



# About SPARQL #

SPARQL Query Language for RDF is a language that can be used to query RDF data sources. The [Navigating Networks Service](NetworkService.md) uses an RDF based datastore that is created and populated by the RdfExport application.

These sample queries that can be used with the NavigatingNetworksSparqlEndpoint or executed using the [Querying the Dataset](RdfExport#Querying_the_Dataset.md) capability of the RdfExport app.

## Useful Links ##

Useful SPARQL Links include:

  * [SPARQL Query Language for RDF Specification](http://www.w3.org/TR/rdf-sparql-query/)
  * [Wikipedia page on SPARQL](http://en.wikipedia.org/wiki/SPARQL)
  * [Jena SPARQL Tutorial](http://jena.sourceforge.net/ARQ/Tutorial/) - Jena is the library used to manage this datastore

## Notes ##

It can be useful when developing queries, especially early in the development process, to use the `LIMIT` clause to limit the number of records returned. This clause is placed after the last `}` of the `WHERE` clause and looks like this:

`LIMIT 10`

Where the number, in this case 10, specifies the number of records to return.

# Sample Queries #

## List the Collaborators of a Contributor ##

```
PREFIX foaf:       <http://xmlns.com/foaf/0.1/>
PREFIX ausestage:  <http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#>
 
SELECT ?collaborator ?collabGivenName ?collabFamilyName
WHERE {  
       <REPLACE_ME>   a foaf:Person ; 
                      ausestage:hasCollaboration ?collaboration. 
       ?collaboration ausestage:collaborator ?collaborator.
       ?collaborator  foaf:givenName ?collabGivenName;
                      foaf:familyName ?collabFamilyName.
       FILTER (?collaborator != <REPLACE_ME>) 
}
```

_Note:_ Replace the `REPLACE_ME` with the unique identifier of a contributor encoded using the [AusStage Unique Id Encoding Scheme](http://code.google.com/p/aus-e-stage/wiki/RdfExportMoreInfo#AusStage_Unique_Id_Encoding_Scheme).

For example to find all of the collaborators for [Robyn Archer](http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_contrib_id=774) the query looks like this:

```
PREFIX foaf:       <http://xmlns.com/foaf/0.1/>
PREFIX ausestage:  <http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#>
 
SELECT ?collaborator ?collabGivenName ?collabFamilyName
WHERE {  
       <ausstage:c:774> a foaf:Person ; 
                      ausestage:hasCollaboration ?collaboration. 
       ?collaboration ausestage:collaborator ?collaborator.
       ?collaborator  foaf:givenName ?collabGivenName;
                      foaf:familyName ?collabFamilyName.
       FILTER (?collaborator != <ausstage:c:774>) 
}
```

## List the Key Collaborators of a Contributor ##

```
PREFIX foaf:       <http://xmlns.com/foaf/0.1/>
PREFIX ausestage:  <http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#>

SELECT ?collaborator ?collabGivenName ?collabFamilyName ?function ?firstDate ?lastDate ?collabCount 
WHERE {  
       <REPLACE_ME> a foaf:Person ; 
                      ausestage:hasCollaboration ?collaboration. 
       ?collaboration ausestage:collaborator ?collaborator; 
                      ausestage:collaborationFirstDate ?firstDate; 
                      ausestage:collaborationLastDate ?lastDate; 
                      ausestage:collaborationCount ?collabCount. 
       ?collaborator  foaf:givenName ?collabGivenName; 
                      foaf:familyName ?collabFamilyName; 
                      ausestage:function ?function. 
       FILTER (?collaborator != <REPLACE_ME>) 
}
```

_Note 1:_ Replace the `REPLACE_ME` with the unique identifier of a contributor encoded using the [AusStage Unique Id Encoding Scheme](http://code.google.com/p/aus-e-stage/wiki/RdfExportMoreInfo#AusStage_Unique_Id_Encoding_Scheme).

_Note 2:_ Due to current limitations in the dataset ordering results by collaboration count `?collabCount` must be done outside the SPARQL query

For example to find all of the key collaborators for [Robyn Archer](http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_contrib_id=774) the query looks like this:

```
PREFIX foaf:       <http://xmlns.com/foaf/0.1/>
PREFIX ausestage:  <http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#>

SELECT ?collaborator ?collabGivenName ?collabFamilyName ?function ?firstDate ?lastDate ?collabCount 
WHERE {  
       <ausstage:c:774> a foaf:Person ; 
                      ausestage:hasCollaboration ?collaboration. 
       ?collaboration ausestage:collaborator ?collaborator; 
                      ausestage:collaborationFirstDate ?firstDate; 
                      ausestage:collaborationLastDate ?lastDate; 
                      ausestage:collaborationCount ?collabCount. 
       ?collaborator  foaf:givenName ?collabGivenName; 
                      foaf:familyName ?collabFamilyName; 
                      ausestage:function ?function. 
       FILTER (?collaborator != <ausstage:c:774>) 
}
```

## List Information About A Contributor ##

```
PREFIX foaf:       <http://xmlns.com/foaf/0.1/> 
PREFIX ausestage:  <http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#> 
SELECT ?givenName ?familyName ?function ?gender ?nationality ?collaboratorCount
WHERE {
	REPLACE_ME a 							foaf:Person;
						foaf:givenName        		?givenName;
						foaf:familyName       		?familyName;
						ausestage:collaboratorCount ?collaboratorCount;
						ausestage:function    		?function.
	OPTIONAL {REPLACE_ME			foaf:gender           		?gender}                                           
	OPTIONAL {REPLACE_ME			ausestage:nationality 		?nationality} 
}
```

_Note 1:_ Replace the `REPLACE_ME` with the unique identifier of a contributor encoded using the [AusStage Unique Id Encoding Scheme](http://code.google.com/p/aus-e-stage/wiki/RdfExportMoreInfo#AusStage_Unique_Id_Encoding_Scheme).

For example to find all of the key collaborators for [Robyn Archer](http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_contrib_id=774) the query looks like this:

```
PREFIX foaf:       <http://xmlns.com/foaf/0.1/> 
PREFIX ausestage:  <http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#> 
SELECT ?givenName ?familyName ?function ?gender ?nationality ?collaboratorCount
WHERE {
	<ausstage:c:774> a 							foaf:Person;
						foaf:givenName        		?givenName;
						foaf:familyName       		?familyName;
						ausestage:collaboratorCount ?collaboratorCount;
						ausestage:function    		?function.
	OPTIONAL {<ausstage:c:774>			foaf:gender           		?gender}                                           
	OPTIONAL {<ausstage:c:774>			ausestage:nationality 		?nationality} 
}
```

## List Contributors to Events ##

This example retrieves contributors to multiple events. It demonstrates SELECT DISTINCT to retrieve only one instance of each row, and UNION to nest multiple WHERE statements.

```
PREFIX foaf:       <http://xmlns.com/foaf/0.1/> 
PREFIX ausestage:  <http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#> 
PREFIX event:      <http://purl.org/NET/c4dm/event.owl#>
PREFIX dcterms:    <http://purl.org/dc/terms/>
SELECT DISTINCT ?agent ?givenName ?familyName
WHERE {
 {<ausstage:e:82378> a                event:Event;
                    event:agent      ?agent.
 ?agent             a                foaf:Person;
                    foaf:givenName   ?givenName; 
                    foaf:familyName  ?familyName.
}
UNION
{<ausstage:e:84285> a                event:Event;
                    event:agent      ?agent.
 ?agent             a                foaf:Person;
                    foaf:givenName   ?givenName; 
                    foaf:familyName  ?familyName.
}
UNION
{<ausstage:e:88155> a                event:Event;
                    event:agent      ?agent.
 ?agent             a                foaf:Person;
                    foaf:givenName   ?givenName; 
                    foaf:familyName  ?familyName.
}
}
```