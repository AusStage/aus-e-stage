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
	<script type="text/javascript" src="assets/javascript/libraries/jquery.jsonp-2.1.4.min.js"></script>		
	<script type="text/javascript" src="assets/javascript/libraries/jquery.scrollto-1.4.2.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.selectboxes-2.2.4.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.validate-1.7.min.js"></script>		
	<!-- custom code -->
	<script type="text/javascript" src="assets/javascript/code_generator.js"></script>
	<script type="text/javascript" src="assets/javascript/tab-selector.js"></script>
</head>
<body>
<div class="wrapper" id="top">
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
				<li><a href="#tabs-1">Code Generator Overview</a></li>
				<li><a href="#tabs-2">Embed Data</a></li>
			</ul>
			<div>
				<div id="tabs-1" class="tab-content">
					<h2>Embed Ausstage Data in Your Website</h2>
					<p>
						These pages help you generate code that will dynamically show data from the Ausstage database. The code is able to be copied and pasted directly into a web page.
					</p>
				</div>
				<!--Event embed page-->
				<div id="tabs-2" class="tab-content">
					<table class="formTable">
						<tbody>
							<tr>
								<th scope="row">
									<label id="task_label" for="task">Records: </label>
								</th>
								<td>
									<select id="type" name="type" title="Hint here">
									</select>
								</td>
							</tr>
						
							<tr>
								<th scope="row">
									<label id="task_label" for="task">Associated with: </label>
								</th>
								<td>
									<select id="task" name="task" title="Hint here">
									</select>
								</td>
							</tr>
							<tr>
								<th scope="row">
									<label id="id_label" for="id">ID: </label>
								</th>
								<td>
									<ul id="selectedIds" class="noBullets">
									</ul>
									<span id="addEntity" class="contributorAddIcon ui-icon ui-icon-plus clickable" style="display: inline-block;" title="add a record"></span>
								</td>
								<td id="idError" class="error"></td>
							</tr>
							<tr>
								<th scope="row">
									<label id="limit_label" for="limit">Record Limit: </label>
								</th>
								<td>
									<input type="radio" name="limitGroup" title="Return all records" value="all" id="noLimit" checked/>
									<label id="userLimit_label" for="noLimit">All</label><br/>

									<input type="radio" name="limitGroup" title="Enter number of records returned" value="userEnter"/>
									<label id="userLimit_label" for="userLimit">Select:</label>
									<input type="text" id="userLimit" title="Enter the desired number of records returned" disabled />						
								</td>
								<td id="limitError" class="error"></td>
							</tr>	
							<tr>
								<th scope="row">
									<label id="sort_by_label" for="sortBy">Sort By: </label>
								</th>
								<td>
									<select size="1" id="sortBy" name="SortBy" title="Select how returned records are sorted">
									</select>
								</td>
							</tr>
							<tr>
								<th scope="row">
								</th>
								<td>
									<input type="button" value='Get Code' id="getCode">
									<label id="styleOn_label" for="styleOn">Ausstage style on</label>								
									<input type="radio" name="styleGroup" title="Ausstage style on" value="true" id="styleOn" checked/>
									<label id="styleOff_label" for="styleOff">off</label>
									<input type="radio" name="styleGroup" title="off" value="false" id="styleOff"/>	
								</td>

							</tr>											
						</tbody>				
					</table>
					<div id="viewer">
						<div id='previewDiv'>
							<p class='center'><strong>Preview</strong></p>
							<div id='preview'></div>						
						</div>
						<div id='embedDiv' class="center">
							<p>Copy the code below and paste it into your website</p>
							<textarea id="embedText" readonly></textarea>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<!--search div-->
	<div id="search_div" title="Search">
		<p>Enter a name, or part of a name, and click the search button</p>
		<p>Select an entity by clicking the 'choose' button on the right of the record. You can continue searching and add up to 10 entities.
		Click the close button when you have finished.</p>
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
	
	<div id="secgenre_div">
		<table>
			<thead>
				<tr>
					<th>Description</th>
					<th class="alignRight">Event Count</th>
					<th class="alignRight"></th>
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
		</table>
	</div>

	<div id="contentindicator_div">
		<table>
			<thead>
				<tr>
					<th>Description</th>
					<th class="alignRight">Event Count</th>
					<th class="alignRight"></th>
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
		</table>
	</div>
	
		<div id="ressubtype_div">
		<table>
			<thead>
				<tr>
					<th>Description</th>
					<th class="alignRight">Event Count</th>
					<th class="alignRight"></th>
				</tr>
			</thead>		
			<tbody id="ressubtype-table">
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
		</table>
	</div>

	
	<!-- always at the bottom of the content -->
	<div class="push"></div>
</div>
<jsp:include page="footer.jsp"/>
</body>
</html>
