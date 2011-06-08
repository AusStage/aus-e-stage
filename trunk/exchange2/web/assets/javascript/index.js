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

// initialise the page
$(document).ready(function() {
	// set up the main tabs
	$('#tabs').tabs();
	
	// bring in the extra data
	getIdentifiers();
});

// function to bring in identifiers
function getIdentifiers() {

	// set up the ajax queue
	var ajaxQueue = $.manageAjax.create('IdentifiersAjaxQueue', {
		queue: true
	});
	
	ajaxQueue.add({
		success: addSecGenre,
		url: "lookup?task=secgenre"
	});
	
	ajaxQueue.add({
		success: addContentIndicator,
		url: "lookup?task=contentindicator"
	});
	
	ajaxQueue.add({
		success: addResSubType,
		url: "lookup?task=ressubtype"
	});

}

// a function to add the secondary genre list
function addSecGenre(data, textStatus, xhr, options) {
	
	var list;

	for(i = 0; i < data.length; i++) {
		
		if(i % 2 == 1) {
			list += '<tr class="odd">'; 
		} else {
			list += '<tr>'; 
		}
		
		list += '<td>' + data[i].id + '</td><td>' + data[i].term + '</td><td class="alignRight">' + data[i].events + '</td><td class="alignRight">' + data[i].items + '</td></tr>';
	}
	
	$('#secgenre-table').empty().append(list);
}

// a function to add the content indicator list
function addContentIndicator(data, textStatus, xhr, options) {

var list;

	for(i = 0; i < data.length; i++) {
		
		if(i % 2 == 1) {
			list += '<tr class="odd">'; 
		} else {
			list += '<tr>'; 
		}
		
		list += '<td>' + data[i].id + '</td><td>' + data[i].term + '</td><td class="alignRight">' + data[i].events + '</td><td class="alignRight">' + data[i].items + '</td></tr>';
	}
	
	$('#contentindicator-table').empty().append(list);
}

// a function to add the resource type list
function addResSubType(data, textStatus, xhr, options) {

var list;

	for(i = 0; i < data.length; i++) {
		
		if(i % 2 == 1) {
			list += '<tr class="odd">'; 
		} else {
			list += '<tr>'; 
		}
		
		list += '<td>' + data[i].id + '</td><td>' + data[i].description + '</td><td class="alignRight">' + data[i].items + '</td></tr>';
	}
	
	$('#ressubtype-table').empty().append(list);
}
