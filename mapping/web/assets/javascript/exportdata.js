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
	} else if (type == "" || id == "") {
		showErrorMessage();	
	} else {
	
		// determine what to do
		if(type == "organisation") {
			// this is an organisation map so get the organisation name
			$.get("data?action=lookup&type=orgname&id=" + id, function(html) {
				$("#export_name").empty();
				$("#export_name").append("Download Map Data on: " + html);
				$("#export_type").val(type);
				$("#export_id").val(id);
			});
			
		} else if(type == "contrib") {
			// this is a contributor map
			if(id.indexOf(',',0) == -1) {
				// single contributor
				$.get("data?action=lookup&type=contribname&id=" + id, function(html) {
					$("#export_name").empty();
					$("#export_name").append("Download Map Data on: " + html);
					$("#export_type").val(type);
					$("#export_id").val(id);
				});
			} else {
				// multiple contributors
				$("#export_name").empty();
				$("#export_name").append("Download Map Data on multiple contributors");
				$("#export_type").val(type);
				$("#export_id").val(id);
			}
					
		} else {
			// unknown type parameter
			showErrorMessage();
		}
	}
	
});

//// function to show error when Ajax failes
$(document).ready(function(){

	jQuery().ajaxError(function(a, b, e) {
			showErrorMessage();
		});
});

// function to show an error message
function showErrorMessage() {

	$("#export_name").hide();
	$("#export_form_div").empty();
	$("#export_form_div").append("<p><strong>An Error has occured.</strong><br/>");
	$("#export_form_div").append("Check the URL and try again. If the problem persists contact the site administrator.</p>");
}

// function to show more info on form options
function showMoreInfo(section) {

	// show this specific one
	$("#more_info_" + section).dialog("open");

}

// hide all of the more info on form options sections
$(document).ready(function(){

	// set common options
	var options = {autoOpen: false, bgiframe: true, height: 200, width: 700, modal: true, title: "More Information",  buttons: { "Close": function() { $(this).dialog("close"); }}};
	
	// setup the dialogs
	$("#more_info_1").dialog(options);
	$("#more_info_2").dialog(options);
	$("#more_info_3").dialog(options);
});
