<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%
/*
 * This file is part of the Aus-e-Stage Beta Website
 *
 * The Aus-e-Stage Beta Website is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The Aus-e-Stage Beta Website is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
 */
%>
<%
/*
 * @author corey.wallis@flinders.edu.au
 */
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<title>Aus-e-Stage Service Development</title>
	<link rel="stylesheet" href="assets/main-style.css"/>
	<link rel="stylesheet" href="assets/ausstage-colours.css"/>
	<link rel="stylesheet" href="assets/ausstage-background-colours.css"/>
	<link rel="stylesheet" href="assets/jquery-ui/jquery-ui-1.8.6.custom.css"/>
	<jsp:include page="analytics.jsp"/>
	<!-- libraries -->
	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.5.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-ui-1.8.6.custom.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.ajaxmanager-3.0.9.js"></script>
	<!-- custom code -->
	<script type="text/javascript" src="assets/javascript/index.js"></script>
</head>
<body>
<div class="wrapper">
	<div class="header b-187"><h1>Aus-e-Stage Service Development</h1></div>
	<div class="sidebar b-186 f-184">
		<!-- side bar content -->
		<div class="mainMenu">
			<h2>Main Menu</h2>
			<ul>
				<li><a href="http://www.ausstage.edu.au" title="AusStage Homepage">AusStage Website</a></li>
				<li><a href="/mapping/" title="AusStage Mapping Service (Alpha)">Mapping Service (Alpha)</a></li>
				<li><a href="/mapping2/" title="AusStage Mapping Service (Beta)">Mapping Service (Beta)</a></li>
				<li><a href="/networks/" title="AusStage Navigating Networks Service (Beta)">Navigating Networks</a></li>
				<li><a href="/mobile/" title="AusStage Researching Audiences Service (Beta)">Researching Audiences</a></li>
			</ul>
		</div>
	</div>
	<div class="main b-184 f-187">
		<!-- main content -->
		<div id="tabs" class="tab-container">
			<ul class="fix-ui-tabs">
				<li><a href="#tabs-1">Project Overview</a></li>
				<li><a href="#tabs-2">Analytics</a></li>
				<li><a href="#tabs-3">Extras</a></li>
				<li><a href="#tabs-4">Contact Us</a></li>
			</ul>
			<div>
				<div id="tabs-1" class="tab-content">
					<h2>Overview of the Aus-e-Stage Project</h2>
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
						<h3>These services are:</h3>
					</p>
					<ul class="services">
						<li><a href="/mapping/" title="AusStage Mapping Service (Alpha)"><h4>Mapping Service (Alpha)</h4>
                        
                       	<img src="assets/images/map-screengrab.jpg" width="200" height="150" alt="Mapping"></a>
                          
                           <p><strong>Visualise AusStage data on a map.</strong></p><p>Will be retired once all of the functionality is migrated into the beta service.</p>                           
                           </li>
                           
						<li><a href="/mapping2/" title="AusStage Mapping Service (Beta)"><h4>Mapping Service (Beta</h4>
                        
                        
                          <img src="assets/images/map-screengrab.jpg" width="200" height="150" alt="Mapping"></a>
							<p> <strong> Visualise AusStage data on a map. </strong></p><p>Undergoing active testing and development.</p>
						</li>
                        
                        
						<li><a href="/networks/" title="AusStage Navigating Networks Service (Beta)"><h4>Navigating Networks</h4> 
                        
                                 <img src="assets/images/networks-screengrab.jpg" width="200" height="150" alt="Networks"></a>

                           <p><strong>Visualise networks of artistic collaboration</strong></p><p>Undergoing active testing and development.</p>                           

						</li>
                        
                        
						<li><a href="/mobile/" title="AusStage Researching Audiences Service (Beta)"><h4>Researching Audiences</h4>
                        
                           <img src="assets/images/response-screengrab.jpg" width="200" height="150" alt="Mobile - screen grab of an iphone "> </a>
                           <p><strong>Gather feedback from audiences using mobile devices</strong><p></p> Best viewed using mobile devices such as smartphones</p>                          
                       
						</li>
					</ul>
                    
					<p class="clear">
						The source code for these services is available on the <a href="http://code.google.com/p/aus-e-stage/" title="Source code repository, documentation wiki etc.">aus-e-stage</a> project hosted on Google Code.
					</p>
				</div>
				<div id="tabs-2" class="tab-content">
					<div id="analytics-tabs">
						<ul class="fix-ui-tabs">
							<li><a href="#analytics-1">Mapping Service</a></li>
							<li><a href="#analytics-2">Networks Service</a></li>
							<li><a href="#analytics-3">Mobile Service</a></li>
							<li><a href="#analytics-4">AusStage Website</a></li>
							<li><a href="#analytics-5">AusStage Database</a></li>
							<li><a href="#analytics-6">Data Exchange Service</a></li>
						</ul>
						<div id="analytics-1">
													
						</div>
						<div id="analytics-2">
							
						</div>
						<div id="analytics-3">
							
						</div>
						<div id="analytics-4">
							
						</div>
						<div id="analytics-5">
							
						</div>
						<div id="analytics-6">
							
						</div>
					</div>
				</div>
				<div id="tabs-3" class="tab-content">
					<h2>Extras &amp; Utilities</h2>
					<p>Coming Soon...</p>
					<!--
					<p>
					On this page we list small applications, scripts and web pages that we're exploring that may be of use to others in the AusStage community. <br/>The first such extra is a bookmarklet to show maps in AusStage.
					</p>
					<h2>Map Bookmarklet</h2>
					<p>This <a href="http://en.wikipedia.org/wiki/Bookmarklet" title="Wikipedia article on this topic">bookmarklet</a> makes it easy to link into the <a href="http://beta.ausstage.edu.au/mapping/" title="Mapping Service Homepage">Mapping Service</a> from an organisation record in <a href="http://www.ausstage.edu.au/" title="AusStage homepage">AusStage</a>. 
					   <br/>&nbsp;<br/>
					   To use the bookmarklet simply drag the link below to your bookmarks bar. Then, when you're viewing an organisation record in AusStage, you can click the bookmark to view the map for the organisation in the Mapping Service.
					   <br/>&nbsp;<br/>
						<a href="javascript:(function(){document.body.appendChild(document.createElement('script')).src='http://beta.ausstage.edu.au/assets/javascript/map-bookmarklet.js';})();"
						  title="Link directly from an organisation record to the Mapping Service"/>View AusStage Map</a>
					</p>
					-->
				</div>
				<div id="tabs-4" class="tab-content">
					<h2>Contact the Aus-e-Stage team</h2>
					<p>
						We encourage you to explore our services and provide as much feedback as you wish. Please contact us using the details below:
					</p>
					<p><strong>Aus-e-Stage Project Manager: </strong>Mrs Liz Milford, (08) 8201 2085, <a href="mailto:liz.milford@flinders.edu.au" title="Email Liz">liz.milford@flinders.edu.au</a></p>
					<p><strong>Aus-e-Stage Software Engineer: </strong>Mr Corey Wallis, (08) 8201 5818, <a href="mailto:corey.wallis@flinders.edu.au" title="Email Corey">corey.wallis@flinders.edu.au</a></p>
				</div>
			</div>
		</div>
	</div>
	<!-- always at the bottom of the content -->
	<div class="push"></div>
</div>
<jsp:include page="footer.jsp"/>
</body>
</html>
