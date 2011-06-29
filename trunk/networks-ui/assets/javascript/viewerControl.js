/*
 * This file is part of the AusStage Mapping Service
 *
 * The AusStage Mapping Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Mapping Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
 */
 
 /* Viewer Controller class. controls interaction elements, and viewer objects  */

	var BASE_URL_EXPORT = "http://beta.ausstage.edu.au/networks/export?";
 	var BASE_URL_CONTRIBUTOR = "http://beta.ausstage.edu.au/networks/protovis?task=ego-centric-network&id=";	
 	var BASE_URL_EVENT = "http://beta.ausstage.edu.au/networks/protovis?task=event-centric-network&id=";	 	
	var END_URL = 	"&radius=1&callback=?"
	var END_URL_EVENT ="&callback=?"	
	var BASE_URL = "http://beta.ausstage.edu.au/networks/networks.jsp?";
 	
 	//messages
	var VIEWER_NO_DATA_MSG = 'No data selected for network';
	var VIEWER_LOADING_MSG = 'Rendering network...';
	var DATA_RETRIEVAL_MSG = 'Retrieving network data...';
	var VIEWER_ERROR_MSG = 'An error occurred loading the network data';
	var NO_EVENT_DATA_MSG = 'There are no contributors associated with this event.';
	var NO_COLLAB_DATA_MSG = 'The selected contributor has no collaborations';	
	var TO_MANY_SELECTED = 'Only two contributors can be selected for contributor path network';
	var TO_MANY_EVENTS_SELECTED = 'Only one event can be selected for event network';
	var DUPLICATE_SELECTED = 'This contributor has already been selected';
	var SUCCESS = 'success';
 
 	//custom colors - http://aus-e-stage.googlecode.com/svn/trunk/common-web-assets/ausstage-colour-scheme.html icon cycle 24
 	var CUSTOM_COLORS = ["276abd","317db9","3b91b5","44a4b1","4eb8ad","3b9d8f","278271","146853",
 						 "004d35","1a6b3a","33893e","4da743","66c547","8cc43d","b3c333","d9d121",
 						 "ffe010","ffd117","ffc11f","ffb320","ffa521","ff9821","ff8a22","fc792c",
 						 "f96835","f6573f","f34648","dc3738","c52829","af1819","980909","a31d34",
 						 "ad315f","b7458a","c259b5","b556b6","a754b7","9951b7","8c4eb8","7758c0",
 						 "6262c7","4d6bcf","3875d7","2a5fd4","1c48d1","0e31cf","001bcc","1442c4"]
 //constructor
function ViewerControlClass (){

	this.selectedContributors = {'id': [], 'name': [], 'url':[], 'event_dates':[], 'functions':[]};
	this.selectedEvent = {'id':[], 'name':[], 'url':[], 'extra_info':[]};	

}

ViewerControlClass.prototype.init = function() {	
	
	//hide the ruler div
	$("#ruler").hide();

	//common setup for network viewer
	$('#viewerMsg').append(buildInfoMsgBox(VIEWER_NO_DATA_MSG));	

	//hide the export tab
	$("#exportContainer").hide();
	//hide the faceted browsing
	$("#faceted_div").hide();

	//hide the viewer options
	$("#viewer_options_div").hide();
	//hide the show labels checkboxes
	$("#contributor_options_div").hide();
	$("#event_options_div").hide();

	//hide the legend
	$("#network_details_div").hide();
	$("#network_properties_div").hide();
	
	//hide the navigation controls
	//$('#navigation').hide();	

		//SET UP INTERACTION $.debounce(250, false, viewer.collabSliderObj.updateNetwork(event, ui)); 
	//style the legend
	createLegend("#network_properties_header");
	createLegend("#selected_object_header");
	createLegend("#viewer_options_header", function(){$.debounce(250, false, $(window).resize())});
	//style the faceted browsing optionf
	createLegend('#faceted_header', function(){viewer.facetedModeOn()},
				function(){viewer.facetedModeOff()});
	createLegend("#functions_header");
	createLegend("#gender_header");
	createLegend("#nationality_header");	
	createLegend("#criteria_header");		
    
  	
    //set up custom dialog
    $("#custom_div").dialog({ 
    	open:function(event, ui) {
    			$('.ui-widget-overlay').click(
				function(e) { 
		            $('#custom_div').dialog('close');
				});
    		},
    	dialogClass: 'noTitle noPadding',
		autoOpen: false,
		resizable: false,
		closeOnEscape: true,
		modal: true,
		position: ["right","top"]
	});
	
	//COLOR PICKER
	$("#color_picker").icolor({
		flat:true,
		colors:CUSTOM_COLORS,
		col:16,
		holdColor:false,
		onSelect:function(c){viewer.setColor(c);
							//viewer.json.nodes[viewer.custNodeIndex].custColor = c;
							 $("#custom_div").dialog("close");
							 viewer.render();							 
							}
	});

	
	//custom dialog buttons
	$("#remove_color").click(function(){
		viewer.resetColor();
		$("#custom_div").dialog("close");
		viewer.render();
	});

	$("#hide_element").click(function(){
		viewer.hideElement();
		$("#custom_div").dialog("close");
		viewer.render();
	});
	 	
	//CONTRIBUTOR TO CONTRIBUTOR
	//viewer display checkboxes
 	//set up label on/off checkboxes for node labels
	$("input[name=showAllNodeLabels]").click(function() { 
		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showAllNodeLabels]").is(":checked")){
			viewer.showAllNodeLabels = true;	
    	}
    	else viewer.showAllNodeLabels = false;
    	viewer.render();
	}); 
	
	//set up label on/off checkboxes for node labels
	$("input[name=showRelatedNodeLabels]").click(function() { 
		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showRelatedNodeLabels]").is(":checked")){
			viewer.showRelatedNodeLabels = true;				
    	}
    	else viewer.showRelatedNodeLabels = false;
    	viewer.render();
	});  

	//node on/off checkboxes ALL
	$("input[name=showAllNodes]").click(function() { 
		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showAllNodes]").is(":checked")){
			viewer.showAllNodes = true;
			//enable the show all edges checkbox
			$("input[name=showAllEdges]").attr('disabled', false);
			//set viewers show all edges var based on checkbox value
			viewer.showAllEdges = ($("input[name=showAllEdges]").is(":checked"))?true:false;
    	}
    	else { 
    		viewer.showAllNodes = false;
	    	//disable the show all edges checkbox
    		$("input[name=showAllEdges]").attr('disabled', true);
    		//set viewers show all edges var to false
			viewer.showAllEdges = false;
    	}
    	viewer.render();
	}); 
	
	//RELATED
	$("input[name=showRelatedNodes]").click(function() { 
		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showRelatedNodes]").is(":checked")){
			viewer.showRelatedNodes = true;	
			//enable the show related edges checkbox
			$("input[name=showRelatedEdges]").attr('disabled', false);
			//set viewers show all edges var based on checkbox value
			viewer.showRelatedEdges = ($("input[name=showRelatedEdges]").is(":checked"))?true:false;
    	}
    	else {
    		viewer.showRelatedNodes = false;
    		//disable the show related edges checkbox
    		$("input[name=showRelatedEdges]").attr('disabled', true);
    		//set viewers show related edges var to false
			viewer.showRelatedEdges = false;
    	}
    	viewer.render();
	}); 

	//set up show/hide edge checkboxes - HIDE ALL
    $("input[name=showAllEdges]").click(function() { 

		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showAllEdges]").is(":checked")){
			viewer.showAllEdges = true;	
    	}
    	else viewer.showAllEdges = false;
    	viewer.render();
    	
	}); 
	
	//set up show/hide edge checkboxes - HIDE UNRELATED
    $("input[name=showRelatedEdges]").click(function() { 

		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showRelatedEdges]").is(":checked")){
			viewer.showRelatedEdges = true;	
    	}
    	else viewer.showRelatedEdges = false;
    	viewer.render();
    	
	}); 
	
	//set up display on/off checkboxes for Faceted Browse
	$("input[name=showAllFaceted]").click(function() { 
		//if checked, then set showAllFaceted to true, else set to false;
    	if($("input[name=showAllFaceted]").is(":checked")){
			viewer.showAllFaceted = true;	
    	}
    	else viewer.showAllFaceted = false;
    	viewer.render();
	});  
	
	
	//EVENT TO EVENT CHECKBOXES
	//viewer display checkboxes
 	//set up label on/off checkboxes for node labels
	$("input[name=showAllNodeLabelsEvt]").click(function() { 
		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showAllNodeLabelsEvt]").is(":checked")){
			viewer.showAllNodeLabels = true;	
    	}
    	else viewer.showAllNodeLabels = false;
    	viewer.render();
	}); 
	
	//set up label on/off checkboxes for node labels
	$("input[name=showRelatedNodeLabelsEvt]").click(function() { 
		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showRelatedNodeLabelsEvt]").is(":checked")){
			viewer.showRelatedNodeLabels = true;	
    	}
    	else viewer.showRelatedNodeLabels = false;
    	viewer.render();
	}); 

	//set up label on/off checkboxes for edge labels
	$("input[name=showAllEdgeLabelsEvt]").click(function() { 
		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showAllEdgeLabelsEvt]").is(":checked")){
			viewer.showAllEdgeLabels = true;
    	}
    	else viewer.showAllEdgeLabels = false;
    	viewer.render();
	}); 
	
	//set up label on/off checkboxes for edge labels
	$("input[name=showRelatedEdgeLabelsEvt]").click(function() { 
		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showRelatedEdgeLabelsEvt]").is(":checked")){
			viewer.showRelatedEdgeLabels = true;	
    	}
    	else viewer.showRelatedEdgeLabels = false;
    	viewer.render();
	}); 

	//node on/off checkboxes ALL
	$("input[name=showAllNodesEvt]").click(function() { 
		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showAllNodesEvt]").is(":checked")){
			viewer.showAllNodes = true;
    	}
    	else viewer.showAllNodes = false;
    	viewer.render();
	}); 
	
	//RELATED
	$("input[name=showRelatedNodesEvt]").click(function() { 
		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showRelatedNodesEvt]").is(":checked")){
			viewer.showRelatedNodes = true;	
    	}
    	else viewer.showRelatedNodes = false;
    	viewer.render();
	}); 

	//set up show/hide edge checkboxes - HIDE ALL
    $("input[name=showAllEdgesEvt]").click(function() { 

		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showAllEdgesEvt]").is(":checked")){
			viewer.showAllEdges = true;	
    	}
    	else viewer.showAllEdges = false;
    	viewer.render();
    	
	}); 
	
	//set up show/hide edge checkboxes - HIDE UNRELATED
    $("input[name=showRelatedEdgesEvt]").click(function() { 

		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showRelatedEdgesEvt]").is(":checked")){
			viewer.showRelatedEdges = true;	
    	}
    	else viewer.showRelatedEdges = false;
    	viewer.render();
    	
	}); 

	//BOTH CONTRIBUTOR AND EVENT CENTRIC NETWORKS
	//custom colors and visibility checkboxes
    $("input[name=showCustColors]").click(function() { 

		//if checked, then set showEvents to true, else set to false;
    	if($("input[name=showCustColors]").is(":checked")){
    		viewer.showCustColors = true;	
    	}
    	else viewer.showCustColors = false;
    	viewer.render();
	}); 

    $("input[name=showCustVis]").click(function() { 

		//if checked, then set showEvents to true, else set to false;
    	if($("input[name=showCustVis]").is(":checked")){
    		viewer.showCustVis = true;	
    	}
    	else viewer.showCustVis = false;
    	viewer.render();
	}); 
	
	$("#reset_cust_colors").click(function() {
        $("#cust_colors_confirm_reset").dialog('open');
	    return false;
    });
    
	$("#reset_cust_vis").click(function() {
        $("#cust_vis_confirm_reset").dialog('open');		
	    return false;
    });
    
    // setup the customisation reset confirmation boxes
        $("#cust_colors_confirm_reset").dialog({
                autoOpen: false,
                height: 300,
                width: 400,
                modal: true,
                buttons: {
                        Cancel: function() {
                                $(this).dialog('close');
                        },
                        Reset: function() {
                                $(this).dialog('close');
								viewer.resetAllColors();
								viewer.render();
                        }
                }
        });

        $("#cust_vis_confirm_reset").dialog({
                autoOpen: false,
                height: 300,
                width: 400,
                modal: true,
                buttons: {
                        Cancel: function() {
                                $(this).dialog('close');
                        },
                        Reset: function() {
                                $(this).dialog('close');
								viewer.resetHiddenElements();
								viewer.render();
                        }
                }
        });
       	

    
	//deal with window resizing	
	$(window).resize(function() {
	  	viewer.w = $(window).width() - ($(".sidebar").width()+viewer.spacer);
		viewer.h =  $(window).height() - ($(".header").height()+$(".footer").height()+$('#fix-ui-tabs').height()+viewer.hSpacer);	  
        viewerControl.resizeLegend();
		if(viewer.json.edges.length!=0){
			viewer.windowResized();			
			viewer.render();
			//deal with zoom element
			checkZoomHeight();
		}
	});	
	
	viewerControl.resizeLegend();
};

ViewerControlClass.prototype.resizeLegend = function(){
	$('.legendContainer').css('max-height', ($(window).height()-$('.legendContainer').offset().top));	
}

//add id for contributor path networking
ViewerControlClass.prototype.addId = function(id, data){
	$('#searchAddContributorError').empty();
	if (this.selectedContributors.id.length == 2){
		return TO_MANY_SELECTED;
	}
	else if(contains(this.selectedContributors.id, id)){
		return DUPLICATE_SELECTED;
	}	
	else{ this.selectedContributors.id.push(id);
		for(i in data){
			if(id == data[i].id){
				this.selectedContributors.name.push(data[i].firstName+' '+data[i].lastName);		
				this.selectedContributors.url.push(data[i].url);
				this.selectedContributors.event_dates.push(data[i].eventDates);
				this.selectedContributors.functions.push(data[i].functions);										
			}	
		}
		viewerControl.displaySelectedContributors();
		return SUCCESS;
	}
}
//remove id for contributor path networking
ViewerControlClass.prototype.removeId = function(index){
	$('#searchAddContributorError').empty();
	this.selectedContributors.id.splice(index, 1);
	this.selectedContributors.name.splice(index, 1);
	this.selectedContributors.url.splice(index, 1);
	this.selectedContributors.event_dates.splice(index, 1);
	this.selectedContributors.functions.splice(index, 1);										
	
	this.displaySelectedContributors();	
}

//add id for event selection
ViewerControlClass.prototype.addEventId = function(id, data){
	var info;
	$('#searchAddEventError').empty();
	if (this.selectedEvent.id.length == 1){
		return TO_MANY_EVENTS_SELECTED;
	}
	else{ this.selectedEvent.id.push(id);
		for(i in data){
			if(id == data[i].id){
				this.selectedEvent.name.push(data[i].name);		
				this.selectedEvent.url.push(data[i].url);
				
				info = data[i].venue.name;
        		
		        if(data[i].venue.suburb != null) {
        			info += ', '+data[i].venue.suburb;
		        }        
		        if (data[i].venue.country == 'Australia'){
	        		if(data[i].venue.state != null) {
		    	    	info += ', '+data[i].venue.state;
        			}
		        }else{
			        if(data[i].venue.country != null) {
    		    		info += ', '+data[i].venue.country;
	        		}        		
    	    	}
    	    	info += ', '+data[i].firstDisplayDate;
    	    	this.selectedEvent.extra_info.push(info);    				
			}	
		}
		viewerControl.displaySelectedEvent();
		return SUCCESS;
	}
}

//remove id for event networking
ViewerControlClass.prototype.removeEventId = function(index){
	$('#searchAddEventError').empty()
	this.selectedEvent.id.splice(index, 1);
	this.selectedEvent.name.splice(index, 1);
	this.selectedEvent.url.splice(index, 1);	
	this.selectedEvent.extra_info.splice(index, 1);		
	this.displaySelectedEvent();	
}


//show the selected contributors - rather than requery the database, hold results for each search in the search object. then look through the
//results for the id and display details
ViewerControlClass.prototype.displaySelectedEvent = function(){
	
	var html = ''
	if(this.selectedEvent.id.length > 0){
		$('#viewEventNetwork').button('option', 'disabled', false);
		$('#eventDegree').removeAttr('disabled');
		for(i in this.selectedEvent.id){
			html += '<span id="'+i+'" class="eventRemoveIcon ui-icon ui-icon-close clickable" style="display: inline-block;"></span>';			
			html += '<a href="' + this.selectedEvent.url[i] + '" title="View the record for ' 
			+ this.selectedEvent.name[i] + ' in AusStage" target="_ausstage">' + this.selectedEvent.name[i] + '</a>, '
			+this.selectedEvent.extra_info[i];

		}
	}
	else{
		$('#viewEventNetwork').button('option', 'disabled', true);
		$('#eventDegree').attr('disabled', 'disabled');		
	}	
	
	$('#selected_event').empty().append(html);
	$('.eventRemoveIcon').click( function (){
									viewerControl.removeEventId(this.id); });	
}

ViewerControlClass.prototype.displaySelectedContributors = function(){
	
	var html = ''
	if(this.selectedContributors.id.length > 0){
		$('#viewContributorNetwork').button('option', 'disabled', false);	
		for(x in this.selectedContributors.id){
			html += '<span id="'+x+'" class="contributorRemoveIcon ui-icon ui-icon-close clickable"></span>';
			html += '<a href="' + this.selectedContributors.url[x] + '" title="View the record for ' 
			+ this.selectedContributors.name[x] + ' in AusStage" target="_ausstage">' + this.selectedContributors.name[x] + '</a>,';
			html += ' '+this.selectedContributors.event_dates[x];
			for (y in this.selectedContributors.functions[x]){
				html += ', '+this.selectedContributors.functions[x][y];	
			}
			html += "<br>";
		}
	}
	else{
		$('#viewContributorNetwork').button('option', 'disabled', true);
	}		
	$('#selected_contributors').empty().append(html);
	$('.contributorRemoveIcon').click( function (){
									viewerControl.removeId(this.id); });	
}


//create and show the network. Parameters - type : CONTRIBUTOR_PATH, EVENT, CONTRIBUTOR or EXISTING
//											id   : either CONTRIBUTOR_ID[], EVENT_ID, or CONTRIBUTOR_ID
//											reset: 0 to leave the sidebar, 1 to reset sidebar.
ViewerControlClass.prototype.displayNetwork = function(type, id, reset, rad_sim){
		viewer.hideInteraction();
		closeLegends();
		viewer.destroy(); 
		//show loading msg	
		$('#viewerMsg').empty().append(buildInfoMsgBox(DATA_RETRIEVAL_MSG)).show();
			
	if(reset==1){
		resetLegend('#selected_object');
		resetLegend('#network_properties');
		resetLegend('#viewer_options');				
	}
	switch (type){
		case 'CONTRIBUTOR':
			viewer = new ContributorViewerClass(type);
			//var obj = this;
			$.jsonp({
				url:BASE_URL_CONTRIBUTOR+id+END_URL,
				error:function(){$('#viewerMsg').empty().append(buildErrorMsgBox(VIEWER_ERROR_MSG)).show();},
				success:function(json){
					//if no nodes
					if(json.edges.length ==0){
						$('#viewerMsg').empty().append(buildInfoMsgBox(NO_COLLAB_DATA_MSG)).show();						
					}

					//else
					else{
						$('#viewerMsg').empty().append(buildInfoMsgBox(VIEWER_LOADING_MSG)).show();					
						viewer.renderGraph(json);
						$('#viewerMsg').hide();
						viewer.showInteraction();
						buildDownloadLink(type,id);
						buildBookmarkLink(type, id);
					}
				}
			})
			
			break;
		case 'CONTRIBUTOR_PATH':
			break;
		case 'EVENT':
			viewer = new EventViewerClass();
			var radius;
			var simplify = false;
			switch (rad_sim){
				case '1': 
					radius = 1;
					break;
				case '2':
					radius = 2;
					break;					
				case '3':	
					radius = 2;
					simplify = true;
					break;					
			}		
			$.jsonp({
				url:BASE_URL_EVENT+id+'&radius='+radius+'&simplify='+simplify+END_URL_EVENT,
				error:function(){$('#viewerMsg').empty().append(buildErrorMsgBox(VIEWER_ERROR_MSG)).show();},
				success:function(json){
					//if no contributors
					if(json.edges.length ==0){
						$('#viewerMsg').empty().append(buildInfoMsgBox(NO_EVENT_DATA_MSG)).show();						
					}
					//else
					else{
						$('#viewerMsg').empty().append(buildInfoMsgBox(VIEWER_LOADING_MSG)).show();					
						viewer.renderGraph(json);
						$('#viewerMsg').hide();
						viewer.showInteraction();
						buildDownloadLink(type, id, radius, simplify);
						buildBookmarkLink(type, id, rad_sim);
					}
				}
			})
			break;	
	}
}

function closeLegends(){
	//resets all legends for a new network
	resetLegend('#functions');	
	resetLegend('#gender');	
	resetLegend('#nationality');	
	resetLegend('#criteria');
	resetLegend('#faceted');				
}


//builds the download link for side bar
function buildDownloadLink(type, id, radius, simplify){
	var exportUrl = BASE_URL_EXPORT;
	switch(type){
		case 'CONTRIBUTOR':
			exportUrl += 'task=ego-centric-network-simple&id='+id+'&format=graphml';
			break;
		case 'EVENT':
			exportUrl += 'task=event-centric-network&id='+id+'&format=graphml&radius='+radius+'&simplify='+simplify;
			break;
	}
	$("#downloadLink").attr("href", exportUrl);
}

//builds bookmark link for side bar
function buildBookmarkLink(type, id, rs){
	var bookmarkUrl = BASE_URL;
	switch(type){
		case 'CONTRIBUTOR':
			bookmarkUrl += 'task=ego-centric&id='+id;
			break;
		case 'EVENT':
			bookmarkUrl += 'task=event-centric&id='+id+'&rs='+rs;
			break;
	}
	$("#network_bookmark_link").attr("href", bookmarkUrl);	
}
