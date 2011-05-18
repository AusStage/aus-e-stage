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

	// a variable to store marker data as it is retrieved
	this.markerData = [];

}

// define a function to intialise the browse elements
BrowseClass.prototype.init = function() {

	 browseObj.getMajorAreas();
	 
	 // associate a click event with the browse major area items
	 $('.browseMajorArea').live('click', browseObj.getSuburbsClickEvent);
	 $('.browseSuburb').live('click', browseObj.getVenuesClickEvent);
	 $('.browseCheckBox').live('click', browseObj.checkboxClickEvent);
	 $('#browse_add_btn').click(browseObj.addToMap);
	 $('#browse_reset_btn').click(browseObj.resetBrowseList);
	 
	 // set up handler for when an ajax request results in an error for searching
	 $("#browse_messages").ajaxError(function(e, xhr, settings, exception) {
	 	// determine what type of request has been made & update the message text accordingly
		// ensure that we're only working on browse activities
		if(settings.url.indexOf("lookup?task=state-list", 0) != -1) {
			$(this).empty().append(buildErrorMsgBox('the request for Country and State data'));
		} else if(settings.url.indexOf("lookup?task=suburb-list", 0) != -1) {
			$(this).empty().append(buildErrorMsgBox('the request for suburb data'));
		} else if(settings.url.indexOf("lookup?task=suburb-venue-list", 0) != -1) {
			$(this).empty().append(buildErrorMsgBox('the request for venue data'));
		}
	});
	
	// set up a handler for when the ajax calls finish
	$("#browse_messages").bind('mappingBrowseAjaxQueue' + 'AjaxStop', browseObj.addDataToMap);
	
	// set up the browse help dialog
	$("#help_browse_div").dialog({ 
		autoOpen: false,
		height: 400,
		width: 450,
		modal: true,
		buttons: {
			Close: function() {
				$(this).dialog('close');
			}
		},
		open: function() {
			
		},
		close: function() {
			
		}
	});
	
	$('#show_browse_help').click(function() {
		$('#help_browse_div').dialog('open');
	});

}

// define a function to populate the major areas cell
BrowseClass.prototype.getMajorAreas = function() {

	// build the url
	var url = BASE_URL + "lookup?task=state-list";
	
	$.get(url, function(data, textStatus, XMLHttpRequest) {
	
		var list = '<ul class="browseList"><li>' + browseObj.buildCheckbox(data[0].name, data[0].id, null, 'browseMajorAreaTickBox') + ' ' + data[0].name + '<ul class="browseList" style="padding-left: 15px;">';
	
		for(var i = 1; i < 9; i++) {
			list += '<li>' + browseObj.buildCheckbox('majorArea', data[i].id, null, 'browseMajorAreaTickBox') + ' <span class="clickable browseMajorArea" id="browse_state_' + data[i].id + '">' + data[i].name + '</span></li>';
		}
		
		list += '</ul></li>';
		list += '<li>' + browseObj.buildCheckbox('majorArea', data[9].id, null, 'browseMajorAreaTickBox') + ' ' + data[9].name + '<ul class="browseList" style="padding-left: 15px;">';
		
		for(var i = 10; i < data.length; i++) {
			list += '<li>' + browseObj.buildCheckbox('majorArea', data[i].id, null, 'browseMajorAreaTickBox') + ' <span class="clickable browseMajorArea" id="browse_state_' + data[i].id + '">' + data[i].name + '</span></li>';
		}
		
		list += '</ul></li></ul>'
		
		$("#browse_major_area").empty().append(list);
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
					list += '<li>' + browseObj.buildCheckbox('suburb', id + '_' + data[i].name, 'checked', 'browseSuburbTickBox'); 
				} else {
					// no
					// build a standard checkbox
					list += '<li>' + browseObj.buildCheckbox('suburb', id + '_' + data[i].name, null, 'browseSuburbTickBox');
				}				
				
			} else {
				// no
				// build a disabled checkbox
				list += '<li>' + browseObj.buildCheckbox('suburb', id + '_' + data[i].name, 'disabled', null);
			}
			
			// add the spans 
			//list += '<span class="clickable browseSuburb" id="browse_state_' + id + '_suburb_' + data[i].name.replace(/[^A-Za-z0-9_]/gi, '-') + '" name="browse_state_' + id + '_suburb_' + data[i].name + '" title="Total Venues: ' + data[i].venueCount + ' / Mapped Venues: ' + data[i].mapVenueCount + '">' + data[i].name + ' ';
			list += ' <span class="clickable browseSuburb" id="browse_state_' + id + '_suburb_' + data[i].name.replace(/[^A-Za-z0-9_]/gi, '-') + '" name="browse_state_' + id + '_suburb_' + data[i].name + '">' + data[i].name + ' ';
			list += '(' + data[i].mapVenueCount  + '/' + data[i].venueCount + ')</span></li>';
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
					list += '<li>' + browseObj.buildCheckbox('venue', tokens[1] + '_' + tokens[2] + '_' + tokens[4] +'-' + data[i].id, 'checked', 'browseVenueTickBox');
				} else {
					// no
					// build an unticked checbox
					list += '<li>' + browseObj.buildCheckbox('venue', tokens[1] + '_' + tokens[2] + '_' + tokens[4] +'-' + data[i].id, null, 'browseVenueTickBox');
				}
			} else {
				list += '<li>' + browseObj.buildCheckbox('venue', tokens[1] + '_' + tokens[2] + '_' + tokens[4] +'-' + data[i].id, 'disabled', null);
			}
			
			// add the span
			list += ' <span class="browseVenue"><a href="' + data[i].url + '" title="View the record for ' + data[i].name + ' in AusStage" target="_ausstage">' + data[i].name + ' (' + data[i].eventCount + ')</a></span></li>';
		}
		
		list += '</ul>';
		
		// replace the list of venues
		$("#browse_venue").empty().append(list);
		
	});
}

// define a function to build a checkbox
BrowseClass.prototype.buildCheckbox = function(title, value, param, selector) {

	var n = 'browse_' + title + '_' + value;
	var i = n.replace(/[^A-Za-z0-9_]/gi, '-');

	if(typeof(param) != 'undefined' && param != null) {
		if(param == 'disabled') {
			return '<input type="checkbox" disabled/>';
		} else if(param == 'checked') {
			return '<input type="checkbox" name="' + n + '" id="' + i + '" value="' + value + '" class="browseCheckBox ' + selector + '" checked/>';
		}		
	} else {
		return '<input type="checkbox" name="' + n + '" id="' + i + '" value="' + value + '" class="browseCheckBox ' + selector + '""/>';
	}
}

// define a function to respond to the click on a checkbox
BrowseClass.prototype.checkboxClickEvent = function(event) {

	// get rid of any error / status messages
	$('#browse_messages').empty()

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

// define a function to add venues to the map
BrowseClass.prototype.addToMap = function() {

	// declare helper variables
	var venues       = {majorAreas: [], suburbs: [], venues: []};
	var allStates    = true;
	var allCountries = true;
	var allSuburbs   = true;
	var allVenues    = true;
	
	// determine what majorAreas have been ticked
	$('.browseMajorAreaTickBox').each(function() {
	
		// convert this to a JQuery object
		target = $(this);
		
		//determine if the box is ticked or not
		if(target.is(':checked') == true) {
			venues.majorAreas.push(target.val());
		} else {
			if(target.val() > 0 && target.val() < 10) {
				allStates = false;
			} else if(target.val().indexOf('999-') == 0) {
				allCountries = false;
			}
		}
	});
	
	// determine what suburbs have been ticked
	$('.browseSuburbTickBox').each(function() {
	
		// convert this to a JQuery object
		target = $(this);
		
		// determine if this box is ticked or not
		if(target.is(':checked') == true) {
			venues.suburbs.push(target.val());
		} else {
			allSuburbs = false;
		}
	});
	
	// determine what venues have been ticked
	$('.browseVenueTickBox').each(function() {
		
		// convert this to a JQuery object
		target = $(this);
		
		// determine if this box is ticked or not
		if(target.is(':checked') == true) {
			venues.venues.push(target.val());
		} else {
			allVenues = false;
		}
	});
	
	// adjust the venues and suburbs arrays
	if(allVenues == true && venues.venues.length > 0) {
	
		// make sure the suburb is in the list
		idx = venues.venues[0];
		idx = idx.split("_");
		idx = idx[1] + '_' + idx[2];
		idx = idx.split("-");
		idx = idx[0];
		
		tmp = $.inArray(idx, venues.suburbs);
		if(tmp == -1) {
			venues.suburbs.push(idx);
		}
		
		// reset all of the venues
		venues.venues = [];
		
		// tick this suburb
		idx = 'browse_suburb_' + idx
		idx = '#' + idx.replace(/[^A-Za-z0-9_]/gi, '-')
		$(idx).attr('checked', true);
	
	} else {
	
		if(venues.venues.length > 0) {
			// take out the state identifier from the list
			idx = venues.venues[0];
			idx = idx.split("_");
			idx = idx[1] + '_' + idx[2];
			idx = idx.split("-");
			idx = idx[0];
		
			idx = $.inArray(idx, venues.suburbs);
			if(idx != -1) {
				venues.suburbs.splice(idx, 1);
			}
		
			// untick this suburb
			idx = 'browse_suburb_' + idx
			idx = '#' + idx.replace(/[^A-Za-z0-9_]/gi, '-')
			$(idx).attr('checked', false);
		}
	}


	// adjust the suburbs and majorAreas arrays as required
	if(allSuburbs == true && venues.suburbs.length > 0) {
		// get the state code
		idx = venues.suburbs[0];
		idx = idx.split("_")[0];
		
		// check to see if the state code is in the list
		if($.inArray(idx, venues.majorAreas) == -1) {
			venues.majorAreas.push(idx);
		}
		
		// tick this major area
		idx = venues.suburbs[0];
		idx = idx.split("_");
		
		$('#browse_majorArea_' + idx).attr('checked', true);
		
		// take out all of the suburbs
		venues.suburbs = [];		
			
	} else {
	
		if(venues.suburbs.length > 0) {
			// take out the majorArea entry
			idx = venues.suburbs[0];
			idx = idx.split("_")[0];
		
			idx = $.inArray(idx, venues.majorAreas);
			if(idx != -1) {
				venues.majorAreas.splice(idx, 1);
			}
		
			// tick this major area
			idx = venues.suburbs[0];
			idx = idx.split("_");
		
			$('#browse_majorArea_' + idx).attr('checked', false);
		}
	}
	
	// adjust the majorAreas list to take into account the Australia checkbox
	if(allStates == true) {	
		for(var i = 1; i < 9; i++) {
			idx = $.inArray(''+ i, venues.majorAreas);
			if(idx != -1) {
				venues.majorAreas.splice(idx, 1);
			}
		}
				
	} else {
		idx = $.inArray("99", venues.majorAreas);
		if(idx != -1) {
			venues.majorAreas.splice(idx, 1);
		}
	}
	
	// adjust the majorAreas list to take into account the International checkbox	
	if(allCountries == true) {
		// take out any of the country check boxes
		tmp = []
		for(var i = 0; i < venues.majorAreas.length; i++) {
			if(venues.majorAreas[i].indexOf('999-') == -1) {
				tmp.push(venues.majorAreas[i]);
			}
		}
		
		venues.majorAreas = tmp;
	} else {
		// take out the international checkbox
		idx = $.inArray("999", venues.majorAreas);
		if(idx != -1) {
			venues.majorAreas.splice(idx, 1);
		}
	}
	
	// adjust the list of venues
	for(var i = 0; i < venues.venues.length; i++) {
		venues.venues[i] = venues.venues[i].split('-')[1];
	}
	
	// reset the marker data variable
	browseObj.markerData = [];
	
	if(venues.majorAreas.length > 0 || venues.suburbs.length > 0 || venues.venues.length > 0) {
		// add these venues to the map
		// inform the user
		$('#browse_messages').empty().append(buildInfoMsgBox('Adding items to the map...'));
		
		var ajaxQueue = $.manageAjax.create("mappingBrowseAjaxQueue", {
			queue: true
		});
				
		// get the data for any major areas
		for(var i = 0; i < venues.majorAreas.length; i++) {
		
			// build the url
			var url  = BASE_URL + 'markers?&type=state&id=' + venues.majorAreas[i];
			
			ajaxQueue.add({
				success: browseObj.processAjaxData,
				url: url
			});
		}
		
		// get the data for any suburbs areas
		for(var i = 0; i < venues.suburbs.length; i++) {
		
			// build the url
			var url  = BASE_URL + 'markers?&type=suburb&id=' + venues.suburbs[i];
			
			ajaxQueue.add({
				success: browseObj.processAjaxData,
				url: url
			});
		}
		
		for(var i = 0; i < venues.venues.length; i++) {
		
			// build the url
			var url  = BASE_URL + 'markers?&type=venue&id=' + venues.venues[i];
			
			ajaxQueue.add({
				success: browseObj.processAjaxData,
				url: url
			});
		}
		
	} else {
		// inform the user
		$('#browse_messages').empty().append(buildInfoMsgBox('No items selected, nothing added to the map'));
	}
}

// function to process the results of the ajax marker data lookups
BrowseClass.prototype.processAjaxData = function(data) {
	browseObj.markerData = browseObj.markerData.concat(data);
}

// function to add the data to the map
BrowseClass.prototype.addDataToMap = function() {
	mappingObj.addVenueBrowseData(browseObj.markerData);
	$('#browse_messages').empty();
}

// function to reset the browse list
BrowseClass.prototype.resetBrowseList = function() {

	// get the major areas again
	browseObj.getMajorAreas();
	
	// empty the other lists
	$("#browse_suburb").empty();
	$("#browse_venue").empty();
}
