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
 
/**
 * Common global variables used across more than one page in the site
 */
var BASE_URL = "/mapping2/";
var UPDATE_DELAY = 500;
var AJAX_ERROR_MSG    = 'An unexpected error occurred during -, please try again. If the problem persists contact the AusStage team.'; 

var ADD_VIEW_BTN_HELP = '<span class="ui-icon ui-icon-help clickable use-tipsy show_add_view_help" style="display: inline-block;" title="Add / View Map Help"></span>';

// declare global objects / variables
var searchObj    = null;
var browseObj    = null;
var mappingObj   = null;
var sidebarState = 0;

var mapIconography = { pointer:      BASE_URL + 'assets/images/iconography/pointer.png',
					   contributor:  BASE_URL + 'assets/images/iconography/contributor.png',
					   organisation: BASE_URL + 'assets/images/iconography/organisation.png',
					   venue:        BASE_URL + 'assets/images/iconography/venue-arch.png',
					   event:        BASE_URL + 'assets/images/iconography/event.png',
					   iconWidth:    '32',
					   iconHeight:   '32',
					   contributorColours:  ['b-112', 'b-113', 'b-114', 'b-115', 'b-116'],
					   organisationColours: ['b-127', 'b-128', 'b-129', 'b-130', 'b-131'],
					   venueColours:        ['b-142', 'b-143', 'b-144', 'b-145', 'b-146'],
					   eventColours:        ['b-97', 'b-98', 'b-99', 'b-100', 'b-101'],
					   individualContributors:  ['b-50', 'b-49', 'b-48', 'b-47', 'b-46', 'b-45', 'b-44', 'b-43', 'b-42', 'b-41', 'b-40', 'b-39', 'b-86', 'b-85', 'b-84', 'b-83', 'b-82', 'b-81', 'b-80', 'b-79', 'b-78', 'b-77', 'b-76', 'b-75', 'b-74', 'b-73', 'b-72', 'b-71', 'b-70', 'b-69', 'b-68', 'b-67', 'b-66', 'b-65', 'b-64', 'b-63', 'b-62', 'b-61', 'b-60', 'b-59', 'b-58', 'b-57', 'b-56', 'b-55', 'b-54', 'b-53', 'b-52', 'b-51'],
					   individualOrganisations: ['b-66', 'b-67', 'b-68', 'b-69', 'b-70', 'b-71', 'b-72', 'b-73', 'b-74', 'b-75', 'b-76', 'b-77', 'b-78', 'b-79', 'b-80', 'b-81', 'b-82', 'b-83', 'b-84', 'b-85', 'b-86', 'b-60', 'b-61', 'b-62', 'b-63', 'b-64', 'b-65', 'b-59', 'b-58', 'b-57', 'b-56', 'b-55', 'b-54', 'b-53', 'b-52', 'b-51', 'b-50', 'b-49', 'b-48', 'b-47', 'b-46', 'b-45', 'b-44', 'b-43', 'b-42', 'b-41', 'b-40', 'b-39']
					 };
 
/**
 * Common functions used across more than one page in the site
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

// function to apply the correct styles to buttons
function styleButtons() {
	$("button, input:submit").button();
	$("button, input:button").button();
}

// setup the help dialogs
$(document).ready(function() {

	$("#help_add_view_div").dialog({ 
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

	// associate the show_add_view_help div with the help icon
	$('.show_add_view_help').live('click', function () {
		$('#help_add_view_div').dialog('open');
	});
	
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
});

$('.clickable').live('mouseenter', function() {
	$(this).addClass('clickable-hover');
});

$('.clickable').live('mouseleave', function() {
	$(this).removeClass('clickable-hover');
});

// define a function to build an error message box
function buildErrorMsgBox(text) {
	return '<div class="ui-state-error ui-corner-all search-status-messages"><p><span class="ui-icon ui-icon-info status-icon"></span>' + AJAX_ERROR_MSG.replace('-', text) + '</p></div>';
}

// define a function to build an info message box
function buildInfoMsgBox(text) {
	return '<div class="ui-state-highlight ui-corner-all search-status-messages" id="status_message"><p><span class="ui-icon ui-icon-info status-icon"></span>' + text + '</p></div>';
}

// define a function to resize the sidebar
function resizeSidebar() {
	if(sidebarState == 0) {
		// hide the sidebar 
		$('.peekaboo-tohide').hide();
		$('.peekaboo').html('&raquo;');
		$('.peekaboo').addClass('peekaboo-big');
		$('.sidebar').animate({width: 15}, 'slow', function() {
			$('.main').addClass('main-big');
			
			// resize the map
			mappingObj.resizeMap();
		});
		sidebarState = 1;
	} else {
		// show the sidebar
		// temp development width
		$('.sidebar').animate({width: 250}, 'slow', function() {
			$('.peekaboo-tohide').show();
			$('.peekaboo').removeClass('peekaboo-big');
			$('.peekaboo').text('Hide Menu');
			$('.main').removeClass('main-big');
	
			// resize the map
			mappingObj.resizeMap();
		});
		sidebarState = 0;
	}
}

// define a function used to sort an array of contributor objects on name
function sortContributorArray(a, b) {

	if(a.contributor.lastName == b.contributor.lastName) {
		if((a.contributor.lastName + a.contributor.firstName) == (a.contributor.lastName + a.contributor.firstName)) {
			return 0;
		} else if((a.contributor.lastName + a.contributor.firstName) < (a.contributor.lastName + a.contributor.firstName)) {
			return -1;
		} else {
			return 1;
		}
	} else if(a.contributor.lastName < b.contributor.lastName) {
		return -1;
	} else {
		return 1;
	}
}

// define a function used to sort an array of organisation objects on name
function sortOrganisationArray(a, b) {

	if(a.organisation.name == b.organisation.name) {
		return 0;
	} else if(a.organisation.name < b.organisation.name) {
		return -1;
	} else {
		return 1;
	}
}

// define a function used to sort an array of contributor objects on name
function sortContributorArrayAlt(a, b) {

	if(a.lastName == b.lastName) {
		if((a.lastName + a.firstName) == (a.lastName + a.firstName)) {
			return 0;
		} else if((a.lastName + a.firstName) < (a.lastName + a.firstName)) {
			return -1;
		} else {
			return 1;
		}
	} else if(a.lastName < b.lastName) {
		return -1;
	} else {
		return 1;
	}
}

// define a function used to sort an array of organisation objects on name
function sortOrganisationArrayAlt(a, b) {

	if(a.name == b.name) {
		return 0;
	} else if(a.name < b.name) {
		return -1;
	} else {
		return 1;
	}
}


// define a function used to sort an array of venue objects on name
function sortVenueArray(a, b) {

	if(a.name == b.name) {	
		return 0;
	} else if(a.name < b.name) {
		return -1;
	} else {
		return 1;
	}
}

// define a function used to sort an array of venue objects on name
function sortEventArray(a, b) {

	if(a.firstDate == b.firstDate) {	
		return 0;
	} else if(a.firstDate < b.firstDate) {
		return 1;
	} else {
		return -1;
	}
}
