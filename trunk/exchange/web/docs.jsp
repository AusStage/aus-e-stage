<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%
/*
 * This file is part of AusStage Data Exchange Service
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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml" lang="en">
<head>
	<title>AusStage Data Exchange Service (Beta)</title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/> 
	<link rel="stylesheet" type="text/css" media="screen" href="assets/main-style.css" />
</head>
<body>
<div id="wrap">
	<div id="header"><h1>AusStage Data Exchange Service (Beta)</h1></div>
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
		<h2>Documentation for the Service</h2>
		<p>
			Diagrams that provide an overview of this service are developed using the <a href="http://vue.tufts.edu/" title="VUE website">Visual Understanding Environment (VUE)</a> system available from Tufts University. 
		</p>
		<p>
			The diagrams that are available are:
		</p>
		<ul>
			<li>The <a href="docs/component-overview.jpeg" title="Component Overview Diagram">Component Overview Diagram</a> - provides an overview of the libraries and components that make up the system.</li>
			<li>The <a href="docs/dataflow-overview.jpeg" title="Dataflow Overview Diagram">Dataflow Overview Diagram</a> - provides an overview of the way in which data flows through the system.</li>
			<li>The VUE source files for the <a href="docs/component-overview.vue" title="Component Overview Diagram">Component Overview Diagram</a> and <a href="docs/dataflow-overview.vue" title="Dataflow Overview Diagram">Dataflow Overview Diagram</a> are also available.</li>
		</ul>
	</div>
	<!-- include the footer -->
	<jsp:include page="footer.jsp"/>
</div>
<!-- include the Google Analytics code -->
<jsp:include page="analytics.jsp"/>
</body>
</html>
