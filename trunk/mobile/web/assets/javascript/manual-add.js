/*
 * This file is part of the AusStage Mobile Service
 *
 * The AusStage Mobile Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Mobile Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mobile Service.  
 * If not, see <http://www.gnu.org/licenses/>.
 */

// define global variables
var LOOKUP_BASE_URL = "/mobile/lookup?task=system-property";

// common theme actions
$(document).ready(function() {
	// style the buttons
	$("button, input:submit").button();
	
	// associate the tipsy library with the form elements
	$('#feedback [title]').tipsy({trigger: 'focus', gravity: 'w'});
});

// get the source types
$(document).ready(function() {

	// build the url
	var url = LOOKUP_BASE_URL + "&id=feedback-source-types";
	
	// get the source types
	$.get(url, function(data, textStatus, XMLHttpRequest) {
	
		// check on what was returned
		if(typeof(data) == "undefined" || data.length == 0) {
			showMissingDataMessage();
		} else {
			
			// loop through the list of source types
			for(var i = 0; i < data.length; i++) {
		
				// populate the list of source types
				$("#source_type").addOption(data[i].id, data[i].name + " - " + data[i].description);
			}
			
			$("#source_type").selectOptions("1");
		}	
	});

});

// attach validation to the form
$(document).ready(function() {

	// attach the validation plugin to the export form
	$("#feedback").validate({
		rules: { // validation rules
			performance: {
				required: true,
				digits: true,
				remote: {
					url: "/mobile/lookup",
					data: {
						task: "validate"
					}
				}
			},
			date: {
				required: true
			},
			time: {
				required: true
			},
			from: {
				required: true,
				minlength: 64,
				maxlength: 64
			},
			source_id: {
				minlength: 64,
				maxlength: 64
			},
			content: {
				required: true
			}	
		},
		messages: {
			performance: {
				remote: "The perfomance ID is invalid"
			}
		}	
	});
});

/* 
 * A function to show an error message
 */
function showMissingDataMessage() {

	$("#error_message").hide();
	$("#error_message").empty();
	$("#error_message").append('<div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;"><p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span><strong>Warning:</strong> An error occured during a request for data. <br/>Please try again and if the problem persists contact the site administrator.</p></div>');	
	$("#error_message").show();
}

/* 
 * Define an Ajax Error handler if something bad happens when we make an Ajax call
 * more information here: http://api.jquery.com/ajaxError/
 */
$(document).ready(function() {

	// getting marker xml for contributors
	$("#error_message").ajaxError(function(e, xhr, settings, exception) {
		// hide certain elements
		$("#error_message").hide();
		$("#error_message").empty();
		$("#error_message").append('<div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;"><p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span><strong>Warning:</strong> An error occured during the request for data. <br/>Please try again and if the problem persists contact the site administrator.</p></div>');	
		$("#error_message").show();
	});
});
