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
var BASE_URL = "/mapping2/";
var DEFAULT_SEARCH_LIMIT = "25";
var UPDATE_DELAY = 500;

var searching_underway_flag = false;
var search_history_log = [ ];
var error_condition = false;

var LIMIT_REACHED_MSG = '<div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;"><p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>The limit of ' + DEFAULT_SEARCH_LIMIT + ' records has been reached.<br/>Please adjust your search query if the result you are looking for is missing.</p></di>';

var AJAX_ERROR_MSG    = 'An unexpected error occured during -, please try again. If the problem persists contact the AusStage team.';

var ID_SEARCH_TOKEN   = "id:";
 
// show / hide the menu
$(document).ready(function(){

	// hide the messages div
	$("#messages").hide();
	
	// prevent a FOUC
	$('html').removeClass('js');

	// add a click handler to the hide menu link
	$("#hidePanel").click(function(){
		$("#panel").animate({marginLeft:"-175px"}, 500 );
		$("#sidebar").animate({width:"0px", opacity:0}, 400 );
		$("#showPanel").show("normal").animate({width:"18px", opacity:1}, 200);
		$("#main").animate({marginLeft:"30px"}, 500);
		$("#tabs").animate({width:"100%"});
	});
	
	// add a click handler to the show menu link
	$("#showPanel").click(function(){
		$("#main").animate({marginLeft:"200px"}, 200);
		$("#tabs").animate({width:"100%"});
		$("#panel").animate({marginLeft:"0px"}, 400 );
		$("#sidebar").animate({width:"175px", opacity:1}, 400 );
		$("#showPanel").animate({width:"0px", opacity:0}, 600).hide("slow");
	});
	
	// setup the tabs
	$("#tabs").tabs();
	
	// style the buttons
	$("button, input:submit").button();
	
	// associate the tipsy library with the form elements
	$('#search [title]').tipsy({trigger: 'focus', gravity: 'n'});
	
	// setup the accordian
	$(".accordion").accordion({collapsible:true, active:false, autoHeight: false });
	
	// setup a handler for the start of an ajax request
	$("#message_text").ajaxSend(function(e, xhr, settings) {
		// determine what type of request has been made & update the message text accordingly
		// ensure that we're only working on searches
		if(settings.url.indexOf("search?", 0) != -1) {
			if(settings.url.indexOf("contributor", 0) != -1) {
				$(this).text("Contributor search is underway...");
			} else if(settings.url.indexOf("organisation", 0) != -1) {
				$(this).text("Organisation search is underway...");
			} else if(settings.url.indexOf("venue", 0) != -1) {
				$(this).text("Venue search is underway...");
			} else if(settings.url.indexOf("event", 0) != -1) {
				$(this).text("Event search is underway...");
			}
		}
	});
	
	// set up handler for when an ajax request results in an error
	$("#error_text").ajaxError(function(e, xhr, settings, exception) {
		// determine what type of request has been made & update the message text accordingly
		// ensure that we're only working on searches
		if(settings.url.indexOf("search?", 0) != -1) {
			if(error_condition == false) {
				if(settings.url.indexOf("contributor", 0) != -1) {
					$(this).text(AJAX_ERROR_MSG.replace('-', 'the contributor search'));
				} else if(settings.url.indexOf("organisation", 0) != -1) {
					$(this).text(AJAX_ERROR_MSG.replace('-', 'the organisation search'));
				} else if(settings.url.indexOf("venue", 0) != -1) {
					$(this).text(AJAX_ERROR_MSG.replace('-', 'the venue search'));
				} else if(settings.url.indexOf("event", 0) != -1) {
					$(this).text(AJAX_ERROR_MSG.replace('-', 'the event search'));
					searching_underway_flag = false;
				}
				error_condition = true;
			} else {
				$(this).text(AJAX_ERROR_MSG.replace('-', 'multiple searches'));
				searching_underway_flag = false;
			}
			
			// show the error message
			$("#error_message").show();
		}
	});
	
	// create a custom validator for validating id messages
	jQuery.validator.addMethod("validIDSearch", function(value, element) {
	
		// check to see if this is an id search query
		if(value.substr(0,3) != ID_SEARCH_TOKEN) {
			// return true as nothing to do
			return true;
		} else {
			// found the id token so we need to validate it
			if(isNaN(value.substr(3)) == true) {
				return false;
			} else {
				return true;
			}
		}
	}, 'An ID search must start with "id:" and be followed by a valid integer');
	
			
	
	// setup the search form
	$("#search").validate({
		rules: { // validation rules
			query: {
				required: true,
				validIDSearch: true
			}
		}, submitHandler: function(form) {
			// indicate that the search is underway
			$("#messages").show();
			$("#status_message").show();
			
			searching_underway_flag = true;
			setTimeout("updateMessages()", UPDATE_DELAY);
		
			// clear the accordian of any past search results
			clearAccordian();
			
			// set up the ajax queue
			var ajaxQueue = $.manageAjax.create("mappingAjaxQueue", {
				queue: true
			});
			
			// undertake the searches in order
			var base_search_url = '';
			var query           = $("#query").val();
			
			if(query.substr(0,3) != ID_SEARCH_TOKEN) {
				// this is a name search
				base_search_url = BASE_URL + "search?type=name&limit=" + DEFAULT_SEARCH_LIMIT + "&query=" + encodeURIComponent(query);
			} else {
				// this is an id search 
				base_search_url = BASE_URL + "search?type=id&query=" + encodeURIComponent(query.substr(3).replace(/^\s\s*/, '').replace(/\s\s*$/, ''));
			}			
						
			// do a contributor search
			var url = base_search_url + "&task=contributor";
			
			// queue this request
			ajaxQueue.add({
				success: buildContributorSearchResults,
				url: url
			});
			
			// do a organisation search
			url = base_search_url + "&task=organisation";
			
			// queue this request
			ajaxQueue.add({
				success: buildOrganisationSearchResults,
				url: url
			});
			
			// do a venue search
			url = base_search_url + "&task=venue";
			
			// queue this request
			ajaxQueue.add({
				success: buildVenueSearchResults,
				url: url
			});
			
			// do an event search
			url = base_search_url + "&task=event";
			
			// queue this request
			ajaxQueue.add({
				success: buildEventSearchResults,
				url: url
			});
			
			// add to the search history if necessary
			if(jQuery.inArray($("#query").val(), search_history_log) == -1) {
				$("#search_history").append('<li><a href="#" onclick="doSearch(\'' + $("#query").val() + '\'); return false;" title="Click to Repeat the Search">Repeat search for: ' + $("#query").val() + '</a> / <a href="' + BASE_URL + 'mapping.jsp?search=true&query=' + encodeURIComponent($("#query").val()) + '" title="Persistent Link for this Search">Persistent Link</a></li>');
				search_history_log.push($("#query").val());
			}
			
		}
	});
	
	// check to see if this is a persistent link request
	var searchParam = $.getUrlVar("search");
	
	if(typeof(searchParam) != "undefined") {
		
		// get parameters
		var queryParam = $.getUrlVar("query");
		
		// check on the parameters
		if(typeof(queryParam) == "undefined") {
			
			// show a message as the query parameters are missing
			$("#message_text").text("Error: The persistent URL for this search is incomplete, please try again");
			$("#messages").show();
		} else {
			doSearch(queryParam);
		}
	}
});

// a function to initiate a search result
function doSearch(searchTerm) {
	$("#query").val(decodeURIComponent(searchTerm));
	$("#search").submit();
}

// a function to clear the accordian of any existing search results
function clearAccordian() {

	// clear each of the sections of the accordian in turn
	$("#contributor_results").empty();
	$("#organisation_results").empty();
	$("#venue_results").empty();
	$("#event_results").empty();
	
	// reset the headings
	$("#contributor_heading").empty().append("Contributors");
	$("#organisation_heading").empty().append("Organisations");
	$("#venue_heading").empty().append("Venues");
	$("#event_heading").empty().append("Events");

	// reset the error div
	$("#error_message").hide();
	
	// reset the error flag
	error_condition = false;
};

// a function to hide the messages div if required
function updateMessages() {

	if(searching_underway_flag != true) {
		// indicate that the search has finished
		if(error_condition == false) {
			$("#messages").hide();
		} else {
			$("#status_message").hide();
		}
	} else {
		setTimeout("updateMessages()", UPDATE_DELAY);
	}
}	

// a function to build the contributor search results
function buildContributorSearchResults(data) {

	var list = "<ul>";
	var i = 0;
	
	for(i; i < data.length; i++) {
		list += "<li>";
		list += '<a href="' + data[i].url + '" title="View the record for ' + data[i].firstName + " " + data[i].lastName + ' in AusStage" target="_ausstage">' + data[i].firstName + " " + data[i].lastName + '</a> <span title="Events to be Mapped">' + data[i].mapEventCount + '</span> / <span title="Total Events">' + data[i].totalEventCount + "</span>"
		list += "</li>";
	}
	
	list += "</ul>";
	
	$("#contributor_results").append(list);
	
	if(i == 25) {
		$("#contributor_results").append(LIMIT_REACHED_MSG);
		$("#contributor_heading").empty().append("Contributors (25+)");
	} else {
		$("#contributor_heading").empty().append("Contributors (" + i + ")");
	}

}

// a function to build the organisation search results
function buildOrganisationSearchResults(data) {

	var list = '<table class="searchResults"><thead><tr><th>Organisation Name</th><th>Address</th><th>Mapped Events</th><th>Total Events</th><th>&nbsp;</th></tr></thead><tbody>';
	
	var i = 0;
	
	for(i; i < data.length; i++) {
	
		if(i % 2 == 1) {
			list += '<tr class="odd">'; 
		} else {
			list += '<tr>'; 
		}
		
		list += '<td><a href="' + data[i].url + '" title="View the record for ' + data[i].name + ' in AusStage" target="_ausstage">' + data[i].name + '</a></td>';
		
		if(data[i].address != null) {
			list += '<td>' + data[i].address + '</td>';
		} else {
			list += '<td>&nbsp;</td>';
		}
		
		list += '<td>' + data[i].mapEventCount + '</td><td>' + data[i].totalEventCount + '</td>';
		
		if(data[i].mapEventCount > 0) {
			list += '<td><input type="checkbox"/></td>';
		} else {
			list += '<td>&nbsp;</td>';
		}
		
		list += '</tr>';
	}
	
	list += '</table>';
	
	$("#organisation_results").append(list);
	
	if(i == 25) {
		$("#organisation_results").append(LIMIT_REACHED_MSG);
		$("#organisation_heading").empty().append("Organisations (25+)");
	} else {
		$("#organisation_heading").empty().append("Organisations (" + i + ")");
	}

}

// a function to build the contributor search results
function buildVenueSearchResults(data) {

	var list = "<ul>";
	
	var i = 0;
	
	for(i; i < data.length; i++) {
		list += "<li>";
		list += '<a href="' + data[i].url + '" title="View the record for ' + data[i].name + ' in AusStage" target="_ausstage">' + data[i].name + ", " + data[i].suburb + '</a> <span title="Total Events">' + data[i].totalEventCount + "</span>";
		list += "</li>";
	}
	
	list += "</ul>";
	
	$("#venue_results").append(list);
	
	if(i == 25) {
		$("#venue_results").append(LIMIT_REACHED_MSG);
		$("#venue_heading").empty().append("Venues (25+)");
	} else {
		$("#venue_heading").empty().append("Venues (" + i + ")");
	}

}

// a function to build the contributor search results
function buildEventSearchResults(data) {

	var list = "<ul>";
	
	var i = 0;
	
	for(i; i < data.length; i++) {
		list += "<li>";
		list += '<a href="' + data[i].url + '" title="View the record for ' + data[i].name + ' in AusStage" target="_ausstage">' + data[i].name + '</a>';
		list += "</li>";
	}
	
	list += "</ul>";

	$("#event_results").append(list);
	
	if(i == 25) {
		$("#event_results").append(LIMIT_REACHED_MSG);
		$("#event_heading").empty().append("Events (25+)");
	} else {
		$("#event_heading").empty().append("Events (" + i + ")");
	}
	
	// update the search underway flag
	searching_underway_flag = false;
}
