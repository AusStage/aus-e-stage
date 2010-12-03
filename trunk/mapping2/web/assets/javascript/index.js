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


 
// show / hide the menu
$(document).ready(function(){

	// initialise global objects
	searchObj  = new SearchClass();
	browseObj  = new BrowseClass();
	mappingObj = new MappingClass();
	
	mappingObj.updateMap();

	/*
	 * page setup
	 */

	// hide the messages div
	$("#messages").hide();
	$("#status_message").hide();
	$("#error_message").hide();
	
	// prevent a FOUC
	$('html').removeClass('js');
	
	// setup the tabs
	$("#tabs").tabs({'show': function(event, ui) {
		$(ui.panel).attr('style', 'width:100%; height:100%');
		google.maps.event.trigger(mappingObj.map, 'resize');
		return true;
	}});
	
	// style the buttons
	styleButtons();
	
	/*
	 * search functionality
	 */
	
	// associate tipsy with the span element
	$('.use-tipsy').tipsy({live: true});
	
	// setup the accordian
	$(".accordion").accordion({collapsible:true, active:false, autoHeight: false });
	
	// setup one of the help dialogs
	$("#help_search_div").dialog({ 
		autoOpen: false,
		height: 400,
		width: 450,
		modal: true,
		buttons: {
			Close: function() {
				$(this).dialog('close');
			}
		},
		open: function() {
			
		},
		close: function() {
			
		}
	});
	
	$("#show_search_help").click(function() {
		$("#help_search_div").dialog('open');
	});
	
	// setup a handler for the start of an ajax request
	$("#message_text").ajaxSend(function(e, xhr, settings) {
		// determine what type of request has been made & update the message text accordingly
		// ensure that we're only working on searches
		if(settings.url.indexOf("search?", 0) != -1) {
			if(settings.url.indexOf("contributor", 0) != -1) {
				$(this).text("Contributor search is underway...");
			} else if(settings.url.indexOf("organisation", 0) != -1) {
				$(this).text("Organisation search is underway...");
			} else if(settings.url.indexOf("venue", 0) != -1) {
				$(this).text("Venue search is underway...");
			} else if(settings.url.indexOf("event", 0) != -1) {
				$(this).text("Event search is underway...");
			}
		}
	});
	
	// set up handler for when an ajax request results in an error for searching
	$("#error_text").ajaxError(function(e, xhr, settings, exception) {
		// determine what type of request has been made & update the message text accordingly
		// ensure that we're only working on searches
		if(settings.url.indexOf("search?", 0) != -1) {
			if(searchObj.error_condition == false) {
				if(settings.url.indexOf("contributor", 0) != -1) {
					$(this).text(AJAX_ERROR_MSG.replace('-', 'the contributor search'));
				} else if(settings.url.indexOf("organisation", 0) != -1) {
					$(this).text(AJAX_ERROR_MSG.replace('-', 'the organisation search'));
				} else if(settings.url.indexOf("venue", 0) != -1) {
					$(this).text(AJAX_ERROR_MSG.replace('-', 'the venue search'));
				} else if(settings.url.indexOf("event", 0) != -1) {
					$(this).text(AJAX_ERROR_MSG.replace('-', 'the event search'));
					searchObj.searching_underway_flag = false;
				}
				searchObj.error_condition = true;
			} else {
				$(this).text(AJAX_ERROR_MSG.replace('-', 'multiple searches'));
				searchObj.searching_underway_flag = false;
			}
			
			// show the error message
			$("#error_message").show();
		}
	});
	
	// setup a handler for when the user clicks on a button to add search results to the map
	$('.selectSearchAll').live('click', searchObj.selectAllClickEvent);
	
	// create a custom validator for validating id messages
	jQuery.validator.addMethod("validIDSearch", function(value, element) {
	
		// check to see if this is an id search query
		if(value.substr(0,3) != searchObj.ID_SEARCH_TOKEN) {
			// return true as nothing to do
			return true;
		} else {
			// found the id token so we need to validate it
			if(isNaN(value.substr(3)) == true) {
				return false;
			} else {
				return true;
			}
		}
	}, 'An ID search must start with "id:" and be followed by a valid integer');

	// setup the search form
	$("#search").validate({
		rules: { // validation rules
			query: {
				required: true,
				validIDSearch: true,
				minlength: searchObj.MIN_QUERY_LENGTH,
			}
		},
		errorContainer: '#error_message',
		errorLabelContainer: '#error_text',
		wrapper: "",
		showErrors: function(errorMap, errorList) {
			this.defaultShowErrors();
			$("#status_message").hide();
			$("#messages").show();
			$("#error_message").show();
		},
		messages: {
			query: {
				required: "Please enter a few search terms",
				validIDSearch: 'An ID search must start with "id:" and be followed by a valid integer',
				minlength: 'A search query must be ' + searchObj.MIN_QUERY_LENGTH + ' characters or more in length'
			}
		},
		submitHandler: function(form) {
			// indicate that the search is underway
			$("#error_message").hide();
			$("#messages").show();
			$("#status_message").show();
			
			searchObj.searching_underway_flag = true;
			setTimeout("searchObj.updateMessages()", UPDATE_DELAY);
		
			// clear the accordian of any past search results
			searchObj.clearAccordian();
			
			// set up the ajax queue
			var ajaxQueue = $.manageAjax.create("mappingSearchAjaxQueue", {
				queue: true
			});
			
			// undertake the searches in order
			var base_search_url = '';
			var query           = $("#query").val();
			
			if(query.substr(0,3) != searchObj.ID_SEARCH_TOKEN) {
				// this is a name search
				base_search_url = BASE_URL + "search?type=name&limit=" + searchObj.DEFAULT_SEARCH_LIMIT + "&query=" + encodeURIComponent(query);
			} else {
				// this is an id search 
				base_search_url = BASE_URL + "search?type=id&query=" + encodeURIComponent(query.substr(3).replace(/^\s\s*/, '').replace(/\s\s*$/, ''));
			}			
						
			// do a contributor search
			var url = base_search_url + "&task=contributor";
			
			// queue this request
			ajaxQueue.add({
				success: searchObj.buildContributorResults,
				url: url
			});
			
			// do a organisation search
			url = base_search_url + "&task=organisation";
			
			// queue this request
			ajaxQueue.add({
				success: searchObj.buildOrganisationResults,
				url: url
			});
			
			// do a venue search
			url = base_search_url + "&task=venue";
			
			// queue this request
			ajaxQueue.add({
				success: searchObj.buildVenueResults,
				url: url
			});
			
			// do an event search
			url = base_search_url + "&task=event";
			
			// queue this request
			ajaxQueue.add({
				success: searchObj.buildEventResults,
				url: url
			});
			
		}
	});
	
	// check to see if this is a persistent link request
	var searchParam = $.getUrlVar("search");
	
	if(typeof(searchParam) != "undefined") {
		
		// get parameters
		var queryParam = $.getUrlVar("query");
		
		// check on the parameters
		if(typeof(queryParam) == "undefined") {
			
			// show a message as the query parameters are missing
			$("#message_text").text("Error: The persistent URL for this search is incomplete, please try again");
			$("#messages").show();
		} else {
			searchObj.doSearch(queryParam);
		}
	}
	
	/*
	 * browse functionality 
	 */
	 browseObj.getMajorAreas();
	 
	 // associate a click event with the browse major area items
	 $('.browseMajorArea').live('click', browseObj.getSuburbsClickEvent);
	 $('.browseSuburb').live('click', browseObj.getVenuesClickEvent);
	 $('.browseCheckBox').live('click', browseObj.checkboxClickEvent);
	 $('#browse_add_btn').click(browseObj.addToMap);
	 
	 // set up handler for when an ajax request results in an error for searching
	 $("#browse_messages").ajaxError(function(e, xhr, settings, exception) {
	 	// determine what type of request has been made & update the message text accordingly
		// ensure that we're only working on browse activities
		if(settings.url.indexOf("lookup?task=state-list", 0) != -1) {
			$(this).empty().append('<div class="ui-state-highlight ui-corner-all" style="padding: 0 .7em;" id="status_message"><p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>' + AJAX_ERROR_MSG.replace('-', 'the request for Country and State data') + '</p></div>');
		} else if(settings.url.indexOf("lookup?task=suburb-list", 0) != -1) {
			$(this).empty().append('<div class="ui-state-highlight ui-corner-all" style="padding: 0 .7em;" id="status_message"><p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>' + AJAX_ERROR_MSG.replace('-', 'the request for suburb data') + '</p></div>');
		} else if(settings.url.indexOf("lookup?task=suburb-venue-list", 0) != -1) {
			$(this).empty().append('<div class="ui-state-highlight ui-corner-all" style="padding: 0 .7em;" id="status_message"><p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>' + AJAX_ERROR_MSG.replace('-', 'the request for venue data') + '</p></div>');
		}
	});
	
	// set up a handler for when the ajax calls finish
	$("#browse_messages").bind('mappingBrowseAjaxQueue' + 'AjaxStop', browseObj.addDataToMap);
	 
	 /*
	  * map functionality
	  */
	$('#tabs').bind('tabsshow', function(event, ui) {
		if (ui.panel.id == "tabs-3") { // tabs-3 == the map tab
			// update the map
			mappingObj.resizeMap();
		}
	});
});
