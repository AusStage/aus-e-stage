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
}

// define our mapping class
function MappingClass() {

	// variable to keep track of the map
	this.map = null;
	
	// variable to keep track of the map options
	this.options = null;
	
	// variable to keep track of the map style
	this.mapStyle = [{ featureType: "all",   elementType: "all", stylers: [ { lightness: 0 }, { saturation: 0 },{ visibility: "off" } ] },
					 { featureType: "water", elementType: "geometry", stylers: [ { visibility: "on" }, { lightness: 60 }, { saturation: -24 } ] },
					 { featureType: "landscape", elementType: "geometry", stylers: [ { lightness: 0 }, { saturation: -100 }, { visibility: "on" } ]},
					 { featureType: "road", elementType: "geometry", stylers: [ { visibility: "on" }, { saturation: -100 }, { lightness: 70 }]},
					 { featureType: "transit", elementType: "geometry", stylers: [ { saturation: -100 }, { lightness: 100 }]}
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
	
		// get the venues
		var venues = objects[i].venues;
		
		// create a marker with a label
		var marker = new MarkerWithLabel({
			position:     new google.maps.LatLng(venues[0].latitude, venues[0].longitude),
			map:          mappingObj.map,
			draggable:    false,
			labelContent: '<table class="mapIcon"><tr><td><img src="' + mapIconography.venue +'"/></td></tr><tr><td>' + venues.length + '</td></tr></table>',
			//labelContent:   '<div><div style="width: 25px; height: 32px"><img src="' + mapIconography.venue +'"/></div></div>',
			labelClass:   'mapIcon',
			labelAnchor:  new google.maps.Point(13, 58),
			icon:         mapIconography.pointer
		});
		
		mappingObj.mapMarkers.push(marker);
		
		/*
		
		for(var x = 0; x < venues.length; x++) {
	
			// build the title
			var title = venues[x].name + ', ';
		
			if(venues[x].street != null) {
				 title += venues[x].street + ', ';
			} 
		
			if(venues[x].suburb != null) {
				title += venues[x].suburb;
			} else {
				title = title.substr(0, title.length - 2);
			}
			
			var marker = new google.maps.Marker({  
				position: new google.maps.LatLng(venues[x].latitude, venues[x].longitude),
				title:    title,
				icon:     mapIcons.venue,
				map:      mappingObj.map  
			});
		}
		*/
	}
	
	//debug code
	//console.log(mappingObj.mapMarkers.length);
}

// function to update the list of venues with data from the browse interface
MappingClass.prototype.addVenueBrowseData = function(data) {

	// declare helper variables
	var hash = null;
	var idx  = null;
	var obj  = null;

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
			
			mappingObj.markerData.hashes.push(hash);
			mappingObj.markerData.objects.push(obj);
		} else {
			// have seen this lat / lng before
			obj = mappingObj.markerData.objects[idx];
			obj.venues.push(data[i]);		
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
