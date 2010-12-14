/*
things to fix.



- contributor info panel :  role

- event info panel : add - full list of contributors (color coded if not in the graph)

- proper 2nd degree network
- arc diagram

- window resize. - not playing nice with pan and zoom.
- pan and zoom not playing nice either.

LOOK AT - if PAN then Zoom, everything recentres
			if PAN then RESIZE everything moves incorrectly.





*/


//global variables
//=======================================================

/* panel */
var vis;
var focus;

/* scales */
var startDate;
var endDate;
var timeLine;
var yAxis;

/* sizing. */
var w = $(window).width() - 220,
	h = $(window).height() - 112;  	//height of the focus panel
    
/* constants */    
var EDGE = "edge";
var NODE = "node";
var CLEAR = "clear";

var centreNode = -1;	//holds the index for the central node. Set on prepareData();
var contributorCount = -1;

/* appearance variables */
var panelColor = "white";

var thickLine = 3;
var thinLine = 1.5;

var hoverEdge = "rgba(105, 105, 105, 0.5)";			//slate grey
var hoverText = "rgba(105, 105, 105, 0.5)";			//slate grey

var selectedEdge = "rgba(0,0,255,1)";				//blue
var selectedNode = "rgba(0,0,255,1)";				//blue
var selectedNodeBorder = "rgba(0,0,255,1)";  		//blue
var selectedText = "rgba(0,0,255,1)";				//blue


var unselectedEdge = "rgba(170,170,170,0.7)";		//light grey
var unselectedNode = "rgba(170,170,170,0.7)";		//light grey
var unselectedNodeBorder = "rgba(170,170,170,1)";	//light grey
var unselectedText = "rgba(170,170,170,1)";			//light grey

var relatedEdge = "rgba(46,46,46,0.7)";				//dark grey
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

//DATE RANGE
var outOfDateEdge = "rgba(170,170,170,0.05)";
var outOfDateNode = "rgba(170,170,170,0.1)";
    
//-------------------END COLOR SCHEME  

//network interaction variables

//MOUSE OVER VARIABLES
var pointingAt;				//set ON MOUSE OVER. EDGE or NODE
var edgeIdPoint = -1;		//stores the id of the edge that the mouse is currently over. Used for mouse in/out. 	
var edgeIndexPoint = -1;	//stores the index of the edge that the mouse is currently over. Used for mouse in/out. 
var nodeIndexPoint = -1;	//stores the index of the node that the mouse is currently over. Used for mouse in/out. 


//CLICK VARIABLES
var dragIndicator = false;	//set to true if dragged, to nullify onClick processing. Set to false after onClick processing
var currentFocus;			//set ON CLICK. EDGE or NODE
var edgeId = -1;			//stored the ID of the selected EDGE
var edgeIndex = -1;			//stores the INDEX of the selected contributor - specific to the selected edge.
var nodeIndex = -1;			//stores the INDEX of the selected NODE	

//SHOW LABEL VARIABLES
var showContributors = false;	//set to true if related checkbox is checked. Will display all contributor labels
var showEvents = false;			//set to true if related checkbox is checked. Will display all event labels

//DATA STORAGE VARIABLES
var beforeCount = 0;
var afterCount = 0;





/*page setup operations
==================================================
*/
//setup 
$(document).ready(function() {
	// style the buttons
	$("button, input:submit").button();
	
	
	//deal with window resizing	
	$(window).resize(function() {
	  windowResized();
	});
		
	//hide the interaction elements		
	//hide the date range
	$("select#startDate").hide();
	$("select#endDate").hide();
	$("#date_range_div").hide();
	//hide the show labels checkboxes
	$("#display_labels_div").hide();
	//hide the faceted browsing button
	$("#faceted_browsing_btn_div").hide();
	//hide the faceted browsing
	$("#faceted_browsing_div").hide();
	
	//clear search field values
	$("#id").val("");
	$("#name").val("");
	
	//set the focus to id
	$("#id").focus();
		
});


// define lookup functionality and all the buttons for search and display /
$(document).ready(function() {

	//define the lookup button
    $("#lookup_btn").click(function() {
	  return false;
    });
           
    //setup the search contributors dialogue
	$("#search_div").dialog({ 
		autoOpen: false,
		height: 500,
		width: 650,
		modal: true,
		buttons: {
			'Search': function() {
				 return false;
			},
			Cancel: function() {
				$(this).dialog('close');
				$("#id").focus();
				return false;
			}
		},
		open: function() {
		// tidy up the form on opening
			$("#search_results_body").empty();
			$("#search_results").hide();
			$("#error_message").hide();
			showLoader("hide");
		},
		close: function() {
			// tidy up the form on close
			$("#search_results_body").empty();
			$("#search_results").hide();
		}
	});
    

	//set up network button    
	$("#network_btn").click(function() {
		showGraph("protovis");	
		windowResized();		
		showInteraction();
     	return false;
    });
    
    //set up refresh button
    $("#refresh_date_btn").click(function() {
	    return false;
    });

    //set up browsing button
    $("#faceted_browsing_btn").click(function() {
	    return false;
    });

    //set up faceted browsing dialog
    $("#faceted_browsing_div").dialog({ 
		autoOpen: false,
		closeOnEscape: false,
		height: 600,
		width: 300,
		modal: false,
		position: ["right","top"],
		buttons: {
			"Refresh": function() {
				return false;
			},
			'Close': function(){
				return false;
			}
		},
		open: function(){
		},
	  	close: function() {
		}
	
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


//function to show hidden interaction elements
function showInteraction(){
	
		$("#display_labels_div").show();	

}



/*helper functions. Perform basic functions used regularly
==========================================================
*/
//quick function to check if an array contains a property	
function contains(a, obj){
	for(var i = 0; i < a.length; i++) {
    	if(a[i] == obj){
	      return true;
    	}
  	}
  	return false;
}

//NOT REQUIRED - AT PRESENT
//get angle of a line given two x and y points.
/*function getAngle(source, target){
	
	return ((Math.atan2(target.bottom - source.bottom, target.left - source.left))/(2*Math.PI))*360;

}


//using the angle, determine which axis should be modified for parallel lines
function chooseAxis(angle){
	
	if (angle > -45 && angle < 45){
		return("bottom");
	}
	else return("left");	

}
*/

//determine if a number is odd or even returns true if even, false if odd
function isEven(a){
	
	var num = a/2;
	var inum = Math.round(num);
	
	if (num == inum) return true;
	else return false;	
	
}


/*Data formatting functions. used to prepare/format data for graphing
==========================================================
*/


/* 	format the event data.

*	create a target link degree count and source link degree count for each node

*	calculate the number of edges that have the same source and target. Store these values in the edge information	

*/
/*

STRUCTURE OF NODES==========================================================
events.nodes
			.id					//event id
			.nodeName			//name of the event
			.startDate			//starting date of the event.
			.venue				//event venue
			
	*****GENERATED WHEN GRAPH IS LOADED************	
	
			.left				//x position
			.bottom				//y position
			.linkDegreeTarget	//count of all links that have this node as target
			.linkDegreeSource	//count of all links that have this node as source					
			.contributor_id [] 	//generated array of all contributors associated with this event. within this network
			.contributor_name[]
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
			.targetInfo	//information about the target node
						.left
						.bottom
						.toBottom
						.edgeInfo					//replication of all info above
									.id					
									.count
									.source
									.target
									.name
									.index 			
						
			.sourceInfo	//information about the source node
						.left
						.bottom
						.toBottom
						.edgeInfo
									.id
									.count
									.source
									.target
									.name
									.index
									
END STRUCTURE OF EDGES==========================================================
*/


function prepareData(){
	beforeCount = 0;
	afterCount = 0;
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
		events.nodes[i].linkDegreeTarget = 0;
		events.nodes[i].linkDegreeSource = 0;
		events.nodes[i].contributor_id = [];
		events.nodes[i].contributor_name = [];		
		events.nodes[i].neighbors = [];
		
		var id_count = 0;
		var neighbor_count = 0;
		
		for (x = 0; x < events.edges.length; x ++){
			if(events.edges[x].source == i){
				events.nodes[i].linkDegreeSource++;
				
				if (!contains(events.nodes[i].neighbors, events.edges[x].target)){
					events.nodes[i].neighbors[neighbor_count] = events.edges[x].target;
					neighbor_count++;
				}
				
				if (!contains(events.nodes[i].contributor_id, events.edges[x].id)){
					events.nodes[i].contributor_id[id_count] = events.edges[x].id;
					events.nodes[i].contributor_name[id_count] = events.edges[x].name;					
					id_count++;
				}
			}
			if(events.edges[x].target == i){
				events.nodes[i].linkDegreeTarget++;
				
				if (!contains(events.nodes[i].neighbors, events.edges[x].source)){
					events.nodes[i].neighbors[neighbor_count] = events.edges[x].source;
					neighbor_count++;
				}

				if (!contains(events.nodes[i].contributor_id, events.edges[x].id)){
					events.nodes[i].contributor_id[id_count] = events.edges[x].id;
					events.nodes[i].contributor_name[id_count] = events.edges[x].name;					
					id_count++;
				}
			}
					
		}			
	}
	
	//format the date field. this allows for scaling.
	for(i = 0;i<events.nodes.length; i ++){
		var year = events.nodes[i].startDate.substring(0,4); 
		var month = events.nodes[i].startDate.substring(5,7);
		var day = events.nodes[i].startDate.substring(7, 9);
		events.nodes[i].startDate = new Date (year, month-1, day);
	}
	
	startDate = new Date (pv.min(events.nodes, function(d) {return d.startDate}));
	endDate = new Date (pv.max(events.nodes, function(d) {return d.startDate}));

	/*create the scale for the x and y axis*/
	timeLine = pv.Scale.linear(startDate, endDate).range(50,w-50);
	yAxis = pv.Scale.linear(0, events.nodes.length).range(0,h-50);
	 
	layout(); //layou system for nodes
	
	//////////////////
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
		events.edges[i].targetInfo = {left:events.nodes[events.edges[i].target].left,
									  toLeft:events.nodes[events.edges[i].source].left,		
									  bottom:events.nodes[events.edges[i].target].bottom, 									  
									  toBottom:events.nodes[events.edges[i].source].bottom,
									  edgeInfo:{id:events.edges[i].id, count:events.edges[i].count, source:events.edges[i].source,
									  target:events.edges[i].target, name:"", index:events.edges[i].index}
									  };
		events.edges[i].sourceInfo = {left:events.nodes[events.edges[i].source].left,
									  toLeft:events.nodes[events.edges[i].target].left,
									  bottom:events.nodes[events.edges[i].source].bottom,
									  toBottom:events.nodes[events.edges[i].target].bottom,									  
									  edgeInfo:{id:events.edges[i].id, count:events.edges[i].count, source:events.edges[i].source,
									  target:events.edges[i].target, name:events.edges[i].name, index:events.edges[i].index}
									  };

	
	}
	contributorCount = contribCount.length;
	
}




/* display functions. Update/alter or load the graph
==========================================================
*/

function showGraph(targetDiv){


	
	prepareData();

	/* Root panel. */
	vis = new pv.Panel().canvas(targetDiv)
	   .width(function(){return w})
 	   .height(function(){return h})
 	   .bottom(20)
 	   //.left(30)
 	   //.right(20)
 	   //.top(5)
 	   .fillStyle(panelColor)
;

	/* X-axis ticks. */
	vis.add(pv.Rule)
	    .data(function() { return timeLine.ticks()})
	    .left(timeLine)
	    .strokeStyle("#eee")
			.anchor("bottom").add(pv.Label)
	    	.text(timeLine.tickFormat);

	/* Y-axis ticks. */
	vis.add(pv.Rule)
	    .bottom(0)
	    .strokeStyle("#aaa");

	/* Focus panel */
		 focus = vis.add(pv.Panel)
		.overflow("hidden")
    	.height(function() {return h})
    	.width(function(){return w})
    	.fillStyle("rgba(255,255,255,0.1)")
    	.event("mousemove", pv.Behavior.point())
    	.event("mousedown", pv.Behavior.pan())
		.event("mousewheel", pv.Behavior.zoom(1).bound(1))
		.event("pan", transform)
    	.event("zoom", transform)
    	.event("click", function(d) {onClick(null, CLEAR, null); 
										 return focus;})
		;


	/* add the lines */
	/* ===================================== */
	for (i=0;i<events.edges.length;i++){
		
		//var angle =	getAngle(events.edges[i].sourceInfo, events.edges[i].targetInfo);
		
		focus.add(pv.Line)
	        .strokeStyle(function(d){return getEdgeStyle(d.edgeInfo)})
	        .lineWidth(function(d){return getLineWidth(d.edgeInfo)}) 
			.data([events.edges[i].sourceInfo, events.edges[i].targetInfo])
			.bottom(function(d){
				if (d.edgeInfo.count == 1)  
					{return d.bottom} 
				else { 
						if(isEven(d.edgeInfo.count)){
							return d.bottom + ((((d.edgeInfo.count)/2)*3)* Math.pow(this.scale, -0.75))
						} else {
							return d.bottom - ((((d.edgeInfo.count-1)/2)*3)* Math.pow(this.scale, -0.75))
						}
					 }
				})
			.left(function (d){ 
					{return d.left} 
				})

			.event("click", function(d) {onClick(d, EDGE, this); 
										 return focus;})
    	    							 
			.event("mouseover", function(d) {	edgeIndexPoint = d.edgeInfo.index;
												edgeIdPoint = d.edgeInfo.id;
												pointingAt = EDGE;
	  							 				return focus;})    	    

			.event("mouseout", function(d) {	edgeIndexPoint = -1;
												edgeIdPoint = -1;
												pointingAt = "";
	  							 				return focus;})    	    	  							 			  	
	  							 										 
			.add(pv.Label)
 				.bottom(function(d) { if (d.edgeInfo.count == 1){ 
 									  	return d.bottom+ ((d.toBottom - d.bottom)/2)
 									  } else{
											if(isEven(d.edgeInfo.count)){
								return d.bottom + ((d.toBottom - d.bottom)/2)+((((d.edgeInfo.count)/2)*9)* Math.pow(this.scale, -0.75))
											} else {
								return d.bottom + ((d.toBottom - d.bottom)/2)-((((d.edgeInfo.count-1)/2)*9)* Math.pow(this.scale, -0.75))
											}  	
 									  }
 					})
 					
				.left(function(d) { return d.left + ((d.toLeft - d.left)/2)})
				.textStyle(function(d){return getEdgeTextStyle(d.edgeInfo)})
				.visible(function(d) {return isVisible(d.edgeInfo, EDGE, this)})
				.text(function(d) {return d.edgeInfo.name})														
			;
			
					
	}


	/* add the events. */
	/* ===================================== */

	focus.add(pv.Dot)
		.data(events.nodes)
		.fillStyle(function(d) {return getNodeFill(d, this)})
		.strokeStyle(function(d) {return getNodeStroke(d, this)})
//		.size(function(d){return (((d.linkDegreeSource + d.linkDegreeTarget) * 10)+30) * Math.pow(this.scale, -1.5)})		
		.size(function(d){return Math.pow(((d.linkDegreeSource + d.linkDegreeTarget)*2), 2)+30})	
		.left(function (d){ return d.left})
		.bottom(function(d){ return d.bottom})
	    
	    .event("mousedown", pv.Behavior.drag())
		.event("drag", function(d){dragNode(d, this); 
								   return vis;})
		
		.event("click", function(d) { 	onClick(d, NODE, this);
									  	return focus; } )
									  
		.event("mouseover", function() {	nodeIndexPoint = this.index;
											pointingAt = NODE;										
	  							 			return focus;})    	    
	  							 			  
		.event("mouseout", function() {		nodeIndexPoint = -1;
											pointingAt = "";										
	  							 			return focus;})    	    						 			  
	  							 			  
		.anchor("top").add(pv.Label)
			.text(function(d){ return d.nodeName})
			.textStyle(function(d){return getNodeTextStyle(d, this)})
			.visible(function(d){return isVisible(d, NODE, this)});
   
   	
   	
   	vis.render();
 	displayNetworkProperties();  	
   	

}

/* APPEARANCE METHODS
==========================================================
*/
//methods to determine appearance of the graph


//layout function determines the layout of nodes in the grid.
//set the x and y positions for nodes.
//very simple layout system. Assuming nodes are in order, alternate up and down on the baseline, 
//expanding to the central node then contract.


//NEED TO - alter layout, position should also take into account link degree, and move towards the centre based on how linked it is.
function layout(){

	var step;
	if (beforeCount > afterCount){
			step = (h/2)/beforeCount;
	}
	else step = (h/2)/afterCount;
	
	var alternate = 0;
	var before = true;
	var afterIndex = 2;
	
	for(i = 0;i<events.nodes.length; i ++){

		if (events.nodes[i].central){
			events.nodes[i].bottom = h/2;
			events.nodes[i].left = timeLine(events.nodes[i].startDate);
			before = false;
		}
	
		if (before){
			switch (alternate){
				case 0: events.nodes[i].bottom = h/2 - (step*i);
						events.nodes[i].left = timeLine(events.nodes[i].startDate); 
	        			alternate = 1;
	        			break;
	        				
				case 1: events.nodes[i].bottom = h/2 + (step*i);
						events.nodes[i].left = timeLine(events.nodes[i].startDate); 
	    				alternate = 0; 
						break;
			}	
		}
	    
		if (!before && (!events.nodes[i].central)){
			switch (alternate){
				case 0: events.nodes[i].bottom = h/2 - (step*(i-afterIndex));
						events.nodes[i].left = timeLine(events.nodes[i].startDate); 
		        		alternate = 1;
		        		afterIndex = afterIndex + 2;
		        		break;
		        				
				case 1: events.nodes[i].bottom = h/2 + (step*afterIndex);
						events.nodes[i].left = timeLine(events.nodes[i].startDate); 
		    			alternate = 0;
		        		afterIndex = afterIndex + 2;
						break;
			}	
		}

	}	
}


// determine line width of edges
function getLineWidth(d){

	//if selected
	if (currentFocus == EDGE && d.id == edgeId){
		return thickLine;
	}
	//if hovering
	if (d.id == edgeIdPoint){
		return thickLine;
	}
	
	return thinLine;
}



// returns color used for edges
function getEdgeStyle(d){
	

	//if contributor has been selected highlight all contributor edges
	if (d.id == edgeId){
		return selectedEdge;
	}
	
	if (d.id == edgeIdPoint){
		return hoverEdge;
	}
	
	//if node selected, highlight all attached edges.
	if (d.source == nodeIndex || d.target == nodeIndex){
		return relatedEdge;
	}
			
//	else
	return unselectedEdge;

}	


// returns color used for edges
function getEdgeTextStyle(d){
	

	//if contributor has been selected highlight all contributor edges
	if (d.id == edgeId){
		return selectedText;
	}
	
	//if node selected, highlight all attached edges.
	if (d.source == nodeIndex || d.target == nodeIndex){
		return relatedText;
	}
	
	if (d.id == edgeIdPoint){
		return relatedText;
	}

			
//	else
	return unselectedText;

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
		
		if(d.index == edgeIndexPoint){	//if current edge index == index of the object being pointed at.
			return true;	
		}
		
		else if(d.index == edgeIndex){	//if current edge index == index of object selected.
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



/* INTERACTION METHODS
==========================================================
*/
//methods for graph interaction
//click functionality
function onClick(d, what, p){
	if(!dragIndicator){
		switch (what){
		case CLEAR: 
			currentFocus = "";
		    edgeId = -1;
		    edgeIndex = -1;
			nodeIndex = -1;		
			break;
		
		case EDGE: 
			currentFocus = EDGE;
		    edgeId = d.edgeInfo.id;
		    edgeIndex = d.edgeInfo.index;
			nodeIndex = -1;		
			break;
		
		case NODE: 
			currentFocus = NODE;
			edgeId = -1;
			edgeIndex = -1;
	   	 nodeIndex = p.index;
			break;
		}
		
		displayPanelInfo(what);	
	}
	dragIndicator = false;	

}


//drag functionality
function dragNode(d, p){
	
	var y = focus.mouse().y;		//get mouse positions
	d.bottom = h-y;				//update the node position

	for(var i = 0; i < events.edges.length; i++){
		if (events.edges[i].source == p.index){
			events.edges[i].sourceInfo.bottom = d.bottom;
			events.edges[i].targetInfo.toBottom = d.bottom;
			
		}
		if (events.edges[i].target == p.index){
			events.edges[i].targetInfo.bottom = d.bottom;
			events.edges[i].sourceInfo.toBottom = d.bottom;
			
		}
		
	}	
	vis.render();
}

//this function is called when the browser window is resized. 
//
function windowResized(){
	
	   var t = focus.transform().invert();  	//get mouse position and scale 	
		  
	  //reset the width and height
	  w = $(window).width() - 220;
	  h = $(window).height() - 112;
	  
	  //reset the timeline range accordingly
	  timeLine.range(50,w-50);
	  
	  //reset the positioning of nodes accordingly.
	  for (var i = 0; i< events.nodes.length; i++){
		  events.nodes[i].left = t.x + timeLine(events.nodes[i].startDate) * t.k ;	
	  }




	//  layout();
	  	  
	  //reset the position of links accordingly.
	  for(var i = 0; i < events.edges.length; i++){
	  
	  //get the source and target x and y values and store in the edge data.
	  	events.edges[i].targetInfo.left = events.nodes[events.edges[i].target].left;
	  	events.edges[i].targetInfo.toLeft = events.nodes[events.edges[i].source].left;	  	
		events.edges[i].sourceInfo.left = events.nodes[events.edges[i].source].left;
		events.edges[i].sourceInfo.toLeft = events.nodes[events.edges[i].target].left;		
	 	events.edges[i].targetInfo.bottom = events.nodes[events.edges[i].target].bottom;
		events.edges[i].sourceInfo.bottom = events.nodes[events.edges[i].source].bottom;	  		  	
	  }

	  vis.render();
	
}

//transformation methods. To alter the domain of the scales on pan and zoom
//transform - for panning and zoom only
function transform() {

  var t = focus.transform().invert();  	//get mouse position and scale 
  var start = timeLine(startDate);	//convert startDate to scale 
  var end = timeLine(endDate);		//convert endDate to scale 
  
  var yStart = 0;
  var yEnd = h-50;

  dragIndicator = true;				//small hack - make sure that if dragged, the onClick function isn't processed as well.
  
  start = timeLine.invert(t.x + start *t.k);		//convert back to date (x position + domain * scale magnitude)
  end = timeLine.invert(t.x + end * t.k);			
  
  tStart = yAxis.invert(t.y + yStart * t.k);
  tEnd = yAxis.invert(t.y + yEnd * t.k);
  
  timeLine.domain(start, end);			//alter the domain
  yAxis.domain(yStart, yEnd);

  vis.render();

}



/* SIDE PANEL INFORMATION DISPLAY METHODS
==========================================================
*/
function displayNetworkProperties(){
	if (centreNode!=-1){
			
		var eventUrl = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_event_id="; 		

	
	    var html = 	"<br><table style=\"border-bottom: grey 1px solid; border-top:grey 1px solid;\" >"+
	    			"<tr><td colspan=2> <h3> Network Properties </h3></td> </tr>"+
	  				"<tr><td valign=top><b> Centre:</b><br> "+    				
						events.nodes[centreNode].nodeName+" ("+events.nodes[centreNode].id+")"+
    				"</a></td></tr>"+
    				"<tr><td><b>Events:</b></td><td> "+events.nodes.length+"</td></tr>"+					
					"<tr><td><b>Contributors:</b> </td><td>"+contributorCount+"</td></tr>";
    ;				
 		$("#network_details").empty();
		$("#network_details").append(html);
	
		
		
	}
	
	
}

function displayPanelInfo(what){
	
	var eventUrl = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_event_id="; 
	var contributorUrl = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_contrib_id="
	var html = "<br><table style=\"border-bottom: grey 1px solid; border-top:grey 1px solid;\" >";
	var dateFormat = pv.Format.date("%d %b %Y"); //create date formatter, format = dd mmm yyyy
	
	var contributorList = "";
	var eventList = [];
	
	//clear the info panel
	$("#selected_details").empty();
	
	if (what == CLEAR){html = " ";}
	
	//***************
	//NODE
	if (what == NODE){
		
		for(i = 0; i < events.nodes[nodeIndex].contributor_name.length; i++){
			contributorList += "<a href=" + contributorUrl +""+ events.nodes[nodeIndex].contributor_id[i]+" target=\"_blank\">"+
								events.nodes[nodeIndex].contributor_name[i] +" ("+events.nodes[nodeIndex].contributor_id[i]+")</a><br>" 	
		}
	  
		html += "<tr><td colspan=2>"+ 
    			"<a href="+eventUrl+""+events.nodes[nodeIndex].id+" target=\"_blank\"><b>"+
    			events.nodes[nodeIndex].nodeName+"</b></a>"+
    			"</td></tr>"+																//name of event link to ausstage
    			"<tr><td><b>Venue:</b> "+events.nodes[nodeIndex].venue+"</td></tr>"+			//venue
    			"<tr><td valign=top>"+
    			"<b>First Date: </b>"+												//list of contributors as link
    			dateFormat(events.nodes[nodeIndex].startDate)+"</td></tr>"+
    			"<tr><td valign=top>"+
    			"<b>Contributors (in network): </b><br>"+												//list of contributors as link
    			contributorList+"</td></tr>"+
    			"</table>";	  
	}
	
	
	//***************
	//EDGE
	if (what == EDGE){
		
	
	var x = 0;
	
			//create the list of events
				for(i = 0; i < events.nodes.length; i++){
					if (contains(events.nodes[i].contributor_id, edgeId)){
						eventList[x] = {event:"<a href=" + eventUrl +""+ events.nodes[i].id+" target=\"_blank\">"+
										events.nodes[i].nodeName+" ("+events.nodes[i].id+")</a><br>"
						, startDate:events.nodes[i].startDate};
						x++;
					}
				}
				//sort events, earliest to latest
				eventList.sort(function(a, b){return a.startDate - b.startDate})		
				
		//create the html to display the info.
		html += "<tr><td colspan=2>"+ 
    			"<a href="+contributorUrl+""+edgeId+" target=\"_blank\"><b>"+
    			events.edges[edgeIndex].name + " (" + events.edges[edgeIndex].id+ ") " +	 "</b></a>"+
    			"</td></tr>"+																//name of event link to ausstage
    			"<tr><td valign=top>"+
    			"<b>Events (in network): </b><br>";
    	for( i = 0;i<eventList.length; i++ ){
    		html += eventList[i].event
    	}
    	
    	html+= "</td></tr></table>";
		
	}

	$("#selected_details").append(html);    		

}