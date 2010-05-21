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
	<div id="header"><h1>AusStage Mapping Service (Beta)</h1></div>
	<div id="nav">
	</div>
	<!-- Include the sidebar -->
	<jsp:include page="sidebar.jsp"/>
	<div id="main">
		<h2>Map Events in the AusStage database</h2>
		<p>
			The aim of the AusStage Mapping Service is to provide an interactive, map-based interface with which to search, manage and display <a href="http://www.ausstage.edu.au" title="AusStage homepage">AusStage</a> data. 
			The maps produced by the service allows users to build visual representations of geography of event data in a meaningful way.
		</p>
		<p>
			Users of this system can build maps that chart the geographic distribution of performance events and related information stored in the <a href="http://www.ausstage.edu.au" title="AusStage homepage">AusStage</a> database. 
			There are currently three different types of maps under development and they are:
		</p>
		<ol>
			<li><a href="/mapping/organisations.jsp" title="Search for Organisations and create a map of their events">Maps By Organisation</a></li>
			<li><a href="/mapping/contributors.jsp" title="Search for Contributors and create a map of their events">Maps By Contributor</a></li>
			<li>Search for Events by Name and create a map (Under Development)</li>
		</ol>
		<p>
			Development of the Mapping Service is part of the larger <a href="http://beta.ausstage.edu.au/" title="Aus-e-Stage project homepage">Aus-e-Stage</a> project. Aus-e-Stage is a <a href="https://www.pfc.org.au/bin/view/Main/NeAT" title="More information about NeAt">NeAT funded</a> project with the aim of developing three new services that will be designed, tested and deployed to operate alongside the current <a href="http://www.ausstage.edu.au" title="AusStage homepage">AusStage</a> text-based search-and-retrieval service. 
		<p>
			We encourage you to use this service and explore the functionality that it provides. If you have any questions, queries or comments please <a href="http://beta.ausstage.edu.au/" title="Contact Project Members">Contact Us</a>.
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
								<li>The map is automatically centred on Australia, pan and zoom the map to see events in other locations
								</li>
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
