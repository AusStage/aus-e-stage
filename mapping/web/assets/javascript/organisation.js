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

// validate the search form
$(document).ready(function(){
	// attach the validation plugin to the name search form
	$("#org_search_form").validate({
		rules: { // validation rules
			org_name: {
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
	$("#org_id_search_form").validate({
		rules: { // validation rules
			org_id: {
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
	$("#map").hide();
	$("#map_header").hide();
	$("#map_legend").hide();
	$("#map_footer").hide();
});

// override the advanced map display functionality with our own function
$(document).ready(function() {

	$("#reload_map").click(advFormSubmit);
});

// set up the tabbed interface
$(document).ready(function() {
	$("#tabs").tabs({ 
		cookie: { 
			name: 'org_search_tab', 
			expires: 30
		},
		select: function(event, ui) { 
			$("#search_results").hide(); 
		} 
	}); 
});

// functions for showing and hiding the loading message
function showLoader() {
	$("#search_waiting").show();
	
	$("#search_results").hide();
	$("#search_results").empty();
	
	$("#map_header").hide();
	$("#map").hide();
	$("#map_legend").hide();
	$("#map_footer").hide();
	
	GUnload();	
}

function hideLoader() {
	$("#search_waiting").hide();
}

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

// function to load and show the map
function showOrgMap(orgId) {
	
	// show the map container
	$("#map_header").show();
	$("#map").show();
	$("#map_legend").show();
	$("#map_footer").show();
	
	// update values in the advanced map options form
	$("#adv_map_org_id").val(orgId);
	$("#adv_map_state").val($("#state").val());
	
	// use reusable function to show and load the map
	showMap('org', orgId, null, null, null, $("#state").val());
	
	// get the data to populate the selectboxes
	$.getJSON("data?action=lookup&type=startdates&id=" + orgId, function(data) {
	
		// remove any existing options
		$("#event_start").removeOption(/./);
		$("#event_finish").removeOption(/./);
		
		// add the new options
		$("#event_start").addOption(data, false);
		$("#event_finish").addOption(data, false);
		
		// clear any current selected values
		$("#event_start").selectOptions('clear');
		$("#event_finish").selectOptions('clear');
		
		// select the last option of the event_finish select
		$("#event_start option:last").attr("selected", "selected");
		
		// remove any existing slider
		$("#sliderComponent").remove();
		
		// create the slider
		$(".slider").selectToUISlider({labels: 10}).hide();
		$(".tohide").hide();
	});
	
	return false;
}

// function to take action when the advanced map options form is submitted
function advFormSubmit() {

	// get the org id
	var orgId = $("#adv_map_org_id").val();
	
	// get the show trajectory option
	var showTraj = $("#show_trajectory:checked").val();
	
	// get the start date
	var startDate = $("#event_start").val();
	var finishDate = $("#event_finish").val();
	
	// get the state limit
	var stateLimit = $("#adv_map_state").val();
	
	// use the reusable function to update the map
	if(showTraj != null) {
		if(stateLimit != 'nolimit') {
			showMap('org', orgId, "true", startDate, finishDate, stateLimit);
		} else {
			showMap('org', orgId, "true", startDate, finishDate);
		}
	} else {
		if(stateLimit != 'nolimit') {
			showMap('org', orgId, "false", startDate, finishDate, stateLimit);
		} else {
			showMap('org', orgId, "false", startDate, finishDate);
		}
	}
	
	// override default action on the form
	return false;
}

// function to load the map in the new page
function loadNewPage(responseText, statusText) {

	if(responseText != "not found") {
		window.location = responseText;
	} else {
		showSearchResults("<p><strong>Error: </strong>No organisations were found matching the organisation id provided.</p>");
	}
}

// add clue tips to the form labels
$(document).ready(function() {
	$('#org_name_label').cluetip({local:true, attribute: 'class', showTitle: false});
	$('#operator_label').cluetip({local:true, attribute: 'class', showTitle: false});
	$('#state_label').cluetip({local:true, attribute: 'class', showTitle: false});
});
