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
 
// define the map legend class
function MapLegendClass() {

}

// function to hide the legend
MapLegendClass.prototype.hideLegend = function() {
	$('.mapLegendContainer').hide();
}

// initialise the map legend
MapLegendClass.prototype.init = function() {
	mapLegendObj.hideLegend();
	
	// update and show the legend when the map is shown
	$('#tabs').bind('tabsshow', function(event, ui) {
		if (ui.panel.id != "tabs-3") { // tabs-3 == the map tab
			// update the map legend
			mapLegendObj.hideLegend();
		}
	});
}

// function to show the legend
MapLegendClass.prototype.showLegend = function() {
	mapLegendObj.updateLegend();
	$('.mapLegendContainer').show();
}

// function to update the legend
MapLegendClass.prototype.updateLegend = function() {

	// declare helper variables
	var tableData = "";
	var objects   = null;
	var obj       = null;
	var idx       = null;
	var tmp       = null;
	
	// get the data used to build the legend
	var recordData = mapLegendObj.buildRecordData();
	
	// build the map legend	
	if(recordData.contributors.objects.length > 0) {
		// add the contributors
		objects = recordData.contributors.objects;
		
		// reset the tableData variable
		tableData = '<table id="mapLegendContributors" class="mapLegendTable">';
		
		// loop through the list of objects
		for(var i = 0; i < objects.length; i++) {
		
			// colour the odd rows
			if(i % 2 == 1) {
				tableData += '<tr class="odd">'; 
			} else {
				tableData += '<tr>'; 
			}
			
			obj = objects[i];
			idx = $.inArray(obj.id, mappingObj.contributorColours.ids);
			
			// add the icon
			tableData += '<td class="mapLegendIcon"><span class="' + mappingObj.contributorColours.colours[idx] + ' mapLegendIconImg"><img src="' + mapIconography.contributor + '" width="' + mapIconography.iconWidth + '" height="' + mapIconography.iconHeight + '"/></span></td>';
			
			// build a tmp variable containing the name
			tmp = obj.firstName + ' ' + obj.lastName;
			tmp = tmp.replace(/\s/g, '&nbsp;');
			
			// add the name and functions
			tableData += '<td class="mapLegendInfo"><a href="' + obj.url + '" target="_ausstage">' + tmp + '</a><br/>';
			
			// add the functions
			if(obj.functions.length != 0) {
			
				for(var y = 0; y < obj.functions.length; y++ ){
					tableData += obj.functions[y] + ', ';
				}
		
				tableData = tableData.substr(0, tableData.length -2);
			}
			
			// add the show/hide check box
			tableData += '</td><td class="mapLegendShowHide"><input type="checkbox" name="mapLegendContributor" class="mapLegendShowHideContributor use-tipsy" value="' + obj.id + '" title="Tick to hide this contributor"/></td>';
			
			// add the delete icon
			tableData += '<td class="mapLegendDelete"><span id="mapLegendDeleteIcon" class="ui-icon ui-icon-closethick clickable use-tipsy" style="display: inline-block;" title="Delete Contributor from Map"></span></td>';
			
			// finsih the row
			tableData += '</tr>';		
		}
		
		// finish the table and add it to the page
		tableData += '</table>';
		$('#mapLegendContributors').empty().append(tableData);
		
		if(objects.length > 0) {
			$('#mapLegendContributorHeading').empty().append('Contributors (' + objects.length +')');
		}
	}
	
	if(recordData.organisations.objects.length > 0) {
		// add the organisations
		objects = recordData.organisations.objects;
		
		// reset the tableData variable
		tableData = '<table id="mapLegendOrganisations" class="mapLegendTable">';
		
		// loop through the list of objects
		for(var i = 0; i < objects.length; i++) {
		
			// colour the odd rows
			if(i % 2 == 1) {
				tableData += '<tr class="odd">'; 
			} else {
				tableData += '<tr>'; 
			}
			
			obj = objects[i];
			idx = $.inArray(obj.id, mappingObj.organisationColours.ids);
			
			// add the icon
			tableData += '<td class="mapLegendIcon"><span class="' + mappingObj.organisationColours.colours[idx] + ' mapLegendIconImg"><img src="' + mapIconography.organisation + '" width="' + mapIconography.iconWidth + '" height="' + mapIconography.iconHeight + '"/></span></td>';
			
			// build a tmp variable containing the name
			//tmp = obj.name;
			//tmp = tmp.replace(/\s/g, '&nbsp;');
			
			// add the name and functions
			tableData += '<td class="mapLegendInfo"><a href="' + obj.url + '" target="_ausstage">' + obj.name + '</a><br/>';
			
			// add the address
			tableData += mappingObj.buildAddressAlt(obj.suburb, obj.state, obj.country);
			
			// add the show/hide check box
			tableData += '</td><td class="mapLegendShowHide"><input type="checkbox" name="mapLegendOrganisation" class="mapLegendShowHideOrganisation use-tipsy" value="' + obj.id + '" title="Tick to hide this organisation"/></td>';
			
			// add the delete icon
			tableData += '<td class="mapLegendDelete"><span id="mapLegendDeleteIcon" class="ui-icon ui-icon-closethick clickable use-tipsy" style="display: inline-block;" title="Delete Organisation from Map"></span></td>';
			
			// finsih the row
			tableData += '</tr>';		
		}
		
		// finish the table and add it to the page
		tableData += '</table>';
		$('#mapLegendOrganisations').empty().append(tableData);
		
		if(objects.length > 0) {
			$('#mapLegendOrganisationHeading').empty().append('Organisations (' + objects.length +')');
		}
		
	}
	
	if(recordData.venues.objects.length > 0) {
		// add the venues
		
		/*
		if(objects.length > 0) {
			$('#mapLegendVenueHeading').empty().append('Venues (' + objects.length +')';
		}
		*/
	}
	
	if(recordData.events.objects.length > 0) {
		// add the events
		
		/*
		if(objects.length > 0) {
			$('#mapLegendEventsHeading').empty().append('Events (' + objects.length +')';
		}
		*/
	}
}

// a function to build the internal legend specific datastructure
MapLegendClass.prototype.buildRecordData = function() {

	// get the map data
	var mapData = mappingObj.markerData.objects;
	var contributors;
	var organisations;
	var venues;
	var events;
	var idx;
	
	// build our own legend specific dataset
	var recordData = {contributors:  {ids: [], objects: []},
					  organisations: {ids: [], objects: []},
					  venues:        {ids: [], objects: []},
					  events:        {ids: [], objects: []}
					 };
					 
	// loop through the marker data
	for (var i = 0; i < mapData.length; i++) {
	
		// get the list of contributors
		contributors = mapData[i].contributors;
		
		// process these contributors
		if(contributors.length > 0) {
		
			for(var x = 0; x < contributors.length; x++) {
				idx = $.inArray(contributors[x].id, recordData.contributors.ids);
				if(idx == -1) {
					recordData.contributors.ids.push(contributors[x].id);
					recordData.contributors.objects.push(contributors[x]);
				}
			}		
		}
		
		// get the list of organisations
		organisations = mapData[i].organisations;
		
		// process these contributors
		if(organisations.length > 0) {
		
			for(var x = 0; x < organisations.length; x++) {
				idx = $.inArray(organisations[x].id, recordData.organisations.ids);
				if(idx == -1) {
					recordData.organisations.ids.push(organisations[x].id);
					recordData.organisations.objects.push(organisations[x]);
				}
			}		
		}
		
		// get the list of venues
		venues = mapData[i].venues;
		
		// process these contributors
		if(venues.length > 0) {
		
			for(var x = 0; x < venues.length; x++) {
				idx = $.inArray(venues[x].id, recordData.venues.ids);
				if(idx == -1) {
					recordData.venues.ids.push(venues[x].id);
					recordData.venues.objects.push(venues[x]);
				}
			}		
		}
		
		// get the list of venues
		events = mapData[i].events;
		
		// process these contributors
		if(events.length > 0) {
		
			for(var x = 0; x < events.length; x++) {
				idx = $.inArray(events[x].id, recordData.events.ids);
				if(idx == -1) {
					recordData.events.ids.push(events[x].id);
					recordData.events.objects.push(events[x]);
				}
			}		
		}	
	}
	
	// sort the arrays
	recordData.contributors.objects.sort(sortContributorArrayAlt);
	recordData.organisations.objects.sort(sortOrganisationArrayAlt);
	recordData.venues.objects.sort(sortVenueArray);
	recordData.events.objects.sort(sortEventArray);
	
	// update the id indexes
	if(recordData.contributors.objects.length > 0) {
		recordData.contributors.ids = [];
		for(var i = 0; i < recordData.contributors.objects.length; i++) {
			recordData.contributors.ids.push(recordData.contributors.objects[i].id);
		}
	}
	
	if(recordData.organisations.objects.length > 0) {
		recordData.organisations.ids = [];
		for(var i = 0; i < recordData.organisations.objects.length; i++) {
			recordData.organisations.ids.push(recordData.organisations.objects[i].id);
		}
	}
	
	if(recordData.venues.objects.length > 0) {
		recordData.venues.ids = [];
		for(var i = 0; i < recordData.venues.objects.length; i++) {
			recordData.venues.ids.push(recordData.venues.objects[i].id);
		}
	}
	
	if(recordData.events.objects.length > 0) {
		recordData.events.ids = [];
		for(var i = 0; i < recordData.events.objects.length; i++) {
			recordData.events.ids.push(recordData.events.objects[i].id);
		}
	}
	
	return recordData;
}
