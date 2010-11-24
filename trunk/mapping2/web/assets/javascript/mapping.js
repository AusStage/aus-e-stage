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

// define our mapping class
function MappingClass() {

	// variable to keep track of the map
	this.map = null;
	
	// variable to keep track of the map options
	this.options = null;
	
	// variable to keep track of the map style
	this.mapStyle = [ 
						{featureType: "poi.park", elementType: "all", stylers:[ {invert_lightness: true}, {visibility: "simplified"}, {lightness: 40},{hue: "#ccff00"}, {saturation: -79}]},
						{featureType: "water", elementType: "all", stylers: [ { hue: "#00ffdd" },{visibility: "simplified"},{lightness: 95} ] } ,
						{featureType: "landscape", elementType: "all", stylers:[ {lightness: -30},{hue: "#ccff00"},  {saturation: -79}]},
						{featureType: "road.highway", elementType: "all", stylers:[{visibility: "simplified"}, {lightness: 40},{hue: "#c0c0c0"}, {saturation: -79}, {gamma: 0.44}]},
						{featureType: "road.arterial", elementType: "all", stylers:[{visibility: "simplified"}, {lightness: 40},{hue: "#c0c0c0"}, {saturation: -79}, {gamma: 0.44}]},
						{featureType: "road.local", elementType: "all", stylers:[ {visibility: "simplified"}, {lightness: 40},{hue: "#c0c0c0"}, {saturation: -79}, {gamma: 0.44}]},
						{featureType: "administrative", elementType: "all", stylers:[{visibility: "off"}, { hue: "#0099ff"},{lightness: 82},{ visibility: "off"}]},
						{featureType: "poi", elementType: "all", stylers:[ {visibility: "off"}]} 
					];
					
	// variable to keep track of common areas
	this.commonLocales = {
							"sa":  {lat: -32,        lng: 135.763333, zoom: 6},
							"wa":  {lat: -25.328055, lng: 122.298333, zoom: 5},
							"nsw": {lat: -32.163333, lng: 147.016666, zoom: 6},
							"qld": {lat: -22.486944, lng: 144.431666, zoom: 5},
							"tas": {lat: -42.021388, lng: 146.593333, zoom: 7},
							"vic": {lat: -36.854166, lng: 144.281111, zoom: 6},
							"act": {lat: -35.49,     lng: 149.001388, zoom: 9},
							"nt":  {lat: -19.383333, lng: 133.357777, zoom: 6},
							"adelaide":  {lat: -34.93, lng: 138.60, zoom: 14},
							"perth":     {lat: -31.95, lng: 115.85, zoom: 14},
							"sydney":    {lat: -33.87, lng: 151.20, zoom: 14},
							"brisbane":  {lat: -27.47, lng: 153.02, zoom: 14},
							"hobart":    {lat: -42.88, lng: 147.32, zoom: 14},
							"melbourne": {lat: -37.82, lng: 144.97, zoom: 14},
							"canberra":  {lat: -35.30, lng: 149.13, zoom: 14},
							"darwin":    {lat: -12.45, lng: 130.83, zoom: 14},
							"international": {lat: -25.947028, lng: 133.209639, zoom: 2},
							"australia":     {lat: -25.947028, lng: 133.209639, zoom: 4},
							"unknown":       {lat: -25.947028, lng: 133.209639, zoom: 4}
						 };               

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
	console.log("an update of the map has been requested");
}
