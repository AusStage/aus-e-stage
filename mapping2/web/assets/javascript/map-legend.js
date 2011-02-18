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
 
// define the map legend class
function MapLegendClass() {

	this.HEIGHT_BUFFER_CONSTANT = 105;
	this.DEFAULT_MARKER_ZOOM_LEVEL = 17;
	
	this.recordData = null;

}

// function to hide the legend
MapLegendClass.prototype.hideLegend = function() {
	$('.mapLegendContainer').hide();
}

// initialise the map legend
MapLegendClass.prototype.init = function() {
	mapLegendObj.resizeMapLegend();
	mapLegendObj.hideLegend();
	
	// update and show the legend when the map is shown
	$('#tabs').bind('tabsshow', function(event, ui) {
		if (ui.panel.id != "tabs-3") { // tabs-3 == the map tab
			// update the map legend
			mapLegendObj.hideLegend();
		}
	});
	
	// add a live event for the pan and zoom controls
	$('.mapPanAndZoom').live('click', mapLegendObj.panAndZoomMap);
	$('.mapLegendIconImgClick').live('click', mapLegendObj.panAndZoomMapToMarker);
	
	// add a live event for the delete of objects
	$('.mapLegendDeleteIcon').live('click', mapLegendObj.deleteMarker);
	
	// add a live event for the hiding of markers
	$('.mapLegendShowHideMarker').live('click', mapLegendObj.showHideMarker);
}

// function to show the legend
MapLegendClass.prototype.showLegend = function() {
	mapLegendObj.updateLegend();
	$('.mapLegendContainer').show();
}

// function to update the legend
MapLegendClass.prototype.updateLegend = function() {

	// add the pan and zoom section
	mapLegendObj.buildPanAndZoom();

	// declare helper variables
	var tableData = "";
	var objects   = null;
	var obj       = null;
	var idx       = null;
	var tmp       = null;
	
	// get the data used to build the legend
	var recordData = mapLegendObj.buildRecordData();
	
	// build the map legend	
	if(recordData.contributors.objects.length > 0) {
		// add the contributors
		objects = recordData.contributors.objects;
		
		// reset the tableData variable
		tableData = '<table id="mapLegendContributors" class="mapLegendTable">';
		
		// loop through the list of objects
		for(var i = 0; i < objects.length; i++) {
		
			// colour the odd rows
			if(i % 2 == 1) {
				tableData += '<tr class="odd">'; 
			} else {
				tableData += '<tr>'; 
			}
			
			obj = objects[i];
			idx = $.inArray(obj.id, mappingObj.contributorColours.ids);
			
			// add the icon
			tableData += '<td class="mapLegendIcon"><span class="' + mappingObj.contributorColours.colours[idx] + ' mapLegendIconImg"><img src="' + mapIconography.contributor + '" width="' + mapIconography.iconWidth + '" height="' + mapIconography.iconHeight + '"/></span></td>';
			
			// build a tmp variable containing the name
			tmp = obj.firstName + ' ' + obj.lastName;
			tmp = tmp.replace(/\s/g, '&nbsp;');
			
			// add the name and functions
			tableData += '<td class="mapLegendInfo"><a href="' + obj.url + '" target="_ausstage">' + tmp + '</a><br/>';
			
			// add the functions
			if(obj.functions.length != 0) {
			
				for(var y = 0; y < obj.functions.length; y++ ){
					tableData += obj.functions[y] + ', ';
				}
		
				tableData = tableData.substr(0, tableData.length -2);
			}
			
			// add the show/hide check box
			tableData += mapLegendObj.buildShowHide($.inArray(obj.id, mappingObj.hiddenMarkers.contributors), 'contributor', obj.id);
			
			// add the delete icon
			tableData += mapLegendObj.buildDelete('contributor', obj.id);
			
			// finsih the row
			tableData += '</tr>';		
		}
		
		// finish the table and add it to the page
		tableData += '</table>';
		$('#mapLegendContributors').empty().append(tableData);
		
		if(objects.length > 0) {
			$('#mapLegendContributorHeading').empty().append('Contributors (' + objects.length +')');
		}
	} else {
		$('#mapLegendContributors').empty();
		$('#mapLegendContributorHeading').empty().append('Contributors (0)');
	}
	
	if(recordData.organisations.objects.length > 0) {
		// add the organisations
		objects = recordData.organisations.objects;
		
		// reset the tableData variable
		tableData = '<table id="mapLegendOrganisations" class="mapLegendTable">';
		
		// loop through the list of objects
		for(var i = 0; i < objects.length; i++) {
		
			// colour the odd rows
			if(i % 2 == 1) {
				tableData += '<tr class="odd">'; 
			} else {
				tableData += '<tr>'; 
			}
			
			obj = objects[i];
			idx = $.inArray(obj.id, mappingObj.organisationColours.ids);
			
			// add the icon
			tableData += '<td class="mapLegendIcon"><span class="' + mappingObj.organisationColours.colours[idx] + ' mapLegendIconImg"><img src="' + mapIconography.organisation + '" width="' + mapIconography.iconWidth + '" height="' + mapIconography.iconHeight + '"/></span></td>';
			
			// build a tmp variable containing the name
			//tmp = obj.name;
			//tmp = tmp.replace(/\s/g, '&nbsp;');
			
			// add the name and functions
			tableData += '<td class="mapLegendInfo"><a href="' + obj.url + '" target="_ausstage">' + obj.name + '</a><br/>';
			
			// add the address
			tableData += mappingObj.buildAddressAlt(obj.suburb, obj.state, obj.country);
			
			// add the show/hide check box
			tableData += mapLegendObj.buildShowHide($.inArray(obj.id, mappingObj.hiddenMarkers.organisations), 'organisation', obj.id);
			
			// add the delete icon
			tableData += mapLegendObj.buildDelete('organisation', obj.id);
			
			// finsih the row
			tableData += '</tr>';		
		}
		
		// finish the table and add it to the page
		tableData += '</table>';
		$('#mapLegendOrganisations').empty().append(tableData);
		
		if(objects.length > 0) {
			$('#mapLegendOrganisationHeading').empty().append('Organisations (' + objects.length +')');
		}
		
	} else {
		$('#mapLegendOrganisations').empty();
		$('#mapLegendOrganisationHeading').empty().append('Organisations (0)');
	}
	
	if(recordData.venues.objects.length > 0) {
		// add the venues
		objects = recordData.venues.objects;
		
		// reset the tableData variable
		tableData = '<table id="mapLegendVenues" class="mapLegendTable">';
		
		// loop through the list of objects
		for(var i = 0; i < objects.length; i++) {
		
			// colour the odd rows
			if(i % 2 == 1) {
				tableData += '<tr class="odd">'; 
			} else {
				tableData += '<tr>'; 
			}
			
			obj = objects[i];
			
			// add the icon
			tableData += '<td class="mapLegendIcon"><span class="' + mapIconography.venueColours[0] + ' mapLegendIconImg"><img class="mapLegendIconImgClick" id="mpz-venue-' + obj.id + '" src="' + mapIconography.venue + '" width="' + mapIconography.iconWidth + '" height="' + mapIconography.iconHeight + '"/></span></td>';
						
			// add the name and functions
			tableData += '<td class="mapLegendInfo"><a href="' + obj.url + '" target="_ausstage">' + obj.name + '</a><br/>';
			
			// add the address
			tableData += mappingObj.buildAddress(obj.street, obj.suburb, obj.state, obj.country);
			
			// add the show/hide check box
			tableData += mapLegendObj.buildShowHide($.inArray(obj.id, mappingObj.hiddenMarkers.venues), 'venue', obj.id);
			
			// add the delete icon
			tableData += mapLegendObj.buildDelete('venue', obj.id);
			
			// finsih the row
			tableData += '</tr>';		
		}
		
		// finish the table and add it to the page
		tableData += '</table>';
		$('#mapLegendVenues').empty().append(tableData);
		
		if(objects.length > 0) {
			$('#mapLegendVenueHeading').empty().append('Venues (' + objects.length +')');
		}
	} else {
		$('#mapLegendVenues').empty();
		$('#mapLegendVenueHeading').empty().append('Venues (0)');
	}
		
	if(recordData.events.objects.length > 0) {
		// add the events
		objects = recordData.events.objects;
		
		// reset the tableData variable
		tableData = '<table id="mapLegendEvents" class="mapLegendTable">';
		
		// loop through the list of objects
		for(var i = 0; i < objects.length; i++) {
		
			// colour the odd rows
			if(i % 2 == 1) {
				tableData += '<tr class="odd">'; 
			} else {
				tableData += '<tr>'; 
			}
			
			obj = objects[i];
			
			// add the icon
			tableData += '<td class="mapLegendIcon"><span class="' + mapIconography.eventColours[0] + ' mapLegendIconImg"><img class="mapLegendIconImgClick" id="mpz-event-' + obj.id + '" src="' + mapIconography.event + '" width="' + mapIconography.iconWidth + '" height="' + mapIconography.iconHeight + '"/></span></td>';
						
			// add the name and functions
			tableData += '<td class="mapLegendInfo"><a href="' + obj.url + '" target="_ausstage">' + obj.name + '</a><br/>';
			tableData += obj.venue.name + ', ' + mappingObj.buildAddressAlt(obj.venue.suburb, obj.venue.state, obj.venue.country);
			
			// add the show/hide check box
			tableData += mapLegendObj.buildShowHide($.inArray(obj.id, mappingObj.hiddenMarkers.events), 'event', obj.id);
			
			// add the delete icon
			tableData += mapLegendObj.buildDelete('event', obj.id);
			
			// finsih the row
			tableData += '</tr>';		
		}
		
		// finish the table and add it to the page
		tableData += '</table>';
		$('#mapLegendEvents').empty().append(tableData);
		
		if(objects.length > 0) {
			$('#mapLegendEventsHeading').empty().append('Events (' + objects.length +')');
		}
	} else {
		$('#mapLegendEvents').empty();
		$('#mapLegendEventsHeading').empty().append('Events (0)');
	}
}

// a function to build the hide checkbox 
MapLegendClass.prototype.buildShowHide = function(idx, type, id) {

	if(idx == -1) {
		return '</td><td class="mapLegendShowHide"><input type="checkbox" name="mapLegendShowHide" class="mapLegendShowHideMarker use-tipsy" checked="checked" value="mlh-' + type + '-' + id + '" title="Untick to hide this ' + type + '"/></td>';
	} else {
		return '</td><td class="mapLegendShowHide"><input type="checkbox" name="mapLegendShowHide" class="mapLegendShowHideMarker use-tipsy" value="mlh-' + type + '-' + id + '" title="Tick to show this ' + type + '"/></td>';
	}
}

// a function to build the delete icon
MapLegendClass.prototype.buildDelete = function(type, id) {

	// add the delete icon
	return '<td class="mapLegendDelete"><span id="mld-' + type + '-' + id + '" class="mapLegendDeleteIcon ui-icon ui-icon-closethick clickable use-tipsy" style="display: inline-block;" title="Delete this ' + type + ' from the map"></span></td>';

}

// a function to build the internal legend specific datastructure
MapLegendClass.prototype.buildRecordData = function() {

	// get the map data
	var mapData = mappingObj.markerData.objects;
	var contributors;
	var organisations;
	var venues;
	var events;
	var idx;
	
	// build our own legend specific dataset
	var recordData = {contributors:  {ids: [], objects: []},
					  organisations: {ids: [], objects: []},
					  venues:        {ids: [], objects: []},
					  events:        {ids: [], objects: []}
					 };
					 
	// loop through the marker data
	for (var i = 0; i < mapData.length; i++) {
	
		// get the list of contributors
		contributors = mapData[i].contributors;
		
		// process these contributors
		if(contributors.length > 0) {
		
			for(var x = 0; x < contributors.length; x++) {
				idx = $.inArray(contributors[x].id, recordData.contributors.ids);
				if(idx == -1) {
					recordData.contributors.ids.push(contributors[x].id);
					recordData.contributors.objects.push(contributors[x]);
				}
			}		
		}
		
		// get the list of organisations
		organisations = mapData[i].organisations;
		
		// process these contributors
		if(organisations.length > 0) {
		
			for(var x = 0; x < organisations.length; x++) {
				idx = $.inArray(organisations[x].id, recordData.organisations.ids);
				if(idx == -1) {
					recordData.organisations.ids.push(organisations[x].id);
					recordData.organisations.objects.push(organisations[x]);
				}
			}		
		}
		
		// get the list of venues
		venues = mapData[i].venues;
		
		// process these contributors
		if(venues.length > 0) {
		
			for(var x = 0; x < venues.length; x++) {
				idx = $.inArray(venues[x].id, recordData.venues.ids);
				if(idx == -1) {
					recordData.venues.ids.push(venues[x].id);
					recordData.venues.objects.push(venues[x]);
				}
			}		
		}
		
		// get the list of venues
		events = mapData[i].events;
		
		// process these contributors
		if(events.length > 0) {
		
			for(var x = 0; x < events.length; x++) {
				idx = $.inArray(events[x].id, recordData.events.ids);
				if(idx == -1) {
					recordData.events.ids.push(events[x].id);
					recordData.events.objects.push(events[x]);
				}
			}		
		}	
	}
	
	// sort the arrays
	recordData.contributors.objects.sort(sortContributorArrayAlt);
	recordData.organisations.objects.sort(sortOrganisationArrayAlt);
	recordData.venues.objects.sort(sortVenueArray);
	recordData.events.objects.sort(sortEventArray);
	
	// update the id indexes
	if(recordData.contributors.objects.length > 0) {
		recordData.contributors.ids = [];
		for(var i = 0; i < recordData.contributors.objects.length; i++) {
			recordData.contributors.ids.push(recordData.contributors.objects[i].id);
		}
	}
	
	if(recordData.organisations.objects.length > 0) {
		recordData.organisations.ids = [];
		for(var i = 0; i < recordData.organisations.objects.length; i++) {
			recordData.organisations.ids.push(recordData.organisations.objects[i].id);
		}
	}
	
	if(recordData.venues.objects.length > 0) {
		recordData.venues.ids = [];
		for(var i = 0; i < recordData.venues.objects.length; i++) {
			recordData.venues.ids.push(recordData.venues.objects[i].id);
		}
	}
	
	if(recordData.events.objects.length > 0) {
		recordData.events.ids = [];
		for(var i = 0; i < recordData.events.objects.length; i++) {
			recordData.events.ids.push(recordData.events.objects[i].id);
		}
	}
	
	mapLegendObj.recordData = recordData;
	
	return recordData;
}

// compute the height of the map
MapLegendClass.prototype.computeMapLegendHeight = function() {

	// start the height calculations
	//var height = mappingObj.HEIGHT_CONSTANT;
	var height = 0;
	
	// get the height of various elements
	var wrapper   = $('.wrapper').height();
	var header    = $('.header').height();
	var mainMenu  = $('.mainMenu').height();
	var pushElem  = $('.push').height();
	var footer    = $('.footer').height();
	
	height = wrapper - (header + mainMenu + pushElem + footer + mapLegendObj.HEIGHT_BUFFER_CONSTANT);
	
	return Math.floor(height);
}

// a function to resize the map
MapLegendClass.prototype.resizeMapLegend = function() {
	
	var mapLegendDiv = $('.mapLegendContainer');
	mapLegendDiv.height(mapLegendObj.computeMapLegendHeight());
}

// a function to build the pan and zoom controls
MapLegendClass.prototype.buildPanAndZoom = function() {

	// declare helper variables
	var tableData = '<table class="mapLegendTable">';
	
	// add the contries row
	tableData += '<tr><th scope="row">Country</th>';
	tableData += '<td><span class="clickable mapPanAndZoom" id="mpz-australia">Australia</span> | <span class="clickable mapPanAndZoom" id="mpz-international">International</span></td></tr>';
	
	// add the state row
	tableData += '<tr class="odd"><th scope="row">State</th>';
	tableData += '<td><span class="clickable mapPanAndZoom" id="mpz-act">ACT</span> | ';
	tableData += '<span class="clickable mapPanAndZoom" id="mpz-nsw">NSW</span> | ';
	tableData += '<span class="clickable mapPanAndZoom" id="mpz-nt">NT</span> | ';
	tableData += '<span class="clickable mapPanAndZoom" id="mpz-qld">QLD</span> | ';
	tableData += '<span class="clickable mapPanAndZoom" id="mpz-sa">SA</span> | ';
	tableData += '<span class="clickable mapPanAndZoom" id="mpz-tas">TAS</span> | ';
	tableData += '<span class="clickable mapPanAndZoom" id="mpz-vic">VIC</span> | ';
	tableData += '<span class="clickable mapPanAndZoom" id="mpz-wa">WA</span></td></tr>';
	
	// add the cities row
	tableData += '<tr><th scope="row">City</th>';
	tableData += '<td><span class="clickable mapPanAndZoom" id="mpz-adelaide">Adelaide</span> | ';
	tableData += '<span class="clickable mapPanAndZoom" id="mpz-brisbane">Brisbane</span> | ';
	tableData += '<span class="clickable mapPanAndZoom" id="mpz-canberra">Canberra</span> | ';
	tableData += '<span class="clickable mapPanAndZoom" id="mpz-darwin">Darwin</span> | ';
	tableData += '<span class="clickable mapPanAndZoom" id="mpz-hobart">Hobart</span> | ';
	tableData += '<span class="clickable mapPanAndZoom" id="mpz-melbourne">Melbourne</span> | ';
	tableData += '<span class="clickable mapPanAndZoom" id="mpz-perth">Perth</span> | ';
	tableData += '<span class="clickable mapPanAndZoom" id="mpz-sydney">Sydney</span></td></tr>';
	
	// finalise the table
	tableData += '</table>';
	
	$('#mapLegendPanAndZoom').empty().append(tableData);
}

// a function to pan and zoom the map
MapLegendClass.prototype.panAndZoomMap = function() {

	var map = mappingObj.map;
	var id = this.id.split('-');
	var location = mappingObj.commonLocales[id[1]];
	
	map.setZoom(location.zoom);
	map.panTo(new google.maps.LatLng(location.lat, location.lng));
}

// a function to pan and zoom the map to a venue
MapLegendClass.prototype.panAndZoomMapToMarker = function() {

	var map = mappingObj.map;
	var id = this.id.split('-');
	var obj = null;
	var idx = null;
	
	// find the venue object of applicable
	if(id[1] == 'venue') {
		idx = $.inArray(id[2], mapLegendObj.recordData.venues.ids);
		obj = mapLegendObj.recordData.venues.objects[idx];
		
		map.setZoom(mapLegendObj.DEFAULT_MARKER_ZOOM_LEVEL);
		map.panTo(new google.maps.LatLng(obj.latitude, obj.longitude));
	
		// bind to the idle event to determine when to show the infoWindow
		google.maps.event.addListenerOnce(mappingObj.map, 'idle', function() {
			$('#mapIcon-venue-' + mappingObj.computeLatLngHash(obj.latitude, obj.longitude)).click();	
		});
	} else if(id[1] == 'event') {
		idx = $.inArray(id[2], mapLegendObj.recordData.events.ids);
		obj = mapLegendObj.recordData.events.objects[idx];
		
		map.setZoom(mapLegendObj.DEFAULT_MARKER_ZOOM_LEVEL);
		map.panTo(new google.maps.LatLng(obj.venue.latitude, obj.venue.longitude));
	
		// bind to the idle event to determine when to show the infoWindow
		google.maps.event.addListenerOnce(mappingObj.map, 'idle', function() {
			$('#mapIcon-event-' + mappingObj.computeLatLngHash(obj.venue.latitude, obj.venue.longitude)).click();	
		});
	}
}

// function to check to ensure the user wants to delete the marker
MapLegendClass.prototype.deleteMarker = function() {

	var param = this.id;
	var id    = param.split('-');
	var mapData = mappingObj.markerData.objects;
	var obj;
	
	// work out what is being deleted
	if(id[1] == 'contributor') {
		// contributors
		id = id[2];
		// loop through the marker data looking for this contributor
		for (var i = 0; i < mapData.length; i++) {
			
			obj = mapLegendObj.findObjectById(mapData[i].contributors, id);
			
			if(obj != null) {
				i = mapData.length + 1;
			}
	
		}
	} else if(id[1] == 'organisation') {
		// organisation
		id = id[2];
		// loop through the marker data looking for this organisation
		for (var i = 0; i < mapData.length; i++) {
			obj = mapLegendObj.findObjectById(mapData[i].organisations, id);
			
			if(obj != null) {
				i = mapData.length + 1;
			}
		}
	} else if(id[1] == 'venue') {
		// venue
		id = id[2];
		// loop through the marker data looking for this venue
		for (var i = 0; i < mapData.length; i++) {
			obj = mapLegendObj.findObjectById(mapData[i].venues, id);
			
			if(obj != null) {
				i = mapData.length + 1;
			}
		}
	} else {
		// event
		id = id[2];
		// loop through the marker data looking for this event
		for (var i = 0; i < mapData.length; i++) {
			objs = mapData[i].events;
			
			obj = mapLegendObj.findObjectById(mapData[i].venues, id);
			
			if(obj != null) {
				i = mapData.length + 1;
			}	
		}
	}
	
	// prepare the prompt
	id = param.split('-');
	var idx;
	var tmp;
	var prompt = '<table><tr>';
	
	if(id[1] == 'contributor') {
	
		idx = $.inArray(obj.id, mappingObj.contributorColours.ids);
			
		// add the icon
		prompt += '<td class="mapLegendIcon"><span class="' + mappingObj.contributorColours.colours[idx] + ' mapLegendIconImg"><img src="' + mapIconography.contributor + '" width="' + mapIconography.iconWidth + '" height="' + mapIconography.iconHeight + '"/></span></td>';
		
		// build a tmp variable containing the name
		tmp = obj.firstName + ' ' + obj.lastName;
		tmp = tmp.replace(/\s/g, '&nbsp;');
		
		// add the name and functions
		prompt += '<td><a href="' + obj.url + '" target="_ausstage">' + tmp + '</a><br/>';
		
		// add the functions
		if(obj.functions.length != 0) {
		
			for(var y = 0; y < obj.functions.length; y++ ){
				prompt += obj.functions[y] + ', ';
			}
	
			prompt = prompt.substr(0, prompt.length -2);
		}
		
		// finalise the prompt
		prompt += '</td></tr></table>';

	} else if(id[1] == 'organisation') {
	
		idx = $.inArray(obj.id, mappingObj.organisationColours.ids);
			
		// add the icon
		prompt += '<td><span class="' + mappingObj.organisationColours.colours[idx] + ' mapLegendIconImg"><img src="' + mapIconography.organisation + '" width="' + mapIconography.iconWidth + '" height="' + mapIconography.iconHeight + '"/></span></td>';
		
		// add the name and functions
		prompt += '<td><a href="' + obj.url + '" target="_ausstage">' + obj.name + '</a><br/>';
		
		// add the address
		prompt += mappingObj.buildAddressAlt(obj.suburb, obj.state, obj.country);
		
		// finalise the prompt
		prompt += '</td></tr></table>';
	
	} else if(id[1] == 'venue') {
	
		// add the icon
		prompt += '<td><span class="' + mapIconography.venueColours[0] + ' mapLegendIconImg"><img id="mpz-venue-' + obj.id + '" src="' + mapIconography.venue + '" width="' + mapIconography.iconWidth + '" height="' + mapIconography.iconHeight + '"/></span></td>';
					
		// add the name and functions
		prompt += '<td><a href="' + obj.url + '" target="_ausstage">' + obj.name + '</a><br/>';
		
		// add the address
		prompt += mappingObj.buildAddress(obj.street, obj.suburb, obj.state, obj.country);
	
		// finalise the prompt
		prompt += '</td></tr></table>';
	
	} else {
	
		// add the icon
		prompt += '<td><span class="' + mapIconography.eventColours[0] + ' mapLegendIconImg"><img id="mpz-event-' + obj.id + '" src="' + mapIconography.event + '" width="' + mapIconography.iconWidth + '" height="' + mapIconography.iconHeight + '"/></span></td>';
					
		// add the name and functions
		prompt += '<td><a href="' + obj.url + '" target="_ausstage">' + obj.name + '</a><br/>';
		prompt += obj.venue.name + ', ' + mappingObj.buildAddressAlt(obj.venue.suburb, obj.venue.state, obj.venue.country);
	
		// finalise the prompt
		prompt += '</td></tr></table>';
	
	}

	$('#map_legend_confirm_delete_text').empty().append(prompt);
	
	// setup the map legend marker delete confirmation box
	$("#map_legend_confirm_delete").dialog({
		autoOpen: false,
		height: 300,
		width: 400,
		modal: true,
		buttons: {
			Yes: function() {
				$(this).dialog('close');
				mapLegendObj.doDeleteMarker(param);
			},
			No: function() {
				$(this).dialog('close');
			}
		}
	});	
		
	$('#map_legend_confirm_delete').dialog('open');
}

// a function to find a the specified object by id
MapLegendClass.prototype.findObjectById = function(collection, id) {

	if(collection.length > 0) {
		// loop through the collection
		for(var i = 0; i < collection.length; i++) {
			// check to see if the ids match
			if(collection[i].id == id) {
				// ids match so return this object
				return collection[i];
			}
		}
	}
	
	// no match found so return null
	return null
}

// a function to delete a marker from the map
MapLegendClass.prototype.doDeleteMarker = function(param) {

	// manually hide the tooltip so it isn't left hanging
	$('#' + param).tipsy('hide');

	var id   = param.split('-');
	var mapData = mappingObj.markerData.objects;
	var contributors;
	var organisations;
	var venues;
	var events;
	var idx;
	var hash;
	
	// find and delete the appropriate object
	if(id[1] == 'contributor') {
		// delete contributors from the map
		
		id = id[2];
		
		// loop through the marker data
		for (var i = 0; i < mapData.length; i++) {
		
			// get the list of contributors
			contributors = mapData[i].contributors;
			
			if(contributors.length > 0) {
				// loop through the list of contributors
				for(var x = 0; x < contributors.length; x++) {
					// check to see if the ids match
					if(contributors[x].id == id) {
						// ids match so delete
						contributors.splice(x, 1);
						if(x > 0) {
							x--;
						}
					}
				}
			}	
		}
	}
	
	// find and delete the appropriate object
	if(id[1] == 'organisation') {
		// delete organisations from the map
		
		id = id[2];
		
		// loop through the marker data
		for (var i = 0; i < mapData.length; i++) {
		
			// get the list of contributors
			organisations = mapData[i].organisations;
			
			if(organisations.length > 0) {
				// loop through the list of contributors
				for(var x = 0; x < organisations.length; x++) {
					// check to see if the ids match
					if(organisations[x].id == id) {
						// ids match so delete
						organisations.splice(x, 1);
						if(x > 0) {
							x--;
						}
					}
				}
			}	
		}
	}
	
	// find and delete the appropriate object
	if(id[1] == 'venue') {
		// delete venues from the map
		
		id = id[2];
		
		// loop through the marker data
		for (var i = 0; i < mapData.length; i++) {
		
			// get the list of contributors
			venues = mapData[i].venues;
			
			if(venues.length > 0) {
				// loop through the list of contributors
				for(var x = 0; x < venues.length; x++) {
					// check to see if the ids match
					if(venues[x].id == id) {
						// ids match so delete
						venues.splice(x, 1);
						if(x > 0) {
							x--;
						}
					}
				}
			}	
		}
	}
	
	// find and delete the appropriate object
	if(id[1] == 'event') {
		// delete venues from the map
		
		id = id[2];
		
		// loop through the marker data
		for (var i = 0; i < mapData.length; i++) {
		
			// get the list of contributors
			events = mapData[i].events;
			
			if(events.length > 0) {
				// loop through the list of contributors
				for(var x = 0; x < events.length; x++) {
					// check to see if the ids match
					if(events[x].id == id) {
						// ids match so delete
						events.splice(x, 1);
						if(x > 0) {
							x--;
						}
					}
				}
			}	
		}
	}
	
	// tidy up the map data structure by removing slots with no data
	for (var i = 0; i < mapData.length; i++) {
	
		// check for zero length arrays
		if(mapData[i].contributors.length == 0) {
			if(mapData[i].organisations.length == 0) {
				if(mapData[i].venues.length == 0) {
					if(mapData[i].events.length == 0) {
					
						//get a hash of the latitude and longitude of this entry
						hash = mappingObj.computeLatLngHash(mapData[i].latitude, mapData[i].longitude);
						idx = $.inArray(hash, mappingObj.markerData.hashes);
						
						// tidy up the hash array
						if(idx > -1) {
							mappingObj.markerData.hashes.splice(idx, 1);
						}
					
						// remove this entry from the array
						mapData.splice(i, 1);
						if(i > 0) {
							i--;
						}
					}
				}
			}
		}
	}
	
	// check for and deal with any open infoWindows
	if(mappingObj.infoWindowReference != null) {
		mappingObj.infoWindowReference.close();
		mappingObj.infoWindowReference = null;
	}
	
	// update the map and as a result update the legend
	mappingObj.updateMap();
}

// a function to show / hide a marker
// function to check to ensure the user wants to delete the marker
MapLegendClass.prototype.showHideMarker = function() {
	
	var checkbox = $(this);
	var id = checkbox.val();
	id = id.split('-');
	var idx;
	
	// manually hide the tooltip
	checkbox.tipsy('hide');
	
	// determine which type of marker to work on
	if(id[1] == 'contributor') {
		// show / hide contributor markers
		if(checkbox.attr("checked") == true) {
			//un-hide this marker
			idx = $.inArray(id[2], mappingObj.hiddenMarkers.contributors);
			if(idx != -1) {
				mappingObj.hiddenMarkers.contributors.splice(idx, 1);
			}
		} else {
			// hide this marker
			mappingObj.hiddenMarkers.contributors.push(id[2]);
		}
	} else if(id[1] == 'organisation') {
		// show / hide organisation markers
		if(checkbox.attr("checked") == true) {
			//un-hide this marker
			idx = $.inArray(id[2], mappingObj.hiddenMarkers.organisations);
			if(idx != -1) {
				mappingObj.hiddenMarkers.organisations.splice(idx, 1);
			}
		} else {
			// hide this marker
			mappingObj.hiddenMarkers.organisations.push(id[2]);
		}
	} else if(id[1] == 'venue') {
		// show / hide a venue marker
		if(checkbox.attr("checked") == true) {
			//un-hide this marker
			idx = $.inArray(id[2], mappingObj.hiddenMarkers.venues);
			if(idx != -1) {
				mappingObj.hiddenMarkers.venues.splice(idx, 1);
			}
		} else {
			// hide this marker
			mappingObj.hiddenMarkers.venues.push(id[2]);
		}
	} else {
		// show / hide an event marker
		if(checkbox.attr("checked") == true) {
			//un-hide this marker
			idx = $.inArray(id[2], mappingObj.hiddenMarkers.events);
			if(idx != -1) {
				mappingObj.hiddenMarkers.events.splice(idx, 1);
			}
		} else {
			// hide this marker
			mappingObj.hiddenMarkers.events.push(id[2]);
		}
	}
	
	// check for and deal with any open infoWindows
	if(mappingObj.infoWindowReference != null) {
		mappingObj.infoWindowReference.close();
		mappingObj.infoWindowReference = null;
	}
	
	// update the map and as a result update the legend
	mappingObj.updateMap();
	
}
