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
function SliderClass() {

	// variables to keep track of minimum amd maximum dates
	this.min = 0;
	this.max  = 0;
	
	// variables to keep track of the time period selected
	this.selectedMin = null;
	this.selectedMax  = null;

}

// function to initialise the timeline
SliderClass.prototype.init = function() {
	$('#collabSlider').bind('valuesChanged', function(event, ui) {
		//do something here - set values
		$.debounce(250, false, viewer.collabSliderObj.updateNetwork(event, ui)); // wait until 250ms after last scroll before executing
	});
}

// function to update the timeline
SliderClass.prototype.update = function(_min, _max, _div) {

	//destroy any existing timline
	$(_div).rangeSlider('destroy');

	// add the slider to the page, clearing away any that already exists
	this.min = _min;
	this.max = _max;
	this.selectedMin = _min;
	this.selectedMax = _max;
		
	var options = {bounds: { min: _min, max: _max},
				   defaultValues: { min: _min, max: _max},
				   wheelMode: null,
				   wheelSpeed: 4,
				   arrows: true,
				   valueLabels: 'show',
				   durationIn: 0,
				   durationOut: 400,
				   delayOut: 200
	              };            
	$(_div).rangeSlider(options);
}

// change the map based on the new values
SliderClass.prototype.updateNetwork = function(event, ui) {
	// get the values
	viewer.hideMin = ui.values.min;
	viewer.hideMax = ui.values.max;
	viewer.render();
	
	// update the map
	viewer.refreshGraph('dateRange');	
}

