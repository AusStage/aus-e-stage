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
var CONTRIBUTOR_IDS = [639,110,2610,1517,227361,308,1796,3202,1026,3872,1357,2925,938,2450,1356,225399,799,1805,1804,54,432,1856,2,952,388,149,2977,6417,4921,466,542,455,1002,2365,1514,8375,891,1746,4774,403];
 
/*
 * Do not edit below this line
 *
 * Very Bad things will happen 
 */
 
var contributorLink  = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&amp;f_contrib_id="

// load the appropriate map
$(document).ready(function() {

	// get the index
	var index = Math.random() * CONTRIBUTOR_IDS.length;
	index = Math.floor(index);
	
	id = CONTRIBUTOR_IDS[index];

	// get the contributors name
	$.get("data?action=lookup&type=contribname&id=" + id, function(html) {
		$("#map_name").empty();
		$("#map_name").append("Featured map of events for: <a href=\"" + contributorLink + id + "\" title=\"View record for " + html + " in AusStage\" target=\"ausstage\">" + html + "</a>");
	});
	
	// get the marker xml data
	$.get("data?action=markers&type=contributor&id=" + id, function(data) {
		
		// show the map
		showMap(data, null, null, null, null);
	});
	
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
