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
	<script type="text/javascript" src="assets/javascript/map.functions.js"></script>
	<script type="text/javascript" src="assets/javascript/maplinks.js"></script>
	<%
		ServletContext context = getServletContext();
		String mapsAPI   = (String)context.getInitParameter("googleMapsAPIUrl");
	%>
	<script type="text/javascript" src="<%=mapsAPI%>"></script>
	<script type="text/javascript" src="assets/javascript/libraries/mapiconmaker-1.1.js"></script>
</head>
<body>
<div id="wrap">
	<div id="header"><h1>AusStage Mapping Service (Beta)</h1></div>
	<div id="nav">
		<!--<ul>
			<li><a href="#">Option 1</a></li>
			<li><a href="#">Option 2</a></li>
			<li><a href="#">Option 3</a></li>

			<li><a href="#">Option 4</a></li>
			<li><a href="#">Option 5</a></li>
		</ul>
		-->
		<ul>
			<li>&nbsp;</li>
		</ul>
	</div>
	<!-- Include the sidebar -->
	<jsp:include page="sidebar.jsp"/>
	<div id="main">
		<h2 id="map_name">Map of events for: ...</h2>
		<div id="map_header" class="map_header_footer">
			<h3></h3>
			<ul class="map_links">
				<li><a href="#" title="Link for this map" id="map_header_link">Persistent link for this map</a></li>
				<li><a href="#" title="Download KML file" id="map_header_kml">Download KML version of this map</a></li>
				<li><a href="#" title="Export data using advanced options" id="map_header_export">Download KML data with advanced options</a></li>
			</ul>
		</div>
		<div id="map">
		</div>
		<div id="map_footer" class="map_header_footer">
			<h3>Advanced Display Options</h3>
			<form action="" method="" id="adv_map_display_form" name="adv_map_display_form">
			<input type="hidden" name="adv_map_org_id" id="adv_map_org_id"/>
			<table class="formTable" width="100%">
				<tr>
					<th scope="row">
						<label for="show_trajectory">Show Trajectory Information: </label> &nbsp; <input type="checkbox" id="show_trajectory" name="show_trajectory" value="on"/>
					</th>
				</tr>
				<tr>
					<th scop="row" colspan="2">Show events that occured between:</th>
				</tr>
				<tr>
					<td colspan="2">
						<fieldset>
							<label for="event_finish" class="tohide">First Date: </label>
								<select name="event_finish" id="event_finish" size="1" class="slider"></select>
							<label for="event_start" class="tohide">Last Date: </label>
								<select name="event_start" id="event_start" size="1" class="slider"></select>
						</fieldset>
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
					<tr>
						<td scope="row" colspan="2">
							Trajectory lines are coloured using a scale:
							<ul>
								<li><span style="background: #FF0000">red</span> is the earliest</li>
								<li><span style="background: #FFFF00">yellow</span> is the latest</li>
								<li>Other times are shades between these two colours</li>
							</ul>
						</td>
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
