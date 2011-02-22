<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%
/*
 * This file is part of the Aus-e-Stage Beta Website
 *
 * The Aus-e-Stage Beta Website is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The Aus-e-Stage Beta Website is distributed in the hope that it will 
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
	<link rel="stylesheet" href="assets/main-style.css"/>
	<link rel="stylesheet" href="assets/ausstage-colours.css"/>
	<link rel="stylesheet" href="assets/ausstage-background-colours.css"/>
	<link rel="stylesheet" href="assets/jquery-ui/jquery-ui-1.8.6.custom.css"/>
	<jsp:include page="analytics.jsp"/>
	<!-- libraries -->
	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.5.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-ui-1.8.6.custom.min.js"></script>
	<!-- custom code -->
	<script type="text/javascript" src="assets/javascript/index.js"></script>
</head>
<body>
<div class="wrapper">
	<div class="header b-187"><h1>Aus-e-Stage Service Development</h1></div>
	<div class="sidebar b-186 f-184">
		<!-- side bar content -->
		<div class="mainMenu">
			<h2>Main Menu</h2>
			<ul>
				<li><a href="http://www.ausstage.edu.au" title="AusStage Homepage">AusStage Website</a></li>
				<li><a href="/mapping2/" title="Networks Service Homepage">Mapping Homepage</a></li>
				<li class="map-icon-help clickable">Map Iconography</li>
			</ul>
		</div>
	</div>
	<div class="main b-184 f-187">
		<!-- main content -->
	</div>
	<!-- always at the bottom of the content -->
	<div class="push"></div>
</div>
<jsp:include page="footer.jsp"/>
</body>
</html>
