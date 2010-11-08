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

// define the search class
function SearchClass () {
	
	// define a flag to indicate if a search is underway
	this.searching_underway_flag = false;
	
	// keep a log of past searches
	this.search_history_log = [ ];
	
	// determine if an error has occured
	this.error_condition = false;
	
	// define the default search result limit
	this.DEFAULT_SEARCH_LIMIT = "25";
	
	// declare a constant for use when the search limit has been reached
	this.LIMIT_REACHED_MSG = '<div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;"><p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>The limit of ' + this.DEFAULT_SEARCH_LIMIT + ' records has been reached.<br/>Please adjust your search query if the result you are looking for is missing.</p></di>';
	
	// define the tocken used to identify an ID search
	this.ID_SEARCH_TOKEN   = "id:";
	
	// keep a track of the items to add
	this.items_to_add = [];
}

// undertake a search
SearchClass.prototype.doSearch = function(searchTerm) {
	$("#query").val(decodeURIComponent(searchTerm));
	$("#search").submit();
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

	var list = '<table class="searchResults"><thead><tr><th>Name</th><th>Event Dates</th><th>Functions</th><th>Mapped Events</th><th>Total Events</th><th><input type="checkbox" name="selectContributorSearchAll" id="selectContributorSearchAll" class="selectSearchAll" title="Tick / Un-Tick all"</th></tr></thead><tbody>';
	
	var i = 0;
	
	for(i; i < data.length; i++) {
		
		if(i % 2 == 1) {
			list += '<tr class="odd">'; 
		} else {
			list += '<tr>'; 
		}
		
		list += '<td><a href="' + data[i].url + '" title="View the record for ' + data[i].firstName + " " + data[i].lastName + ' in AusStage" target="_ausstage">' + data[i].firstName + " " + data[i].lastName + '</a></td>';
		list += '<td>' + data[i].eventDates + '</td><td>';
		
		if(data[i].functions.length > 0) {
			for(var x = 0; x < data[i].functions.length; x++) {
				list += data[i].functions[x] + ', ';
			}
			
			list = list.substr(0, list.length - 2);
		} else {
			list += "&nbsp;";
		}
		
		list += '</td><td>' + data[i].mapEventCount + '</td><td>' + data[i].totalEventCount + '</td>';
		
		if(data[i].mapEventCount > 0) {
			list += '<td><input type="checkbox" name="searchContributor" class="searchContributor" value="' + data[i].id + '" title="Tick to add this contributor to the map"/></td>';
		} else {
			list += '<td>&nbsp;</td>';
		}
		
		list += '</tr>';
	}
	
	list += "</tbody></table>";
	
	// add the button
	list += '<button type="button" id="addContributor" class="addSearchResult">Add to Map</button>';
	
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

}

// define a method of the search class to build the organisation search results
SearchClass.prototype.buildOrganisationResults = function(data) {

	var list = '<table class="searchResults"><thead><tr><th>Organisation Name</th><th>Address</th><th>Mapped Events</th><th>Total Events</th><th>&nbsp;</th></tr></thead><tbody>';
	
	var i = 0;
	
	for(i; i < data.length; i++) {
	
		if(i % 2 == 1) {
			list += '<tr class="odd">'; 
		} else {
			list += '<tr>'; 
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
			list += data[i].state + ', ';
		}
		
		if(data[i].postcode != null) {
			list += data[i].postcode;
		} else {
			list = list.substr(0, list.length - 2);
		}
		
		list += '</td><td>' + data[i].mapEventCount + '</td><td>' + data[i].totalEventCount + '</td>';
		
		if(data[i].mapEventCount > 0) {
			list += '<td><input type="checkbox"/></td>';
		} else {
			list += '<td>&nbsp;</td>';
		}
		
		list += '</tr>';
	}
	
	list += '</tbody></table>';
	
	if(i > 0) {
		$("#organisation_results").append(list);
	}
	
	if(i == 25) {
		$("#organisation_results").append(searchObj.LIMIT_REACHED_MSG);
		$("#organisation_heading").empty().append("Organisations (25+)");
	} else {
		$("#organisation_heading").empty().append("Organisations (" + i + ")");
	}

}

// define a method of the search class to build the venue search results
SearchClass.prototype.buildVenueResults = function (data) {

	var list = '<table class="searchResults"><thead><tr><th>Venue Name</th><th>Address</th><th>Total Events</th><th>&nbsp;</th></tr></thead><tbody>';
	
	var i = 0;
	
	for(i; i < data.length; i++) {
		
		// style odd rows differently
		if(i % 2 == 1) {
			list += '<tr class="odd">'; 
		} else {
			list += '<tr>'; 
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
			list += data[i].state + ', ';
		}
		
		if(data[i].postcode != null) {
			list += data[i].postcode;
		} else {
			list = list.substr(0, list.length - 2);
		}
		
		list += '</td><td>' + data[i].totalEventCount + '</td>';
		
		if(data[i].latitude != null) {
			list += '<td><input type="checkbox"/></td>';
		} else {
			list += '<td>&nbsp;</td>';
		}
		
		list += '</tr>';
	}
	
	list += '</tbody></table>';
	
	if(i > 0) {
		$("#venue_results").append(list);
	}
	
	if(i == 25) {
		$("#venue_results").append(searchObj.LIMIT_REACHED_MSG);
		$("#venue_heading").empty().append("Venues (25+)");
	} else {
		$("#venue_heading").empty().append("Venues (" + i + ")");
	}

}

// define a method of the search class to build the event search results
SearchClass.prototype.buildEventResults = function (data) {

	var list = '<table class="searchResults"><thead><tr><th>Event Name</th><th>Venue</th><th>First Date</th><th>&nbsp;</th></tr></thead><tbody>';
	
	var i = 0;
	
	for(i; i < data.length; i++) {
	
		// style odd rows differently
		if(i % 2 == 1) {
			list += '<tr class="odd">'; 
		} else {
			list += '<tr>'; 
		}
		
		list += '<td><a href="' + data[i].url + '" title="View the record for ' + data[i].name + ' in AusStage" target="_ausstage">' + data[i].name + '</a></td>';
		
		list += '<td>' + data[i].venue.name + ' ';
				
		if(data[i].venue.suburb != null) {
			list += data[i].venue.suburb + ', ';
		}
		
		if(data[i].venue.state != null) {
			list += data[i].venue.state + ', ';
		}
		
		if(data[i].venue.suburb != null || data[i].venue.state != null) {
			list = list.substr(0, list.length - 2);
		}
		
		list += '</td><td>' + data[i].firstDisplayDate + '</td>';
		
		if(data[i].venue.latitude != null) {
			list += '<td><input type="checkbox"/></td>';
		} else {
			list += '<td>&nbsp;</td>';
		}
		
		list += '</tr>';
	}
	
	list += '</tbody></table>';

	if(i > 0) {
		$("#event_results").append(list);
	}
	
	if(i == 25) {
		$("#event_results").append(searchObj.LIMIT_REACHED_MSG);
		$("#event_heading").empty().append("Events (25+)");
	} else {
		$("#event_heading").empty().append("Events (" + i + ")");
	}
	
	// update the search underway flag
	searchObj.searching_underway_flag = false;
}

// define a method of the search class to respond to the click event
// of the add to map button
SearchClass.prototype.addToMapClickEvent = function(event) {

	// reset the list of items
	searchObj.items_to_add = [];

	// determine which button was pressed
	var target = $(event.target);
	if(target.attr('id') == 'addContributor') {
		// adding contributors to a map
		
		$(".searchContributor:checkbox:checked").each(function() {
			searchObj.items_to_add.push($(this).val());
		});
		
		$("#add_search_results_div h2").empty().append('Add ' + searchObj.items_to_add.length + ' contributors to the map');
		
		$("#add_search_results_div").dialog('open');
	}

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
	}
}
