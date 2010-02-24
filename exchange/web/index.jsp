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
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
	<title>AusStage Data Exchange Service (Beta)</title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/> 
	<link rel="stylesheet" type="text/css" media="screen" href="assets/main-style.css" />
	<script type="text/javascript" src="assets/syntaxhighlighter/src/shCore.js"></script>
	<script type="text/javascript" src="assets/syntaxhighlighter/scripts/shBrushJScript.js"></script>
	<script type="text/javascript" src="assets/syntaxhighlighter/scripts/shBrushXml.js"></script>
	<link href="assets/syntaxhighlighter/styles/shCore.css" rel="stylesheet" type="text/css" />
	<link href="assets/syntaxhighlighter/styles/shThemeDefault.css" rel="stylesheet" type="text/css"/>
	<script type="text/javascript">
		SyntaxHighlighter.config.clipboardSwf = 'assets/syntaxhighlighter/scripts/clipboard.swf';
		SyntaxHighlighter.all()
	</script>
</head>
<body>
<div id="wrap">
	<div id="header"><h1>AusStage Data Exchange Service (Beta)</h1></div>
	<div id="nav">
		<ul>
			<li>&nbsp;</li>
		</ul>
	</div>
	<!-- Include the sidebar -->
	<jsp:include page="sidebar.jsp"/>
	<div id="main">
		<h2>Access AusStage Data for your Website</h2>
		<p>
			The AusStage Data Exchange Service provides a mechanism for users to include AusStage data in their websites dynamically through the use of specially crafted URLs.
		</p>
		<p>
			The service can be used to include data directly in a web page using <a href="#javascript" title="">JavaScript</a> or by using one of the four data formats currently supported by the service. 
		</p>
		<p>
			To use this service it is necessary to know the unique identifier of the organisation, contributor or venue that you wish to retrieve event data about.
			For example to retrieve event data about organisations it is necessary to know the unique Organisation Identifier for the organisation, or organisations, that are of interest.
			These numbers are displayed at the bottom of the record details page in the AusStage website. 
		</p>
		<p>
			Please note that the service creates a <a href="#logging" title="">log file</a> that contains information that helps us in monitoring the service.
		</p>
		<p>
			Users access the service via a URL that has two required parameters and a number of optional parameters. If an optional parameter is not specified as part of the URL the default value will be used. The parameters are outlined in the table below.
		</p>
		<table class="mapLegend">
			<thead>
				<tr>
					<th>Parameter</th><th>Description</th><th>Allowed values</th><th>Required</th>
				</tr>
			</thead>
			<tr style="border-bottom: 1px solid #333333;">
				<th scope="row" style="text-align: left">type</th>
				<td>
					Specifies the type of unique identifier that will be used to retrieve event information. 
					<br/>&nbsp;<br/>
					For example the <em>organisation</em> type retrieves event information associated with the specified organisation(s).
				<td>
					<ul>
						<li>organisation</li>
						<li>contributor</li>
						<li>venue</li>
					</ul>
				</td>
				<td>
					Yes
				</td>
			</tr>
			<tr style="border-bottom: 1px solid #333333;" class="odd">
				<th scope="row" style="text-align: left">id</th>
				<td>
					Specified the id number, or numbers that will be used to identify the organisation(s), contributor(s) or venue(s) that are of interest. 
					<br/>&nbsp;<br/>
					It is possible to list up to ten unique id numbers as a comma delimited list.
				<td>
					&nbsp;
				</td>
				<td>
					Yes
				</td>
			</tr>
			<tr style="border-bottom: 1px solid #333333;">
				<th scope="row" style="text-align: left">output</th>
				<td>
					Specifies the format of the output.
					<br/>&nbsp;<br/>
					By default the data will formatted as a un ordered list in HTML. The list will have the class "ausstage_events" associated with it so that users can style the list to match more accurately their individual website design.
				<td>
					<ul>
						<li>html (default)</li>
						<li>json</li>
						<li>xml</li>
						<li>rss</li>
					</ul>
				</td>
				<td>
					No
				</td>
			</tr>
			<tr style="border-bottom: 1px solid #333333;" class="odd">
				<th scope="row" style="text-align: left">limit</th>
				<td>
					Specifies the number of event records that will be returned
					<br/>&nbsp;<br/>
					The rss output format, defined above, will always only return ten records. 
					<br/>&nbsp;<br/>
					Records will always be sorted in reverse chronological order before the record limit is applied. 
				<td>
					<ul>
						<li>10 (default)</li>
						<li>all</li>
						<li>An arbitary number</li>
					</ul>
				</td>
				<td>
					No
				</td>
			</tr>
			<tr style="border-bottom: 1px solid #333333;">
				<th scope="row" style="text-align: left">script</th>
				<td>
					Specifies that the data should be enclosed within a JavaScript function.
					<br/>&nbsp;<br/>
					The use of this option is explained in the <a href="#javascript" title="">JavaScript</a> section of this document
				<td>
					<ul>
						<li>true</li>
					</ul>
				</td>
				<td>
					No
				</td>
			</tr>
		</table>
		<p>
			The following are example URLs that show how these options can be combined to customise the type of data retrieved using this service. 
		</p>
		<ul>
			<li>
				A URL that requests event details for the organisation with id 102 in the default format and default record limit:
				<br/><a href="http://beta.ausstage.edu.au/exchange/data?type=organisation&id=102" title="Example URL">http://beta.ausstage.edu.au/exchange/data?type=organisation&amp;id=102</a>
			</li>
			<li>
				A URL that requests event details for the organisation with id 102 in the xml format and with a limit of 20 records:
				<br/><a href="http://beta.ausstage.edu.au/exchange/data?type=organisation&id=102&output=xml&limit=20" title="Example URL">http://beta.ausstage.edu.au/exchange/data?type=organisation&amp;id=102&amp;output=xml&amp;limit=20</a>
			</li>
			<li>
				A URL that requests event details for the organisation with ids 102 and 11898 in the default format and with a limit of 20 records:
				<br/><a href="http://beta.ausstage.edu.au/exchange/data?type=organisation&id=102,11898&limit=20" title="Example URL">http://beta.ausstage.edu.au/exchange/data?type=organisation&amp;id=102,11898&amp;limit=20</a>
			</li>
			<li>
				A URL that requests event details for the contributor with id 6139 in the rss format:
				<br/><a href="http://beta.ausstage.edu.au/exchange/data?type=contributor&id=6139&output=rss" title="Example URL">http://beta.ausstage.edu.au/exchange/data?type=contributor&amp;id=6139&amp;output=rss</a>
			</li>
		</ul>
		<a id="javascript"></a><h3>Using the JavaScript Option</h3>
		<p>
			The use of the JavaScript option is best illustrated with an example. Assume that you need to include the most recent 10 events for an organisation in a web page. 
			<br/>The basic skeleton HTML code for the webpage looks like this:
		</p>
		<script type="syntaxhighlighter" class="brush: xml">
		<![CDATA[
<html>
  <head>
    <title>AusStage Data Exchange Service Demonstration</title>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/> 
  </head>
  <body>
    <h1>Events for our organisation</h1>
  </body>
</html>
		]]></script>
		<p>
			The first task is to build a URL with the appropriate parameters. Using the parameters as listed in the above table the URL would be as follows:
			<br/>&nbsp;<br/>
			http://beta.ausstage.edu.au/exchange/data?type=organisation&amp;id=102&amp;limit=10
			<br/>&nbsp;<br/>
			Once the basic URL is defined it can be used as the value for the src attribute for a script tag when the <em>script=true</em> parameter is also added.
			<br/>The basic skeleton HTML code for the webpage now looks like this:
		</p>		
		<script type="syntaxhighlighter" class="brush: xml; auto-links: false">
		<![CDATA[
<html>
  <head>
    <title>AusStage Data Exchange Service Demonstration</title>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/> 
    <script type="text/javascript" src="http://beta.ausstage.edu.au/exchange/data?type=organisation&amp;id=102&amp;limit=10&amp;script=true">&lt;/script&gt;
  </head>
  <body>
    <h1>Events for our organisation</h1>
  </body>
</html>
		]]></script>
		<p>
			The second task is to add a div tag that will hold the list of events retrieved from AusStage.
			<br/>The basic skeleton HTML code for the webpage now looks like this:
		</p>
<script type="syntaxhighlighter" class="brush: xml; auto-links: false">
		<![CDATA[
<html>
  <head>
    <title>AusStage Data Exchange Service Demonstration</title>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/> 
    <script type="text/javascript" src="http://beta.ausstage.edu.au/exchange/data?type=organisation&amp;id=102&amp;limit=10&amp;script=true">&lt;/script&gt;
  </head>
  <body>
    <h1>Events for our organisation</h1>
    <div id="events_from_ausstage"></div>
  </body>
</html>
		]]></script>
		<p>
			The third task is to include some additional JavaScript code that will use the returned data and add it to the web page. A simplistic version of this type of 
			JavaScript looks like this:
		</p>
		<script type="syntaxhighlighter" class="brush: js; auto-links: false">
		<![CDATA[
function add_events() {
  document.getElementById("events_from_ausstage").innerHTML = ausstage_data();
}
]]></script>
		<ul>
			<li>This defines a function called <em>add_events()</em> which can be called on page load</li>
			<li>The <em>ausstage_data()</em> function is defined by the script tag that was added in the previous section</li>
			<li>The <em>document.getElementById("events_from_ausstage").innerHTML</em> snippet takes the data returned by the function and appends it to the div tag with the id "events_from_ausstage"</li>
		</ul>
		<p>
			Adding the JavaScript code to the sekeleton page makes it look like this:
		</p>
<script type="syntaxhighlighter" class="brush: xml; auto-links: false">
<![CDATA[
<html>
  <head>
    <title>AusStage Data Exchange Service Demonstration</title>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/> 
    <script type="text/javascript" src="http://beta.ausstage.edu.au/exchange/data?type=organisation&amp;id=102&amp;limit=10&amp;script=true">&lt;/script&gt;
    <script type="text/javascript">
      function add_events() {
        document.getElementById("events_from_ausstage").innerHTML = ausstage_data();
      }
    &lt;/script&gt;
  </head>
  <body>
    <h1>Events for our organisation</h1>
    <div id="events_from_ausstage"></div>
  </body>
</html>
		]]></script>
		<p>
			The last task is to include one final peice of JavaScript that will call the <em>add_events()</em> function to the <em>body</em> body tag in the HTML. 
			Adding the JavaScript code to the sekeleton page makes it look like this:		
		</p>
		<script type="syntaxhighlighter" class="brush: xml; auto-links: false">
<![CDATA[
<html>
  <head>
    <title>AusStage Data Exchange Service Demonstration</title>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/> 
    <script type="text/javascript" src="http://beta.ausstage.edu.au/exchange/data?type=organisation&amp;id=102&amp;limit=10&amp;script=true">&lt;/script&gt;
    <script type="text/javascript">
      function add_events() {
        document.getElementById("events_from_ausstage").innerHTML = ausstage_data();
      }
    &lt;/script&gt;
  </head>
  <body onload="add_events();">
    <h1>Events for our organisation</h1>
    <div id="events_from_ausstage"></div>
  </body>
</html>
		]]></script>
		<p>
			You can see how this page works by looking at this <a href="sample.html" title="Web Service Example">sample page</a>.
		</p>
		<p>
			This is the most simplistic way to call the Data Exchange Service. In a prodution environment error checking on the JavaScript should be included and more advanced manipulation of the HTML coud be achieved using a library such as <a href="http://jquery.com" title="More information about this library">JQuery</a>. 
		</p>
		<p>
			If more complex customisation of the data is required this technique can be used with the <em>json</em> or <em>xml</em> output types and processed with JavaScript or other scripting language. It is anticipated that the <em>rss</em> output type will be most useful for those who have blog hosted on such services as <a href="http://www.blogger.com/" title="">www.blogger.com</a> or <a href="http://wordpress.com" title="">wordpress.com</a> and wish to include event information in their sidebar.
		</p>
		<a id="logging"></a><h3>Logging of information by the service</h3>
		<p>To assist us in monitoring the service the following information is kept in a log file:</p>
		<ul>
			<li>The request <a href="http://en.wikipedia.org/wiki/Query_string" title="Wikipedia article on this topic">query string</a>.</li>
			<li>The <a href="http://en.wikipedia.org/wiki/HTTP_referrer" title="Wikipedia article on this topic">HTTP referrer</a> header.<li>
			<li>The <a href="http://en.wikipedia.org/wiki/IP_address" title="Wikipedia article on this topic">IP address</a> of the computer that made the request.</li>
		</ul>
		<p>This information is retained to help us to identify, websites that are using the service and the data formats are most popular.</p>
	</div>
	<!-- include the footer -->
	<jsp:include page="footer.jsp"/>
</div>
<!-- include the Google Analytics code -->
<jsp:include page="analytics.jsp"/>
</body>
</html>
