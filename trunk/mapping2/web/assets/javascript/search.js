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

// define a search tracker class
function SearchTrackerClass() {

	// keep a track of search terms for ease of comparison
	this.history_log = [];
	
	// keep a track of the search result counts
	this.contributor_count  = 0;
	this.organisation_count = 0;
	this.venue_count        = 0;
	this.event_count        = 0;
}

// define the search class
function SearchClass () {
	
	// define a flag to indicate if a search is underway
	this.searching_underway_flag = false;
	
	// determine if an error has occured
	this.error_condition = false;
	
	// define the default search result limit
	this.DEFAULT_SEARCH_LIMIT = "25";
	
	// declare a constant for use when the search limit has been reached
	this.LIMIT_REACHED_MSG = '<div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;"><p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>The limit of ' + this.DEFAULT_SEARCH_LIMIT + ' records has been reached.<br/>Please adjust your search query if the result you are looking for is missing.</p></di>';
	
	// define the token used to identify an ID search
	this.ID_SEARCH_TOKEN   = "id:";
	
	// define the minimum query size
	this.MIN_QUERY_LENGTH = 4;
	
	// keep track of various search related stuff
	this.trackerObj = new SearchTrackerClass();
	
}

// undertake a search
SearchClass.prototype.doSearch = function(searchTerm) {
	$("#query").val(decodeURIComponent(searchTerm));
	$("#search").submit();
}

// hide the search notes
SearchClass.prototype.hideNotes = function() {
	$("#search_notes").hide('slow');
	$("#search_notes_toggle").empty().append("(Show Notes)");
	searchObj.show_notes = false;
	$.cookie('show_search_notes', 'false');
}

// show the search notes
SearchClass.prototype.showNotes = function() {
	$("#search_notes").show('slow');
	$("#search_notes_toggle").empty().append("(Hide Notes)");
	searchObj.show_notes = true;
	$.cookie('show_search_notes', null);
}

// check to see if the cookie is set
SearchClass.prototype.checkNoteCookie = function() {
	if($.cookie('show_search_notes') == 'false') {
		searchObj.hideNotes();
	}
}

// clear the accordians related to the searching functionality
SearchClass.prototype.clearAccordian = function() {

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

// a function to hide the search messages div if required
SearchClass.prototype.updateMessages = function() {

	if(searchObj.searching_underway_flag != true) {
		// indicate that the search has finished
		if(error_condition == false) {
			$("#messages").hide();
		} else {
			$("#status_message").hide();
		}
	} else {
		setTimeout("searchObj.updateMessages()", UPDATE_DELAY);
	}
}

//define a method of the search class to build the contributor search results
SearchClass.prototype.buildContributorResults = function(data) {

	var list = '<table class="searchResults"><thead><tr><th style="text-align: center"><input type="checkbox" name="selectContributorSearchAll" id="selectContributorSearchAll" class="selectSearchAll" title="Tick / Un-Tick all"/></th><th>Name</th><th>Event Dates</th><th>Functions</th><th class="alignRight">Mapped Events</th><th class="alignRight">Total Events</th></tr></thead><tbody>';
	
	var i = 0;
	
	for(i; i < data.length; i++) {
		
		if(i % 2 == 1) {
			list += '<tr class="odd">'; 
		} else {
			list += '<tr>'; 
		}
		
		if(data[i].mapEventCount > 0) {
			list += '<td style="text-align: center"><input type="checkbox" name="searchContributor" class="searchContributor" value="' + data[i].id + '" title="Tick to add this contributor to the map"/></td>';
		} else {
			list += '<td style="text-align: center"><input type="checkbox" disabled/></td>';
		}
		
		list += '<td class="nowrap"><a href="' + data[i].url + '" title="View the record for ' + data[i].firstName + " " + data[i].lastName + ' in AusStage" target="_ausstage">' + data[i].firstName + " " + data[i].lastName + '</a></td>';
		list += '<td class="nowrap">' + data[i].eventDates + '</td><td>';
		
		if(data[i].functions.length > 0) {
			for(var x = 0; x < data[i].functions.length; x++) {
				list += data[i].functions[x] + ', ';
			}
			
			list = list.substr(0, list.length - 2);
		} else {
			list += "&nbsp;";
		}
		
		list += '</td><td class="alignRight">' + data[i].mapEventCount + '</td><td class="alignRight">' + data[i].totalEventCount + '</td>';
		
		list += '</tr>';
	}
	
	// add the button
	list += '</tbody><tfoot><tr><td colspan="6" class="alignRight"><button id="searchAddContributors" class="addSearchResult">Add to Map</button>' + ADD_VIEW_BTN_HELP + '</td></tr></tfoot></table>';
	
	if(i > 0) {
		$("#contributor_results").append(list);
		styleButtons();
	}
	
	if(i == 25) {
		$("#contributor_results").append(searchObj.LIMIT_REACHED_MSG);
		$("#contributor_heading").empty().append("Contributors (25+)");
	} else {
		$("#contributor_heading").empty().append("Contributors (" + i + ")");
	}
	
	// keep track of the number of search results
	searchObj.trackerObj.contributor_count = i;

}

// define a method of the search class to build the organisation search results
SearchClass.prototype.buildOrganisationResults = function(data) {

	var list = '<table class="searchResults"><thead><tr><th style="text-align: center"><input type="checkbox" name="selectOrganisationSearchAll" id="selectOrganisationSearchAll" class="selectSearchAll" title="Tick / Un-Tick all"/></th><th>Organisation Name</th><th>Address</th><th class="alignRight">Mapped Events</th><th class="alignRight">Total Events</th></tr></thead><tbody>';
	
	var i = 0;
	
	for(i; i < data.length; i++) {
	
		if(i % 2 == 1) {
			list += '<tr class="odd">'; 
		} else {
			list += '<tr>'; 
		}
		
		if(data[i].mapEventCount > 0) {
			list += '<td style="text-align: center"><input type="checkbox" name="searchOrganisation" class="searchOrganisation" value="' + data[i].id + '" title="Tick to add this organisation to the map"/></td>';
		} else {
			list += '<td style="text-align: center"><input type="checkbox" disabled/></td>';
		}
		
		list += '<td><a href="' + data[i].url + '" title="View the record for ' + data[i].name + ' in AusStage" target="_ausstage">' + data[i].name + '</a></td>';
		
		list += '<td>';
		
		if(data[i].street != null) {
			 list += data[i].street + ', ';
		} 
		
		if(data[i].suburb != null) {
			list += data[i].suburb + ', ';
		}
		
		if(data[i].state != null) {
			list += data[i].state;
		} else {
			list = list.substr(0, list.length - 2);
		}
		
		list += '</td><td class="alignRight">' + data[i].mapEventCount + '</td><td class="alignRight">' + data[i].totalEventCount + '</td>';
		
		list += '</tr>';
	}
	
	// add the button
	list += '</tbody><tfoot><tr><td colspan="5" style="text-align: right"><button id="searchAddOrganisations" class="addSearchResult">Add to Map</button>' + ADD_VIEW_BTN_HELP + '</td></tr></tfoot></table>';
	
	if(i > 0) {
		$("#organisation_results").append(list);
		styleButtons();
		
	}
	
	if(i == 25) {
		$("#organisation_results").append(searchObj.LIMIT_REACHED_MSG);
		$("#organisation_heading").empty().append("Organisations (25+)");
	} else {
		$("#organisation_heading").empty().append("Organisations (" + i + ")");
	}
	
	// keep track of the number of search results
	searchObj.trackerObj.organisation_count = i;

}

// define a method of the search class to build the venue search results
SearchClass.prototype.buildVenueResults = function (data) {

	var list = '<table class="searchResults"><thead><tr><th style="text-align: center"><input type="checkbox" name="selectVenueSearchAll" id="selectVenueSearchAll" class="selectSearchAll" title="Tick / Un-Tick all"/></th><th>Venue Name</th><th>Address</th><th class="alignRight">Total Events</th></tr></thead><tbody>';
	
	var i = 0;
	
	for(i; i < data.length; i++) {
		
		// style odd rows differently
		if(i % 2 == 1) {
			list += '<tr class="odd">'; 
		} else {
			list += '<tr>'; 
		}
		
		if(data[i].latitude != null) {
			list += '<td style="text-align: center"><input type="checkbox" name="searchVenue" class="searchVenue" value="' + data[i].id + '" title="Tick to add this venue to the map"/></td>';
		} else {
			list += '<td style="text-align: center"><input type="checkbox" disabled/></td>';
		}
		
		list += '<td><a href="' + data[i].url + '" title="View the record for ' + data[i].name + ' in AusStage" target="_ausstage">' + data[i].name + '</a></td>';
		list += '<td>';
		
		if(data[i].street != null) {
			 list += data[i].street + ', ';
		} 
		
		if(data[i].suburb != null) {
			list += data[i].suburb + ', ';
		}
		
		if(data[i].state != null) {
			list += data[i].state;
		} else {
			list = list.substr(0, list.length - 2);
		}
		
		list += '</td><td class="alignRight">' + data[i].totalEventCount + '</td>';
		
		list += '</tr>';
	}
	
	// add the button
	list += '</tbody><tfoot><tr><td colspan="4" style="text-align: right"><button id="searchAddVenues" class="addSearchResult">Add to Map</button>' + ADD_VIEW_BTN_HELP + '</td></tr></tfoot></table>';
	
	if(i > 0) {
		$("#venue_results").append(list);
		styleButtons();
	}
	
	if(i == 25) {
		$("#venue_results").append(searchObj.LIMIT_REACHED_MSG);
		$("#venue_heading").empty().append("Venues (25+)");
	} else {
		$("#venue_heading").empty().append("Venues (" + i + ")");
	}
	
	// keep track of the number of search results
	searchObj.trackerObj.venue_count = i;

}

// define a method of the search class to build the event search results
SearchClass.prototype.buildEventResults = function (data) {

	var list = '<table class="searchResults"><thead><tr><th style="text-align: center"><input type="checkbox" name="selectEventSearchAll" id="selectEventSearchAll" class="selectSearchAll" title="Tick / Un-Tick all"/></th><th>Event Name</th><th>Venue</th><th class="alignRight">First Date</th></tr></thead><tbody>';
	
	var i = 0;
	
	for(i; i < data.length; i++) {
	
		// style odd rows differently
		if(i % 2 == 1) {
			list += '<tr class="odd">'; 
		} else {
			list += '<tr>'; 
		}
		
		if(data[i].venue.latitude != null) {
			list += '<td style="text-align: center"><input type="checkbox" name="searchEvent" class="searchEvent" value="' + data[i].id + '" title="Tick to add this event to the map"/></td>';
		} else {
			list += '<td style="text-align: center"><input type="checkbox" disabled/></td>';
		}
		
		list += '<td><a href="' + data[i].url + '" title="View the record for ' + data[i].name + ' in AusStage" target="_ausstage">' + data[i].name + '</a></td>';
		
		list += '<td class="nowrap">' + data[i].venue.name + ' ';
				
		if(data[i].venue.suburb != null) {
			list += data[i].venue.suburb + ', ';
		}
		
		if(data[i].venue.state != null) {
			list += data[i].venue.state + ', ';
		}
		
		if(data[i].venue.suburb != null || data[i].venue.state != null) {
			list = list.substr(0, list.length - 2);
		}
		
		list += '</td><td class="nowrap alignRight">' + data[i].firstDisplayDate + '</td>';
		
		list += '</tr>';
	}
	
	// add the button
	list += '</tbody><tfoot><tr><td colspan="4" class="alignRight"><button id="searchAddEvents" class="addSearchResult">Add to Map</button>' + ADD_VIEW_BTN_HELP + '</td></tr></tfoot></table>';

	if(i > 0) {
		$("#event_results").append(list);
		styleButtons();
	}
	
	if(i == 25) {
		$("#event_results").append(searchObj.LIMIT_REACHED_MSG);
		$("#event_heading").empty().append("Events (25+)");
	} else {
		$("#event_heading").empty().append("Events (" + i + ")");
	}
	
	// update the search underway flag
	searchObj.searching_underway_flag = false;
	
	// keep track of the number of search results
	searchObj.trackerObj.event_count = i;
	
	// add to the search history if necessary
	if(jQuery.inArray($("#query").val(), searchObj.trackerObj.history_log) == -1) {
	
		// add the new query
		searchObj.trackerObj.history_log.push($("#query").val());
		
		var row;
		
		if(searchObj.trackerObj.history_log.length % 2 == 1) {
			row = '<tr class="odd">'; 
		} else {
			row = '<tr>';
		}
	
		// buld the new table row
		row += '<td><a href="#" onclick="searchObj.doSearch(\'' + $("#query").val() + '\'); return false;" title="Click to Repeat this Search" class="use-tipsy">' + $("#query").val() + '</a></td>';
		row += '<td><a href="' + BASE_URL + 'mapping.jsp?search=true&query=' + encodeURIComponent($("#query").val()) + '" title="Persistent Link for this Search" class="use-tipsy">link</a></td>';
		row += '<td><span class="use-tipsy" title="Contributors"> ' + searchObj.trackerObj.contributor_count + ' </span>/<span class="use-tipsy" title="Organisations"> ' + searchObj.trackerObj.organisation_count + ' </span>/<span class="use-tipsy" title="Venues"> ' + searchObj.trackerObj.venue_count + ' </span>/<span class="use-tipsy" title="Events"> ' + searchObj.trackerObj.event_count + '</span></td></tr>';
		
		// insert the new row in the table
		$(row).insertAfter('#search_history');
		
		$('.use-tipsy').tipsy({trigger: 'focus', gravity: 'n'});
		
	}
}

// define a method of the search class to respond to the click event
// of the add to map button
SearchClass.prototype.addToMapClickEvent = function(event) {


}

// define a method of the search class to respnd to the click event
// of the select all check box
SearchClass.prototype.selectAllClickEvent = function(event) {

	// determine which checkbox was clicked
	var target = $(event.target);
	if(target.attr('id') == 'selectContributorSearchAll') {
		
		if(target.is(':checked') == false) {
			$(".searchContributor:checkbox").each(function() {
				$(this).attr('checked', false);
			});
		} else {
			$(".searchContributor:checkbox").each(function() {
				$(this).attr('checked', true);
			});
		}
	} else if(target.attr('id') == 'selectOrganisationSearchAll') {
		
		if(target.is(':checked') == false) {
			$(".searchOrganisation:checkbox").each(function() {
				$(this).attr('checked', false);
			});
		} else {
			$(".searchOrganisation:checkbox").each(function() {
				$(this).attr('checked', true);
			});
		}
	} else if(target.attr('id') == 'selectVenueSearchAll') {
		
		if(target.is(':checked') == false) {
			$(".searchVenue:checkbox").each(function() {
				$(this).attr('checked', false);
			});
		} else {
			$(".searchVenue:checkbox").each(function() {
				$(this).attr('checked', true);
			});
		}
	} else if(target.attr('id') == 'selectEventSearchAll') {
		
		if(target.is(':checked') == false) {
			$(".searchEvent:checkbox").each(function() {
				$(this).attr('checked', false);
			});
		} else {
			$(".searchEvent:checkbox").each(function() {
				$(this).attr('checked', true);
			});
		}
	}
}
