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
	this.firstDate = 0;
	this.lastDate  = 0;
	
	// variables to keep track of the time period selected
	this.selectedFirstDate = null;
	this.selectedLastDate  = null;

}

// function to initialise the timeline
TimelineClass.prototype.init = function() {
	$('#timeSlider').bind('valuesChanged', function(event, ui) {
		//timelineObj.updateNetwork(event, ui);
		$.debounce(250, false, viewer.timelineObj.updateNetwork(event, ui)); // wait until 250ms after last scroll before executing
	});
}

// function to update the timeline
TimelineClass.prototype.update = function() {

	// don't do anything if we don't have to
	if(viewer.json.edges.length == 0) {
		
		return;
	}
	//set the start and end dates
	viewer.timelineObj.findDates();

		
	// add the time slider to the page, clearing away any that already exists
	var fdate = viewer.timelineObj.firstDate;
	var ldate = viewer.timelineObj.lastDate;
	
	// adjust the dates
	
	viewer.timelineObj.firstDate.setDate(viewer.timelineObj.firstDate.getDate() - 31);
	viewer.timelineObj.lastDate.setDate(viewer.timelineObj.lastDate.getDate() + 31);
	
	if(viewer.timelineObj.selectedFirstDate == null) {
		viewer.timelineObj.selectedFirstDate = viewer.timelineObj.firstDate;
		viewer.timelineObj.selectedLastDate = viewer.timelineObj.lastDate;
	}
	var options = {bounds: { min: fdate, max: ldate},
				   defaultValues: { min: viewer.timelineObj.selectedFirstDate, max: viewer.timelineObj.selectedLastDate},
				   wheelMode: null,
				   wheelSpeed: 4,
				   arrows: true,
				   valueLabels: 'show',
				   formatter: viewer.timelineObj.dateFormatter,
				   durationIn: 0,
				   durationOut: 400,
				   delayOut: 200
	              };
	$('#timeSlider').dateRangeSlider(options);

}

// find the min and max dates
TimelineClass.prototype.findDates = function(list) {	
	// get the max and min first dates
	viewer.timelineObj.firstDate = new Date(pv.min(viewer.json.edges, function(d) {return d.firstDate}));	
	viewer.timelineObj.lastDate = new Date(pv.max(viewer.json.edges, function(d) {return d.firstDate}));		 
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
TimelineClass.prototype.updateNetwork = function(event, ui) {
	// get the values
	var minDate = ui.values.min;
	var maxDate = ui.values.max;
	
	//minDate = viewer.timelineObj.minDate;
	//maxDate = viewer.timelineObj.maxDate;
	
	// store the values
	viewer.timelineObj.selectedFirstDate = minDate;
	viewer.timelineObj.selectedLastDate  = maxDate;
	
	// update the map
	viewer.refreshGraph('dateRange');	
}