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
var MARKER_BASE_URL = "http://localhost:8181/mapping2/markers?";

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
		}
	});	
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
