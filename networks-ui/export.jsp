<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%
/*
 * This file is part of the AusStage Navigating Networks Service
 *
 * The AusStage Navigating Networks Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Navigating Networks Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>
*/
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
	<title>AusStage Navigating Networks Service (Beta)</title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/> 
	<meta http-equiv="content-script-type" content="text/javascript">	
	<link rel="stylesheet" href="assets/networks.css"/>
	<link rel="stylesheet" href="assets/ausstage-colours.css">
	<link rel="stylesheet" href="assets/ausstage-background-colours.css">
	<link rel="stylesheet" type="text/css" media="screen" href="assets/jquery-ui/jquery-ui-1.8.6.custom.css"/>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.6.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-ui-1.8.4.custom.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.form-2.4.3.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.validate-1.7.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.selectboxes-2.2.4.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.tipsy-1.0.0a.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.jsonp-2.1.4.min.js"></script>				
	<script type="text/javascript" src="assets/javascript/libraries/browser-detect.js"></script>
	<script type="text/javascript" src="assets/javascript/export.js"></script>
</head>
<body>

<div class="wrapper">
	<div class="header b-187"><h1>AusStage Navigating Networks Service (Beta)</h1></div>
	<!-- Include the sidebar -->
	<div class="sidebar b-186 f-184"/>
		<div class="mainMenu">
			<ul>
				<li>&nbsp;</li>
				<li><a href="http://www.ausstage.edu.au" title="AusStage Website homepage">AusStage Website</a></li>
				<li><a href="http://beta.ausstage.edu.au/" title="Aus-e-Stage Project homepage">Aus-e-Stage Project</a></li>
				<li><a href="index.jsp" title="Browse Contributor Network">Navigating Networks</a> </li>	
				<li>&nbsp;</li>
				<li><a href="http://code.google.com/p/aus-e-stage/wiki/NetworkService" title="Technical and Development Documentation">Technical Info.</a></li>
				<li><a href="sparql.jsp" title="SPARQL Query Form">Use SPARQL Queries</a></li>
			</ul>
		</div>
	</div>
	
	
	<div class="main b-184 f-187">		

			<h2>Export Graph Data</h2>
			<p>
			Use the form below to export egocentric graph data in a variety of different formats for use in network visualisation software.
			</p>
			<p>
			More information on how to use this page, including guidance on loading the data into a small number of sample programs, is 
			<a href="http://code.google.com/p/aus-e-stage/wiki/NavigatingNetworksExportGraphs" target="ausstage" title="Information and Guidance on how to use this page">available in our Wiki</a>.
			</p>
			<p>
			If you do not know the contributor id, leave this field blank and click the lookup button to use the contributor search form.
			</p>
			<div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;" id="mac_warning" style="visibility: hidden;">
				<p>
				<span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span><strong>Warning:</strong> 
				It has been detected that you are using a Mac. <br/>
				When you download graphml export files from this site using a Mac the export file name may have an additional .xml extension 
				added to it.<br/>
				Please rename the file to remove this additional extension before opening the file in your visualisation software.<br/>
				</p>
			</div>

			<form action="/networks/export" method="get" id="export_data" name="export_data">
				<table class="formTable">
					<tbody>
					<tr>
						<th scope="row">
							<label id="task_label" for="task">Graph Type: </label>
						</th>
						<td>
							<select size="1" id="task" name="task" title="Select the desired type of graph. Note: a simple ego centric network is the most common">
							</select>
						</td>
					</tr>
					<tr>
						<th scope="row">
							<label id="id_label" for="id">Contributor ID: </label>
						</th>
						<td>
							<input type="text" size="40" id="id" name="id" title=""/>
						</td>
					</tr>
					<tr>
						<th scope="row">
							<label id="name_label" for="name">Contributor Name: </label>
						</th>
						<td>
							<input type="text" readonly="readonly" size="40" id="name" name="name"/> <button name="lookup" id="lookup_btn">Lookup</button>
						</td>
					</tr>
					<tr id='format_container'>
						<th scope="row">
							<label id="format_label" for="format">Data Format: </label>
						</th>
						<td>
							<select size="1" id="format" name="format" title="Select the desired data format. Note: graphml is the most common">
							</select>
						</td>
					</tr>
					<tr id='radius_container'>
						<th scope="row">
							<label id="radius_label" for="radius">Radius: </label>
						</th>
						<td>
							<select size="1" id="radius" name="radius" title="Select the desired number of edges between the central node and the peripheral nodes of the network">
							</select>
						</td>
					</tr>
					<tr id="simplify_container">
						<th scope="row">
							<label id="simplify_label" for="simplify">Simplify: </label>
						</th>
						<td>
							<select size="1" id="simplify" name="simplify" title="Select the desired number of edges between the central node and the peripheral nodes of the network">
							</select>
						</td>
					</tr>					
					<tr>
						<td colspan="2">
							<input type="submit" name="submit" id="export_btn" value="Export"/><br/>
							Use the lookup button to confirm the id is valid before clicking on the export button.<br/>
							Compiling the data for an export may take some time, please click the export button once only.
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
	<!-- Search form div -->
	<div id="search_div" title="Search">
		<p>Enter a name, or part of a name, and click the search button</p>
		<form method="get" id="search_form" name="search_form">
			<input type="hidden" name="task" id="task" value="collaborator"/>
			<input type="hidden" name="limit" id="limit" value="5"/>
			<input type="hidden" name="sort" id="sort" value="name"/>
			<table class="formTable">
				<tbody>
				<tr>
					<th scope="row">
						<label id="search_name_label" for="name">Contributor Name: </label>
					</th>
					<td>
						<input type="text" size="40" id="query" name="query" title="Enter the contributor name, or part of their name, and click the search button"/>
					</td>
				</tr>
				</tbody>
			</table>
		</form>
		<table id="search_results" class="searchResults">
			<thead>
				<tr>
					<th>Contributor Name</th>
					<th>Event Dates</th>
					<th>Functions</th>
					<th>&nbsp;</th>
				</tr>
			</thead>
			<tbody id="search_results_body">
			</tbody>
		</table>
		<table id="search_results_evt" class="searchResults">
			<thead>
				<tr>
					<th>Event Name</th>
					<th>Venue</th>
					<th>First Date</th>
					<th>&nbsp;</th>
				</tr>
			</thead>
			<tbody id="search_results_body_evt">
			</tbody>
		<table id="search_results_org" class="searchResults">
			<thead>
				<tr>
					<th>Organisation Name</th>
					<th>Address</th>
					<th>&nbsp;</th>
				</tr>
			</thead>
			<tbody id="search_results_body_org">
			</tbody>			
		</table>
		<table id="search_results_venue" class="searchResults">
			<thead>
				<tr>
					<th>Venue Name</th>
					<th>Address</th>
					<th>&nbsp;</th>
				</tr>
			</thead>
			<tbody id="search_results_body_venue">
			</tbody>			
		</table>


		<div id="search_waiting">
			<p style="text-align: center;">
				<img src="assets/images/ajax-loader.gif" width="220" height="19" alt=" "/>
				<br/>Loading Search Results...
			</p>
		</div>
		<div id="error_message">
		</div>
		<p>
		 <strong>Note: </strong>Search results are limited to 5 records. If you do not see the results that you expected, please refine your search.
		</p>
	</div>

	<div class='push'></div>
</div>
	<!-- include the footer -->
<jsp:include page="footer.jsp"/>
<!-- include the Google Analytics code -->
<jsp:include page="analytics.jsp"/>
</body>
</html>
