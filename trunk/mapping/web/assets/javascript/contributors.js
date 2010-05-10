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
var contributorMapData = null
var contributorIDs = null;

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
	
	$("#to_map_list").hide();	
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
function showContributorMap(id, contrib, url) {
	// show the map container
	$("#map_header").show();
	$("#map").show();
	$("#map_legend").show();
	$("#map_footer").show();

	// update the map heading
	if(contrib != null) {
		$("#map_heading").empty();
		$("#map_heading").append('Map of Events for <a href="' + url + '" target="ausstage">' + contrib + '</a>');
		$("#to_map_list").hide();
	} else {
		$("#map_heading").empty();
		$("#map_heading").append('Map of Events for multiple contributors');
	}
	
	// update the download as KML link
	$("#map_header_kml").attr("href", "data?action=kml&type=contrib&id=" + id);
	
	// update the export KML link
	$("#map_header_export").attr("href", "exportdata.jsp?type=contrib&id=" + id);	
	
	// get the marker xml data
	//$.get("data?action=markers&type=contributor&id=" + id, function(data) {
	$.get("data?action=markers&type=contributor&id=2256,580", function(data) {
		
		// show the map
		showMap3(data, null, $("#state").val(), null, null);
		
		// build the time slider
		buildTimeSlider(data);
		
		// store reference to marker data for reuse
		contributorMapData = data;
	});
}

// function to add contribtor to the list of contributors
function addContrib(id, contrib, url) {

	// hide the map
	hideMap();

	// get the number of rows
	var rows = $("#contrib_list").attr('rows').length;
	var j = 2;
	
	if(rows % j == 1) {
		$('#contrib_list > tbody:last').append('<tr class="odd" id="list' + id + '"><td><a href="' + url + '" target="ausstage">' + contrib + '</a></td><td><input class="ui-state-default ui-corner-all button" type="button" onclick="delContrib(\'' + id + '\'); return false;" value="Delete"/></td></tr>');
	} else {
		$('#contrib_list > tbody:last').append('<tr id="list' + id + '"><td><a href="' + url + '" target="ausstage">' + contrib + '</a></td><td><input class="ui-state-default ui-corner-all button" type="button" onclick="delContrib(\'' + id + '\'); return false;" value="Delete"/></td></tr>');
	}

	if(contributorIDs == null) {
		contributorIDs = id;
	} else {
		contributorIDs += ',' + id;
	}
	
	$("#to_map_list").show();
	
	alert(contrib + " has been added to the list of contributors");
	
	// override default form behaviour
	return false;
}

// function to delete a contributor
function delContrib(id) {

	hideMap();
	
	$("#list" + id).remove();
	
	contributorIDs = contributorIDs.replace(id, "");
	contributorIDs = contributorIDs.replace(',,', ',');
	
	return false;
}

// function to build a map from a list of contributors
function buildListMap() {

	// update the header
	$("#map_header h3").empty();
	$("#map_header h3").append("Map of events for multiple contributors");

	// build the map
	showContributorMap(contributorIDs, null, null);

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
