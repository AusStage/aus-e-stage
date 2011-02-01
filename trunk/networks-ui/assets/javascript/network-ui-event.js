
//global variables
//=======================================================

// panels 
var vis;
var focus;

// sizing and scales 
var spacer = 40;
var hSpacer = 45;
var w;
var	h;


var startDate;
var endDate;

var xAxis;
var yAxis;

/* appearance variables */
var panelColor = "white";

var thickLine = 3;
var thinLine = 1.5;

var fontHeight = 9;
var largeFont = "8 pt sans-serif";
var smallFont = "6 pt sans-serif";

var hoverEdge = "rgba(105, 105, 105, 0.5)";			//slate grey
var hoverText = "rgba(105, 105, 105, 0.5)";			//slate grey

var selectedEdge = "rgba(0,0,255,1)";				//blue
var selectedLabel = "rgba(0,0,255,1)";				//blue
var selectedNode = "rgba(0,0,255,1)";				//blue
var selectedNodeBorder = "rgba(0,0,255,1)";  		//blue
var selectedText = "rgba(0,0,255,1)";				//blue

var unselectedEdge = "rgba(170,170,170,0.7)";		//light grey
var unselectedLabel = "rgba(170,170,170,1)";			//light grey
var unselectedNode = "rgba(170,170,170,0.7)";		//light grey
var unselectedNodeBorder = "rgba(170,170,170,1)";	//light grey
var unselectedText = "rgba(170,170,170,1)";			//light grey

var relatedEdge = "rgba(46,46,46,0.7)";				//dark grey
var relatedLabel = "rgba(46,46,46,1)";				//dark grey
var relatedNode = "rgba(46,46,46,1)";				//dark grey
var relatedNodeBorder = "rgba(46,46,46,1)";			//dark grey
var relatedText = "rgba(46,46,46,1)";				//dark grey
	
//FACETED
var fPanelColor = "#333333"

var fSelectedEdge =	"rgba(255,255,255,0.9)"; 
var fSelectedNode = "rgba(170,170,170,1)";
var fSelectedNodeBorder = "white";
var fSelectedText = "rgba(170,170,170,1)";

var fUnselectedEdge = "rgba(170,170,170,0.2)";
var fUnselectedNode = "rgba(170,170,170,1)";
var fUnselectedNodeBorder = "rgba(170,170,170,1)";
var fUnselectedText = "rgba(170,170,170,1)";

var fRelatedEdge = "rgba(255,255,255,0.9)";
var fRelatedNode = "rgba(170,170,170,1)";
var fRelatedNodeBorder = "white";
var fRelatedText = "rgba(170,170,170,1)";

var highlightEdge = "rgba(255,255,255,0.9)";
var highlightNode = "blue";
var highlightNodeBorder = "blue";
var highlightText = "blue";
//-------------------END COLOR SCHEME 

//MOUSE OVER VARIABLES
var pointingAt;				//set ON MOUSE OVER. EDGE or NODE
var edgeIdPoint = -1;		//stores the id of the edge that the mouse is currently over. Used for mouse in/out. 	
var edgeIndexPoint = -1;	//stores the index of the edge that the mouse is currently over. Used for mouse in/out. 
var nodeIndexPoint = -1;	//stores the index of the node that the mouse is currently over. Used for mouse in/out. 

//CLICK VARIABLES
var dragIndicator = false;  //set to true on drag
var currentFocus = "";			//set ON CLICK. EDGE or NODE
var edgeId = -1;			//stored the ID of the selected EDGE
var edgeIndex = -1;			//stores the INDEX of the selected contributor - specific to the selected edge.
var nodeIndex = -1;			//stores the INDEX of the selected NODE	

//SHOW LABEL VARIABLES
var showContributors = false;	//set to true if related checkbox is checked. Will display all contributor labels
var showEvents = false;			//set to true if related checkbox is checked. Will display all event labels

// constants    
var EDGE = "edge";
var NODE = "node";
var CLEAR = "clear";



/*PAGE SET UP - set display style and hide elements etc
=======================================================================================================================*/
$(document).ready(function() {
	
	//set the width and height
	w = $(window).width() - ($("#sidebar").width()+spacer);

	h =  $(window).height() - ($("#header").height()+$("footer").height()+hSpacer);  	//height of the focus panel
	
	//set up legend
	$("#selected_object").hide();

	$("#related_objects").hide();

	$("#selected_object").click(function () {
		$(this).toggleClass("open");	
		$("#related_objects").slideToggle();
	}); 

	$("#network_properties_header").hide();

	$("#network_properties").hide();

	$("#network_properties_header").click(function () {
		$(this).toggleClass("open");	
		$("#network_properties").slideToggle();		
	}); 

	//hide the ruler div
	$("#ruler").hide();
	
	//hide the main div
	$("#main").hide();
	
	// style the buttons
	$("button, input:submit").button();
		
	//deal with window resizing	
	$(window).resize(function() {
	  windowResized();
	});
	
	//hide the show labels checkboxes
	$("#display_labels_div").hide();
	
	//hide the faceted browsing button
	$("#faceted_browsing_btn_div").hide();
			
	//clear search field values
	$("#id").val("");
	$("#name").val("");
	
	//set the focus to id
	$("#id").focus();
	
	//define the lookup button
    $("#lookup_btn").click(function() {
	  return false;
    });

	//set up network button    
	$("#network_btn").click(function() {
		showGraph("protovis");	
		windowResized();		
		showInteraction();
     	return false;
    });

    //set up label on/off checkboxes for contributor and event names
    $("input[name=showContributors]").click(function() { 

		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showContributors]").is(":checked")){
			showContributors = true;	
    	}
    	else showContributors = false;
    	vis.render();
	}); 

    $("input[name=showEvents]").click(function() { 

		//if checked, then set showEvents to true, else set to false;
    	if($("input[name=showEvents]").is(":checked")){
    		showEvents = true;	
    	}
    	else showEvents = false;
    	vis.render();
	}); 

		
});


/* DISPLAY FUNCTIONS - UPDATE/ALTER or LOAD THE GRAPH - 
   SHOW OR HIDE ELEMENTS
====================================================================================================================*/
//function to show hidden interaction elements
function showInteraction(){
		$("#main").show();
		$("#display_labels_div").show();	
		$("#network_properties_header").show();	

}

function showGraph(targetDiv){
	
	//format the data
	prepareData();

	// The root panel. 
	vis = new pv.Panel().canvas(targetDiv)
	.fillStyle(panelColor)
    .width(function(){return w})
    .height(function(){return h})
    .bottom(15);

	// X-axis and ticks. 
	vis.add(pv.Rule)
    	.data(function() {return xAxis.ticks()})
    	.left(xAxis)
			.anchor("bottom").add(pv.Label)
		    .text(xAxis.tickFormat);

	// Y-axis and ticks. 
	vis.add(pv.Rule)
    	.data(function() {return yAxis.ticks()})
	    .strokeStyle("lightgrey")
    	.top(yAxis)
			.anchor("left").add(pv.Label)
		    .text(yAxis.tickFormat);
    
	// Use an invisible panel to capture pan & zoom events. 
	vis.add(pv.Panel)
    	.events("all")
	    .event("mousedown", pv.Behavior.pan().bound(1))
	    .event("mousewheel", pv.Behavior.zoom(0.4).bound(1))
    	.event("pan", transformPan)
	    .event("zoom", transform)
	    .event("click", function(d) {onClick(null, CLEAR, null); return focus;})
		;

    
	//create new panel to hold the nodes and edges
	focus = vis.add(pv.Panel); 

	//add the edges
	for(var i = 0; i < events.edges.length; i++){
		focus.add(pv.Line)
			.data([events.edges[i].sourceNode, events.edges[i].targetNode])
			.left(function(d){return xAxis(events.nodes[d.index].startDate)})
			.top(function(d){return getLinePlacement(d)}) 
	        .strokeStyle(function(d){return getEdgeStyle(d)})
	        .lineWidth(function(d){return getLineWidth(d)}) 			
			//events
			.event("click", function(d) {onClick(d, EDGE, this); 
										 return focus;})    	    							 
			.event("mouseover", function(d) {	edgeIndexPoint = d.parentIndex;
												edgeIdPoint = events.edges[d.parentIndex].id;
												pointingAt = EDGE;
	  							 				return focus;})    	    
			.event("mouseout", function(d) {	edgeIndexPoint = -1;
												edgeIdPoint = -1;
												pointingAt = "";
	  							 				return focus;});  	  		 
	}//end for loop
    
   	// add a bar to the centre of each line. To act as a background for the labels
	focus.add(pv.Bar)	  							 				
		.data(events.edges)
	 	.top(function(d){ return getBarPlacement(d.sourceNode, this, "top")})	
		.left(function(d) { return getBarPlacement(d.sourceNode, this, "left")})
		.width(function(d){ return measureText(d.name)* Math.pow(this.scale,-0.75)})
		.height(function(){ return fontHeight* Math.pow(this.scale,-0.75)})		
		.fillStyle(function(d){return getEdgeLabelStyle(d)})
		.strokeStyle(function(d){return getEdgeLabelStyle(d)})
		.visible(function(d) {return isVisible(d.sourceNode, EDGE, this)})
		//add events		
		.event("click", function(d) {onClick(d.sourceNode, EDGE, this); 
									 return focus;})
		.event("mouseover", function(d) {	edgeIndexPoint = d.index;
											edgeIdPoint = d.id;
											pointingAt = EDGE;
	  						 				return focus;})    	   
		.event("mouseout", function(d) {	edgeIndexPoint = -1;
											edgeIdPoint = -1;
											pointingAt = "";
	  						 				return focus;})   										 
	  	//add the label to the box
	  	.anchor() 
		.add(pv.Label)
		.textStyle("white")
		.font(function(d){return getFont(d.sourceNode, EDGE, this)})
		.visible(function(d) {return isVisible(d.sourceNode, EDGE, this)})
		.text(function(d) {return d.name});
    
	//add the nodes 
	focus.add(pv.Dot)
    	.data(events.nodes)
	    .left(function(d) {return xAxis(d.startDate)})
    	.top(function(d) {return yAxis(d.top)})
		.fillStyle(function(d) {return getNodeFill(d, this)})
		.strokeStyle(function(d) {return getNodeStroke(d, this)})
		.lineWidth(function(d) {return getNodeLineWidth(d, this)})	
		.size(function(d){return Math.pow(((d.linkDegree)*2), 2)* Math.pow(this.scale, -2) })	
    	.event("mouseover", function() {	nodeIndexPoint = this.index;
											pointingAt = NODE;										
	  							 			return focus;}) 
    	.event("mouseout", function() {		nodeIndexPoint = -1;
											pointingAt = "";										
	  							 			return focus;}) 
		.event("click", function(d) { 		onClick(d, NODE, this);
									  		return focus; } )	  						
    	.event("mousedown", pv.Behavior.drag())
		.event("drag", function(d){dragNode(d); 
								   return vis;})
			//label
			.anchor("top").add(pv.Label)
			.text(function(d){ return d.nodeName})
			.font(function(d){ return getFont(d, NODE, this)})
			.textStyle(function(d){return getNodeTextStyle(d, this)})
			.visible(function(d){return isVisible(d, NODE, this)});


	//render the graph
	vis.render();
	//load the network properties
	displayNetworkProperties();  	

}




/* TRANSFORM FUNCTIONS - handles zoom, pan, drag, click and window resize events.
====================================================================================================================*/

//pan handler... ha! repeates exactly what zoom handler does, but changes the drag indicator. 
//Couldn't work out how to combine the two. stoopid
function transformPan(){
	dragIndicator = true;
	var t = this.transform().invert();
		xAxis.domain(xAxis.invert(t.x + xAxis(startDate) *t.k),xAxis.invert(t.x + xAxis(endDate) * t.k));				
		yAxis.domain(yAxis.invert(t.y + yAxis(0) *t.k), yAxis.invert(t.y + yAxis(events.nodes.length) * t.k));
	vis.render();
}

// zoom handler 
function transform() {
	
	var t = this.transform().invert();
		xAxis.domain(xAxis.invert(t.x + xAxis(startDate) *t.k),xAxis.invert(t.x + xAxis(endDate) * t.k));				
		yAxis.domain(yAxis.invert(t.y + yAxis(0) *t.k), yAxis.invert(t.y + yAxis(events.nodes.length) * t.k));
	vis.render();
}

//window resize handler
function windowResized(){

	  var t = focus.transform().invert();  	//get mouse position and scale 	
		  
	  //reset the width and height
	  w = $(window).width() - ($("#sidebar").width()+spacer);
	  h = $(window).height() - ($("#header").height()+$("footer").height()+hSpacer);

	  //reset the timeline range accordingly
	  xAxis.range(0, w);
	  yAxis.range(0, h);

	  vis.render();
	
}

//drag functionality
function dragNode(d){
	
	var y = focus.mouse().y;		//get mouse position
	d.top = yAxis.invert(y);		//update the node position		

	dragIndicator = true;

	vis.render();
}

//click functionality
function onClick(d, what, p){
	switch (what){
		case CLEAR: 
			if (!dragIndicator){
				currentFocus = "";
			    edgeId = -1;
			    edgeIndex = -1;
				nodeIndex = -1;
				resetLegend();					
			}else {
				what = currentFocus;
			} 
			dragIndicator = false;
			break;
		
		case EDGE: 
			currentFocus = EDGE;
		    edgeId = events.edges[d.parentIndex].id;
		    edgeIndex = d.parentIndex;
			nodeIndex = -1;		
			break;
		
		case NODE: 
			if (!dragIndicator){
				currentFocus = NODE;
				edgeId = -1;
				edgeIndex = -1;
  	   	   		nodeIndex = p.index;
			}else{
				what = currentFocus;
				console.log(currentFocus);
			}
			dragIndicator = false;
			break;
	}	
		if(currentFocus == ""){currentFocus = CLEAR;}
		displayPanelInfo(what);			
		windowResized();
}



/* GRAPH APPEARANCE FUNCTIONS - determine the fill, stroke and general appearance of marks
====================================================================================================================*/

// function to place a bar in the centre of each line for labelling, takes in d:the mark, p:the panel, s:switch string
function getBarPlacement(d, p, s){
	var from;
	var to;
	
	switch (s){
		
		case "top":
			from = yAxis(events.nodes[events.edges[d.parentIndex].source].top);
			to = yAxis(events.nodes[events.edges[d.parentIndex].target].top);
			
			if (events.edges[d.parentIndex].count == 1){ 
				return from + ((to-from)/2); break;
			} else{
				if(isEven(events.edges[d.parentIndex].count)){
					return from + ((to - from)/2)+((((events.edges[d.parentIndex].count)/2)*9)* Math.pow(p.scale, -0.75)); break;
				} else {
					return from + ((to - from)/2)-((((events.edges[d.parentIndex].count-1)/2)*9)* Math.pow(p.scale, -0.75)); break;
				}  	
 			}
 		
 		case "left":
 			from = xAxis(events.nodes[events.edges[d.parentIndex].source].startDate);
			to = xAxis(events.nodes[events.edges[d.parentIndex].target].startDate);
			return 	from +((to - from)/2);
			break;
	}
 }


// line placement method taking into account parallel lines
function getLinePlacement(d){
	
	if (events.edges[d.parentIndex].count == 1) {return yAxis(events.nodes[d.index].top)} 
	else { if(isEven(events.edges[d.parentIndex].count)){
				return yAxis(events.nodes[d.index].top) + ((((events.edges[d.parentIndex].count)/2)*3)* Math.pow(focus.scale, -0.75))
		 } else {
				return yAxis(events.nodes[d.index].top) - ((((events.edges[d.parentIndex].count-1)/2)*3)* Math.pow(focus.scale, -0.75))
		 }
	 }
}


// determine line width of edges
function getLineWidth(d){
	//if selected
	if (currentFocus == EDGE && events.edges[d.parentIndex].id == edgeId){
		return thickLine;
	}
	//if hovering
	if (events.edges[d.parentIndex].id == edgeIdPoint){
		return thickLine;
	}	
	return thinLine;
}

// returns color used for edges
function getEdgeStyle(d){
	//if contributor has been selected highlight all contributor edges
	if (events.edges[d.parentIndex].id == edgeId){
		return selectedEdge;
	}
	if (events.edges[d.parentIndex].id == edgeIdPoint){
		return hoverEdge;
	}
	//if node selected, highlight all attached edges.
	if (events.edges[d.parentIndex].source == nodeIndex ||events.edges[d.parentIndex].target == nodeIndex){
		return relatedEdge;
	}			
	return unselectedEdge;
}	

//return fill color for Label boxes
function getEdgeLabelStyle(d){
	//if contributor has been selected highlight all contributor edges
	if (d.id == edgeId){
		return selectedLabel;
	}
	 
	//if node selected, highlight all attached edges.
	if (d.source == nodeIndex || d.target == nodeIndex){
		return relatedLabel;
	}		
//	else
	return unselectedLabel;

}

// function to determine NODE fill color
function getNodeFill(d, p){

	//if selected contributor passes through this node
	if(contains(d.contributor_id, edgeId)){
		return relatedNode;
	}

	//if this node is selected
	if (p.index == nodeIndex){
		return selectedNode;
	}	
	//if this node is related
	if (contains(d.neighbors, nodeIndex)){	
		return relatedNode;
	}
	return unselectedNode;	
}

//function to determine NODE outline
function getNodeStroke(d, p){
	
	//if selected contributor passes through this node
	if(contains(d.contributor_id, edgeId)){
		return relatedNodeBorder;	
	}
	//if this node is selected
	if (p.index == nodeIndex){
		return selectedNodeBorder;
	}
	//if this node is related
	if (contains(d.neighbors, nodeIndex)){
		return relatedNodeBorder;
	}
	return unselectedNodeBorder;	
}

//determine the NODE outline width
function getNodeLineWidth(d, p){
	
	if (p.index == nodeIndexPoint){	//if current node index == index of object selected.
		return thickLine;
	}
	if (p.index == nodeIndex){		//if current node index == index of object selected
		return thickLine;
	}
	if (contains(d.contributor_id, edgeId)){	//if selected contributor has passed through this node
		return thickLine;
	}
	else return thinLine;	
}

//function to determine NODE outline
function getNodeTextStyle(d, p){
	
	//if selected contributor passes through this node
	if(contains(d.contributor_id, edgeId)){
		return relatedText;	
	}
	//if this node is selected
	if (p.index == nodeIndex){
		return selectedText;
	}
	//if this node is related
	if (contains(d.neighbors, nodeIndex)){
		return relatedText;
	}
	return unselectedText;
}


// function to determine if text is visible. Requires data :d and what: variable either NODE or EDGE
function isVisible(d, what, p){
	//for edges
	if (what == EDGE){
		if (showContributors){return true;}
		if(d.parentIndex == edgeIndexPoint){	//if current edge index == index of the object being pointed at.
			return true;	
		}
		else if(d.parentIndex == edgeIndex){	//if current edge index == index of object selected.
			return true;
		}	
	}
	//for Nodes
	if (what == NODE){	
		if (showEvents){return true;}
		if (p.index == nodeIndexPoint){	//if current node index == index of object selected.
			return true;
		}
		if (p.index == nodeIndex){		//if current node index == index of object selected
			return true;
		}
		if (contains(d.contributor_id, edgeId)){	//if selected contributor has passed through this node
			return true;
		}
		
	}	    
	else
	return false;	 
}

//determine font
function getFont(d, what, p){
	//for edges
	if (what == EDGE){
		
		if(d.parentIndex == edgeIndexPoint){	//if current edge index == index of the object being pointed at.
			return largeFont;	
		}
		
		if(d.parentIndex == edgeIndex){	//if current edge index == index of object selected.
			return largeFont;
		}
		else
			return smallFont;		
	}
	//for Nodes
	if (what == NODE){	
				
		if (p.index == nodeIndexPoint){	//if current node index == index of object selected.
			return largeFont;
		}
		if (p.index == nodeIndex){		//if current node index == index of object selected
			return largeFont;
		}
		if (contains(d.contributor_id, edgeId)){	//if selected contributor has passed through this node
			return largeFont;
		}
		else 
			return smallFont;
	}	    
	else
	return smallFont;
}


/* SIDE PANEL INFORMATION DISPLAY METHODS
====================================================================================================================*/
//display the network properties
function displayNetworkProperties(){
	if (centreNode!=-1){
			
		var eventUrl = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_event_id="; 		
	    var html = 	"<table>"+
	  				"<tr class=\"d0\"><td valign=top><b> Centre:</b></td><td>"+
	  				"<a href=" + eventUrl +""+ events.nodes[centreNode].id+" target=\"_blank\">"+	
	  				events.nodes[centreNode].nodeName+" ("+events.nodes[centreNode].id+")<br>"+
    				"</a> "+events.nodes[centreNode].venue+"</td></tr>"+
    				"<tr class=\"d1\"><td><b>Events:</b></td><td> "+events.nodes.length+"</td></tr>"+					
					"<tr class=\"d0\"><td><b>Contributors:</b> </td><td>"+contributorCount+"</td></tr></table>";			
 		$("#network_properties").empty();
		$("#network_properties").append(html);	
	}	
}


//display information about the selected element.
function displayPanelInfo(what){
	
	var eventUrl = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_event_id="; 
	var contributorUrl = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_contrib_id="
	var titleHtml = ""
	var html = "<table width=100%>";
	var dateFormat = pv.Format.date("%d %b %Y"); //create date formatter, format = dd mmm yyyy
	var tableClass = "";
	
	var contributorList = new Array();
	var eventList = [];
	
	//clear the info panel
	$("#selected_object").empty();
	$("#related_objects").empty();
	
	if (what == CLEAR){	
		html = " ";
		$("#selected_object").hide();
		$("#related_objects").hide();		
	}else{
		$("#selected_object").show();
	}
			
	
	//***************/
	//NODE
	if (what == NODE){

		//set the title to the event.
		titleHtml = "<a href=" + eventUrl +""+ events.nodes[nodeIndex].id+" target=\"_blank\">"+
										events.nodes[nodeIndex].nodeName+"</a><p>"+
										events.nodes[nodeIndex].venue+" "+
										dateFormat(events.nodes[nodeIndex].startDate)+"</p>";
		
		//create an array of related contributors, sort by last name
    	for (i = 0; i < events.nodes[nodeIndex].contributor_id.length; i++){

    		var lastName = events.nodes[nodeIndex].contributor_name[i].split(" ")[1];
    		contributorList[i] = {name:lastName,
    		 					  fullName: events.nodes[nodeIndex].contributor_name[i],
    					   		  id:events.nodes[nodeIndex].contributor_id[i]}		
    	}
    	contributorList.sort(sortByName);    	
		//create the list of contributors
		for(i = 0; i < contributorList.length; i++){
			if(isEven(i)) tableClass = "d0";
			 else tableClass = "d1";
			html += "<tr class=\""+tableClass+"\"><td><a href=" + contributorUrl +""+ contributorList[i].id+" target=\"_blank\">"+
								contributorList[i].fullName +"</a>"+
								"<p>"+"Role Not Yet Supported"+"</p></td></tr>" 	
		}
	  
		html += "</table><br>";	  
	}

	//***************/
	//EDGE
	if (what == EDGE){
	
		titleHtml = events.edges[edgeIndex].name+" <p>"+
    				events.edges[edgeIndex].role+			
					"</p>";		
	
		var x = 0;
	
		//create the list of events
			for(i = 0; i < events.nodes.length; i++){
					if (contains(events.nodes[i].contributor_id, edgeId)){
						eventList[x] = {event:"<a href=" + eventUrl +""+ events.nodes[i].id+" target=\"_blank\">"+
										events.nodes[i].nodeName+"</a><p>"+
										events.nodes[i].venue+" "+
										dateFormat(events.nodes[i].startDate)+"</p>"
						, startDate:events.nodes[i].startDate};
						x++;
					}
				}
				//sort events, earliest to latest
				eventList.sort(function(a, b){return a.startDate - b.startDate})		
				
		//create the html to display the info.
    	for( i = 0;i<eventList.length; i++ ){
    		if(isEven(i)) tableClass = "d0";
			else tableClass = "d1";
			
    		html += "<tr class=\""+tableClass+"\"><td>"+eventList[i].event+"</td></tr>"
    	}
    	
    	html+= "</table><br>";
		
	}
	
	$("#selected_object").append(titleHtml);    		
	$("#related_objects").append(html);    		

}



/* DATA MANIPULATION FUNCTIONS - manipulate retrieved data and prepare for visualisation.
====================================================================================================================*/
/*
at this stage the contributor_index array added to events.nodes is not needed. Have left it for now until the visualiser 
requirements are finalised.

STRUCTURE OF NODES==========================================================
events.nodes
			.id					//event id
			.nodeName			//name of the event
			.startDate			//starting date of the event.
			.venue				//event venue
			
	*****GENERATED WHEN GRAPH IS LOADED************	
	
			.linkDegreeTarget	//count of all links related to this node
			.contributor_index [] 	//generated array of all contributors associated with this event. within this network
			.contributor_id []	// generated array of all contributor id's associated with this event. within this network
			.contributor_name [] // generated array of all contributor names associated with this event. within this network 
			.neighbors[]		//generated array of nodes linked directly to this node			
			

END STRUCTURE OF NODES==========================================================
STRUCTURE OF EDGES==========================================================

events.edges
			.id			//id of the contributor
			.name		//name of the contributor
			.source		//index in the events.nodes array of the source node
			.target		//index in the events.nodes array of the target node
			
	*****GENERATED WHEN GRAPH IS LOADED************			
			.index 		//index of this element in the array. 
			.count		//default 1. if this is the second edge to have the same source/target = 2, third edge = 3 etc...
			.targetNode [index, parentIndex]  //index is the index of the node, parentIndex is the index of this edge
			.sourceNode [index, parentIndex]  //index is the index of the node, parentIndex is the index of this edge
									
END STRUCTURE OF EDGES==========================================================
*/



function prepareData(){

	//get the network statistics
	var counts = getStats();
	//get and set values for duplicate edges
	getDupEdges();
	//format the date field and create axis
	getDateAxis();
	//layout the nodes
	layout(counts[0], counts[1]);
}



function getStats(){
	var beforeCount = 0;
	var afterCount = 0;
	var isBefore = true;
	
	//get link degree for nodes. And set contrib_id array with associated contributors
	for(i = 0; i < events.nodes.length; i ++){
		
		//calculate number of nodes before and after the central node - used for layout method
		if(!isBefore){
			afterCount++;
		}
		if(events.nodes[i].central){
			isBefore = false;
			centreNode = i;
		}
		if (isBefore){
		beforeCount++;
		}
		
		// calculate node statistics, link degree, linking contributors, neighboring nodes
		events.nodes[i].linkDegree = 0;
		events.nodes[i].contributor_index = [];
		events.nodes[i].contributor_id = [];
		events.nodes[i].contributor_name = [];
		events.nodes[i].neighbors = [];
		
		var index_count = 0;
		var neighbor_count = 0;
		var id_count = 0;
		
		for (x = 0; x < events.edges.length; x ++){
			if(events.edges[x].source == i){
				events.nodes[i].linkDegree++;
				
				if (!contains(events.nodes[i].neighbors, events.edges[x].target)){
					events.nodes[i].neighbors[neighbor_count] = events.edges[x].target;
					neighbor_count++;
				}
				
				if (!contains(events.nodes[i].contributor_index, x)){
					events.nodes[i].contributor_index[id_count] = x;
					index_count++;
				}
				if (!contains(events.nodes[i].contributor_id, events.edges[x].id)){
					events.nodes[i].contributor_id[id_count] = events.edges[x].id;
					events.nodes[i].contributor_name[id_count] = events.edges[x].name;
					id_count++;
				}
				
			}
			if(events.edges[x].target == i){
				events.nodes[i].linkDegree++;
				
				if (!contains(events.nodes[i].neighbors, events.edges[x].source)){
					events.nodes[i].neighbors[neighbor_count] = events.edges[x].source;
					neighbor_count++;
				}

				if (!contains(events.nodes[i].contributor_index, x)){
					events.nodes[i].contributor_index[id_count] = x;
					index_count++;
				}
				if (!contains(events.nodes[i].contributor_id, events.edges[x].id)){
					events.nodes[i].contributor_id[id_count] = events.edges[x].id;
					events.nodes[i].contributor_name[id_count] = events.edges[x].name;
					id_count++;
				}

			}			
		}			
	}
	return [beforeCount, afterCount];	
}



function getDupEdges(){
	//get count of duplicate lines AND add x and y position of source and target nodes. 
	var tempArray = [];
	//loop through the edges.
		var edge = new Object();
		edge.source = events.edges[0].source;
		edge.target = events.edges[0].target;
		edge.count = 0;
		tempArray[tempArray.length] = edge;
		contribCount = [];
		
	for(i = 0; i< events.edges.length; i ++){

		//get count of individual contributors
		if(!contains(contribCount, events.edges[i].id)){
			contribCount.push(events.edges[i].id);	
		}
		
		
		events.edges[i].index = i;
			
		events.edges[i].count = 1;	
		//for each edge, 
		//look for a match in the temp array
		var noMatch = true;
		for(y = 0; y< tempArray.length; y ++){
			if (events.edges[i].source == tempArray[y].source){  
					if(events.edges[i].target == tempArray[y].target){
						tempArray[y].count = tempArray[y].count + 1;
						events.edges[i].count = tempArray[y].count;
						noMatch == false;
					}
			}
		}
		if (noMatch == true){ 
				edge = new Object();
				edge.source = events.edges[i].source;
				edge.target = events.edges[i].target;
				edge.count = events.edges[i].count;
				tempArray[tempArray.length] = edge;
		}
	//end count duplicate lines
	////////////////////

		//get the source and target x and y values and store in the edge data.
		events.edges[i].targetNode = {index: events.edges[i].target,
									  parentIndex: i};
		events.edges[i].sourceNode = {index: events.edges[i].source,
									  parentIndex: i};

	
	}
	contributorCount = contribCount.length;
	
}


function getDateAxis(){
	//format the startDate field to a date
	for(i = 0;i<events.nodes.length; i ++){
		events.nodes[i].startDate = new Date (events.nodes[i].startDate.substring(0,4),
											  events.nodes[i].startDate.substring(5,7)-1,
											  events.nodes[i].startDate.substring(7, 9));
	}

	//set the global start date to the min date less 12 months
	startDate = new Date (pv.min(events.nodes, function(d) {return d.startDate}));
	startDate.setMonth(startDate.getMonth()-11);

	//set the global end date to the max date plus 12 months
	endDate = new Date (pv.max(events.nodes, function(d) {return d.startDate}));	
	endDate.setMonth(endDate.getMonth()+11);

	//set the x and y axis scale.
	xAxis = pv.Scale.linear(startDate, endDate).range(0, w);
	yAxis = pv.Scale.linear(0, events.nodes.length).range(0, h);
	
}


function layout(beforeCount, afterCount){
	
	var range = events.nodes.length/2;
	var step;
	if (beforeCount > afterCount){
			step = (range)/beforeCount;
	}
	else step = (range)/afterCount;
	
	var alternate = 0;
	var before = true;
	var afterIndex = 2;
	var linkDegree = 0;
	var x = 1;
	
	for(i = 0;i<events.nodes.length; i ++){
		
		if (events.nodes[i].central){
			events.nodes[i].top = range;
			before = false;
		}
	
		if (before){
			switch (alternate){
				case 0: 
						if(linkDegree < events.nodes[i].linkDegree){
							events.nodes[i].top = range - (step*x);
							x++;
						}
						else{
							events.nodes[i].top = range - (step*i);
						}
	        				alternate = 1;
	        			break;
	        				
				case 1: 
						if(linkDegree < events.nodes[i].linkDegree){
							events.nodes[i].top = range + (step*x);
							x++;
						}
						else{
							events.nodes[i].top = range + (step*i);
						}
	    				alternate = 0; 
						break;
			}	
		}
	    
		if (!before && (!events.nodes[i].central)){
			switch (alternate){
				case 0: 
						if(linkDegree < events.nodes[i].linkDegree){
							events.nodes[i].top = range - (step*(x-afterIndex));
							x++;
						}
						else{						
							events.nodes[i].top = range - (step*(i-afterIndex));
						}
		        		alternate = 1;
		        		afterIndex = afterIndex + 2;
		        		break;
		        				
				case 1: 
						if(linkDegree < events.nodes[i].linkDegree){
							events.nodes[i].top = range + (step*(x-afterIndex));
							x++;
						}
						else{				
							events.nodes[i].top = range + (step*(i-afterIndex));
						}
		    			alternate = 0;
		        		afterIndex = afterIndex + 2;
						break;
			}	
		}
		
		linkDegree = events.nodes[i].linkDegree;
		
	}	

}


/*helper functions. Perform basic functions used regularly
====================================================================================================================*/

//quick function to check if an array contains a property	
function contains(a, obj){
	for(var i = 0; i < a.length; i++) {
    	if(a[i] == obj){
	      return true;
    	}
  	}
  	return false;
}

//determine if a number is odd or even returns true if even, false if odd
function isEven(a){
	
	var num = a/2;
	var inum = Math.round(num);
	
	if (num == inum) return true;
	else return false;	
	
}

//text measurement function to allow for solid backgrounds on labels.
function measureText(pText) {

 $("#ruler").empty(); 
 $("#ruler").append(pText);
 return $("#ruler").width();
 
}

//sorting function
function sortByName(a, b) {
    var x = a.name.toLowerCase();
    var y = b.name.toLowerCase();
    return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}

function resetLegend(){
	if ($("#selected_object").attr('class').indexOf("open") >=0){
		$("#selected_object").toggleClass("open");			
	}
}
