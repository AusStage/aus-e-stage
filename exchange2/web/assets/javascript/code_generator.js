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
var taskLabel 	= ["Contributor","Organisation","Venue","Second Genre", "Content Indicator"];
var tasks		= ["contributor","organisation","venue","secgenre", "contentindicator"];

var sortByLabel	= ["First Date", "Created Date", "Updated Date"];
var sortBy	= ["firstdate", "createdate", "updatedate"];

var typeLabel	= ["Events", "Resources"];
var types		= ["events", "resources"];

var idList = [];

var style = true;

var loadingMessage = '<p> Retrieving AusStage records...</p>';
var serverError = '<p> An error occurred retrieving AusStage Records</p>';

// initialise the page
$(document).ready(function() {

	//style the button
	$("#getCode").button();
	
	//set up the event embed page
	initEventPage();
	
	//set up search dialog etc
	initSearch();
});

function initEventPage(){
	//hide the textarea to start
	$('#viewer').hide();
	
	//add the labels and values for the type option
	$.each(typeLabel, function(index, value){
		$("#type").addOption(types[index],value);	
	});
	$("#type").val(types[0]);
	
	//add the labels and values for the task option
	$.each(taskLabel, function(index, value){
		$("#task").addOption(tasks[index],value);	
	});
	$("#task").val(tasks[0]);
	
	//add disable and enable to text box for radio buttons. (Records returned section)
	$("input[name*='limitGroup']").click(function() {
                if($(this).val()=='userEnter'){
                	$('#userLimit').removeAttr('disabled')
                }else{
                	$('#userLimit').attr('disabled', 'disabled')}
	});
	
	//add labels to the sort by drop down
	$.each(sortByLabel, function(index, value){
		$("#sortBy").addOption(sortBy[index],value);	
	});
	$("#sortBy option:first").attr('selected','selected');
	//bind functions to the buttons
	$("#getCode").bind('click', function(){if(validateOptions()){getCode()}});
	//if the All option is selected remove error from limit field;
	$('#noLimit').bind('click', function(){$("#limitError").empty();});
	//if task changes, clear the selected list
	$('#task').change(function(){
		$("#search_name_label").empty().append($('#task option:selected').text());			
		clearSelectedList();
	});
	//if type changes add or remove options accordingly to the task list.
	$('#type').change(function(){
		addRemoveOptions()});
	//add toggle functionality to the styleGroup radio buttons
	$('input[name=styleGroup]:radio').change(function(){
		toggleStyle(this)});
		
}


//validate the options before proceeding
function validateOptions(){

	var validated = true;	
	//clear error massages
	$(".error").empty();
	
	if (idList.length >10 || idList.length == 0){ 
		$("#idError").append(buildErrorMsgBox("Select between 1 and 10 "+$('#task option:selected').text()+"'s"));	
		validated = false;
	}
	
	//check record limit is set
	var limit = $('input:radio[name=limitGroup]:checked').val();
	if (limit !="all"){
		$('#userLimit').val($('#userLimit').val().split(' ').join(''));
		//remove letters
		$('#userLimit').val($('#userLimit').val().replace(/[a-zA-Z]/g, ""));
		//remove all other characters
		$('#userLimit').val($('#userLimit').val().replace(/[^\w]/gi, ''));
		
		$('input:radio[name=limitGroup]:checked').val($('#userLimit').val());
		if ($("#userLimit").val() == ''){ 
			$("#limitError").append(buildErrorMsgBox("Enter a maximum number of records to be returned"));	
			validated = false;
		}
		
	}
	return validated;
	
}


//get preview of the data that will be returned. and present code to be embedded
function getCode(){
	
	//first generate the preview
	var html;
	$("#preview").empty().append(loadingMessage);
	
	var styleString = "<style type='text/css'>\n"
						+"<!-- \n"
						+"/* You can modify these CSS styles */\n"
						+".ausstage-data p{font-size:110%;padding:5px;}"
						+".ausstage-data{list-style:none;border:1px solid grey;padding:0px;margin:5px;font-size: 95%;font-family: Helvetica, Verdana, Arial, sans-serif;color:#333;}\n"
						+".off{background-color:#FFF} \n"
						+".on{background-color:#EEE}\n"
						+"-->\n"
						+"</style>\n"
									
	var recordType = $('#type').val();					
	var type = $('#task').val();
	var limit = $('input:radio[name=limitGroup]:checked').val();
	var sort = $('#sortBy').val();
		
	var id = '';
	var names = '';
	var tempName = '';
	var urlStart = 'http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&'
	var urlOption = '';
	var header = '';

	switch (type){
		case 'contributor':
			urlOption = 'f_contrib_id=';
			break;
		case 'organisation':	
			urlOption = 'f_org_id=';
			break;
		case 'venue':
			urlOption = 'f_venue_id=';
			break;
	}
	
	for (i in idList){
		id += idList[i].id +',';
		if (type == 'contentindicator'||type == 'secgenre'){
			tempName = idList[i].name;
		}else {
			tempName = "<a href="+urlStart+urlOption+idList[i].id+" target='_blank' title='view this record in Ausstage' >"+idList[i].name+"</a>";
		}
		names += (i==0)?tempName:(i<idList.length-1)?', '+tempName:' and '+tempName;
	}
	
	header = "<p>"+$('#type option:selected').text()+" for "+names+"</p>";			

	var url = 'http://beta.ausstage.edu.au/exchange/'+recordType+'?type='+type+'&id='+id+'&limit='+limit+'&sort='+sort+'&output=json&callback=?'
	
	$.getJSON(url, 
		function(json) {
			var row;
			var html = '';
			html += (style)?styleString:'';	
			html += "<ul class='ausstage-data'>";
			html += header
			html += json.shift()._generator;
			if(json.length < 1){
				html +="<li>The selected criteria returned no results from the ausstage database</li>";
			}
			$.each(json, function(index, value){
				row = (index%2) ? "on" : "off" 
				if(recordType == 'events'){
					html += '<li class="'+row+'"><a href="'+value.url+'" title="View this record in AusStage">'+value.name+'</a>';
					html += ', '+value.venue+', '+value.date+'</li>'; 	
				}else{
					html += '<li class="'+row+'"><a href="'+value.url+'" title="View this record in AusStage">'+value.title+'</a>, '+value.citation;			
				}
		});
		html += '</ul>';
		$("#preview").empty().append(html);
	}).error(function(){$('#preview').empty().append(serverError)});

	//then the code to embed
	
	//first get a unique number to use as an id incase more than one ausstage embed
		var divId = new Date().getTime();
		var codeString = '';
		codeString += (style)?styleString:'';
		codeString += "<div id='"+divId+"'>"+loadingMessage+"</div>\n"
			+"<script type='text/javascript' src='http://code.jquery.com/jquery-1.6.4.min.js'></script>	\n"
			+"<script type='text/javascript'>\n"
			+"$.getJSON('"+url+"',\n" 
				+"function(json) {\n"
				+"var row;	\n"
				+"var html = \"<ul class='ausstage-data'>\";\n"
				+'html += "'+header+'";\n'
				+"html += json.shift()._generator;\n"
				+"if(json.length < 1){"
				+"html +='<li>The selected criteria returned no results from the ausstage database</li>' \n}"
				+"$.each(json, function(index, value){\n"
					+"row = (index%2) ? 'on' : 'off'\n"; 
				if(recordType =='events'){
					codeString+="html += '<li class=\"'+row+'\"><a href=\"'+value.url+'\" title=\"View this record in AusStage\">'+value.name+'</a>'\n"
					+"html += ', '+value.venue+', '+value.date+'</li>'\n";
				}else{
					codeString+="html += '<li class=\"'+row+'\"><a href=\"'+value.url+'\" title=\"View this record in AusStage\">'+value.title+'</a>, '+value.citation";
				}	
				codeString+="});\n"
				+"html += '</ul>';\n"
				+"$('#"+divId+"').empty().append(html);\n"
			+"}).error(function(){$('#"+divId+"').empty().append('"+serverError+"')});\n"
		+"</script>\n"
		$('#embedText').val(codeString);
		$('#viewer').show();

	
}

//function to toggle the css styles for embedded code. Links to the StyleGroup radio buttons
function toggleStyle(selected){
	style = (selected.value == "true")?true:false;
}

function initSearch(){

	$('#addEntity').click(function(){
		if($('#task').val()=='secgenre'){$('#secgenre_div').dialog('open');}
		else if($('#task').val()=='contentindicator'){$('#contentindicator_div').dialog('open');}
		else if($('#task').val()=='ressubtype'){$('#ressubtype_div').dialog('open');}
		else $('#search_div').dialog('open');
	});

	// setup the dialog box
	$("#search_div").dialog({ 
		autoOpen: false,
		height: 400,
		width: 650,
		modal: true,
		buttons: {
			'Search': function() {
				// submit the form
				$('#search_form').submit();
			},
			Close: function() {
				$(this).dialog('close');
			}
		},
		open: function() {
			// tidy up the form on opening
			$("#query").val('');

			clearSearchResults();
			showLoader("hide");
		},
		close: function() {
			// tidy up the form on close
			clearSearchResults()
		}
	});

	// setup the dialog box
	$("#contentindicator_div").dialog({ 
		autoOpen: false,
		height: 400,
		width: 400,
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

	//populate the second genre dialog
	var url = "http://beta.ausstage.edu.au/exchange/lookup?task=contentindicator";
			
			$.jsonp({
				url:url+'&callback=?',
				error: showErrorMessage,
				success: function(data){
					addContentIndicator(data);
				}	
	});


	// setup the dialog box
	$("#secgenre_div").dialog({ 
		autoOpen: false,
		height: 400,
		width: 400,
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

	//populate the second genre dialog
	var url = "http://beta.ausstage.edu.au/exchange/lookup?task=secgenre";
			
			$.jsonp({
				url:url+'&callback=?',
				error: showErrorMessage,
				success: function(data){
					addSecGenre(data);
				}	
	});

	// setup the dialog box
	$("#ressubtype_div").dialog({ 
		autoOpen: false,
		height: 400,
		width: 400,
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

	//populate the second genre dialog
	var url = "http://beta.ausstage.edu.au/exchange/lookup?task=ressubtype";
			
			$.jsonp({
				url:url+'&callback=?',
				error: showErrorMessage,
				success: function(data){
					addResSubType(data);
				}	
	});


	// attach the validation plugin to the name search form
	$("#search_form").validate({
		rules: { // validation rules
			query: {
				required: true
			}
		},
		submitHandler: function(form) {
			var url = "http://beta.ausstage.edu.au/mapping2/search?task="+$('#task').val()+"&type=name&format=json&query="+encodeURIComponent($('#query').val());
			$("#search_waiting").show(); clearSearchResults();
			
			$.jsonp({
				url:url+'&callback=?',
				error: showErrorMessage,
				success: function(data){
					showSearchResults(data);
				}	
			});
		}
	});
	
}

function buildErrorMsgBox(text) {
        return '<div class="ui-state-error ui-corner-all search-status-messages" id="error_message"><span class="ui-icon ui-icon-alert status-icon"></span>' + text + '</div>';
}


/** form processing functions **/
//function to clear results
function clearSearchResults(){
		$("#search_results_org").hide();
		$("#search_results_body_org").empty();			
		$("#search_results_venue").hide();
		$("#search_results_body_venue").empty();												
		$("#search_results").hide();
		$("#search_results_body").empty();			
		$("#error_message").hide();	
}

// function to show the moving bar
// functions for showing and hiding the loading message
function showLoader(type) {

	if(type == "show" || typeof(type) == "undefined") {
		// tidy up the search results
		clearSearchResults();
	
		//show the loading message
		$("#search_waiting").hide();
	} else {
		// hide the loading message
		$("#search_waiting").hide();
	}
	
}

// function to show the search results
function showSearchResults(responseText)  {

	//define helper constants
	var MAX_FUNCTIONS = 3;

	// tidy up the search results
	clearSearchResults();

	var html = "";
	
	switch ($('#task').val()){
		case 'contributor':
			var contributor;
			var functions;
			// loop through the search results
			for(i = 0; i < responseText.length; i++){
				contributor = responseText[i];
				// add the name and link
				html += '<tr><td><a href="' + contributor.url + '" target="ausstage" title="View the record for ' + 
				contributor.firstName +' ' +contributor.lastName/*contributor.name*/ + ' in AusStage">' + contributor.firstName +' ' 
				+contributor.lastName + '</a></td>';
				// add the event dates
				html += '<td>' + contributor.eventDates + '</td>';
				// add the list of functions
				html += '<td>';
				functions = contributor.functions;
				var space = '';
				for(x = 0; x < functions.length; x++) {
						html += space + functions[x];
						space = ', ';
				}
				html += '</td>';
				// add the button
				html += '<td><button id="choose_' + contributor.id + '_'+contributor.firstName+' '+contributor.lastName+'" class="choose_button">Choose</button></td>';
				// finish the row
				html += '</tr>';
			}
		break;		
		case 'organisation':
			var org;
			// loop through the search results
			for(i = 0; i < responseText.length; i++){
				org = responseText[i];
				// add the name and link
				html += '<tr><td><a href="' + org.url + '" target="ausstage" title="View the record for ' + 
				org.name + ' in AusStage">' + org.name + '</a></td>';
				// add the venue info
				html += '<td>' + org.address;
				if (org.suburb != null){
					html += ', '+org.suburb;	
				}
				if (org.country == 'Australia'){
					html += ', '+org.state;
				}else {
					html += ', '+org.country;
				}
				html += '</td>';
				// add the button
				html += '<td><button id="choose_' + org.id + '_'+org.name+'" class="choose_button">Choose</button></td>';	
				// finish the row
				html += '</tr>';
			}
		break;
		
		case 'venue':
			var venue;
			// loop through the search results
			for(i = 0; i < responseText.length; i++){
				venue = responseText[i];
				// add the name and link
				html += '<tr><td><a href="' + venue.url + '" target="ausstage" title="View the record for ' + 
				venue.name + ' in AusStage">' + venue.name + '</a></td>';
				// add the venue info
				html += '<td>' + venue.address;
				if (venue.suburb != null){
					html += ', '+venue.suburb;	
				}
				if (venue.country == 'Australia'){
					html += ', '+venue.state;
				}else {
					html += ', '+venue.country;
				}
				html += '</td>';
				// add the button
				html += '<td><button id="choose_' + venue.id + '_'+venue.name+'" class="choose_button">Choose</button></td>';	
				// finish the row
				html += '</tr>';
			}
		break;
			
//////		
	}
	// check to see on what was built
	if(html != "") {
		switch ($('#task').val()){ 
			case 'contributor':
				// add the search results to the table
				$("#search_results_body").append(html);
			break;
						
			case 'organisation':
				$("#search_results_body_org").append(html); 
			break;
			
			case 'venue':
				$("#search_results_body_venue").append(html); 
			break;				
		}
		// hide the loader
		showLoader("hide");
	
		// style the new buttons
		$("button, input:submit").button();
		
		// add a function to each of the choose buttons
		$(".choose_button").click(function(eventObject) {

			// get the id of this button
			var id = this.id;
			
			var tags = id.split("_");
			
			// add the id to the text file
			//$("#id").val(tags[1]);
			var exists = false;
			for (i in idList){
				if(idList[i].id==tags[1]){
					exists = true;
				}	
			}
			if (!exists){idList.push({"id":tags[1], "name":tags[2]});}		
			updateSelectedList();	
		});
	
		switch($('#task').val()){
			case 'contributor':
			// show the search results
				$("#search_results").show();
			break;	
			
			case 'organisation':
				$("#search_results_org").show(); 
			break;

			case 'venue':
				$("#search_results_venue").show(); 
			break;
		}
	} else {
		
		// hide the loader
		showLoader("hide");
	
		$("#error_message").empty();
		$("#error_message").append('<div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;"><p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span><strong>Warning:</strong> No '+$('#task option:selected').text()+'s matched your search criteria. Please try again</p></div>');	
		$("#error_message").show();
	}
}

// function to show a generic error message
function showErrorMessage() {

	// tidy up the search results
	$("#search_results_body").empty();
	$("#search_results").hide();
	
	// hide the loader
	showLoader("hide");
	
	// show an error message
	$("#error_message").empty();
	$("#error_message").append('<div class="ui-state-error ui-corner-all" style="padding: 0 .7em;"><p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span><strong>Error:</strong> An error occured while processing this request. Please try again. <br/>If the problem persists please contact the site administrator.</p></div>');
	$("#error_message").show();
}

function updateSelectedList(){
	//clear the area
	$('#selectedIds').empty();
	//loop through the array, adding the name and a remove button for each element.
	for (i in idList){
		$('#selectedIds').append("<li><span title='remove this record' id='"+i+"' class='remove ui-icon ui-icon-minus clickable' style='display: inline-block;'></span>"+idList[i].name+"</li>");	
	}
	$('.remove').click(function(eventObject){idList.splice(this.id,1);updateSelectedList();})

}

function clearSelectedList(){
	//clear the array
	idList.splice(0, idList.length);
	updateSelectedList();	
}

function addSecGenre(data) {
	var list;
	for(i = 0; i < data.length; i++) {
		
		if(i % 2 == 1) {
			list += '<tr class="odd">'; 
		} else {
			list += '<tr>'; 
		}
		
		list += '<td>' + data[i].term + '</td><td class="alignRight">' + data[i].events + '</td><td class="alignRight">'
		+'<button id="choose_' + data[i].id + '_'+data[i].term+'" class="choose_button">Choose</button></td></tr>';
	}
	
	$('#secgenre-table').empty().append(list);
	
	$(".choose_button").click(function(eventObject) {

		// get the id of this button
		var id = this.id;
			
		var tags = id.split("_");
			
		// add the id to the text file
		//$("#id").val(tags[1]);
		var exists = false;
		for (i in idList){
			if(idList[i].id==tags[1]){
				exists = true;
			}	
		}
		if (!exists){idList.push({"id":tags[1], "name":tags[2]});}		
		updateSelectedList();	
	});
	
	// style the new buttons
	$("button, input:submit").button();

}

function addContentIndicator(data) {
	var list;
	for(i = 0; i < data.length; i++) {
		
		if(i % 2 == 1) {
			list += '<tr class="odd">'; 
		} else {
			list += '<tr>'; 
		}
		
		list += '<td>' + data[i].term + '</td><td class="alignRight">' + data[i].events + '</td><td class="alignRight">'
		+'<button id="choose_' + data[i].id + '_'+data[i].term+'" class="choose_button">Choose</button></td></tr>';
	}
	
	$('#contentindicator-table').empty().append(list);
	
	$(".choose_button").click(function(eventObject) {

		// get the id of this button
		var id = this.id;
			
		var tags = id.split("_");
			
		// add the id to the text file
		//$("#id").val(tags[1]);
		var exists = false;
		for (i in idList){
			if(idList[i].id==tags[1]){
				exists = true;
			}	
		}
		if (!exists){idList.push({"id":tags[1], "name":tags[2]});}		
		updateSelectedList();	
	});
	
	// style the new buttons
	$("button, input:submit").button();

}

function addResSubType(data) {
	var list;
	for(i = 0; i < data.length; i++) {
		
		if(i % 2 == 1) {
			list += '<tr class="odd">'; 
		} else {
			list += '<tr>'; 
		}
		
		list += '<td>' + data[i].description + '</td><td class="alignRight">' + data[i].items + '</td><td class="alignRight">'
		+'<button id="choose_' + data[i].id + '_'+data[i].description+'" class="choose_button">Choose</button></td></tr>';
	}
	
	$('#ressubtype-table').empty().append(list);
	
	$(".choose_button").click(function(eventObject) {

		// get the id of this button
		var id = this.id;
			
		var tags = id.split("_");
			
		// add the id to the text file
		//$("#id").val(tags[1]);
		var exists = false;
		for (i in idList){
			if(idList[i].id==tags[1]){
				exists = true;
			}	
		}
		if (!exists){idList.push({"id":tags[1], "name":tags[2]});}		
		updateSelectedList();	
	});
	
	// style the new buttons
	$("button, input:submit").button();

}

function addRemoveOptions(){
	var current = $("#task").val();
	//if events remove resource sub type option
	//else add it.	
	
	if($('#type').val()=='events'){
		$('#task').removeOption('ressubtype');	
		if (current == 'ressubtype'){$('#task').change();}
	}else $('#task').addOption('ressubtype', 'Resource Sub-Type');	 
	$("#task").val(current);
}
