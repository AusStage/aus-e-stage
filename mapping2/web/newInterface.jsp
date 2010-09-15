<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%
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
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml" lang="en">
<head>
	<title>AusStage Mapping Service - Persistent Link Map</title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/> 
 	<link rel="stylesheet" type="text/css" media="screen" href="assets/main-style.css"/>
	<link rel="stylesheet" type="text/css" media="screen" href="assets/tabs-accordion.css"/>
  	<link rel="stylesheet" type="text/css" media="screen" href="assets/jquery-ui/jquery-ui-1.8.4.custom.css"/> 
	<link rel="stylesheet" type="text/css" media="screen" href="assets/jquery.selecttouislider-2.0.css"/> 
  	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.4.2.min.js"></script>
  	<script type="text/javascript" src="assets/javascript/libraries/jquery.selectboxes-2.2.4.min.js"></script> 
    <script type="text/javascript" src="assets/javascript/libraries/jquery-ui-1.8.4.custom.min.js"></script> 
  	<script type="text/javascript" src="assets/javascript/libraries/jquery.selecttouislider-2.0.js"></script> 
	<script type="text/javascript" src="assets/javascript/libraries/jquery.tools.min.js"></script>	
  	<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
	<!-- Load AusStage JavaScript -->
<!--    	<script type="text/javascript" src="assets/javascript/maplinks.js"></script> -->
<!--   	<script type="text/javascript" src="assets/javascript/map.functions.js"></script> -->
	
	<script type="text/javascript">
		$(function(){
		/* $("#accordion").tabs("#accordion div.pane", {tabs: 'h2', event: 'mouseover', effect: 'fade', initialIndex: null}); */
			$("#accordion").tabs("#accordion div.pane", {tabs: 'h2', effect: 'slide', initialIndex: null});
		});
			// add new effect to the tabs
		$.tools.tabs.addEffect("slide", function(i, done) {

 			// 1. upon hiding, the active pane has a ruby background color
 			this.getPanes().slideUp().css({backgroundColor: "#FFFFCC"});

			// 2. after a pane is revealed, its background is set to its original color (transparent)
			this.getPanes().eq(i).slideDown(function() {
 				$(this).css({backgroundColor: 'transparent'});

				 // the supplied callback must be called after the effect has finished its job
 				done.call();
 			}); 
		}); 			
	</script>
	
	<script type="text/javascript">		
		$(function() {
			$("ul.css-tabs").tabs("div.css-panes > div", {effect: 'ajax'});			
		}); 
	</script>

	
</head>
<body>
  
		<div id="header"><h1>AusStage Mapping Service Google Maps v3 Migration</h1></div>
		
	 	<div id="inner"> 
				<div id="accordion">
			
					<h2 class="current">Venues</h2>
					<div class="pane">
						<ul class="css-tabs">
							<li><a class="current" href="location.html">Location</a></li>
							<li><a class="" href="events.html">Events</a></li>			
						</ul>
					
						<div class="css-panes">
							<div style="display: block;"></div>
						</div>
						
						
					</div>
				
					<h2>Contributors</h2>
					<div class="pane">... pane content ...</div>
				
					<h2>Organisations</h2>
					<div class="pane">
						<ul class="css-tabs">
							<li><a class="current" href="org_name_search.html">Name</a></li>
							<li><a class="" href="org_id_search.html">ID</a></li>			
						</ul>
					
						<div class="css-panes">
							<div style="display: block;">
							
							</div>
						</div>											
						
					</div>
			
				</div>
 			
				<div id="main">
					<h2 id="map_name">Map of events for: </h2>
					
					<div id="map"></div>
					
					<div id="map_footer" class="map_header_footer">
						
						<form action="" method="get" id="adv_map_display_form" name="adv_map_display_form">
						<table class="formTable" width="100%">
							<tr id="time_slider_option_row_1">
								<th scope="row">Show events that occured between:</th>
							</tr>
							<tr id="time_slider_option_row_2">
								<td width="80%">
									
										<label for="event_finish" class="tohide">First Date: </label>
											<select name="event_start" id="event_start" size="1" class="slider"></select>
										<label for="event_start" class="tohide">Last Date: </label>
											<select name="event_finish" id="event_finish" size="1" class="slider"></select>									
								</td>
								<td width="10%">
									<input class="ui-state-default ui-corner-all button" type="button" name="reload_map" id="reload_map" value="Reload"/>
								</td>
								<td width="10%">	
									<input class="ui-state-default ui-corner-all button" type="button" name="reset_map" id="reset_map" value="Reset"/>
								</td>
							</tr>													
						</table>
						</form>
					</div>
					
				</div> 
	 	</div>

</body>
</html>
