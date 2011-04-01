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

// define the timeline class
function TimelineClass() {

	// variables to keep track of minimum amd maximum dates
	this.firstDate = 99999999;
	this.lastDate  = 0;
	
	// variables to keep track of the time period selected
	this.selectedFirstDate = -1;
	this.selectedLastDate  = -1;

}

// function to initialise the timeline
TimelineClass.prototype.init = function() {

}

// function to update the timeline
TimelineClass.prototype.update = function() {

	// get the marker data
	var markers = mappingObj.markerData.objects;
	
	// don't do anything if we don't have to
	if(markers.length == 0) {
		$('#timeSlider').empty();
		return;
	}
	
	// loop through the markers looking for the lowest fDate an the highest lDate
	for(var i = 0; i < markers.length; i++) {
		if(markers[i].contributors.length > 0) {
			timelineObj.findDates(markers[i].contributors, 1);
		}
		
		if(markers[i].organisations.length > 0) {
			timelineObj.findDates(markers[i].organisations, 1);
		}
		
		if(markers[i].venues.length > 0) {
			timelineObj.findDates(markers[i].venues, 2);
		}
		
		if(markers[i].events.length > 0) {
			timelineObj.findDates(markers[i].events, 3);
		}
	}
	
	// add the time slider to the page, clearing away any that already exists
	var fdate = timelineObj.getDateFromInt(timelineObj.firstDate);
	var ldate = timelineObj.getDateFromInt(timelineObj.lastDate);
	var options = {bounds: { min: fdate, max: ldate},
				   defaultValues: { min: fdate, max: ldate},
				   wheelMode: null,
				   wheelSpeed: 4,
				   arrows: true,
				   valueLabels: 'show',
				   formatter: timelineObj.dateFormatter,
				   durationIn: 0,
				   durationOut: 400,
				   delayOut: 200
	              };
	$('#timeSlider').dateRangeSlider(options);
	$('#timeSlider').dateRangeSlider('values', fdate, ldate);
	$('#timeSlider').bind('valuesChanged', function(event, ui) {
		timelineObj.updateMarkers(event, ui);
	});

}

// find the lowest fDate and highest lDate in the array
TimelineClass.prototype.findDates = function(list, type) {

	var obj;

	if(type == 1) {
		// find the fDate and lDate and compare
		for(var i = 0; i < list.length; i++) { // contributors and organisations
			obj = list[i];
		
			if(obj.venueObj.minEventDate < timelineObj.firstDate) {
				timelineObj.firstDate = obj.venueObj.minEventDate;
			}
		
			if(obj.venueObj.maxEventDate > timelineObj.lastDate) {
				timelineObj.lastDate = obj.venueObj.maxEventDate;
			}
		}
	} else if(type == 2) { // venues
	
		// find the fDate and lDate and compare
		for(var i = 0; i < list.length; i++) {
			obj = list[i];
		
			if(obj.minEventDate < timelineObj.firstDate) {
				timelineObj.firstDate = obj.minEventDate;
			}
		
			if(obj.maxEventDate > timelineObj.lastDate) {
				timelineObj.lastDate = obj.maxEventDate;
			}
		}
	} else if(type == 3) { // events
	
		// find the fDate and lDate and compare
		for(var i = 0; i < list.length; i++) {
			obj = list[i];
		
			if(obj.sortFirstDate < timelineObj.firstDate) {
				timelineObj.firstDate = obj.sortFirstDate;
			}
		
			if(obj.sortLastDate > timelineObj.lastDate) {
				timelineObj.lastDate = obj.sortLastDate;
			}
		}
	}
	
	// reset the selected dates
	timelineObj.selectedFirstDate = timelineObj.firstDate;
	timelineObj.selectedLastDate  = timelineObj.lastDate;
}

// convert the sort date value into a date
TimelineClass.prototype.getDateFromInt = function(value) {

	var tokens = [];
	
	value = value.toString();
	
	tokens[0] = value.substr(0, 4);
	tokens[1] = value.substr(4, 2);
	tokens[2] = value.substr(6, 2);
	
	return new Date(tokens[0], tokens[1], tokens[2]);
	
}

// format the dates to the AusStage style
TimelineClass.prototype.dateFormatter = function(value) {

	var tokens = [];
	
	tokens[0] = value.getDate();
	tokens[1] = value.getMonth() + 1;
	tokens[2] = value.getFullYear();
	
	tokens[1] = lookupMonthFromInt(tokens[1]);
	
	return tokens[0] + " " + tokens[1] + " " + tokens[2];
}

// change the map based on the new values
TimelineClass.prototype.updateMarkers = function(event, ui) {

	// get the values
	var minDate = ui.values.min;
	var maxDate = ui.values.max;
	
	minDate = timelineObj.DateToInt(minDate.getFullYear(), minDate.getMonth(), minDate.getDate());
	maxDate = timelineObj.DateToInt(maxDate.getFullYear(), maxDate.getMonth(), maxDate.getDate());
	
	// store the values
	timelineObj.selectedFirstDate = minDate;
	timelineObj.selectedLastDate  = maxDate;
	
	// update the map
	mappingObj.updateMap();	
}

// turn a date back into the int representation
TimelineClass.prototype.DateToInt = function(year, month, day) {

	var tokens = [];
	tokens[0] = "" + year
	tokens[1] = "" + (month + 1);
	tokens[2] = "" + day
	
	if(tokens[1].length == 1) {
		tokens[1] = "0" + tokens[1];
	}
	
	if(tokens[2].length == 1) {
		tokens[2] = "0" + tokens[2];
	}
	
	return tokens[0] + "" + tokens[1] + "" + tokens[2];
}
