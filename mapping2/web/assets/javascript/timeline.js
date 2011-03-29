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

}

// function to initialise the timeline
TimelineClass.prototype.init = function() {

}

// function to update the timeline
TimelineClass.prototype.update = function() {

	// get the marker data
	var markers = mappingObj.markerData.objects;
	
	// loop through the markers looking for the lowest fDate an the highest lDate
	for(var i = 0; i < markers.length; i++) {
		if(markers[i].contributors.length > 0) {
			timelineObj.findDates(markers[i].contributors, 1);
		}
		
		if(markers[i].organisations.length > 0) {
			timelineObj.findDates(markers[i].organisations, 1);
		}
	}
	
	console.log(timelineObj.firstDate);
	console.log(timelineObj.lastDate);

}

// find the lowest fDate and highest lDate in the array
TimelineClass.prototype.findDates = function(list, type) {

	var obj;

	if(type == 1) {
		// find the fDate and lDate and compare
		for(var i = 0; i < list.length; i++) {
			obj = list[i];
		
			if(obj.venueObj.minEventDate < timelineObj.firstDate) {
				timelineObj.firstDate = obj.venueObj.minEventDate;
			}
		
			if(obj.venueObj.maxEventDate > timelineObj.lastDate) {
				timelineObj.lastDate = obj.venueObj.maxEventDate;
			}
		}
	}
}
