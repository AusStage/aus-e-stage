/*
 * This file is part of the AusStage Navigating Networks Service
 *
 * The AusStage Navigating Networks Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Navigating Networks Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/
var task = "contributor";

// common theme actions
$(document).ready(function() {
	// style the buttons
	$("button, input:submit").button();
	
	//setup the tabs
	$("#tabs").tabs();
});

// determine if we need to show the OS specific warning
$(document).ready(function() {
	// do our best to determine the OS type
	if(BrowserDetect.OS == "Mac") {
		// show the Apple Mac specific warning if required
		if($("#format").val() == "graphml") {
			$("#mac_warning").show();
		}
	} else {
		// hide the Apple Mac warning
		$("#mac_warning").hide();
	}
	
	// add a change event handler to the format select box
	$("#format").change(function() {
		if(BrowserDetect.OS == "Mac") {
			// show the Apple Mac specific warning if required
			if($("#format").val() == "graphml") {
				$("#mac_warning").show();
			} else {
				// hide the Apple Mac warning
				$("#mac_warning").hide();
			}
		}
	});
});


// populate the select boxes in the form
$(document).ready(function() {
	//populate the select boxes
	$("#task").addOption('ego-centric-network-simple', 'Contributor Network');	
	$("#task").addOption('event-centric-network', 'Event Network');	
	
	$("#format").addOption('graphml', 'graphml');
	$("#format").addOption('debug', 'debug');	
	
	$("#radius").addOption('1', '1');	
	$("#radius").addOption('2', '2');	
	$("#radius").addOption('3', '3');	
	
	$("#simplify").addOption('true', 'true');
	$("#simplify").addOption('false', 'false');	
			
	// sort the options & select appropriate defaults
	$("#task").selectOptions("ego-centric-network-simple");
	$("#format").selectOptions("graphml");
	$("#radius").sortOptions();
	$("#radius").selectOptions("1");
	$("#simplify").selectOptions('true');
	$("#simplify_container").hide();
	
	// bind a focusout event to the id text field
	$("#id").focusout(function() {

		// get the content of the id text box	
		var val = $("#id").val();
		
		// trim the value
		val = val.replace(/^\s*/, "").replace(/\s*$/, "");

		// see if the value is an empty string
		if(val == "") {
			// if it is tidy the form
			$("#name").val("");
			$("#id").val("");
			
			// disable the export button
			$("#export_btn").button("disable");
		}
	});
	
	// disable the export button
	$("#export_btn").button("disable");
	
	// empty the text boxes
	$("#name").val("");
	$("#id").val("");
	

	// attach the validation plugin to the export form
	$("#export_data").validate({
		rules: { // validation rules
			id: {
				required: true,
				digits: true
			}
		}
	});
	
	// attach the validation plugin to the name search form
	$("#search_form").validate({
		rules: { // validation rules
			name: {
				required: true
			}
		},
		submitHandler: function(form) {
			jQuery(form).ajaxSubmit({
				beforeSubmit: function() {$("#search_waiting").show(); $("#search_results").hide(); $("#error_message").hide();},
				success:      showSearchResults,
				error:        showErrorMessage
			});
		}
	});
	
	//when task is selected, alter the fields and available values 
	$("#task").change(function(){
		var task = $("#task").val();
		
		switch (task) {
			case 'event-centric-network':
				//change the export type
				task = "event";
				//change the labels
				$("#id_label").empty().append('Event ID:');
				$("#name_label").empty().append('Event Name:');	
				//remove a radius option
				$("#radius").removeOption('3');
				//show the simplify option
				$("#simplify_container").show();
			break;	
			case 'ego-centric-network-simple':
				//change the export type
				task = "contributor";
				//change the labels
				$("#id_label").empty().append('Contributor ID:');
				$("#name_label").empty().append('Contributor Name:');				
				//add the radius
				$("#radius").addOption('3', '3');				
				//hide the simplify options
				$("#simplify_container").hide();				
			break;	
		}
		//set default radius
		$("#radius").selectOptions("1");
	})
	
	
	// define the lookup function
	$("#lookup_btn").click(function () {

		// disable the export button
		$("#export_btn").button("disable");
	
		// define helper variables
		var url = "/mapping2/search?task="+task+"&type=id&format=json&id="
//		var url = "/networks/lookup?task=collaborator&format=json&id=";

		// get the id from the text box
		var id = $("#id").val();
	
		if(id.length > 0) {
	
			// complete the url
			url += id;
		
			// lookup the id
			$.get(url, function(data, textStatus, XMLHttpRequest) {
			
				// check on what was returned
				if(data.length()==0){
//				if(data.name == "No Collaborator Found") {
					$("#name").val("Contributor with that id was not found");
				} else {
			
					// use the name to fill in the text box
//					$("#name").val(data.name);
					$("#name").val(data.firstName+' '+data.lastName);
			
					// enable the button
					$("#export_btn").button("enable");
				}
			});
		} else {
			// show the search form
			$("#search_div").dialog('open');
		}
		
		return false;
	});
	
	// setup the dialog box
	$("#search_div").dialog({ 
		autoOpen: false,
		height: 500,
		width: 650,
		modal: true,
		buttons: {
			'Search': function() {
				// submit the form
				$('#search_form').submit();
			},
			Cancel: function() {
				$(this).dialog('close');
			}
		},
		open: function() {
			// tidy up the form on opening
			$("#search_results_body").empty();
			$("#search_results").hide();
			$("#error_message").hide();
			showLoader("hide");
		},
		close: function() {
			// tidy up the form on close
			$("#search_results_body").empty();
			$("#search_results").hide();
		}
	});

});

/** form processing functions **/
// function to show the moving bar
// functions for showing and hiding the loading message
function showLoader(type) {

	if(type == "show" || typeof(type) == "undefined") {
		// tidy up the search results
		$("#search_results_body").empty();
		$("#search_results").hide();
	
		//show the loading message
		$("#search_waiting").hide();
	} else {
		// hide the loading message
		$("#search_waiting").hide();
	}
	
}

// function to show the search results
function showSearchResults(responseText, statusText)  {
	
	//define helper constants
	var MAX_FUNCTIONS = 3;

	// tidy up the search results
	$("#search_results_body").empty();
	$("#search_results").hide();
	
	var html = "";
	var contributor;
	var functions;
	
	// loop through the search results
	for(i = 0; i < responseText.length; i++){
		
		contributor = responseText[i];
		
		// add the name and link
		html += '<tr><td><a href="' + contributor.url + '" target="ausstage" title="View the record for ' + 
		contributor.firstName +' ' +contributor.lastName/*contributor.name*/ + ' in AusStage">' + contributor.firstName +' ' +contributor.lastName + '</a></td>';
		
		// add the list of functions
		html += '<td><ul>';
		
		functions = contributor.functions;
		
		for(x = 0; x < functions.length; x++) {
			if(x < MAX_FUNCTIONS) {
				html += '<li>' + functions[x] + '</li>';
			} else {
				html += '<li>...</li>';
				x = functions.length + 1;
			}
		}
		
		html += '</ul></td>';
		
		// add the contributor count
		html += '<td>' + contributor.collaborations + '</td>';
		
		// add the button
		html += '<td><button id="choose_' + contributor.id + '" class="choose_button">Choose</button></td>';
		
		// finish the row
		html += '</tr>';
	}
	
	// check to see on what was built
	if(html != "") {
	
		// add the search results to the table
		$("#search_results_body").append(html);
	
		// hide the loader
		showLoader("hide");
	
		// style the new buttons
		$("button, input:submit").button();
		
		// add a function to each of the choose buttons
		$(".choose_button").click(function(eventObject) {

			// get the id of this button
			var id = this.id;
			
			var tags = id.split("_");
			
			// add the id to the text file
			$("#id").val(tags[1]);
			
			// close the dialog box
			$("#search_div").dialog("close");
			
			// execute the lookup function
			$("#lookup_btn").trigger('click');
		});
	
		// show the search results
		$("#search_results").show();
		
	} else {
		
		// hide the loader
		showLoader("hide");
	
		$("#error_message").empty();
		$("#error_message").append('<div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;"><p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span><strong>Warning:</strong> No contributors matched your search criteria. Please try again</p></div>');	
		$("#error_message").show();
	}
}

// function to show a generic error message
function showErrorMessage() {

	// tidy up the search results
	$("#search_results_body").empty();
	$("#search_results").hide();
	
	// hide the loader
	showLoader("hide");
	
	// show an error message
	$("#error_message").empty();
	$("#error_message").append('<div class="ui-state-error ui-corner-all" style="padding: 0 .7em;"><p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span><strong>Error:</strong> An error occured while processing this request. Please try again. <br/>If the problem persists please contact the site administrator.</p></div>');
	$("#error_message").show();
}
