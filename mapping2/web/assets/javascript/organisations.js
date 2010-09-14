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
var idMsg = "The ID appears to be invalid.\n";
var nameMsg = "The Name appears to be invalid.\n";
var noEventMsg = "The organisation does not have any events that can be mapped.\n\n";
var resultLength = null;
var resultList = [
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
	
	$("#ShowCheckBox").click(function () {
		showCheckBox();
	});
/*	
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
				beforeSubmit: showRequest,
				success:      showSearchResults,			
				error:		showErrorMessage("#name_search_results", nameMsg)	
			});
		}
	});*/
	
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
				success:      showResponse
				//error:		showErrorMessage("#id_search_results", idMsg)
			});
		}
	});	
	
	$("#mapInfo").hide();
	$("#name_search_results").empty();
	$("#id_search_results").empty();
	mapAction(false);
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
		var name_url = '<a href="' + data[0].url + '" target="ausstage">' + data[0].name + '</a>';
		$("#org_name").append(name_url);
		$("#org_total_events_count").empty();
		$("#org_total_events_count").append(data[0].totalEventCount);
		$("#org_mappped_events_count").empty();
		$("#org_mappped_events_count").append(data[0].mapEventCount);
		$("#mapInfo").show();
		getMapData('organisation', data[0].id, 'a', null, null, true);
		mapAction(true);
		
	}else {
		alert('Empty return!');
		removeMarkers();
		//mapAction(false);
		$("#mapInfo").hide();
	}
} 

/*
 * Create checkboxes according to the name search result and append them and a button to the "name_search_result" div 
 */
function showCheckBox() {
	resultLength = resultList.length; 
	var chBox = new Array();
	var name_prefix = "chBox_";
	var href = null;
		
	//create checkboxes
	for (var n = 0; n < resultList.length; n++) {
		cb_name = name_prefix + n;
		href = '<a href="' + resultList[n].url + '" target="ausstage">' + resultList[n].name + '</a> (' + resultList[n].mapEventCount+ '/' + resultList[n].totalEventCount + ')<br>';
		chBox[n] = jQuery('<input type="checkbox" name="' + cb_name + '" id="' + resultList[n].id + '" value="'+ resultList[n].name + '">' + href);
		chBox[n].appendTo('#name_search_results');         
    }
			
	//create "Add To Map" button
	var addMapBttn = jQuery('<input class="ui-state-default ui-corner-all button" type="button" name="addToMap" id="addToMap" value="Add To Map"/>');
	addMapBttn.appendTo('#name_search_results');
	addMapBttn.attr('disabled', 'disabled');
	$('#addToMap').live('click', function (event) {
		addToMap();
	});
	
	//if one of the checkbox is checked, the Add To Map button become enabled. If no checkbox is selected, the AddToMap button becomes disable
	$('#name_search_results input:checkbox').live('click', function (){
		var thisCheck = $(this);
		if (thisCheck.is (':checked'))	{
			$('#addToMap').attr('disabled', '');
		}else if ($('#name_search_results  :checkbox:checked').size() == 0){
			removeMarkers(); //clean the map
			$('#addToMap').attr('disabled', 'disabled'); //disable the button	
		}
	});
	
	$("#name_search_results").show();	
}

function addToMap(){
	//an array that keep the index of the checked checkbox
	var toBeShownIDs = '';   
	var checkBox = null;
	
    //construct the multiple ids with comma
	$('#name_search_results :checkbox:checked').each(function() {
		toBeShownIDs = toBeShownIDs + $(this).attr('id') + ',';
	});
	
	//get rid of the last comma ','
	toBeShownIDs = toBeShownIDs.substr(0, toBeShownIDs.length -1);
	getMapData('organisation', toBeShownIDs, 'a', null, null, true);
	mapAction(true);
}

function mapAction(flag) {
	if (flag == false){
		// hide the map div
		$("#map").hide();
		$("#map_header").hide();
		$("#map_legend").hide();
		$("#map_footer").hide();
	}else { //show the map		
		$("#map_header").show();
		$("#map_legend").show();
		$("#map_footer").show();
		$("#map").show();
	}
}


/** form processing functions **/
//function to show the name search results
function showSearchResults(data)  {
	var JSONFile = "someVar = responseText";
	eval(JSONFile);
	alert(data.message); 
			
}

// function to show a generic error message
function showErrorMessage(whichDiv, msg) {
	$(whichDiv).empty();
	$(whichDiv).append("<table class=\"searchResults\"><tbody><tr class=\"odd\"><td><strong>Error:</strong> " + msg + "<br/>Please check your search terms and try again. If the problem persists contact the site administrator.</td></tr></tbody></table>");
	$(whichDiv).show();		
}

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

