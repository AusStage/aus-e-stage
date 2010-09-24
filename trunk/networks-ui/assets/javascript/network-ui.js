

//style buttons

$(document).ready(function() {
	// style the buttons
	$("button, input:submit").button();
	
	// disable network button
	$("#network_btn").button("disable");	
	
	//hide the date range
	$("select#startDate").hide();
	$("select#endDate").hide();
	$("#date_range_div").hide();
	
	//clear search field values
	$("#id").val("");
	$("#name").val("");
		
});


//search area ////////////////////////////////////////////////////////////////////////////
// define lookup functionality /
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
			
			// disable the export button
			$("#network_btn").button("disable");
			$("#network_btn").focus();
		}
	});
    
    
    
    //setup the search contributors dialogue
	$("#search_div").dialog({ 
		autoOpen: false,
		height: 500,
		width: 650,
		modal: true,
		buttons: {
			Cancel: function() {
				$(this).dialog('close');
			},
			'Search': function() {
				// search for the contributor
				 getContributors($("input#query").val());
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
	 
	 	findAndDisplayContributorNetwork(id); 
	 	
	 	//tidy up the form
	 	$("#name").val("");
		$("#id").val(""); 
     	return false;
    });
    
    //set up date range refresh button
    $("#refresh_date_btn").click(function() {
    	$("#startDateStore").val( $("select#startDate").val()) ;
    	$("#endDateStore").val( $("select#endDate").val()) ;  
    	
		refreshNetworkForDateRange();
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


// SEARCH DIV functions.
// query dataset for contributor
// display returned results.

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
		html += '<tr><td><a href="' + contributor.url + '" target="ausstage" title="View the record for ' + contributor.name + ' in AusStage">' + contributor.name + '</a></td>';
		
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
		
		// add the contributor count
		html += '<td>' + contributor.collaborations + '</td>';
		
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


//Network display/////////////////////////////////////////////////////////////////////////////////////////

// NETWORK information search and display functions
// 	find and display contributor network,
//  load node neighbors (used in network interaction)
//  set date ranges for time slider

//define the function to find and contributor network based on id
function findAndDisplayContributorNetwork(id){
   
   //build the url
   var dataString = "http://beta.ausstage.edu.au/networks/protovis?task=ego-centric-network&id="+id+"&radius=1&callback=?";

   //display the loading bar
				waitingDialog();

	//query the api
	  jQuery.getJSON(dataString, 
			function(data) {
				contributors = null;
				contributors = data;
				
				//need to do date population before loading the graph
				//get the date ranges
				var dateRange = findDateRange(contributors.edges);

				//set the date ranges for the slider
				setDateRanges(dateRange);				
				
				//load the graph
				reloadNetwork("protovis")

				//hide the loading bar				
				closeWaitingDialog();
				//update the side panel with contributor infomation
				displayAllInfo(contributors.nodes.length-1,contributors);
				
				//display the slider
				$("#date_range_div").show(); 
				
			});
	}

//findDateRange - traverses the firstdate of contributors and gets the largest and smallest values to become our date range.
function findDateRange(edgeList){
	var startDate = edgeList[0].firstDate;
	var endDate = edgeList[0].firstDate;
	for (i = 1; i < edgeList.length; i++){
		if (startDate > edgeList[i].firstDate){
			startDate = edgeList[i].firstDate;			
			}
		if (endDate < edgeList[i].firstDate){
			endDate = edgeList[i].firstDate;
			}
		}
		return new Array(startDate, endDate);
	
	}


//loadNodeNeighbors takes the json data and loads each node as the first value in a 3d array. Any 
// associated nodes are then added into the second array. 
function loadNodeNeighbors(contributors){
				
	var nodeHash = new Array();
	var firstIndex=0;
	for(firstIndex; firstIndex < contributors.nodes.length; firstIndex++){
				
		var secondIndex = 0;	
		nodeHash[firstIndex] = new Array();
				
		for(var x=0; x<contributors.edges.length; x++){
			if (contributors.edges[x].source == firstIndex){	
				nodeHash[firstIndex][secondIndex]=contributors.edges[x].target;
				secondIndex++;						
			}
			if (contributors.edges[x].target == firstIndex){
				nodeHash[firstIndex][secondIndex]=contributors.edges[x].source;
				secondIndex++;
			}
		}
	}
	return nodeHash;
}

function setDateRanges(dateRange){
		var months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]
		var days = [31,28,31,30,31,30,31,31,30,31,30,31]
		//set the start and end dates
		var startDate = dateRange[0];
		var endDate = dateRange[1];
		
		var currYear = startDate.substring(0,4);
		var currMonth = startDate.substring(5,7);
		
		var endYear = endDate.substring(0,4);
		var endMonth = endDate.substring(5,7);		
		var startList = document.getElementById("startDate");
		var endList = document.getElementById("endDate");

		var firstRun = true;
		var finishPopulate = false;
		

		//clear date range select boxes
		while (startList.hasChildNodes()) {
 			 startList.removeChild(startList.firstChild);
		}
		while (endList.hasChildNodes()) {
 			 endList.removeChild(endList.firstChild);
		}

		for(currYear; currYear<=endYear; currYear++){ 
				//var yearGroup = document.createElement("optgroup")
				//var yearGroup2 = document.createElement("optgroup")						
				//yearGroup.label = currYear;
				//yearGroup2.label = currYear						
						
				if (!firstRun) currMonth = 1; 
						
				for(currMonth; currMonth<=12 ; currMonth++){
					if(!finishPopulate){
						var month = document.createElement("option");
						var month2 = document.createElement("option");							
							
						month2.value = (currYear+"-"+currMonth+"-"+days[currMonth-1]);
						month2.appendChild(document.createTextNode(months[currMonth-1]+"-"+currYear));							
						month.value = (currYear+"-"+currMonth+"-"+"01");
						month.appendChild(document.createTextNode(months[currMonth-1]+"-"+currYear));							
						if (firstRun){
								month.selected = true;
								firstRun = false; 
						}
						if (currYear == endYear && currMonth == endMonth){
								month2.selected = true;
								finishPopulate=true;
						}
				//		yearGroup.appendChild(month);
				//		yearGroup2.appendChild(month2);							
						startList.appendChild(month);
						endList.appendChild(month2);
					}
				}
//				startList.appendChild(yearGroup);
//				endList.appendChild(yearGroup2);
						
						
		}
		$("#startDateStore").val( $("select#startDate").val()) ;
    	$("#endDateStore").val( $("select#endDate").val()) ;
		
		// remove any existing slider
	    $('.ui-slider-scale').hide();
		$('.ui-slider').slider('destroy');
        
		$('select#startDate, select#endDate').selectToUISlider({
			labels: 6
		});	
		
}


//SIDE PANEL information display functions
//  display all info
//  	display network stats
//		display contributor info
//		display edge info 


    //displays overall network and selected contributor information
    function displayAllInfo(activeNode, contributors){
    	updateNetworkInfo(contributors);
    	updateInfoWindow(activeNode, contributors);
    	document.getElementById("networkCentreTitle").innerHTML = ("<b>Centre :</b><br>");
    	document.getElementById("networkCentre").innerHTML = (contributors.nodes[activeNode].nodeName+" ("+contributors.nodes[activeNode].id+")");
    	document.getElementById("networkCentre").href = contributors.nodes[activeNode].nodeUrl;
    	}
    
    //displays the network statistics in the side panel
    function updateNetworkInfo(contributors){
    	var networkDetails = document.getElementById("networkTitle");
	    	networkDetails.innerHTML = "<H3> Network Properties </H3>";
    	networkDetails = document.getElementById("nodeCount");
    		networkDetails.innerHTML = "<b> Node Count: </b>"+contributors.nodes.length;
    	networkDetails = document.getElementById("edgeCount");
    		networkDetails.innerHTML = "<b> Edge Count: </b>"+contributors.edges.length;
    	networkDetails = document.getElementById("eventCount");
    		networkDetails.innerHTML = "<b> Event Count: </b>Undefined.";
    	networkDetails = document.getElementById("diameter");
    		networkDetails.innerHTML = "<b> Diameter: </b>Undefined.";

    	}
    
    //displays information on the selected contributor in the side panel
    function updateInfoWindow(activeNode, contributors){
  
    //get contributor functions
    	var functionList = "";
    	for(i = 0;i<contributors.nodes[activeNode].functions.length; i++){
			functionList = functionList + contributors.nodes[activeNode].functions[i] + "<br>";
    	}
  
    //clear the window of previous information
    	clearInfoWindow("edge");

	//set the contributor name
    	var infoDetails = document.getElementById("contName");
    	infoDetails.innerHTML = "<br><b>"+contributors.nodes[activeNode].nodeName + 		" ("+contributors.nodes[activeNode].id+")</b>";
    	infoDetails.href = contributors.nodes[activeNode].nodeUrl;

    //set the functions
    	infoDetails = document.getElementById("contFunct");
    	infoDetails.innerHTML = "<br><b>Functions:</b><br>"+functionList;		
    	}


	//clear the side panel of edge or contributor information
	function clearInfoWindow(info){
		if (info == "node"){
			
	    //remove the contributor name
    		var infoDetails = document.getElementById("contName");
    		infoDetails.innerHTML = " ";
    		
    	//remove the functions
    		infoDetails = document.getElementById("contFunct");
    		infoDetails.innerHTML = " ";	
		}
		else if (info == "edge"){
		//remove the contributors	
			infoDetails = document.getElementById("collab1");
    		infoDetails.innerHTML = " ";
			infoDetails = document.getElementById("collab2");
    		infoDetails.innerHTML = " ";
		//remove the edge weight
			infoDetails = document.getElementById("noOfCollab");
    		infoDetails.innerHTML = " ";
    	//remove the date range	
			infoDetails = document.getElementById("eventRange");
    		infoDetails.innerHTML = " ";    		
		}
	}
    

	//displays information on the selected edge in the side panel    
    function updateInfoWindowEdge(edgeInformation){

		//clear the infoWindow
			clearInfoWindow("node");

		//set collaborator information
	    	var infoDetails = document.getElementById("collab1");
    		infoDetails.innerHTML = "<br><b>"+edgeInformation.sourceNode.nodeName + " ("+edgeInformation.sourceNode.id+")</b>";
    		infoDetails.href = edgeInformation.sourceNode.nodeUrl;
    		var infoDetails = document.getElementById("collab2");
    		infoDetails.innerHTML = "<b>"+edgeInformation.targetNode.nodeName + " ("+edgeInformation.targetNode.id+")</b>";
    		infoDetails.href = edgeInformation.targetNode.nodeUrl;

		//date range
			infoDetails = document.getElementById("eventRange");
    		infoDetails.innerHTML = "<br><b>Date Range: </b><br>" + edgeInformation.firstDate + " to " + edgeInformation.lastDate;
    		
		//set the edge weight
			infoDetails = document.getElementById("noOfCollab");
    		infoDetails.innerHTML = "<br><b>Number of collaborations: </b>" + edgeInformation.value;

    }
    
    
    
//small helper functions.
 	
    //quick function to check if an array contains a property	
	function contains(a, obj){
		for(var i = 0; i < a.length; i++) {
    		if(a[i] == obj){
		      return true;
    		}
  		}
  		return false;
	}
	
//global variables, to be used with protovis code.
//var contributors = null ;
var vis = null;

// define variables for colour options.
    	
	var bgColour = "white";
    		
	var activeLinkStroke = "rgba(0,0,255,1)";
    var relatedLinkStroke = "rgba(46,46,46,0.7)"; 
    var inactiveLinkStroke = "rgba(170,170,170,0.7)";
    var mouseoverLinkStroke = "rgba(220,20,60,0.7)";

    var linkColourHolder;

    var outOfDateEdge = "rgba(170,170,170,0.05)";
    var outOfDateNode = "rgba(170,170,170,0.1)";
        		
    var relatedNodeFill = "rgba(46,46,46,1)";
    var relatedNodeStroke = "rgba(46,46,46,1)";
    		
    var inactiveNodeFill = "rgba(170,170,170,1)";
    var inactiveNodeStroke = "rgba(170,170,170,1)";
    		
    var focusNodeFill = "blue";
    var focusNodeStroke = "blue";
			 
	var w = 796,
    	h = 600;

// define the active node
	var activeNode;
// define nodeFocus. True if focus is on nodes. False if on edges.
    var nodeFocus = true;    	

// define mouseOverEdge. True if mouse is over an edge.
    var mouseOver = false;
		
// store target and source node for edge focus operations
    var targetNodeSelect;
    var sourceNodeSelect;
    	
// store seperate target and source node info for mouse over operations.
    var targetNodeMO;
    var sourceNodeMO;	



function getNodes(){return contributors.nodes;}
function getEdges(){return contributors.edges;}



//create the network
function createNetwork(targetDiv){
	if (contributors != null){
    	
    	resetDateRangeVisibility();    		
    	////////////////////////////////////////////////////////	
		// construct a 3d array of nodes and their neighbors
			var nodeNeighbors = loadNodeNeighbors(contributors);

		///////////////////////////////////////////////////////
		// set the active node to the last in the array. -JSON data export puts the central node last.
		//fix the central node in position. This is a fix to help the stability of the system
			activeNode = contributors.nodes.length-1;
			
			contributors.nodes[activeNode].fix = new pv.Vector(w/2,h/2);

		///////////////////////////////////////////////////////
		// new panel. 
		// Essentially the base element 
		// for all protovis visualisation
			vis = new pv.Panel().canvas(targetDiv)
			    .width(w)
			    .height(h)
			    .fillStyle(bgColour)
			    .event("mousedown", pv.Behavior.pan())
			    .event("mousewheel", pv.Behavior.zoom());

		
		///////////////////////////////////////////////////////
		//add a force layout to the Panel. 
			var	force = vis.add(pv.Layout.Force)
			    .nodes(getNodes())
			    .links(getEdges())
			    .bound(true)
			    .dragConstant(0.05)
			    .springConstant(0.05)
 			    .iterations(500)
			    .transform(pv.Transform.identity.scale(3).translate(-w/3,-h/3)) 

				;
		
		/////////////////////////////////////////////////////////
		//add the edges
  		   force.link.add(pv.Line) 
  		   		.strokeStyle(function(d, p){return getLinkStrokeStyle(d, p) })
	/*		    .visible(function (d, p){
			    							if (($("#startDateStore").val().substring(0,p.firstDate.length)>p.firstDate)||
			    							($("#endDateStore").val().substring(0,p.firstDate.length)<p.firstDate)) {			    														
			    								return false; 
			    							}else 
			    							p.targetNode.visible = true;
			    							p.sourceNode.visible = true;
			    							return true;	
			    						})
	*/	    	

	//	    	.event("mouseover", function(d, p) {targetNodeMO = p.targetNode.index;
	//		    									sourceNodeMO = p.sourceNode.index;
	//		    									mouseOver = true;
	//		    									return vis;
	//	    										})
		    										
	//	    	.event("mouseout", function(d,p) {mouseOver = false;
	//	    									  targetNodeMO = -1;
	//	    									  sourceNodeMO = -1;
	//	    									  return vis;
	//	    									  })

			    .event("click", function(d, p) {updateInfoWindowEdge(p);
			    								targetNodeSelect = p.targetNode.index;
			    								sourceNodeSelect = p.sourceNode.index;
			    								targetNodeMO = -1;
			    								sourceNodeMO = -1;
			    								nodeFocus=false;
			    								vis.render();})
			;		
		//add the nodes
			force.node.add(pv.Dot)

			    .size(function(d){return (d.linkDegree + 1) * Math.pow(this.scale, -1.5)})

			    .fillStyle(function(d){ return getNodeFillStyle(d, this, nodeNeighbors)})

			    .strokeStyle(function(d) { return getNodeStrokeStyle(d, this, nodeNeighbors)})

			    .lineWidth(2)

	 		  	.event("mouseover", function(d) {targetNodeMO = d.index;
		    									mouseOver = true;
		    									return vis;})

		    	.event("mouseout", function() {mouseOver = false;
		    								   targetNodeMO = -1;
											   return vis;})

			    .event("mousedown", pv.Behavior.drag())

			    .event("drag", force)

			    .event("click", function(d) { activeNode = this.index;
    										  nodeFocus = true;
    										  updateInfoWindow(activeNode, contributors);
    										  return vis;})
    										  
    			.event("dblclick", function(d){return findAndDisplayContributorNetwork(contributors.nodes[this.index].id)})
    			.anchor("top").add(pv.Label).text(function(d){return d.nodeName})
    				.font("9pt sans-serif")
					.textStyle(	function(d) { return getNodeStrokeStyle(d, this, nodeNeighbors)})
					.textShadow("0.1em 0.1em 0.1em #fff")
					.visible(function(){return mouseOver && this.index == targetNodeMO || this.index == sourceNodeMO ? true :
										nodeFocus ? activeNode == this.index?true:false:
							    		targetNodeSelect == this.index || sourceNodeSelect == this.index ? true : false})
    			;



		
			}
					    	
}

//reload network and render
function reloadNetwork(targetDiv){
		createNetwork(targetDiv);
		
		vis.render();	
		
	}
	
//refresh network for date range	
function refreshNetworkForDateRange(){
	resetDateRangeVisibility();
	vis.render();	
	}

//resets all the visibility fields for nodes
function resetDateRangeVisibility(){

	for (var i = 0; i<contributors.nodes.length; i++){

		contributors.nodes[i].visible=false;
		}
	}


//functions to set the colour and style of nodes and links	

//link stroke style rules
function getLinkStrokeStyle(d, p){
	//mouse over stuff, not used anymore, but may come back
	if (($("#startDateStore").val().substring(0,p.firstDate.length)>p.firstDate)||
		($("#endDateStore").val().substring(0,p.firstDate.length)<p.firstDate)){
			return outOfDateEdge;
			}
	else{	
		p.targetNode.visible = true;
		p.sourceNode.visible = true;
		 if (mouseOver == true && p.targetNode.index == targetNodeMO && p.sourceNode.index == sourceNodeMO) {
			return mouseoverLinkStroke;
			} 
	//if the focus is on a node rather than a link
		else if (nodeFocus == true && (p.sourceNode.index == activeNode || p.targetNode.index == activeNode)){
			return relatedLinkStroke;
			}
	//if the focus is on edges
		else if (p.targetNode.index == targetNodeSelect && p.sourceNode.index == sourceNodeSelect){
			return activeLinkStroke;
			}
		else 
			return inactiveLinkStroke;	
	}	
}

//node fill style rules
function getNodeFillStyle(d, node, nodeNeighbors){
	if (!d.visible){
		return outOfDateNode;
	}
	else
	if (nodeFocus==true){
		if (activeNode== node.index){ 
			return focusNodeFill;
		}else if (contains(nodeNeighbors[activeNode], node.index)==true){
			return relatedNodeFill;	
		}
		else return inactiveNodeFill;		
	}
	else if (targetNodeSelect == node.index || sourceNodeSelect == node.index){
		return focusNodeFill;
	}
	else return inactiveNodeFill;	
}

//node stroke style rules
function getNodeStrokeStyle(d, node, nodeNeighbors){
	if (!d.visible){
		return outOfDateNode;
	}
	else
	if (nodeFocus==true){
		if (activeNode== node.index){ 
			return focusNodeStroke;
		}else if (contains(nodeNeighbors[activeNode], node.index)==true){
			return relatedNodeStroke;	
		}
		else return inactiveNodeStroke;		
	}
	else if (targetNodeSelect == node.index || sourceNodeSelect == node.index){
		return focusNodeStroke;
	}
	else return inactiveNodeStroke;		

}	