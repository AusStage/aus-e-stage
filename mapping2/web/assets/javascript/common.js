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

var mapIconography = { pointer:    BASE_URL + 'assets/images/map-iconography-pointer.png',
					   lozenge:    BASE_URL + 'assets/images/map-iconography-lozenge.png',
					   venue:      BASE_URL + 'assets/images/map-iconography-venue.png',
					   iconWidth:  '24',
					   iconHeight: '32'
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

// setup one of the help dialogs
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
		});
		sidebarState = 1;
	} else {
		// show the sidebar
		$('.sidebar').animate({width: 150}, 'slow', function() {
			$('.peekaboo-tohide').show();
			$('.peekaboo').removeClass('peekaboo-big');
			$('.peekaboo').text('Hide Menu');
		});
		$('.main').removeClass('main-big');
		sidebarState = 0;
	}
	
	// resize the map
	mappingObj.resizeMap();
}
