
//a class to embedd ausstage exchange data into web pages. Tested in html pages and wordpress.
//this javascript file will be linked in the page and called to retrieve the data.

function AusstageDataEmbed(id_list,div_id,record_type,search_type,limit,sort_by,style){
	
	var loadingMessage = '<p> Retrieving AusStage records...</p>';
	var serverError = '<p> An error occurred retrieving AusStage Records</p>';
	
	var id = "";
	var names = "";
	var lookup_code="";
	var html;
	var urlStart = 'http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&'	
	//define css style to embed
	var styleString = "<style type='text/css'>\n"
						+"<!-- \n"
						+"/* You can modify these CSS styles */\n"
						+".ausstage-data p{font-size:110%;padding:5px;}"
						+".ausstage-data{list-style:none;border:1px solid grey;padding:0px;margin:5px;font-size: 95%;font-family: Helvetica, Verdana,"
						+" Arial, sans-serif;color:#333;}\n"
						+".off{background-color:#FFF} \n"
						+".on{background-color:#EEE}\n"
						+"-->\n"
						+"</style>\n"
	//define the lookup code - used to point the ausstage url in the right direction regarding the object you're trying to view	
	switch (search_type){
		case 'contributor':
			lookup_code = 'f_contrib_id=';
			break;
		case 'organisation':	
			lookup_code = 'f_org_id=';
			break;
		case 'venue':
			lookup_code = 'f_venue_id=';
			break;
	}
	
	//show loading message
	$("#"+div_id).empty().append(loadingMessage);
	
	//extract the names from the list of names. If appropriate make them links to ausstage records
	for (i in id_list){
		id += id_list[i].id +',';
		if (search_type == 'contentindicator'||search_type == 'secgenre'){
			tempName = id_list[i].name;
		}else {
			tempName = "<a href="+urlStart+lookup_code+id_list[i].id+" target='_blank' title='view this record in Ausstage' >"+id_list[i].name+"</a>";
		}
		names += (i==0)?tempName:(i<id_list.length-1)?', '+tempName:' and '+tempName;
	}
	
	//define the header 
	var header = "<p>"+toTitleCase(record_type)+" for "+names+"</p>";			

	//prepare the url to retrieve the records
	var url = 'http://beta.ausstage.edu.au/exchange/'+record_type+'?type='+search_type+'&id='+id+'&limit='+limit+'&sort='+sort_by
			+'&output=json&callback=?'
	
	//retrieve the records and create the html to display them
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
				if(record_type == 'events'){
					html += '<li class="'+row+'"><a href="'+value.url+'" title="View this record in AusStage">'+value.name+'</a>';
					html += ', '+value.venue+', '+value.date+'</li>'; 	
				}else{
					html += '<li class="'+row+'"><a href="'+value.url+'" title="View this record in AusStage">'+value.title+'</a>, '+value.citation;			
				}
		});
		html += '</ul>';
		//insert the html in the page
		$("#"+div_id).empty().append(html);
	}).error(function(){$('#'+div_id).empty().append(serverError)});
}


//makes the first letter of each word upper case
function toTitleCase(str) {
    return str.replace(/(?:^|\s)\w/g, function(match) {
        return match.toUpperCase();
    });
}