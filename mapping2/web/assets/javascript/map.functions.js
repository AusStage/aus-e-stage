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
var MARKER_BASE_URL = "http://localhost:8080/mapping2/markers?";
var infowindow = new google.maps.InfoWindow({}); 
var map = null;
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
	var mapData = null;
	
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
	
		// determine if we should update the header
		if(updateHeader == true) {
			console.log("Updating the header");

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
			
			// create a new map and centre it on the focus
		    var myOptions = {
		      zoom: getZoom(focus),
		      center: getLatLng(focus),
		      mapTypeId: google.maps.MapTypeId.ROADMAP
		    };

			map = new google.maps.Map(document.getElementById("map"), myOptions);
					
			google.maps.event.addListener(map, 'click', function() {
			    infowindow.close();
			    });
			
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
				var latlng = new google.maps.LatLng(lat, lng);
				
				// get the event info for this marker
				var info   = markers[i].textContent;
				
				if(typeof(info) == "undefined") {
					info = markers[i].text;
				}
				
				// get the name, suburb and postcode
				var venueName   = markers[i].getAttribute("name");
				var venueSuburb = markers[i].getAttribute("suburb");
				
				venueName = venueName + ", " + venueSuburb;
				
				// get the colour of the icon
				var eventCount = parseInt(markers[i].getAttribute("events"));
				
				var okToAdd = false;
				
				// filter markers if required
		/*			
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
		*/					
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
//					}
				
					// add marker
					if(okToAdd == true) {
						
						createMarker(latlng, info, venueName);
					}

				}//end of for (build group of markers)
			
			$("#map").empty();
			$("#map").append(map);
			
			mapData = data;
		}// end of if(updateHeader == true)
		
	});	//end of $.get()
	return mapData;
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
	case 'a':
		return new google.maps.LatLng(-25.947028, 133.209639); // Aus only		
	default:
		return new google.maps.LatLng(-25.947028, 133.209639); // default, AUS		
	}
}

//build a single marker
function createMarker(latlng, info, venueName) {
	
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
