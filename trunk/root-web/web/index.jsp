<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%
/*
 * This file is part of the Aus-e-Stage Beta Root Website
 *
 * The Aus-e-Stage Beta Root Website is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The Aus-e-Stage Beta Root Website is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Aus-e-Stage Beta Root Website.  
 * If not, see <http://www.gnu.org/licenses/>.
*/
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
	<title>Aus-e-Stage Service Development</title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/> 
	<link rel="stylesheet" type="text/css" media="screen" href="assets/main-style.css"/>
	<link rel="stylesheet" type="text/css" media="screen" href="assets/jquery-ui/jquery-ui-1.7.2.custom.css"/>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.4.2.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-ui-1.7.2.custom.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.cookie-1.0.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.bgiframe.min.js"></script>
	<!--<script type="text/javascript" src="http://www.google.com/jsapi?key=ABQIAAAAokh3vtCLg3EQykFxKl2aQBRcqV8LHQN2o3XXxxVSrkLbt4i0AxQ5l2CeCM-F5zjn8uvEwY2dI90uFg"></script>-->
	<script type="text/javascript" src="http://www.google.com/jsapi"></script>
	<script type="text/javascript" src="assets/javascript/main.js"></script>
	<style type="text/css" media="screen">
		/* suppress the snippet in the rss feed display */
		.gfc-control .gf-snippet {
			display: none;
		}
	</style>	
</head>
<body>
<div id="wrap">
	<div id="header"><h1>Aus-e-Stage Service Development</h1></div>
	<div id="nav">
	</div>
	<!-- Include the sidebar -->
	<jsp:include page="sidebar.jsp"/>
	<div id="main">
		<h2>Developing data visualisation and mobile services for AusStage</h2>
		<div id="tabs">
			<ul>
				<li><a href="#tabs-1">Project Overview</a></li>
				<li><a href="#tabs-2">Delicious Links</a></li>
				<li><a href="#tabs-3">Zotero Links</a></li>
				<li><a href="#tabs-4">Analytics</a></li>
				<li><a href="#tabs-5">Contact Us</a></li>
			</ul>
			<div id="tabs-1">
				<p>
					<a href="http://www.ausstage.edu.au" title="AusStage homepage">AusStage</a> fulfils a national need for public access to reliable information on the full spectrum of live performance in Australia,
					delivering a data set of national significance to research, post graduate students, policy makers in government and industry practitioners;
					there is no other comparable database in existence. 
				</p>
				<p>
					However, the conventional database methods of text-based search-and-retrieval are, on
					their own, no longer sufficiently effective in meeting the evolving needs of arts and humanities research. 
				</p>
				<p>
					As a result the Aus-e-Stage project has evolved from this need for new visual interfaces to interact more flexibly with quantifiable research data and to collaborate
					more productively across sectors of the creative economy. 
				</p>
				<p>
					Aus-e-Stage is a <a href="https://www.pfc.org.au/bin/view/Main/NeAT" title="More information on the National eResearch Architecture Taskforce">NeAT</a> funded project with the aim of developing
					three new services that will be designed, tested and deployed to operate alongside the current AusStage text-based search-and-retrieval service.
				</p>
				<p>
					These services are:
				</p>
				<ol>
					<li><a href="/mapping/" title="AusStage Mapping Service (Beta)">Mapping Events</a></li>
					<li>Navigating Networks</li>
					<li>Researching Audiences</li>
				</ol>
				<p>
					The source code for these services is available on the <a href="http://code.google.com/p/aus-e-stage/" title="Source code repository, issue tracker etc.">aus-e-stage</a> project hosted on Google Code.
				</p>
			</div>
			<div id="tabs-2">
				<p>
					Aus-e-Stage project members are always on the look out for resources on the internet that may be of interest to others working on the project. When something
					of interest is discovered it is added to the <a href="http://delicious.com/" title="Delicious homepage">Delicious</a> bookmark service using a common tag. 
				</p>
				<p>
					Below are the 10 most recent links that we have added to the Delicous service using the <a href="http://delicious.com/tag/ausstage/" title="View links with this tag on Delicious">ausstage</a> tag. 
				</p>
				<div id="delicious_feed"></div>
			</div>
			<div id="tabs-3">
				<p>
					Aus-e-Stage project members undertake research in a number of different areas related to the services currently under development. Where resources are found that 
					are on public websites links are added to the <a href="http://delicious.com/" title="Delicious homepage">Delicious</a> bookmark service using a common tag. 
					You can see these links by clicking on the Delicious tab on this page. 
				</p>
				<p>
					Journal articles and other research papers are typically in databases and other online repositories. These items are collected by Aus-e-Stage project members and
					stored using the <a href="http://www.zotero.org/" title="Zotero homepage">Zotero citation management system</a>. Aus-e-Stage project members are all members of the 
					<a href="http://www.zotero.org/groups/ausstage/items" title="Group page in the Zotero system">AusStage group</a> on Zotero to maintain a common library.
				</p>
				<p>
					Below are the 10 most recent items added to the library. 
				</p>
				<div id="zotero_feed"></div> 
			</div>
			<div id="tabs-4">
				<p>Coming Soon!</p>
			</div>
			<div id="tabs-5">
				<p>
					We encourage you to explore our services and provide as much feedback as you wish. Please contact us using the details below:
				</p>
				<p><strong>Aus-e-Stage Project Manager: </strong>Mrs Liz Milford, (08) 8201 2085, <a href="mailto:liz.milford@flinders.edu.au" title="Email Liz">liz.milford@flinders.edu.au</a></p>
				<p><strong>Aus-e-Stage Software Engineer: </strong>Mr Corey Wallis, (08) 8201 5818, <a href="mailto:corey.wallis@flinders.edu.au" title="Email Corey">corey.wallis@flinders.edu.au</a></p>
			</div>
		</div>
	</div>
	<!-- include the footer -->
	<jsp:include page="footer.jsp"/>
</div>
<!-- include the Google Analytics code -->
<jsp:include page="analytics.jsp"/>
</body>
</html>
