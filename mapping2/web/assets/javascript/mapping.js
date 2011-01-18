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
 
// define a MarkerData class
function MarkerData() {
	this.contributors  = [];
	this.organisations = [];
	this.venues        = [];
	this.events        = [];
	this.latitude      = null;
	this.longitude     = null;
}

// define our mapping class
function MappingClass() {

	// variable to keep track of the map
	this.map = null;
	
	// variable to keep track of the map options
	this.options = null;
	
	// variable to keep track of the map style
	this.mapStyle = [ { featureType: "all", elementType: "all", stylers: [ { visibility: "off" } ]},
					  { featureType: "water", elementType: "geometry", stylers: [ { visibility: "on" }, { lightness: 40 }, { saturation: 0 } ] },
					  { featureType: "landscape", elementType: "geometry", stylers: [ { visibility: "on" }, { saturation: -100 } ]},
					  { featureType: "road", elementType: "geometry", stylers: [ { visibility: "on" }, { saturation: -100 }, { lightness: 40 } ] },
					  { featureType: "transit", elementType: "geometry", stylers: [ { saturation: -100 }, { lightness: 40 } ] }
					];
					
	// variable to keep track of common areas
	this.commonLocales = {
							sa:  {lat: -32,        lng: 135.763333, zoom: 6},
							wa:  {lat: -25.328055, lng: 122.298333, zoom: 5},
							nsw: {lat: -32.163333, lng: 147.016666, zoom: 6},
							qld: {lat: -22.486944, lng: 144.431666, zoom: 5},
							tas: {lat: -42.021388, lng: 146.593333, zoom: 7},
							vic: {lat: -36.854166, lng: 144.281111, zoom: 6},
							act: {lat: -35.49,     lng: 149.001388, zoom: 9},
							nt:  {lat: -19.383333, lng: 133.357777, zoom: 6},
							adelaide:  {lat: -34.93, lng: 138.60, zoom: 14},
							perth:     {lat: -31.95, lng: 115.85, zoom: 14},
							sydney:    {lat: -33.87, lng: 151.20, zoom: 14},
							brisbane:  {lat: -27.47, lng: 153.02, zoom: 14},
							hobart:    {lat: -42.88, lng: 147.32, zoom: 14},
							melbourne: {lat: -37.82, lng: 144.97, zoom: 14},
							canberra:  {lat: -35.30, lng: 149.13, zoom: 14},
							darwin:    {lat: -12.45, lng: 130.83, zoom: 14},
							international: {lat: -25.947028, lng: 133.209639, zoom: 2},
							australia:     {lat: -25.947028, lng: 133.209639, zoom: 4},
							unknown:       {lat: -25.947028, lng: 133.209639, zoom: 4}
						 };
						 
	// variable to store details of the markers
	this.markerData = {hashes: [], objects: []};
					  
	// variable to keep track of the markers on the map
	this.mapMarkers = [];      
	
	// variables to hold a height / width constant for use in computing the height / width of the map
	this.HEIGHT_BUFFER_CONSTANT = 55;
	this.WIDTH_BUFFER_CONSTANT  = 55;
	
	// variables to hold the x / y offset constants for computing the placement pointer on a marker
	this.POINTER_X_OFFSET = 16;
	this.POINTER_Y_OFFSET = 60;
	
	// variables to hold the colours of individual contributors
	this.contributorColours  = {ids: [], colours: []};
	
	// variables to hold the colours of individual organisations
	this.organisationColours = {ids: [], colours:[]};
	
	// variable to hold data use to populate infoWindows
	this.infoWindowData = [];
	
	// variable to store pointer to current infoWindow
	this.infoWindowReference = null;

}

// define a function to initialise the mapping page elements
MappingClass.prototype.init = function() {

	// resize the map when the tab is shown
	$('#tabs').bind('tabsshow', function(event, ui) {
		if (ui.panel.id == "tabs-3") { // tabs-3 == the map tab
			// update the map
			mappingObj.updateMap();
		}
	});
	
	// resize the map when the window is resized
	$(window).resize(function() {
		mappingObj.resizeMap();
	});
	
	// bind to the ajax stop event so we know we've got the data
	$("#tabs-3").bind('mappingMapGatherVenueInfo' + 'AjaxStop', mappingObj.buildVenueInfoWindow);
	
	// setup the live bind for scrolling in infoWindows
	$('.infoWindowHeaderItem').live('click', mappingObj.scrollInfoWindow);
	
	// setup the live bind for scrolling to the top of infoWindows
	$('.infoWindowToTop').live('click', mappingObj.scrollInfoWindowToTop);
}

// function to initialise the map
MappingClass.prototype.initMap = function() {

	// initialise the map
	mappingObj.options = {zoom:   mappingObj.commonLocales.unknown.zoom,
					 	  center: new google.maps.LatLng(mappingObj.commonLocales.unknown.lat, mappingObj.commonLocales.unknown.lng)
				         };	
	
	mappingObj.map = new google.maps.Map(document.getElementById("map_div"), mappingObj.options);
	
	// style the map
	var styledMapOptions = {map: mappingObj.map, name: "ausstage-style"};
	var ausstageStyle    = new google.maps.StyledMapType(mappingObj.mapStyle, styledMapOptions);
	
	mappingObj.map.mapTypes.set('ausstage', ausstageStyle);
	mappingObj.map.setMapTypeId('ausstage');
	
	// add a function to the idle event to ensure markers get events added to them
	google.maps.event.addListener(mappingObj.map, 'idle', mappingObj.addMarkerClickEvent);

}

// function to update the map
MappingClass.prototype.updateMap = function() {

	// do we need to reset or initialise the map
	if(mappingObj.map == null) {
		// initialise the map
		mappingObj.initMap();
		mappingObj.resizeMap();
	} else {
		// reset the map
	
		// remove any existing markers
		for(var i = 0; i < mappingObj.mapMarkers.length; i++) {
			// remove the marker from the map
			mappingObj.mapMarkers[i].setMap(null);
		
			// null this object
			mappingObj.mapMarkers[i] = null;
		}
	
		// reset the array
		mappingObj.mapMarkers = [];
	}
	
	// declare helper variables
	var title = null;
	
	// add the markers to the map
	var objects = mappingObj.markerData.objects;
	
	for(var i = 0; i < objects.length; i++) {
		
		// build the iconography
		var iconography = mappingObj.buildIconography(objects[i]);
		
		// calculate the pointer offset
		var offset = 0;
		
		if(objects[i].contributors.length > 0) {
			offset++;
		}
		
		if(objects[i].organisations.length > 0) {
			offset++;
		}
		
		if(objects[i].venues.length > 0) {
			offset++;
		}
		
		if(objects[i].events.length > 0) {
			offset++;
		}
		
		// create a marker with a label
		var marker = new MarkerWithLabel({
			position:     new google.maps.LatLng(objects[i].latitude, objects[i].longitude),
			map:          mappingObj.map,
			draggable:    false,
			labelContent: iconography,
			labelClass:   'mapIconContainer',
			labelAnchor:  new google.maps.Point(mappingObj.POINTER_X_OFFSET * offset, mappingObj.POINTER_Y_OFFSET),
			icon:         mapIconography.pointer
		});
		
		mappingObj.mapMarkers.push(marker);
	}
	
	// finalise the map updates
	mappingObj.resizeMap();
}

// function to build the table for the iconography layout
MappingClass.prototype.buildIconography = function(data) {

	// determine the colour to use 
	var cellColour;
	
	// build the table cells
	var cells = '';
	
	if(data.contributors.length > 0) {
		// we need to add a contributor icon
		if(data.contributors.length == 1) {
			//cellColour = mapIconography.contributorColours[0];
			var obj = data.contributors[0];
			var idx = $.inArray(obj.id, mappingObj.contributorColours.ids);
			cellColour = mappingObj.contributorColours.colours[idx];
		} else if (data.contributors.length > 1 && data.contributors.length < 6) {
			cellColour = mapIconography.contributorColours[1];
		} else if (data.contributors.length > 5 && data.contributors.length < 16) {
			cellColour = mapIconography.contributorColours[2];
		} else if (data.contributors.length > 15 && data.contributors.length < 31) {
			cellColour = mapIconography.contributorColours[3];
		} else {
			cellColour = mapIconography.contributorColours[4];
		}
		
		cells += '<td class="' + cellColour + ' mapIconImg"><img class="mapIconImgImg" id="mapIcon-contributor-' + mappingObj.computeLatLngHash(data.latitude, data.longitude) +'" src="' + mapIconography.contributor + '" width="' + mapIconography.iconWidth + '" height="' + mapIconography.iconHeight + '"/></td>';
	}
	
	if(data.organisations.length > 0) {
		// we need to add a venue icon
		if(data.organisations.length == 1) {
			//cellColour = mapIconography.organisationColours[0];
			var obj = data.organisations[0];
			var idx = $.inArray(obj.id, mappingObj.organisationColours.ids);
			cellColour = mappingObj.organisationColours.colours[idx];
		} else if (data.organisations.length > 1 && data.organisations.length < 6) {
			cellColour = mapIconography.organisationColours[1];
		} else if (data.organisations.length > 5 && data.organisations.length < 16) {
			cellColour = mapIconography.organisationColours[2];
		} else if (data.organisations.length > 15 && data.organisations.length < 31) {
			cellColour = mapIconography.organisationColours[3];
		} else {
			cellColour = mapIconography.organisationColours[4];
		}
		
		cells += '<td class="' + cellColour + ' mapIconImg"><img class="mapIconImgImg" id="mapIcon-organisation-' + mappingObj.computeLatLngHash(data.latitude, data.longitude) +'" src="' + mapIconography.organisation + '" width="' + mapIconography.iconWidth + '" height="' + mapIconography.iconHeight + '"/></td>';
	}
	
	if(data.venues.length > 0) {
		// we need to add a venue icon
		if(data.venues.length == 1) {
			cellColour = mapIconography.venueColours[0];
		} else if (data.venues.length > 1 && data.venues.length < 6) {
			cellColour = mapIconography.venueColours[1];
		} else if (data.venues.length > 5 && data.venues.length < 16) {
			cellColour = mapIconography.venueColours[2];
		} else if (data.venues.length > 15 && data.venues.length < 31) {
			cellColour = mapIconography.venueColours[3];
		} else {
			cellColour = mapIconography.venueColours[4];
		}
		
		cells += '<td class="' + cellColour + ' mapIconImg"><img class="mapIconImgImg" id="mapIcon-venue-' + mappingObj.computeLatLngHash(data.latitude, data.longitude) +'" src="' + mapIconography.venue + '" width="' + mapIconography.iconWidth + '" height="' + mapIconography.iconHeight + '"/></td>';
	}
	
	if(data.events.length > 0) {
		// we need to add a venue icon
		if(data.events.length == 1) {
			cellColour = mapIconography.eventColours[0];
		} else if (data.events.length > 1 && data.events.length < 6) {
			cellColour = mapIconography.eventColours[1];
		} else if (data.events.length > 5 && data.events.length < 16) {
			cellColour = mapIconography.eventColours[2];
		} else if (data.events.length > 15 &&data.events.length < 31) {
			cellColour = mapIconography.eventColours[3];
		} else {
			cellColour = mapIconography.eventColours[4];
		}
		
		cells += '<td class="' + cellColour + ' mapIconImg"><img class="mapIconImgImg" id="mapIcon-event-' + mappingObj.computeLatLngHash(data.latitude, data.longitude) +'" src="' + mapIconography.event + '" width="' + mapIconography.iconWidth + '" height="' + mapIconography.iconHeight + '"/></td>';
	}
	
	// start to build the table for venue data	
	var table =  '<table class="mapIcon"><tr>' + cells + '</tr>';
	cells = '';
	
	if(data.contributors.length > 0) {
		cells += '<td class="mapIconNum b-184">' + data.contributors.length + '</td>';
	}
	
	if(data.organisations.length > 0) {
		cells += '<td class="mapIconNum b-184">' + data.organisations.length + '</td>';
	}
	
	if(data.venues.length > 0) {
		cells += '<td class="mapIconNum b-184">' + data.venues.length + '</td>';
	}
	
	if(data.events.length > 0) {
		cells += '<td class="mapIconNum b-184">' + data.events.length + '</td>';
	}
	
	table += '<tr>' + cells + '</tr>';
	    
	// return the completed table
	table += '</table>';
	
	return table;	
}

// function to update the list of venues with data from the browse interface
MappingClass.prototype.addVenueBrowseData = function(data) {

	// declare helper variables
	var hash  = null;
	var idx   = null;
	var obj   = null;
	var id    = null;
	var found = false;

	// loop through the data
	for(var i = 0; i < data.length; i++) {
	
		// compute a hash
		hash = mappingObj.computeLatLngHash(data[i].latitude, data[i].longitude);
		
		// check to see if we have this venue already
		idx = $.inArray(hash, mappingObj.markerData.hashes);
		if(idx == -1) {
			// not seen this lat / lng before
			obj = new MarkerData();
			obj.venues.push(data[i]);
			obj.latitude  = data[i].latitude;
			obj.longitude = data[i].longitude
			
			mappingObj.markerData.hashes.push(hash);
			mappingObj.markerData.objects.push(obj);
		} else {
			// have seen this lat / lng before
			// check to see if the venue is already added
			obj = mappingObj.markerData.objects[idx];
			id = data[i].id;
			found = false;
			
			for(var x = 0; x < obj.venues.length; x++) {
				if(id == obj.venues[x].id) {
					found = true;
					x = obj.venues.length + 1;
				}
			} 
			
			if(found == false) {
				obj.venues.push(data[i]);	
			}	
		}
	}
	
	// switch to the map tab including an update
	$('#tabs').tabs('select', 2);
}

// function to update the list of contributors with data from the search interface
MappingClass.prototype.addContributorData = function(data) {

	// declare helper variables
	var hash  = null;
	var idx   = null;
	var obj   = null;
	var id    = null;
	var found = false;
	var contributor = null;
	var venues      = null;

	// loop through the data
	for(var i = 0; i < data.length; i++) {
	
		// get the contributor information and list of venes
		contributor = data[i].extra[0];
		venues      = data[i].venues;
		
		// have we assigned a colour to this contributor before?
		idx = $.inArray(contributor.id, mappingObj.contributorColours.ids);
		
		if(idx == -1) {
			// no we haven't
			var count = mappingObj.contributorColours.ids.length;
			
			while(count > mapIconography.individualContributors.length) {
				count = count - mapIconography.individualContributors.length;
			}
			
			mappingObj.contributorColours.ids.push(contributor.id);
			mappingObj.contributorColours.colours.push(mapIconography.individualContributors[count]);
		}
		
		// loop through the list of venues
		for(var x = 0; x < venues.length; x++) {
			
			// compute a hash
			hash = mappingObj.computeLatLngHash(venues[x].latitude, venues[x].longitude);
			
			// check to see if we have this venue already
			idx = $.inArray(hash, mappingObj.markerData.hashes);
			
			if(idx == -1) {
				// not seen this lat / lng before
				obj = new MarkerData();
				obj.contributors.push(contributor);
				obj.latitude  = venues[x].latitude;
				obj.longitude = venues[x].longitude
				
				mappingObj.markerData.hashes.push(hash);
				mappingObj.markerData.objects.push(obj);
			} else {
				// have seen this lat / lng before
				// check to see if the contributor has already been added
				obj   = mappingObj.markerData.objects[idx];
				id    = contributor.id;
				found = false;
				
				for(var y = 0; y < obj.contributors.length; y++) {
					if(id == obj.contributors[y].id) {
						found = true;
						y = obj.contributors.length + 1;
					}
				}
				
				if(found == false) {
					obj.contributors.push(contributor);
				}
			}
		}
	}
	
	// switch to the map tab including an update
	$('#tabs').tabs('select', 2);
}

// function to update the list of contributors with data from the search interface
MappingClass.prototype.addOrganisationData = function(data) {

	// declare helper variables
	var hash  = null;
	var idx   = null;
	var obj   = null;
	var id    = null;
	var found = false;
	var organisation = null;
	var venues      = null;

	// loop through the data
	for(var i = 0; i < data.length; i++) {
	
		// get the contributor information and list of venes
		organisation = data[i].extra[0];
		venues       = data[i].venues;
		
		// have we assigned a colour to this contributor before?
		idx = $.inArray(organisation.id, mappingObj.organisationColours.ids);
		
		if(idx == -1) {
			// no we haven't
			var count = mappingObj.organisationColours.ids.length;
			
			while(count > mapIconography.individualOrganisations.length) {
				count = count - mapIconography.individualOrganisations.length;
			}
			
			mappingObj.organisationColours.ids.push(organisation.id);
			mappingObj.organisationColours.colours.push(mapIconography.individualOrganisations[count]);
		}
		
		// loop through the list of venues
		for(var x = 0; x < venues.length; x++) {
			
			// compute a hash
			hash = mappingObj.computeLatLngHash(venues[x].latitude, venues[x].longitude);
			
			// check to see if we have this venue already
			idx = $.inArray(hash, mappingObj.markerData.hashes);
			
			if(idx == -1) {
				// not see this lat / lng before
				obj = new MarkerData();
				obj.organisations.push(organisation);
				obj.latitude  = venues[x].latitude;
				obj.longitude = venues[x].longitude
				
				mappingObj.markerData.hashes.push(hash);
				mappingObj.markerData.objects.push(obj);
			} else {
				// have seen this lat / lng before
				// check to see if the contributor has already been added
				obj   = mappingObj.markerData.objects[idx];
				id    = organisation.id;
				found = false;
				
				for(var y = 0; y < obj.organisations.length; y++) {
					if(id == obj.organisations[y].id) {
						found = true;
						y = obj.organisations.length + 1;
					}
				}
				
				if(found == false) {
					obj.organisations.push(organisation);
				}
			}
		}
	}
	
	// switch to the map tab including an update
	$('#tabs').tabs('select', 2);
}

// function to update the list of contributors with data from the search interface
MappingClass.prototype.addEventData = function(data) {

	// declare helper variables
	var hash  = null;
	var idx   = null;
	var obj   = null;
	var id    = null;
	var found = false;
	var event = null;
	var venues = null;

	// loop through the data
	for(var i = 0; i < data.length; i++) {
	
		// get the contributor information and list of venes
		event  = data[i].extra[0];
		venues = data[i].venues;
		
		// loop through the list of venues
		for(var x = 0; x < venues.length; x++) {
			
			// compute a hash
			hash = mappingObj.computeLatLngHash(venues[x].latitude, venues[x].longitude);
			
			// check to see if we have this venue already
			idx = $.inArray(hash, mappingObj.markerData.hashes);
			
			if(idx == -1) {
				// not see this lat / lng before
				obj = new MarkerData();
				obj.events.push(event);
				obj.latitude  = venues[x].latitude;
				obj.longitude = venues[x].longitude
				
				mappingObj.markerData.hashes.push(hash);
				mappingObj.markerData.objects.push(obj);
			} else {
				// have seen this lat / lng before
				// check to see if the contributor has already been added
				obj   = mappingObj.markerData.objects[idx];
				id    = event.id;
				found = false;
				
				for(var y = 0; y < obj.events.length; y++) {
					if(id == obj.events[y].id) {
						found = true;
						y = obj.events.length + 1;
					}
				}
				
				if(found == false) {
					obj.events.push(event);
				}
			}
		}
	}
	
	// switch to the map tab including an update
	$('#tabs').tabs('select', 2);
}


// compute a LatLng hash
MappingClass.prototype.computeLatLngHash = function(latitude, longitude) {

	var lat = parseFloat(latitude);
	var lng = parseFloat(longitude);
	var latlngHash = (lat.toFixed(6) + "" + lng.toFixed(6));
	latlngHash     = latlngHash.replace(".","").replace(",", "").replace("-","");
	latlngHash     = latlngHash.replace(".","").replace(",", "").replace("-","");
	return latlngHash;
}

// compute the height of the map
MappingClass.prototype.computeMapHeight = function() {

	// start the height calculations
	//var height = mappingObj.HEIGHT_CONSTANT;
	var height = 0;
	
	// get the height of various elements
	var wrapper   = $('.wrapper').height();
	var header    = $('.header').height();
	var tabHeader = $('.fix-ui-tabs').height();
	var pushElem  = $('.push').height();
	var footer    = $('.footer').height();
	
	height = wrapper - (header + tabHeader + pushElem + footer + mappingObj.HEIGHT_BUFFER_CONSTANT);
	
	return Math.floor(height);
}

// compute the width of the map
MappingClass.prototype.computeMapWidth = function() {

	// start the width calculations
	var width = 0;
	
	// get the width of various elements
	var wrapper = $('.wrapper').width();
	var sidebar = $('.sidebar').width();
	
	width = wrapper - (sidebar + mappingObj.WIDTH_BUFFER_CONSTANT);
	
	return Math.floor(width);
}

// a function to resize the map
MappingClass.prototype.resizeMap = function() {

	if(mappingObj.map != null) {
		var mapDiv = $(mappingObj.map.getDiv());
		mapDiv.height(mappingObj.computeMapHeight());
		mapDiv.width(mappingObj.computeMapWidth());
	
		google.maps.event.trigger(mappingObj.map, 'resize');
		
		// manually trigger an idle event
		var zoom = mappingObj.map.getZoom();
		mappingObj.map.setZoom(zoom - 1);
		mappingObj.map.setZoom(zoom);
		//mappingObj.map.setCenter(new google.maps.LatLng(mappingObj.commonLocales.unknown.lat, mappingObj.commonLocales.unknown.lng)); 
	} else {
		mappingObj.initMap();
	}
}

// a function to build the map iconography table
MappingClass.prototype.buildIconographyHelp = function() {

	// build each row in turn
	var row = '<th scope="row">Contributors</th>';
	row += '<td style="border: 1px solid #aaaaaa;">Individual<br/>Colour</td>';
	for(var i = 1; i < mapIconography.contributorColours.length; i++) {
		row += '<td class="' + mapIconography.contributorColours[i] + '"><img src="' + mapIconography.contributor +'"/></td>';
	}
	$('#map_iconography_contributors').empty().append(row);
	
	row = '<th scope="row">Organisations</th>';
	row += '<td style="border: 1px solid #aaaaaa;">Individual<br/>Colour</td>';
	for(var i = 1; i < mapIconography.organisationColours.length; i++) {
		row += '<td class="' + mapIconography.organisationColours[i] + '"><img src="' + mapIconography.organisation +'"/></td>';
	}
	$('#map_iconography_organisations').empty().append(row);

	row = '<th scope="row">Venues</th>';
	for(var i = 0; i < mapIconography.venueColours.length; i++) {
		row += '<td class="' + mapIconography.venueColours[i] + '"><img src="' + mapIconography.venue +'"/></td>';
	}
	$('#map_iconography_venues').empty().append(row);
	
	row = '<th scope="row">Events</th>';
	for(var i = 0; i < mapIconography.eventColours.length; i++) {
		row += '<td class="' + mapIconography.eventColours[i] + '"><img src="' + mapIconography.event +'"/></td>';
	}
	$('#map_iconography_events').empty().append(row);
	
	// build the individual colour pallette
	var row = '';
	var rows = '';
	
	var startColour  = 39;
	var endColour    = 86;
	var cellCount    = 1;
	var maxCellCount = 12;
	
	for(var i = startColour; i <= endColour; i++) {
	
		if(cellCount == 1) {
			row = '<tr><td class="b-' + i + ' mapIconImg2">&nbsp;</td>';
		} else if(cellCount == maxCellCount) {
			row += '</tr>';
			rows += row;
			row = '';
			cellCount = 0;
		} else {
			row += '<td class="b-' + i + ' mapIconImg2">&nbsp;</td>';
		}
		
		cellCount++;
	}
	
	$("#map_iconography_individual_colours").empty().append(rows);
}

// define a function to add the click event to the marker components 
MappingClass.prototype.addMarkerClickEvent = function() {

	// get all of the individual icons by class
	$('.mapIconImgImg').each(function() {
		var icon = $(this);
		
		if(icon.hasClass('mapIconImgImgEvnt') == false) {
			icon.click(mappingObj.iconClick);
			icon.addClass('mapIconImgImgEvnt');
		}
	});
}

// respond to click events on icons
MappingClass.prototype.iconClick = function(event) {

	// split the id of this icon into it's component parts
	var tokens = this.id.split('-');
	
	// get the markerData object for this location
	var idx = $.inArray(tokens[2], mappingObj.markerData.hashes);
	var data = mappingObj.markerData.objects[idx];
	
	// reset the infoWindowData variable
	mappingObj.infoWindowData = [];
	mappingObj.infoWindowReference = null;
	
	// determine what type of icon this is
	if(tokens[1] == 'contributor') {
		// this is a contributor icon
	} else if(tokens[1] == 'organisation') {
		// this is a organisation icon
	} else if(tokens[1] == 'venue') {
		// this is a venue icon
		
		// create a queue
		var ajaxQueue = $.manageAjax.create("mappingMapGatherVenueInfo", {
			queue: true
		});

		// define a basic marker			
		var marker = new google.maps.Marker({
			position: new google.maps.LatLng(data.latitude, data.longitude),
			map:      mappingObj.map,
			visible:  false
		});
		
		// define placeholder content		
		var content = '<div class="infoWindowContent">' + buildInfoMsgBox('Loading venue information, please wait...') + '</div>';
		
		// build and so the infoWindow
		mappingObj.infoWindowReference = new google.maps.InfoWindow({
			content: content
		});
		
		mappingObj.infoWindowReference.open(mappingObj.map, marker);
		
		// use the queue to get the data
		for(var i = 0; i < data.venues.length; i++) {
	
			// build the url
			var url  = BASE_URL + 'events?task=venue&id=' + data.venues[i].id;
		
			ajaxQueue.add({
				success: mappingObj.processInfoWindowData,
				url: url
			});
		}
	} else {
		// this is a event icon
	}
}

// function to process the results of the ajax infoWindow data lookups
MappingClass.prototype.processInfoWindowData = function(data) {
	mappingObj.infoWindowData = mappingObj.infoWindowData.concat(data);
}

// function to buld the infoWindow for venues
MappingClass.prototype.buildVenueInfoWindow = function() {

	// define a variable to store the infoWindow content
	var content = '<div class="infoWindowContent">';
	var header  = '<div class="infoWindowContentHeader"><ul>';
	var list    = '<div class="infoWindowContentList">';
	
	// build the content
	for(var i = 0; i < mappingObj.infoWindowData.length; i++) {
	
		var data = mappingObj.infoWindowData[i];
		
		// add the venue to the header
		header += '<li class="infoWindowHeaderItem clickable" id="infoWindowScroll-' + data.id + '">' + data.name + '</li>';
		
		// add the venue content
		if(i > 0) {
			list += '<p class="infowWindowListHeader" id="infoWindowScrollTo-' + data.id + '"><span class="infoWindowListTitle">' + data.name + '</span> <span class="infoWindowToTop clickable">[top]</span><br/>';
		} else {
			list += '<p class="infowWindowListHeader" id="infoWindowScrollTo-' + data.id + '"><span class="infoWindowListTitle">' + data.name + '</span><br/>';
		}
		
		// add the link
		list += '<a href="' + data.url + '" target="_ausstage">';
		
		// output the address
		if(data.country == 'Australia') {
			list += data.street + ', ' + data.suburb + ', ' + data.state;
		} else {
			list += data.street + ', ' + data.suburb + ', ' + data.country;
		}
		
		// finalise the link and start of the content
		list += '</a></p><ul>';
		
		// add the events
		for(var x = 0; x < data.events.length; x++) {
		
			list += '<li><a href="' + data.events[x].url + '" target="_ausstage">' + data.events[x].name + '</a>, ' + data.events[x].firstDate.replace(' ', '&nbsp;'); + '</li>';
			
		}
		
		// finalise the list of events
		list += '</ul>';
		
	}
	
	// finish the content
	header += '</ul></div>';
	list   += '</div>';
	
	if(mappingObj.infoWindowData.length > 1) {
		content += header + list + '</div>';
	} else {
		content += list + '</div>';
	}
	
	// replace the content of the infoWindow
	mappingObj.infoWindowReference.setContent(content);
}

// function to scroll the info window
MappingClass.prototype.scrollInfoWindow = function() {

	var id = this.id.split('-');
	
	$('.infoWindowContent').parent().scrollTo($('#infoWindowScrollTo-' + id[1]), {duration:1000});
}

// function to scroll the info window
MappingClass.prototype.scrollInfoWindowToTop = function() {
	
	$('.infoWindowContent').parent().scrollTo($('.infoWindowContentHeader'), {duration:1000});
}
