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
			var url = "http://beta.ausstage.edu.au/mapping2/search?task="+task+"&type=name&format=json&query="+encodeURIComponent($('#query').val());

			$("#search_waiting").show(); $("#search_results").hide(); $("#error_message").hide();
			
			$.jsonp({
				url:url+'&callback=?',
				error: showErrorMessage,
				success: function(data){
					showSearchResults(data);
				}	
			});
		}
	});
	
	//when task is selected, alter the fields and available values 
	$("#task").change(function(){
		
		//clear the fields 
		$("#id").val('');
		$("#name").val('');		
		switch ($("#task").val()) {
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
				//change the label on the search field
				$("#search_name_label").empty().append("Event Name: ");		
				
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
				//change the label on the search field
				$("#search_name_label").empty().append("Contributor Name: ");		
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
		var url = "http://beta.ausstage.edu.au/mapping2/search?task="+task+"&type=id&format=json&query="

		// get the id from the text box
		var id = $("#id").val();
	
		if(id.length > 0) {
	
			// complete the url
			url += id;
		
			// lookup the id
			//$.get(url, function(data, textStatus, XMLHttpRequest) {
			$.jsonp({
				url:url+'&callback=?',
				success: function(data){
				// check on what was returned
					if(data.length==0){
						switch (task){
						case 'contributor':
							$("#name").val("Contributor with that id was not found");
							break;
						case 'event':
							$("#name").val("Event with that id was not found");
							break;
						}
						
					} else {
						// use the name to fill in the text box
						switch (task){
							case 'contributor':
								$("#name").val(data[0].firstName+' '+data[0].lastName);
								break;
							case 'event':
								$("#name").val(data[0].name);
								break;
						}
						// enable the button
						$("#export_btn").button("enable");
					}
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
			$("#query").val('');
			$("#search_results_body").empty();
			$("#search_results_evt").hide();			
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
function showSearchResults(responseText)  {
	console.log(responseText);
	//define helper constants
	var MAX_FUNCTIONS = 3;

	// tidy up the search results
	$("#search_results_body").empty();
	$("#search_results").hide();
	$("#search_results_body_evt").empty();
	$("#search_results_evt").hide();

	var html = "";
	
	if (task == 'contributor'){
	
		var contributor;
		var functions;
	
		// loop through the search results
		for(i = 0; i < responseText.length; i++){
		
			contributor = responseText[i];
		
			// add the name and link
			html += '<tr><td><a href="' + contributor.url + '" target="ausstage" title="View the record for ' + 
			contributor.firstName +' ' +contributor.lastName/*contributor.name*/ + ' in AusStage">' + contributor.firstName +' ' +contributor.lastName + '</a></td>';
		
			// add the event dates
			html += '<td>' + contributor.eventDates + '</td>';
		
			// add the list of functions
			html += '<td>';
		
			functions = contributor.functions;
			var space = '';
			for(x = 0; x < functions.length; x++) {
					html += space + functions[x];
					space = ', ';
			}
		
			html += '</td>';
			
			// add the button
			html += '<td><button id="choose_' + contributor.id + '" class="choose_button">Choose</button></td>';
		
			// finish the row
			html += '</tr>';
		}
	}else {
//////
		var event;
	
		// loop through the search results
		for(i = 0; i < responseText.length; i++){
		
			event = responseText[i];
		
			// add the name and link
			html += '<tr><td><a href="' + event.url + '" target="ausstage" title="View the record for ' + 
			event.name + ' in AusStage">' + event.name + '</a></td>';
		
			// add the venue info
			html += '<td>' + event.venue.name;
			if (event.venue.suburb != null){
				html += ', '+event.venue.suburb;	
			}
			if (event.venue.country == 'Australia'){
				html += ', '+event.venue.state;
			}else {
				html += ', '+event.venue.country;
			}
			html += '</td>';
		
			// add the event dates
			html += '<td>'+event.firstDisplayDate +'</td>';
			
			// add the button
			html += '<td><button id="choose_' + event.id + '" class="choose_button">Choose</button></td>';
		
			// finish the row
			html += '</tr>';
		}
//////		
	}
	// check to see on what was built
	if(html != "") {
		if (task == 'contributor'){
			// add the search results to the table
			$("#search_results_body").append(html);
		} else $("#search_results_body_evt").append(html); 
	
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
	
		if (task == 'contributor'){
		// show the search results
			$("#search_results").show();
		}
		else $("#search_results_evt").show(); 
		
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
