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
	<title>AusStage Mapping Service (Beta)</title>
	<link rel="stylesheet" href="assets/main-style.css"/>
	<link rel="stylesheet" href="assets/jquery-ui/jquery-ui-1.8.5.custom.css"/>
	<jsp:include page="analytics.jsp"/>
	<!-- libraries -->
	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.4.3.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-ui-1.8.5.custom.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.tipsy-0.1.7.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.ajaxmanager-3.06.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.validate-1.7.min.js"></script>
	<!-- custom code -->
	<script type="text/javascript" src="assets/javascript/mapping.js"></script>
</head>
<div id="wrap">
	<div id="header"><h1>AusStage Mapping Service</h1></div>
	<div id="nav"></div>
	<jsp:include page="sidebar.jsp"/>
	<div id="showPanel"><span>&raquo;</span></div>
	<div id="main">
		<div id="tabs" class="tab-container">
			<ul>
				<li><a href="#tabs-1">Search</a></li>
				<li><a href="#tabs-2">Map</a></li>
			</ul>
			<div class="ui-layout-content" style="font-size:90%">
				<div id="tabs-1">
					<h3>Search the AusStage Database</h3>
					<form action="/mapping/" method="get" id="search" name="search">
						<table class="formTable">
							<tbody>
							<tr>
								<th scope="row">
									<label id="query-label" for="query">Search Terms: </label>
								</th>
								<td>
									<input type="text" size="40" id="query" name="query" title="Enter a few search terms into this box"/>
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<input type="submit" name="submit" id="search_btn" value="Search"/>
								</td>
							</tr>
							</tbody>
						</table>
					</form>
					<p>
						This is a simplified search page that returns results in the four categories outlined below. A search result will contain all of your search terms in the search index, including such items as
						alternate names, and previous names. Please wait for the search to complete before exploring the results.
						<br/>&nbsp;<br/>
						At most 25 search results will be displayed under each category. If you find a search result is missing, refine your search terms. If a search result is still missing, use the main <a href="http://www.ausstage.edu.au/" title="Main AusStage website">AusStage</a> website to ensure
						that the item you are looking for is associated with a venue which has coordinates stored in the AusStage database. 
					</p>
					<div id="messages">
						<div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
							<p>
								<span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
								<span id="message_text">Search is underway...</span>
							</p>
						</div>
						<br/>&nbsp;
					</div>
					<h3>Search Results</h3>
					<div class="accordion">
						<h3><a href="#" id="contributor_heading">Contributors</a></h3>
							<div id="contributor_results">Contributor Results</div>
					</div>
					<div class="accordion">
						<h3><a href="#" id="organisation_heading">Organisations</a></h3>
							<div id="organisation_results">Organisation Results</div>
					</div>
					<div class="accordion">
						<h3><a href="#" id="venue_heading">Venues</a></h3>
							<div id="venue_results">Venue Results</div>
					</div>
					<div class="accordion">
						<h3><a href="#" id="event_heading">Events</a></h3>
							<div id="event_results">Event Results</div>
					</div>
				</div>
				<div id="tabs-2">
					<h3>Map of the Search Results</h3>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="footer.jsp"/>
</div>
</html>
