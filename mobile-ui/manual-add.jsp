<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%
/*
 * This file is part of the AusStage Mobile Service
 *
 * The AusStage Mobile Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Mobile Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mobile Service.  
 * If not, see <http://www.gnu.org/licenses/>.
 */
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
	<title>AusStage Researching Audiences Service (Beta)</title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/> 
	<link rel="stylesheet" type="text/css" media="screen" href="assets/main-style.css"/>
	<link rel="stylesheet" type="text/css" media="screen" href="assets/jquery-ui/jquery-ui-1.8.4.custom.css"/>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.4.2.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-ui-1.8.4.custom.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.form-2.4.3.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.validate-1.7.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.selectboxes-2.2.4.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.tipsy-1.0.0a.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.maskedinput-1.2.2.min.js"></script>
	<script type="text/javascript" src="assets/javascript/manual-add.js"></script>
</head>
<body>
<div id="wrap">
	<div id="header"><h1>AusStage Researching Audiences Service (Beta)</h1></div>
	<div id="nav">
	</div>
	<div id="main">
		<h2>Add Feedback Manually From an Exception Report</h2>
		<p>
			Use the form below to add Feddback into the Researching Audiences Service based on an exception report.
		</p>
		<div id="error_message"></div>
		<form action="/mobile/manualadd" method="post" id="feedback" name="feedback">
			<table class="formTable">
				<tbody>
				<tr>
					<th scope="row">
						<label id="performance_label" for="performance">Performance ID: </label>
					</th>
					<td>
						<input type="text" size="40" id="performance" name="performance" title="Enter the Performance ID from the mob_performance table"/>
					</td>
				</tr>
				<tr>
					<th scope="row">
						<label id="question_label" for="question">Question ID: </label>
					</th>
					<td>
						<input type="text" size="40" id="question" name="question" title="Enter the Question ID from the mob_questions table"/>
					</td>
				</tr>
				<tr>
					<th scope="row">
						<label id="source_label" for="source_type">Source Type: </label>
					</th>
					<td>
						<select size="1" id="source_type" name="source_type" title="Select the source type for the feedback">
						</select>
					</td>
				</tr>
				<tr>
					<th scope="row">
						<label id="date_label" for="date">Received Date: </label>
					</th>
					<td>
						<input type="text" size="40" id="date" name="date" title="Enter the date that the feedback was received"/><br/>
						<strong>Format: </strong> dd-mmm-yyyy e.g. 21-Sep-2010
					</td>
				</tr>
				<tr>
					<th scope="row">
						<label id="time_label" for="time">Received Time: </label>
					</th>
					<td>
						<input type="text" size="40" id="time" name="time" title="Enter the time that the feedback was received"/><br/>
						<strong>Format: </strong> hh:mm:ss (24 hour time) e.g. 13:08:00
					</td>
				</tr>
				<tr>
					<th scope="row">
						<label id="from_label" for="from">Received From Hash: </label>
					</th>
					<td>
						<input type="text" size="40" id="from" name="from" title="Enter the Received From hash"/>
					</td>
				</tr>
				<tr>
					<th scope="row">
						<label id="source_id_label" for="source_id">Source ID Hash: </label>
					</th>
					<td>
						<input type="text" size="40" id="source_id" name="source_id" title="Enter the Source ID hash"/>
					</td>
				</tr>
				<tr>
					<th scope="row">
						<label id="content_label" for="content">Feedback Content: </label>
					</th>
					<td>
						<textarea rows="5" cols="45" id="content" name="content" title="Enter the content of the feedback message"></textarea>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input type="submit" name="submit" id="add_btn" value="Add Feedback"/>
					</td>
				</tr>
				</tbody>
			</table>
		</form>
		<hr/>
		<h3>Decode Hex String into Characters</h3>
		<form id="decode" name="decode">
			<table class="formTable">
				<tbody>
				<tr>
					<th scope="row">
						<label id="hexstring_label" for="hexstring">Hex Encoded String: </label>
					</th>
					<td>
						<textarea rows="5" cols="45" id="hexstring" name="hexstring" title="Enter the hex encoded string"></textarea>
					</td>
				</tr>
				<tr>
					<th scope="row">
						<label id="charstring_label" for="charstring">Decoded String: </label>
					</th>
					<td>
						<textarea rows="5" cols="45" id="charstring" name="charstring" title="Decoded string will appear here"></textarea>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input type="submit" name="submit" id="decode_btn" value="Decode the String"/>
					</td>
				</tr>
				</tbody>
			</table>
		</form>		
	</div>
	<!-- include the footer -->
	<jsp:include page="footer.jsp"/>
</div>
<!-- include the Google Analytics code -->
<jsp:include page="analytics.jsp"/>
</body>
</html>
