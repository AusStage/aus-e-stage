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

// common theme actions
$(document).ready(function() {
	// style the buttons
	$("button, input:submit").button();
});


// populate the select boxes in the form
$(document).ready(function() {

	// define helper variables
	var url = "/networks/lookup?task=system-property&id=export-options";

	// get the data
	$.get(url, function(data, textStatus, XMLHttpRequest) {
	
		var list = data.tasks;
		
		for(var i = 0; i < list.length; i++) {
			$("#task").addOption(list[i], list[i]);
		}
		
		var list = data.formats;
		
		for(var i = 0; i < list.length; i++) {
			$("#format").addOption(list[i], list[i]);
		}
		
		var list = data.radius;
		
		for(var i = 0; i < list.length; i++) {
			$("#radius").addOption(list[i], list[i]);
		}
		
		// sort the options & select appropriate defaults
		$("#task").selectOptions("simple-network-undirected");
		$("#format").selectOptions("graphml");
		$("#radius").sortOptions();
		$("#radius").selectOptions("1");
	
	});
	
	// disable the export button
	$("#export_btn").button("disable");
	
	// empty the text boxes
	$("#name").val("");
	$("#id").val("");
	
	// associate the tipsy library with the form elements
	$('#export_data [title]').tipsy({trigger: 'focus', gravity: 'w'});

	// attach the validation plugin to the id search form
	$("#export_data").validate({
		rules: { // validation rules
			id: {
				required: true,
				digits: true
			}
		}
	});
	
	// define the lookup function
	$("#lookup_btn").click(function () {
	
		// disable the export button
		$("#export_btn").button("disable");
	
		// define helper variables
		var url = "/networks/lookup?task=collaborator&format=json&id=";

		// get the id from the text box
		var id = $("#id").val();
	
		if(id.length > 0) {
	
			// complete the url
			url += id;
		
			// lookup the id
			$.get(url, function(data, textStatus, XMLHttpRequest) {
			
				// check on what was returned
				if(data.name == "No Collaborator Found") {
					$("#name").val("Contributor with that id was not found");
				} else {
			
					// use the name to fill in the text box
					$("#name").val(data.name);
			
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
		height: 400,
		width: 550,
		modal: true,
		buttons: {
			'Search': function() {
				// TODO replace with a real search function
				alert("Search Button Clicked");
			},
			Cancel: function() {
				$(this).dialog('close');
			}
		},
		open: function() {
			// clean up the search results table
			$("#search_results_body").empty();
			//$("#search_results").hide();
		},
		close: function() {
			//TODO tidy up the form
			$("#search_results_body").empty();
			//$("#search_results").hide();
		}
	});

});
