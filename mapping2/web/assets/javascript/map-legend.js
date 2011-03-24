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

	this.HEIGHT_BUFFER_CONSTANT = 0;
	this.DEFAULT_MARKER_ZOOM_LEVEL = 17;
	
	this.controlsOpen = false;
	this.mapLegendHeight = 0;
	
	this.recordData = null;

}

// initialise the map legend
MapLegendClass.prototype.init = function() {
	mapLegendObj.hideLegend();
	
	// add the pan and zoom section
	mapLegendObj.buildPanAndZoom();
	
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
	
	// setup the map legend map reset confirmation box
	$("#map_legend_confirm_reset").dialog({
		autoOpen: false,
		height: 300,
		width: 400,
		modal: true,
		buttons: {
			Yes: function() {
				$(this).dialog('close');
				mappingObj.resetMap();
			},
			No: function() {
				$(this).dialog('close');
			}
		}
	});
	
	// setup the map legend map reset confirmation box
	$("#map_legend_clustering_error").dialog({
		autoOpen: false,
		height: 300,
		width: 400,
		modal: true,
		buttons: {
			OK: function() {
				$(this).dialog('close');
			}
		}
	});
	
	// setup click event on the reset map button
	$("#btn_reset_map").click(function() {
		$("#map_legend_confirm_reset").dialog('open');
	});
	
	// setup click event on the enable clustering button
	$("#btn_cluster_map").click(function() {
		if(mappingObj.clusteringEnabled == false) {
			mappingObj.clusteringEnabled = true;
			mappingObj.updateMap();
			$("#btn_cluster_map").val('Disable Clustering');
		} else {
		
			// check to see if we can disable clustering
			if(mappingObj.markerData.objects.length >= mappingObj.applyClusterLimit) {
				$("#mlce_max").empty().append(mappingObj.applyClusterLimit);
				$("#mlce_current").empty().append(mappingObj.markerData.objects.length);
				$("#map_legend_clustering_error").dialog('open');
			} else {
				mappingObj.clusteringEnabled = false;
				mappingObj.updateMap();
				$("#btn_cluster_map").val('Enable Clustering');
			}
		}
	});
	
	// setup an event to detect a change in the map controls and adjust legend hieght accordingly
	$('#mapLegendPanAndZoomControls').bind( "accordionchange", function(event, ui) {
		if(mapLegendObj.controlsOpen == false) {
			mapLegendObj.HEIGHT_BUFFER_CONSTANT = ($('#mapLegendPanAndZoomControls').height() - 50 ) * -1;
			mapLegendObj.controlsOpen = true;
		} else {
			mapLegendObj.controlsOpen = false;
		}
		mapLegendObj.resizeMapLegend();
		mapLegendObj.HEIGHT_BUFFER_CONSTANT = 0;
	});
}

// function to hide the legend
MapLegendClass.prototype.hideLegend = function() {
	$('.mapControlsContainer').hide();
	$('.mapLegendContainer').hide();
}

// function to show the legend
MapLegendClass.prototype.showLegend = function() {
	mapLegendObj.updateLegend();
	$('.mapControlsContainer').show();
	$('.mapLegendContainer').show();
	
	// update the size of the legend if required
	mapLegendObj.resizeMapLegend();

}

// a function to resize the map
MapLegendClass.prototype.resizeMapLegend = function() {
	
	var mapLegendDiv = $('.mapLegendContainer');
	mapLegendDiv.height(mapLegendObj.computeMapLegendHeight());
}

// compute the height of the map
MapLegendClass.prototype.computeMapLegendHeight = function() {

	// get the coordinates of the map legend and footer
	var legendCoords = $('.mapLegendContainer').offset();
	var footerCoords = $('.footer').offset();
	
	var newHeight = footerCoords.top - (legendCoords.top - mapLegendObj.HEIGHT_BUFFER_CONSTANT);

	return Math.floor(newHeight);	

/*
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
*/
}

// function to update the legend
MapLegendClass.prototype.updateLegend = function() {

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
			// output the date
			tableData += ', ' + obj.firstDisplayDate.replace(/\s/g, '&nbsp;');
			
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
	
	// determine the text to show on the cluster button
	if(mappingObj.clusteringEnabled == true) {
		$("#btn_cluster_map").val('Disable Clustering');
	} else {
		$("#btn_cluster_map").val('Enable Clustering');
	}
}

// a function to build the hide checkbox 
MapLegendClass.prototype.buildShowHide = function(idx, type, id) {

	if(idx == -1) {
		return '</td><td class="mapLegendShowHide"><input type="checkbox" name="mapLegendShowHide" class="mapLegendShowHideMarker" checked="checked" value="mlh-' + type + '-' + id + '"/></td>';
	} else {
		return '</td><td class="mapLegendShowHide"><input type="checkbox" name="mapLegendShowHide" class="mapLegendShowHideMarker" value="mlh-' + type + '-' + id + '"/></td>';
	}
}

// a function to build the delete icon
MapLegendClass.prototype.buildDelete = function(type, id) {

	// add the delete icon
	return '<td class="mapLegendDelete"><span id="mld-' + type + '-' + id + '" class="mapLegendDeleteIcon ui-icon ui-icon-closethick clickable" style="display: inline-block;"></span></td>';

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
	
		// process contributors
		mapLegendObj.createRecordDataArray(mapData[i].contributors,  recordData.contributors);
		mapLegendObj.createRecordDataArray(mapData[i].organisations, recordData.organisations);
		mapLegendObj.createRecordDataArray(mapData[i].venues,        recordData.venues);
		mapLegendObj.createRecordDataArray(mapData[i].events,        recordData.events);
	}
	
	// sort the arrays
	recordData.contributors.objects.sort(sortContributorArrayAlt);
	recordData.organisations.objects.sort(sortOrganisationArrayAlt);
	recordData.venues.objects.sort(sortVenueArray);
	recordData.events.objects.sort(sortEventArray);
	
	// update the id indexes
	mapLegendObj.reindexRecordDataArray(recordData.contributors);
	mapLegendObj.reindexRecordDataArray(recordData.organisations);
	mapLegendObj.reindexRecordDataArray(recordData.venues);
	mapLegendObj.reindexRecordDataArray(recordData.events);
	
	mapLegendObj.recordData = recordData;
	
	return recordData;
}

// copy an array and turn it into the record data array
MapLegendClass.prototype.createRecordDataArray = function(src, dest) {

	// declare helper variables
	var idx = null;

	// process the array
	if(src.length > 0) {
		for(var i = 0; i < src.length; i++) {
			// check to see if it is in the dest array already
			idx = $.inArray(src[i].id, dest.ids);
			if(idx == -1) {
				// no so add it
				dest.ids.push(src[i].id);
				dest.objects.push(src[i]);
			}
		}
	}
}

// reindex record data array
MapLegendClass.prototype.reindexRecordDataArray = function(arr) {

	if(arr.objects.length > 0) {
		arr.ids = [];
		for(var i = 0; i < arr.objects.length; i++) {
			arr.ids.push(arr.objects[i].id);
		}
	}
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
	
	if(id[1] == 'venue') {
		// find the venue object
		idx = $.inArray(id[2], mapLegendObj.recordData.venues.ids);
		obj = mapLegendObj.recordData.venues.objects[idx];
		
		map.setZoom(mapLegendObj.DEFAULT_MARKER_ZOOM_LEVEL);
		map.panTo(new google.maps.LatLng(obj.latitude, obj.longitude));
	
		// bind to the idle event to determine when to show the infoWindow
		google.maps.event.addListenerOnce(mappingObj.map, 'idle', function() {
			$('#mapIcon-venue-' + mappingObj.computeLatLngHash(obj.latitude, obj.longitude)).click();	
		});
	} else if(id[1] == 'event') {
		// find the event object
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
			
			obj = mapLegendObj.findObjectById(mapData[i].events, id);
			
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
		// output the date
		prompt += ', ' + obj.firstDisplayDate.replace(/\s/g, '&nbsp;');
	
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
			mapLegendObj.doMarkerDeletion(mapData[i].contributors, id);				
		}
	}
	
	// find and delete the appropriate object
	if(id[1] == 'organisation') {
		// delete organisations from the map
		
		id = id[2];
		
		// loop through the marker data
		for (var i = 0; i < mapData.length; i++) {
			mapLegendObj.doMarkerDeletion(mapData[i].organisations, id);	
		}
	}
	
	// find and delete the appropriate object
	if(id[1] == 'venue') {
		// delete venues from the map
		
		id = id[2];
		
		// loop through the marker data
		for (var i = 0; i < mapData.length; i++) {
			mapLegendObj.doMarkerDeletion(mapData[i].venues, id);	
		}
	}
	
	// find and delete the appropriate object
	if(id[1] == 'event') {
		// delete venues from the map
		
		id = id[2];
		
		// loop through the marker data
		for (var i = 0; i < mapData.length; i++) {
			mapLegendObj.doMarkerDeletion(mapData[i].events, id);	
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

// function to do the actual deletion of markers
MapLegendClass.prototype.doMarkerDeletion = function(arr, id) {

	// get the list of contributors
	//contributors = mapData[i].contributors;
	
	if(arr.length > 0) {
		// loop through the list of contributors
		for(var i = 0; i < arr.length; i++) {
			// check to see if the ids match
			if(arr[i].id == id) {
				// ids match so delete
				arr.splice(i, 1);
				if(i > 0) {
					i--;
				}
			}
		}
	}
}

// a function to show / hide a marker
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
