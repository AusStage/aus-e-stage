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
			map.setCenter(new GLatLng(-30.058333, 135.763333), 6);
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
