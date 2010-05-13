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

// define helper variables
var trajectoryColours = ["#FF0000", "#00FF00", "#00FF00", "#FF00FF", "#FFFF00"]; // colours for trajectories

// function to show a map
function showMap(data, trajectory, focus, start, finish) {

	// tidy up any previous maps
	GUnload();
	
	// create a new map and centre it on australia
	var map = new GMap2(document.getElementById("map"));
	
	// determine where to centre the map
	//recentre the map
	switch(focus){
		case '1':
			//map.setCenter(new GLatLng(-30.058333, 135.763333), 6); //SA
			map.setCenter(new GLatLng(-32, 135.763333), 6); //SA
			break;
		case '2':
			map.setCenter(new GLatLng(-25.328055, 122.298333), 5); //WA
			break;
		case '3':
			map.setCenter(new GLatLng(-32.163333, 147.016666), 6); //NSW
			break;
		case '4':
			map.setCenter(new GLatLng(-22.486944, 144.431666), 5); //QLD
			break;
		case '5':
			map.setCenter(new GLatLng(-42.021388, 146.593333), 7); //TAS
			break;
		case '6':
			map.setCenter(new GLatLng(-36.854166, 144.281111), 6); //VIC
			break;
		case '7':
			map.setCenter(new GLatLng(-35.49, 149.001388), 9); //ACT
			break;
		case '8':
			map.setCenter(new GLatLng(-19.383333, 133.357777), 6); //NT
			break;
		case '9':
			map.setCenter(new GLatLng(-25.947028, 133.209639), 2); //outside Aus
			break;
		case 'a':
			map.setCenter(new GLatLng(-25.947028, 133.209639), 4); // Aus only
			break;
		default:
			map.setCenter(new GLatLng(-25.947028, 133.209639), 4); // defult, AUS
			break;
	}
	
	// finish setting up the map
	map.setUIToDefault();
	
	// adjust the dates if necessary
	if(start != null && finish != null) {
		start = start.replace("-", "");
		start = start.replace("-", "");
		if(start.length == 4) {
			start += "0101";
		} else if(start.length == 6) {
			start += "01";
		}
		start = parseInt(start);
		
		finish = finish.replace("-", "");
		finish = finish.replace("-", "");
		if(finish.length == 4) {
			finish += "1231";
		} else if(finish.length == 6) {
			finish += "31";
		}		
		finish = parseInt(finish);
	}
	
	// extract the markers from the xml
	var markers = data.documentElement.getElementsByTagName("marker");
	
	// object to hold marker locations
	var locations = {};
	
	// build a group of markers
	for (var i = 0; i < markers.length; i++) {
	
		// build a hash of this location
		var lat = parseFloat(markers[i].getAttribute("lat"));
		var lng = parseFloat(markers[i].getAttribute("lng"));
		var latlngHash = (lat.toFixed(6) + "" + lng.toFixed(6));
		latlngHash     = latlngHash.replace(".","").replace(",", "").replace("-","");
		
		if(locations[latlngHash] == null) {
			// not seen this location before
			// add to hash
			locations[latlngHash] = true;
		} else {
			// have seen this location before
			// adjust the lat and lng
			var randomNumber = Math.floor(Math.random()*3) + 1;
			randomNumber = "0.000" + randomNumber;
			randomNumber = parseFloat(randomNumber);
			
			lat = lat + randomNumber;
			lng = lng + randomNumber;
		}
		
		// build a latlng object for this marker
		var latlng = new GLatLng(lat, lng);
		var info   = markers[i].textContent;
		
		if(typeof(info) == "undefined") {
			info = markers[i].text;
		}
		
		// get the colour of the icon
		var eventCount = parseInt(markers[i].getAttribute("events"));
		var colour;
	
		if(eventCount == 1) {
			colour = "#CCBAD7";
		}else if(eventCount < 6) {
			colour = "#9A7BAB";
		}else if(eventCount > 5 && eventCount < 16) {
			colour = "#7F649B";
		}else if(eventCount > 15 && eventCount < 31) {
			colour = "#69528E";
		} else {
			colour = "#4D3779";
		}
		
		// filter markers
		var okToAdd = false;
		
		// filter markers by date
		if(start != null) {
			// filter using date
			var fDate = parseInt(markers[i].getAttribute("fdate"));
			var lDate = parseInt(markers[i].getAttribute("ldate"));
			
			// check on the fdate
			if((fDate >= start && fDate <= finish) || (lDate >= start && lDate <= finish)) {
				// filter markers by state
				if(focus != null && focus != "nolimit") {
					// use the state to filter
					var state = markers[i].getAttribute("state")
			
					if(focus == "a" && state != "9") {
						okToAdd = true;
					}else if(focus == "1" && state == "1") {
						okToAdd = true;
					}else if(focus == "2" && state == "2") {
						okToAdd = true;
					}else if(focus == "3" && state == "3") {
						okToAdd = true;
					}else if(focus == "4" && state == "4") {
						okToAdd = true;
					}else if(focus == "5" && state == "5") {
						okToAdd = true;
					}else if(focus == "6" && state == "6") {
						okToAdd = true;
					}else if(focus == "7" && state == "7") {
						okToAdd = true;
					}else if(focus == "8" && state == "8") {
						okToAdd = true;
					}else if(focus == "9" && state == "9") {
						okToAdd = true;
					}			
				} else {
					okToAdd = true;
				}
			}
			
		} else {
			// filter markers by state
			if(focus != null && focus != "nolimit") {
				// use the state to filter
				var state = markers[i].getAttribute("state")
			
				if(focus == "a" && state != "9") {
					okToAdd = true;
				}else if(focus == "1" && state == "1") {
					okToAdd = true;
				}else if(focus == "2" && state == "2") {
					okToAdd = true;
				}else if(focus == "3" && state == "3") {
					okToAdd = true;
				}else if(focus == "4" && state == "4") {
					okToAdd = true;
				}else if(focus == "5" && state == "5") {
					okToAdd = true;
				}else if(focus == "6" && state == "6") {
					okToAdd = true;
				}else if(focus == "7" && state == "7") {
					okToAdd = true;
				}else if(focus == "8" && state == "8") {
					okToAdd = true;
				}else if(focus == "9" && state == "9") {
					okToAdd = true;
				}			
			} else {
				okToAdd = true;
			}
		}
		
		// add marker
		if(okToAdd == true) {
			map.addOverlay(createMarker(latlng, info, colour));
		}
	}	
	
	// build the trajectories if required
	if(trajectory == true && focus == "nolimit") {
		buildTrajectory(data, map);
	}
}

// function to build a trajectory
function buildTrajectory(data, map) {

	// declare additional variables
	var colourIndex = 0;
	var maxColourIndex = trajectoryColours.length;

	// extract the trajectory from the xml
	var trajectories = data.documentElement.getElementsByTagName("trajectory");
	
	// loop through all of the trajectories
	for(var i = 0; i < trajectories.length; i++) {
	
		// get the colour
		var trajectoryColour = trajectoryColours[colourIndex];
		colourIndex++;
		
		if(colourIndex == maxColourIndex) {
			colourIndex = 0;
		}
		
		// get the coordinates
		var coordinateList = trajectories[i].childNodes;
		
		// loop through the coordinates adding lines
		for(var x = 0; x < coordinateList.length; x++) {
		
			// get the individual coordinates
			var coordinates = coordinateList[x].textContent;
			
			if(typeof(coordinates) == "undefined") {
				coordinates = coordinateList[x].text;
			}	
			
			// split into the pair of coordinates
			coordinates = coordinates.split(" ");
			
			var start = coordinates[0].split(",");
			var finish = coordinates[1].split(",");
			
			map.addOverlay(new GPolyline([new GLatLng(start[0], start[1]), new GLatLng(finish[0], finish[1])], trajectoryColour, 5));		
		} // coordinates loop
	} // trajectories loop

} // end buildTrajectory function

// build a single marker
function createMarker(latlng, info, colour) {
	
	// make a new icon
	var newIcon = MapIconMaker.createMarkerIcon({width: 32, height: 32, primaryColor: colour});
	
	// make a new marker
	var marker = new GMarker(latlng, {icon: newIcon});
	
	// add an event listener to listen for a click and show the InfoWindow
	GEvent.addListener(marker, 'click', function() {
		marker.openInfoWindowHtml(info, {maxWidth:520, maxHeight:400,autoScroll:true});
	});
	
	return marker;
}


// function to build the time slider
function buildTimeSlider(data) {

	// define an array to store the dates
	var dates = {};

	// extract the markers from the xml
	var markers = data.documentElement.getElementsByTagName("marker");
	
	// build a group of dates
	for (var i = 0; i < markers.length; i++) {
		
		//get the date	
		var first = markers[i].getAttribute("fdatestr");
		
		// add the date to the has if required
		if(dates[first] == null) {
			dates[first] = first;
		}
	}
	
	// clear the time slider
	$("#event_start").removeOption(/./);
	$("#event_finish").removeOption(/./);
	
	// add the new options
	$("#event_start").addOption(dates, false);
	$("#event_finish").addOption(dates, false);
	
	// clear any current selected values
	$("#event_start").selectOptions('clear');
	$("#event_finish").selectOptions('clear');
	
	// sort the options
	$("#event_start").sortOptions();
	$("#event_finish").sortOptions();
	
	// select the last and first options before building the time slider
	$("#event_start option:first").attr("selected", "selected");
	$("#event_finish option:last").attr("selected", "selected");
	
	// remove any existing slider
	$("#sliderComponent").remove();
	
	// create the slider
	$(".slider").selectToUISlider({labels: 0}).hide();
	$(".tohide").hide();
	
	addSliderDescription();
}

// add a description to the slider
function addSliderDescription() {
	// add some descriptive text
	$("#sliderComponent").append('<p style="text-align: center;">Use the above time slider to select a date range.<br/>Only venues where all events fall outside the selected date range will be removed.</p>');
}

// function to manage clicking the show trajectory option
function showTrajectory() {

	var showTraj = $("#show_trajectory:checked").val();
	
	if(showTraj != null) {
		// reset and disable time slider
		$("#event_start option:first").attr("selected", "selected");
		$("#event_finish option:last").attr("selected", "selected");
		$("#sliderComponent").hide();
		$("#sliderComponent").remove();
		
		// reset and disable state limit
		$("#state option:first").attr("selected", "selected");
		$("#state").attr("disabled", "disabled");
	} else {
		// enable time slider
		$(".slider").selectToUISlider({labels: 0}).hide();
		addSliderDescription();

		// enable state limit
		$("#state").removeAttr("disabled", "disabled");
	}
}

// Make Google API Scripts clean up
$(document).unload(function(){
	GUnload();
});


/*
// helper function to parse a date into a number
function parseDateToNumber(data) {

	// strip the dashes
	data = data.replace('-', '');
	
	// turn the string as a number
	return new Number(data);
	
}
*/
