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
	<link rel="stylesheet" type="text/css" media="screen" href="assets/jquery-ui/jquery-ui-1.8.4.custom.css"/>
	<link rel="stylesheet" type="text/css" media="screen" href="assets/jquery.selecttouislider-2.0.css"/>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.4.2.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.selectboxes-2.2.4.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-ui-1.8.4.custom.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.selecttouislider-2.0.js"></script>
	<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"/>
</head>
<body>
<div id="wrap">
	<div id="header"><h1>AusStage Mapping Service Google Maps v3 Migration</h1></div>
	<div id="nav">
	</div>
	<!-- Include the sidebar -->
	<jsp:include page="sidebar.jsp"/>
	<div id="main">
		<h2>Map of events for: </h2>
		<div id="map_header" class="featured_map_header_footer map_header_footer">
			<h3 id="map_name"></h3>
		</div>
		<div id="map">
		</div>
		<div id="map_footer" class="map_header_footer">
			<h3>Advanced Display Options</h3>
			<form action="" method="get" id="adv_map_display_form" name="adv_map_display_form">
			<table class="formTable" width="100%">
				<tr id="time_slider_option_row_1">
					<th scope="row">Show events that occured between:</th>
				</tr>
				<tr id="time_slider_option_row_2">
					<td>
						<fieldset>

							<label for="event_finish" class="tohide">First Date: </label>
								<select name="event_start" id="event_start" size="1" class="slider"></select>
							<label for="event_start" class="tohide">Last Date: </label>
								<select name="event_finish" id="event_finish" size="1" class="slider"></select>
						</fieldset>
					</td>
				</tr>
				<tr id="state_option_row">
					<th scope="row">
					<label id="state_label" for="state" class="#cluetip_state" style="cursor: help;">Only show venues in: </label>
						<select size="1" id="state" name="state">
							<option value="nolimit" selected="selected">No Limit - All venues</option>
							<option value="a">Australia</option>
							<option value="7"> - Australian Capital Territory</option>
							<option value="3"> - New South Wales</option>
							<option value="8"> - Northern Territory</option>
							<option value="4"> - Queensland</option>
							<option value="1"> - South Australia</option>
							<option value="5"> - Tasmania</option>
							<option value="6"> - Victoria</option>
							<option value="2"> - Western Australia</option>
							<option value="9">Outside Australia</option>
						</select>
					</th>
				</tr>
				<tr>
					<td>
						<input class="ui-state-default ui-corner-all button" type="button" name="reload_map" id="reload_map" value="Reload Map"/>
					</td>
				</tr>
			</table>
			</form>
		</div>
		<div id="map_legend">
			<table class="mapLegend">
				<thead>
					<tr>
						<th colspan="2">Map Legend</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<th style="background: #CCBAD7; width: 20px;" scope="row">&nbsp;</th>
						<td>Markers this colour indicate only 1 event</td>
					</tr>
					<tr>
						<th style="background: #9A7BAB; width: 20px;" scope="row">&nbsp;</th>
						<td>Markers this colour indicate between 2 &amp; 5 events</td>
					</tr>
					<tr>
						<th style="background: #7F649B; width: 20px;" scope="row">&nbsp;</th>
						<td>Markers this colour indicate between 6 &amp; 15 events</td>
					</tr>
					<tr>
						<th style="background: #69528E; width: 20px;" scope="row">&nbsp;</th>
						<td>Markers this colour indicate between 16 &amp; 30 events</td>
					</tr>
					<tr>
						<th style="background: #4D3779; width: 20px;" scope="row">&nbsp;</th>
						<td>Markers this colour indicate more than 30 events</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
	<!-- include the footer -->
	<jsp:include page="footer.jsp"/>
</div>
<!-- include the Google Analytics code -->
<jsp:include page="analytics.jsp"/>
</body>
</html>
