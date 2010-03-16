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
			$('#map_canvas').empty();
			$('#map_canvas').append('<p>An error occured while determining your location. Details are: <br/>' + position_error.message + '</p>');
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
				// hack to check if we're running in the emulator
				if(navigator.userAgent.match(/sdk/i)) {
					// running in the emulator so ignore error and fake coordinates
					$('#map_canvas').empty();
					$('#map_canvas').append('<p>Running in the emulator</p>');
				} else {
					// error
					$('#map_canvas').empty();
					$('#map_canvas').append('<p>An error occured while determining your location. Details are: <br/>' + position_error.message + '</p>');
				}
			}, {
				// options
				enableHighAccuracy: true,
				gearsLocationProviderUrls: null
			});
		} else {
			// not available
			$('#map_canvas').empty();
			$('#map_canvas').append('<p>The W3C Geolocation API isn\'t availble.</p><p>The Google Gears API isn\'t availble.</p>');
		}
		
	}
});
	

/*
$(document).ready(function() {

	var myOptions = {
	    zoom: 8,
	    mapTypeId: google.maps.MapTypeId.ROADMAP
	};

	var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
	
	var initialLocation;
var siberia = new google.maps.LatLng(60, 105);
var newyork = new google.maps.LatLng(40.69847032728747, -73.9514422416687);
var browserSupportFlag =  new Boolean();

function initialize() {
  var myOptions = {
    zoom: 6,
    mapTypeId: google.maps.MapTypeId.ROADMAP
  };
  var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
 
  // Try W3C Geolocation (Preferred)
  if(navigator.geolocation) {
    browserSupportFlag = true;
    navigator.geolocation.getCurrentPosition(function(position) {
      initialLocation = new google.maps.LatLng(position.coords.latitude,position.coords.longitude);
      map.setCenter(initialLocation);
    }, function() {
      handleNoGeolocation(browserSupportFlag);
    });
  // Try Google Gears Geolocation
  } else if (google.gears) {
    browserSupportFlag = true;
    var geo = google.gears.factory.create('beta.geolocation');
    geo.getCurrentPosition(function(position) {
      initialLocation = new google.maps.LatLng(position.latitude,position.longitude);
      map.setCenter(initialLocation);
    }, function() {
      handleNoGeoLocation(browserSupportFlag);
    });
  // Browser doesn't support Geolocation
  } else {
    browserSupportFlag = false;
    handleNoGeolocation(browserSupportFlag);
  }
 
  function handleNoGeolocation(errorFlag) {
    if (errorFlag == true) {
      alert("Geolocation service failed.");
      initialLocation = newyork;
    } else {
      alert("Your browser doesn't support geolocation. We've placed you in Siberia.");
      initialLocation = siberia;
    }
    map.setCenter(initialLocation);
  }
}
});
*/
