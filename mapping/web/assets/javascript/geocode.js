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

// validate the search forms
$(document).ready(function(){
	// attach the validation plugin to the search form
	$("#org_search_form").validate({
		rules: { // validation rules
			search_term: {
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
	
	// attach the validation plugin to the search form
	$("#venue_search_form").validate({
		rules: { // validation rules
			search_term: {
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
	
	// hide the loading div
	hideLoader();
});

// functions for showing and hiding the loading message
function showLoader() {
	$("#search_waiting").show();
	$("#search_results").hide();
	$("#search_results").empty();
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

// function to load a list of venues based on organisation id
function showVenues(orgId) {

	// show the loading message
	showLoader();
	
	// get the venue information
	$.ajax({ url: "geocode/", 
	         type: "POST",
	         data: {action: "org_venue_search", search_term: orgId}, 
	         error: showErrorMessage,
	         success: showSearchResults});
	         
	return false;

}

// prepare the dialog div to show venue information
$(document).ready(function(){

	// set common options
	var options = {autoOpen: false, bgiframe: true, height: 400, width: 700, modal: true, title: "Venue Information",  buttons: { "Close": function() { $(this).dialog("close"); }}};
	
	// setup the dialogs
	$("#venue_info").dialog(options);
});

// load venue information into the dialog 
function showVenue(venueId) {

	// emtpy the dialog
	$("#venue_info").empty();
	
	// get the venue information
	$.ajax({ url: "geocode/", 
	         type: "GET",
	         data: {action: "lookup", type: "venue", id: venueId}, 
	         error: showErrorMessage,
	         success: function(responseText, statusText) {
	         	$("#venue_info").append(responseText);
	         	$("#venue_info").dialog("open");
	         }
	       });
	
	return false;
}
