

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


//search area ////////////////////////////////////////////////////////////////////////////
// define lookup functionality and all the buttons for search and display /
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
	 
	 	findAndDisplayContributorNetwork(id); 
	 	
	 	//tidy up the form
	 	$("#name").val("");
		$("#id").val(""); 
     	return false;
    });
    
    //set up refresh button
    $("#refresh_date_btn").click(function() {
    	$("#startDateStore").val( $("select#startDate").val()) ;
    	$("#endDateStore").val( $("select#endDate").val()) ;  
		refreshNetwork("dateRange");
	    return false;
    });

    //set up browsing button
    $("#faceted_browsing_btn").click(function() {
		$("#faceted_browsing_div").dialog("open");
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
				reloadFacetedSelection();
				refreshNetwork("browse");
				return false;
			},
			'Close': function(){
				$(this).dialog('close');
				return false;
			}
		},
		open: function(){
		$("#refresh_date_btn").button("disable");	
		browseTrigger = true;
		refreshNetwork("browse");
		},
	  	close: function() {
		$("#refresh_date_btn").button("enable");	
		browseTrigger= false;
		refreshNetwork("dateRange");
		}
	
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
//		html += '<td>' + contributor.collaborations + '</td>';
		
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
				
				// small fix for erroneous export, should be able to remove later
				if(!contains(data.nodes, null)){ 
					
					contributors = null;
					contributors = data;
					
					//need to do date population before loading the graph
					//get the date ranges
					var dateRange = findDateRange(contributors.edges);
	
					//set the date ranges for the slider
					setDateRanges(dateRange);		

					//find all functions for faceted browsing
					setFacetedOptions(getFacetedOptions(contributors.nodes));		

					//display the faceted Options
					$("#faceted_browsing_div").show();
				
					//load the graph
					reloadNetwork("protovis")

					//update the side panel with contributor infomation
					displayAllInfo(contributors.nodes.length-1,contributors);
				
					//display the slider
					$("#date_range_div").show(); 
					//display the faceted browsing button
					$("#faceted_browsing_btn_div").show();
				}
				else{
					alert("Data for contributor "+id+" is corrupted. Please search for another contributor");
				}
				//hide the loading bar				
				closeWaitingDialog();

			});
	}

//Faceted Browsing operations.//////////////////////////

//get all functions from the contributor list.
function getFacetedOptions(nodeList){
	
	//function variables
	var functionList = [""];
	var currFunction;
	var functionIndex = 0;
	
	//gender variables
	var genderList = [""];
	var currGender;
	var genderIndex = 0;
	
	//nationality variables
	var nationalityList = [""];
	var currNationality;
	var nationalityIndex = 0;
	var returnArray = [""];

	//loop through the nodelist.
	for (i=0; i<nodeList.length; i++){
		
		//find unique genders. if null set to unknown
		currGender = nodeList[i].gender;
		if (currGender == null){
			currGender = "Unknown";
		}
		
		if (contains(genderList, currGender) == false){
			genderList[genderIndex] = currGender;
			genderIndex++;
		}
		
		//find unique nationality. If null set to unknown
		currNationality = nodeList[i].nationality;
		if (currNationality == null){
			currNationality = "Unknown";
		}
		if (contains(nationalityList, currNationality) == false){
			nationalityList[nationalityIndex] = currNationality;
			nationalityIndex++;
		}
						
		//loop through the function list. 
		for (x=0; x<nodeList[i].functions.length; x++){	
		
			//if - this function is not in function list, add it to the function list.
			currFunction = nodeList[i].functions[x];

			if (contains(functionList, currFunction ) == false){
				functionList[functionIndex] = currFunction;

				functionIndex++;
			}
		//else do nothing
		}
	}

	returnArray[0] = functionList;
	returnArray[1] = genderList;
	returnArray[2] = nationalityList;
	
	return returnArray;
}


//set the select box with functions
function setFacetedOptions(facetedList){
	
	var functionList = facetedList[0];
	var genderList = facetedList[1];
	var nationalityList = facetedList[2];

	//clear the functions list
	$("#faceted_function_div").empty();
	//clear the gender list
	$("#faceted_gender_div").empty();
	//clear the nationality list
	$("#faceted_nationality_div").empty();
	
	//sort and create the function checkboxes
	functionList.sort();	
	for(i = 0; i<functionList.length; i++){
	$("#faceted_function_div").append('<input type="checkbox" id="function" value="'+functionList[i]+'" /> <label for="'+i+'">'+functionList[i]+
	'</label><br>');
	}	

	
	//sort and create the gender checkboxes
	genderList.sort();	
	for(i = 0; i<genderList.length; i++){
	$("#faceted_gender_div").append('<input type="checkbox" id="gender" value="'+genderList[i]+'" /> <label for="'+i+'">'+genderList[i]+
	'</label><br>');
	}	

	//sort and create the nationality checkboxes
	nationalityList.sort();	
	for(i = 0; i<nationalityList.length; i++){
	$("#faceted_nationality_div").append('<input type="checkbox" id="nationality" value="'+nationalityList[i]+'" /> <label for="'+i+'">'+nationalityList[i]+
	'</label><br>');
	}	

	//restrict gender to only one check box selected.
	$('#faceted_gender_div input:checkbox').exclusiveCheck();

}

	//update the display to show what's been selected
	function reloadFacetedSelection() {
		
		$("#faceted_selection_p").empty();
		var displayStr = "";
		var startStr = "<b>Contributors who are: </b><br>";
		var middleStr = "<br><b>and </b>";
		var middleOrStr = "<br><b>or </b>";
		var functionStr = "";
		var genderStr = "";
		var nationalityStr = "";
		
		if($("input:checked").length>0){
	         displayStr = startStr;         
		}
         
        $("input#function:checked").each(function (index, value) {
               functionStr += value.value + "<br>";
        });
        
        if(functionStr.length !=0){
        	functionStr += " ";
        }
        
        //remove the last - and replace with s
		functionStr = functionStr.replace("<br> ","s ");

        
        $("input#gender:checked").each(function (index, value) {
               genderStr += value.value + " ";
        });
        
        $("input#nationality:checked").each(function (index, value) {
               nationalityStr += value.value + " "+ middleOrStr;
        });
        
        if(nationalityStr.length!=0){
        	nationalityStr+= " ";
        }
        nationalityStr = nationalityStr.replace(middleOrStr+" "," ");

		
		displayStr += functionStr;
		
		if(functionStr.length !=0 && genderStr.length != 0){
			displayStr += middleStr;			
		}
		displayStr += genderStr;
		
		if((functionStr.length !=0 && genderStr.length != 0 && nationalityStr.length !=0)||
			(functionStr.length == 0 && genderStr.length !=0 && nationalityStr.length !=0)||
			(functionStr.length !=0 && genderStr.length ==0 && nationalityStr.length !=0) ){
				displayStr += middleStr;
		}
		displayStr += nationalityStr;
		
                     
        $("#faceted_selection_p").append(displayStr);
   };




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


// sets the slider values.
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

				if (!firstRun) currMonth = 1; 
						
				for(currMonth; currMonth<=12 ; currMonth++){
					if(!finishPopulate){
						var month = document.createElement("option");
						var month2 = document.createElement("option");							
							
						month2.value = (currYear+"-"+currMonth+"-"+days[currMonth-1]);
						month2.appendChild(document.createTextNode(months[currMonth-1]+"_"+currYear));							
						month.value = (currYear+"-"+currMonth+"-"+"01");
						month.appendChild(document.createTextNode(months[currMonth-1]+"_"+currYear));							
						if (firstRun){
								firstRun = false; 
						}
						if (currYear == endYear && currMonth == endMonth){
								finishPopulate=true;
						}
						startList.appendChild(month);
						endList.appendChild(month2);
					}
				}
						
						
		}
		// select the last and first options before building the time slider
        $("#startDate option:first").attr("selected", "selected");
        $("#endDate option:last").attr("selected", "selected");

		
		$("#startDateStore").val( $("select#startDate").val()) ;
    	$("#endDateStore").val( $("select#endDate").val()) ;
		
		// remove any existing slider
	    $('.ui-slider-scale').hide();
		$('.ui-slider').slider('destroy');
        
		$('select#startDate, select#endDate').selectToUISlider({
			labels: 6,
			labelSrc:"text"
		});	
		
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



//SIDE PANEL information display functions
//  display all info
//  	display network stats
//		display contributor info
//		display edge info 


    //displays overall network and selected contributor information
    function displayAllInfo(activeNode, contributors){
    	updateNetworkInfo(contributors);
    	updateSelectedInfo(activeNode, contributors, "nodes");
    	}
    
    //displays the network statistics in the side panel
function updateNetworkInfo(contributors){
		var eventCount = 0;
		for (i=0;i<contributors.edges.length;i++){
			eventCount = eventCount+contributors.edges[i].value;
		}
		
	    var html = 	"<br><table style=\"border-bottom: grey 1px solid; border-top:grey 1px solid;\" >"+
	    			"<tr><td colspan=2> <h3> Network Properties </h3></td> </tr>"+
    				"<tr><td valign=top><b> Centre:</b></td><td> <a href ="+contributors.nodes[activeNode].nodeUrl.replace("amp;", "")+
    				" target=\"_blank\">"+	    
					contributors.nodes[activeNode].nodeName+" ("+contributors.nodes[activeNode].id+")"+    				
    				"</a></td></tr>"+
    				"<tr><td><b>Contributors:</b></td><td> "+contributors.nodes.length+"</td></tr>"+					
					"<tr><td><b>Relationships:</b> </td><td>"+contributors.edges.length+"</td></tr>"+
					"<tr><td><b>Collaborations:</b> </td><td>"+eventCount+"</td></tr>";
    				
 		$("#network_details").empty();
		$("#network_details").append(html);

}
    
    //displays information on the selected contributor in the side panel
function updateSelectedInfo(activeNode, contributorArray, ui_focus){
    //get contributor functions
    var functionList = "";
    var html = "<br><table style=\"border-bottom: grey 1px solid; border-top:grey 1px solid;\" >";
    
    //clear the window of previous information
    $("#selected_details").empty(); 

    if (ui_focus == "nodes"){	  	
    	for(i = 0;i<contributorArray.nodes[activeNode].functions.length; i++){
			functionList = functionList + contributorArray.nodes[activeNode].functions[i] + "<br>";
    	} 
    	html += 	"<tr><td colspan=2>"+ 
    			"<a href="+contributorArray.nodes[activeNode].nodeUrl.replace("amp;", "")+" target=\"_blank\"><b>"
    			+contributorArray.nodes[activeNode].nodeName +" ("+contributorArray.nodes[activeNode].id+")</b></a>"+
    			"</td></tr>"+
    			"<tr><td valign=top>"+
    			"<b>Functions: </b><br>"+
    			functionList+"</td></tr>"+
    			"<tr><td><b>Relationships:</b> "+nodeNeighbors[activeNode].length+"</td></tr>"
    			"</table>";

    }
    else if(ui_focus =="link"){
		
    	
    	html +=  "<tr><td><a href="+contributorArray.sourceNode.nodeUrl.replace("amp;", "")+"><b>"+contributorArray.sourceNode.nodeName+
				" ("+contributorArray.sourceNode.id+")</b></a>"+
    			"</td></tr><tr><td><a href="+contributorArray.targetNode.nodeUrl.replace("amp;", "")+"><b>"+contributorArray.targetNode.nodeName+
				" ("+contributorArray.targetNode.id+")</b></a></td></tr>"+
				"<tr><td><b>Date Range: </b></td></tr><tr><td>"+formatDate(contributorArray.firstDate) + " to " + formatDate(contributorArray.lastDate)+"</td> </tr>"+
				"<tr><td><b>Events: </b>"+contributorArray.value+"</td></tr>" ;
    }

		$("#selected_details").append(html);    		
}
//small helper functions.

	// formatDate function, takes in Ausstage format date, and spits out date string as dd mmm yyyy
	// ASSUMES date input as YYYY-MM-DD
	function formatDate(d){
		
		var monthNames = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];
		var dateToken = d.split("-");
		var day = dateToken[2];
		var month = monthNames[dateToken[1]-1];
		var year = dateToken[0];

		return day+" "+month+" "+year;		
	}
	 	
    //quick function to check if an array contains a property	
	function contains(a, obj){
		for(var i = 0; i < a.length; i++) {
    		if(a[i] == obj){
		      return true;
    		}
  		}
  		return false;
	}
	

//Protovis code. for displaying the graph/////////////////////////////////////////////////
	
//global variables, to be used with protovis code.
var vis = null;

// holds functions selected by faceted browsing criteria
var selectedFunctions = [""];

// holds gender selected by faceted browsing criteria
var selectedGender = "";

// holds the nationality selected by faceted browsing options
var selectedNationality = [""];

// define variables for colour options.

    //backgound for the graph	
	var bgColour = "white";
			
    //edge colour for Selected edges		
	var activeLinkStroke = "rgba(0,0,255,1)";
	
	//edge colour for related edges.
    var relatedLinkStroke = "rgba(46,46,46,0.7)"; 
    
    //edge colour for non related edges
    var inactiveLinkStroke = "rgba(170,170,170,0.7)";
    
    //mouse over colour for edges. - not currently in use.
    var mouseoverLinkStroke = "rgba(220,20,60,0.7)";

	//edge and node colouring for elemenst out of the range of the date slider.
    var outOfDateEdge = "rgba(170,170,170,0.05)";
    var outOfDateNode = "rgba(170,170,170,0.1)";
        		
    // node fill and stroke for related nodes    		
    var relatedNodeFill = "rgba(46,46,46,1)";
    var relatedNodeStroke = "rgba(46,46,46,1)";
    		
    // node fill and stroke for unrelated nodes		
    var inactiveNodeFill = "rgba(170,170,170,1)";
    var inactiveNodeStroke = "rgba(170,170,170,1)";
    		
    // node fill and stroke for focus-central node.
    var focusNodeFill = "blue";
    var focusNodeStroke = "blue";
    var focusText = "blue";
    
    
    // faceted colour scheme
    var bgColourFacet = "#333333";   
    var nodeFacet = "rgba(170,170,170,1)";
    var nodeFacetStroke = "rgba(170,170,170,1)";
    var focusNodeFacet = "blue";
    var focusNodeStrokeFacet = "blue";
    var focusNodeRelStrokeFacet = "white";
    var linkFacet = "rgba(170,170,170,0.2)";
	var relatedLinkFacet = "rgba(255,255,255,0.9)";
	var activeLinkFacet = "rgba(255,255,255,0.9)";    
    var textFacet = "white";
  		 
	//width and height settings for the protovis window.
	var w = 995,
    	h = 600;

//define the faceted browsing switch
	var browseTrigger = false;

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
    
	var nodeNeighbors;

function getNodes(){return contributors.nodes;}
function getEdges(){return contributors.edges;}

function resetProtovisVariables(){

	activeNode = null;
	nodeFocus = true;    	
	mouseOver = false;
	targetNodeSelect = -1;
    sourceNodeSelect = -1;
    targetNodeMO = -1;
	sourceNodeMO = -1;	

	
}

//create the network
//sets the focus node in position and creates the visualisation. does not render it though.
function createNetwork(targetDiv){
	if (contributors != null){
    	
    	
    	////////////////////////////////////////////////////////	
		// construct a 3d array of nodes and their neighbors
			nodeNeighbors = loadNodeNeighbors(contributors);

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
			    .fillStyle(function(){if (browseTrigger==true) {
			    						return bgColourFacet;
			    					  }else return bgColour;
			    						})
			    .event("mousedown", pv.Behavior.pan())
			    .event("mousewheel", pv.Behavior.zoom());

		
		///////////////////////////////////////////////////////
		//add a force layout to the Panel. 
			var	force = vis.add(pv.Layout.Force)
			    .nodes(getNodes())
			    .links(getEdges())
			    .bound(true)
			    .chargeConstant(-80)
			    .dragConstant(0.06)
			    .springConstant(0.05)
			    .chargeMaxDistance(600)			    
			    .springLength(150)
//			    .dragConstant(0.06)
			   // .springConstant(0.05)
 			    .iterations(500)
			   // .transform(pv.Transform.identity.scale(3).translate(-w/3,-h/3)) 

				;
		
		/////////////////////////////////////////////////////////
		//add the edges
  		   force.link.add(pv.Line) 
  		   		.strokeStyle(function(d, p){return getLinkStrokeStyle(d, p) })

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

			    .event("click", function(d, p) {updateSelectedInfo(activeNode, p, "link");
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
			    							  targetNodeSelect = -1;
			    							  sourceNodeSelect = -1;    										 
    										  updateSelectedInfo(activeNode, contributors, "nodes");
    										  return vis;})
    										  
    			.event("dblclick", function(d){return findAndDisplayContributorNetwork(contributors.nodes[this.index].id)})
    			
    			.anchor("top").add(pv.Label).text(function(d){return d.nodeName})
    			
    				.font("9pt sans-serif")
					.textStyle(	function(d) { return getTextStyle(d, this, nodeNeighbors)})
					.textShadow("0.1em 0.1em 0.1em #fff")
				
					.visible(function(){return mouseOver && this.index == targetNodeMO || this.index == sourceNodeMO ? true :
										nodeFocus ? activeNode == this.index?true:false:
							    		targetNodeSelect == this.index || sourceNodeSelect == this.index ? true : false})
    			;		
			}
					    	
}

//reload network and render
function reloadNetwork(targetDiv){
		resetDateRangeVisibility();
		resetProtovisVariables();    		
		createNetwork(targetDiv);
		vis.render();	
	}
	
//refresh network 
function refreshNetwork(typeOfRefresh){
	if(typeOfRefresh == "dateRange"){
		resetDateRangeVisibility();
	}
	else if (typeOfRefresh == "browse"){
		setFacetedSelection($("input#function:checked"),$("input#gender:checked"),$("input#nationality:checked"));  
	}
	vis.render();
	}

//resets all the visibility fields for nodes
function resetDateRangeVisibility(){

	for (var i = 0; i<contributors.nodes.length; i++){
		contributors.nodes[i].visible=false;
		}
	}

//resets the checked values for faceted browsing 
function setFacetedSelection(chosenFunctions, chosenGender, chosenNationality){
	
	
	//reset the function array
	selectedFunctions.splice(0, selectedFunctions.length);

	//reset the nationality array
	selectedNationality.splice(0, selectedNationality.length);


	//set the function array with chosen values
	for (i = 0; i < chosenFunctions.length; i++){
		selectedFunctions[i] = chosenFunctions[i].value;
	}
	
	//reset the gender variable
	if(chosenGender.length == 1){
		selectedGender = chosenGender[0].value;
	}
	else{
		selectedGender = "";
	}

	//set the function array with chosen values
	for (i = 0; i < chosenNationality.length; i++){
		selectedNationality[i] = chosenNationality[i].value;
	}

}

//functions to set the colour and style of nodes and links	

//link stroke style rules
function getLinkStrokeStyle(d, p){
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
			if (browseTrigger){ 
				return relatedLinkFacet;
			}
			else
				return relatedLinkStroke;
		}
	//if the focus is on edges
		else if (p.targetNode.index == targetNodeSelect && p.sourceNode.index == sourceNodeSelect){
			if(browseTrigger){
				return activeLinkFacet;
			}
			else
				return activeLinkStroke;
			}
		else if(browseTrigger){
			return linkFacet;
		}
		else 
			return inactiveLinkStroke;	
	}	
}

//node fill style rules
function getNodeFillStyle(d, node, nodeNeighbors){
	if (browseTrigger){
		
		var functionMatch = true;
		var genderMatch = true;
		var nationalityMatch = true;
		var selectedOptions = 0;
		
		
		if(selectedFunctions.length != 0) selectedOptions += 1;
		if(selectedGender != "") selectedOptions += 1;
		if(selectedNationality.length != 0) selectedOptions += 1;
		
		if(selectedFunctions[0] != ""){
			//check for matching functions		
			for(i=0; i<selectedFunctions.length; i++){			
				if(contains(d.functions, selectedFunctions[i])){
					functionMatch = true;
	
				}
				else{
					 functionMatch = false; 
					 break;
				}
			
			}
		}
		
		if (selectedGender!=""){
			if(d.gender == null){d.gender = "Unknown";}
			if (selectedGender == d.gender){
				genderMatch = true;	
			}	
			else genderMatch = false;
		}

		if(selectedNationality[0] != ""){
			if (d.nationality == null){d.nationality = "Unknown";}			
			//check for matching functions		
			if (contains(selectedNationality, d.nationality)){
				nationalityMatch = true;
			}
			else nationalityMatch = false;
		
		}
		
		if (functionMatch == true && genderMatch == true && nationalityMatch == true && selectedOptions > 0 ){

			return focusNodeFacet;
		}
		else{
			return nodeFacet;
		}
	}
		else
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
	if (browseTrigger){
		
		var functionMatch = true;
		var genderMatch = true;
		var nationalityMatch = true;
		var selectedOptions = 0;
		
		
		if(selectedFunctions.length != 0) selectedOptions += 1;
		if(selectedGender != "") selectedOptions += 1;
		if(selectedNationality.length != 0) selectedOptions += 1;
		
		if(selectedFunctions[0] != ""){
			//check for matching functions		
			for(i=0; i<selectedFunctions.length; i++){			
				if(contains(d.functions, selectedFunctions[i])){
					functionMatch = true;
	
				}
				else{
					 functionMatch = false; 
					 break;
				}
			
			}
		}
		
		if (selectedGender!=""){
			if(d.gender == null){d.gender = "Unknown";}
			if (selectedGender == d.gender){
				genderMatch = true;	
			}	
			else genderMatch = false;
		}
		
		if(selectedNationality[0] != ""){
			if (d.nationality == null){d.nationality = "Unknown";}			
			//check for matching functions		
			if (contains(selectedNationality, d.nationality)){
				nationalityMatch = true;
			}
			else nationalityMatch = false;
		
		}

		if (functionMatch == true && genderMatch == true && nationalityMatch == true && selectedOptions > 0 ){
			if (contains(nodeNeighbors[activeNode], node.index)==true || activeNode == node.index){
				return focusNodeRelStrokeFacet;
			}
			return focusNodeStrokeFacet;
		}
		else{
			if (contains(nodeNeighbors[activeNode], node.index)==true|| activeNode == node.index){
				return focusNodeRelStrokeFacet;
			}
			return nodeFacetStroke;
		}
	}
		else
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
/*function getNodeStrokeStyle(d, node, nodeNeighbors){
	if (!d.visible){
		return outOfDateNode;
	}
	else
	if (nodeFocus==true){
		if (activeNode== node.index){ 
			if(browseTrigger){
				return focusNodeStrokeFacet;
			}
			else return focusNodeStroke;
			
		}else if (contains(nodeNeighbors[activeNode], node.index)==true){
			if (browseTrigger) {
				return focusNodeStrokeFacet;
			}
			else return relatedNodeStroke;	
		}
		else return inactiveNodeStroke;		
	}
	else if (targetNodeSelect == node.index || sourceNodeSelect == node.index){
		if(browseTrigger){
			return focusNodeStrokeFacet;
		}return focusNodeStroke;
	}
	else return inactiveNodeStroke;		

}*/	

//Text style rules
function getTextStyle(d, node, nodeNeighbors){
	if (browseTrigger){
		
		var functionMatch = true;
		var genderMatch = true;
		var nationalityMatch = true;
		var selectedOptions = 0;
		
		
		if(selectedFunctions.length != 0) selectedOptions += 1;
		if(selectedGender != "") selectedOptions += 1;
		if(selectedNationality.length != 0) selectedOptions += 1;
		
		if(selectedFunctions[0] != ""){
			//check for matching functions		
			for(i=0; i<selectedFunctions.length; i++){			
				if(contains(d.functions, selectedFunctions[i])){
					functionMatch = true;
	
				}
				else{
					 functionMatch = false; 
					 break;
				}
			
			}
		}
		
		if (selectedGender!=""){
			if(d.gender == null){d.gender = "Unknown";}
			if (selectedGender == d.gender){
				genderMatch = true;	
			}	
			else genderMatch = false;
		}
		
		if(selectedNationality[0] != ""){
			if (d.nationality == null){d.nationality = "Unknown";}			
			//check for matching nationality		
			if (contains(selectedNationality, d.nationality)){
				nationalityMatch = true;
			}
			else nationalityMatch = false;
		
		}

		if (functionMatch == true && genderMatch == true && nationalityMatch == true && selectedOptions > 0 ){

			return focusNodeFacet;
		}
		else{
			return nodeFacet;
		}
	}
		else
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


