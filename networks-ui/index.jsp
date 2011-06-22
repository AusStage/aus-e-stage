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
	<title>Navigating Networks</title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/> 

	<!-- css -->
	<link rel="stylesheet" href="assets/networks.css"/>
	<link rel="stylesheet" href="assets/ausstage-colours.css">
	<link rel="stylesheet" href="assets/ausstage-background-colours.css">
	<link rel="stylesheet" href="assets/jquery-ui/jquery-ui-1.8.6.custom.css" type="text/css" />
	<link rel="stylesheet" type="text/css" href="assets/jquery-ui/jquery.multiselect.css" />	
	<link rel="stylesheet" type="text/css" href="assets/jquery-ui/jquery.multiselect.filter.css" />	
	<link rel="Stylesheet" href="assets/jquery-ui/ui.slider.extras.css" type="text/css" />
	<link rel="stylesheet" href="assets/jquery-ui/common.css" type="text/css" />
    <!-- Google Analytics Script -->
<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-10089663-2']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>

    
</head>

<body>

<span id="ruler"></span>

<div class="wrapper">
	<div class="header b-187"><h1>Navigating Networks</h1></div>

	<!-- Sidebar -->
	<div class="sidebar b-186 f-184">
		<div class="mainMenu">
			<ul>
				<li>&nbsp;</li>
				<li><a href="http://www.ausstage.edu.au" title="AusStage Website homepage">AusStage Website</a></li>
				<li><a href="http://beta.ausstage.edu.au/" title="Aus-e-Stage Project homepage">Aus-e-Stage Project</a></li>
				<li><a href="http://beta.ausstage.edu.au/mapping/" title="Mapping Events homepage">Mapping Events</a> </li>	
				<li><a href="index.jsp" title="Navigating Networks homepage">Navigating Networks</a> </li>	
				<li><a href="http://beta.ausstage.edu.au/mobile/mobile-vis/" title="Researching Audiences homepage">Researching Audiences</a> </li>	
				<li><a href="http://code.google.com/p/aus-e-stage/wiki/StartPage" title="Project Wiki homepage">Project Wiki</a> </li>	
				<li><a href="http://beta.ausstage.edu.au/?tab=contacts" title="Contact information">Contact Us</a> </li>	
			</ul>
		</div>
	</div>

	<!--main display window. -->
	<div class="main b-184 f-187">
		<h2>Explore networks of artistic collaboration</h2>
		<p>	The <a href="index.jsp" >Navigating Networks</a> service provides an interactive interface for exploring and analysing networks of artistic 
			collaborations in the <a href="http://www.ausstage.edu.au" title="AusStage Homepage">AusStage</a> database.
		</p>
		<p>Network visualisation and analysis can reveal patterns of collaboration between artists which have previously been 
			unrepresentable using conventional text-based displays. This service presents existing data in new ways, allowing researchers 
			to interrogate the collaborations that underpin creativity in the performing arts.
		</p>
		<p>Explore networks of artistic collaboration using the <a href="networks.jsp" title="Navigating Networks">Navigating 
			Networks</a>  interface. You can also <a href="export.jsp" title="Export Graph Data">Export Graph Data</a>  for use 
			in stand-alone applications such as <a href="http://visone.info" title="Visone">Visone</a> 
			and <a href="http://gephi.org" title="Gephi">Gephi</a>.
		</p>
		<ul class="services">
		<li>
			<a href="networks.jsp" title="Navigating Networks">
				<h3>Navigating Networks</h3>
				<img src="assets/images/networks-screengrab.jpg" width="200" height="150" alt="">
			</a>
		</li>
		<li>
			<a href="export.jsp" title="Export Graph Data">
				<h3>Export Graph Data</h3>
				<img src="assets/images/export-screengrab.jpg" width="200" height="150" alt="">
			</a>
		</li>
		</ul>

		
		<p class="clear">Note: The interface for browsing networks uses the 
		<a href="http://vis.stanford.edu/protovis/">Protovis</a> toolkit for visualisation;
		 it only works in up-to-date browsers, such as  
		<a href="http://www.apple.com/safari/" title="More information about this browser">Safari 5.x</a>, 
		<a href="http://www.mozilla.com/en-US/" title="More information about this browser">Firefox 3.6x</a> and 
		<a href="http://www.google.com/chrome/" title="More information about this browser">Google Chrome 6.x</a>.
		</p>
		<p>
			More information on the service is available in our Wiki including:
		</p>
		<ul>
			<li>
				<a href="http://code.google.com/p/aus-e-stage/wiki/NavigatingNetworksSpecification" title="">Navigating Networks Specification</a> - 
				Outline the service and provide context to the development
			</li>
		</ul>
	</div>
	<!-- always at the bottom of the content -->
	<div class="push"></div>
</div>
	<!-- include the footer -->
	<jsp:include page="footer.jsp"/>

<!-- include the Google Analytics code -->
<jsp:include page="analytics.jsp"/>
</body>
</html>
