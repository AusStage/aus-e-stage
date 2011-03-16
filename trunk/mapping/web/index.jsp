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
<%
/*
 * @author corey.wallis@flinders.edu.au
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
	<div class="header b-187"><h1>Map Events in the AusStage database</h1></div>
	<div class="sidebar b-186 f-184">
		<!-- side bar content -->
		<div class="mainMenu">
			<h2>Main Menu</h2>
			<ul>
				<li><a href="http://www.ausstage.edu.au" title="AusStage Homepage">AusStage Website</a></li>
				<li><a href="/" title="Aus-e-Stage Homepage">Aus-e-Stage Website</a></li>
				<li><a href="/mapping/index2.jsp" title="Aus-e-Stage Mapping Service (Alpha)">Mapping Service (Alpha)</a></li>
				<li><a href="/mapping2/" title="Aus-e-Stage Mapping Service (Beta)">Mapping Service (Beta)</a></li>
				<li><a href="/networks/" title="Aus-e-Stage Navigating Networks Service (Beta)">Navigating Networks</a></li>
				<li><a href="/mobile/" title="Ause-e-Stage Researching Audiences Service (Beta)">Researching Audiences</a></li>
				<li><a href="http://code.google.com/p/aus-e-stage/wiki/StartPage" title="Aus-e-Stage Project Documentation.">Aus-e-Stage Wiki</a></li>
			</ul>
		</div>
	</div>
	<div class="main b-184 f-187" style="margin-left: 300px;">
		<!-- main content -->
		<p>The <a href="http://code.google.com/p/aus-e-stage/wiki/MappingService" title="Information on the Service in our Wiki">AusStage Mapping Service</a> is one element of the three <a href="https://www.pfc.org.au/bin/view/Main/NeAT" title="More information about NeAT">NeAT</a> funded services to enhance the research capabilities of the <a href="http://www.ausstage.edu.au/" title="AusStage homepage">AusStage</a> database.</p> 
		<p>The AusStage Mapping Service aims to provide an interactive, map-based interface which allows users to search, manage and visually display AusStage event data. The Mapping service will introduce a geographic intelligence to research applications of the AusStage data set by providing researchers with the ability to chart the geographic distribution of performance events.</p>
		<p>To date the Mapping Service has been developed in two distinct phases.</p>
		<ol>
			<li>The <a href="index2.jsp" title="Mapping Service homepage">first version of the Mapping Service</a> was developed prior to the release of Google Maps API V3 and is still available. It was decided to terminate further development of this model due to the significant changes in the Google Maps API implemented in 2010.</li>
			<li>The <a href="/mapping2/" title="Mapping Service homepage">second, and current, version of the Mapping Service</a> has been developed in line with upgrade to Google Maps API V3 and is the version under continued development.</li>
		</ol>
		<p>
			More information on the Mapping Service is available in our <a href="http://code.google.com/p/aus-e-stage/wiki/StartPage?tm=6" title="Aus-e-Stage Wiki homepage">Wiki</a> including:
		</p>
		<ul>
			<li><a href="http://code.google.com/p/aus-e-stage/wiki/MappingServiceAPI" title="Information on the API in our Wiki">Mapping Service API</a> - Information on the API designed for the retrieval of data related to building a map on a web page and the export of data in the KML format.</li>
			<li><a href="http://code.google.com/p/aus-e-stage/wiki/MappingServiceFeedback" title="Alpha testing feedback document">Mapping Service Feedback</a> - Detail of user feedback received in Alpha testing processes.</li>
			<li><a href="http://code.google.com/p/aus-e-stage/wiki/MappingServiceConsultancy" title="Documentation on the consultancy activity">Mapping Service Consultancy</a> - Documentation on the consultancy undertaken with the University of Sydney Archaeology Computing Laboratory.</li>
			<li><a href="http://code.google.com/p/aus-e-stage/wiki/GoogleEarthImageOverlay" title="Overlay creation documentation">Mapping How To Article</a> - Information on how to create an overlay for use in <a href="http://www.google.com/earth/index.html" title="Information on the Google Earth app">Google Earth</a>.</li>
		</ul>
		<p>
			<h2>These services are:</h2>
		</p>
		<ul class="services">
			<li>
				<a href="index2.jsp" title="AusStage Mapping Service (Alpha)">
					<h3>Mapping Service (Alpha)</h3>
					<img src="/assets/images/map-alpha-screengrab.jpg" width="200" height="150" alt="">
				</a>
				<p>
					<strong>Mapping Service - Alpha Version</strong>
				</p>
			</li>
			<li>
				<a href="/mapping2/" title="AusStage Mapping Service (Beta)">
					<h3>Mapping Service (Beta)</h3>
					<img src="/assets/images/map-screengrab.jpg" width="200" height="150" alt="">
				</a>
				<p>
					<strong>Mapping Service - Beta Version</strong>
				</p>
			</li>
		</ul>                    
		<p class="clear">
			Please help us to develop the AusStage Mapping Service by exploring its functionality. If you have any feedback, questions or queries please <a href="/" title="Aus-e-Stage homepage">Contact Us</a>. 
		</p>
	</div>
	<!-- always at the bottom of the content -->
	<div class="push"></div>
</div>
<jsp:include page="footer2.jsp"/>
</body>
</html>
