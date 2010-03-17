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

$(document).ready(function() {

	// Try W3C Geolocation (Preferred)
	if(navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			// success
			$('#map_canvas').empty();
			$('#map_canvas').append('<p>Latitude: ' + position.coords.latitude + '<br/>Longitude: ' + position.coords.longitude + '</p>');
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
				$('#map_canvas').empty();
				$('#map_canvas').append('<p>Latitude: ' + position.latitude + '<br/>Longitude: ' + position.longitude + '</p>');
			}, function(position_error) {
				//failure
				// hack to check if we're running in the emulator
				if(navigator.userAgent.match(/sdk/i)) {
					// running in the emulator so ignore error and fake coordinates
					//$('#map_canvas').empty();
					//$('#map_canvas').append('<p>Running in the emulator</p>');
					showError(position_error, 'Google Gears');
				} else {
					showError(position_error, 'Google Gears');
				}
			}, {
				// options
				enableHighAccuracy: true,
				gearsLocationProviderUrls: null
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
