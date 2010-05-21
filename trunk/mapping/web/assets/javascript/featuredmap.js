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

// declare variables to determine what type of map to build
var type = "contributor"; // valid values are contributor and organisation
var id   = "2";          // id of the contributor / organisation to feature
 
/*
 * Do not edit below this line
 *
 * Very Bad things will happen 
 */
 
var organisationLink = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&amp;f_org_id=";
var contributorLink  = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&amp;f_contrib_id="

// load the appropriate map
$(document).ready(function() {
	
	// check on parameters
	if(typeof(type) == "undefined" || typeof(id) == "undefined") {
		showErrorMessage();
	} else {
		if(type == "organisation") {
			// this is an organisation map
			// this is an organisation map so get the organisation name
			$.get("data?action=lookup&type=orgname&id=" + id, function(html) {
				$("#map_name").empty();
				$("#map_name").append("Featured map of events for: <a href=\"" + organisationLink + id + "\" title=\"View record for " + html + " in AusStage\">" + html + "</a>");
			});
			
			// get the marker xml data
			$.get("data?action=markers&type=organisation&id=" + id, function(data) {
				
				// show the map
				showMap(data, null, null, null, null);
			});
			
		} else if(type == "contributor") {
			// this is a contributor map
			
			// get the contributors name
			$.get("data?action=lookup&type=contribname&id=" + id, function(html) {
				$("#map_name").empty();
				$("#map_name").append("Featured map of events for: <a href=\"" + contributorLink + id + "\" title=\"View record for " + html + " in AusStage\">" + html + "</a>");
			});
			
			// get the marker xml data
			$.get("data?action=markers&type=contributor&id=" + id, function(data) {
				
				// show the map
				showMap(data, null, null, null, null);
			});
		
		} else {
			// this is an unknown type
			showErrorMessage();
		}
	}
});

// register ajax error handlers
$(document).ready(function() {

	// getting marker xml for contributors
	$("#map").ajaxError(function(e, xhr, settings, exception) {
		showErrorMessage();
	});
});

// function to show an error message
function showErrorMessage() {

	$("#map_name").hide();
	$("#map_header").hide();
	$("#map_legend").hide();
	$("#map").append('<p style="text-align: center"><strong>Error: </strong>An error occured whilst loading markers, please try again.<br/>If the problem persists please contact the site administrator.</p>'); 
}
