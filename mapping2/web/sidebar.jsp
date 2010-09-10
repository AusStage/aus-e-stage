<%@page language="java" contentType="text/html"%>
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
<!-- 
<div id="sidebar">
	<ul>
		<li><a href="http://www.ausstage.edu.au">AusStage Website</a></li>
		<li><a href="/mapping2/" title="Mapping Service Homepage">Mapping Home</a></li>
		<li><a href="/mapping2/browse.jsp" title="Browse a map of venues and list events">Browse Map of Venues</a></li>
		<li><a href="/mapping2/organisations.jsp" title="Search for Organisations and create a map of their events">Maps By Organisation</a></li>
		<li><a href="/mapping2/contributors.jsp" title="Search for Contributors and create a map of their events">Maps By Contributor</a></li>
	</ul>
</div>
 -->
<link rel="stylesheet" type="text/css" media="screen" href="assets/tabs-accordion.css"/>
<script type="text/javascript" src="assets/javascript/libraries/jquery.tools.min.js"></script>
<script type="text/javascript">
		$(function(){
		/* $("#accordion").tabs("#accordion div.pane", {tabs: 'h2', event: 'mouseover', effect: 'fade', initialIndex: null}); */
			$("#accordion").tabs("#accordion div.pane", {tabs: 'h2', effect: 'slide', initialIndex: null});
		});
			// add new effect to the tabs
		$.tools.tabs.addEffect("slide", function(i, done) {

 			// 1. upon hiding, the active pane has a ruby background color
 			this.getPanes().slideUp().css({backgroundColor: "#FFFFCC"});

			// 2. after a pane is revealed, its background is set to its original color (transparent)
			this.getPanes().eq(i).slideDown(function() {
 				$(this).css({backgroundColor: 'transparent'});

				 // the supplied callback must be called after the effect has finished its job
 				done.call();
 			}); 
		}); 			
</script>
	
<script type="text/javascript">		
		$(function() {
			$("ul.css-tabs").tabs("div.css-panes > div", {effect: 'ajax', history: true});
		}); 
</script>


<div id="accordion">
			
	<h2 class="current">Venues</h2>
	<div class="pane">
		<ul class="css-tabs">
			<li><a class="current" href="location.html"><span>Location</span></a></li>
			<li><a class="" href="events.html"><span>Events</span></a></li>			
		</ul>
	
		<div class="css-panes">
			<div style="display: block;"></div>
		</div>				
	</div>

	<h2>Contributors</h2>
	<div class="pane">... pane content ...</div>

	<h2>Organisations</h2>
	<div class="pane">
		<ul class="css-tabs">
			<li><a class="current" href="org_name_search.html"><span>Name</span></a></li>
			<li><a class="" href="org_id_search.html"><span>ID   </span></a></li>			
		</ul>
	
		<div class="css-panes">
			<div style="display: block;"></div>
			<!-- <div id="tabs-1">
			 	<form action="data/" method="post" id="name_search" name="name_search">
					<input type="hidden" name="action" value="organisation_search"/>
					<input type="hidden" name="search_type" value="single"/>
					<table class="formTable">
						<tbody>
						<tr>
							<th scope="row">
								<label id="name_label_1" for="organisation_name" class="#cluetip_name" style="cursor: help;">Organisation Name: </label>
							</th>
							<td>
								<input type="text" size="40" id="organisation_name" name="organisation_name"/>
							</td>
						</tr>
						<tr>
							<th scope="row">
								<label id="operator_label_1" for="operator" class="#cluetip_operator" style="cursor: help;">Search Operator: </label>
							</th>
							<td>
								<select size="1" id="operator" name="operator">
									<option value="and" selected="selected">And</option>
									<option value="or">Or</option>
									<option value="exact">Exact Phrase</option>
								</select>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<input class="ui-state-default ui-corner-all button" type="submit" name="submit" id="name_search_btn" value="Search"/><br/>
								<span style="font-size: 90%"><strong>Note:</strong> Hover your mouse over the label next to the input box for more information</span>
							</td>
						</tr>
						</tbody>
					</table>
				</form>
			</div>	
			
			<div id="tabs-2">
				<form action="data/" method="get" id="id_search" name="id_search">
					<input type="hidden" name="action" value="lookup"/>
					<input type="hidden" name="type"   value="orgname2"/>
					<table class="formTable">
						<tbody>
						<tr>							
							<td>
								<input type="text" size="40" id="id" name="id"/>
							</td>
							<td colspan="2">
								<input class="ui-state-default ui-corner-all button" type="submit" name="submit" id="id_search_btn" value="Search"/>
							</td>
						</tr>						
						</tbody>
					</table>
				</form>
			</div>-->
			
		</div>
	</div>
</div>
			