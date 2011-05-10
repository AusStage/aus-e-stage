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
	<link rel="stylesheet" type="text/css" media="screen" href="assets/main-style.css"/>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.3.2.min.js"></script>
	<script type="text/javascript" src="assets/javascript/featuredmap.js"></script>
	<script type="text/javascript" src="assets/javascript/map.functions.js"></script>
	<%
		ServletContext context = getServletContext();
		String mapsAPI   = (String)context.getInitParameter("googleMapsAPIUrl");
	%>
	<script type="text/javascript" src="<%=mapsAPI%>"></script>
	<script type="text/javascript" src="assets/javascript/libraries/mapiconmaker-1.1.js"></script>
</head>
<body>
<div id="wrap">
	<div id="header"><h1>Mapping Events (Alpha)</h1></div>
	<div id="nav">
	</div>
	<!-- Include the sidebar -->
	<jsp:include page="sidebar.jsp"/>
	<div id="main">
		<h2>Map Events in the AusStage database</h2>
		<p>
			The aim of the AusStage Mapping Service is to provide an interactive, map-based interface which allows you to search, manage and display AusStage data. 
			The maps generated by your searches build geographic representations of <a href="http://www.ausstage.edu.au" title="AusStage homepage">AusStage</a> data in a variety of meaningful ways. 
			There are five kinds of maps under development:
		</p>
		<ol>
			<li><a href="/mapping/browse.jsp" title="Browse a Map of Venues in the AusStage database">Browse Map of Venues</a> (Testing)</li>
			<li><a href="/mapping/organisations.jsp" title="Search for Organisations and create a map of their events">Maps By Organisation</a> (Testing)</li>
			<li><a href="/mapping/contributors.jsp" title="Search for Contributors and create a map of their events">Maps By Contributor</a> (Testing)</li>
			<li>Maps by Event Name (In Technical Development)</li>
			<li><a href="/mapping/absdatasets.jsp" title="Add ABS Data Overlays to Maps in Google Earth">ABS Data Overlays</a> (Sample available)</li>
		</ol>
		<p>
			The AusStage Mapping Service is one element of the <a href="/" title="Aus-e-Stage project homepage">Aus-e-Stage</a> project.
			Aus-e-Stage is funded by <a href="https://www.pfc.org.au/bin/view/Main/NeAT" title="National eResearch Architecture Taskforce homepage">NeAT</a> and is creating three new services to enhance the
			research capabilities of the AusStage database.

			Please help us to develop the AusStage mapping service by exploring its
			functionality. If you have any feedback, questions, or queries please <a href="/" title="Aus-e-Stage project homepage">Contact Us</a>.
		</p>
		<p>
			The Alpha 1 Testing phase of this mapping service was conducted during March and
			April 2010. The report on this process is <a href="/mapping/docs/mapping-service-alpha-1-testing-report.pdf" title="Mapping Service Alpha 1 Testing Report">now available</a>.
		</p>
		<div id="map_header" class="featured_map_header_footer map_header_footer">
			<h3 id="map_name"></h3>
		</div>
		<div id="map">
		</div>
		<div id="map_legend">
			<table class="mapLegend">
				<thead>
					<tr>
						<th colspan="2">Map Legend</th>
					</tr>
				</thead>
				<tbody>
					<tr style="border-bottom: 1px solid #333333;">
						<td style="font-size: 90%" colspan="2">
							<ul>
								<li>This is a featured map constructed using data retrieved live from the <a href="http://www.ausstage.edu.au" title="AusStage homepage">AusStage</a> database</li>
								<li>The map is automatically centred on Australia, pan and zoom the map to see events in other locations</li>
								<li>A darker shadow under a marker indicates that there are large number of markers in close proximity to that marker that can only be seen at a higher zoom level</li>
							</ul>
						</td>
					</tr>
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
