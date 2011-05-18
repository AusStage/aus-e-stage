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


 
// show / hide the menu
$(document).ready(function(){

	// initialise global objects
	searchObj    = new SearchClass();
	browseObj    = new BrowseClass();
	mappingObj   = new MappingClass();
	mapLegendObj = new MapLegendClass();
	timelineObj  = new TimelineClass();
	bookmarkObj  = new BookmarkClass();
	downloadObj  = new DownloadClass();

	/*
	 * page setup
	 */
	 	
	// prevent a FOUC
	$('html').removeClass('js');
	
	// initialise the map legend
	mapLegendObj.init();
	
	// initialise the timeline
	timelineObj.init();
	
	// initialise the bookmark class
	bookmarkObj.init();
	
	// initialise the download class
	downloadObj.init();
	
	// setup the tabs
	$("#tabs").tabs();
	
	// style the buttons
	styleButtons();
	
	// setup the accordian
	$(".accordion").accordion({collapsible:true, active:false, autoHeight: false });
	
	// initialise the search page elements
	searchObj.init();	
	
	// initialise the browse page elements
	browseObj.init();
	
	// initialise the mapping page elements
	mappingObj.init();
	
	// set up the menu hide / show functionality
	$('.peekaboo').click(resizeSidebar);
	
	// set up the iconography help dialog
	$("#help_map_icons_div").dialog({ 
		autoOpen: false,
		height: 600,
		width: 600,
		modal: true,
		buttons: {
			Close: function() {
				$(this).dialog('close');
			}
		},
		open: function() {
			// build the legend table rows
			mappingObj.buildIconographyHelp();			
		},
		close: function() {
			
		}
	});
	
	$('.map-icon-help').click(function() {
		$('#help_map_icons_div').dialog('open');
	});
	
	// check to see if this is a persistent link search request
	searchObj.doSearchFromLink();
	
	// check to see if this is a persistent link map request
	mappingObj.doMapFromLink();
});
