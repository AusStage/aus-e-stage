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

// declare global variables
var contributorMapData = null;

// setup the page
$(document).ready(function(){
	// attach the validation plugin to the name search form
	$("#name_search").validate({
		rules: { // validation rules
			contributor_name: {
				required: true
			}
		},
		submitHandler: function(form) {
			jQuery(form).ajaxSubmit({
				beforeSubmit: showLoader,
				success:      showSearchResults,
				error:        showErrorMessage
			});
		}
	});
	
	// attach the validation plugin to the id search form
	$("#id_search").validate({
		rules: { // validation rules
			id: {
				required: true,
				digits: true
			}
		},
		submitHandler: function(form) {
			jQuery(form).ajaxSubmit({
				beforeSubmit: showLoader,
				success:      loadNewPage,
				error:        showErrorMessage
			});
		}
	});	
	
	// hide the loading div
	hideLoader();
	
	// hide the map div
	hideMap();
	
});

// override the default action of some form buttons
$(document).ready(function() {
	$("#reload_map").click(reloadMap);
});

// set up the tabbed interface
$(document).ready(function() {
	$("#tabs").tabs({ 
		cookie: { 
			name: 'contributor_search_tab', 
			expires: 30
		},
		select: function(event, ui) {
			$("#search_results").hide();
			$("#search_results").empty();
			hideMap();			
		} 
	}); 
});

// add clue tips to the form labels
$(document).ready(function() {
	$('#name_label').cluetip({local:true, attribute: 'class', cursor: 'help', showTitle: false});
	$('#operator_label').cluetip({local:true, attribute: 'class', cursor: 'help', showTitle: false});
	$('#state_label').cluetip({local:true, attribute: 'class', cursor: 'help', showTitle: false});
});


/** helper functions **/
function hideLoader() {
	$("#search_waiting").hide();
	$("#name_search_btn").removeAttr("disabled");
	$("#id_search_btn").removeAttr("disabled");
}

// functions for showing and hiding the loading message
function showLoader() {
	$("#search_waiting").show();
	
	$("#search_results").hide();
	$("#search_results").empty();
	
	$("#map_header").hide();
	$("#map").hide();
	$("#map_legend").hide();
	$("#map_footer").hide();
	
	$("#name_search_btn").attr("disabled", "disabled");
	$("#id_search_btn").attr("disabled", "disabled");
	
	GUnload();	
}

function hideMap() {
	// hide the map div
	$("#map").hide();
	$("#map_header").hide();
	$("#map_legend").hide();
	$("#map_footer").hide();	
}

/** form processing functions **/
// function to show the search results
function showSearchResults(responseText, statusText)  {

	$("#search_results").hide();
	$("#search_results").empty();
	$("#search_results").append(responseText);
	$("#search_results").show();
	
	hideLoader();
	
	// associate a click event with the links
	$("#search_results a").click(function() {
		$("#map_header h3").empty();
		$("#map_header h3").append("Map of " + $(this).text() + " events");
		return false;
	});
	
	// overide the default form action
	$("#multi_contrib").click(multiContribMap);
	
}

// function to show a generic error message
function showErrorMessage() {

	$("#search_results").hide();
	$("#search_results").empty();
	$("#search_results").append("<p><strong>Error:</strong> An error occured while processing your search.<br/>");
	$("#search_results").append("Please check your search terms and try again. If the problem persists contact the site administrator.</p>");
	$("#search_results").show();
	
	hideLoader();	
}

// register ajax error handlers
$(document).ready(function() {

	// getting marker xml for contributors
	$("#map").ajaxError(function(e, xhr, settings, exception) {
		if(settings.url.search("action=markers&type=contributor") != -1) {
			$(this).empty();
			$(this).append('<p style="text-align: center"><strong>Error: </strong>An error occured whilst loading markers, please try again.<br/>If the problem persists please contact the site administrator.</p>'); 
		}
	});
});

// function to show a map
function showContributorMap(id) {
	// show the map container
	$("#map_header").show();
	$("#map").show();
	$("#map_legend").show();
	$("#map_footer").show();
	
	// update values in the advanced map options form
	$("#adv_map_org_id").val(id);
	$("#adv_map_state").val($("#state").val());
	
	// update the persistent link
	$("#map_header_link").attr("href", "maplinks.jsp?type=contrib&id=" + id);
	$("#map_header_link").show();
	
	// update the download as KML link
	$("#map_header_kml").attr("href", "data?action=kml&type=contrib&id=" + id);
	
	// update the export KML link
	$("#map_header_export").attr("href", "exportdata.jsp?type=contrib&id=" + id);
	
	if(id.indexOf(',',0) == -1) {
		
		// uncheck all of the checkboxes
		$('input:checkbox').attr('checked', false);
	}		
	
	// get the marker xml data
	$.get("data?action=markers&type=contributor&id=" + id + "&state=" + $("#state").val(), function(data) {
		
		// show the map
		showMap2(data, null, $("#state").val(), null, null);
		
		// build the time slider
		buildTimeSlider(data);
		
		// store reference to marker data for reuse
		contributorMapData = data;
	});
}

// function to map multiple contributors
function multiContribMap() {

	// update the header
	$("#map_header h3").empty();
	$("#map_header h3").append("Map of events for multiple contributors");
	
	// select all of the checkboxes
	var checkboxes = $('input:checkbox').serializeArray();
	var ids = ""; // store a list of ids
	
	// loop through looking for contributor checkboxes
	for (var i = 0; i < checkboxes.length; i++) {
		if(checkboxes[i].name == "contributor") {
			ids += checkboxes[i].value + ',';
		}
	}
	
	// check to see if we need to tidy the id string
	if(ids.indexOf(',',0) != -1) {
		// tidy up the list of ids
		ids = ids.substr(0, ids.length -1);
	}
	
	// check on the list of ids
	if(ids == "") {
		alert("You must select at least one contributor");
		return false;
	}
	
	// build the map
	showContributorMap(ids);
	
	return false;
}

// function to reload a map
function reloadMap() {
	
	// check to ensure map data is present
	if(contributorMapData == null) {
		$("#map").empty();
		$("#map").append('<p style="text-align: center"><strong>Error: </strong>An error occured whilst loading markers, please start again.<br/>If the problem persists please contact the site administrator.</p>'); 
	}

	// get the show trajectory option
	var showTraj = $("#show_trajectory:checked").val();
	
	// get the start date
	var startDate  = $("#event_start").val();
	var finishDate = $("#event_finish").val();
	
	// determine if the trajectory option is set
	if(showTraj != null) {
		// reload the map with trajectory information
		showMap2(contributorMapData, true, $("#state").val(), startDate, finishDate);
	} else {
		// reload the map with trajectory information
		showMap2(contributorMapData, false, $("#state").val(), startDate, finishDate);
	}
}

// function to load the maplinks page
// function to load the map in the new page
function loadNewPage(responseText, statusText) {
	window.location = "maplinks.jsp?type=contrib&id=" + $("#id").val();
}
