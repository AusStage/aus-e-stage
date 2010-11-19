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

// declare global objects
var searchObj = new SearchClass();
var browseObj = new BrowseClass();
 
// show / hide the menu
$(document).ready(function(){

	/*
	 * page setup
	 */

	// hide the messages div
	$("#messages").hide();
	
	// prevent a FOUC
	$('html').removeClass('js');

	// add a click handler to the hide menu link
	$("#hidePanel").click(function(){
		$("#panel").animate({marginLeft:"-175px"}, 500 );
		$("#sidebar").animate({width:"0px", opacity:0}, 400 );
		$("#showPanel").show("normal").animate({width:"18px", opacity:1}, 200);
		$("#main").animate({marginLeft:"30px"}, 500);
		$("#tabs").animate({width:"100%"});
	});
	
	// add a click handler to the show menu link
	$("#showPanel").click(function(){
		$("#main").animate({marginLeft:"200px"}, 200);
		$("#tabs").animate({width:"100%"});
		$("#panel").animate({marginLeft:"0px"}, 400 );
		$("#sidebar").animate({width:"175px", opacity:1}, 400 );
		$("#showPanel").animate({width:"0px", opacity:0}, 600).hide("slow");
	});
	
	// setup the tabs
	$("#tabs").tabs();
	
	// style the buttons
	styleButtons();
	
	/*
	 * search functionality
	 */
	
	// associate the tipsy library with the form elements
	$('.tipsy_form [title]').tipsy({trigger: 'focus', gravity: 'n'});
	
	// setup the accordian
	$(".accordion").accordion({collapsible:true, active:false, autoHeight: false });
	
	// setup the help dialog
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
	
	// set up handler for when an ajax request results in an error
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
				validIDSearch: true
			}
		}, submitHandler: function(form) {
			// indicate that the search is underway
			$("#messages").show();
			$("#status_message").show();
			
			searchObj.searching_underway_flag = true;
			setTimeout("searchObj.updateMessages()", UPDATE_DELAY);
		
			// clear the accordian of any past search results
			searchObj.clearAccordian();
			
			// set up the ajax queue
			var ajaxQueue = $.manageAjax.create("mappingAjaxQueue", {
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
			
			// add to the search history if necessary
			if(jQuery.inArray($("#query").val(), searchObj.search_history_log) == -1) {
				$("#search_history").append('<li><a href="#" onclick="searchObj.doSearch(\'' + $("#query").val() + '\'); return false;" title="Click to Repeat the Search">Repeat search for: ' + $("#query").val() + '</a> / <a href="' + BASE_URL + 'mapping.jsp?search=true&query=' + encodeURIComponent($("#query").val()) + '" title="Persistent Link for this Search">Persistent Link</a></li>');
				searchObj.search_history_log.push($("#query").val());
			}
			
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
});
