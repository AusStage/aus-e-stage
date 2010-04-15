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
			contributor_id: {
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
	$("#reload_map").click(advFormSubmit);
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

// function to show a map
function showContributorMap(id) {
	alert(id);
}

// function to map multiple contributors
function multiContribMap() {
	$("#map_header h3").empty();
	$("#map_header h3").append("Map of events for multiple contributors");
	
	return false;
}

function advFormSubmit() {

}
