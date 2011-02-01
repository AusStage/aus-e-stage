//GLOBAL VARIABLES
//=======================================================
var vis = null;
var force;

//sizing 
var w = $(window).width() - 250,	//width of the focus panel = width of the window less padding for the sidebar AND slider on the left.
	h = $(window).height() - 94;  	//height of the focus panel
	
/* appearance variables */
var panelColor = "white";

var thickLine = 3;
var thinLine = 1.5;

var largeFont = "8 pt sans-serif";
var smallFont = "6 pt sans-serif";

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


var showAllContributors = false;	//set to true if related checkbox is checked. Will display all contributor labels
var showRelatedContributors = false;	//set to true if related checkbox is checked. Will display related contributor labels

//MOUSE OVER VARIABLES
var nodeIndexPoint = -1;	//stores the index of the node that the mouse is currently over. Used for mouse in/out. 
var edgeTIndexPoint = -1;  	//stores the target index of the edge that the mouse is currently over. Used for mouse in/out. 
var edgeSIndexPoint = -1;	//stores the source index of the edge that the mouse is currently over. Used for mouse in/out. 
 
//CLICK VARIABLES
var nodeIndex = -1;			//stores the INDEX of the selected NODE	 
var edgeTIndex = -1;		//stores the target index of the edge selected
var edgeSIndex = -1;		//stores the source index of the edge selected

// constants    
var EDGE = "edge";
var NODE = "node";
var CLEAR = "clear";

//PAGE SET UP
//=======================================================

$(document).ready(function() {
	
	//hide the main div
	$("#main").hide();
	
	//hide the show labels checkboxes
	$("#display_labels_div").hide();
	
	//set up accordion legend and hide it
	$(function() {
		$( ".accordion" ).accordion({collapsible: true,
									 clearStyle: true,
									 active: false});
	});

	$("#network_details_div").hide();
	$("#network_properties_div").hide();	
	
	// style the buttons
	$("button, input:submit").button();
	
	// disable network button
	$("#network_btn").button("disable");
		
	//clear search field values
	$("#id").val("");
	$("#name").val("");
	
	//set the focus to id
	$("#id").focus();
	
	//deal with window resizing	
	$(window).resize(function() {
	  windowResized();
	});
	
	//set up label on/off checkboxes for contributor names
	$("input[name=showAllContributors]").click(function() { 

		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showAllContributors]").is(":checked")){
			showAllContributors = true;	
    	}
    	else showAllContributors = false;
    	vis.render();
	}); 
	//set up label on/off checkboxes for contributor names
	$("input[name=showRelatedContributors]").click(function() { 

		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showRelatedContributors]").is(":checked")){
			showRelatedContributors = true;	
    	}
    	else showRelatedContributors = false;
    	vis.render();
	}); 

		
});


//SEARCH CONTRIBUTOR SET UP
//=======================================================
$(document).ready(function() {

	//define the lookup button
    $("#lookup_btn").click(function() {
	
	  //disable network button
	  $("#network_btn").button("disable");

      // validate and process form here
  	  var id = $("input#id").val();
  	  
  	  //define the api.
  	  var dataString = "http://beta.ausstage.edu.au/networks/lookup?task=collaborator&format=json&id="+id+"&callback=?";
  	  
  	  //if id has been entered check for a contributor 
  	  if (id.length > 0 ) {
	 
	  jQuery.getJSON(dataString, 
			function(data) {
				// check on what was returned
				if(data.name == "No Collaborator Found") {
					$("#name").val("Contributor with that id was not found");
				} else {
					
					// use the name to fill in the text box
					$("#name").val(data.name);
					$("#network_btn").button("enable");
					$("#network_btn").focus();
				}
			});
			
  	  } else {
  	  	$("#search_div").dialog('open');
  	  	
  	  }
	  return false;
    });
        
    // bind a focusout event to the id text field
	$("#id").focusout(function() {

		// get the content of the id text box	
		var val = $("#id").val();
		
		// trim the value
		val = val.replace(/^\s*/, "").replace(/\s*$/, "");

		// see if the value is an empty string
		if(val == "") {
			// if it is tidy the form
			$("#name").val("");
			$("#id").val("");
			
			// disable the network button
			$("#network_btn").button("disable");

		}
	});
	
	//focus in event on the name field. If clicked it automatically calls the lookup
	$("#name").focusin(function(){
		var val = $("#id").val(); 
		if (val == ""){
			$("#name").val("");
			$("#lookup_btn").trigger('click');
		}
			
	});
    
  	//handle enter in the search dialogue
	$("#query").keypress(function(event){
			if (event.keyCode == '13') {
    			 event.preventDefault();
    			 var i = $("#query").val();
    			 if (i == ""){
    			 	$("#query").val("please enter a contributor name or part of");
    			 	}
    			 else getContributors($("input#query").val());
   			}
	});	

    //setup the search contributors dialogue
	$("#search_div").dialog({ 
		autoOpen: false,
		height: 500,
		width: 650,
		modal: true,
		buttons: {
			'Search': function() {
				// search for the contributor
				 if ($("#query").val()==""){
				 	$("#query").val("please enter a contributor name or part of");
				 }else{
				 	getContributors($("input#query").val());
				 }
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

    	// validate and process form here
  		var id = $("input#id").val();
  		if (id == "") {
        	$("input#id").focus();
        	return false;
      	}
	 
	 	getNetworkAndDisplay(id); 
	 	
	 	//tidy up the form
	 	$("#name").val("");
		$("#id").val(""); 
     	return false;
    });
});    
    
function showLoader(type) {
	if(type == "show" || typeof(type) == "undefined") {
		// tidy up the search results
		$("#search_results_body").empty();
		$("#search_results").hide();
		//show the loading message
		$("#search_waiting").hide();
	} else {
		// hide the loading message
		$("#search_waiting").hide();
	}
}
 


// set up loading network dialogue box
$(document).ready(function() {
	// create the loading window and set autoOpen to false
	$("#network_loading").dialog({
		autoOpen: false,	// set this to false so we can manually open it
		draggable: false,
		closeOnEscape: false,
		width: 460,
		minHeight: 50, 
		modal: true,
		buttons: {}
	}); // end of dialog
});

// open and close network loading functions
function waitingDialog() { 
	$("#network_loading").dialog('open');
}
function closeWaitingDialog() {
	$("#network_loading").dialog('close');
}

//look for contributors
function getContributors(queryString){
	var dataString = "http://beta.ausstage.edu.au/networks/search?task=collaborator&query="+queryString+"&format=json&limit=5&callback=?";
	$("#search_waiting").show();
		  jQuery.getJSON(dataString, 
			function(data) {
				showLoader("hide");
				displayResults(data);
			});
	}

//display results in the search results div.	
function displayResults(results){
	//define helper constants
	var MAX_FUNCTIONS = 3;
	// tidy up the search results
	$("#search_results_body").empty();
	$("#search_results").hide();
	
	var html = "";
	var contributor;
	var functions;
	
	// loop through the search results
	for(i = 0; i < results.length; i++){
		contributor = results[i];
		// add the name and link
		html += '<tr><td><a href="' + contributor.url 
				+ '" target="ausstage" title="View the record for ' + contributor.name + ' in AusStage">' + contributor.name + '</a></td>';
		
		// add the list of functions
		html += '<td><ul>';
		
		functions = contributor.functions;
		
		for(x = 0; x < functions.length; x++) {
			if(x < MAX_FUNCTIONS) {
				html += '<li>' + functions[x] + '</li>';
			} else {
				html += '<li>...</li>';
				x = functions.length + 1;
			}
		}
		
		html += '</ul></td>';
				
		// add the button
		html += '<td><button id="choose_' + contributor.id + '" class="choose_button">Choose</button></td>';
		
		// finish the row
		html += '</tr>';
	}
	
	// check to see on what was built
	if(html != "") {
	
		// add the search results to the table
		$("#search_results_body").append(html);
	
		// hide the loader
		showLoader("hide");
	
		// style the new buttons
		$("button, input:submit").button();
		
		// add a function to each of the choose buttons
		$(".choose_button").click(function(eventObject) {

			// get the id of this button
			var id = this.id;
			
			var tags = id.split("_");
			
			// add the id to the text file
			$("#id").val(tags[1]);
			
			// close the dialog box
			$("#search_div").dialog("close");
			
			// execute the lookup function
			$("#lookup_btn").trigger('click');
		});
	
		// show the search results
		$("#search_results").show();
		
	} else {
		
		// hide the loader
		showLoader("hide");
	
		$("#error_message").empty();
		$("#error_message").append('<div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;"><p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span><strong>Warning:</strong> No contributors matched your search criteria. Please try again</p></div>');	
		$("#error_message").show();
	}
}


//DATA RETRIEVAL 
//=======================================================

function getNetworkAndDisplay(id){
   
   	//build the url
   	var dataString = "http://beta.ausstage.edu.au/networks/protovis?task=ego-centric-network&id="+id+"&radius=1&callback=?";

   	//display the loading bar
	waitingDialog();

	//query the api
	jQuery.getJSON(dataString, 
			function(data) {			
				// small fix for erroneous export, should be able to remove later
				if(!contains(data.nodes, null)){ 
					contributors = null;
					contributors = data;
					showGraph("protovis");
					showInteraction();
				}
				else{
					alert("Data for contributor "+id+" is corrupted. Please search for another contributor");
				}
				//hide the loading bar				
				closeWaitingDialog();
			}
	);
}



//GRAPH DISPLAY
//=======================================================
function showInteraction(){
		$("#main").show();
		$("#display_labels_div").show();
		$("#network_properties_div").show();		
}

function getNodes(){return contributors.nodes;}
function getEdges(){return contributors.edges;}
function getCentralNode(){return contributors.nodes.length-1;}

//create the network
function showGraph(targetDiv){
	if (contributors != null){
		
		prepareData();

		vis = new pv.Panel().canvas(targetDiv)
				.fillStyle(panelColor)
			  	.width(function(){return w})
			  	.height(function(){return h})
			  	.event("mousedown", pv.Behavior.pan())
			  	.event("mousewheel", pv.Behavior.zoom())
			  	.event("click", function(d) {onClick(null, CLEAR); return vis;});

		//add a force layout to the Panel. 
		force = vis.add(pv.Layout.Force)
			  	.nodes(getNodes())
			    .links(getEdges())
			    .bound(true)
			    .chargeConstant(-80)
			    .dragConstant(0.06)
			    .springConstant(0.05)
			    .chargeMaxDistance(600)			    
			    .springLength(150)
 			    .iterations(500)
 			    .transform(pv.Transform.identity.scale(1));

		//add the edges
  		force.link.add(pv.Line)
	  			.lineWidth(function(d,p){return getEdgeLineWidth(p)})
	  			.strokeStyle(function(d,p){return getEdgeStroke(p)})
	  			.event("click", function(d, p) {onClick(p, EDGE); return vis;})		
	  			.event("mouseover", function(d, p){	edgeTIndexPoint = p.target;
	  											 	edgeSIndexPoint = p.source;
	  											 	return vis})
	  			.event("mouseout", function(d, p){	edgeTIndexPoint = -1;
	  											 	edgeSIndexPoint = -1;
	  											 	return vis});
	  											 	
		//add the nodes
		force.node.add(pv.Dot)
			    .size(function(d){return (d.linkDegree + 1) * Math.pow(this.scale, -1.5)})
			    .fillStyle(function(d){return getNodeFill(d)})
			    .strokeStyle(function(d){return getNodeStroke(d)})
			    .lineWidth(function(d) {return getNodeLineWidth(d)})
			    
				.event("click", function(d) {onClick(d, NODE); return vis;})
			    .event("mouseover", function(d){nodeIndexPoint = d.index;
			    								return vis;})
			    .event("mouseout", function(d){nodeIndexPoint = -1;
			    								return vis;})
				.event("mousedown", pv.Behavior.drag())			    								
			    .event("drag", force)
    			.anchor("top").add(pv.Label)
    				.text(function(d){return d.nodeName})
    				.visible(function(d){return isVisible(d)})
    				.font(function(d){return getFont(d)})
    				.textStyle(function(d){return getTextStyle(d)});

		displayNetworkProperties();  
		displayPanelInfo(NODE);  				
    	vis.render();		
	}					    	
}

//INTERACTION FUNCTIONS - resize, zoom etc
//=======================================================

function windowResized(){
	  //reset the width and height
	  w = $(window).width() - 250;
	  h = $(window).height() - 94;	  
	  vis.render();	

}

//click functionality
function onClick(d, what){
	switch (what){
		case CLEAR: 
			nodeIndex = -1;					
			edgeTIndex = -1;
			edgeSIndex = -1;
			break;
		
		case EDGE: 
			nodeIndex = -1;	
			edgeTIndex = d.target;
			edgeSIndex = d.source;	
			break;
		
		case NODE: 
  	   	    nodeIndex = d.index;
			edgeTIndex = -1;
			edgeSIndex = -1;  	   	    
			break;
	}
	displayPanelInfo(what);				
}

//GRAPH APPEARANCE FUNCTIONS
//=======================================================

//label visibility
function isVisible(d){
	//if mouse is over the node
	if(d.index==nodeIndexPoint){return true}
	//if node is selected
	else if (d.index==nodeIndex){return true}
	//if mouse is over a related edge
	else if (d.index == edgeTIndexPoint || d.index == edgeSIndexPoint){return true}
	//if related edge is selected
	else if (d.index == edgeTIndex || d.index == edgeSIndex){return true}
	//if show all contributors button is selected
	else if (showAllContributors){return true}
	//if show related contributors is selected
	else if (showRelatedContributors && (contains(d.neighbors, nodeIndex))){return true}
	else return false;
}


//determine font
function getFont(d){
	
	//if current node index == index of object selected.			
	if (d.index == nodeIndexPoint){return largeFont;}
	//if current node index == index of object selected
	if (d.index == nodeIndex){return largeFont;}
	//if selected contributor is related to this node
	if (contains(d.neighbors, nodeIndex)){return largeFont;}
	//if mouse is over a related edge
	else if (d.index == edgeTIndexPoint || d.index == edgeSIndexPoint){return largeFont}
	//if related edge is selected
	else if (d.index == edgeTIndex || d.index == edgeSIndex){return largeFont}

	else 
		return smallFont;
}

function getTextStyle(d){
	//if node is selected
	if (d.index == nodeIndex){return selectedText;}
	//if node is related to selected node
	else if (contains(d.neighbors, nodeIndex)){return relatedText;}
	//if node is related to selected edge
	else if (d.index == edgeTIndex || d.index == edgeSIndex){return relatedText}
	else return unselectedText;
	
}


//line width
function getNodeLineWidth(d){
	switch(d.index){
		case nodeIndexPoint:
			return thickLine;
			break;
		case nodeIndex:
			return thickLine;
			break;
		default : return thinLine;
	}
}

function getNodeFill(d){
	//if node is selected
	if (d.index == nodeIndex){return selectedNode;}
	//if node is related to selected node
	else if (contains(d.neighbors, nodeIndex)){return relatedNode;}
	//if node is related to selected edge
	else if (d.index == edgeTIndex || d.index == edgeSIndex){return relatedNode}
	else return unselectedNode;
}

function getNodeStroke(d){
	//if node is selected
	if (d.index == nodeIndex){return selectedNodeBorder;}
	//if node is related to selected node
	else if (contains(d.neighbors, nodeIndex)){return relatedNodeBorder;}
	//if node is related to selected edge
	else if (d.index == edgeTIndex || d.index == edgeSIndex){return relatedNode}	
	else return unselectedNodeBorder;
}

function getEdgeLineWidth(p){
	var i = p.value;
	if (i != 1){i = i/2;}
	
	//if mouse is over this edge
	if(p.source == edgeSIndexPoint && p.target == edgeTIndexPoint){
		return i+2;		
	}
	if(p.source == edgeSIndex && p.target == edgeTIndex){
		return i+2;		
	}
	else return i;
}

function getEdgeStroke(p){
	//if the edge connects to the selected node
	if (p.source == nodeIndex || p.target == nodeIndex){
		return relatedEdge;
	}
	else if(p.source == edgeSIndex && p.target == edgeTIndex){
		return selectedEdge;	
	}
	else return unselectedEdge;
}

/* SIDE PANEL INFORMATION DISPLAY METHODS
====================================================================================================================*/
//display the network properties
function displayNetworkProperties(){	
		var collabCount = 0;
		for (i=0;i<contributors.edges.length;i++){
			collabCount = collabCount+contributors.edges[i].value;
		}
		var center = getCentralNode();
		var comma = "";	
		var contribUrl = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_contrib_id=";
	    var html = 	"<table>"+
	  				"<tr class=\"d0\"><td valign=top><b> Centre:</b></td><td>"+
	  				"<a href=" + contribUrl +""+ contributors.nodes[center].id+" target=\"_blank\">"+	
	  				contributors.nodes[center].nodeName+"</a><br> ";
    	for (var i=0; i<contributors.nodes[center].functions.length; i++){
    		html+= comma + contributors.nodes[center].functions[i];
    		comma = ", ";
    	}
    	html += "</td></tr>"+
    				"<tr class=\"d1\"><td><b>Contributors:</b></td><td> "+contributors.nodes.length+"</td></tr>"+					
					"<tr class=\"d0\"><td><b>Relationships:</b> </td><td>"+contributors.edges.length+"</td></tr></table>";			
					"<tr class=\"d1\"><td><b>Collaborations:</b> </td><td>"+collabCount+"</td></tr></table>";							
 		$("#network_properties").empty();
		$("#network_properties").append(html);

}

//display information about the selected element.
function displayPanelInfo(what){
	
	var comma = "";
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
	
	if (what == CLEAR){	html = " "; $("#network_details_div").hide();}
	else{$("#network_details_div").show();}
			
	
	//***************/
	//NODE
	if (what == NODE){
	
		//set the title to the contributor.
		titleHtml = contributors.nodes[nodeIndex].nodeName+" <p>";
    	// add the roles
    	for (var i=0; i<contributors.nodes[nodeIndex].functions.length; i++){
    		titleHtml+= comma + contributors.nodes[nodeIndex].functions[i];
    		comma = ", ";
    	}

    	titleHtml +="</p>";
		
		//create an array of related contributors, sort by last name
    	for (i = 0; i < contributors.nodes[nodeIndex].neighbors.length; i++){

    		var lastName = contributors.nodes[contributors.nodes[nodeIndex].neighbors[i]].nodeName.split(" ")[1];
    		contributorList[i] = {name:lastName, 
    					   		  index:contributors.nodes[contributors.nodes[nodeIndex].neighbors[i]].index}		
    	}
    	contributorList.sort(sortByName);    	
    	
		//build the table of contributors
		for(i = 0; i < contributorList.length; i++){
			comma = "";
			if(isEven(i)) tableClass = "d0";
			 else tableClass = "d1";

			html += "<tr class=\""+tableClass+"\"><td><a href=" 
								+ contributorUrl +""+ contributors.nodes[contributorList[i].index].id
								+" target=\"_blank\">"+
								contributors.nodes[contributorList[i].index].nodeName +"</a>";
			
			html +="<p>";
			 
			for (var x=0; x<contributors.nodes[contributorList[i].index].functions.length; x++){
    			html+= comma + contributors.nodes[contributorList[i].index].functions[x];
    			comma = ", ";
	    	}
			html+="</p></td></tr>";
		}
		html += "</table><br>";	  
		
	}
	

	//***************/
	//EDGE
/*	if (what == EDGE){
	
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
*/	
	$("#selected_object").append(titleHtml);    		
	$("#related_objects").append(html);    		

}


/* DATA MANIPULATION FUNCTIONS - manipulate retrieved data and prepare for visualisation.
====================================================================================================================*/

function prepareData(){
	
	//fix the central node in position. This is a fix to help the stability of the system	
	contributors.nodes[getCentralNode()].fix = new pv.Vector(w/2,h/2);
	//set the active node
	nodeIndex = getCentralNode();
	//set list of adjoining nodes for each node
	loadNeighbors();	
}

//adds extra array to each node storing neighboring node indexes
function loadNeighbors(){

	for(i = 0; i < contributors.nodes.length; i ++){

		//while we're already looping through the nodes array, best remove any double spaces
		contributors.nodes[i].nodeName = contributors.nodes[i].nodeName.replace("  ", " "); 
		
		contributors.nodes[i].neighbors = [];
		var neighbor_count = 0;
		
		for (x = 0; x < contributors.edges.length; x ++){
			if(contributors.edges[x].source == i){
				if (!contains(contributors.nodes[i].neighbors, contributors.edges[x].target)){
					contributors.nodes[i].neighbors[neighbor_count] = contributors.edges[x].target;
					neighbor_count++;
				}
			}
			if(contributors.edges[x].target == i){
				if (!contains(contributors.nodes[i].neighbors, contributors.edges[x].source)){
					contributors.nodes[i].neighbors[neighbor_count] = contributors.edges[x].source;
					neighbor_count++;
				}
			}			
		}			
	}	
}


//HELPER FUNCTIONS
//=======================================================
    
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

//sorting function
function sortByName(a, b) {
    var x = a.name.toLowerCase();
    var y = b.name.toLowerCase();
    return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}

