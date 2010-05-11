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

// function to show a map - version 2
function showMap(data, traj, focus, start, finish) {

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
		
		if(info == "" || info == undefined) {
			info = markers[i].innerText;
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
		
		// determine if we add this marker
		if(start != null) {
			// filter using date
			var fDate = parseInt(markers[i].getAttribute("fdate"));
			var lDate = parseInt(markers[i].getAttribute("ldate"));
			
			// check on the fdate
			if((fDate >= start && fDate <= finish) || (lDate >= start && lDate <= finish)) {
				map.addOverlay(createMarker(latlng, info, colour));
			}
		} else {
			// just add the marker to the map
			map.addOverlay(createMarker(latlng, info, colour));
		}
	}	
}

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
	
	// select the last option of the event_finish select
	$("#event_start option:first").attr("selected", "selected");
	$("#event_finish option:last").attr("selected", "selected");
	
	// remove any existing slider
	$("#sliderComponent").remove();
	
	// create the slider
	$(".slider").selectToUISlider({labels: 0}).hide();
	$(".tohide").hide();
	
	// add some descriptive text
	$("#sliderComponent").append('<p style="text-align: center;">Use the above time slider to select a date range.<br/>Only venues where all events fall outside the selected date range will be removed.</p>');
}

// Make Google API Scripts clean up
$(document).unload(function(){
	GUnload();
});

// generate a series of colours from pure red to pure yellow
function getColourGradient() {

	var redHex = "FF";
	var greenHex;
	var blueHex = "00";
	var colourGradient = new Array(256);
	
	for(i = 1; i <= 255; i++) {
		// get the yellow colour
		greenHex = i.toString(16);
		
		if(greenHex.length < 2) {
			greenHex = "0" + greenHex;
		}
		
		// add the new colour to the gradient
		colourGradient[i] = "#" + redHex + greenHex + blueHex;
	}
	
	return colourGradient;
}

// function to get a colour along a scale from red to yellow
function getScaledColour(index, maximum) {

	// determine starting colour components
	var redHex   = "FF";
	var greenHex = null;
	var blueHex  = "00";
	
	// define values for formula
	var startGreen = 0;
	var endGreen   = 255;
	
	// calculate the green value
	var greenVal = startGreen + ((endGreen - startGreen) * (index / (maximum -1)));
	
	// round the green value to an integer
	greenVal = Math.round(greenVal);
	
	// convert from decimal to hexadecimal
	greenHex = greenVal.toString(16);
	
	// pad the hexadecimal number if required
	if(greenHex.length < 2) {
		greenHex = "0" + greenHex;
	}
	
	// return the final colour
	return "#" + redHex + greenHex + blueHex;
}

// helper function to parse a date into a number
function parseDateToNumber(data) {

	// strip the dashes
	data = data.replace('-', '');
	
	// turn the string as a number
	return new Number(data);
	
}
