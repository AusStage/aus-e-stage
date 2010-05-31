<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="java.awt.Color"%>
<%
/*
 * This file is part of the AusStage Mapping Service
 *
 * The AusStage Mapping Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Mapping Service is distributed in the hope that it will 
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
	Color maleColour   = createColorObject("00FFFF");
	Color femaleColour = createColorObject("FF0080");
	Color totalColour  = createColorObject("FFF600");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
	<title>AusStage Mapping Service (Beta)</title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/> 
	<link rel="stylesheet" type="text/css" media="screen" href="assets/main-style.css"/>
</head>
<body>
<div id="wrap">
	<div id="header"><h1>AusStage Mapping Service (Beta)</h1></div>
	<div id="nav">
	</div>
	<!-- Include the sidebar -->
	<jsp:include page="sidebar.jsp"/>
	<div id="main">
		<a name="top"></a>
		<h2>Map Overlays Using Australian Bureau Statistics</h2>
		<p>
			All of the maps available on the AusStage Mapping Service can be downloaded as <a href="http://en.wikipedia.org/wiki/KML" title="Wikipedia article on this topic">Keyhole Markup Language</a> (KML) files. These files use an <a href="http://en.wikipedia.org/wiki/XML" title="Wikipedia article on this topic">XML</a> based language
			to describe overlays that may be loaded in Geographic Information Systems (GIS) such as <a href="http://earth.google.com" title="Google Earth homepage">Google Earth</a>. 
		</p>
		<p>
			All you need to do is click one of the download KML links above the map and a kml file will be downloaded to your computer. 
		</p>
		<p>
			In the near future available for download will be a series of overlays. These overlays are used in conjunction with an organisation, contributor or event map downloaded from the AusStage mapping service and they
			provide additional information and context to the basic map. 
		</p>
		<p>
			Current versions of the overlay files are available to those users participating in the Alpha 2 round of testing. Please <a href="http://beta.ausstage.edu.au/" title="Project Homepage">contact us</a> should you wish to receive some sample overlays. 
		</p>
		<p>
			The first set of overlays that we're going to make available are those that use data from the <a href="http://www.abs.gov.au/" title="ABS Homepage">Australian Bureau Statistics</a> and more specifically the <a href="http://www.abs.gov.au/census" title="Information about the 2006 census">2006 census</a>. 
		</p>
		<p>
			Each overlay uses one specific dataset and creates a map using the smallest geographic unit available for the 2006 census data, namely the Collection District. More information on the datasets and their associated overlays is outlined below.
		</p>
		<p>
			Information about how we collect and manipulate the data to construct these overlays is available <a href="http://code.google.com/p/aus-e-stage/wiki/StartPage" title="Start Page of the Wiki">on the Wiki</a> that is part of the <a href="http://code.google.com/p/aus-e-stage/" title="Aus-e-Stage project homepage">Aus-e-Stage project</a> hosted on the <a href="http://code.google.com/" title="Google Code Service Homepage">Google Code service</a>. 
		</p>
		<a name="agebysex"></a>
		<h3>Average Age by Sex in 5 Year Groupings</h3>
		<p>
			The average age by sex overlays are constructed using age and population data sources from the 2006 census and matched to collection districts. The average age is determined using a formula similar to that used to determine the average response for a lickert scale question in a survey. 
		</p>
		<p>
			Once the average is determined it is used to choose the approriate fill colour for the collection district in the overlay. The table below outlines the age groupings and the colour used for the male, female and total population datasets.
		</p>
		<p>
			For the purposes of Alpha 2 testing, the following three overlays are available for download:
		<p>
		<ul>
			<li><a href="/mapping/docs/abs-overlay-act-agebysex-male.kmz" title="Open this Overlay in Google Earth">Average Age by Sex (Male) - ACT</a></li>
			<li><a href="/mapping/docs/abs-overlay-act-agebysex-female.kmz" title="Open this Overlay in Google Earth">Average Age by Sex (Female) - ACT</a></li>
			<li><a href="/mapping/docs/abs-overlay-act-agebysex-total.kmz" title="Open this Overlay in Google Earth">Average Age by Sex (Total Population) - ACT</a></li>
		</ul>
		<table class="searchResults" width="100%">
			<thead>
				<tr>
					<th>Age Grouping</th>
					<th>Male Dataset</th>
					<th>Female Dataset</th>
					<th>Total Population</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>Average Not Available</td>
					<td>No Colour</td>
					<td>No Colour</td>
					<td>No Colour</td>

				</tr>
				<tr>
					<td>0-4 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>5-9 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>10-14 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>15-19 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>20-24 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>25-29 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>30-34 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>35-39 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>40-44 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>45-49 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>50-54 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>54-59 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>60-64 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>65-69 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>70-74 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>75-79 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>80-84 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>85-89 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>90-94 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>95-99 years</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
				<%
					maleColour   = darkerColour(maleColour, 0.1);
					femaleColour = darkerColour(femaleColour, 0.1);
					totalColour  = darkerColour(totalColour, 0.1);
				%>
				<tr>
					<td>100 years and over</td>
					<td style="background-color: <%= colorObjectToHTML(maleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(femaleColour)%>">&nbsp;</td>
					<td style="background-color: <%= colorObjectToHTML(totalColour)%>">&nbsp;</td>
				</tr>
			</tbody>
		</table>		
	</div>
	<!-- include the footer -->
	<jsp:include page="footer.jsp"/>
</div>
<!-- include the Google Analytics code -->
<jsp:include page="analytics.jsp"/>
</body>
</html>
<%!
/**
 * A method to take a HTML colour representation and 
 * build a Java Color object
 *
 * @param value the colour value in HTML notation
 *
 * @return      the newly constructed color object
 */
public Color createColorObject(String value) {
	
	if(value.length() != 6) {
		throw new IllegalArgumentException("The HTML colour notation must be six characters long");
	}
	
	// deconstruct the HTML colour into its component parts		
	int red   = Integer.parseInt(value.substring(0, 2), 16);
	int green = Integer.parseInt(value.substring(2, 4), 16);
	int blue  = Integer.parseInt(value.substring(4, 6), 16);
	
	// build a new color object		
	return new Color(red, green, blue);
}
%>
<%!
/** 
 * A method to take a Java Color object and return
 * the HTML representation
 *
 * @param color  the color object
 * @param kml    if set to true return the colour in KML notation
 *
 * @return       the colour in HTML notation and optionally ordered for use in KML
 */
public String colorObjectToHTML(Color color) {

	if(color == null) {
		throw new IllegalArgumentException("The color object can not be null");
	}
	
	// deconstruct the color
	String red   = Integer.toHexString(color.getRed());
	String green = Integer.toHexString(color.getGreen());
	String blue  = Integer.toHexString(color.getBlue());
	
	String colour = null;
	
	// double check the values
	if(red.length() == 1) {
		red = "0" + red;
	}
	
	if(blue.length() == 1) {
		blue = "0" + blue;
	}
	
	if(green.length() == 1) {
		green = "0" + green;
	}
	
	return "#" + red.toUpperCase() + green.toUpperCase() + blue.toUpperCase();
}
%>
<%!
/**
* Make a colour darker.
* 
* @param color     Color to make darker.
* @param fraction  Darkness fraction.
* @return          Darker color.
*/
public Color darkerColour (Color colour, double fraction)
{
	int red   = (int) Math.round (colour.getRed()   * (1.0 - fraction));
	int green = (int) Math.round (colour.getGreen() * (1.0 - fraction));
	int blue  = (int) Math.round (colour.getBlue()  * (1.0 - fraction));

	if (red < 0) {
		red = 0;
	} else if (red   > 255) {
		red = 255;
	}
	
	if (green < 0) {
		green = 0; 
	} else if (green > 255) {
		green = 255;
	}
	
	if (blue  < 0) {
		blue = 0; 
	} else if (blue  > 255) {
		blue  = 255;
	}

	return new Color (red, green, blue);
}
%>
