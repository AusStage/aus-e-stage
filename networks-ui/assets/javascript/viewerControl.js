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
 
 /* Viewer Controller class. controls interaction elements, and viewer objects */

 	var BASE_URL = "http://beta.ausstage.edu.au/networks/protovis?task=ego-centric-network&id=";	
	var END_URL = 	"&radius=1&callback=?"
 	
 	//messages
	var VIEWER_NO_DATA_MSG = 'No data selected for network';
	var VIEWER_LOADING_MSG = 'Rendering network...';
	var DATA_RETRIEVAL_MSG = 'Retrieving network data...';
	var VIEWER_ERROR_MSG = 'An error occurred loading the network data';	
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

	this.selectedContributors = {'id': [], 'name': [], 'url':[]};
	this.selectedEvent = {'id':[], 'name':[], 'url':[]};	

}

ViewerControlClass.prototype.init = function() {
	
	//hide the ruler div
	$("#ruler").hide();

	//common setup for network viewer
	$('#viewerMsg').append(buildInfoMsgBox(VIEWER_NO_DATA_MSG));	

	//hide the faceted browsing button
	$("#faceted_browsing_btn_div").hide();

	//hide the faceted browsing
	$("#faceted_browsing_div").hide();

	//hide the viewer options
	$("#viewer_options_div").hide();
	//hide the show labels checkboxes
	$("#display_labels_div").hide();
	$("#display_event_labels_div").hide();
	//hide the edge display section
	$('#display_edges_div').hide();;	

	//hide the legend
	$("#network_details_div").hide();
	$("#network_properties_div").hide();	

		//SET UP INTERACTION 
	//style the legend
	createLegend("#network_properties_header", this);
	createLegend("#selected_object_header", this);
	createLegend("#viewer_options_header", this);
	//style the faceted browsing optionf
	createLegend("#functions_header", this);
	createLegend("#gender_header", this);
	createLegend("#nationality_header", this);	
	createLegend("#criteria_header", this);		
	
	//set up browsing button
    $("#faceted_browsing_btn").click(function() {
		$("#faceted_browsing_div").dialog("open");
	    return false;
    });
 
    //set up faceted browsing dialog
    $("#faceted_browsing_div").dialog({ 
		autoOpen: false,
		closeOnEscape: false,
		maxWidth:280,
		minWidth:280,
		width: 280,
		modal: false,
		position: ["right","top"],
		buttons: {
			"Refresh": function() {
				updateFacetedTerms();
				viewer.refreshGraph("faceted");
				return false;
			},
			'Close': function(){
				$(this).dialog('close');
				return false;
			}
		},
		open: function(){
		viewer.viewFaceted = true;
		viewer.refreshGraph("faceted");
		},
	  	close: function() {
		viewer.viewFaceted= false;
		viewer.refreshGraph("dateRange");
		}
	});
	
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
	 	//PERSON TO PERSON CHECKBOXES
 		//set up label on/off checkboxes for contributor names
	$("input[name=showAllContributors]").click(function() { 
		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showAllContributors]").is(":checked")){
			viewer.showAllContributors = true;	
    	}
    	else viewer.showAllContributors = false;
    	viewer.render();
	}); 
	
	//set up label on/off checkboxes for contributor names
	$("input[name=showRelatedContributors]").click(function() { 
		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showRelatedContributors]").is(":checked")){
			viewer.showRelatedContributors = true;	
    	}
    	else viewer.showRelatedContributors = false;
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
	
	//set up show/hide edge checkboxes - HIDE ALL
    $("input[name=hideAll]").click(function() { 

		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=hideAll]").is(":checked")){
			viewer.hideAll = true;	
    	}
    	else viewer.hideAll = false;
    	viewer.render();
    	console.log('hide all');
	}); 
	
	//set up show/hide edge checkboxes - HIDE UNRELATED
    $("input[name=hideUnrelated]").click(function() { 

		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=hideUnrelated]").is(":checked")){
			viewer.hideUnrelated = true;	
    	}
    	else viewer.hideUnrelated = false;
    	viewer.render();
    	console.log('hide unrelated');
	}); 
	
	
	//EVENT TO EVENT CHECKBOXES
	//set up label on/off checkboxes for contributor and event names
    $("input[name=showContributors]").click(function() { 

		//if checked, then set showContributors to true, else set to false;
    	if($("input[name=showContributors]").is(":checked")){
			viewer.showContributors = true;	
    	}
    	else viewer.showContributors = false;
    	viewer.render();
	}); 

    $("input[name=showEvents]").click(function() { 

		//if checked, then set showEvents to true, else set to false;
    	if($("input[name=showEvents]").is(":checked")){
    		viewer.showEvents = true;	
    	}
    	else viewer.showEvents = false;
    	viewer.render();
	}); 	

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
    		viewer.showCustVis = false;	
    	}
    	else viewer.showCustVis = true;
    	viewer.render();
	}); 
	
	$("#reset_cust_colors").click(function() {
		viewer.resetAllColors();
		viewer.render();		
	    return false;
    });
    
	$("#reset_cust_vis").click(function() {
		viewer.resetHiddenElements();
		viewer.render();		
	    return false;
    });
    
	
	//deal with window resizing	
	$(window).resize(function() {
	  	viewer.w = $(window).width() - ($(".sidebar").width()+viewer.spacer);
		viewer.h =  $(window).height() - ($(".header").height()+$(".footer").height()+$('#fix-ui-tabs').height()+viewer.hSpacer);	  
		if(viewer.json.edges.length!=0){
			viewer.windowResized();			
			viewer.render();	
		}
	});	
		
};

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
			}	
		}
		viewerControl.displaySelectedContributors();
		return SUCCESS;
	}
}
//remove id for contributor path networking
ViewerControlClass.prototype.removeId = function(index){
	$('#searchAddContributorError').empty()
	this.selectedContributors.id.splice(index, 1);
	this.selectedContributors.name.splice(index, 1);
	this.selectedContributors.url.splice(index, 1);	
	this.displaySelectedContributors();	
}

//add id for event selection
ViewerControlClass.prototype.addEventId = function(id, data){
	$('#searchAddEventError').empty();
	if (this.selectedEvent.id.length == 1){
		return TO_MANY_EVENTS_SELECTED;
	}
	else{ this.selectedEvent.id.push(id);
		for(i in data){
			if(id == data[i].id){
				this.selectedEvent.name.push(data[i].name);		
				this.selectedEvent.url.push(data[i].url);						
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
			html += '<a href="' + this.selectedEvent.url[i] + '" title="View the record for ' 
			+ this.selectedEvent.name[i] + ' in AusStage" target="_ausstage">' + this.selectedEvent.name[i] + '</a>';
			html += '<span id="'+i+'" class="eventRemoveIcon ui-icon ui-icon-close clickable" style="display: inline-block;"></span>';			
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
			html += '<a href="' + this.selectedContributors.url[x] + '" title="View the record for ' 
			+ this.selectedContributors.name[x] + ' in AusStage" target="_ausstage">' + this.selectedContributors.name[x] + '</a>';
			html += '<span id="'+i+'" class="contributorRemoveIcon ui-icon ui-icon-close clickable" style="display:	inline-block"></span>';		
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
ViewerControlClass.prototype.displayNetwork = function(type, id, reset){
		viewer.hideInteraction();
		resetCheckboxes();
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
				url:BASE_URL+id+END_URL,
				error:function(){$('#viewerMsg').empty().append(buildErrorMsgBox(VIEWER_ERROR_MSG)).show();},
				success:function(json){
					$('#viewerMsg').empty().append(buildInfoMsgBox(VIEWER_LOADING_MSG)).show();					
					viewer.renderGraph(json);
					$('#viewerMsg').hide();
					viewer.showInteraction();
				}
			})
			
			break;
		case 'CONTRIBUTOR_PATH':
			console.log('contributor path selected for :'+id[0]+' '+id[1]);
			break;
		case 'EVENT':
			viewer = new EventViewerClass();
			//build the url, use jsonp to get the data. 
			viewer.renderGraph(events);
			$('#viewerMsg').hide();
			viewer.showInteraction();
			break;	
	}
}

function resetCheckboxes(){
	$("input[name=showAllContributors]").attr('checked', false);
	$("input[name=showRelatedContributors]").attr('checked', false);
	$("input[name=showAllFaceted]").attr('checked', true); 
}

function closeLegends(){
	//resets all legends for a new network
	resetLegend('#functions');	
	resetLegend('#gender');	
	resetLegend('#nationality');	
	resetLegend('#criteria');				
}