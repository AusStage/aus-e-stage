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
	<meta name="viewport" content="initial-scale=1.0, user-scalable=no, width=device-width"/>
	<meta name="apple-mobile-web-app-capable" content="yes">
	<link rel="apple-touch-startup-image" href="apple-touch-startup-image.png"/>
	<link rel="stylesheet" type="text/css" href="assets/mobile.css" media="only screen and (max-width: 480px)"/>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.4.2.min.js"></script>
	<script type="text/javascript" src="assets/javascript/mobile.js"></script>
</head>
<body>
	<div id="header">
		<div class="leftButton"><a href="index.jsp">Back</a></div>
		<h1>AusStage Mobile</h1>
	</div>
	<div id="content">
		<h2>Performances Seeking Feedback</h2>
		<ul>
			<li><a href="feedback.jsp?id=" title="">Ibsen Rocks!</a></li>
			<li><a href="feedback.jsp?id=" title="">A Doll's House</a></li>
			<li><a href="feedback.jsp?id=" title="">The Wild Duck</a></li>
			<li><a href="feedback.jsp?id=" title="">The Woman from Snowy River</a></li>
		</ul>
	</div>
<!-- include the Google Analytics code -->
<jsp:include page="analytics.jsp"/>
</body>
</html>
