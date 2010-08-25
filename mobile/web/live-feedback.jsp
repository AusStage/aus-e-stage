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
	<!--<script type="text/javascript" src="assets/javascript/libraries/jquery-ui-1.8.4.custom.min.js"></script>-->
	<script type="text/javascript" src="assets/javascript/live-feedback.js"></script>
</head>
<body>
<div id="wrap">
	<div id="header"><h1>AusStage Researching Audiences Service (Beta)</h1></div>
	<div id="nav">
	</div>
	<div id="main"style="width: 1086px;">
		<div id="company_logo">
			<img src="assets/images/bkgd-adt_logo.gif"/>
		</div>
		<h2 id="performance_name">Live Feedback for</h2>
		<h3 id="performance_by">Performance By: </h3>
		<h3 id="performance_question">In response to the question: </h3>
		<div id="error_message"></div>
		<table id="feedback_messages">
			<tr id="table_anchor" style="visibility: hidden">
				<td>&nsbsp;</td>
				<td>&nsbsp;</td>
			</tr>
		</table>
		
	</div>
	<!-- include the footer -->
	<jsp:include page="footer.jsp"/>
</div>
<!-- include the Google Analytics code -->
<jsp:include page="analytics.jsp"/>
</body>
</html>
