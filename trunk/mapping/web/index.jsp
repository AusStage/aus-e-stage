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
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<title>Aus-e-Stage Service Development</title>
	<link rel="stylesheet" href="/assets/main-style.css"/>
	<link rel="stylesheet" href="/assets/ausstage-colours.css"/>
	<link rel="stylesheet" href="/assets/ausstage-background-colours.css"/>
	<link rel="stylesheet" href="/assets/jquery-ui/jquery-ui-1.8.6.custom.css"/>
	<jsp:include page="analytics.jsp"/>
</head>
<body>
<div class="wrapper">
	<div class="header b-187"><h1>Mapping Events</h1></div>
	<div class="sidebar b-186 f-184">
		<!-- side bar content -->
		<div class="mainMenu">
			<ul>
				<li>&nbsp;</li>
				<li><a href="http://www.ausstage.edu.au" title="AusStage Homepage">AusStage Website</a></li>
				<li><a href="http://beta.ausstage.edu.au/" title="Aus-e-Stage Project">Aus-e-Stage Project</a></li>
				<li><a href="http://beta.ausstage.edu.au/mapping" title="Mapping Events homepage">Mapping Events</a></li>
				<li><a href="http://beta.ausstage.edu.au/networks" title="Navigating Networks homepage">Navigating Networks</a></li>
				<li><a href="http://beta.ausstage.edu.au/mobile/mobile-vis/" title="Researching Audiences homepage">Researching Audiences</a></li>
				<li><a href="http://code.google.com/p/aus-e-stage/wiki/StartPage" title="Aus-e-Stage Project Wiki">Project Wiki</a></li>
				<li><a href="http://beta.ausstage.edu.au/?tab=contacts" title="Contact information">Contact Us</a></li>
			</ul>
		</div>
	</div>
	<div class="main b-184 f-187" style="margin-left: 300px;">
		<!-- main content -->
		<h2>Visualise live performance on a map</h2>
		<p>
			The <a href="http://beta.ausstage.edu.au/mapping" title="Mapping Events homepage">Mapping Events</a> service enables researchers to search, display and visually analyse information about live performance on a map. It introduces new cartographic capabilities to extend research applications of the <a href="http://www.ausstage.edu.au" title="AusStage Homepage">AusStage</a> dataset. It is one of three <a href="http://www.pfc.org.au/bin/view/Main/NeAT" title="NeAT homepage">NeAT</a> funded services developed by the <a href="http://beta.ausstage.edu.au/" title="Aus-e-Stage Project">Aus-e-Stage</a> project.
		</p>
		<p>
			The Mapping Events service has developed in two distinct phases.
		</p>
		<ol>
			<li>An <a href="http://beta.ausstage.edu.au/mapping/index2.jsp" title="Mapping Service Alpha version homepage">Alpha</a> version of the Mapping Events service was developed using Google Maps API Version 2. It is still available for reference.</li>
			<li>The <a href="http://beta.ausstage.edu.au/mapping2/" title="Mapping Service Beta version homepage">Beta</a> version of the Mapping Events service is the current version under ongoing development. It takes advantage of Google Maps API Version 3.</li>
		</ol>
		<ul class="services">
			<li>
				<a href="index2.jsp" title="Mapping Events (Alpha)">
					<h3>Mapping Events (Alpha)</h3><img src="/assets/images/map-alpha-screengrab.jpg" width="200" height="150" alt="">
				</a>
			</li>
			<li>
				<a href="/mapping2/" title="Mapping Events (Beta)">
					<h3>Mapping Events (Beta)</h3><img src="/assets/images/map-screengrab.jpg" width="200" height="150" alt="">
				</a>
			</li>
		</ul>
		<p class="clear">
			More information on the Mapping Events service is available in the Aus-e-Stage project <a href="http://code.google.com/p/aus-e-stage/wiki/StartPage" title="Project Wiki homepage">Wiki</a> including:
		</p>
		<ul>
			<li><a href="http://code.google.com/p/aus-e-stage/downloads/list?q=label:Type-Map" title="Download Maps page">Download Maps</a> - Repository of map files for use with AusStage data in Google Earth, including historical maps and census data.</li>
			<li><a href="http://code.google.com/p/aus-e-stage/wiki/WorkingWithMaps" title="Working With Maps help index">Working with Maps</a> - Information on how to create an overlay for use in Google Earth, how to convert MapInfo data to KML, and how to prepare an ABS overlay.</li>
			<li><a href="http://code.google.com/p/aus-e-stage/wiki/MappingServiceAPI" title="Information on the Mapping Service API">Mapping Service API</a> - Information on the API used to retrieve data for building a map on a web page and exporting data in the KML format.</li>
			<li><a href="http://code.google.com/p/aus-e-stage/wiki/MappingServiceFeedback" title="Details Mapping Service feedback">Mapping Service Feedback</a> - Detail of user feedback received development processes.</li>
			<li><a href="http://code.google.com/p/aus-e-stage/wiki/MappingServiceConsultancy" title="Download the document">Mapping Service Consultancy</a> - Documentation on the consultancy undertaken with the <a href="http://acl.arts.usyd.edu.au/" title="ACL homepage">Archaeology Computing Laboratory</a> at the University of Sydney.</li>
		</ul>
		<p>
			A draft information manual for the Mapping Events service is available for download <a href="http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/Aus-e-Stage-Mapping-Events-module-for-AusStage-Manual.pdf" title="Download the PDF file">here</a>.
		</p>
		<p>
			If you have any feedback, questions or queries about the Mapping Events service, please <a href="http://beta.ausstage.edu.au/?tab=contacts" title="Contact information">contact us</a>.
		</p>
	</div>
	<!-- always at the bottom of the content -->
	<div class="push"></div>
</div>
<jsp:include page="footer2.jsp"/>
</body>
</html>
