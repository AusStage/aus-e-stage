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
	// TODO add other marker types
	var objects = mappingObj.markerData.objects;
	
	for(var i = 0; i < objects.length; i++) {
		
		// build the iconography
		var iconography = mappingObj.buildIconography(objects[i]);
		
		// create a marker with a label
		var marker = new MarkerWithLabel({
			position:     new google.maps.LatLng(objects[i].latitude, objects[i].longitude),
			map:          mappingObj.map,
			draggable:    false,
			labelContent: iconography,
			labelClass:   'mapIconContainer',
			labelAnchor:  new google.maps.Point(16, 68),
			icon:         mapIconography.pointer
		});
		
		mappingObj.mapMarkers.push(marker);
	}
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
			cellColour = mapIconography.contributorColours[0];
		} else if (data.contributors.length > 1 && data.contributors.length < 6) {
			cellColour = mapIconography.contributorColours[1];
		} else if (data.contributors.length > 5 && data.contributors.length < 16) {
			cellColour = mapIconography.contributorColours[2];
		} else if (data.contributors.length > 15 && data.contributors.length < 31) {
			cellColour = mapIconography.contributorColours[3];
		} else {
			cellColour = mapIconography.contributorColours[4];
		}
		
		cells += '<td class="' + cellColour + ' mapIconImg"><img src="' + mapIconography.contributor +'"/></td>';
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
		
		cells += '<td class="' + cellColour + ' mapIconImg"><img src="' + mapIconography.venue +'"/></td>';
	}
	
	// start to build the table for venue data	
	var table =  '<table class="mapIcon"><tr>' + cells + '</tr>';
	cells = '';
	
	if(data.contributors.length > 0) {
		cells += '<td class="mapIconNum b-184">' + data.contributors.length + '</td>';
	}
	
	if(data.venues.length > 0) {
		cells += '<td class="mapIconNum b-184">' + data.venues.length + '</td>';
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
		
		// loop through the list of venues
		for(var x = 0; x < venues.length; x++) {
			
			// compute a hash
			hash = mappingObj.computeLatLngHash(venues[x].latitude, venues[x].longitude);
			
			// check to see if we have this venue already
			idx = $.inArray(hash, mappingObj.markerData.hashes);
			
			if(idx == -1) {
				// not see this lat / lng before
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


// compute a LatLng hash
MappingClass.prototype.computeLatLngHash = function(latitude, longitude) {

	var lat = parseFloat(latitude);
	var lng = parseFloat(longitude);
	var latlngHash = (lat.toFixed(6) + "" + lng.toFixed(6));
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
		//mappingObj.map.setCenter(new google.maps.LatLng(mappingObj.commonLocales.unknown.lat, mappingObj.commonLocales.unknown.lng)); 
	} else {
		mappingObj.initMap();
	}
}

// a function to build the map iconography table
MappingClass.prototype.buildIconographyHelp = function() {

	// build each row in turn
	var row = '<th scope="row">Contributors</th>';
	for(var i = 0; i < mapIconography.contributorColours.length; i++) {
		row += '<td class="' + mapIconography.contributorColours[i] + '"><img src="' + mapIconography.contributor +'"/></td>';
	}
	$('#map_iconography_contributors').empty().append(row);
	
	row = '<th scope="row">Organisations</th>';
	for(var i = 0; i < mapIconography.organisationColours.length; i++) {
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
}
