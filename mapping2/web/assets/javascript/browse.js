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
 
// define the browse tracker class
function BrowseTrackerClass() {
	this.majorAreas = [];
	this.suburbs    = [];
	this.venues     = [];
}

// define the browse class
function BrowseClass() {

	this.trackerObj = new BrowseTrackerClass();
}

// define a function to populate the major areas cell
BrowseClass.prototype.getMajorAreas = function() {

	// build the url
	var url = BASE_URL + "lookup?task=state-list";
	
	$.get(url, function(data, textStatus, XMLHttpRequest) {
	
		var list = '<h3>Major Areas</h3><ul class="browseList"><li>' + browseObj.buildCheckbox(data[0].name, data[0].id) + ' ' + data[0].name + '<ul class="browseList" style="padding-left: 15px;">';
	
		for(var i = 1; i < 9; i++) {
			list += '<li>' + browseObj.buildCheckbox('majorArea', data[i].id) + ' <span class="clickable browseMajorArea" id="state_' + data[i].id + '">' + data[i].name + '</span></li>';
		}
		
		list += '</ul></li>';
		list += '<li>' + browseObj.buildCheckbox('majorArea', data[9].id) + ' ' + data[9].name + '</li>'
		list += '</ul>'
		list += '<button type="button" id="browseAddToMap">Add to Map</button>';
		
		$("#browse_major_area").append(list);
		styleButtons();
		$("#browse_major_area").append($("#browse_notes"));
		$("#browse_notes").removeClass("hideMe");
	});
}

//define a function to respond to a click on one of the major areas
BrowseClass.prototype.getSuburbsClickEvent = function(event) {

	$("#browse_venue").empty();

	// build the url
	var target = $(event.target);
	var id     = target.attr('id').replace('state_', '');
	var url    = BASE_URL + "lookup?task=suburb-list&id=" + id;
	
	// get the new list of suburbs and add them
	$.get(url, function(data, textStatus, XMLHttpRequest) {
		var list = '<h3>Suburbs</h3><ul class="browseList">';
		
		for(var i = 0; i < data.length; i++) {
			if(data[i].mapVenueCount > 0) {
				list += '<li>' + browseObj.buildCheckbox('suburb', id + '_' + data[i].name) + ' <span class="clickable browseSuburb" id="state_' + id + '_suburb_' + data[i].name + '" title="Total Venues: ' + data[i].venueCount + ' / Mapped Venues: ' + data[i].mapVenueCount + '">' + data[i].name + '</span></li>';
			} else {
				list += '<li>' + browseObj.buildCheckbox('suburb', id + '_' + data[i].name, 'disabled') + ' <span class="clickable browseSuburb" id="state_' + id + '_suburb_' + data[i].name + '" title="Total Venues: ' + data[i].venueCount + ' / Mapped Venues: ' + data[i].mapVenueCount + '">' + data[i].name + '</span></li>';
			}
		}
		
		list += '</ul>';
	
		// replace the list of suburbs
		$("#browse_suburb").empty().append(list);
		
		$('#browse_suburb [title]').tipsy({trigger: 'focus', gravity: 'n'});
	});
}

// define a function to get a list of venues
BrowseClass.prototype.getVenuesClickEvent = function(event) {

	// build the url
	var target = $(event.target);
	var tokens = target.attr('id').split('_');
	var url    = BASE_URL + "lookup?task=suburb-venue-list&id=" + tokens[1] + '_' + encodeURIComponent(tokens[3].replace('-', ' '));
	
	// get the list of venues and add them
	$.get(url, function(data, textStatus, XMLHttpRequest) {
		var list = '<h3>Venues</h3><ul class="browseList">';
		
		for(var i = 0; i < data.length; i++) {
			if(data[i].mapEventCount > 0) {
				list += '<li>' + browseObj.buildCheckbox('venue', tokens[1] + '_' + tokens[3] + '-' + data[i].id) + ' <span class="browseVenue" id="' + data[i].id + '" title="Total Events: ' + data[i].eventCount + ' / Mapped Events: ' + data[i].mapEventCount + '">' + data[i].name + '</span></li>';
			} else {
				list += '<li>' + browseObj.buildCheckbox('venue', tokens[1] + '_' + tokens[3] + '-' + data[i].id, 'disabled') + ' <span class="browseVenue" id="' + data[i].id + '" title="Total Events: ' + data[i].eventCount + ' / Mapped Events: ' + data[i].mapEventCount + '">' + data[i].name + '</span></li>';
			}
		}
		
		list += '</ul>';
		
		// replace the list of venues
		$("#browse_venue").empty().append(list);
		
		$('#browse_venue [title]').tipsy({trigger: 'focus', gravity: 'n'});
	});
}

// define a function to build a checkbox
BrowseClass.prototype.buildCheckbox = function(title, value, disabled) {
	var nameAndId = 'browse_' + title + '_' + value;
	nameAndId = nameAndId.replace(/[^A-Za-z0-9_]/gi, '-');
	if(disabled == null) {
		return '<input type="checkbox" name="' + nameAndId + '" id="' + nameAndId + '" value="' + value + '" class="browseCheckBox"/>';
	} else {
		return '<input type="checkbox" name="' + nameAndId + '" id="' + nameAndId + '" value="' + value + '" disabled/>';
	}
}

// define a function to respond to the click on a checkbox
BrowseClass.prototype.checkboxClickEvent = function(event) {

	// determine which checkbox was checked
	var target = $(event.target);
	var tokens = target.attr('id').split('_');
	
	if(tokens[1] == 'majorArea') {
		if(target.is(':checked') == false) {
			// remove this item from the list
			var idx = jQuery.inArray(browseObj.trackerObj.suburbs, tokens[2]);
			browseObj.trackerObj.majorAreas.splice(idx, 1);
			
			// remove items from the other arrays
			browseObj.tidyCheckboxes(browseObj.trackerObj.suburbs, tokens[1]);
			browseObj.tidyCheckboxes(browseObj.trackerObj.venues,  tokens[1]);
		} else {
			// add this item to the list
			browseObj.trackerObj.majorAreas.push(tokens[2]);
		}
	} else if(tokens[1] == 'suburb') {
		if(target.is(':checked') == false) {
			// remove this item from the list
			var idx = jQuery.inArray(browseObj.trackerObj.suburbs, tokens[2] + '_' + tokens[3]);
			browseObj.trackerObj.suburbs.splice(idx, 1);
			
			// remove items from the other arrays
			browseObj.tidyCheckboxes(browseObj.trackerObj.venues, tokens[2] + '_' + tokens[3]);
		} else {
			// add this item to the list
			browseObj.trackerObj.suburbs.push(tokens[2] + '_' + tokens[3]);
		}
	} else {
		if(target.is(':checked') == false) {
			// remove this item from the list
			var idx = jQuery.inArray(browseObj.trackerObj.venues, target.val())
			browseObj.trackerObj.venues.splice(idx, 1);
		} else {
			// add this item to the list
			browseObj.trackerObj.venues.push(target.val());
		}
	}
	
	console.log('browse checkbox event fired');
}

// define a function to remove all elements from an array based on a starts with match
BrowseClass.prototype.tidyCheckboxes = function(array, selector) {

	// loop through the array looking for a match
	for(var i = 0; i < array.length; i++) {
	
		if(array[i].substr(0, selector.length) === selector) {
		
			// get the checkbox id
			var id = array[i].replace(/[^A-Za-z0-9_]/gi, '-');
			
			// untick the checkbox
			$('input[name|="browse_venue_' + id +'"]').each(function(index) {
				$(this).attr('checked', false);
			});
			
			// remove the item from the array
			array.splice(i, 1);
			
			// decrement the counter
			i--;
		}
	}
}
