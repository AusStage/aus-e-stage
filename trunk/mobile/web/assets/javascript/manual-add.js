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

// define custom validation methods
$(document).ready(function() {

	// custom validation of dates
	jQuery.validator.addMethod("validDate", function(value, element) {

		var months = ["jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"];

		// check to see if there is three elements
		var elements = value.split("-");
	
		if(elements.length != 3) {
			return false;
		}
	
		// check on the individual elements
		if(elements[0] < 0 || elements[0] > 32) {
			return false;
		}
	
		// check on the month
		elements[1] = elements[1].toLowerCase();
		var found = false;
	
		for( var i = 0; i < months.length; i++) {
			if(months[i] == elements[1]) {
				found = true;
			}
		}
	
		if(found != true) {
			return false;
		}	
	
		// check on the year
		if(elements[2] < 2010) {
			return false;
		}
	
		// if we get this far everything is OK
		return true;
	} , "The date is invalid");
	
	// custom validation of dates
	jQuery.validator.addMethod("validTime", function(value, element) {

		// check to see if there is three elements
		var elements = value.split(":");
	
		if(elements.length != 3) {
			return false;
		}
	
		// check on the hour
		if(elements[0] < 0 || elements[0] > 23) {
			return false;
		}
		
		// check the minute
		if(elements[1] < 0 || elements[1] > 59) {
			return false;
		}
		
		// check the second
		if(elements[2] < 0 || elements[2] > 59) {
			return false;
		}
			
		// if we get this far everything is OK
		return true;
	} , "The time is invalid");
});

// attach validation and masked input to the form
$(document).ready(function() {

	// attach the validation plugin to the form
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
			question: {
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
				required: true,
				validDate: true
			},
			time: {
				required: true,
				validTime: true
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
			},
			question: {
				remote: "The question ID is invalid"
			}
		},
		submitHandler: function(form) {
			jQuery(form).ajaxSubmit({ 
				beforeSubmit: function() {$("#error_message").hide(); },
				clearForm:    true,
				success:      showSuccess,
				error:        showError
			});
		}
	});
	
	// attached the masked input plugin to the form
	$("#date").mask("99-aaa-9999");
	$("#time").mask("99:99:99");
});

// attach validation and masked input to the form
$(document).ready(function() {

	$("#decode").validate({
		rules: { // validation rules
			hexstring: {
				required: true,
			}
		},
		submitHandler: function(form) {
			// get the hex string
			var hexString = $("#hexstring").val();
			
			// trim the whitespace from the hexstring
			hexString = hexString.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
			
			// decode and turn it into the character string
			var decodeString = "";
			
			for(i = 0; i < hexString.length; i+=2) {
				decodeString += String.fromCharCode(parseInt(hexString.substring(i, i+2), 16));
			}
			
			$("#charstring").val(decodeString);
		}
	});
});

/* 
 * A function to show an error message
 */
function showSuccess(responseText, statusText, xhr, $form)  { 

	if(responseText.status == "success") {
		$("#error_message").hide();
		$("#error_message").empty();
		$("#error_message").append('<div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;"><p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span><strong>Success:</strong> The feedback has been saved successfully</p></div>');	
		$("#error_message").show();
	} else {
		$("#error_message").hide();
		$("#error_message").empty();
		$("#error_message").append('<div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;"><p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span><strong>Warning:</strong> An error occured during a request to save feedback. <br/>Please try again and if the problem persists contact the site administrator.</p></div>');	
		$("#error_message").show();
	}
} 

/* 
 * A function to show an error message
 */
function showError() {

	$("#error_message").hide();
	$("#error_message").empty();
	$("#error_message").append('<div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;"><p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span><strong>Warning:</strong> An error occured during a request to save feedback. <br/>Please try again and if the problem persists contact the site administrator.</p></div>');	
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
