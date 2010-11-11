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
		$("#browse_suburb").empty().append('<h3>Suburbs</h3>');
		$("#browse_venue").empty().append('<h3>Venues</h3>');
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
		
		// build a list of suburbs
		for(var i = 0; i < data.length; i++) {
			// check to see if this suburb has mappable venues
			if(data[i].mapVenueCount > 0) {
				// yes
				// check to see if the major area for this suburb is ticked
				if(inArray(browseObj.trackerObj.majorAreas, id) > -1) {
					// yes
					// build a ticked checkbox
					list += '<li>' + browseObj.buildCheckbox('suburb', id + '_' + data[i].name, 'checked') + ' <span class="clickable browseSuburb" id="state_' + id + '_suburb_' + data[i].name + '" title="Total Venues: ' + data[i].venueCount + ' / Mapped Venues: ' + data[i].mapVenueCount + '">' + data[i].name + '</span></li>';
					
					// add this suburb to the array if required
					if(inArray(browseObj.trackerObj.suburbs, id + '_' + data[i].name) == -1) {
						browseObj.trackerObj.suburbs.push(id + '_' + data[i].name);
					}
				} else {
					// no
					// build a standard checkbox
					list += '<li>' + browseObj.buildCheckbox('suburb', id + '_' + data[i].name) + ' <span class="clickable browseSuburb" id="state_' + id + '_suburb_' + data[i].name + '" title="Total Venues: ' + data[i].venueCount + ' / Mapped Venues: ' + data[i].mapVenueCount + '">' + data[i].name + '</span></li>';
				}
			} else {
				// no
				// build a disabled checkbox
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
		
		// build a list of venues
		for(var i = 0; i < data.length; i++) {
			// check to see if this venue is mappable
			if(data[i].mapEventCount > 0) {
				// yes
				// check to see if this venue is in a suburb that is ticked
				if(inArray(browseObj.trackerObj.suburbs, tokens[1] + '_' + tokens[3]) > -1) {
					// yes
					// build a ticked check box
					list += '<li>' + browseObj.buildCheckbox('venue', tokens[1] + '_' + tokens[3] + '-' + data[i].id, 'checked') + ' <span class="browseVenue" id="' + data[i].id + '" title="Total Events: ' + data[i].eventCount + ' / Mapped Events: ' + data[i].mapEventCount + '">' + data[i].name + '</span></li>';
					
					// add this venue to the array if required
					if(inArray(browseObj.trackerObj.venues, data[i].id) == -1) {
						browseObj.trackerObj.venues.push(data[i].id);
					}
				} else {
					// no
					// build a standard checkbox
					list += '<li>' + browseObj.buildCheckbox('venue', tokens[1] + '_' + tokens[3] + '-' + data[i].id) + ' <span class="browseVenue" id="' + data[i].id + '" title="Total Events: ' + data[i].eventCount + ' / Mapped Events: ' + data[i].mapEventCount + '">' + data[i].name + '</span></li>';
				}
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
BrowseClass.prototype.buildCheckbox = function(title, value, param) {

	var n = 'browse_' + title + '_' + value;
	var i = n.replace(/[^A-Za-z0-9_]/gi, '-');

	if(typeof(param) != 'undefined') {
		if(param == 'disabled') {
			return '<input type="checkbox" disabled/>';
		} else if(param == 'checked') {
			return '<input type="checkbox" name="' + n + '" id="' + i + '" value="' + value + '" class="browseCheckBox" checked/>';
		}		
	} else {
		return '<input type="checkbox" name="' + n + '" id="' + i + '" value="' + value + '" class="browseCheckBox"/>';
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
			var idx = inArray(browseObj.trackerObj.majorAreas, tokens[2]);
			browseObj.trackerObj.majorAreas.splice(idx, 1);
			
			// remove items from the other arrays
			browseObj.tidySuburbCheckboxes(browseObj.trackerObj.suburbs, tokens[2]);
			browseObj.tidyVenueCheckboxes(browseObj.trackerObj.venues,  tokens[2]);
		} else {
			// add this item to the list
			browseObj.trackerObj.majorAreas.push(tokens[2]);
			
			// tick any existing checkboxes
			$('input[name*="browse_suburb_' + tokens[2] +'"]').each(function(index) {
				$(this).attr('checked', true);
				
				var elems    = $(this).attr('name').split('_');
				
				// add this suburb to the array if required
				if(inArray(browseObj.trackerObj.suburbs, elems[2] + '_' + elems[3]) == -1) {
					browseObj.trackerObj.suburbs.push(elems[2] + '_' + elems[3]);
				}
			});
		}
	} else if(tokens[1] == 'suburb') {
		if(target.is(':checked') == false) {
			// remove this item from the list
			var idx = inArray(browseObj.trackerObj.suburbs, tokens[2] + '_' + tokens[3]);
			browseObj.trackerObj.suburbs.splice(idx, 1);
			
			// remove items from the other arrays
			browseObj.tidyVenueCheckboxes(browseObj.trackerObj.venues, tokens[2] + '_' + tokens[3]);
		} else {
			// add this item to the list
			browseObj.trackerObj.suburbs.push(tokens[2] + '_' + tokens[3]);
						
			// tick any existing boxes
			$('input[name*="browse_venue_' + tokens[2] + '_' + tokens[3] +'"]').each(function(index) {
				$(this).attr('checked', true);
				
				var elems    = $(this).attr('name').split('_');
				
				// add this suburb to the array if required
				if(inArray(browseObj.trackerObj.venues, elems[2] + '_' + elems[3]) == -1) {
					browseObj.trackerObj.venues.push(elems[2] + '_' + elems[3]);
				}
			});
		}
	} else {
		if(target.is(':checked') == false) {
			// remove this item from the list
			var idx = inArray(browseObj.trackerObj.venues, target.val())
			browseObj.trackerObj.venues.splice(idx, 1);
		} else {
			// add this item to the list
			browseObj.trackerObj.venues.push(target.val());
		}
	}
}

// define a function to remove all elements from the suburbs array
// and untick the checkboxes
BrowseClass.prototype.tidySuburbCheckboxes = function(array, selector) {

	// loop through the array looking for a match
	for(var i = 0; i < array.length; i++) {
	
		if(array[i].substr(0, selector.length) === selector) {
		
			// get the checkbox id
			var id = array[i].replace(/[^A-Za-z0-9_]/gi, '-');
			
			// untick the checkbox
			$('input[name*="browse_suburb_' + id +'"]').each(function(index) {
				$(this).attr('checked', false);
			});
			
			// remove the item from the array
			array.splice(i, 1);
			
			// decrement the counter
			i--;
		}
	}
	
	// catch any others that may be ticked but not in the array
	$('input[name*="browse_suburb_' + selector +'"]').each(function(index) {
		$(this).attr('checked', false);
	});
}

// define a function to remove all elements from the venues array
// and untick the checkboxes
BrowseClass.prototype.tidyVenueCheckboxes = function(array, selector) {

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
	
	// catch any others that may be ticked but not in the array
	$('input[name*="browse_venue_' + selector +'"]').each(function(index) {
		$(this).attr('checked', false);
	});
}
