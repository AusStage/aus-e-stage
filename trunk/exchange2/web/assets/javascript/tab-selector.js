/*
 * This file is part of the AusStage Data Exchange Service
 *
 * AusStage Data Exchange Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * AusStage Data Exchange Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AusStage Data Exchange Service.  
 * If not, see <http://www.gnu.org/licenses/>.
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

// automatically select a tab based on parameter
$(document).ready(function() {

	var tab  = $.getUrlVar('tab');
	var tabs = $('#tabs');
	
	// was the parameter passed?
	if(typeof(tab) != 'undefined') {
		if(tab == 'secgenre') {
			tabs.tabs('select', 1);
		} else if(tab == 'contentindicator') {
			tabs.tabs('select', 2); 
		} else if(tab == 'ressubtype') {
			tabs.tabs('select', 3);
		}else {
			tabs.tabs('select', 0);
		}
	} else {
		tabs.tabs('select', 0);
	}
});
