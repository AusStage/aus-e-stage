<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%
/*
 * This file is part of the AusStage Data Exchange Service
 *
 * AusStage Data Exchange Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * AusStage Data Exchange Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AusStage Data Exchange Service.  
 * If not, see <http://www.gnu.org/licenses/>.
 */
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<title>Aus-e-Stage Data Exchange</title>
	<link rel="stylesheet" href="assets/main-style.css"/>
	<link rel="stylesheet" href="assets/ausstage-colours.css"/>
	<link rel="stylesheet" href="assets/ausstage-background-colours.css"/>
	<link rel="stylesheet" href="assets/jquery-ui/jquery-ui-1.8.6.custom.css"/>
	<jsp:include page="analytics.jsp"/>
	<!-- libraries -->
	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.6.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-ui-1.8.12.custom.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.ajaxmanager-3.0.9.js"></script>
	<!-- custom code -->
	<script type="text/javascript" src="assets/javascript/index.js"></script>
	<script type="text/javascript" src="assets/javascript/tab-selector.js"></script>
</head>
<body>
<div class="wrapper">
	<div class="header b-187"><h1>Data Exchange</h1></div>
	<div class="sidebar b-186 f-184">
		<!-- side bar content -->
		<div class="mainMenu">
			<ul>
				<li>&nbsp;</li>
				<li><a href="http://www.ausstage.edu.au" title="AusStage Website homepage">AusStage Website</a></li>
				<li><a href="http://beta.ausstage.edu.au/" title="Aus-e-Stage Project homepage">Aus-e-Stage Project</a></li>
				<li><a href="http://code.google.com/p/aus-e-stage/wiki/StartPage" title="Project Wiki homepage">Project Wiki</a></li>
				<li><a href="http://beta.ausstage.edu.au/?tab=contacts" title="Contact information">Contact Us</a></li>
			</ul>
		</div>
	</div>
	<div class="main b-184 f-187">
		<!-- main content -->
		<div id="tabs" class="tab-container">
			<ul class="fix-ui-tabs">
				<li><a href="#tabs-1">Exchange Overview</a></li>
				<li><a href="#tabs-2">Secondary Genres</a></li>
				<li><a href="#tabs-3">Content Indicators</a></li>
			</ul>
			<div>
				<div id="tabs-1" class="tab-content">
					<p>
						instructions on how to use the service go here
					</p>
				</div>
				<div id="tabs-2" class="tab-content">
					<p>
						Below is a list of secondary genre identifiers that can be used with the Data Exchange service. <a href="/exchange2/?tab=secgenre" title="Persistent link to this tab">Persistent Link</a> to this tab.
					</p>
					<table class="identifiers">
						<thead>
							<tr>
								<th>Identifier</th>
								<th>Description</th>
								<th class="alignRight">Event Count</th>
								<th class="alignRight">Resource Count</th>
							</tr>
						</thead>
						<tbody id="secgenre-table">
							<tr>
								<td colspan="4">
									<div class="ui-state-highlight ui-corner-all search-status-messages">
										<p>
											<span class="ui-icon ui-icon-info status-icon"></span>Loading secondary genre list, please wait...
										</p>
									</div>
								</td>
							</tr>
						</tbody>
						<tfoot>
							<tr>
								<td>Identifier</td>
								<td>Description</td>
								<td class="alignRight">Event Count</td>
								<td class="alignRight">Resource Count</td>
							</tr>
						</tfoot>
					</table>
				</div>
				<div id="tabs-3" class="tab-content">
					<p>
						Below is a list of content indicator identifiers that can be used with the Data Exchange service. <a href="/exchange2/?tab=contentindicator" title="Persistent link to this tab">Persistent Link</a> to this tab.
					</p>
					<table class="identifiers">
						<thead>
							<tr>
								<th>Identifier</th>
								<th>Description</th>
								<th class="alignRight">Event Count</th>
								<th class="alignRight">Resource Count</th>
							</tr>
						</thead>
						<tbody id="contentindicator-table">
							<tr>
								<td colspan="4">
									<div class="ui-state-highlight ui-corner-all search-status-messages">
										<p>
											<span class="ui-icon ui-icon-info status-icon"></span>Loading content indicator list, please wait...
										</p>
									</div>
								</td>
							</tr>
						</tbody>
						<tfoot>
							<tr>
								<td>Identifier</td>
								<td>Description</td>
								<td class="alignRight">Event Count</td>
								<td class="alignRight">Resource Count</td>
							</tr>
						</tfoot>
					</table>
				</div>
			</div>
		</div>
	</div>
	<!-- always at the bottom of the content -->
	<div class="push"></div>
</div>
<jsp:include page="footer.jsp"/>
</body>
</html>
