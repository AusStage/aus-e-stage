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
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
	<title>AusStage Mapping Service (Beta)</title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/> 
	<link rel="stylesheet" type="text/css" media="screen" href="assets/main-style.css" />
	<link rel="stylesheet" type="text/css" media="screen" href="assets/jquery-ui/jquery-ui-1.7.2.custom.css"/>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.3.2.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-ui-1.7.2.custom.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.bgiframe.min.js"></script>
	<script type="text/javascript" src="assets/javascript/exportdata.js"></script>
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
		<h2 id="export_name">Download Map Data on: ...</h2>
		<p>
			Use the form below to customise the export of data from the AusStage database into a <a href="http://en.wikipedia.org/wiki/Kml" title="Wikipedia article on this topic">Keyhole Markup Language</a> (KML) file.
			If you are unsure about what an option does, click the [?] link next to the option for more information.
		</p>
		<div id="export_form_div">
			<form action="data/" method="post" id="export_form" name="export_form">
			<input type="hidden" name="action" value="export"/>
			<input type="hidden" name="type" id="export_type" value=""/>
			<input type="hidden" name="id"   id="export_id"  value=""/>
			<table class="formTable">
				<tr>
					<th scope="row">
						<label for="time_span">Include TimeSpan Elements: </label>
					</th>
					<td>
						<input type="checkbox" id="time_span" name="time_span"/> [<a href="#" title="More Information" onclick="showMoreInfo('1'); return false;">?</a>]
					</td>
				</tr>
				<tr>
					<th scope="row">
						<label for="trajectory">Include Trajectory Information: </label>
					</th>
					<td>
						<input type="checkbox" id="trajectory" name="trajectory"/> 
						[<a href="#" title="More Information" onclick="showMoreInfo('2'); return false;">?</a>]
					</td>
				</tr>
				<tr>
					<th scope="row">
						<label for="groupevents">Include Events Grouped by Venue: </label>
					</th>
					<td>
						<input type="checkbox" id="groupevents" name="groupevents"/> 
						[<a href="#" title="More Information" onclick="showMoreInfo('3'); return false;">?</a>]
					</td>
				</tr>
				<!--
				<tr>
					<th scope="row">
						<label for="elevation">Include Elevation Information: </label>
					</th>
					<td>
						<input type="checkbox" id="elevation" name="elevation"/> [<a href="#" title="More Information" onclick="showMoreInfo('x'); return false;">?</a>]
					</td>
				</tr>
				-->
				<tr>
					<td colspan="2">
						<input class="ui-state-default ui-corner-all button" type="submit" name="submit" value="Export"/>
					</td>
				</tr>
			</table>
		</form>
		</div>
		<div id="more_info_1">
			<p>
				When you include <a href="http://code.google.com/apis/kml/documentation/kmlreference.html#timespan" target="_blank" title="More information about this element">TimeSpan</a> elements in the KML, 
				each placemark will have a TimeSpan element that describes the First Date and Last Date of an event. Including these elements allows an application such as <a href="http://earth.google.com/" target="_blank" title="Official Google Earth homepage">Google Earth</a> to automatically
				display a time slider on the map. 
			</p>
		</div>
		<div id="more_info_2">
			<p>
				When you include Trajectory Information placemarks are linked together in date order. This option can be useful when mapping an series of events, for example a tour.
			</p>
		</div>
		<div id="more_info_3">
			<p>
				When you include events grouped by venue only one placemark will be added for each venue. The event information is grouped together and displayed as one list for that venue.
			</p>
		</div>
		<!--
		<div id="more_info_x">
			<p>
				When you include Elevation Information time is treated as a third dimention on the map and later events are added to the map using placemarks at a higher elevation in xxxx increments of time.
			</p>
		</div>
		-->
	</div>
	<!-- include the footer -->
	<jsp:include page="footer.jsp"/>
</div>
<!-- include the Google Analytics code -->
<jsp:include page="analytics.jsp"/>
</body>
</html>
