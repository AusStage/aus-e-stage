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

// declare global variables
var idMsg = "The ID appears to be invalid";
var nameMsg = "The Name appears to be invalid";
var returnList = [
		{	"id"				:"121",
			"totalEventCount"	:344,
			"name"				:"Q Theatre Company",
			"mapEventCount"		:202,
			"url"				:"http:\/\/www.ausstage.edu.au\/indexdrilldown.jsp?xcid=59&amp;f_org_id=121"
		},
		{
			"id"				:"102",
			"totalEventCount"	:435,
			"name"				:"Australian Dance Theatre",
			"mapEventCount"		:285,
			"url"				:"http:\/\/www.ausstage.edu.au\/indexdrilldown.jsp?xcid=59&amp;f_org_id=102"
		},
		{
			"id"				:"11",
			"totalEventCount"	:1,
			"name"				:"National Dance Company of Spain",
			"mapEventCount"		:1,
			"url"				:"http:\/\/www.ausstage.edu.au\/indexdrilldown.jsp?xcid=59&amp;f_org_id=11"
		}
];
 

// setup the page
$(document).ready(function(){
	
	// attach the validation plugin to the name search form
	$("#name_search").validate({
		rules: { // validation rules
			organisation_name: {
				required: true
			}
		},
		messages: {
		     query: "Please specify Organisation Name!"
		},
		submitHandler: function(form) {
			jQuery(form).ajaxSubmit({
				dataType:	'json',
				beforeSubmit: showCheckbox,
				success:      showSearchResults,
				error:        showErrorMessage(nameMsg)
			});
		}
	});
	
	$("#id_search").validate({
		rules: { // validation rules
			query: {
				required: true,
				digits: true
			}
		},
    	messages: {
		     query: "Please specify digital ID!"
    	},
		submitHandler: function(form) {
			jQuery(form).ajaxSubmit({
				dataType:	'json',
				beforeSubmit: showRequest,
				success:      showResponse,
				error:        showErrorMessage(idMsg)
			});
		}
	});	
	
	$("#mapInfo").hide();
});

function showRequest(formData, jqForm, options) { 
    // formData is an array; here we use $.param to convert it to a string to display it 
    // but the form plugin does this for you automatically when it submits the data 
    var queryString = $.param(formData); 
 
    // jqForm is a jQuery object encapsulating the form element.  To access the 
    // DOM element for the form do this: 
    // var formElement = jqForm[0]; 
 
    alert('About to submit: \n\n' + queryString); 
 
    // here we could return false to prevent the form from being submitted; 
    // returning anything other than false will allow the form submit to continue 
    return true; 
} 

function showResponse(data)  { 
	// 'data' is the json object returned from the server 
	//alert('jason length: ' + data.length);
	if (data.length == 1){		
		$("#org_id").empty();
		$("#org_id").append(data[0].id);
		$("#org_name").empty();
		var name_url = '<a href="' + data[0].url + '" target="ausstage">' + data[0].name + "</a>";
		$("#org_name").append(name_url);
		$("#org_total_events_count").empty();
		$("#org_total_events_count").append(data[0].totalEventCount);
		$("#org_mappped_events_count").empty();
		$("#org_mappped_events_count").append(data[0].mapEventCount);
		$("#mapInfo").show();
		$("#mapInfo").show();
		getMapData('organisation', data[0].id, 'a', null, null, true);
		$("#map_footer").show();
		
		
	}else {
		showErrorMessage('The ID appears to be invalid, or the organisation does not have any events that can be mapped.\n\n');		
		$("#map").empty();
		$("#map_footer").hide();
		$("#mapInfo").hide();
	}
} 

/*// functions for showing and hiding the loading message
function showLoader(formData, jqForm, options) {

	var queryString = $.param(formData); 
	alert('About to submit: \n\n' + queryString); 
	
	$("#search_waiting").show();
	
	$("#search_results").hide();
	$("#search_results").empty();
	
	$("#map_header").hide();
	$("#map").hide();
	$("#map_legend").hide();
	$("#map_footer").hide();
	 return true; 
}

function hideMap() {
	// hide the map div
	$("#map").hide();
	$("#map_header").hide();
	$("#map_legend").hide();
	$("#map_footer").hide();	
}
*/

function showCheckBox() {
	
	
}

/** form processing functions **/
//function to show the search results
function showSearchResults(data)  {
	var JSONFile = "someVar = responseText";
	eval(JSONFile);
	alert(data.message); 
	
	$("#search_results").hide();
	$("#search_results").empty();
	$("#search_results").append(data.message);
	$("#search_results").show();
	
	hideLoader();
	
	// scroll to the map
	$.scrollTo("#search_results");
	
}

// function to show a generic error message
function showErrorMessage(msg) {

	$("#search_results").hide();
	$("#search_results").empty();
	$("#search_results").append("<table class=\"searchResults\"><tbody><tr class=\"odd\"><td><strong>Error:</strong> An error occured while processing your search.<br/>" + msg + ", or the organisation does not have any events that can be mapped.\n\n<br/>Please check your search terms and try again. If the problem persists contact the site administrator.</td></tr></tbody></table>");
	$("#search_results").show();		
}
/*
// register ajax error handlers
$(document).ready(function() {

	// getting marker xml for contributors
	$("#map").ajaxError(function(e, xhr, settings, exception) {
		if(settings.url.search("action=markers&type=organisation") != -1) {
			$(this).empty();
			$(this).append('<p style="text-align: center"><strong>Error: </strong>An error occured whilst loading markers, please try again.<br/>If the problem persists please contact the site administrator.</p>'); 
		}
	});
});

*/