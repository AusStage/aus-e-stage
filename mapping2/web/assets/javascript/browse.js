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
	
		var list = '<ul class="browseList"><li>' + browseObj.buildCheckbox(data[0].name, data[0].id) + ' ' + data[0].name + '<ul class="browseList" style="padding-left: 15px;">';
	
		for(var i = 1; i < 9; i++) {
			list += '<li>' + browseObj.buildCheckbox('majorArea', data[i].id) + ' <span class="clickable browseMajorArea" id="browse_state_' + data[i].id + '">' + data[i].name + '</span></li>';
		}
		
		list += '</ul></li>';
		list += '<li>' + browseObj.buildCheckbox('majorArea', data[9].id) + ' ' + data[9].name + '<ul class="browseList" style="padding-left: 15px;">';
		
		for(var i = 10; i < data.length; i++) {
			list += '<li>' + browseObj.buildCheckbox('majorArea', data[i].id) + ' <span class="clickable browseMajorArea" id="browse_state_' + data[i].id + '">' + data[i].name + '</span></li>';
		}
		
		list += '</ul></li></ul>'
		
		$("#browse_major_area").append(list);
		styleButtons();
	});
}

//define a function to respond to a click on one of the major areas
BrowseClass.prototype.getSuburbsClickEvent = function(event) {

	$("#browse_venue").empty();

	// build the url
	var target = $(event.target);
	var id     = target.attr('id').replace('browse_state_', '');
	var url    = BASE_URL + "lookup?task=suburb-list&id=" + id;
	
	var parentArea = $('#browse_majorArea_' + id);
	var parentId = null;
	
	if(parentArea.is(':checked') == true) {
		var parentId  = parentArea.attr('id').replace('browse_majorArea_', '');
	}
	
	// remove the highlight if necessary
	$('#browse_major_area span').removeClass('ui-state-highlight ui-corner-all browseHighlight');
	
	// highlight this item
	target.addClass('ui-state-highlight ui-corner-all browseHighlight');
	
	// get the new list of suburbs and add them
	$.get(url, function(data, textStatus, XMLHttpRequest) {
		var list = '<ul class="browseList">';
		
		// build a list of suburbs
		for(var i = 0; i < data.length; i++) {
			// check to see if this suburb has mappable venues
			if(data[i].mapVenueCount > 0) {
				// yes
				// should this checkbox be ticked
				if(id == parentId) {
					// yes
					// build a standard checkbox
					list += '<li>' + browseObj.buildCheckbox('suburb', id + '_' + data[i].name, 'checked'); 
				} else {
					// no
					// build a standard checkbox
					list += '<li>' + browseObj.buildCheckbox('suburb', id + '_' + data[i].name);
				}				
				
			} else {
				// no
				// build a disabled checkbox
				list += '<li>' + browseObj.buildCheckbox('suburb', id + '_' + data[i].name, 'disabled');
			}
			
			// add the spans 
			//list += '<span class="clickable browseSuburb" id="browse_state_' + id + '_suburb_' + data[i].name.replace(/[^A-Za-z0-9_]/gi, '-') + '" name="browse_state_' + id + '_suburb_' + data[i].name + '" title="Total Venues: ' + data[i].venueCount + ' / Mapped Venues: ' + data[i].mapVenueCount + '">' + data[i].name + ' ';
			list += '<span class="clickable browseSuburb" id="browse_state_' + id + '_suburb_' + data[i].name.replace(/[^A-Za-z0-9_]/gi, '-') + '" name="browse_state_' + id + '_suburb_' + data[i].name + '">' + data[i].name + ' ';
			list += '(' + data[i].mapVenueCount  + '/' + data[i].venueCount + ')</span></li>';
		}
		
		list += '</ul>';
	
		// replace the list of suburbs
		$("#browse_suburb").empty().append(list);
		
		//$('.browseSuburb').tipsy({gravity: 'n', delayIn: 1000, delayOut: 0});
	});
}

// define a function to get a list of venues
BrowseClass.prototype.getVenuesClickEvent = function(event) {

	// build the url
	var target = $(event.target);
	var tokens = target.attr('id').split('_');
	var id     = tokens[2] + '_' + tokens[4];
	var parentArea = $('#browse_suburb_' + tokens[2] + '_' + tokens[4]);
	var parentId = null;
	
	if(parentArea.is(':checked') == true) {
		var parentId  = parentArea.attr('id').replace('browse_suburb_', '');
	}
	
	var tokensFromName = target.attr('name').split('_');
	var url = BASE_URL + "lookup?task=suburb-venue-list&id=" + encodeURIComponent(tokensFromName[2] + '_' + tokensFromName[4]);
	
	$('#browse_suburb span').removeClass('ui-state-highlight ui-corner-all browseHighlight');
	
	// highlight this item
	target.addClass('ui-state-highlight ui-corner-all browseHighlight');
	
	
	// get the list of venues and add them
	$.get(url, function(data, textStatus, XMLHttpRequest) {
		var list = '<ul class="browseList">';
		
		// build a list of venues
		for(var i = 0; i < data.length; i++) {
			// check to see if this venue is mappable
			if(data[i].mapEventCount > 0) {
				// yes
				// should this check box be ticked
				if(parentId == id) {
					// yes
					// build a ticked checkbox
					list += '<li>' + browseObj.buildCheckbox('venue', tokens[1] + '_' + tokens[3] + '-' + data[i].id, 'checked');
				} else {
					// no
					// build an unticked checbox
					list += '<li>' + browseObj.buildCheckbox('venue', tokens[1] + '_' + tokens[3] + '-' + data[i].id);
				}
			} else {
				list += '<li>' + browseObj.buildCheckbox('venue', tokens[1] + '_' + tokens[3] + '-' + data[i].id, 'disabled');
			}
			
			// add the span
			list += ' <span class="browseVenue">' + data[i].name + ' (' + data[i].eventCount + ')</span></li>';
		}
		
		list += '</ul>';
		
		// replace the list of venues
		$("#browse_venue").empty().append(list);
		
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
	
	if(tokens[1] == 'majorArea' && tokens[2] != '999') {
		
		// fire off the click event for the state
		$('#browse_state_' + tokens[2]).click();

	} else if(tokens[1] == 'suburb') {
	
		// fire off the click event for the suburb
		$('#browse_state_' + tokens[2] + '_suburb_' + tokens[3]).click();
		
	} else if(tokens[1] == 'Australia') {
		if(target.is(':checked') == true) { 
			// tick all of the australian states
			for(var i = 1; i < 9; i++) {
				$('#browse_majorArea_' + i).attr('checked', true);
			}
			
			$('#browse_suburb').empty();
			$('#browse_venue').empty();
			$('#browse_major_area span').removeClass('ui-state-highlight ui-corner-all browseHighlight');
			
		
		} else {
			// untick all of the australian states
			for(var i = 1; i < 9; i++) {
				$('#browse_majorArea_' + i).attr('checked', false);
			}
			
			$('#browse_suburb').empty();
			$('#browse_venue').empty();
			$('#browse_major_area span').removeClass('ui-state-highlight ui-corner-all browseHighlight');
		}
	} else if(tokens[1] == 'majorArea' && tokens[2] == '999') {
		if(target.is(':checked') == true) { 
			// tick all of the countries
			$('input[name^="browse_majorArea_999-"]').attr('checked', true);
			
			$('#browse_suburb').empty();
			$('#browse_venue').empty();
			$('#browse_major_area span').removeClass('ui-state-highlight ui-corner-all browseHighlight');
			
		} else {
			// untick all of the countries
			$('input[name^="browse_majorArea_999-"]').attr('checked', false);
			
			$('#browse_suburb').empty();
			$('#browse_venue').empty();
			$('#browse_major_area span').removeClass('ui-state-highlight ui-corner-all browseHighlight');
		}
	}
}
