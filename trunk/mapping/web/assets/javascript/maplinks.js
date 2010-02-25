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

// determine the type of map and adjust function calls accordingly
$(document).ready(function(){

	// get the map type
	var type = $.getUrlVar("type");
	var id   = $.getUrlVar("id");
	
	// check on parameters
	if(typeof(type) == "undefined" || typeof(id) == "undefined") {
		showErrorMessage();
	} else {
	
		// determine what to do
		if(type == "org") {
			// this is an organisation map so get the organisation name
			$.get("data?action=lookup&type=orgname&id=" + id, function(html) {
				$("#map_name").empty();
				$("#map_name").append("Map events of events for: " + html);
				
				// show the map
				showOrgMap(id);
			});
			
		} else {
			// unknown type parameter
			showErrorMessage();
		}
	}
	
});

//// function to show error when Ajax fails
$(document).ready(function(){

	jQuery().ajaxError(function(a, b, e) {
			showErrorMessage();
		});
});

// function to show an error message
function showErrorMessage() {

	$("#map_name").hide();
	$("#map_header").hide();
	$("#map_legend").hide();
	$("#map").append("<p><strong>An Error has occured.</strong><br/>");
	$("#map").append("Check the URL and try again. If the problem persists contact the site administrator.</p>");
}

// override the advanced map display functionality with our own function
$(document).ready(function() {

	$("#reload_map").click(advFormSubmit);
});

// function to load and show the map
function showOrgMap(orgId) {
	
	// show the map container
	$("#map_header").show();
	$("#map").show();
	$("#map_legend").show();
	$("#map_footer").show();
	
	// update values in the advanced map options form
	$("#adv_map_org_id").val(orgId);
	
	// use reusable function to show and load the map
	showMap('org', orgId);
	
	// get the data to populate the selectboxes
	$.getJSON("data?action=lookup&type=startdates&id=" + orgId, function(data) {
	
		// remove any existing options
		$("#event_start").removeOption(/./);
		$("#event_finish").removeOption(/./);
		
		// add the new options
		$("#event_start").addOption(data, false);
		$("#event_finish").addOption(data, false);
		
		// clear any current selected values
		$("#event_start").selectOptions('clear');
		$("#event_finish").selectOptions('clear');
		
		// select the last option of the event_finish select
		$("#event_start option:last").attr("selected", "selected");
		//$("#event_finish").val($("#event_finish option:last").val());
		
		// remove any existing slider
		$("#sliderComponent").remove();
		
		// create the slider
		//$(".slider").selectToUISlider({labels: 7})
		$(".slider").selectToUISlider({labels: 10}).hide();
		$(".tohide").hide();
	});
	
	return false;
}


// function to take action when the advanced map options form is submitted
function advFormSubmit() {

	// get the org id
	var orgId = $("#adv_map_org_id").val();
	
	// get the show trajectory option
	var showTraj = $("#show_trajectory:checked").val();
	
	// get the start date
	var startDate = $("#event_start").val();
	var finishDate = $("#event_finish").val();
	
	// use the reusable function to update the map
	if(showTraj != null) {
		showMap('org', orgId, "true", startDate, finishDate);
	} else {
		showMap('org', orgId, "false", startDate, finishDate);
	}
	
	// override default action on the form
	return false;
}
