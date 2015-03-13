<h1>Navigating Networks Dataset</h1>

The sections below detail information about the dataset that is constructed by the RdfExport application for use by the [Navigating Networks Service](NetworkService.md) by periodically exporting data from the AusStage relational database.



# Export Steps #

The RdfExport application undertakes the following steps to build the dataset

  1. [An export of all contributor records](NavigatingNetworksDataset#Contributors.md)
  1. [An export of contributor functions](NavigatingNetworksDataset#Contributor_Functions.md)
  1. [An export of contributor relationships](NavigatingNetworksDataset#Contributor_Relationships.md)
  1. [An export of events and contributor relationships to events](NavigatingNetworksDataset#Events.md)
  1. [An Export of functions that contributors have had when they've contributed to an event](NavigatingNetworksDataset#Contributor_Functions_at_Events.md)

# RDF Values & Source Fields #

## Contributors ##

| **Database Table** | **Database Field** | **RDF Field** | **Notes** | **Supports Goal** |
|:-------------------|:-------------------|:--------------|:----------|:------------------|
| contributor | contributorid | rdf:about | Encoded in the [AusStage URI Schema](NavigatingNetworksDataset#AusStage_URI_Schema.md) | [All](NavigatingNetworksGoals.md) |
| contributor | contirbutorid | foaf:page | As a persistent link to the Contributor page in AusStage | [All](NavigatingNetworksGoals.md) |
| contributor | first\_name | foaf:givenName |  | [All](NavigatingNetworksGoals.md) |
| contributor | last\_name | foaf:familyName |  |  [All](NavigatingNetworksGoals.md) |
|  | first\_name + " " + last\_name | foaf:name |  |  [All](NavigatingNetworksGoals.md) |
| contributor | gender | foaf:gender| Where the field is "unknown" or blank no gender node is added | [1.3](NavigatingNetworksGoals#1.3_Faceted_browsing.md) |
| contributor | nationality | ausestage:nationality | Where the field is null, no nationality is added | [1.3](NavigatingNetworksGoals#1.3_Faceted_browsing.md) |
|  |  | ausestage:hasCollaboration | used to link a contributor with a collaboration object | [1.1](NavigatingNetworksGoals#1.1_Graphic_Interface.md) [1.2](NavigatingNetworksGoals#1.2_Time-slider.md) [2.1](NavigatingNetworksGoals#2.1_Key_collaborators.md) [4.1](NavigatingNetworksGoals#4.1_Contributor_Career_as_a_line_on_a_timeline.md) |
| contributor | yyyydate\_of\_birth, mmdate\_of\_birth, dddate\_of\_birth | bio:event/bio:Birth/bio:date | At a minimum a yyyydate\_of\_birth is required for this to be added | [1.4](NavigatingNetworksGoals#1.4_Pop-ups.md) |

### SQL Code ###

```
SELECT c.contributorid, c.first_name, c.last_name, LOWER(g.gender), nationality, 
       c.yyyydate_of_birth, c.mmdate_of_birth, c.dddate_of_birth 
FROM contributor c, gendermenu g, 
     (SELECT DISTINCT contributorid FROM conevlink WHERE eventid IS NOT NULL) ce 
WHERE c.gender = g.genderid(+) 
AND c.contributorid = ce.contributorid 
ORDER BY contributorid
```

## Contributor Functions ##
| **Database Table** | **Database Field** | **RDF Field** | **Notes** | **Supports Goal** |
|:-------------------|:-------------------|:--------------|:----------|:------------------|
| subquery | functions | ausestage:function |  | [1.3](NavigatingNetworksGoals#1.3_Faceted_browsing.md) |

### SQL Code ###
```
SELECT c.contributorid, cp.preferredterm
FROM contributor c, contributorfunctpreferred cp, contfunctlink cl,
     (SELECT DISTINCT contributorid FROM conevlink WHERE eventid IS NOT NULL) ce
WHERE c.contributorid = cl.contributorid
AND cl.contributorfunctpreferredid = cp.contributorfunctpreferredid
AND c.contributorid = ce.contributorid
ORDER BY contributorid
```

## Contributor Relationships ##

| **Database Table** | **Database Field** | **RDF Field** | **Notes** | **Supports Goal** |
|:-------------------|:-------------------|:--------------|:----------|:------------------|
|  |  | ausestage:collaboratorCount | Stores the number contributors a contributor has collaborated with | [1.1](NavigatingNetworksGoals#1.1_Graphic_Interface.md) |
|  |  | ausestage:collaboration | represents a collaboration relationship between to contributors | [1.1](NavigatingNetworksGoals#1.1_Graphic_Interface.md) [1.2](NavigatingNetworksGoals#1.2_Time-slider.md) [2.1](NavigatingNetworksGoals#2.1_Key_collaborators.md) [4.1](NavigatingNetworksGoals#4.1_Contributor_Career_as_a_line_on_a_timeline.md)|
|  |  | ausestage:collaborator | stores the link to a contributor in this collaboration relationship | [1.1](NavigatingNetworksGoals#1.1_Graphic_Interface.md) |
|  |  | ausestage:collaborationCount | store the number of times these two collaborators have collaborated | [1.1](NavigatingNetworksGoals#1.1_Graphic_Interface.md) |
|  |  | ausestage:hasCollaboration | links a contributor to this collaboration | [1.1](NavigatingNetworksGoals#1.1_Graphic_Interface.md) |

### SQL Code ###
```
SELECT c.contributorid, c1.collaboratorid, COUNT(c.contributorid) as collaborations 
FROM conevlink c, (SELECT eventid, contributorid AS collaboratorid FROM conevlink WHERE contributorid IS NOT NULL AND eventid IS NOT NULL) c1 
WHERE c.eventid = c1.eventid 
AND c.contributorid IS NOT NULL 
GROUP BY c.contributorid, c1.collaboratorid 
ORDER BY contributorid
```

## Events ##

| **Database Table** | **Database Field** | **RDF Field** | **Notes** | **Supports Goal** |
|:-------------------|:-------------------|:--------------|:----------|:------------------|
| events | eventid | rdf:about | Encoded in the [AusStage URI Schema](NavigatingNetworksDataset#AusStage_URI_Schema.md) | [All](NavigatingNetworksGoals.md) |
| events | event\_name | dcterms:title |  | [All](NavigatingNetworksGoals.md) |
| events | eventid | dcterms:identifier | As a persistent link to the Event page in AusStage | [All](NavigatingNetworksGoals.md) |
| contributor | contributorid | event:agent | Encoded in the [AusStage URI Schema](NavigatingNetworksDataset#AusStage_URI_Schema.md) |  [1.1](NavigatingNetworksGoals#1.1_Graphic_Interface.md) [2.1](NavigatingNetworksGoals#2.1_Key_collaborators.md) |
| events | eventid | event:isAgentIn | Associated with a contributor not an event | [1.1](NavigatingNetworksGoals#1.1_Graphic_Interface.md) [2.1](NavigatingNetworksGoals#2.1_Key_collaborators.md) |
| events | start date fields | event:time/time:interval/tl.beginsAtDateTime | Encoded as a valid xsd:date value | [1.2](NavigatingNetworksGoals#1.2_Time-slider.md) |
| events | last date fields | event:time/time:interval/tl.endsAtDateTime | Encoded as a valid xsd:date value | [1.2](NavigatingNetworksGoals#1.2_Time-slider.md) |

### SQL Code ###
```
SELECT DISTINCT e.eventid, e.event_name, c.contributorid, 
                e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, 
                e.yyyylast_date, e.mmlast_date, e.ddlast_date 
FROM events e, conevlink c  
WHERE e.eventid = c.eventid 
AND e.eventid IS NOT NULL 
AND c.contributorid IS NOT NULL 
ORDER BY e.eventid
```

## Contributor Functions at Events ##

| **Database Table** | **Database Field** | **RDF Field** | **Notes** | **Supports Goal** |
|:-------------------|:-------------------|:--------------|:----------|:------------------|
|  |  | ausestage:functionAtEvent | lists the function that a contributor had when contributing to an event | [1.1](NavigatingNetworksGoals#1.1_Graphic_Interface.md) [2.1](NavigatingNetworksGoals#2.1_Key_collaborators.md) [1.3](NavigatingNetworksGoals#1.3_Faceted_browsing.md)|
| eventid | conevlink | ausestage:atEvent | link the fuctionAtEvent object to the event |  |
| contributorfunctpreferred | preferredterm | ausestage:function | list the function that the contributor held |  |
| conevlink | contributorid | ausestage:undertookFunction | link the contributor to this functionAtEvent object |  |


### SQL Code ###
```
SELECT cl.contributorid, cl.eventid, cfp.preferredterm 
FROM conevlink cl, contributorfunctpreferred cfp 
WHERE cl.eventid IS NOT NULL 
AND cl.contributorid IS NOT NULL 
AND cl.function = cfp.contributorfunctpreferredid 
ORDER BY contributorid
```

## Organisations ##
| **Database Table** | **Database Field** | **RDF Field** | **Notes** | **Supports Goal** |
|:-------------------|:-------------------|:--------------|:----------|:------------------|
| organisation | organisationid | rdf:about | Encoded in the [AusStage URI Schema](NavigatingNetworksDataset#AusStage_URI_Schema.md) | [All](NavigatingNetworksGoals.md) |
| organisation | name | skos:prefLabel |  | [2.2](NavigatingNetworksGoals#2.2_Key_organisations.md) [3.3](NavigatingNetworksGoals#3.3_Search_by_organisation.md) |
| organisation | other\_names1 | skos:altLabel |  | [2.2](NavigatingNetworksGoals#2.2_Key_organisations.md) [3.3](NavigatingNetworksGoals#3.3_Search_by_organisation.md) |
| organisation | other\_names2 | skos:altLabel |  | [2.2](NavigatingNetworksGoals#2.2_Key_organisations.md) [3.3](NavigatingNetworksGoals#3.3_Search_by_organisation.md) |
| organisation | other\_names3 | skos:altLabel |  | [2.2](NavigatingNetworksGoals#2.2_Key_organisations.md) [3.3](NavigatingNetworksGoals#3.3_Search_by_organisation.md) |

### SQL Code ###
```
SELECT organisationid, name, other_names1, other_names2, other_names3
FROM organisation
```


# RDF Ontologies #

## In Use ##

| **Ontology Name** | **Namespace** | **More Information** |
|:------------------|:--------------|:---------------------|
| FOAF - Friend of a Friend| foaf | http://www.foaf-project.org/ |
| Event| event | http://motools.sourceforge.net/event/event.html |
| DCMI Metadata Terms| dcterms | http://dublincore.org/documents/dcmi-terms/ |
| Timeline | tl | http://motools.sourceforge.net/timeline/timeline.html |
| OWL-Time | time | http://www.w3.org/TR/owl-time/ |
| Aus-e-Stage | ausestage | AuseStageOntology |
| BIO | bio | http://vocab.org/bio/0.1/.html |
| Organization | org | http://www.epimorphics.com/public/vocabulary/org.html |
| SKOS | skos | http://www.w3.org/TR/skos-reference/skos.html |

## Aus-e-Stage Ontology ##

To expedite the construction of the dataset an Aus-e-Stage ontology is used. It is important to note that this ontology does not pre-empt any work conducted under the AusStage phase 4 project. Once the flattening of the AusStage dataset has occurred and the ontologies constructed have been finalised, the ontology used for this dataset should be retired and replaced with the output of the AusStage phase 4 project.

Details of the ontology are available on the AuseStageOntology page.

## Planned for Use ##

| **Ontology Name** | **Namespace** | **More Information** | **Notes**|
|:------------------|:--------------|:---------------------|:|


# AusStage Unique Id Encoding Scheme #
<a href='Hidden comment:  Keep this list in sync with the RdfExportMoreInfo page'></a>

To assist in the clarity of AusStage unique identifiers in the dataset the following encoding scheme is used:

  * A unique identifier is prefixed with _ausstage:_
  * If the unique identifier is for a contributor _c:_ is then added
  * If the unique identifier is for an event _e:_ is then added

The [AusStageURI class](http://code.google.com/p/aus-e-stage/source/browse/trunk/rdf-export/src/au/edu/ausstage/vocabularies/AusStageURI.java) in the au.edu.ausstage.vocabularies package provides convenience methods for encoding identifiers, and converting identifiers into the persistent URLs used by the AusStage website

The class also provides functionality that encodes a unique identifier for the collaboration objects in the NavigatingNetworksDataset the identifier takes the form:

ausstage:rel:id1-id2

Where _id1_ and _id2_ are contributor ids. The URI is resolved to a URL in the Navigating Networks site that can provide additional information about the collaboration relationship.

# Additional Information #

  * http://en.wikipedia.org/wiki/Resource_Description_Framework