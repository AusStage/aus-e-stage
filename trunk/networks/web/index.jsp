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
		<h2>Navigating the network of artistic collaborations in AusStage</h2>
		<p>
			The Navigating Network Service will provide an interactive interface for navigating and analysing the network of artistic collaborations embedded in the <a href="http://www.ausstage.edu.au" title="AusStage Homepage">AusStage</a> dataset. The service will present existing data in new ways and allow researchers to interrogate the collaborative methodologies underpinning creativity in the performing arts.  
		</p>
		<p>A network-based interface will humanise the representation of artists in AusStage by modelling the collaborative ethic of the performing arts. The interface will transform research practice in the performing arts. The application of network visualisation and analysis will reveal patterns of collaborative creativity in the performing arts that have previously been unrepresentable using conventional text-based displays. </p>
		<p>
			More information on the service is available in our Wiki including:
		</p>
		<ul>
			<li>
				<a href="http://code.google.com/p/aus-e-stage/wiki/NavigatingNetworksSpecification" title="">Navigating Networks Specification</a> - Outline the service and provide context to the development
			</li>
			<li>
				<a href="http://code.google.com/p/aus-e-stage/wiki/NavigatingNetworksGoals" title="">Service Goals</a> - Extracted from the specification</a>
			</li>
			<li>
				<a href="http://code.google.com/p/aus-e-stage/wiki/NavigatingNetworksDataset" title="">Navigating Networks Dataset</a> - A subset of the full AusStage dataset especially constructed for use with the service
			</li>
			<li>
				<a href="http://code.google.com/p/aus-e-stage/wiki/NavigatingNetworksSparqlEndpoint" title="">Navigating Networks SPARQL endpoint</a> - Information about the SPARQL endpoint that can be used to access the dataset
			</li>
			<li>
				<a href="http://code.google.com/p/aus-e-stage/wiki/NavigatingNetworksSparql" title="">Sample SPARQL Queries</a> - For use with the SPARQL endpoint
			</li>
		</ul>
	</div>
	<!-- include the footer -->
	<jsp:include page="footer.jsp"/>
</div>
<!-- include the Google Analytics code -->
<jsp:include page="analytics.jsp"/>
</body>
</html>
