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
	<title>AusStage Mapping Service (Beta)</title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/> 
	<link rel="stylesheet" type="text/css" media="screen" href="assets/main-style.css" />
	<link rel="stylesheet" type="text/css" media="screen" href="assets/jquery-ui/jquery-ui-1.7.2.custom.css"/>
	<link rel="stylesheet" type="text/css" media="screen" href="assets/jquery-ui/ui.slider.extras.css"/>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.3.2.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.selectboxes-2.2.4.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-ui-1.7.2.custom.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/selectToUISlider.jQuery.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.cookie-1.0.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.bgiframe.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.scrollTo-min.js"></script>	
	<script type="text/javascript" src="assets/javascript/browse.js"></script>
	<%
		ServletContext context = getServletContext();
		String mapsAPI   = (String)context.getInitParameter("googleMapsAPIUrl");
	%>
	<script type="text/javascript" src="<%=mapsAPI%>"></script>
	<script type="text/javascript" src="assets/javascript/libraries/mapiconmaker-1.1.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/markerclusterer-1.0.js"></script>
	<script src="http://static.simile.mit.edu/timeline/api-2.3.0/timeline-api.js?bundle=true" type="text/javascript"></script>
</head>
<body>
<div id="wrap">
	<div id="header"><h1>AusStage Mapping Service (Beta) - Browse Map of Venues</h1></div>
	<div id="nav">
	</div>
	<!-- Include the sidebar -->
	<!--<jsp:include page="sidebar.jsp"/>-->
	<div id="main" style="width: 980px;">
		<h2>Browse AusStage event data by Venue</h2>
		<p>
			 Use the map below to browse venues and associated events in AusStage.
		</p>
		<div id="map_header" class="map_header_footer">
			<h3></h3>
			<ul class="map_links">
				<li><a href="/mapping/" title="AusStage Mapping Service Homepage">Mapping Service Home</a></li>
				<li><a href="/mapping/browse?action=kml" title="Download KML file" id="map_header_kml">Download KML version of this map</a></li>
			</ul>
		</div>
		<div id="map" style="width: 975px;">
		</div>
		<div id="map_footer" class="map_header_footer">
			<h3>Advanced Display Options</h3>
			<form action="" method="" id="adv_map_display_form" name="adv_map_display_form">
			<table class="formTable" width="100%">
				<tr>
					<th scop="row" colspan="2">Show venues with events that occured between:</th>
				</tr>
				<tr>
					<td colspan="2">
						<fieldset>
							<label for="event_start" style="display: none;">Last Date: </label>
								<select name="event_start" id="event_start" size="1" class="slider" style="display: none;"></select>
							<label for="event_finish" class="tohide" style="display: none;">First Date: </label>
								<select name="event_finish" id="event_finish" size="1" class="slider" style="display: none;"></select>
						</fieldset>
					</td>
				</tr>
				<tr>
					<th scope="row">
						<label id="state_label" for="state" class="#cluetip_state" style="cursor: help;">Only show venues in: </label>
						<select size="1" id="state" name="state">
							<option value="nolimit" selected="selected">No Limit - All venues</option>
							<option value="a">Australia</option>
							<option value="7">&nbsp;- Australian Capital Territory</option>
						   <option value="7a">&nbsp;&nbsp;&nbsp;- Canberra</option>							
							<option value="3">&nbsp;- New South Wales</option>
						   <option value="3a">&nbsp;&nbsp;&nbsp;- Sydney</option>
							<option value="8">&nbsp;- Northern Territory</option>
						   <option value="8a">&nbsp;&nbsp;&nbsp;- Darwin</option>
							<option value="4">&nbsp;- Queensland</option>
						   <option value="4a">&nbsp;&nbsp;&nbsp;- Brisbane</option>
							<option value="1">&nbsp;- South Australia</option>
						   <option value="1a">&nbsp;&nbsp;&nbsp;- Adelaide</option>
							<option value="5">&nbsp;- Tasmania</option>
						   <option value="5a">&nbsp;&nbsp;&nbsp;- Hobart</option>
							<option value="6">&nbsp;- Victoria</option>
						   <option value="6a">&nbsp;&nbsp;&nbsp;- Melbourne</option>
							<option value="2">&nbsp;- Western Australia</option>
						   <option value="2a">&nbsp;&nbsp;&nbsp;- Perth</option>
							<option value="9">Outside Australia</option>
						</select>
					</td>
				</tr>
				<tr>
					<td colspan="2">
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
				<tfoot>
					<tr>
						<td colspan="2">
							The map is automatically centred on Australia, pan and zoom the map to see events in other locations.
						</td>
					</tr>
				</tfoot>
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
	<div id="map_loading">
		<p style="text-align: center;">
			<img src="assets/images/ajax-loader.gif" width="220" height="19" alt=" "/>
			<br/>Loading markers onto the map...
		</p>
	</div>
	<!-- include the footer -->
	<jsp:include page="footer.jsp"/>
</div>
<!-- include the Google Analytics code -->
<jsp:include page="analytics.jsp"/>
</body>
</html>
