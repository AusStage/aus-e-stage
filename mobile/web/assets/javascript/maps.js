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
/*var mapOptions = {
	zoom: 15,
	mapTypeId: google.maps.MapTypeId.ROADMAP
};
*/
var mapOptions = {
	zoom: 9,
	mapTypeId: google.maps.MapTypeId.ROADMAP
};

$(document).ready(function() {

	var startLocation;
	
	// Try W3C Geolocation (Preferred)
	if(navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			// success
			startLocation = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);		
			showMap(startLocation);
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
				showMap(startLocation);
			}, function(position_error) {
				//failure
				// hack to check if we're running in the emulator
				if(navigator.userAgent.match(/sdk/i)) {
					// running in the emulator so ignore error and fake coordinates
					//$('#map_canvas').empty();
					//$('#map_canvas').append('<p>Running in the emulator</p>');
					// success
					startLocation = new google.maps.LatLng(138.5727, -35.02597);
					showMap(startLocation);
				} else {
					showError(position_error, 'Google Gears');
				}
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
});

// function to build helpful errors
function showError(position_error, error_source) {

	$('#error_message').val(position_error.message);
	$('#error_code').val(position_error.code);
	$('#error_source').val(error_source);
	
	$('#map_canvas').empty();
	$('#map_canvas').hide();
	
	$('#error_form_div').show();
}

// function to show the map
function showMap(location) {

	// get a new map object and set to the location we've found
	map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
	map.setCenter(location);
	var marker = new google.maps.Marker({
      position: location,
      map: map,
      title:"You Are Here!"
  });
}
