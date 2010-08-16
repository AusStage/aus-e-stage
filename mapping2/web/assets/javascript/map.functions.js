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
function getMapData(type, id, updateHeader) {

	// declare helper variables
	var url;
	
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
			
			// create a new map and centre it on australia
			var latlng = new google.maps.LatLng(-25.947028, 133.209639);
		    var myOptions = {
		      zoom: 4,
		      center: latlng,
		      mapTypeId: google.maps.MapTypeId.ROADMAP
		    };

			var map = new google.maps.Map(document.getElementById("map"), myOptions);
			$("#map").empty();
			$("#map").append(map);
			
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
				
				venueName = venueName + ", " + venueSuburb
				
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
				
				var okToAdd = true;
				
/*				// filter markers if required
			
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
*/				
					// add marker
					if(okToAdd == true) {
						var marker = new google.maps.Marker({  
							   position: latlng,  
							   map: map,
							   title: venueName
							 });
					}

				}//end of for (group of markers)
			
		}// end of if(updateHeader == true)
	});	//end of $.get()
}

//build a single marker
function createMarker(latlng, info, colour, venueName, map) {
	
	// make a new icon
	var newIcon = MapIconMaker.createMarkerIcon({width: 32, height: 32, primaryColor: colour});
	
	// make a new marker
	var marker = new GMarker(latlng, {icon: newIcon, title: venueName});
	
	// add an event listener to listen for a click and show the InfoWindow
	GEvent.addListener(marker, 'click', function() {
		marker.openInfoWindowHtml(info, {maxWidth:520, maxHeight:400, autoScroll:true});
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
