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

//set up a search class
function SearchClass(){
	this.contributorJson = '';
	this.eventJson = '';
	
	//base url for the search
	this.BASE_URL = 'http://beta.ausstage.edu.au/mapping2/search?';
	
	//query and validation settings
	this.QUERY_LIMIT = 25;
	this.MIN_QUERY_LENGTH = 4;
	this.ID_SEARCH_TOKEN = 'id:';
	
	//search validation messages
	this.SEARCH_ERR_NO_TERMS = 'Please enter a few search terms';
	this.SEARCH_ERR_TO_SHORT = 'A search query must be 4 characters or more';
	this.SEARCH_ERR_NUMERIC_ID = 'id search must have numeric value';

	//search error messages
	this.SEARCH_ERR_CONTRIBUTOR = 'An error occurred searching for contributors';
	this.SEARCH_ERR_EVENT = 'An error occurred searching for events';

	//search in progress messages
	this.CONTRIBUTOR_SEARCH_MSG = 'Contributor search is underway...';
	this.EVENT_SEARCH_MSG = 'Event search is underway...';

	//result selection messages
	this.NO_CONTRIBUTOR_SELECTED = 'No contributor selected';
	this.NO_EVENT_SELECTED = 'No event selected';
	
}

//initialise search related elements
SearchClass.prototype.init = function () {

	//hide the message windows.
	$("#search_status_message").hide();
	$("#search_error_message").hide();	
		
	//lookup button functionality. 
   	$("#search_btn").click(function() {
		//remove any error message
		$("#search_error_message").hide();			
		//get search terms.
		var query = $('#query').val();
		//validate
		if (searchObj.validateQuery(query)){
			searchObj.getData(query);	
		}
    	return false;
    });
}

	//check for id in search terms
SearchClass.prototype.checkForId = function(query) {
	if(query.substr(0,3) == 'id:') {
		return true;
	}	
	else return false;
}
	
	//validate submitted search terms function
SearchClass.prototype.validateQuery = function(query) {
	var error_msg = '';
	//check its length
	if (query.length == 0){ error_msg = searchObj.SEARCH_ERR_NO_TERMS;}
	else if (query.length < 4){error_msg = searchObj.SEARCH_ERR_TO_SHORT;}
	else if (query.substr(0,3) == 'id:' && isNaN(query.substr(3))){error_msg = searchObj.SEARCH_ERR_NUMERIC_ID}
	//display error if any
	if (error_msg!=''){
		showMessage(error_msg, '#search_error');
		return false;
	} else
	return true;
}


SearchClass.prototype.getData = function(query) {
	var query_string;
	var type;
	//clear the accordions
	$("#contributor_heading").empty().append('Contributors');
	$("#contributor_results").empty();
	$("#event_heading").empty().append('Events');
	$("#event_results").empty();
	
	if(searchObj.checkForId(query)){
		type = 'id';
		query = encodeURIComponent(query.substr(3).replace(/^\s\s*/, '').replace(/\s\s*$/, ''));
		//add extra 0's if required
		while(query.length<4){
			query = '0'+query;
		}
	}else {
		type = 'name';
		query = encodeURIComponent(query);
	}
	
	query_string = searchObj.BASE_URL+'task=contributor&type='+type+'&query='+query+'&limit='+searchObj.QUERY_LIMIT+'&callback=?';
				
	showMessage(searchObj.CONTRIBUTOR_SEARCH_MSG, '#search_status');
	
	//request json data for contributors
	$.jsonp({
		url:query_string,
		error:function(error){hideMessage('#search_status');
								showMessage(searchObj.SEARCH_ERR_CONTRIBUTOR, '#search_error');},
		success:function(json){
			searchObj.contributorJson = json
			//if successful,display and request event json data 
			searchObj.displayContributorResults(json);
			showMessage(searchObj.EVENT_SEARCH_MSG, '#search_status');
			query_string = searchObj.BASE_URL+'task=event&type='+type+'&query='+query+'&limit='+searchObj.QUERY_LIMIT+'&callback=?';
			$.jsonp({
				url:query_string,
				error:function(error){hideMessage('#search_status');
										showMessage(searchObj.SEARCH_ERR_EVENT+' events', '#search_error');},
				success:function(json){
					searchObj.eventJson = json
					searchObj.displayEventResults(json);
					hideMessage('#search_status');
				}				
			})
		}

	})
}

SearchClass.prototype.displayEventResults = function(data){

	var html = '<table class="searchResults"><thead><tr><th style="text-align: center">&nbsp</th><th>Event Name</th><th>Venue</th>'+
				'<th class="alignRight">First Date</th></tr></thead><tbody>';	
	
	var i = 0;
	
	for(i; i<data.length; i++){
		if(isEven(i)){
			html += '<tr>';
		}else {html += '<tr class="odd">'}	
		
		html += '<td style="text-align: center">'
				+'<span id="'+data[i].id+'" class="eventAddIcon ui-icon ui-icon-plus clickable" style="display: inline-block;"></span></td>';
		html += '<td><a href="' + data[i].url + '" title="View the record for ' + data[i].name + ' in AusStage" target="_ausstage">' + data[i].name 				+ '</a></td>';		
		html += '<td class="nowrap">' + data[i].venue.name + ' ';
        
        if(data[i].venue.suburb != null) {
        	html += data[i].venue.suburb + ', ';
        }        
        if(data[i].venue.state != null) {
        	html += data[i].venue.state + ', ';
        }
                
        if(data[i].venue.suburb != null || data[i].venue.state != null) {
        	html = html.substr(0, html.length - 2);
        }        
        html += '</td><td class="nowrap alignRight">' + data[i].firstDisplayDate + '</td>';
                
        html += '</tr>';				
	}
	html+= '</tbody><tfoot><tr>'
			+'<td colspan="3" style="vertical-align:middle"><div id="selected_event"></div></td>'
			+'<td class="alignRight">'
			+'<select id="eventDegree" disabled="disabled"> <option>1st Degree</option><option>2nd Degree</option></select>'
			+'<button id="viewEventNetwork" class="addSearchResult" disabled="disabled">View Network</button></td></tr>'
			+'<tr><td colspan="4"><div id="searchAddEventError"></div></td></tr>'
			+'</tfoot></table>';

    if(i > 0) {
    	$("#event_results").empty().append(html);
    	styleButtons();
    	viewerControl.displaySelectedEvent();
    }
        
    if(i == 25) {
       $("#event_heading").empty().append("Events (25+)");
    } else {
       $("#event_heading").empty().append("Events (" + i + ")");
    }	
	
    $('.eventAddIcon').click( function (){
    	var result = viewerControl.addEventId($(this)[0].id, searchObj.eventJson);
		if (result != SUCCESS){
			$('#searchAddEventError').empty().append(buildInfoMsgBox(result));	
		}
    });    
       
}

SearchClass.prototype.displayContributorResults = function(data){
	var html = '<table class="searchResults"><thead><tr><th>&nbsp</th><th>Name</th><th>Event Dates</th><th>Functions</th>'+
				'<th class="alignRight numeric">Total Events</th></tr></thead><tbody>';
	
	for(i=0;i<data.length; i++){
		if(isEven(i)){
			html += '<tr>';
		}else {html += '<tr class="odd">'}	

		html += '<td style="text-align: center"><span id="'+data[i].id+'" class="contributorAddIcon ui-icon ui-icon-plus clickable" style="display: inline-block;"></span></td>';
		html += '<td class="nowrap"><a href="' + data[i].url + '" title="View the record for ' + data[i].firstName + ' ' + data[i].lastName + 
				' in AusStage" target="_ausstage">' + data[i].firstName + " " + data[i].lastName + '</a></td>';
		html += '<td class="nowrap">' + data[i].eventDates + '</td><td>';
		
		if(data[i].functions.length > 0) {
			var comma = "";
        	for(var x = 0; x < data[i].functions.length; x++) {
            	html += comma+data[i].functions[x];
            	comma = ", ";
			}
		} else {
			html += "&nbsp";	
		}
		html += '</td><td class="alignRight numeric">' + data[i].totalEventCount + '</td></tr>';
	}	
	html+= '</tbody><tfoot><tr>'
			+'<td colspan="4" style="vertical-align:middle"><div id="selected_contributors" style="display: inline"></div></td>'
			+'<td class="alignRight"><button id="viewContributorNetwork" class="addSearchResult" disabled="disabled">View Network</button></td></tr>'
			+'<tr><td colspan="5"><div id="searchAddContributorError"></div></td>';
    if(i > 0) {

        $("#contributor_results").empty().append(html);
        styleButtons();
        viewerControl.displaySelectedContributors();
    }
	if(i == 25) {
        $("#contributor_heading").empty().append("Contributors (25+)");
    } else {
        $("#contributor_heading").empty().append("Contributors (" + i + ")");
    }
    
    $('.contributorAddIcon').click( function (){
    	var result = viewerControl.addId($(this)[0].id, searchObj.contributorJson);
		if (result != SUCCESS){
			$('#searchAddContributorError').empty().append(buildInfoMsgBox(result));	
		}
    });    
}

showMessage = function(text, location){
	$(location+'_text').empty();
	$(location+'_text').append(text);
	$(location+'_message').show();	
}

hideMessage = function(location){
	$(location+'_message').hide();	
}

