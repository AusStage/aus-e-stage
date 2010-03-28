/*
 * This file is part of the AusStage Audience Participation Service
 *
 * The AusStage Audience Participation Service is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage Audience Participation Service is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Audience Participation Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

// declare global variables
var map;
var mapOptions = {
	zoom: 3,
	mapTypeId: google.maps.MapTypeId.ROADMAP
};
var youAreHere = null;


$(document).ready(function() {

	// start the map
	map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
	map.setCenter(new google.maps.LatLng(-25.947028, 133.209639));
	
	// add an event listener so we know when the viewport changes
	google.maps.event.addListener(map, 'bounds_changed', updateMap);

	// determine where the device is
	var startLocation;
	
//	// check to see if this is an emulator
//	if(navigator.userAgent.match(/sdk/i)) {
//		// running in the emulator so fake coordinates
//		// success
//		startLocation = new google.maps.LatLng(-35.02597, 138.5727);
//		//startLocation = new google.maps.LatLng(40.69847032728747, -73.9514422416687);
//		initialMap(startLocation);
//	} else {
//		getLocation();		
//	}

// debug code
showError({message: 'no provider', code: '-1'}, 'No Provider');
});

// function to get the location of the device
function getLocation() {

	// Try W3C Geolocation (Preferred)
	if(navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			// success
			startLocation = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);		
			initialMap(startLocation);
		}, function(position_error) {
			// failure
			showError(position_error, 'W3C Geolocation');
		}, {
			// options
			enableHighAccuracy: true
		});
	} else {
		// W3C Geolocation method isn't available
		// try Google Gears
		if (google.gears) {
			// google gears is available so use it
			var geo = google.gears.factory.create('beta.geolocation');
			geo.getCurrentPosition(function(position) {
				// success
				startLocation = new google.maps.LatLng(position.latitude, position.longitude);
				initialMap(startLocation);
			}, function(position_error) {
				showError(position_error, 'Google Gears');
			}, {
				// options
				enableHighAccuracy: true
				//gearsLocationProviderUrls: null
			});
		} else {
			// not available
			showError({message: 'no provider', code: '-1'}, 'No Provider');
		}
		
	}
}

// function to build helpful errors
function showError(position_error, error_source) {

	$('#error_message').val(position_error.message);
	$('#error_code').val(position_error.code);
	$('#error_source').val(error_source);
	
	$('#map_canvas').empty();
	$('#map_canvas').hide();
	
	$('#error_form_div').show();
	
	$('#progress').hide();
}

// function to show the map
function initialMap(newLocation) {

	if(youAreHere == null) {
		youAreHere = new google.maps.Marker({
			position: newLocation,
			map: map,
			title:"You Are Here!"
		});
	} else {
		youAreHere.setMap(null);
		youAreHere = null;
		youAreHere = new google.maps.Marker({
			position: newLocation,
			map: map,
			title: 'You Are Here!'
		});
	}
	
	$('#progress').hide();
	
	// set map center to new location
	map.setZoom(15);
	map.setCenter(newLocation);
	
}

// function to detect when the viewport of the map changes
function updateMap() {

	// log to know it got called
	boundryCoords = map.getBounds();
	
	// get the coordinates
	northEast = boundryCoords.getNorthEast();
	southWest = boundryCoords.getSouthWest();
	
	// get details of any venues close by
	$.get('/mobile/lookup?type=venue-geo&ne=' + northEast.toUrlValue() + '&sw=' + southWest.toUrlValue(), function(data) {});
	
	// debug code
	//console.log(northEast.toUrlValue());
	//console.log(southWest.toUrlValue());

}
