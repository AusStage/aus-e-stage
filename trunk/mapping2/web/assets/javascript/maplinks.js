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

/*
 * Use the $(document).ready() function to ensure that our code only executes once the page has completed
 * loading and is ready to manipulate
 *
 * When the document has completed loading the ready() function will execute our anonymous function
 */
$(document).ready(function() {

	/* 
	 * get the parameters to this page using the function that is outlined at the beginning 
	 * of this file
	 */
	var type = $.getUrlVar("type");
	var id   = $.getUrlVar("id");
	
	/*
	 * check on the value of the parameters. If they're "undefined" the URL didn't include what we require to continue
	 */
	if(typeof(type) == "undefined" || typeof(id) == "undefined") {
		// execute a function to show an error message
		showMissingParameterMessage();
	} else { 
		// use the getMapData function in the map.functions.js file to load the map data
		getMapData(type, id, true);
	}
});

/* 
 * A function to show an error message
 */
// function to show an error message
function showMissingParameterMessage() {

	/*
	 * Use jQuery functions to hide various elements and populate the #map element with an
	 * informative error message
	 */

	// hide certain elements
	$("#map_name").hide();
	$("#map_header").hide();
	$("#map_legend").hide();
	
	// empty the contents of this tag
	$("#map").empty();
	
	// add the error message to the page
	$("#map").append('<p style="text-align: center"><strong>Error: </strong>The URL is missing required parameters, please ensure the URL is correct and try again.<br/>If the problem persists please contact the site administrator.</p>'); 
}
