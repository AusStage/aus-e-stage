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
 
// function to get parameters from url
// taken from: http://jquery-howto.blogspot.com/2009/09/get-url-parameters-values-with-jquery.html
$.extend({
	getUrlVars: function(){
		var vars = [], hash;
		var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
		for(var i = 0; i < hashes.length; i++)
		{
			hash = hashes[i].split('=');
			vars.push(hash[0]);
			vars[hash[0]] = hash[1];
		}
		return vars;
	},
	getUrlVar: function(name){
		return $.getUrlVars()[name];
	}
});

// declare global variables
var mapData = null;

//declare constants
var CONTRIBUTOR_URL  = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_contrib_id=";
var ORGANISATION_URL = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_org_id=";

// load the appropriate map
$(document).ready(function() {

	// get the parameters to this page
	var type = $.getUrlVar("type");
	var id   = $.getUrlVar("id");
	
	// check on parameters
	if(typeof(type) == "undefined" || typeof(id) == "undefined") {
		showErrorMessage();
	} else {
		if(type == "organisation") {
			// this is an organisation map
			// this is an organisation map so get the organisation name
			$.get("data?action=lookup&type=orgname&id=" + id, function(html) {
				$("#map_name").empty();
				$("#map_name").append("Map events of events for: <a href=\"" + ORGANISATION_URL + id + "\" title=\"View record for " + html + " in AusStage\" target=\"ausstage\">" + html + "</a>");
			});
			
			// get the marker xml data
			$.get("data?action=markers&type=organisation&id=" + id, function(data) {
				
				// show the map
				showMap(data, null, null, null, null);
				
				// build the time slider
				buildTimeSlider(data);
				
				// store reference to marker data for reuse
				mapData = data;
			});
			
			// update the persistent link
			$("#map_header_link").attr("href", "maplinks.jsp?type=org&id=" + id);
			
		} else if(type == "contributor") {
			// this is a contributor map
			
			if(id.indexOf(',',0) != -1) {
				// multiple contributors
				$("#map_name").empty();
				$("#map_name").append("Map events of events for Multiple Contributors");
								
			} else {
				// get the contributors name
				$.get("data?action=lookup&type=contribname&id=" + id, function(html) {
					$("#map_name").empty();
					$("#map_name").append("Map events of events for: <a href=\"" + CONTRIBUTOR_URL + id + "\" title=\"View record for " + html + " in AusStage\" target=\"ausstage\">" + html + "</a>");
				});
			}
			
			// get the marker xml data
			$.get("data?action=markers&type=contributor&id=" + id, function(data) {
				
				// show the map
				showMap(data, null, null, null, null);
				
				// build the time slider
				buildTimeSlider(data);
				
				// store reference to marker data for reuse
				mapData = data;
			});
			
			// update the persistent link
			$("#map_header_link").attr("href", "maplinks.jsp?type=contrib&id=" + id);
		
		} else {
			// this is an unknown type
			showErrorMessage();
		}
		
		// override the default form action
		$("#reload_map").click(reloadMap);
		
		// hide the other two links
		$("#map_header_kml").hide();
		$("#map_header_export").hide();
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

// function to reload a map
function reloadMap() {
	
	// check to ensure map data is present
	if(mapData == null) {
		$("#map").empty();
		$("#map").append('<p style="text-align: center"><strong>Error: </strong>An error occured whilst loading markers, please start again.<br/>If the problem persists please contact the site administrator.</p>'); 
		return false;
	}

	// get the show trajectory option
	var showTraj = $("#show_trajectory:checked").val();
	
	// get the start date
	var startDate  = $("#event_start").val();
	var finishDate = $("#event_finish").val();
	
	// determine if the trajectory option is set
	if(showTraj != null) {
		// reload the map with trajectory information
		showMap(mapData, true, $("#state").val(), startDate, finishDate);
	} else {
		// reload the map with trajectory information
		showMap(mapData, false, $("#state").val(), startDate, finishDate);
	}
}
