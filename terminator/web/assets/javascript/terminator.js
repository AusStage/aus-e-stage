/*
 * This file is part of the AusStage Terminator Service
 *
 * The AusStage Terminator Service is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage Terminator Service is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Terminator Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

// do stuff once the page is ready
$(document).ready(function() {
	// attach the validation library
	$("#authenticate").validate({
		rules: { // validation rules
			auth_token: {
				required: true
			}
		},
		submitHandler: function(form) {
			jQuery(form).ajaxSubmit({
				beforeSubmit: showAuthStatus,
				success:      showScriptList,
				error:        showAuthError
			});
		}
	});
	
	// hide unnecessary divs
	$(".hidden").hide();
});

// ajax related functions
function showAuthStatus() {
	$("#status").hide();
	$("#status").empty();
	$("#status").append("<p>Attempting authentication, please wait...</p>");
	$("#status").show();
}

function showScriptList(responseText, statusText) {
	
	// show the list of scripts that can be executed
	$("#status").hide();
	$("#status").empty();
	$("#auth_form").hide();
	
	$("#script_list").empty();
	$("#script_list").append(responseText);
	$("#script_list").show();
}

function showAuthError() {
	$("#status").hide();
	$("#status").empty();
	$("#status").append('<p class="error">Authentication failed, please try again. <br/>If the problem persists contact the system adminstrator</p>');
	$("#status").show();
}


