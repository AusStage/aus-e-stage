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
var FEEDBACK_BASE_URL = "/mobile/feedback?";
var UPDATE_DELAY = 5000;
var feedback_count = 1;
var item = null;
var performance = null;
var updating = false;

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

// get the initial batch of data
$(document).ready(function() {

	// hide required elements
	$("#error_message").hide();
	$("#table_anchor").hide();	

	// get the parameters
	performance = $.getUrlVar("performance");
	
	/*
	 * check on the value of the parameters. If they're "undefined" the URL didn't include what we require to continue
	 */
	if(typeof(performance) == "undefined") {
		// execute a function to show an error message
		showMissingParameterMessage();
	} else {
			
		// build the url
		var url = FEEDBACK_BASE_URL + "performance=" + performance + "&task=initial";
		
		// get the intial batch of data
		$.get(url, function(data, textStatus, XMLHttpRequest) {
		
			// process the batch of data
			if(data.name == "" || typeof(performance) == "undefined") {
				showMissingDataMessage();
			} else {
				// add the performance details
				$("#performance_name").empty();
				$("#performance_name").append('Live Feedback for: <a href="' + data.eventUrl + '" title="View record for ' + data.event + ' in AusStage" target="ausstage">' + data.event+ "</a>, " + data.date);
				
				$("#performance_by").empty();
				$("#performance_by").append('Peformance by: <a href="' + data.organisationUrl + '" title="View record for ' + data.organisation + ' in AusStage" target="ausstage">' + data.organisation + '</a> at <a href="' + data.venueUrl + '" title="View record for ' + data.venue + ' in AusStage" target="ausstage">' + data.venue + '</a>');
				
				$("#performance_question").empty();
				$("#performance_question").append("In response to the question: " + data.question);
				
				// add the list of feedback
				for(var i = 0; i < data.feedback.length; i++) {
					item = data.feedback[i];					
					$("#table_anchor").after('<tr><td class="feedback_messages_left">' + feedback_count + '</td><td class="feedback_messages_middle">' + item.content + '</td><td class="feedback_messages_right">' + item.type + '</td></tr>');
					feedback_count++;
				}
			}
			
			//set up the loop that will start the automatic updating process every x seconds
			setTimeout("updateFeedback()", UPDATE_DELAY);		
		});
	}
});

/*
 * A function to check for new feedback and update the page
 */
function updateFeedback() {

	// check to make sure there isn't an update happening already
	if(updating == false) {
		// continue with the update
		updating = true;
		
		// check to see if there is an item to use in building the url
		var url;
		if(item != null) {
			url = FEEDBACK_BASE_URL + "performance=" + performance + "&task=update&lastid=" + item.id;
		} else {
			url = FEEDBACK_BASE_URL + "performance=" + performance + "&task=update&lastid=0";
		}
		
		// get the new batch of data
		$.get(url, function(data, textStatus, XMLHttpRequest) {
		
			// "age" any existing new feedback
			$('td').removeClass("feedback_messages_new");
		
			// process the batch of data
			if(data.length != 0 || typeof(data) != "undefined") {
				
				// add the list of feedback
				for(var i = 0; i < data.length; i++) {
					item = data[i];					
					$("#table_anchor").after('<tr><td class="feedback_messages_left feedback_messages_new">' + feedback_count + '</td><td class="feedback_messages_middle feedback_messages_new">' + item.content + '</td><td class="feedback_messages_right feedback_messages_new">' + item.type + '</td></tr>');
					feedback_count++;
				}
			}
			
			// set the updating flag
			updating = false;
			
			// set an interval for the next update
			setTimeout("updateFeedback()", UPDATE_DELAY);
		});		
	}
}

/* 
 * A function to show an error message
 */
// function to show an error message
function showMissingParameterMessage() {

	$("#error_message").empty();
	$("#error_message").append('<div class="ui-state-error ui-corner-all" style="padding: 0 .7em;"><p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span><strong>Error:</strong> Missing performance parameter. Please adjust the URL and try again.</p></div>');
	$("#error_message").show();
}

function showMissingDataMessage() {

	$("#error_message").hide();
	$("#error_message").empty();
	$("#error_message").append('<div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;"><p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span><strong>Warning:</strong> An error occured during the request for feedback. <br/>Please try again and if the problem persists contact the site administrator.</p></div>');	
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
		$("#error_message").append('<div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;"><p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span><strong>Warning:</strong> An error occured during the request for feedback. <br/>Please try again and if the problem persists contact the site administrator.</p></div>');	
		$("#error_message").show();
	});
});
