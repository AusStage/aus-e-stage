/*
 * This file is part of the AusStage Navigating Networks Service
 *
 * The AusStage Navigating Networks Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Navigating Networks Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

// complete the form
$(document).ready(function() {

	// define helper variables
	var url = "/networks/lookup?task=system-property&id=export-options";

	// get the data
	$.get(url, function(data, textStatus, XMLHttpRequest) {
	
		var list = data.tasks;
		
		for(var i = 0; i < list.length; i++) {
			$("#task").addOption(list[i], list[i]);
		}
		
		var list = data.formats;
		
		for(var i = 0; i < list.length; i++) {
			$("#format").addOption(list[i], list[i]);
		}
		
		var list = data.radius;
		
		for(var i = 0; i < list.length; i++) {
			$("#radius").addOption(list[i], list[i]);
		}
		
		
		// sort the options
		//$("#myselect2").selectOptions("Value 1");, o
		$("#task").selectOptions("simple-network-undirected");
		$("#format").selectOptions("graphml");
		$("#radius").sortOptions();
		$("#radius").selectOptions("1");
	
	});
});
