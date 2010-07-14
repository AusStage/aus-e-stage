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
 * If not, see <http://www.gnu.org/licenses/>.
*/
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml" lang="en">
<head>
	<title>AusStage Navigating Networks Service (Beta)</title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/> 
	<link rel="stylesheet" type="text/css" media="screen" href="assets/main-style.css"/>
</head>
<body>
<div id="wrap">
	<div id="header"><h1>AusStage Navigating Networks Service (Beta)</h1></div>
	<div id="nav">
	</div>
	<!-- Include the sidebar -->
	<jsp:include page="sidebar.jsp"/>
	<div id="main">
		<h2>Navigating Networks SPARQLer</h2>
		<p>
			Use the form below to execute <a href="http://en.wikipedia.org/wiki/SPARQL" title="Wikipedia article on this topic">SPARQL</a> queries using the Navigating Networks dataset. 
			More information about the dataset is available <a href="http://code.google.com/p/aus-e-stage/wiki/NavigatingNetworksDataset" title="Information on what is contained in the datset">in our Wiki</a>
			as are a small number of <a href="http://code.google.com/p/aus-e-stage/wiki/NavigatingNetworksSparql" title="List of sample queries in our Wiki">sample queries</a>.
		</p>
		<p>
			<strong>Please Note:</strong> This interface should be use for simple queries that return a reasonable number of results. It should not be used for exports or large datasets.
			Completed exports of the datasets are made periodically and made <a href="http://code.google.com/p/aus-e-stage/downloads/list" title="Aus-e-Stage project download library">available here</a>. 
		</p>
		<form action="sparql" method="get">
			<p>
				<!-- text area for the query -->
				Enter the query in the text area below. <br/>
				<textarea name="query" cols="70" rows="20"></textarea> <br/>
				Output XML: <input type="radio" name="output" value="xml" checked/>
				with XSLT style sheet (leave blank for none): 
				<input name="stylesheet" size="25" value="/networks/assets/xml-to-html.xsl" /> <br/>
				or JSON output: <input type="radio" name="output" value="json"/> <br/>
				or text output: <input type="radio" name="output" value="text"/> <br/>
				Force the accept header to <tt>text/plain</tt> regardless 
				<input type="checkbox" name="force-accept" value="text/plain"/>	<br/>
				<input type="submit" value="Execute Query" />
			</p>
		</form>		
	</div>
	<!-- include the footer -->
	<jsp:include page="footer.jsp"/>
</div>
<!-- include the Google Analytics code -->
<jsp:include page="analytics.jsp"/>
</body>
</html>
