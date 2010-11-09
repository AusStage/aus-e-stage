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

// define the browse class
function BrowseClass() {

}

// define a function to populate the major areas cell
BrowseClass.prototype.getMajorAreas = function() {

	// build the url
	var url = BASE_URL + "lookup?task=state-list";
	
	$.get(url, function(data, textStatus, XMLHttpRequest) {
	
		var list = '<h3>Major Areas</h3><ul class="browseList"><li>' + browseObj.buildCheckbox(data[0].name, data[0].id) + ' ' + data[0].name + '<ul class="browseList" style="padding-left: 15px;">';
	
		for(var i = 1; i < 9; i++) {
			list += '<li>' + browseObj.buildCheckbox(data[i].name, data[i].id) + ' <span class="clickable browseMajorArea" id="state_' + data[i].id + '">' + data[i].name + '</span></li>';
		}
		
		list += '</ul></li>';
		list += '<li>' + browseObj.buildCheckbox(data[9].name, data[9].id) + ' ' + data[9].name + '</li>'
		list += '</ul>'
		list += '<button type="button" id="browseAddToMap">Add to Map</button>';
		
		$("#browse_major_area").append(list);
		styleButtons();
		$("#browse_major_area").append($("#browse_notes"));
		$("#browse_notes").removeClass("hideMe");
	});
}

// define a function to build a checkbox
BrowseClass.prototype.buildCheckbox = function(title, value) {
	var nameAndId = title + '_' + value;
	return '<input type="checkbox" name="' + nameAndId + '" id="' + nameAndId + '" value="' + value + '"/>';
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
				list += '<li>' + browseObj.buildCheckbox(data[i].name, data[i].id) + ' <span class="clickable browseSuburb" id="state_' + id + '_suburb_' + data[i].name + '">' + data[i].name + '</span></li>';
			} else {
				list += '<li style="padding-left: 25px;">' + data[i].name + '</span></li>';
			}
		}
		
		list += '</ul>';
	
		// replace the list of suburbs
		$("#browse_suburb").empty().append(list);
	});
}

// define a function to get a list of venues
BrowseClass.prototype.getVenuesClickEvent = function(event) {

	// build the url
	var target = $(event.target);
	var tokens = target.attr('id').split('_');
	var url    = BASE_URL + "lookup?task=suburb-venue-list&id=" + tokens[1] + '_' + encodeURIComponent(tokens[3]);
	
	// get the list of venues and add them
	$.get(url, function(data, textStatus, XMLHttpRequest) {
		var list = '<h3>Venues</h3><ul class="browseList">';
		
		for(var i = 0; i < data.length; i++) {
			if(data[i].mapEventCount > 0) {
				list += '<li>' + browseObj.buildCheckbox(data[i].name, data[i].id) + ' <span class="clickable browseVenue" id="' + data[i].id + '">' + data[i].name + '</span></li>';
			} else {
				list += '<li style="padding-left: 25px;">' + data[i].name + '</span></li>';
			}
		}
		
		list += '</ul>';
		
		// replace the list of venues
		$("#browse_venue").empty().append(list);
	});
}
