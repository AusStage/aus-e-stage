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

/*
 * JavaScript doesn't provide true constants so any variabls in
 * all caps should be considered constants
 */
var MARKER_BASE_URL = "/mapping2/markers?";
var infowindow = new google.maps.InfoWindow({}); 
var mapData = null;
var markers = null;
var mapID = null;
// object to hold marker locations
var locations = {};

/*
 * A function to retrieve XML using AJAX and prepare the data
 * for use with v3 of the Google Maps API
 *
 * This function relies on JQuery being present
 *
 * @param type the type of data to retrieve
 * @param id   the id number(s) of the organisation / contributor
 * @param updateHeader a flag to indicate that the header of the page shold be updated
 */
function getMapData(type, id, focus, start, finish, updateHeader) {

	// declare helper variables
	var url;
	var map = null;
	
	if (updateHeader == true) {
	
		// determine what type of API call to make
		if(type == "organisation") {
			// this is a request for organisation data
			url = MARKER_BASE_URL + "type=organisation&id=" + id;
		} else if(type == "contributor") {
			// this is a request for contributor data
			url = MARKER_BASE_URL + "type=contributor&id=" + id;
		} else {
			// unknown type
			return null;
		}
	
		/*
		 * $ is a short hand way to refer to the jQuery object
		 * use the 'get' method to retrieve data using AJAX and pass it to an callback function for processing
		 * more information on 'get' here: http://api.jquery.com/jQuery.get/
		 */
		$.get(url, function(data, textStatus, XMLHttpRequest) {
			mapData = data;	
		// determine if we should update the header
		//if(updateHeader == true) {
//			console.log("Updating the header");

			// extract the entity elements from the XML
			var entities = data.documentElement.getElementsByTagName("entity");
	
			// build the header
			var header = "Map of events for: ";
	
			// loop through the entity elements adding the data to the header
			for(var i = 0; i < entities.length; i++) {
	
				header += '<a href="' + entities[i].getAttribute("url") + '" target="ausstage">' + entities[i].getAttribute("name") + "</a>, ";
			}
			
			// tidy up the header by removing the last two characters
			header = header.substr(0, header.length -2);
	
			// add the new header into the page
			// the $("#map_name") syntax uses jQuery to locate the element within the page with the id attribute "map_name"
			$("#map_name").empty(); // empty the contents of this tag
			$("#map_name").append(header); // append the contents header variable into the tag
			
			mapID = document.getElementById("map");
			
			// create a new map and centre it on the focus
			map = createMap(mapID, focus, start, finish);			
			
			// build the time slider
			buildTimeSlider(data);
			
		});	//end of $.get()
	}else if (updateHeader == false){
		if (mapID != null) {
			map = createMap(mapID, focus, start, finish);

		}
	};//end of if (updateHeader == true)	
	
}

//create map
function createMap(mapID, focus, start, finish){
    var myOptions = {
		      zoom: getZoom(focus),
		      center: getLatLng(focus),
		      mapTypeId: google.maps.MapTypeId.ROADMAP
		    };

    var map = new google.maps.Map(mapID, myOptions);

    google.maps.event.addListener(map, 'click', function() {
    	infowindow.close();
    });

    // extract the markers from the xml
    if (markers == null){
    	markers = mapData.documentElement.getElementsByTagName("marker");
    }
    var j = 0;
    var markerArray = new Array();
    // build a group of markers on the map
    for (var i = 0; i < markers.length; i++) {

    	// get the colour of the icon
    	//var eventCount = parseInt(markers[i].getAttribute("events"));

    	// filter markers if required and add the marker on the map
    	if(checkMarkers(i, focus, start, finish) == true) {			
    		// build a latlng object for this marker
    		var latlng = getMarkerLatLng(i);

    		// get the event info for this marker
    		var info   = markers[i].textContent;					
    		if(typeof(info) == "undefined") {
    			info = markers[i].text;
    		}

    		// get the name, suburb and postcode
    		var venueName   = markers[i].getAttribute("name");
    		var venueSuburb = markers[i].getAttribute("suburb");
    		venueName = venueName + ", " + venueSuburb;

    		markerArray[j] = createMarker(map, latlng, info, venueName);
    	    j = j+1;
    	}
    }//end of for (build group of markers)	
    console.log("Num of makers created: " + j);
    return map;
}

//get the zoom number according to the focus
function getZoom(focus){
	switch(focus){
	case '1':
		//map.setCenter(new GLatLng(-30.058333, 135.763333), 6); //SA
		return 6; //SA		
	case '2':
		return 5; //WA		
	case '3':
		 return 6; //NSW
	case '4':
		return 5; //QLD
	case '5':
		return 7; //TAS
	case '6':
		return 6; //VIC
	case '7':
		return 9; //ACT
	case '8':
		return 6; //NT
/*	case '1a':
		return 14; // Adelaide
	case '2a':
		return 14; // Perth
	case '3a':
		return 14; // Sydney
	case '4a':
		return 14; // Brisbane
	case '5a':
		return 14; // Hobart
	case '6a':
		return 14; // Melbourne
	case '7a':
		return 14; // Canberra
	case '8a':
		return 14; // Darwin
*/	case '9':
		return 2; //outside Aus
	case 'nolimit':
		return 2; //all venues
	case 'a':
		return 4; // Aus only			
	default:
		return 4; // default, AUS
	}
}

//create Latlng to set the ma center according to the focus	
function getLatLng(focus) {
	
	switch(focus){
	case '1':
		//map.setCenter(new GLatLng(-30.058333, 135.763333), 6); //SA
		return new google.maps.LatLng(-32, 135.763333); //SA
	case '2':
		return new google.maps.LatLng(-25.328055, 122.298333); //WA	
	case '3':
		return new google.maps.LatLng(-32.163333, 147.016666); //NSW
	case '4':
		return new google.maps.LatLng(-22.486944, 144.431666); //QLD		
	case '5':
		return new google.maps.LatLng(-42.021388, 146.593333); //TAS
	case '6':
		return new google.maps.LatLng(-36.854166, 144.281111); //VIC
	case '7':
		return new google.maps.LatLng(-35.49, 149.001388); //ACT
	case '8':
		return new google.maps.LatLng(-19.383333, 133.357777); //NT
/*	case '1a':
		return new google.maps.LatLng(-34.93, 138.60); // Adelaide		
	case '2a':
		return new google.maps.LatLng(-31.95, 115.85); // Perth
	case '3a':
		return new google.maps.LatLng(-33.87, 151.20); // Sydney
	case '4a':
		return new google.maps.LatLng(-27.47, 153.02); // Brisbane
	case '5a':
		return new google.maps.LatLng(-42.88, 147.32); // Hobart		
	case '6a':
		return new google.maps.LatLng(-37.82, 144.97); // Melbourne
	case '7a':
		return new google.maps.LatLng(-35.30, 149.13); // Canberra		
	case '8a':
		return new google.maps.LatLng(-12.45, 130.83); // Darwin		
*/	case '9':
		return new google.maps.LatLng(-25.947028, 133.209639); //outside Aus	
	case 'nolimit':
		return new google.maps.LatLng(-25.947028, 133.209639); //all venues
	case 'a':
		return new google.maps.LatLng(-25.947028, 133.209639); // Aus only		
	default:
		return new google.maps.LatLng(-25.947028, 133.209639); // default, AUS		
	}
}

function getMarkerLatLng(i){
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
	return new google.maps.LatLng(lat, lng);
}

//Filter markers by date & state 
//true: not filtered and will create it on the map
//false: filtered and will not display on the map

function checkMarkers(i, focus, start, finish){
	
	var okToAdd = false;
	
	if(start != null) {
		// filter using date
		var fDate = parseInt(markers[i].getAttribute("fdate"));
		var lDate = parseInt(markers[i].getAttribute("ldate"));
	
		// check on the fdate
		if((fDate >= start && fDate <= finish) || (lDate >= start && lDate <= finish)) {
			// filter markers by state
			okToAdd = checkMarkersByState(i, focus);			
		}	
	}else {
		okToAdd = checkMarkersByState(i, focus);	
	}
	
	return okToAdd;
}

// filter markers by state
function checkMarkersByState(i, focus){		

	if(focus != null && focus != "nolimit") {
		// use the state to filter
		var state = markers[i].getAttribute("state");

		if(focus == "a" && state != "9") {
			return true;
		}else if(focus == "1" && state == "1") {
			return true;
		}else if(focus == "2" && state == "2") {
			return true;
		}else if(focus == "3" && state == "3") {
			return true;
		}else if(focus == "4" && state == "4") {
			return true;
		}else if(focus == "5" && state == "5") {
			return true;
		}else if(focus == "6" && state == "6") {
			return true;
		}else if(focus == "7" && state == "7") {
			return true;
		}else if(focus == "8" && state == "8") {
			return true;
		}else if(focus == "9" && state == "9") {
			return true;
		}			
	} else {
		return true;
	}
	return false;
}

//build a single marker
function createMarker(map, latlng, info, venueName) {
	
	var marker = new google.maps.Marker({  
		   position: latlng,  
		   map: map,
		   title: venueName
		 });
	
	google.maps.event.addListener(marker, 'click', function() {  
		   infowindow.setContent(info);
		   infowindow.open(map, marker);  
		 });  
	
	return marker;
}

//function to build the time slider
function buildTimeSlider(data) {

	// define an array to store the dates
	var dates = {};
	
	// build a group of dates
	for (var i = 0; i < markers.length; i++) {
		
		//get the date	
		var first = markers[i].getAttribute("fdatestr");
		var last  = markers[i].getAttribute("ldatestr");
		
		// add the date to the hash if required
		if(dates[first] == null) {
			dates[first] = first;
		}
		
		if(dates[last] == null) {
			dates[last] = last;
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
	$(".slider").selectToUISlider({labels: 5}).hide();
	$(".tohide").hide();
	
}

/* 
 * Define an Ajax Error handler if something bad happens when we make an Ajax call
 * more information here: http://api.jquery.com/ajaxError/
 */
$(document).ready(function() {

	// getting marker xml for contributors
	$("#map").ajaxError(function(e, xhr, settings, exception) {
		// hide certain elements
		$("#map_name").hide();
		$("#map_header").hide();
		$("#map_legend").hide();
	
		// empty the contents of this tag
		$("#map").empty();
	
		// add the error message to the page
		$("#map").append('<p style="text-align: center"><strong>Error: </strong>An error occured whilst processing your request. Please try again.<br/>If the problem persists please contact the site administrator.</p>'); 
	});
});
