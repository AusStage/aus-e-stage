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
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<title>Aus-e-Stage Project</title>
	<link rel="stylesheet" href="assets/main-style.css"/>
	<link rel="stylesheet" href="assets/ausstage-colours.css"/>
	<link rel="stylesheet" href="assets/ausstage-background-colours.css"/>
	<link rel="stylesheet" href="assets/jquery-ui/jquery-ui-1.8.6.custom.css"/>
	<jsp:include page="analytics.jsp"/>
	<!-- libraries -->
	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.6.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-ui-1.8.12.custom.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.ajaxmanager-3.0.9.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.form-2.64.js"></script>
	<!-- custom code -->
	<script type="text/javascript" src="assets/javascript/index.js"></script>
	<script type="text/javascript" src="assets/javascript/tab-selector.js"></script>
</head>
<body>
<div class="wrapper">
	<div class="header b-187"><h1>Aus-e-Stage Project</h1></div>
	<div class="sidebar b-186 f-184">
		<!-- side bar content -->
		<div class="mainMenu">
			<ul>
				<li>&nbsp;</li>
				<li><a href="http://www.ausstage.edu.au" title="AusStage Website homepage">AusStage Website</a></li>
				<li><a href="http://beta.ausstage.edu.au/" title="Aus-e-Stage Project homepage">Aus-e-Stage Project</a></li>
				<li><a href="http://beta.ausstage.edu.au/mapping" title="Mapping Events homepage">Mapping Events</a></li>
				<li><a href="http://beta.ausstage.edu.au/networks" title="Navigating Networks homepage">Navigating Networks</a></li>
				<li><a href="http://beta.ausstage.edu.au/mobile/mobile-vis/" title="Researching Audiences homepage">Researching Audiences</a></li>
				<li><a href="http://code.google.com/p/aus-e-stage/wiki/StartPage" title="Project Wiki homepage">Project Wiki</a></li>
				<li><a href="http://beta.ausstage.edu.au/?tab=contacts" title="Contact information">Contact Us</a></li>
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
					<h1 id="mainHeader">Aus-e-Stage Project Overview</h1>
					<p>
						<a href="http://www.ausstage.edu.au" title="AusStage Website homepage">AusStage</a> fulfils a national need for public access to reliable information on the full spectrum of live performance in Australia. It delivers a dataset of national significance to researchers, postgraduate students, policy makers in government and industry practitioners. However, conventional database methods of text-based search-and-retrieval are, on their own, no longer sufficiently effective in meeting the evolving needs of research.
					</p>
					<p>
						The <a href="http://beta.ausstage.edu.au/" title="Aus-e-Stage Project homepage">Aus-e-Stage project</a> is developing new visual interfaces to enable researchers to interact more flexibly with the <a href="http://www.ausstage.edu.au" title="AusStage Website homepage">AusStage</a> dataset. Three new services are developed to operate alongside the current <a href="http://www.ausstage.edu.au" title="AusStage Website homepage">AusStage</a> search-and-retrieval service. These services are:
					<p>
					<ul class="services">
						<li>
							<a href="http://beta.ausstage.edu.au/mapping" title="Mapping Events homepage">
								<h3>Mapping Events</h3>
								<img src="assets/images/map-screengrab.jpg" width="200" height="150" alt="">
							</a>
							<p>
								<strong>Visualise live performance on a map</strong>
							</p>
						</li>
						<li>
							<a href="http://beta.ausstage.edu.au/networks" title="Navigating Networks homepage">
								<h3>Navigating Networks</h3>
								<img src="assets/images/networks-screengrab.jpg" width="200" height="150" alt="">
							</a>
							<p>
								<strong>Explore networks of artistic collaboration</strong>
							</p>
						</li>
						<li>
							<a href="http://beta.ausstage.edu.au/mobile/mobile-vis/" title="Researching Audiences homepage">
								<h3>Researching Audiences</h3>
								<img src="assets/images/response-screengrab.jpg" width="200" height="150" alt=""> 
							</a>
							<p>
								<strong>Gather feedback from audiences using mobile devices</strong>
							</p>
						</li>
					</ul>
					<p class="clear">
						<a href="http://beta.ausstage.edu.au/" title="Aus-e-Stage Project homepage">Aus-e-Stage</a> is funded by <a href="http://www.pfc.org.au/bin/view/Main/NeAT" title="NeAT homepage">NeAT</a>, the National eResaerch Architecture Taskforce. The source code for these services is available on the <a href="http://code.google.com/p/aus-e-stage/" title="Aus-e-Stage Project Wiki and Source Code Repository">aus-e-stage</a> project hosted on Google Code.
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
								This bookmarklet acts as a link between the <a href="http://www.ausstage.edu.au" title="AusStage Website homepage">AusStage Website</a> and the <a href="http://beta.ausstage.edu.au/mapping" title="Mapping Events homepage">Aus-e-Stage Mapping Events</a> service. When you are viewing an event, contributor, organisation or venue in AusStage, clicking on the bookmarklet will redirect your browser to the Aus-e-Stage Mapping Events service and build a map for the record you had displayed.
							</p>
							<p>
								To use the bookmarklet simply drag the link below to your bookmarks bar.
							</p>
							<p>
								<a href="javascript:(function(){document.body.appendChild(document.createElement('script')).src='http://beta.ausstage.edu.au/assets/javascript/map-bookmarklet.js';})();" title="Link directly from AusStage into the Mapping Service"/>View AusStage Map</a>
							</p>
							<p>
								<a href="http://beta.ausstage.edu.au/?tab=extras&section=bookmarklet" title="Persistent link to this tab">Persistent Link</a> to this section.
							</p>
							</div>
						</div>
						<div id="extras-2">
							<h1>AusStage Colour Scheme</h1>
							<p>
								The AusStage <a href="http://aus-e-stage.googlecode.com/svn/trunk/common-web-assets/ausstage-colour-scheme.html" title="HTML version of the Colour Scheme">colour scheme</a> is documented in the Aus-e-Stage project <a href="http://code.google.com/p/aus-e-stage/wiki/AusStageColourScheme" title="Direct link to the page in the wiki">wiki</a>.
								To ensure consistency across all of our services the CSS files are stored in our <a href="http://code.google.com/p/aus-e-stage/source/browse/#svn%2Ftrunk%2Fcommon-web-assets" title="Browse the Source Code Repository">source code repository</a>. 
							<p>
								The colour scheme was developed using <a href="http://www.colorschemer.com/" title="ColourSchemer Studio homepage">ColourSchemer Studio</a>, version 2.0.1.
								Use the form below to turn the CSS generated by ColourSchemer into:
							</p>
							<ul>
								<li>The CSS rules for <a href="http://aus-e-stage.googlecode.com/svn/trunk/common-web-assets/ausstage-colours.css" title="Download the CSS">foreground colours</a></li>
								<li>The CSS rules for <a href="http://aus-e-stage.googlecode.com/svn/trunk/common-web-assets/ausstage-background-colours.css" title="Download the CSS">background colours</a></li>
								<li>The colours transformed into the syntax used in <a href="http://aus-e-stage.googlecode.com/svn/trunk/common-web-assets/kml-colours.xml" title="Download the XML file">KML files</a></li>
							</ul>
							<p>
								<a href="http://beta.ausstage.edu.au/?tab=extras&section=colours" title="Persistent link to this tab">Persistent Link</a> to this section.
							</p>
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
					<p>
						<a href="http://beta.ausstage.edu.au/?tab=contacts" title="Persistent link to this tab">Persistent Link</a> to this section.
					</p>
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
