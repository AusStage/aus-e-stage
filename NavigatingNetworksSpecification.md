

This document specifies the development requirements for implementing the Navigating Networks service. It summarises the exploratory research we have undertaken on the visualisation and analysis of networks in the AusStage dataset, outlines the research needs for network analysis, describes the technical requirements for implementing the network interface, evaluates the tools and technologies underlying the service, and outlines a development schedule.

# EXPLORATORY RESEARCH #

This section summarises the exploratory research we have undertaken on the visualisation and analysis of networks in the AusStage dataset.

## Background ##

The impetus to visualise performing arts data emerges from the accumulation of data in AusStage. There is now, quite simply, more data in AusStage than it is possible to conveniently view on screen as text. Visualisations of various kinds – including charts, maps, timelines and network graphs – lend us the synoptic power of abstraction: they provide an opportunity to grasp the whole picture, to see relationships at a distance, where previously we had focused up-close on textual details.

In understanding the significance of data visualisations and their application to research in the performing arts, we have been inspired by literary historian Franco Moretti and psychologist Jacob Moreno (1889-1974). Some of the founding ideas about social networks were elaborated in the 1930s by Moreno. His research and practice focused on the connection between psychological well-being and the patterning of social relations between people.1 A key innovation introduced by Moreno in 1933 was the ‘sociogram’, a technique for depicting individuals as ‘points’ and their relations as connecting ‘lines’. Moreno used the sociogram to envisage how small-scale networks of kinship, friendship, the workplace and so forth aggregate into larger networks such as communities, corporations, and nation-states.

Moreno’s interest in aggregated social relations has parallels in the performing arts. The interactions between artists, as they train, rehearse and work together, have implications for the kinds of artists they become and the kind of performances they make. Consider the biographies in theatre programs, where the cast and crew each recount their relations with training institutions, theatre companies, leading artists and notable productions. A history of working where, with whom, and on what is how practitioners in the performing arts accumulate and articulate their experience, qualification and distinction. With its relational design, the AusStage database records these networks of contact and collaboration and how they change over time. Zooming out to bring these relations into focus entails a degree of abstraction and a reduction in detail – ‘fewer elements’, as literary historian Franco Moretti puts it, ‘hence a sharper sense of their overall interconnection’.

In Graphs, Maps and Trees, Moretti advocates a practice of ‘distant reading’, as distinct from the conventional ‘close reading’ of literary studies.4 He uses statistical graphs, geographical maps, and evolutionary trees to reveal shifting patterns of genre and form in the history of the novel. Moretti reminds us that quantitative research – of the collaborative kind undertaken through AusStage – provides data, not interpretation.5 Arranging such data in abstract models for graphic display invokes the analytical attitude, provoking interrogation and offering scope for new interpretations.

Underlying our explorations in visualisation is the information architecture created for the AusStage database. This architecture organises information about the performing arts into five main categories – events, artists, venues, organisations and resources – and it provides a way to record meaningful relations between entries in each category. Thus an event is a temporal happening, defined by its title and date, but also by its location at a venue, by the organisations that produce it, and by the cast and crew of artists who contribute to it. Our knowledge of the event is derived from all the resources – the programs, photographs, advertising, reviews, commentaries, scripts, recordings and so on – that are left behind in its wake.

Our research on analysing and visualising the networks recorded in the AusStage database has developed in conjunction with the dissemination of scientific research on networks, complexity and emergence. Works by Watts (1999, 2003), Ball (2004), Barabasi (2003), and Newman, Barabasi and Watts (2006) provided useful introductions to the field. Börner et al (2007) and Golden et al (2009) provide up-to-date surveys. An initial source of inspiration was the popular 'Oracle of Bacon' (http://oracleofbacon.org/) which draws relational data from the Internet Movie Database (http://imdb.com) to calculate shortest paths, network centrality and degrees of distance between actors working on movies. This network of actors and movies in IMDb presented a direct correlate with the network of contributors and events in the AusStage database.

Our understanding of statistical concepts for social network analysis draws on John Scott's (2000) Social Network Analysis: A Handbook, and on the various algorithms embedded in the software packages we have used. From Scott, we have drawn understandings of the following key concepts:

  * Degree - the total number of other points in the neighbourhood of a point, the total number of other points to which a point is directly linked. Neighbourhood - those points to which a particular point is adjacent. Adjacent - two points connected by a line are said to be adjacent. In directed graphs: Indegree of a point is the total number of other points that have lines directed towards it; Outdegree of a point is the total number of other points to which it directs lines. Multiplicity - the number of lines between two points, the separate contacts which make up the relationship
  * Path - sequence of lines between two points; Length of a path - the number of lines connecting two points; Distance - the length of the shortest path between two points. A path in a directed graph is a sequence of lines in which all the arrows point in the same direction. The distance between two points in a directed graph is measured only along the paths flowing in the same direction. When agents are regarded as either 'sources' or 'sinks' for the 'flow' of resources or information through a network, it is important to take direction into account
  * Density - The more points that are connected to one another, the more dense the graph will be. In a 'complete' graph, each point is connected to every other point. Inclusiveness - the number of points that are included within the various connected parts of the graph. Density - the number of lines in a graph, expressed as a proportion of the maximum possible number of lines. formula: l / (n(n-1)2) where l is the number of lines and n is the number of points.
  * Centrality - degree as a measure of local centrality - doesn't take into account indirect links, relative measure of local centrality = degree of a point / number of points. Global centrality is expressed as the distances among the various points (closeness). Sum distance = the sum of the distances from one point to all other points = the lower the sum, the closer the points.
  * Betweenness = the extent to which a particular point lies 'between' the various other points in the graph: a point of relatively low degree may play an important 'intermediary' role and so be very central to the network. The betweenness of a point measures the extent to which an agent can play the part of a 'broker' or 'gatekeeper' with a potential for control over others

## Experiments ##

Jonathan Bollen began experiments in visualising networks in the AusStage dataset in early 2008. He worked in an open-ended, exploratory manner, trialling various methods of data extraction, manipulation and visualisation, and sharing the process and results with colleagues. This research was undertaken in collaboration with Julie Holledge, the lead Chief Investigator on the AusStage project, and with Glen McGillivray and Yana Taylor from UWS. It was also pursued through a 2008/2009 summer scholarship which eResearchSA awarded to Nathan Lambert, a student in the Drama program at Flinders University.

The research focused on small subsets of the AusStage dataset, generating visualisations of networks of artistic collaboration associated with:

  * Venues – The Performance Space in Sydney (1986-2005); Playbox/Malthouse in Melbourne (1984-2008); Australian Dance Theatre (1964-2008)
  * Companies – Troupe, Red Shed, Brink Productions and The Border Project, theatre companies associated with Flinders Drama Centre (1976-2008);
  * Festivals – Adelaide Festival and Adelaide Fringe (1978-2004); and
  * Artists – Brent McGregor, Brian Joyce and Michael Cohen, three theatre artists associated with theatre production in Newcastle, NSW; Nigel Kellaway, David Williams and other artists associated with The Performance Space in Sydney; and

Results were presented at the AusStage Symposium and the eResearch Australasia conference in 2008, and reported in an article published in Australasaian Drama Studies (Bollen et al 2009).

Bollen and Holledge continued the research in 2009 as part of the Ibsen between Cultures Discovery Project. This work focused on visualising data related to Scandinavian productions of Ibsen plays and the integration of geographic mapping and network visualisation techniques. This research was presented at UCLA in 2009, and is currently being prepared for publication.

In August 2010, Bollen is returning to UCLA for a two-week institute on [Networks and Network Analysis for the Humanities](http://www.ipam.ucla.edu/programs/hum2010/), where he’ll be exploring the following research questions:

  * What is the network topology of artistic collaboration in the Australian performing arts? This question entails visualizing the entire set of relations between 70,000+ artists in the AusStage database and applying cluster analysis to identify core structures in the network.
  * How is aesthetic knowledge about performance transmitted between artists? This question entails visualizing the relations between 47,000+ events in the AusStage database as a directed network depicting the temporal flow of artists moving between events. Methods for modelling the accumulation and transmission of aesthetic knowledge will be explored.
  * How can network visualization inform an analysis of performance genre? This question will explore ‘hidden’ relations between events based on associations synthesised from a combination of descriptive fields, including genre, status, content indicators, and summary descriptions, in conjunction with artist, venue and organization associations. The results will form a basis for a relational analysis of genre as a basis for evaluating and improving the typological attribution of genre categories to events.

## Workflow ##

To prepare the visualisations, queries in Structure Query Language (SQL) were constructed and used to export sets of data from the AusStage database. Data were extracted from the Event, Contributor and ConEvLink tables in AusStage. The key fields were EventID, Event Name, First Date, ContributorID, Contributor Name and Function fields. Other fields were used to narrow the selection by venue, company, etc. The AusStage project manager generated large exports using Oracle administrator software; the AusStage data entry interface provides an SQL facility which limits returns to 250 rows.

AusStage records associations between events and contributors (cast and crew); it does not directly record associations between contributions. A manipulation to transform event-contributor relations into a series of contributor-contributor relations based on event associations was devised. This transformation was applied to the exported datasets.

ATTACH IMAGE.￼

To encode the structure and relationships of network data, the data was first migrated to DL, an edgelist format associated with UCINET and Pajek network analysis packages. Visone was then used to convert DL into GraphML, a relatively portable format for representing network data and its visualisation. A combination of regular desktop data manipulation tools – primarily Excel, FileMaker Pro and text-editors – were also used in the process of formatting the data.

In reformatting the data, it was necessary to concatenate some fields, such as dates, names and IDs, to use the data as a node or an edge attribute. For example, AusStage stores dates in day, month and year fields; these were combined into a single string, such as 19880114. We also concatenated the Event name with the Event ID and the contributor with the contributor ID.

dl n=612  format=edgelist1

labels embedded

data:
|James\_H._Bowles_(225287)|	Wendy\_Blacklock_(225082)_|	19700822|
|:------------------------|:----------------|:--------|
|Michael\_Cohen_(227898)_|  Wendy\_Blacklock_(225082)_|	19700822|
|Alastair\_Duncan_(226621)_|	        Wendy\_Blacklock_(225082)_|	19700822|
|Tim\_Eliott_(3392)_|      Wendy\_Blacklock_(225082)_|	19700822|
|John\_Gregg_(3018)_|     Wendy\_Blacklock_(225082)_|	19700822|
|Doug\_Kingsman_(3717)_|    Wendy\_Blacklock_(225082)_|	19700822|
|Jackson\_Lacey_(225364)_|    Wendy\_Blacklock_(225082)_|	19700822|
|Kirrily\_Nolan_(3548)_|    Wendy\_Blacklock_(225082)_|	19700822|
|Sean\_Scully_(3015)_|    Wendy\_Blacklock_(225082)_|	19700822|
|Anna\_Volska_(225038)_|    Wendy\_Blacklock_(225082)_|	19700822|
|Wendy\_Blacklock_(225082)_|	James\_H._Bowles_(225287)|	19700822|
|Michael\_Cohen_(227898)_|    James\_H._Bowles_(225287)|	19700822|
|Alastair\_Duncan_(226621)_|     James\_H._Bowles_(225287)|	19700822|
|Tim\_Eliott_(3392)_|     James\_H._Bowles_(225287)|	19700822|

We experimented with a range of network visualisation software, all open source, including:

  * BioLayout Express (http://www.biolayout.org/)
  * Cytoscape (http://www.cytoscape.org)
  * Gephi (http://gephi.org/)
  * Network Workbench (http://nwb.slis.indiana.edu/)
  * SoNIA (http://www.stanford.edu/group/sonia/)
  * Visone (http://visone.info/)
  * Vizster (http://hci.stanford.edu/jheer/projects/vizster/)
  * yEd Graph Editor (http://www.yworks.com)

Much of the work was undertaken using Visone, which strikes a balance in providing statistical tools for network analysis and graphical tools for visualisation. Visone is a cross-platform Java application which can import and export a variety of file formats, including DL, GraphML and PDF. We also used BioLayout Express (GraphML) to visualise network graphs in a three-dimensional display, and SoNIA to explore the animation of dynamic networks.

Data processing and visualisations were undertaken using conventional desktop and laptop machines running Mac OS X and Windows XP. The workflow we developed was effective at producing two-dimensional images, three-dimensional models and two-dimensional animations of networks focusing on particular companies, venues or individuals (<2,000 nodes, <20,000 links).

## Visualisations ##

Two approaches for visualising networks of artistic collaboration in the AusStage dataset were explored:

  1. artist-to-artist linked by events
  1. event-to-event linked by artists.

We also explored methods for viewing both approaches as dynamic networks, using time-slice and animation techniques.

_Artist-to-artist_

Artist-to-artist visualisations display connections between artists who have worked together on an event. Productions are visible as polygonal shapes linking clusters of people. A layout algorithm determines the position of points relative to the distance between them on the graph.

Practitioners who have worked on productions together exert a pull on each other and cluster closely together. This pull is strongest when practitioners have worked together on productions with many other contributors who have also worked together on other productions.

We have also visualised other network attributes, specifically measures of node centrality such as ‘degree of connection’ and ‘betweenness’ using colour and size; and we have distinguished contributor functions using node shape.

‘Degree of connection’ represents the extent to which artists have collaborated with others. ‘Betweenness’ is a complex measure of the extent to which an artist lies between other artists in the network: it identifies artists whose collaborations join areas of the network together; they are the anchor points, without which clusters of practitioners would float free (as, indeed, some do).

_Event-to-event_

An alternative approach to visualising the network of artistic collaboration is through the mapping of event-to-event relations determined by the flow of artists. The direction of the arrows indicates the movement of artists from one event to another subsequent event. The time series reveals the pathways of collaborations flowing between events.7 This approach enables us to chart an artist’s journey through the events of their career. Overall, it visualises the flow of artists through a series of events. It suggests the possibility of tracing contact and connection, lines of influence and aesthetic transmission.

_Time-slice_

Returning to the person-to-person approach, we graphed the network of relations between contributors as a year-by-year series.6 Working with data from the performance space, we found that each year, the network grows as new contributors are drawn into productions. The network also consolidates as contributors who have previously worked at the venue work there again. Over time, the network displays a dynamic of reaching out to incorporate new contributors, and a drawing from within to consolidate the core.

_Animation_

We continued our exploration of how networks change over time by graphically animating dynamic network data. Our preliminary animations focus on four theatre companies associated with Flinders University Drama Centre. These animations reveal a similar branching and consolidating dynamic. They reveal patterns of transition as the function of leading collaborators shifts over time. They also reveal patterns of growth, consolidation, release and reorganisation, reminiscent of the four-phase model of ecosystem dynamics.

## Interface ##

The Vizster project (http://hci.stanford.edu/jheer/projects/vizster/) provides a valuable model of an interactive interface to network data. Vizster addresses issues of focus and context in visualising networks, and presents an interactive interface for exploring network structure. Its design was guided by ethnographic research of online communities and it is the only network interface which, to our knowledge, has been subject to user testing (Heer and Boyd 2005).

Some other examples of interactive interfaces for exploring network data include:

  * Live Plasma http://www.liveplasma.com/ (Flash)
  * NetVis Module - Dynamic Visualization of Social Networks http://www.netvis.org/index.php (Java)
  * RAMA - Relational Artists Maps http://rama.inescporto.pt/ (Java)
  * MentionMap - A Twitter Visualization http://apps.asterisq.com/mentionmap/ (Constellation Framework)
  * Optimice - Optimizing Busines Reltationships http://www.optimice.com.au/friends_join.php (Constellation Framework)
  * Music Recommendations with JSViz http://www.kylescholz.com/projects/speaking/tae2006/music.fm/ (JSVis)
  * ViewRu - Music Network http://www.viewru.com/ (JSVis)
  * Processing.js demo http://www.mattryall.net/demo/atlassian-vis/contributors/ (processing.js)

Additional online resources which have informed our understanding of social networks, their visualisation and analysis include:

  * International Network for Social Network Analysis http://www.insna.org/
  * Social Networks: An International Journal of Structural Analysis http://www.elsevier.com/wps/find/journaldescription.cws_home/505596/description
  * Journal of Social Structure http://www.cmu.edu/joss/
  * MelNet, University of Melbourne http://www.sna.unimelb.edu.au/
  * Information Aesthetics http://infosthetics.com/
  * Info-vis wiki http://www.infovis-wiki.net
  * Visual Complexity http://www.visualcomplexity.com/vc/


# RESEARCH NEEDS #

This section summarises research needs for network analysis from the AusStage phase 4 LIEF Proposal and the Aus-e-Stage project plan.

## `AusStage` Phase 4 ##

AusStage researchers are taking an entirely new approach to analysing performance data by asking the question, ‘Who works with whom?’. The National eResearch Architecture Taskforce is funding the technological development of an interface for navigating the network of artistic collaborations in the AusStage dataset. ARC LIEF is funding research to enhance the representation of artists in the AusStage user interface. These development activities are driven by the research needs of the Networks Cluster of AusStage Phase 4: Jonathan Bollen (Flinders), Kim Durban (Ballarat), Shona Erskine and Nanette Hassal (ECU), Glen McGillivray (Sydney/UWS) and Helen Trenos (Tasmania).

AusStage researchers want to explore the network of artistic collaborations in the AusStage dataset. A network-based interface will humanize the representation of artists in AusStage by modelling the collaborative ethic of the performing arts. Network visualisation and analysis will enable researchers to interrogate patterns of collaborative creativity in the performing arts that have previously been unrepresentable using conventional text-based displays. The AusStage partnership has also identified a suite of related improvements for recording and displaying information about artists and their work, including: displaying artists’ associations with events by artistic function; recording details of funding and awards; improving the navigation between artistic collaborators; and using the upgraded interface to encourage artists and organisations to participate in data curation. To this end, a methodology based on social network analysis (Scott 2000) will be applied: artists identified as network ‘hubs’ in the dataset – those who have worked with many other artists – will be invited to review records and submit contributions.

## Aus-e-Stage ##

The NeAT-developed Network service will deliver an interactive interface for navigating networks in AusStage. Node-link network graphs will depict artists and their collaborations, enabling users to search, display and explore patterns of contributors working together and how these patterns change over time. The network service will script the encoding of network data to enable live display of user-search results as interactive network graphs with embedded links to AusStage records. Development and testing will be undertaken to determine suitable capabilities for the network interface which make it easy for researchers to specify the visualization of search results, explore particular relationships, and apply relevant analytical techniques. Tools for quantitative analysis of the collaboration network graphs will also be provided, enabling researchers to visualise centrality, hubs, clustering and other quantitative measures of network topology. The project will also scope the visualisation of a broader range of relationships between artists, organisations, venues, genres and events and the differentiation of relationships by artistic function (director, designer, actor, technician etc).

The new interface will be used by AusStage researchers to undertake and substantiate analyses which were previously difficult to deliver, including:  lines of artistic contact, influence and cross-fertilization; organisational cycles of growth, consolidation and release; patterns in career pathways and professional development; and emerging clusters of collaborative creativity suitable for strategic investment. AusStage will work with the Australia Council to apply these interface improvements in representing the work of artists, organisations and artistic collaborations within arts programming and arts funding policy.

## Research Applications ##

The development of network interface to support visualisation and analysis will be applied across a variety of research projects undertaken by AusStage partners:

  * Hassall and Erskine investigating creative collaborations amongst prominent West Australian dance artists
  * Bollen is tracking networks of mid-twentieth century Australian variety entertainers moving from tent shows and theatres to television studios and licensed clubs
  * Durban is documenting the unacknowledged influence of theatre director Ewa Czajor on generations of women directors
  * McGillivray is exploring concealed networks of collaboration in records of live performance that remain outside of authoritative archives
  * Trenos is documenting networks of performance production in Tasmania; and
  * Holledge, Tompkins and Bollen are analysing the collaborative networks of Ibsen production in Australia in their ARC Discovery Project.

## Emergent Principles ##

Through the course of exploratory research and the articulation of research needs, a set of principles have emerged which will guide the development of the network interface.

### A focus on artistic networks ###

While it is possible to use network analysis to visualise various relationships between different entities (organisations, events, venues, genres and so on), our focus is on the artistic networks articulated through the relationships between contributors and events. There is a realism to this focus reflects the collaborative ethic of the performance arts and the obligations of co-presence in the rehearsal room.

### An awareness of complexity ###

The AusStage contribute network is large and complex. At June 2010, it comprises 1,634,088 relationships between 76,488 contributors. Visualisation efforts using dedicated software have succeeded in working on relatively small sub-networks (<20,000 edges, <2,000 nodes) constrained by contributor (ego-centric networks), or by association with particular venues, and organisations. Visualising networks for display in web browsers imposes additional constraints.

### The necessity of focus+context ###

Our experience in using network visualisation for analysis is characterised by zooming in and out - zooming in to focus on particular artists and relationships, and zooming out to grasp their position within the larger network. This experience is addressed by the interface principle of focus plus context (http://www.infovis-wiki.net/index.php/Focus-plus-Context).

### An interest in quality ###

Researchers are interested in articulating the significance and quality of artistic collaborations. While these measures imply qualitative research, the are three aspects of data available in AusStage which could assist researchers in identifying quality and significance of a relationship between two or more artists: the number of their collaborations, the frequency of their collaboration over time (and changes in the frequency), their artistic functions(and again, changes in function over time), and their position (or 'betweeness') within an artistic network.

### The significance of time ###

The networks of artistic collaboration in the AusStage dataset are dynamic. They are time-coded by event and they change over time. Experiments in analysing network dynamics in the AusStage data suggest patterns of branching and consolidation, transitions in centrality, and sequences of growth, consolidation, release and reorganisation (Walker and Salt 2006). Yet approaches to analysing dynamic networks are at the leading edge of the field. Refining techniques for visualisation, including timelines, animation and 3D, will entail experimentation.

# INTERFACE IDEAS #

The Networks Cluster, a group of researchers involved in AusStage phase 4 -  Jonathan Bollen, Kim Durban, Shona Erskine, Nanette Hassall, Glen McGillivray and Helen Trenos - developed a set of ideas for the Network interface through a series of meetings to June 2010. These ideas were grouped according to functionality and ranked according to popularity across the cluster; rankings from Julie Holledge and Neal Harvey were also included.

The interface will present network graphs as an interactive method for exploring the network of contributors and events in the AusStage dataset. Network graphs are composed of nodes and edges. The node represents an entity in the AusStage database, either a Contributor or an Event; an edge represents a relationship between nodes, derived from the ConEvLink table.

## 1. NETWORK BROWSING - Person-To-Person  - Graphic Interface ##

### 1.1 Graphic Interface ###
_Priority Ranking: 94%_

The network browsing interface allows the user to search the AusStage database for a contributor. Once a contributor is selected by the user, the interface will display that contributor's network first degree network, i.e. those contributors with whom he or she  has directly worked. The default display will show the selected contributor, represented as a node in the centre of the network, with all associated contributors represented as nodes radiating outwards; the edges between nodes will also be shown.
When a user selects a node by double clicking on the node, this node becomes the ‘central’ node. Their first degree network will be added to the graphic network display.

Work flow:
  1. Initial Display
  1. User enters search criteria and selects search
  1. Database queried for contributors matching search criteria (NAME?) Results found?
YES: display results on page.
NO: display ‘no records found’. Return to 1.
  1. User selects contributor from results.
  1. User submits the choice.
  1. Network Browsing interface loads network for the submitted contributor.

Node selected (double click):
  1. is this the current node?
Yes: do nothing
No: redraw, or add to the network graphic this nodes first degree network.  

**Questions**

_QUESTION: How many degrees of separation before outlying nodes drop off?
ie say you start with contributorA. You select contributor B. Now you have ContributorA and contributorB’s networks. Now you select contibutorC who has collaborated with ContributorB., and not contributorA. Should contributorA’s network dissappear if only relevant to contributorA?_ ANSWER: As many degrees as possible. But actually the limit will be the number of nodes and links, not the number of degrees - because the number of nodes and links added with each degree will vary widely. At a certain point, there will be too many nodes and links being displayed. At that point, those which are furthest from the current focus, could be dropped from display.

_QUESTION: Can we retrieve data asynchronously and in anticipation of the user, to avoid the lag that's entailed in retrieving data between user clicks and display changes? To provide a seamless browsing experience, it would be preferable for more data to be downloaded to the client than is initially displayed. If the initial download includes the second degree network, but only displays the first degree, then the collaborators of contributors at the edge of the initial network will be available for display when required by the user. At some point, however, the user will reach the edge of the downloaded data, and the system will need to retrieve a new dataset. Ideally, this new dataset could be seemlessley appended to the existing display. If that is not possible, the new dataset, centered on the currently selected node, would replace the previous set on display._ ANSWER: In this case then we're probably going to need to look into using a technique called "[Web Storage](http://en.wikipedia.org/wiki/Web_Storage)" or "DOM Storage".In this way we can store data on a client machine that most importantly persists page refreshes. The potential downside is that it is only supported on recent browsers. Which isn't such a factor for us as most of what I anticipate we'll be doing will only be supported by recent browsers. This technique would give us a way to persist and cache data that would have a positive effect on the speed of the website. We would still need to develop some sort of algorithm to ensure that nodes are data is cached in an intelligent way and that the server isn't overloaded by too aggressive caching. For example using AJAX type techniques and caching nodes closest to the centre of the network first, which are most likely to be of immediate interest.

_QUESTION: Contributor archaeology presents a problem, as contributors with different names will currently be represented as different nodes. How to handle this? And how to prepare for the proposed database changes to handle contributor archaeology?_

**Interface sketch ideas**
  * [Basic Search Interface](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/1-1InterfaceBasicSearch.png)
  * [Network Interface Sketch 1](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/1-1InterfaceSketch.png)
  * [Network Interface Sketch 2](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/1-1InterfaceSketch2.png)
  * [Sketches of Node Visuals](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/nodeVisualIdeas.png)

### 1.2 Time-slider ###
_Priority Ranking: 89%_

The time-slider is a bar that depicts the start and end point of the contributor's activity within AusStage. The behaviour and look of the time-slider should correspond to the time-slider user for the Visual Mapping interface.
A user may manipulate both markers to determine a start and end date. The visual display of the network should then be refreshed to display only collaborations that occurred during this period. Ideally the nodes outside this date range would ‘fade’ from view. Collaborations are defined as contributors who are associated with the same event. So, if the event’s start date falls outside of the dates specified, the associated contributors are not displayed.
The start point is defined as the earliest start date of all the events that a contributor has been associated with.
The end point is defined as the latest start date of all the events that a contributor has been associated with.

Work flow:
  1. User alters start and/or end date.
  1. User submits changes
  1. Remove nodes and links from visibility that are outside of the dates specified.

### 1.3 Faceted browsing ###
_Priority Ranking 89%_

Included in the Network Browsing interface will be a panel of criteria represented as checkboxes, select menus and open text boxes. Criteria relate to the nodes (e.g. gender, nationality) or to information aggregated to nodes from event associations (e.g. artistic function). Selecting one or multiple criteria will highlight nodes that match that criteria, whilst fading the nodes that don’t match.

Criteria to be listed

| Label	| Database Reference| 	Type| 	Facet Options| RDF Reference |
|:------|:------------------|:-----|:--------------|:--------------|
| Gender| TABLE: CONTRIBUTOR - ROW: GENDER|	radio button 1 : Male radio button 2: Female| Highlight matching nodes OR only display nodes with matching criteria HOW DO WE DEAL WITH UNKNOWN?| foaf:gender _Only added to dataset if not UNKNOWN or NULL_|
| Nationality| TABLE: CONTRIBUTOR - ROW: NATIONALITY| Select menu, multiple selections allowed | Highlight nodes matching nationality/ies selected by user. | [ausestage:nationality](AuseStageOntology#nationality.md)|
| Function/s| TABLE: CONEVLINK| Select menu, multiple selections allowed, correspond to the functions listed in CONTFUNCT|Highlight nodes matching function/s selected by user| [ausestage:function](AuseStageOntology#function.md) |

Additional criteria derived from event and venue attributes (status, primary genre, secondary genre, suburb, state, country) may be considered after initial testing.

Work flow:
User selects a criteria
The browsing graphic is updated to display the criteria - (More detail)

### 1.4 Pop-ups ###

_Priority Ranking 83%_

1.4.1: 	Each node should be generated with a label. This label will correspond to the contributors name as stored rdf export. It should be apparent on mouse-over. 

&lt;foaf:name&gt;



1.4.2	Single clicking on a link or node within the network browsing graphic will trigger a popup window. This window will hold key information relating to that particular node or link. It will also select the node and its links; or the link and its two nodes.

Node information displayed:

| **Information For Display** | **Source** |
|:----------------------------|:-----------|
| Name (as link to AusStage Record) | foaf:name / rdf:id |
| Other names | [ausestage:otherNames](AuseStageOntology#otherNames.md) |
| Gender | foaf:gender |
| Nationality | [ausestage:nationality](AuseStageOntology#nationality.md) |
| Contributor ID | rdf:id |
| Function| [ausestage:function](AuseStageOntology#function.md) |
| Number of relationships | [ausestag:collaboratorCount](AuseStageOntology#collaboratorCount.md)|

Link information displayed

| **Information For Display** | **Source** |
|:----------------------------|:-----------|
| Name (as link to AusStage Record) | foaf:name / rdf:id |
| Number of events | Count of event:isAgentIn |
| Date range of events | Calculated from tl:intervals |
| Number of collaborations between contributors | [ausestage:collaborationCount](AuseStageOntology#collaborationCount.md) |

An alternative to pop-ups would be to display node and link information in separate panels.

_Pop Up Sketch Ideas_
  * [Pop Up Sketch 1](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/1-4PopUps.png)
  * [Pop Up Sketch 2](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/1-4PopUps2.png)

## 2. NETWORK BROWSING - Person-to-person - Text Interface ##

### 2.1 Key collaborators ###
_Priority Ranking 83%._

The contributor detail page within AusStage should display a collapsible list of collaborators, ordered by frequency of collaborations - most frequent, to least frequent.
The display will list the collaborator's name, collaboration count, year range, and a list of the collaborator's artistic functions. The collaborator's name is a link to their contributor detail page.

The layout should be as follows:

  * _Contributor detail page with network browsing collapsed_
[Screen Shot](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/collapsedCollaborators.jpg)

  * _Contributor detail page with network browsing expanded_
[Screen Shot](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/CollaboratorsExp.jpg)

### 2.2 Key organisations ###
_Priority Ranking 74%_

The contributor detail page within AusStage should display a collapsible list of organisations, ordered by frequency of collaborations - most frequent, to least frequent.
The display will list the organisation's name, collaboration count, year range, and a list of the contributor's artistic functions. The organisations name should be a link to their organisation detail page.

The layout should be as follows:

  * _￼Contributor detail page with organisation network browsing collapsed_
[Screen Shot](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/orgCollapsed.jpg)

  * _Contributor detail page with organisation network browsing expanded\_￼￼
[Screen Shot](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/orgsExpanded.jpg)_


## 3. NETWORK SEARCH AND DISPLAY - Person-to-person ##
The AusStage system should allow the user to search for contributors and events. The results of this search should be displayed graphically as in interactive network. The interface for displaying results should also allow user interaction to navigate further through the network. This interface should provide functionality such as the [Time-slider](NavigatingNetworksSpecification#1.2_Time-slider.md) and [faceted browsing](NavigatingNetworksSpecification#1.3_Faceted_browsing.md)  to further refine the network graphic results displayed.

### 3.1 Search for connections between collaborators ###

_Priority Ranking: 86%_

The search functionality should allow the user to select two or more collaborators.
This should work in a similar fashion to the AusStage Mapping service whereby the user can search for a contributor, select them, and then search again for another contributor etc.
Upon completion of the selection process the network browsing interface should display the network of contributors that connects them. _Question: is this a particular type of browsing? should ONLY the contributors that connect them, or that they have in common be displayed? How do we handle (if it arises) the possibility that there is NO path between the two contributors?_
This is a way of exploring closeness, degrees of separation, lines of influence, artistic families, and so on. There is an issue with playwrights, composers and so on - who do provide 'links' between artists - but may need to be excluded to explore lines of influence in the rehearsal room.

Work flow:
  1. User searches for a contributor.
  1. Results displayed
  1. User, if appropriate, selects to add a contributor from the results
  1. This process is repeated until the user has selected all desired contributors
  1. Once the user has selected all desired contributors to be mapped _(should there be a limit?)_ the network is displayed in the browsing interface.

_Search for connections between collaborators sketch ideas_

  * [Search for Connections Between Collaborators Sketch](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/3-1P2PSearch.png)

### 3.2 Search for events matching criteria ###

_Priority Ranking: 74%_

Search functionality should allow the user to select certain event criteria such as genre, status, content indicators, and/or keyword.
The browsing interface should then then display the network of contributors who have worked on events that match this criteria.

Criteria to be listed in the search field

|Label|database reference|Type|
|:----|:-----------------|:---|
|Status|TABLE:EVENTS ROW:STATUS, TABLE:STATUSMENU| Select menu. Multiple selections allowed.|
|Primary Genre|TABLE:EVENTS ROW:PRIMARY\_GENRE| Select menu. Multiple selections allowed.|
|Secondary Genre|TABLE:SECGENRECLASSLINK| Select menu. Multiple selections allowed.|
|Content Indicator|TABLE:EVENTS | Free Text.|

Work flow:
  1. User selects event criteria available.
  1. User approves the criteria selected.
  1. The system retrieves all contributors and their networks who have been involved with events matching the criteria.
  1. The contributors and their networks are then loaded graphically in the browsing interface.

_Search for contributors by event sketch ideas_

  * [Search for Contributors by Event Sketch](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/3-2NetworkEventCriteria.png)

### 3.3 Search by organisation ###

_Priority Ranking: 71%_

Search functionality should allow a user to search for networks by association to an organisation.
The browsing interface should then display the network of contributors who have worked with the selected organisation.

Work flow:
  1. User enters organisation search criteria - Organisation Name.
  1. Matching organisations are listed
  1. If the desired organisation is returned, the user selects this organisation. If not the user returns to the search field.
  1. If an organisation is selected the system will retrieve the network of contributors that have worked on events associated with this organisation.
  1. The contributors and their networks are then loaded graphically in the browsing interface.

_Search for networks by organisation sketch ideas_

  * [Search for Contributors by Organisation Sketch](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/3-3NetworkOrganisationAssociates.png)

### 3.4 Search by venue ###

_Priority Ranking: 63%_

Search functionality should allow a user to search for networks by association to a venue.
The browsing interface should then display the network of contributors who have worked on events performed within the specified venue.

Work flow:
  1. User enters venue search criteria - Venue Name.
  1. Matching venues are listed
  1. If the desired venue is returned, the user selects this venue. If not the user returns to the search field.
  1. If a venue is selected the system will retrieve the network of contributors that have worked on events associated with this venue.
  1. The contributors and their networks are then loaded graphically in the browsing interface.

_Search for networks by venue sketch ideas_

  * [Search for Contributors by Venue Sketch](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/3-4NetworkVenueAssociates.png)

## 4. NETWORKS+TIMELINES ##

### 4.1 Timeline ###

_Priority Ranking: 74%_

In addition to the network browser interface, we also require the following timeline visualisation functionality.
A user should be able to graph a particular contributors career as a line on a timeline.
The timeline should be interactive, see the Similie Project's interactive timeline widget [here ](http://www.simile-widgets.org/timeline/) as an example
  * The graph will show the contributor, as well as all of his collaborators as lines within this graph.
  * The lines will run horizontally, and parallel to each other, with space in between each of the different collaborators lines.
  * The lines should be colour coded (perhaps randomly selected colours)
  * There should be a legend, matching the colour of the lines to the particular contributors.
  * The left of the display shows the earliest date, extending to the latest date on the right.
  * When collaborators are recorded as working on the same event within the database, their lines should come together in the timeline graph in the corresponding date range.
  * When they are no longer working together, their lines should return to their initial position.
This type of visualisation should show the frequency of collaborations and their duration over time. Also which collaborators are converging, at which points in time and how often.

_Timeline sketch idea_

  * [Timeline Interface Sketch](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/4-1Timeline.png)

### 4.2 Shona's Idea ###
I want to map the works on BUZZ, STRUT, STEPS and LINK while also catching the career pathways of individual AD's of each company. In my mind the companies sit on one axis (say horizontal) and the individuals sit on another (say vertical). I then imagine where the lines cross. For example, Felicity Bott will cross STEPS and BUZZ. Alice Hollands will cross STEPS and STRUT. There will also be Phillipa Clarke crossing BUZZ and LINK, Chrissie Parrott and Michael Whaites for LINK. And many more too. The idea of incorporating a third dimension for time also sounds good. JB: I'm seeing companies as the horizontal lines, with artists as lines weaving across them and intersecting at events. If the events are spread along the company lines in chronological order, that would get the time element included. So to construct this kind of diagram - you'd select one or more companies and one or more artists - and then ask the system to build a timeline that weaves them together. Rank: 63%

## 5. NETWORK BROWSING - Event-to-event ##

Event-to-event networks provide an alternative way of exploring collaborations. In these graphics, the events are the spots and the lines connecting them are people.

### 5.1 Event-to-event network graphic ###

_Priority Ranking: 77%_

The event-to-event network browsing interface allows the user to search the AusStage database for an event. Once an event is selected by the user, the interface will display that event as the central node within a network of events - it's first degree network.
Events are related by the contributors who have been involved with them.
Clicking on a link should reveal the contributor, and highlight any other links that refer to that particular contributor.
If an event on the edge of the network is selected, the display should be refreshed to reveal its first degree network of events.
It should be noted here, the event to event network graph will display and perform differently to a person to person network graph. Where a person to person network graph doesn't include time as a basis for the layout of people and events, a event to event network will layout the network on a timeline. With each event occuring at a certain period in time, and people being the links from one event to the next.

Work flow:
  1. Initial event-to-event Display
  1. User enters search criteria and selects search
  1. Database queried for events matching search criteria (NAME?) Results found?
YES: display results on page.
NO: display ‘no records found’. Return to 1.
  1. User selects event from results.
  1. User submits the choice.
  1. Network Browsing interface loads network for the submitted event.

Node selected (double click):
  1. is this the current node?
Yes: do nothing
No: redraw, or add to the network graphic this nodes first degree network.  

Link selected:
  1. Display link details.
  1. Refresh display, highlighting all links where the link name is equal to the selected link name.


### 5.2 Advanced features ###

_Priority Ranking: 69%_

Advance features for refining the display of event-to-event networks could also include pop-ups, time-slider and faceted browsing.
Similar features are described in [Time-slider](NavigatingNetworksSpecification#1.2_Time-slider.md), [faceted browsing](NavigatingNetworksSpecification#1.3_Faceted_browsing.md) and [pop-ups](NavigatingNetworksSpecification#1.4_Pop-ups.md).

  * The Time-slider feature should work as described in 1.2 above since the time element relates to events.
  * Faceted Browsing however, will operate differently. _DISCUSSION NEEDED_ Is the criteria to sort events/nodes and as a roll on effect the links? OR is it a way of highlighting links?
  * Pop-ups will display data as listed below:

Node information displayed:
  * Event Name as link to AusStage record
  * Event Status
  * Primary Genre
  * Secondary Genre
  * Content Indicator
  * Location
  * Open and closing date of the event

Link information displayed
  * Contributor name as link to AusStage records
  * Other names
  * Artistic Functions

### 5.3 Search and display ###
Search and display by venue, organisations, event-criteria and connections could also be used as a way into exploring event-to-event networks. Rank: 66%

## 6. DYNAMIC NETWORKS ##

I'm looking at a network of collaborations in AusStage and I want to see how that network has changed over time, how it has evolved, where people joined and left, who stuck around, when the network grew, shrank, changed shape and so on.

### 6.1 Animation ###
> I'm looking at a network at a particular point in time. I hit play and the network animates through a series of changes - as new people are added, others leave with each collaboration, the network shifts and changes shape. I can hit pause at any point and see the time. I can also use a time-mark to scrub through the animation. Rank: 74%

### 6.2 Time-slices ###
> I'm looking at a network as a series of time-slices- each representing the network at successive points in time. The time-slices are arranged in three-dimensional space, with time as the third dimension. By manipulating the view, and looking at the time-slices from different angles, I can explore change over time. Rank: 71%

### 6.3 Fly-throughs ###
> I'm looking at a network as a series of time-slices - arranged in three-dimensional space. But this time, instead of rotating the object, I can fly through it - moving forwards and backwards in time, I can see the state of the network at any point in time. Rank: 69%

## 7. NETWORKS+MAPS ##

I'm looking at a contributor's career as a line on a map. The line links the venues at which the contributor has performed in chronological order. Following the line from beginning to end, I can see where the contributor has performed over time. If I start adding their collaborators to the map, I can see where those collaborators have worked together, how often, and at which points they head off on their own. This is similar to the timeline, with events displayed geographically on a map. Rank: 66%

## 8 STATISTICAL ANALYSES and PATTERN MATCHING ##

I'm looking at a network of collaborations in AusStage and I want to explore its statistical properties - degree of connection, shortest path, clustering and so on. I might want to compare its statistical properties to other networks. I might want to search AusStage for other network areas with similar or matching properties. Rank: 60%

# TOOLS AND TECHNOLOGIES - CANDIDATES FOR APPLICATION #

## RDF Dataset ##

To efficiently process data for the Navigating Networks service it is necessary to reformat the data stored in the AusStage database. The process of reformatting the data needs to be undertaken to align the data with the requirements of the networks service by creating direct links between contributors that have collaborated on the same event. There were three possible approaches:

  1. Reformat the data "on the fly" using SQL queries in the AusStage database
  1. Reformat the data into a new set of tables in the AusStage database
  1. Export the data into a different format outside the AusStage database

The option of reformatting data "on the fly" was found not to be viable due to the very large size of the datasets that needed to be created. This left the two remaining options, both of which create a dataset that is static and that needs to be periodically refreshed. It was also determined that due to the large size of the dataset the way in which it was indexed would be critical to the performance of the overall system. An efficient indexing system would mean that data could be retrieved quickly and efficiently with a minimum of processing.

Concurrent to this work was an investigation into the types of data formats that could be used to share data with external systems and processes. Early in these investigations the [FOAF project](http://www.foaf-project.org/) was evaluated and found to meet the basic needs of constructing a network. FOAF is an acronym meaning "Friend Of A Friend" and is an ontology that describes at a basic level people and their associations via the [foaf:knows](http://xmlns.com/foaf/spec/#term_knows) term. This term links two people together as it can be said that person A "knows" person B. Use of this ontology meant that that the data set would need to use [Resource Description Framework (RDF)](http://en.wikipedia.org/wiki/Resource_Description_Framework) based technologies.

The decision was made to undertake periodic exports of data from the AusStage database into an RDF based dataset. All of the development work undertaken as part of the Aus-e-Stage project has used the Java programming language. Therefore tools and libraries that used Java would need to be used to build the dataset. [Jena](http://openjena.org/), a Semantic Web Framework for Java was chosen as it has modular architecture which allowed development to be undertaken in stages.

The modules that have been used include:

  * [Jena](http://openjena.org/) as the glue between all of the other components
  * [TDB](http://openjena.org/wiki/TDB) to provide the persistent storage of the RDF dataset
  * [ARQ](http://openjena.org/ARQ/documentation.html) for the processing of [SPARQL](http://en.wikipedia.org/wiki/SPARQL) queries
  * [Joseki](http://www.joseki.org/) to provide a SPARQL endpoint

When constructing an RDF based dataset it is advisable to use existing standards or ontologies that are freely available. In this way the dataset that is made available to user becomes richer as it is a dataset that become self describing as the ontologies used provide context to the terms used to describe the data. A list of ontologies currently used by the dataset [is available](NavigatingNetworksDataset#RDF_Ontologies.md) and contains terms to describe contributors, the relationships between contributors and events. Where an existing ontology could not be found an [Aus-e-Stage ontology](AuseStageOntology.md) was created to fill these gaps. It is anticipated that the use of this ontology will be phased out as the plans for the periodic export of the entire AusStage dataset in RDF mature as part of a separate project.

As well as providing a platform for the creation of RDF data, that would make it easier to share data with other users and stakeholders, the data store was found to be provide efficient indexing and query times to return data were well below those provided by the relational database.

A planned [API](http://en.wikipedia.org/wiki/Application_programming_interface) will make it easier for users to extract data from the data store, for example extract the data necessary to construct a network graph for a particular contributor. An API of this type is not well suited to ad-hoc querying of the data. For this reason the SPARQL endpoint is made available to our users. Using this endpoint any user can construct a SPARQL query and return data from the RDF data store. [SPARQL](http://en.wikipedia.org/wiki/SPARQL) is a query language designed specifically for use with RDF based data.

In this way data that is required for the Navigating Networks Service is:

  * Stored in a format that makes it easier to share with others
  * The data uses the emerging [semantic web](http://en.wikipedia.org/wiki/Semantic_Web) based technologies
  * Data can be returned in an efficient manner supporting the many formats required for network visualisation
  * Ad-hoc querying of the data is also supported for those cases where the API does not meet the users needs

More information on the dataset can be found starting with the [Navigating Networks Dataset](NavigatingNetworksDataset.md) page here on the Wiki.

## Processing.js ##
[Processing.js](http://processingjs.org/) is a port of the Java based programming language - Processing.
It uses javascript to create interactive visualisations in a web based context, and appears on the surface to be entirely appropriate for our purposes.
It creates interactive images through iteration, within the HTML, the processing.js script makes a call to a draw method, the contents of which are defined by the programmer.
This draw method iterates indefinately, until either the exit method is called or the HTML page is closed.
It is only through the constant iteration of the draw method that interaction is made possible.
The language its self is relatively simple and easy to pick up. It is however difficult to find particular objects within a visualisation.
As a simple example, in the case of user interaction, if the mouse where to be used to select a 'node', in the underlying code the program would have to cross reference the mouse's x and y position with the x and y positions of every node and line displayed to work out which object had been selected, if any.

It is also worth noting that the processing.js library currently lacks support for force directed layouts. With this in mind, if we took this approach, we would need not only to write event handlers and further object definitions, but also we would need to somehow implement a force directed layout algorithm.

In summary, processing.js provides us with a language, and the ability to visualise and animate simple objects, it would be up to us to extend the provided libraries to implement both the physics elements and the extra levels of interactivity needed.

## Protovis ##
[Protovis](http://vis.stanford.edu/protovis/) is another language that implements javascript and SVG to create web visualisations of custom data.
Its library provides extensive support for force-directed network graphs, and on the surface, Protovis provides a very elegant solution to visualizing our network data.
Within the existing force-directed library is support for altering the physics behind the layout, from node repulsion and attraction, drag (friction), to spring dampening.
I have had some success altering the physics behind the visualization in order to get a reasonable view of the data being represented.

Protovis is able to render a largely connected network. The physics behind the Force Directed algorithm will need to be adjusted for our purposes, but a working example has proven this to be a possibility.

One question that remains to be answered is the interactivity of edges rendered with Protivis.

Protovis is built on javascript, and uses a variable array of data to render the network graphic. I believe it is possible to alter the contents of this data 'on the fly' and re-render.

The data is essentially JSON information wrapped in a variable as shown by the code below:
```
var contributors = {
  nodes:[
{
id:"123",
nodeName:"ABC",
},
{
id:"345",
nodeName:"DEF",
}
  ],
  links:[
{source:0, target:1, value:1},
{source:1, target:0, value:1}
]
};
```
Within the **nodes** array, any number of attributes can be added and manipulated within Protovis, allowing us to wrap all the essential contributor information within this array.
The important thing to note is how the **source** and **target** values within the **links** array work:
Essentially, every object in the **nodes** array is accessed via it's index. So when we look at the **links** array, the **source** value, 0, actually refers to the first object in the **nodes** array.
The **value** variable in the **links** array can relate to the edge weight. As Protovis receives this information, the **value** is added up for each source and target node and is stored within the node information as **linkDegree**. We can use this as a degree of connection.
Depending on further studies of Protovis, and whether or not further attributes can be stored in the **links** array, this value attribute may also be able to determine edge weight. ie assuming we can store within each **link** object another array storing the events between two collaborators, we can alter the **value** attribute to reflect the number of times the two collaborators have worked together.
Alternatively, Protovis automatically provides another way of dealing with edge weight that effects the opacity of lines. If a link between two nodes appears more than once in the data provided, the link becomes more opaque.

In the case of event to event networks Protovis is not quite as straight forward. A force directed layout is not appropriate given the time aspect to the layout required. It may be possible with further investigation to create an appropriate layout however it should be noted there is no prototype available at this time, therefore the visualisation and the logic behind it will need to be designed.

## JIT ##
[JIT](http://thejit.org/) or the JavaScript InfoVis Toolkit is a javascript based library that provides tools for creating interactive web visualisations.
At the time of writing, this particular library had provided me with the best results.
Essentially, the HTML page reads in a javascript file. Included in this file is an array that contains information on every node and its edges.
Basic testing revealed that it could display 120 nodes and 2863 edges with a loading time of 30seconds.
This is an example of one contributors first degree network. The interaction had a very slow response time. However has thus far been able to visualise the largest network.

## JSVis ##
Another javascript visualisation library with physics and layout support for force directed layouts. This library also supports the dynamic generation of graphs using XML.
At this point I have abandoned testing of JSVis. Although existing examples of networks using this library are promising, I have had trouble creating a working example. Investigation in a few forums (Apologies, I did not keep the links and hence cannot reference them!) suggests that JSVis is also not geared towards larger networks.

## SIMILE ##
discuss.

# DEVELOPMENT SCHEDULE #

Please refer to the [linked pdf file](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/navigating-networks-development-schedule.pdf) for Navigating Networks Development Schedule details.



# REFERENCES #

Ball, Philip. 2004. Critical Mass: How One Thing Leads to Another. Heinemann/Farrar, Straus & Giroux.

Barabasi, Albert-Laszlo. 2003. Linked: How Everything Is Connected to Everything Else and What It Means, Plume.

Bollen, J., Harvey, N., Holledge, J., McGillivray, G. 2009. ‘AusStage: eResearch in the Performing Arts’, Australasian Drama Studies, 54.

Börner, Katy, Sanyal, Soma and Vespignani, Alessandro. 2007. Network Science. In Blaise Cronin (Ed.), ARIST, Information Today, Inc./American Society for Information Science and Technology, Medford, NJ, Volume 41, Chapter 12, pp. 537-607. http://ivl.slis.indiana.edu/km/pub/2007-borner-arist.pdf

Goldenberg, A., Zheng, A.X., Fienberg, S.E., & Airoldi E.M. 2009.   A survey of statistical network models.   Foundations and Trends in Machine Learning,   2, 129-233. http://arxiv.org/abs/0912.5410/

Heer, Jeffrey and danah boyd. 2005. Vizster: Visualizing Online Social Networks, Proc. IEEE Symposium on Information Visualization (InfoVis), pp. 32-39, Oct 2005. http://vis.berkeley.edu/papers/vizster/

Moreno, J.L. 1953. Who Shall Survive? Foundations of Sociometry, Group Psychotherapy and Sociodrama. Beacon, N. Y.: Beacon House.

Moretti, Franco. 2005. Graphs, Maps, Trees: Abstract Models for a Literary History, London & New York: Verso.

Newman, Mark, Albert-Laszlo Barabasi and Duncan J. Watts. 2006. The Structure and Dynamics of Networks, Princeton, NJ: Princeton University Press.

Walker, B.H. & D. Salt. 2006. Resilience Thinking: Sustaining Ecosystems and People in a Changing World, Washington, D.C.: Island Press.

Watts, Duncan J. 2004. Six Degrees. The Science of a Connected Age, Norton.

Watts, Duncan J. 1999. Small Worlds: The Dynamics of Networks between Order and Randomness (Princeton Studies in Complexity), Princeton, NJ: Princeton University Press.
