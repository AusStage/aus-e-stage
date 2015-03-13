# AUS-e-STAGE MAPPING SERVICE #

## REPORT ON THE ALPHA 1 TESTING PHASE ##

### Testing group invitees ###

LIEF Grant stakeholders: Gillian Arrighi, Kathryn Gilbey, Joon Kwok, Paul Makeham,
Peter Stephenson, Helen Trenos, David Watt

Aus-e-Stage Project Committee: Jonathan Bollen, Paul Coddington, Neil Dickson, Jenny Fewster, Chris Marlin, Ian Maxwell, Liz Milford, Corey Wallis

Selected additional researchers: Bill Dunstone, Neal Harvey, Julie Holledge

### Number of respondents ###
14

### Testing period ###
March/April 2010

### Summary of feedback requested ###
Please go to http://beta.ausstage.edu.au/mapping/ to view the trial version of the Aus-e-Stage Mapping service. When you have ten or fifteen minutes could you please have a play with the site.

When you have explored its current possibilities we would like you to answer the following questions:
  * Can you use this mapping function in AusStage to further your research?
  * What additional functions would you like to see incorporated in the maps to further your research needs?
  * How user friendly are these maps?

To assist with this feedback, a few more specific questions are listed below that cover the interactive elements of the prototype:
  * Have you been able to easily and accurately search for an organisation?
  * Did you find the organisation search related time slider useful and easy to use?
  * Was organisation search trajectory information accurately shown and of use?
  * Have you been able to successfully browse the available AusStage venues?
  * Was interpretation of the browse venue map easy to navigate and zoom functionality adequate?
  * Did you find the marker linked venue information and time lines function useful?
  * Did you experience any difficulties in downloading and using the KML code or persistent map links?


## Feedback on the Map Events by Organisation function ##

Reviewers were happy with the Map Events by Organisation screen layout and functionality overall.  They were particularly enthusiastic about the potential application of the mapping service to their own companies and research interests, and observed numerous possible collaborative opportunities that could occur in future, such as statewide and national liaisons between artistic historians endeavouring to map historical performance events within particular timeframes.  Queensland respondents, Paul Makeham and Joon Kwok, examined the service with specific recommendations from the Mapping Queensland Theatre Report (Baylis 2009) in mind to help understand how Queensland functions theatrically and to produce maps of  Queensland theatre ecology.

Progress on the Map Events by Organisation service to date was well acknowledged.  Some recommendations were also made to further improve and enhance it.  Of concern for several reviewers was the zoom function “clunkiness” and resulting slight difficulty of use.  A couple of users requested the zoom function be specific to their search criteria and display the map of the full extent for  the found data set (including international sites) without requiring additional zooming in and out.  In addition, the trajectory was not considered as effective as it could be.  Some requested that the trajectory be marked with a point of origin and used to display the events of one specific production (e.g. tour tracking).  Clarification of the timeslider functionality was also requested and it was suggested that simple instruction could be added to explain how to use this.  Various aesthetic factors such as colours and visual display could also do with some more dynamic enhancement but the colour intensity (light to dark etc) on markers and trajectory lines was said to be fine.  There were a few individual problems with downloading and using KML files but these seemed linked to the speed of user systems and ability to use Google Earth functions.

Suggestions for future service revisions for the Map Events by Organisation function included the additions of a progressive animation factor in the timeline and a number of data layers such as historical maps, census data and venue archaeology details (for use on all maps).  Additional requests for institutional data and audience demographics were also made but as this information is not currently available in the AusStage database it can therefore not be presented.

## Feedback on the Browse Event Data by Venue ##

Reviewers were once again very impressed with the overall concept and application potential of the Browse Event Data by Venue map and commended the Aus-e-Stage team on progress to date.  As with the Map Events by Organisation service there were a few suggestions for service revisions and enhancements for the Browse Event Data by Venue facility.

At this stage the number of pins on the map (some 1000) takes a little time to render and it was of concern that the addition of another 4000 or so (when all venue geocoordinate information is eventually added to AusStage) would further compound time to load and the visual effect on the screen (clutter factor).  As such the ability to select a reduced time span prior to loading the map may be advisable, as well as the exploration of other methods to speed up the map pin generation.  It was also suggested that the service could be simplified to just provide basic venue details with pins on the initial map and then offer a drill down option via additional clicks through selected pins to provide the full venue performance details (rather than having all information linked to the first map pin).  Queensland reviewers suggested that this mapping service could be more user friendly if a venue search function was added as this would not only allow researchers to browse more specific venue data but also speed up rendering time.  Request to view organisation information relating to the events listed under the venue pin was also made.

Again additions such as map and data overlays and the ability to visualize performance tours were raised in conjunction with the Browse Event Data by Venue service.  The option to not only map tours for particular companies but also tours relating to a specific set of performances (that may be part of an umbrella event) was discussed.  Specific mention was made of types of maps such as transport routes (roads, rail, shipping), historical town regions, goldfields, warden maps, town sites, population statistics, shipwrecks, indigenous zones, language areas and historical modes of transport (and therefore the times taken to travel between locations etc).  As there are so many options available and dependent on individual research interests the suggestion was made that the specialised map layers (including geocode coordinate information and, if required, linked time slider information) be sourced and supplied to the Aus-e-Stage project team by individual parties as required.  Alternatively, the desire to show “homegrown” activities was also requested (ie when the location of the venue and the location of the contributing organisations coincide, as opposed to tours).

## Additional Mapping Functions ##

Reviewers suggested a number of additional mapping functions.  Several of these have been incorporated into the above notes on Map Events by Organisation and Browse Event Data by Venue but a few completely new ideas were also received.

The desire to map multiple organisations and contributors (single and multiple) to maps was requested by all reviewers.  The ability to map the results of a specific event search (by title) was also important and the wish to map by genre and sub-genre (particularly for historical reasons) requested on several occasions.  Methods to map by region, postcode, suburb/town and indigenous language areas were also suggested by testers.

A full summary of feedback requests is included on the following pages of this document and can also be viewed at the Aus-e-Stage Google Code site at http://code.google.com/p/aus-e-stage/issues/list.


---

## FEEDBACK SUMMARY ##
As tabled on the Aus-e-Stage Google Code site
http://code.google.com/p/aus-e-stage/issues/list

### 1.	Map multiple organisations on the one map ###
Currently the search by organisation interface only allows one organisation to be added to a map. A required feature of this part of the service is to have the ability to:
  * conduct a search by organisation name
  * select multiple organisations (perhaps through the use of checkboxes)
  * generate a map containing the data from all of the selected organisations

### 2.	Map contributors onto a map ###
Duplicate the existing functionality that is provided in the Alpha 1 release of the organisation search, except search for contributors by name / id.

### 3.	Map multiple contributors on the one map ###
Extend the search by contributor interface to have the ability to:
  * conduct a search by contributor name
  * select multiple contributors (perhaps through the use of checkboxes)
  * generate a map containing the data from all of the selected contributors

### 4.	Map results of an event search ###
Provide functionality that allows a user to conduct an event search, similar to the existing event search in the AusStage database and map the events that match. An example is to conduct a search for "midsummer night's dream" and then display a map containing place marks that indicate where an event of this play have occurred.

### 5.	Zoom on a map is clunky ###
Zooming on a map can be clunky as it isn't immediately obvious how to zoom.  Alternative ways of providing zoom functionality should be explored.

### 6.	Enhanced time slider functionality on a map in a web page ###
The existing time slider functionality on a map in a web page is useful. However it isn't the same as that as provided by Google Earth in that it is a static change. It would be nice if the time slider could automate the changes from one time period to the next as a form of animation within the map on the page.

### 8.	Show tours on a map (and #7 Trajectory Information on a Map isn’t clear) ###
(->AusStage-Phase-4 consideration)
Extend the trajectory functionality so that it links events together that are part of a tour.

Further comment by corey.wallis@flinders.edu.au, Mar 04: [Issue 7](https://code.google.com/p/aus-e-stage/issues/detail?id=7) has been merged into this issue.
Changes to the AusStage schema to make full use of this feature will need to provide the ability to:
  * link events together to form a tour
  * link events together under an umbrella event
  * link umbrella events together to form a tour

### 9.	Increased documentation on KML functionality ###
Increased user focused documentation on the KML functionality is required where this functionality is made available.

This is prompted from some feedback from a user who said that the KML file in Google Earth didn't make any sense as when they used the time slider functionality they were "bounced around all over the map showing venues in no discernible order".

### 10.	Simplify venue browse map ###
There are too many placemarks on the Browse by Venue map by default and this can cause confusion, and can also make the interface intimidating. Investigation of some sort of clustering algorithm would be beneficial.

Further comment by corey.wallis@flinders.edu.au, Mar 04: Initial development of this map used the MarkerClusterer library available here: http://gmaps-utility-library-dev.googlecode.com/svn/tags/markerclusterer/. Unfortunately it had an unexpected impact on the generation of the map when the time slider functionality was added. Alternative clustering solutions will need to be investigated, for example limiting the browse to specific geographic areas.

### 11.	Venue Archaeology on maps ###
(->AusStage-Phase-4 consideration)
An enhancement to the mapping interface is the ability to view venue archaeology. In its most simple form this is the ability to see how a venue has evolved over time by tracking changes in name. For example a venue place marker could list events that have occurred at that venue broken up into separate lists for each stage of the venue following the changes in name that has occurred. A more advanced example is the ability to track the way a venue has changed over time from early open air venues, through to different venues on the same site, through the current venue. An example of this is the way the Adelaide Festival Centre has evolved over time.

### 12.	Historical Transport Overlays ###
To assist in research activities overlays of maps showing transport routes, roads, and railways at a specific point in history are going to be very valuable.

Further comment by corey.wallis@flinders.edu.au, Mar 04: Discussions have started with project partners on potential data that could be used to construct overlays such as this. From a project standpoint it is more appropriate that project partners provide the source material for these overlays and we work on assisting with the construction of the overlay and then integrate that overlay into the mapping systems that we produce. Additional issues will be added for specific overlays of this type.

### 13.	Construct ABS map overlays ###
Construct overlays using the Australian Bureau of Statistics Census data. This issue should be linked, using the blocking functionality in the issue tracking system to tasks that are related to the creation of these overlays.

### 14.	Map events based on genre and sub-genre ###
It would be really useful to map changing genres and sub-genres.. there is a lot of interest in the rise and fall of the musical, or the prevalence of comedies in the mainstream repertoire... or the shift from spoken word theatre to physical theatre in fringe seasons etc.

### 15.	Expand Analytics to cover AusStage database ###
It would be very useful to expand on the current analytics available on the Aus-e-Stage homepage to include a variety of analytical information about the AusStage database. For example:
  * Counts of various record types
  * Number of entries made over time
  * Highlight various issues with the system eg. venues with / without coordinate info

### 16.	Timeslider on maps ###
Need to make it clear that the time slider can be adjusted from either end of the slider.

### 17.	Investigate use of Dublin Core metadata ###
Investigate the use of Dublin Core Metadata in:
  * Web pages
  * maps
  * KML export files
  * KML files used for overlays
Could be useful as a means of providing a search / retrieval and indexing service for overlays that are produced.
More info: http://en.wikipedia.org/wiki/Dublin_Core

### 18.	Automatic Zoom level adjustment on maps ###
On the mapping interface it would be useful if the zoom level and centre of the map adjusted so that all markers were visible at a glance.  For example:
  * zooming out when overseas venues are displayed
  * zooming in when locality venues are display
  * centering the map on the region where venues are displayed

### 19.	Limit search by state/city/postcode ###
Searching for organisations/contributors/events should include an option to limit by state/city/postcode using the address details stored in the venue table of the database.

### 20.	Postcode overlay for maps ###
Provide an overlay using postcode divisions as a way of browsing a map and focusing the search results.  Similar to the ABS data overlay or the indigenous language overlay.

### 21.	Consistent navigation on left hand side of the page ###
Ensure that all pages in the mapping service have consistent navigation on the left hand side of the page.

### 22.	Include additional information on event links on maps ###
Add as a tooltip on the event links for a venue in the info-window popup the name of the organisation.  Investigate if a third tab should be added displaying organisations that have participated in events at the venue.