<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%
/*
 * This file is part of the AusStage Audience Participation Service
 *
 * The AusStage Audience Participation Service is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage Audience Participation Service is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Audience Participation Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
	<title>AusStage Mobile</title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
	<meta name="viewport" content="user-scalable=no, width=device-width"/>
	<link rel="stylesheet" type="text/css" href="/mobile/assets/mobile.css" media="only screen and (max-width: 480px)"/>
	<script type="text/javascript" src="/mobile/assets/javascript/libraries/jquery-1.4.2.min.js"></script>
	<script type="text/javascript" src="/mobile/assets/javascript/mobile.js"></script>
</head>
<body>
	<div id="header">
		<div class="leftButton"><a href="/mobile/index.jsp">Back</a></div>
		<h1>AusStage Mobile</h1>
	</div>
	<div id="content">
		<h2>Report A Problem</h2>
		<p>Thank you for reporting the problem, one of our engineers will look into the problem as soon as possible.</p>
	</div>
<!-- include the Google Analytics code -->
<jsp:include page="analytics.jsp"/>
</body>
</html>
