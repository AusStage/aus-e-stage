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

// function to load and show a map
function showMap(type, id, traj, start, finish, limit) {

	// tidy up any previous maps
	GUnload();
	
	// declare helper variables
	var markerUrl = "";
	
	if(type == "org") {
		// update the map header links
		$("#map_header_kml").attr("href", "data?action=kml&type=orgdata&id=" + id);
		$("#map_header_link").attr("href", "maplinks.jsp?type=org&id=" + id);
		$("#map_header_export").attr("href", "exportdata.jsp?type=org&id=" + id);
		
		// buld an appropriate marker url
		if(start == null && finish == null) {
			// use the default marker url
			markerUrl = "data?action=markers&type=orgdata&id=" + id;
		} else {
			// pass date parameters to marker xml
			markerUrl = "data?action=markers&type=orgdata&id=" + id + "&start=" + start + "&finish=" + finish;
		}
		
		// add state limit if appropriate
		if(limit != null && limit != "nolimit") {
			markerUrl += "&state=" + limit;
		}			
	}
	
	// create a new map and centre it on australia
	var map = new GMap2(document.getElementById("map"));
	
	// determine where to centre the map
	switch(limit){
		case '1':
			//map.setCenter(new GLatLng(-30.058333, 135.763333), 6);
			map.setCenter(new GLatLng(-32, 135.763333), 6); //SA
			break;
		case '2':
			map.setCenter(new GLatLng(-25.328055, 122.298333), 5);
			break;
		case '3':
			map.setCenter(new GLatLng(-32.163333, 147.016666), 6);
			break;
		case '4':
			map.setCenter(new GLatLng(-22.486944, 144.431666), 5);
			break;
		case '5':
			map.setCenter(new GLatLng(-42.021388, 146.593333), 7);
			break;
		case '6':
			map.setCenter(new GLatLng(-36.854166, 144.281111), 6);
			break;
		case '7':
			map.setCenter(new GLatLng(-35.49, 149.001388), 9);
			break;
		case '8':
			map.setCenter(new GLatLng(-19.383333, 133.357777), 6);
			break;
		default:
			map.setCenter(new GLatLng(-25.947028, 133.209639), 4);
			break;
	}
	
	// finish setting up the map
	map.setUIToDefault();
    
    // get the marker data
    GDownloadUrl(markerUrl, function(data) {
    
    	// object to hold marker locations
    	var locations = {};
    	
    	// array to hold point locations for trajectory info
    	// need to use an array as order is significant
    	var trajectoryPoints = Array();
    
    	// get the xml data
    	var xml = GXml.parse(data);
    	
    	// extract the markers from the xml
    	var markers = xml.documentElement.getElementsByTagName("marker");
    	
    	// build a group of markers
    	for (var i = 0; i < markers.length; i++) {
    		
    		// get data from the xml
    		var event  = markers[i].getAttribute("event");
    		var venue  = markers[i].getAttribute("venue");
    		var first  = markers[i].getAttribute("first");
    		var last   = markers[i].getAttribute("last");
    		var suburb = markers[i].getAttribute("suburb");
    		var url    = markers[i].getAttribute("url");
    		var latlng = new GLatLng(parseFloat(markers[i].getAttribute("lat")), parseFloat(markers[i].getAttribute("lng")));
    		
    		// add coordinates to the array
    		trajectoryPoints.push(markers[i].getAttribute("lat") + " " + markers[i].getAttribute("lng"));
    		
    		// build javascript objects
    		var location   = {latlng: latlng, event: event, venue: venue, first: first, last: last, suburb: suburb, url: url};
    		var latlngHash = (latlng.lat().toFixed(6) + "" + latlng.lng().toFixed(6));
			latlngHash     = latlngHash.replace(".","").replace(",", "").replace("-","");
			
			// add marker to a hash of markers
			if (locations[latlngHash] == null) {
				locations[latlngHash] = []
			}
		
			locations[latlngHash].push(location);
		}
		
		// create markers
		for (var latlngHash in locations) {
			
			// get locations	
			var events = locations[latlngHash];
			
			// more than one marker at the same location?
			if (events.length > 1) {
				
				// yes, create a clustered marker
				map.addOverlay(createClusteredMarker(events));
				
			} else {
				
				// no, create just a marker
				map.addOverlay(createMarker(events));
			}
		}
		
		// create trajectory - if required
		// remove duplicates from the array
		if(traj == "true") {
			//trajectoryPoints = unique(trajectoryPoints);
			var current = null;
			var previous = null;
			var colourGradient = getColourGradient();
			var lineColour = null;
			var index = null;
			
			for(var i = 0; i < trajectoryPoints.length; i++) {
				if(i == 0) {
					previous = trajectoryPoints[0];
				} else {
					
					// get the current point
					current = trajectoryPoints[i];
					
					// get the colour for this line
					if (trajectoryPoints.length > 255) {
						// use array if over 255 colours
						index = Math.round((255 / trajectoryPoints.length) * i);
						
						if(index == 0) {
							index = 1;
						}
						
						lineColour = colourGradient[index];
					} else {
						// use scale function if less than 255
						lineColour = getScaledColour(i - 1, trajectoryPoints.length - 1);
					}
					
					// create a new polyline
					var polyline = new GPolyline(
						[
							new GLatLng(parseFloat(previous.split(" ")[0]), parseFloat(previous.split(" ")[1])),
							new GLatLng(parseFloat(current.split(" ")[0]), parseFloat(current.split(" ")[1]))
						], 
						lineColour, 6);
						
					// add plyline to the map
					map.addOverlay(polyline);
					
					previous = current;
				}
			}
		}		
	});
}

// function to show a map - version 2
function showMap2(data, traj, focus, start, finish) {

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
    
    // object to hold marker locations
	var locations = {};
	
	// array to hold point locations for trajectory info
	// need to use an array as order is significant
	var trajectoryPoints = Array();
	
	// if necessary convert the start and finish dates
	if(start != null && finish != null) {
		start = parseDateToNumber(start);
		finish = parseDateToNumber(finish);
	}
	
	// extract the markers from the xml
	var markers = data.documentElement.getElementsByTagName("marker");
	
	// build a group of markers
	for (var i = 0; i < markers.length; i++) {
	
		// declare helper variable
		var okToAdd = true;
		
		// get data from the xml
		var event  = markers[i].getAttribute("event");
		var venue  = markers[i].getAttribute("venue");
		var first  = markers[i].getAttribute("first");
		var last   = markers[i].getAttribute("last");
		var suburb = markers[i].getAttribute("suburb");
		var url    = markers[i].getAttribute("url");
		
		// check on the dates for this event if necessary
		if(start != null && finish != null) {
			// compare the dates
			var firstAsNum = parseDateToNumber(markers[i].getAttribute("sliderDate"));
			
			if(firstAsNum >= start && firstAsNum <= finish) {
				okToAdd = true;
			} else {
				okToAdd = false;
			}
		}
		
		// do we add this marker to the map?
		if(okToAdd == true) {
		
			// build a latlng object for this marker
			var latlng = new GLatLng(parseFloat(markers[i].getAttribute("lat")), parseFloat(markers[i].getAttribute("lng")));
			
			// add coordinates to the array
			trajectoryPoints.push(markers[i].getAttribute("lat") + " " + markers[i].getAttribute("lng"));
			
			// build javascript objects
			var location   = {latlng: latlng, event: event, venue: venue, first: first, last: last, suburb: suburb, url: url};
			var latlngHash = (latlng.lat().toFixed(6) + "" + latlng.lng().toFixed(6));
			latlngHash     = latlngHash.replace(".","").replace(",", "").replace("-","");
			
			// add marker to a hash of markers
			if (locations[latlngHash] == null) {
				locations[latlngHash] = []
			}
		
			locations[latlngHash].push(location);
		}
	}
	
	// create markers
	for (var latlngHash in locations) {
		
		// get locations	
		var events = locations[latlngHash];
		
		// more than one marker at the same location?
		if (events.length > 1) {
			
			// yes, create a clustered marker
			map.addOverlay(createClusteredMarker(events));
			
		} else {
			
			// no, create just a marker
			map.addOverlay(createMarker(events));
		}
	}
	
	// create trajectory lines - if required
	if(traj == "true" || traj == true) {
		//trajectoryPoints = unique(trajectoryPoints);
		var current = null;
		var previous = null;
		var colourGradient = getColourGradient();
		var lineColour = null;
		var index = null;
		
		for(var i = 0; i < trajectoryPoints.length; i++) {
			if(i == 0) {
				previous = trajectoryPoints[0];
			} else {
				
				// get the current point
				current = trajectoryPoints[i];
				
				// get the colour for this line
				if (trajectoryPoints.length > 255) {
					// use array if over 255 colours
					index = Math.round((255 / trajectoryPoints.length) * i);
					
					if(index == 0) {
						index = 1;
					}
					
					lineColour = colourGradient[index];
				} else {
					// use scale function if less than 255
					lineColour = getScaledColour(i - 1, trajectoryPoints.length - 1);
				}
				
				// create a new polyline
				var polyline = new GPolyline(
					[
						new GLatLng(parseFloat(previous.split(" ")[0]), parseFloat(previous.split(" ")[1])),
						new GLatLng(parseFloat(current.split(" ")[0]), parseFloat(current.split(" ")[1]))
					], 
					lineColour, 6);
					
				// add plyline to the map
				map.addOverlay(polyline);
				
				previous = current;
			}
		}
	}		
}

// function to show a map - version 2
function showMap3(data, focus, start, finish) {

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
			var randomNumber = Math.floor(Math.random()*10) + 20;
			randomNumber = "0.0000" + randomNumber;
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
		
		// build the marker
		var marker = createMarker2(latlng, info, colour);
		
		// add marker to the map
		map.addOverlay(marker);
	}	
}

// build a single marker
function createMarker2(latlng, info, colour) {
	
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
		var first = markers[i].getAttribute("sliderDate");
		
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
	$(".slider").selectToUISlider({labels: 10}).hide();
	$(".tohide").hide();
}

// build a single marker
function createMarker(events) {
	// get the event
	var event = events[0];

	// make a new icon
	var newIcon = MapIconMaker.createMarkerIcon({width: 32, height: 32, primaryColor: "#CCBAD7"});
	
	// make a new marker
	var marker = new GMarker(event.latlng, {icon: newIcon});
	
	// make html to respond to click
	if(event.last != "") {
		var html = "<ul><li><a href=\"" + event.url + "\" target=\"_blank\" title=\"More information in AusStage\">" + event.event + "</a>, "
				 + event.venue + ", " + event.suburb + ", " + event.first + " - " + event.last + "</li></ul>";
	} else {
		var html = "<ul><li><a href=\"" + event.url + "\" target=\"_blank\" title=\"More information in AusStage\">" + event.event + "</a>, "
				 + event.venue + ", " + event.suburb + ", " + event.first + "</li></ul>";
	}
	
	// add an event listener to listen for a click and show the InfoWindow
	GEvent.addListener(marker, 'click', function() {
		marker.openInfoWindowHtml(html, {maxWidth:520, maxHeight:400,autoScroll:true});
	});
	
	return marker;
}

// build a clustered marker
function createClusteredMarker(events) {

	// determine colour of the icon
	var iconColour;
	
	if(events.length > 1 && events.length < 6) {
		iconColour = "#9A7BAB";
	}else if(events.length > 5 && events.length < 16) {
		iconColour = "#7F649B";
	}else if(events.length > 15 && events.length < 31) {
		iconColour = "#69528E";
	} else {
		iconColour = "#4D3779";
	}

	// make a new icon
	var newIcon = MapIconMaker.createMarkerIcon({width: 32, height: 32, primaryColor: iconColour});
	
	// make a new marker
	var marker = new GMarker(events[0].latlng, {icon: newIcon});
	
	// build the html for all of these events
	var html = "<ul>";
	
	for(var i = 0; i < events.length; i++) {
	
		// make html to respond to click
		if (events[i].last != "") {
			html += "<li><a href=\"" + events[i].url + "\" target=\"_blank\" title=\"More information in AusStage\">" + events[i].event + "</a>, "
				 + events[i].venue + ", " + events[i].suburb + ", " + events[i].first + " - " + events[i].last + "</li>";
		} else {
			html += "<li><a href=\"" + events[i].url + "\" target=\"_blank\" title=\"More information in AusStage\">" + events[i].event + "</a>, "
				 + events[i].venue + ", " + events[i].suburb + ", " + events[i].first + "</li>";
		}
	}
	
	// finish the html
	html += "</ul>";
	
	// add an event listener to listen for a click and show the html
	GEvent.addListener(marker, 'click', function() {
		marker.openInfoWindowHtml(html, {maxWidth:520, maxHeight:400,autoScroll:true});
	});
	
	return marker;
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
