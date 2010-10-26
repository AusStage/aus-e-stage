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
	<link rel="stylesheet" href="assets/main-style.css"/>
	<link rel="stylesheet" href="assets/jquery-ui/jquery-ui-1.8.5.custom.css"/>
	<jsp:include page="analytics.jsp"/>
	<!-- libraries -->
	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.4.3.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-ui-1.8.5.custom.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.tipsy-0.1.7.js"></script>
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
									<input type="text" size="80" id="query" name="query" title="Enter a few search terms into this box"/>
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
