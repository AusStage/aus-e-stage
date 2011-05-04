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
 
// define the bookmark class
function BookmarkClass() {
	
	// a variable to store marker data as it is retrieved
	this.data = { contributors:  [],
				  organisations: [],
				  venues:        [],
				  events:        []
				};
}

// initialise the bookmark functionality
BookmarkClass.prototype.init = function() {

	// set up the iconography help dialog
	$("#map_bookmark_div").dialog({ 
		autoOpen: false,
		height: 300,
		width: 400,
		modal: true,
		buttons: {
			Close: function() {
				$(this).dialog('close');
			}
		},
		open: function() {
			// build the legend table rows
			bookmarkObj.buildBookmark();
		},
		close: function() {
			
		}
	});
	
	// set up the iconography help dialog
	$("#map_bookmark_error_div").dialog({ 
		autoOpen: false,
		height: 300,
		width: 400,
		modal: true,
		buttons: {
			Close: function() {
				$(this).dialog('close');
			}
		},
		close: function() {
			
		}
	});
	
	// set up the iconography help dialog
	$("#map_bookmark_loading_div").dialog({ 
		autoOpen: false,
		height: 150,
		width: 400,
		modal: true,
		buttons: {},
		close: function() {}
	});
	
	$(".map-bookmark-open").click(function() {
		$("#map_bookmark_div").dialog('open');
	});
}

// function to build the bookmark link
BookmarkClass.prototype.buildBookmark = function() {

	// get the unique identifiers
	var recordData = mapLegendObj.recordData;
	
	if(recordData == null) {
		$("#map_bookmark_div").dialog('close');
	}
	
	// check to ensure the map isn't too big
	if(mappingObj.markerData.objects.length >= mappingObj.applyClusterLimit) {
		$("#map_bookmark_div").dialog('close');
		$(".mlce_max").empty().append(mappingObj.applyClusterLimit);
		$(".mlce_current").empty().append(mappingObj.markerData.objects.length);
		$("#map_bookmark_error_div").dialog('open');
	}
	
	//build the url
	
	//var uri = "http://beta.ausstage.edu.au" + BASE_URL + "?complex-map=true&amp;";
	
	var uri = "http://localhost:8181" + BASE_URL + "?complex-map=true";
	
	uri += bookmarkObj.buildUriSegment("c", recordData.contributors.ids);
	uri += bookmarkObj.buildUriSegment("o", recordData.organisations.ids);
	uri += bookmarkObj.buildUriSegment("v", recordData.venues.ids);
	uri += bookmarkObj.buildUriSegment("e", recordData.events.ids);
	
	// append the url to the link
	$("#map_bookmark_link").attr('href', uri);
}

// build a URI segment
BookmarkClass.prototype.buildUriSegment = function(ident, ids) {

	ident = "&" + ident + "=";
	
	for(var i = 0; i < ids.length; i++) {
		ident += ids[i] + "-";
	}
	
	if(ids.length > 0) {
		ident = ident.substring(0, ident.length - 1);
	}
	
	return ident;
}

// build a complex map from the link
BookmarkClass.prototype.doComplexMapFromLink = function (c, o, v, e) {

	// keep the user informed
	$("#map_bookmark_loading_div").dialog('open');

	// create a queue
	var ajaxQueue = $.manageAjax.create("mappingBookmarkGatherDataAjaxQueue", {
		queue: true
	});
	
	// create an event listener so we know when it has stopped
	$("#browse_messages").bind('mappingBookmarkGatherDataAjaxQueue' + 'AjaxStop', bookmarkObj.addDataToMap);

	// break up the attributes into arrays
	if(c != null) {
		if(c.indexOf('-') != -1) {
			c = c.split('-');
		}
		
		
		// search for the data on each of the items in turn
		for(var i = 0; i < c.length; i++) {
	
			// build the url
			var url  = BASE_URL + 'markers?type=contributor&id=' + c[i];
		
			ajaxQueue.add({
				success: bookmarkObj.processAjaxData1,
				url: url
			});
		}
	}
	
	if(o != null) {
		if(o.indexOf('-') != -1) {
			o = o.split('-');
		}
		
		for(var i = 0; i < o.length; i++) {
	
			// build the url
			var url  = BASE_URL + 'markers?type=organisation&id=' + o[i];
		
			ajaxQueue.add({
				success: bookmarkObj.processAjaxData2,
				url: url
			});
		}
	}
	
	if(v != null) {
		if(v.indexOf('-') != -1) {
			v = v.split('-');
		}
		
		for(var i = 0; i < v.length; i++) {
	
			// build the url
			var url  = BASE_URL + 'markers?type=venue&id=' + v[i];
		
			ajaxQueue.add({
				success: bookmarkObj.processAjaxData3,
				url: url
			});
		}
	}
	
	if(e != null) {
		if(e.indexOf('-') != -1) {
			e = e.split('-');
		}
		
		for(var i = 0; i < e.length; i++) {
	
			// build the url
			var url  = BASE_URL + 'markers?type=event&id=' + e[i];
		
			ajaxQueue.add({
				success: bookmarkObj.processAjaxData4,
				url: url
			});
		}
	}
}

// functions to process the results of the ajax marker data lookups
BookmarkClass.prototype.processAjaxData1 = function(data) {
	bookmarkObj.data.contributors = bookmarkObj.data.contributors.concat(data);
}

BookmarkClass.prototype.processAjaxData2 = function(data) {
	bookmarkObj.data.organisations = bookmarkObj.data.organisations.concat(data);
}

BookmarkClass.prototype.processAjaxData3 = function(data) {
	bookmarkObj.data.venues = bookmarkObj.data.venues.concat(data);
}

BookmarkClass.prototype.processAjaxData4 = function(data) {
	bookmarkObj.data.events = bookmarkObj.data.events.concat(data);
}

// function to add the data to the map
BookmarkClass.prototype.addDataToMap = function() {

	if(bookmarkObj.data.contributors.length > 0) {
		mappingObj.addContributorData(bookmarkObj.data.contributors, false);
	}
	
	if(bookmarkObj.data.organisations.length > 0) {
		mappingObj.addOrganisationData(bookmarkObj.data.organisations, false);
	}
	
	if(bookmarkObj.data.venues.length > 0) {
		mappingObj.addVenueBrowseData(bookmarkObj.data.venues, false);
	}
	
	if(bookmarkObj.data.events.length > 0) {
		mappingObj.addEventData(bookmarkObj.data.events, false);
	}
	
	// keep the user informed
	$("#map_bookmark_loading_div").dialog('close');
	
	// switch to the map tab including an update
	$('#tabs').tabs('select', 2);
}
