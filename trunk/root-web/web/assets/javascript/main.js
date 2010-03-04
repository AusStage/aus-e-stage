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

// load the zotero feed
google.setOnLoadCallback(load_zotero);

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

//https://api.zotero.org/groups/12290/items

// load the delicious rss feed
function load_zotero() {

	// empty the delicious feed div
	$("#zotero_feed").empty();

	// Create a feed control
	var feedControl = new google.feeds.FeedControl();
	
	// add the feed url
	feedControl.addFeed("https://api.zotero.org/groups/12290/items/top", "");
	
	// set the number of items to display
	feedControl.setNumEntries(10);

	// Draw it.
	feedControl.draw(document.getElementById("zotero_feed"));
}

// get the Exchange Service Analytics
$(document).ready(function() {
	
	// get the exchange data analytics information
	$.get("analytics?type=exchange", function(html) {
		$("#analytics-1").empty();
		$("#analytics-1").append(html);
	});
});
