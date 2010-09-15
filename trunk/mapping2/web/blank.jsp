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
 	<script type="text/javascript" src="assets/javascript/libraries/jquery.form-2.4.3.js"></script> 
	<script type="text/javascript" src="assets/javascript/libraries/jquery.validate-1.7.min.js"></script>
  	<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script> 
  	
	<!-- Load AusStage JavaScript -->
	 <script type="text/javascript">
		$(function() {
			$("#accordion").tabs("#accordion div.pane", {tabs: 'h2', effect: 'slide', initialIndex: null});
		});
	</script> 
	<script type="text/javascript">
		$(function() {
			// setup ul.tabs to work as tabs for each div directly under div.panes
			$("ul.css-tabs").tabs("div.css-panes > div");
		});
	</script>
	<script type="text/javascript" src="assets/javascript/organisations.js"></script>
   	<!-- <script type="text/javascript" src="assets/javascript/contributors.js"></script> -->
   	<script type="text/javascript" src="assets/javascript/map.functions.js"></script> 	

</head>

<body>

	<div id="header"><h1>AusStage Mapping Service Google Maps v3 Migration</h1></div>
	<div id="inner"> 
		<div id="accordion">
			
				<h2 class="current">Venues</h2>
				<div class="pane">
					<ul class="css-tabs">
						<li><a class="current" href="#tab_location">Location</a></li>
						<li><a class="" href="#tab_events">Events</a></li>			
					</ul>
				
					<div class="css-panes">
						<div>First Tab for location</div>
						<div>Second tab for events</div>
					</div>
					
					
				</div>
			
				<h2>Contributors</h2>
				<div class="pane">
					<ul class="css-tabs">
						<li><a class="current" href="#tab_contr_name">Name</a></li>
						<li><a class="" href="#tab_contr_id">ID</a></li>			
					</ul>
					<div class="css-panes">
						<div>
							<form action="" method="get" id="name_search" name="name_search">
								<input type="hidden" name="task" value="contributor"/>
								<input type="hidden" name="type" value="single"/>
								<input type="text" size="40" id="contributor_name" name="contributor_name"/>
								<input class="ui-state-default ui-corner-all button" type="submit" name="submit" id="name_search_btn" value="Search"/>								
	 						</form> 										

							<!-- <input class="ui-state-default ui-corner-all button" type="button" name="ShowCheckBox" id="ShowCheckBox" value="ShowCheckBox"/> -->

							<div id="contr_name_search_results" style="padding-top: 60px;">																					
							</div>	
																		
						</div>
						<div>
<!-- 	 						
							<form action="search/" method="get" id="id_search" name="id_search">														
								<input type="hidden" name="task" value="contributor"/>
								<input type="hidden" name="type"   value="id"/>														
								<table>
								  <tr><td>
								    <input type="text" size="40" id="query" name="query"/>								  
								  </td></tr>								  
								  <tr><td>
								    <input class="ui-state-default ui-corner-all button" type="submit" name="submit" id="id_search_btn" value="Search"/>
								  </td></tr>
								  <tr>																													
								  </tr>
								  <tr><td>
								    <table id="contr_mapInfo">
									  	<caption>Map of events for Contributor:</caption>	
									  	<tr></tr>
										<tr>
										    <td align="right">Name:</td>
											<td id="contr_name" align="center"></td>										
										</tr>
										<tr>
											<td align="right">ID:</td>
											<td id="contr_id" align="center"></td>
										</tr>
										<tr>
											<td align="right">Total Events Count:</td>
											<td id="contr_total_events_count" align="center"></td>
										</tr>
										<tr>
											<td align="right">Mapped Events Count:</td>
											<td id="contr_mappped_events_count" align="center"></td>
										</tr>
									</table>
								  </td></tr>
								</table>									
							</form>															
							<div id="contr_id_search_results" style="padding-top: 60px;">																					
							</div>   
  -->																						 														
						</div>						
						
					</div>	
				</div>
			
				<h2>Organisations</h2>
				<div class="pane">
					<ul class="css-tabs">
						<li><a class="current" href="#tab_org_name">Name</a></li>
						<li><a class="" href="#tab_org_id">ID</a></li>			
					</ul>
				
					<div class="css-panes">
						<div>
						
 						<form action="" method="get" id="name_search" name="name_search">
								<input type="hidden" name="task" value="organisation"/>
								<input type="hidden" name="type" value="single"/>
								<input type="text" size="40" id="organisation_name" name="organisation_name"/>
								<!-- <input class="ui-state-default ui-corner-all button" type="submit" name="submit" id="name_search_btn" value="Search"/> -->
								<input class="ui-state-default ui-corner-all button" type="button" name="ShowCheckBox" id="ShowCheckBox" value="ShowCheckBox"/>								
 						</form> 										
							
							<div id="name_search_results" style="padding-top: 60px;">																					
							</div>	
																		
						</div>
						
						<div>
							
							<form action="search/" method="get" id="id_search" name="id_search">														
								<input type="hidden" name="task" value="organisation"/>
								<input type="hidden" name="type"   value="id"/>														
								<table>
								  <tr><td>
								    <input type="text" size="40" id="query" name="query"/>
								     </td></tr>								  
								  <tr><td>
								    <input class="ui-state-default ui-corner-all button" type="submit" name="submit" id="id_search_btn" value="Search"/>
								  </td></tr>
								  <tr>																													
								  </tr>
								  <tr><td>
								    <table id="mapInfo">
									  	<caption>Map of events for organisation:</caption>	
									  	<tr></tr>
										<tr>
										    <td align="right">Name:</td>
											<td id="org_name" align="center"></td>										
										</tr>
										<tr>
											<td align="right">ID:</td>
											<td id="org_id" align="center"></td>
										</tr>
										<tr>
											<td align="right">Total Events Count:</td>
											<td id="org_total_events_count" align="center"></td>
										</tr>
										<tr>
											<td align="right">Mapped Events Count:</td>
											<td id="org_mappped_events_count" align="center"></td>
										</tr>
									</table>
								  </td></tr>
								</table>									
							</form>															
							<div id="id_search_results" style="padding-top: 60px;">																					
							</div>   															 														
						</div>
					</div>											
					
				</div>
		</div>
		
		<div id="main">					
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