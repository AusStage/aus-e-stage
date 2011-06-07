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
 

// download plugin from: http://www.filamentgroup.com/lab/jquery_plugin_for_requesting_ajax_like_file_downloads/

jQuery.download = function(url, data, method){
	//url and data options required
	if( url && data ){ 
		//data can be string of parameters or array/object
		data = typeof data == 'string' ? data : jQuery.param(data);
		//split params into form inputs
		var inputs = '';
		jQuery.each(data.split('&'), function(){ 
			var pair = this.split('=');
			inputs+='<input type="hidden" name="'+ pair[0] +'" value="'+ pair[1] +'" />'; 
		});
		//send request
		jQuery('<form action="'+ url +'" method="'+ (method||'post') +'">'+inputs+'</form>')
		.appendTo('body').submit().remove();
	};
};
 
// define the bookmark class
function DownloadClass() {
	
}

// initialise the bookmark functionality
DownloadClass.prototype.init = function() {

	$('.map-kml-download').click(function () {
		downloadObj.startDownload()
	});
	
}

DownloadClass.prototype.startDownload = function() {

	var uri = BASE_URL + "kml/";

	// build the data
	var recordData = mapLegendObj.recordData;
	
	if(recordData == null) {
		alert("Map contains no data, aborting download");
	}

	var data = bookmarkObj.buildUriSegment("contributors", recordData.contributors.ids);
	data += bookmarkObj.buildUriSegment("organisations", recordData.organisations.ids);
	data += bookmarkObj.buildUriSegment("venues", recordData.venues.ids);
	data += bookmarkObj.buildUriSegment("events", recordData.events.ids);
	
	data = data.substring(1, data.length);
	
	// start the download
	$.download(uri, data, 'post');
	
}

DownloadClass.prototype.buildIdList = function(ids) {

	var list = "";
	
	for(var i = 0; i < ids.length; i++) {
		list += ids[i] + ",";
	}
	
	if(ids.length > 0) {
		list = list.substring(0, list.length - 1);
	}
	
	return list;
}
