/*
 * This file is part of the Aus-e-Stage Beta Root Website
 *
 * The Aus-e-Stage Beta Root Website is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The Aus-e-Stage Beta Root Website is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Aus-e-Stage Beta Root Website.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

// setup the analytics tabs
$(document).ready(function() {
	$("#analytics-tabs").tabs({ 
		cookie: { 
			name: 'root_analytics_tab', 
			expires: 30
		}
	}); 
});

// set up the tabbed interface
$(document).ready(function() {
	$("#tabs").tabs({ 
		cookie: { 
			name: 'root_main_tab', 
			expires: 30
		}
	}); 
});

// load the google api
google.load("feeds", "1");

// load the delicious feed
google.setOnLoadCallback(load_delicious);

// load the delicious rss feed
function load_delicious() {

	// empty the delicious feed div
	$("#delicious_feed").empty();

	// Create a feed control
	var feedControl = new google.feeds.FeedControl();
	
	// add the feed url
	feedControl.addFeed("http://feeds.delicious.com/v2/rss/tag/ausstage?count=15", "");
	
	// set the number of items to display
	feedControl.setNumEntries(10);

	// Draw it.
	feedControl.draw(document.getElementById("delicious_feed"));
}

// get the Exchange Service Analytics
$(document).ready(function() {

	// set up the ajax queue
	var ajaxQueue = $.manageAjax.create("RootWebAjaxQueue", {
		queue: true
	});
	
	// build the request
	var url = "analytics?report-file=exchange-analytics.xml";
	
	// queue this request
	ajaxQueue.add({
		success: exchangeAnalytics,
		url: url
	});
	
	// build the request
	url = "analytics?report-file=mapping-service.xml";
	
	// queue this request
	ajaxQueue.add({
		success: mappingAnalytics,
		url: url
	});
	
	// build the request
	url = "analytics?report-file=networks-service.xml";
	
	// queue this request
	ajaxQueue.add({
		success: networksAnalytics,
		url: url
	});
	
	// build the request
	url = "analytics?report-file=mobile-service.xml";
	
	// queue this request
	ajaxQueue.add({
		success: mobileAnalytics,
		url: url
	});
	
	// build the request
	url = "analytics?report-file=ausstage-website.xml";
	
	// queue this request
	ajaxQueue.add({
		success: ausstageAnalytics,
		url: url
	});
	
});

// function to add the content to the page
function exchangeAnalytics(data) {
	$("#analytics-1").empty().append(data);
}

// function to add the content to the page
function mappingAnalytics(data) {
	$("#analytics-2").empty().append(data);
}

// function to add the content to the page
function networksAnalytics(data) {
	$("#analytics-3").empty().append(data);
}

// function to add the content to the page
function mobileAnalytics(data) {
	$("#analytics-4").empty().append(data);
}

// function to add the content to the page
function ausstageAnalytics(data) {
	$("#analytics-5").empty().append(data);
}
