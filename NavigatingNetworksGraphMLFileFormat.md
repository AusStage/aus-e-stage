## Introduction ##

GraphML can be used to define the topology of a graph by declaring its nodes and edges. In addition to this, application related information of simple type for the elements of the graph can be specified through the extension [GraphML-Attributes](http://graphml.graphdrawing.org/primer/graphml-primer.html#Attributes). To look into the capability of the network analysis and visualization software (Network Workbench 1.0, Visone 2.6.2 and Gephi 0.7 beta) in regards to their support for GraphML format – attribute in particular, a simple actors’ network in GraphML format is prepared and examined in the software mentioned above.


## Actors’ Network in GraphML Format ##

To illustrate the GraphML with attribute support, a sample GraphML described a simple actors’ network with 3 nodes and 2 edges is prepared. Please refer to the following Figure.

http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/graphml.PNG

In this actors’ network, actors are defined as nodes.  The contributor ID is defined as node id. Each node has 3 attributes. They are **FirstName**, **LastName** and **Gender** respectively. Edge is defined from source node to target node. It represents the events two actors have worked on. Each edge has 2 attributes. They are **NumOfEvents** which represents edge weight and **DateRange**. The topology of the network is shown in the following Figure.
http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/topology.PNG


## GraphML support in Network Workbench, Visone and Gephi ##


To explore how GraphML format is supported in the network analysis and visualization software such as Network Workbench (GUESS is used as visualization tool), Visone and Gephi in particular, eight categories are identified to examine their GraphML support when the actors network file is opened in these three software. They are **Topology**, **NodeID**, **Edge source & target**, **Node attribute definition**, **Node attribute value**, **Edge attribute definition**, **Edge attribute value** and **GraphML export**. The first seven categories are related with input format and how the software explains attributes and their value when the GraphML file is opened in them. The following section will explain the eight categories and how the software supports it respectively. Table 1 shows the summary of the comparison.

**1)	Topology** is used to justify whether the software can display the graph (nodes and edges) correctly without considering attributes.
> All the three software can display the graph properly. In another words, all the nodes and edges declared in the actors network can be recognized.
_Note:_
> [GUESS](http://guess.wikispot.org/GraphML) and [Gephi](http://gephi.org/users/supported-graph-formats/graphml-format/) said on their website that it support a limited set of this format (no subgraphs or hyperedges).
> [Visone](http://visone.info/doku.php?id=docs:index) said that its native file format  is GraphML.

**2)	Node ID**
> Gephi and GUESS can preserve the ID value and assign the ID value to node label. However, Visone will re-assign a new value to the Node ID and the original Node ID (contributor ID) is lost.

**3)	Edge source & target**
> All three software preserve the Edge source and target correctly. The edge source and target  in Visone refer to new Node IDs as mentioned in 2).

**4)	Node attribute definition**
> All three software preserve the Node attribute definition correctly. Gephi displays the Node attribute definition and its value in Data Table. Visone displays these in Attribute Manager and GUESS in Information Window.

**5)	Node attribute value**
> Node attribute values are preserved correctly in Visone and Gephi. However, no value is preserved in GUESS.

**6)	Edge attribute definition**
> All three software preserve the Edge attribute definition correctly. The definitions are displayed in correspondent windows mentioned in 4).

**7)	Edge attribute value**
> Edge attribute values are preserved correctly in Visone. No value is preserved in GUESS. Gephi can only recognize the value with the edge attribute name “weight”.

**8)	GraphML export**
> GUESS dose not support GraphML export function. Gephi adds some attributes for nodes such as size and color. Visone adds some attributes for nodes and edges such as style information.


Table 1. GraphML capability comparison.
http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/graphML-comparison-table.PNG

Based on the above observation, Visone is the best to cope with GraphML format among these three software. However, it re-assigns a new value to the Node ID and the original Node ID (contributor ID) is lost. To keep the contributor ID, we can add an extra attribute for node. Please see the following snippet in red lines. In this way, all the application related information for each node and edge can be browsed in the Attribute Manager in Visone.

http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/new-contributor-ID-attribute.PNG