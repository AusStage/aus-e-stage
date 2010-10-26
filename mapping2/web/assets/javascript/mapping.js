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
	// add a click handler to the hide menu link
	$("#hidePanel").click(function(){
		$("#panel").animate({marginLeft:"-175px"}, 500 );
		$("#sidebar").animate({width:"0px", opacity:0}, 400 );
		$("#showPanel").show("normal").animate({width:"18px", opacity:1}, 200);
		$("#main").animate({marginLeft:"30px"}, 500);
		$("#tabs").animate({width:"100%"});
	});
	// add a click handler to the show menu link
	$("#showPanel").click(function(){
		$("#main").animate({marginLeft:"200px"}, 200);
		$("#tabs").animate({width:"100%"});
		$("#panel").animate({marginLeft:"0px"}, 400 );
		$("#sidebar").animate({width:"175px", opacity:1}, 400 );
		$("#showPanel").animate({width:"0px", opacity:0}, 600).hide("slow");
	});
	
	// set the tabs
	$("#tabs").tabs();
	
	// style the buttons
	$("button, input:submit").button();
	
	// associate the tipsy library with the form elements
	$('#search [title]').tipsy({trigger: 'focus', gravity: 'n'});
});
 
// setup the analytics tabs
$(document).ready(function() {
	
});

// common theme actions
$(document).ready(function() {
	
});
