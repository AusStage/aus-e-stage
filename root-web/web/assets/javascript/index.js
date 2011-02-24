/*
 * This file is part of the Aus-e-Stage Beta Website
 *
 * The Aus-e-Stage Beta Website is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The Aus-e-Stage Beta Website is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @author corey.wallis@flinders.edu.au
 */
 
// global variables
var BASE_ANALYTICS_URL = 'analytics?report-file=';

// initialise the page
$(document).ready(function() {
	// set up the main tabs
	$('#tabs').tabs();
	
	// setup the analytics tabs
	$('#analytics-tabs').tabs();
	
	// bring in the analytics reports via ajax
	getAnalyticsReports();
});

// function to get the analytics reports 
function getAnalyticsReports() {

	// set up the ajax queue
	var ajaxQueue = $.manageAjax.create('AnalyticsAjaxQueue', {
		queue: true
	});
	
	// get the analytics
	var url = BASE_ANALYTICS_URL + 'mapping-service.xml';
	
	ajaxQueue.add({
		success: addAnalytics,
		url: url
	});
	
	url = BASE_ANALYTICS_URL + 'networks-service.xml';
	
	ajaxQueue.add({
		success: addAnalytics,
		url: url
	});
	
	url = BASE_ANALYTICS_URL + 'mobile-service.xml';
	
	ajaxQueue.add({
		success: addAnalytics,
		url: url
	});
	
	url = BASE_ANALYTICS_URL + 'ausstage-website.xml';
	
	ajaxQueue.add({
		success: addAnalytics,
		url: url
	});
	
	// ausstage database placeholder
	url = BASE_ANALYTICS_URL + 'ausstage-record-count';
	
	ajaxQueue.add({
		success: addAnalytics,
		url: url
	});
	
	url = BASE_ANALYTICS_URL + 'exchange-analytics.xml';
	
	ajaxQueue.add({
		success: addAnalytics,
		url: url
	});
}

// a function to add the analytics into the page
function addAnalytics(data, textStatus, xhr, options) {

	// determine which report we're adding
	if(options.url.indexOf('mapping-service.xml') != -1) {
		$('#analytics-1').empty().append(data);
	} else if(options.url.indexOf('networks-service.xml') != -1) {
		$('#analytics-2').empty().append(data);
	} else if(options.url.indexOf('mobile-service.xml') != -1) {
		$('#analytics-3').empty().append(data);
	} else if(options.url.indexOf('ausstage-website.xml') != -1) {
		$('#analytics-4').empty().append(data);
	} else if(options.url.indexOf('ausstage-record-count') != -1) {
		$('#analytics-5').empty().append(data);
	} else {
		$('#analytics-6').empty().append(data);
	}
}
