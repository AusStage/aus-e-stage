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
	<script type="text/javascript" src="assets/javascript/libraries/jquery.form-2.64.js"></script>
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
				<li><a href="/mapping/" title="Aus-e-Stage Mapping Service (Alpha)">Mapping Service (Alpha)</a></li>
				<li><a href="/mapping2/" title="Aus-e-Stage Mapping Service (Beta)">Mapping Service (Beta)</a></li>
				<li><a href="/networks/" title="Aus-e-Stage Navigating Networks Service (Beta)">Navigating Networks</a></li>
				<li><a href="/mobile/" title="Ause-e-Stage Researching Audiences Service (Beta)">Researching Audiences</a></li>
				<li><a href="http://code.google.com/p/aus-e-stage/wiki/StartPage" title="Aus-e-Stage Project Documentation.">Aus-e-Stage Wiki</a></li>
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
					<h1>Aus-e-Stage Project Overview</h1>
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
						<h2>These services are:</h2>
					</p>
					<ul class="services">
						<li>
							<a href="/mapping/" title="AusStage Mapping Service homepage">
								<h3>Mapping Service</h3>
								<img src="assets/images/map-screengrab.jpg" width="200" height="150" alt="">
							</a>
							<p>
								<strong>Visualise AusStage data on a map</strong>
							</p>
						</li>
						<li>
							<a href="/networks/" title="AusStage Navigating Networks Service">
								<h3>Navigating Networks</h3>
								<img src="assets/images/networks-screengrab.jpg" width="200" height="150" alt="">
							</a>
							<p>
								<strong>Visualise networks of artistic collaboration</strong>
							</p>
						</li>
						<li>
							<a href="http://code.google.com/p/aus-e-stage/wiki/MobileService" title="Information about the AusStage Researching Audiences Service">
								<h3>Researching Audiences</h3>
								<img src="assets/images/response-screengrab.jpg" width="200" height="150" alt=""> 
							</a>
							<p>
								<strong>Gather feedback from audiences using mobile devices</strong>
							</p>
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
					<div id="extras-tabs">
						<ul class="fix-ui-tabs">
							<li><a href="#extras-1">Map Bookmarklet</a></li>
							<li><a href="#extras-2">AusStage Colour Scheme</a></li>
						</ul>
						<div id="extras-1">
							<p>
								A <a href="http://en.wikipedia.org/wiki/Bookmarklet" title="Wikipedia article on this topic">bookmarklet</a> is a link that you can save in the bookmarks bar or the bookmarks list in your browser. 
							</p>
							<p>
								The bookmarklet below acts as a link between the <a href="http://www.ausstage.edu.au" title="AusStage Homepage">AusStage Website</a> and the <a href="/mapping2/" title="Aus-e-Stage Mapping Service (Beta)">Aus-e-Stage Mapping Service</a>. 
								When you are viewing an Index Drill Down page or a record page in AusStage clicking on the bookmarklet will 
								automatically redirect your browser to the Aus-e-Stage mapping service and build a map for the contributor, organisation, venue, or event record that you had displayed.
							</p>
							<p>
								To use the bookmarklet simply drag the link below to your bookmarks bar.
							</p>
							<p>
								<a href="javascript:(function(){document.body.appendChild(document.createElement('script')).src='http://beta.ausstage.edu.au/assets/javascript/map-bookmarklet.js';})();" title="Link directly from AusStage into the Mapping Service"/>View AusStage Map</a>
							</p>
						</div>
						<div id="extras-2">
							<h1>AusStage Colour Scheme</h1>
							<p>
								The <a href="http://aus-e-stage.googlecode.com/svn/trunk/common-web-assets/ausstage-colour-scheme.html" title="HTML version of the Colour Scheme">AusStage colour scheme</a> that is being used for all of the Aus-e-Stage services is documented in <a href="http://code.google.com/p/aus-e-stage/wiki/AusStageColourScheme" title="More Information on the Colour Scheme">our project wiki</a>.
							</p>
							<p>
								To ensure consistency across all of our services the CSS files are stored in our <a href="http://code.google.com/p/aus-e-stage/source/browse/#svn%2Ftrunk%2Fcommon-web-assets" title="Browse the Source Code Repository">source code repository</a>. 
								The colour scheme was developed using <a href="http://www.colorschemer.com/" title="ColourSchemer Studio homepage">ColourSchemer Studio</a>, version 2.0.1.
							</p>
							<p>
								Use the form below to turn the CSS generated by ColourSchemer into:
							</p>
							<ul>
								<li>The CSS rules for <a href="http://aus-e-stage.googlecode.com/svn/trunk/common-web-assets/ausstage-colours.css" title="Download the CSS">foreground colours</a></li>
								<li>The CSS rules for <a href="http://aus-e-stage.googlecode.com/svn/trunk/common-web-assets/ausstage-background-colours.css" title="Download the CSS">background colours</a></li>
								<li>The colours transformed into the syntax used in <a href="http://aus-e-stage.googlecode.com/svn/trunk/common-web-assets/kml-colours.xml" title="Download the XML file">KML files</a></li>
							</ul>
							<h2 style="padding-top: 5px;">Transform the CSS</h2>
							<p>
								Copy &amp; Paste the CSS output from the ColourSchemer application into the field below and click the 'Transform CSS' button.
							</p>
							<form action="/" method="get" id="css-input-form" name="css-input-form">
								<table class="formTable">
									<tbody>
									<tr>
										<td>
											<textarea cols="50" rows="5" name="source" id="source"></textarea>
										</td>
									</tr>
									<tr>
										<td style="text-align: left">
											<input type="submit" name="submit" id="css-input-btn" value="Transform CSS"/>
										</td>
									</tr>
									</tbody>
								</table>
							</form>
							<h3>Foreground Colour CSS</h3>
							<textarea cols="50" rows="5" name="css-foreground" id="css-foreground"></textarea>
							<h3>Background Colour CSS</h3>
							<textarea cols="50" rows="5" name="css-background" id="css-background"></textarea>
							<h3>Colours in KML Syntax</h3>
							<textarea cols="50" rows="5" name="kml-colours" id="kml-colours"></textarea>
						</div>
					</div>
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
