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
	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.3.2.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.validate-1.6.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.form-2.36.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-ui-1.7.2.custom.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.bgiframe.min.js"></script>
	<script type="text/javascript" src="assets/javascript/geocode.js"></script>
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
		<h2>Lookup Venues to Geocode</h2>
		<p>
			Use this webpage to search for venues that are lacking geographic coordinates in the AusStage database. You can search for a specific venue or 
			venues associated with an organisation. 
		</p>
		<h3>Search by Organisation</h3>
		<form action="geocode/" method="post" id="org_search_form" name="org_search_form">
			<input type="hidden" name="action" value="org_search"/>
			<table class="formTable">
				<tr>
					<th scope="row">
						<label for="search_term">Organisation Name: </label>
					</th>
					<td>
						<input type="text" size="40" id="search_term" name="search_term"/>
					</td>
				</tr>
				<tr>
					<th scope="row">
						<label for="operator">Search Operator: </label>
					</th>
					<td>
						<select size="1" id="operator" name="operator">
							<option value="and" selected="selected">And</option>
							<option value="or">Or</option>
							<option value="exact">Exact Phrase</option>
						</select>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input class="ui-state-default ui-corner-all button" type="submit" name="submit" value="Search"/>
					</td>
				</tr>
			</table>
		</form>
		<h3>Search by Venue</h3>
		<form action="geocode/" method="post" id="venue_search_form" name="venue_search_form">
			<input type="hidden" name="action" value="venue_search"/>
			<table class="formTable">
				<tr>
					<th scope="row">
						<label for="search_term">Venue Name: </label>
					</th>
					<td>
						<input type="text" size="40" id="search_term" name="search_term"/>
					</td>
				</tr>
				<tr>
					<th scope="row">
						<label for="operator">Search Operator: </label>
					</th>
					<td>
						<select size="1" id="operator" name="operator">
							<option value="and" selected="selected">And</option>
							<option value="or">Or</option>
							<option value="exact">Exact Phrase</option>
						</select>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input class="ui-state-default ui-corner-all button" type="submit" name="submit" value="Search"/>
					</td>
				</tr>
			</table>
		</form>
		<div id="search_waiting">
		<p style="text-align: center;">
			<img src="assets/images/ajax-loader.gif" width="220" height="19" alt=" "/>
			<br/>Loading Search Results...
		</p>
		</div>
		<div id="search_results">
		</div>
		<div id="venue_info">
		</div>
	</div>
	<!-- include the footer -->
	<jsp:include page="footer.jsp"/>
</div>
<!-- include the Google Analytics code -->
<jsp:include page="analytics.jsp"/>
</body>
</html>
