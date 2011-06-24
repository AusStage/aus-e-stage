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
 
 /* event viewer - event to event visualiser */
 
 
 function EventViewerClass(){
 	
 	this.className = 'EventViewerClass';
 	//data
 	this.json = {'edges':[],'nodes':[]};
 	this.renderComplete = false;
 	//graph stats- information
 	this.centralNode = -1;
 	this.contributorCount = -1;
 	//protovis visualiser objects
 	this.vis = new pv.Panel();
 	this.focus;
 	// sizing 
	this.spacer = 70;
	this.hSpacer = 80;
 	this.w = $(window).width() - ($(".sidebar").width()+this.spacer);
	this.h = $(window).height() - ($(".header").height()+$(".footer").height()+$('#fix-ui-tabs').height()+this.hSpacer);
	//scales 
	this.startDate;
	this.endDate;
	this.xAxis;
	this.yAxis;
	
	// appearance variabilityles 
	this.thickLine = 3;
	this.thinLine = 1.5;
	this.fontHeight = 9;
	this.largeFont = "8 pt sans-serif";
	this.smallFont = "8 pt sans-serif";
	//edge, node and background colouring for normal browsing
	this.colors = {		panelColor:			"white",
						selectedNode:		"rgba(0,0,255,1)",				//blue
						unselectedNode:		"rgba(170,170,170,1)",		//light grey
						relatedNode:		"rgba(46,46,46,1)",				//dark grey	
						selectedNodeBorder:	"rgba(0,0,255,1)",	  			//blue								
						unselectedNodeBorder:"rgba(170,170,170,1)",			//light grey
						relatedNodeBorder:	"rgba(46,46,46,1)",				//dark grey
						selectedEdge:		"rgba(0,0,255,1)",				//blue				
						unselectedEdge:		"rgba(170,170,170,0.7)",		//light grey
						relatedEdge:		"rgba(46,46,46,0.7)",			//dark grey
						hoverEdge:			"rgba(105, 105, 105, 0.5)",		//slate grey
						selectedLabel:		"rgba(0,0,255,1)",				//blue
						unselectedLabel:	"rgba(170,170,170,1)",			//light grey
						relatedLabel:		"rgba(46,46,46,1)",				//dark grey
						selectedText:		"rgba(25,25,112,1)",				//blue
						unselectedText:		"rgba(170,170,170,1)",			//light grey
						relatedText:		"rgba(46,46,46,1)",				//dark grey			
						hoverText:			"rgba(105, 105, 105, 0.5)"		//slate grey
					  }	 
	//-------------------END COLOR SCHEME 
 	//MOUSE OVER VARIABLES
	this.pointingAt;				//set ON MOUSE OVER. EDGE or NODE
	this.edgeIdPoint = -1;		//stores the id of the edge that the mouse is currently over. Used for mouse in/out. 	
	this.edgeIndexPoint = -1;	//stores the index of the edge that the mouse is currently over. Used for mouse in/out. 
	this.nodeIndexPoint = -1;	//stores the index of the node that the mouse is currently over. Used for mouse in/out. 

	//CLICK VARIABLES
	this.custNodeIndex = -1;	//stores the index of the element to be altered
	this.custEdgeIndex = -1;
	this.dragIndicator = false;  //set to true on drag
	this.currentFocus = "";			//set ON CLICK. EDGE or NODE
	this.edgeId = -1;			//stored the ID of the selected EDGE
	this.edgeIndex = -1;			//stores the INDEX of the selected contributor - specific to the selected edge.
	this.nodeIndex = -1;			//stores the INDEX of the selected NODE	

	this.showAllNodeLabels = $("input[name=showAllNodeLabels]").is(":checked");	
	this.showRelatedNodeLabels = $("input[name=showRelatedNodeLabels]").is(":checked");	
	
	this.showAllEventLabels = $("input[name=showAllEdgeLabels]").is(":checked");	
	this.showRelatedEventLabels = $("input[name=showRelatedEdgeLabels]").is(":checked");	
	
	this.showAllNodes = $("input[name=showAllNodes]").is(":checked");
	this.showRelatedNodes = $("input[name=showRelatedNodes]").is(":checked");

	this.showAllEdges = (!$("input[name=showAllNodes]").is(":checked"))?false:$("input[name=showAllEdges]").is(":checked");					
	this.showRelatedEdges = (!$("input[name=showRelatedNodes]").is(":checked"))?false:$("input[name=showRelatedEdges]").is(":checked");	
		
	this.showCustColors = $("input[name=showCustColors]").is(":checked");
	this.showCustVis = $("input[name=showCustVis]").is(":checked");
	
	//CLASS METHODS
	this.render = function(){this.vis.render();}
 }
 
 //clear visualiser
EventViewerClass.prototype.destroy = function(){
	$('#'+PANEL).empty();
}

//render the graph 
EventViewerClass.prototype.renderGraph = function(json){
 	this.json = json;
 	this.prepareData();
 	initEventGraph(this);
 	this.displayNetworkProperties();
	this.displayPanelInfo(NODE);
 }
 
 /* SIDE PANEL INFORMATION DISPLAY METHODS
====================================================================================================================*/

//display the network properties
EventViewerClass.prototype.displayNetworkProperties = function(){
	if (this.centralNode!=-1){
			
		var eventUrl = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_event_id="; 		
	    var html = 	"<table>"+
	  				"<tr class=\"d0\"><th scope='row'><input type=\"submit\" name=\"submit\" class=\"button\" id=\"find_centre\" value=\"Centre\" /></th><td>"+
	  				"<a href=" + eventUrl +""+ this.json.nodes[this.centralNode].id+" target=\"_blank\">"+	
	  				this.json.nodes[this.centralNode].nodeName+
    				"</a> <p>"+this.json.nodes[this.centralNode].venue+", "+
    				"<span class='date'>"+dateFormat(this.json.nodes[this.nodeIndex].startDate)+"</span></p></td></tr>"+
    				"<tr class=\"d1\"><th scope='row'>Events</th><td> "+this.json.nodes.length+"</td></tr>"+					
					"<tr class=\"d0\"><th scope='row'>Contributors</th><td>"+this.contributorCount+"</td></tr></table>";			
 		$("#network_properties_body").empty();
		$("#network_properties_body").append(html);	
		//style the centre button
		$('#find_centre').button();	
	}	
	
}


//display information about the selected element.
EventViewerClass.prototype.displayPanelInfo = function(what){
	
	var eventUrl = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_event_id="; 
	var contributorUrl = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_contrib_id="
	var titleHtml = ""
	var html = "<table>";
	var tableClass = "";
	
	var contributorList = new Array();
	var eventList = [];
	
	//clear the info panel
	$("#selected_object_body").empty();
	if (what == CLEAR){	
		html = " ";
		$("#network_details_div").hide();
		resetLegend("#selected_object");							
	}else{
		$("#network_details_div").show();
	}
			
	
	//***************/
	//NODE
	if (what == NODE){
		//set the title to the event.
		titleHtml = "<a class=\"titleLink\" href=" + eventUrl +""+ this.json.nodes[this.nodeIndex].id+" target=\"_blank\">"+
										this.json.nodes[this.nodeIndex].nodeName+"</a><p>"+
										this.json.nodes[this.nodeIndex].venue+", "+
										"<span class='date'>"+dateFormat(this.json.nodes[this.nodeIndex].startDate)+"</span></p>";
		
		//create an array of related contributors, sort by last name
    	for (i = 0; i < this.json.nodes[this.nodeIndex].contributor_id.length; i++){

    		var lastName = this.json.nodes[this.nodeIndex].contributor_name[i].split(" ")[1];
    		contributorList[i] = {name:lastName,
    		 					  fullName: this.json.nodes[this.nodeIndex].contributor_name[i],
    					   		  id: this.json.nodes[this.nodeIndex].contributor_id[i],
    					   		  roles: this.json.nodes[this.nodeIndex].contributor_roles[i],    					   		   
    					   		 }		
    	}
    	contributorList.sort(sortByName);    	
		//create the list of contributors
		for(i = 0; i < contributorList.length; i++){
			if(isEven(i)) tableClass = "d0";
			 else tableClass = "d1";
			html += "<tr class=\""+tableClass+"\"><td><a href=" + contributorUrl +""+ contributorList[i].id+" target=\"_blank\">"+
								contributorList[i].fullName +"</a>"+
								"<p>"+contributorList[i].roles+"</p></td></tr>" 	
		}
	  
		html += "</table><br>";	  
	}

	//***************/
	//EDGE
	if (what == EDGE){
	
		titleHtml = "<a class=\"titleLink\" href=" + contributorUrl +""+ this.json.edges[this.edgeIndex].id+" target=\"_blank\">"+
					this.json.edges[this.edgeIndex].name+"</a> <p>"+
    				this.json.edges[this.edgeIndex].roles+			
					"</p>";		
	
		var x = 0;
	
		//create the list of events
			for(i = 0; i < this.json.nodes.length; i++){
					if (contains(this.json.nodes[i].contributor_id, this.edgeId)){
						eventList[x] = {event:"<a href=" + eventUrl +""+ this.json.nodes[i].id+" target=\"_blank\">"+
										this.json.nodes[i].nodeName+"</a><p>"+
										this.json.nodes[i].venue+", "+
										"<span class='date' >"+dateFormat(this.json.nodes[i].startDate)+"</span></p>"
						, startDate: this.json.nodes[i].startDate};
						x++;
					}
				}
				//sort events, earliest to latest
				eventList.sort(function(a, b){return b.startDate - a.startDate })		
				
		//create the html to display the info.
    	for( i = 0;i<eventList.length; i++ ){
    		if(isEven(i)) tableClass = "d0";
			else tableClass = "d1";
			
    		html += "<tr class=\""+tableClass+"\"><td>"+eventList[i].event+"</td></tr>"
    	}
    	
    	html+= "</table><br>";
		
	}
	
	$("#selected_object_header").button( "option", "label", titleHtml );					 		
	$("#selected_object_body").append(html);  
	//fix to ensure the content doesn't toggle based on the link click
	$(".titleLink").click(function(){
		allowToggle = false;
	}); 	
}

 

/* DATA MANIPULATION FUNCTIONS - manipulate retrieved data and prepare for visualisation.
====================================================================================================================*/
/*
at this stage the contributor_index array added to events.nodes is not needed. Have left it for now until the visualiser 
requirements are finalised.

STRUCTURE OF NODES==========================================================
events.nodes
			.id					//event id
			.nodeName			//name of the event
			.startDate			//starting date of the event.
			.venue				//event venue
			
	*****GENERATED WHEN GRAPH IS LOADED************	
	
			.linkDegreeTarget	//count of all links related to this node
			.contributor_index [] 	//generated array of all contributors associated with this event. within this network
			.contributor_id []	// generated array of all contributor id's associated with this event. within this network
			.contributor_name [] // generated array of all contributor names associated with this event. within this network 
			.neighbors[]		//generated array of nodes linked directly to this node			
			

END STRUCTURE OF NODES==========================================================
STRUCTURE OF EDGES==========================================================

events.edges
			.id			//id of the contributor
			.name		//name of the contributor
			.source		//index in the events.nodes array of the source node
			.target		//index in the events.nodes array of the target node
			
	*****GENERATED WHEN GRAPH IS LOADED************			
			.index 		//index of this element in the array. 
			.count		//default 1. if this is the second edge to have the same source/target = 2, third edge = 3 etc...
			.targetNode [index, parentIndex]  //index is the index of the node, parentIndex is the index of this edge
			.sourceNode [index, parentIndex]  //index is the index of the node, parentIndex is the index of this edge
									
END STRUCTURE OF EDGES==========================================================
*/
EventViewerClass.prototype.prepareData = function(){
	//get the network statistics
	var counts = this.getStats();
	//get and set values for duplicate edges
	this.getDupEdges();
	//format the date field and create axis
	this.getDateAxis();
	//layout the nodes
	this.layout(counts[0], counts[1]);
}


EventViewerClass.prototype.getStats = function(){
	var beforeCount = 0;
	var afterCount = 0;
	var isBefore = true;
	
	//get link degree for nodes. And set contrib_id array with associated contributors
	for(i = 0; i < this.json.nodes.length; i ++){
		this.json.nodes[i].index = i;
		this.json.nodes[i].custVis = true;
		this.json.nodes[i].custColor = null;
		//calculate number of nodes before and after the central node - used for layout method
		if(!isBefore){
			afterCount++;
		}
		if(this.json.nodes[i].central){
			isBefore = false;
			this.centralNode = i;
			this.nodeIndex = i;
			
		}
		if (isBefore){
		beforeCount++;
		}
		
		// calculate node statistics, link degree, linking contributors, neighboring nodes
		this.json.nodes[i].linkDegree = 0;
		this.json.nodes[i].contributor_index = [];
		this.json.nodes[i].contributor_id = [];
		this.json.nodes[i].contributor_name = [];
		this.json.nodes[i].contributor_roles = [];		
		this.json.nodes[i].neighbors = [];
		
		var index_count = 0;
		var neighbor_count = 0;
		var id_count = 0;
		
		for (x = 0; x < this.json.edges.length; x ++){
			this.json.edges[x].custVis = true;
			if(this.json.edges[x].source == i){
				this.json.nodes[i].linkDegree++;
				
				if (!contains(this.json.nodes[i].neighbors, this.json.edges[x].target)){
					this.json.nodes[i].neighbors[neighbor_count] = this.json.edges[x].target;
					neighbor_count++;
				}
				
				if (!contains(this.json.nodes[i].contributor_index, x)){
					this.json.nodes[i].contributor_index[index_count] = x;
					index_count++;
				}
				if (!contains(this.json.nodes[i].contributor_id, this.json.edges[x].id)){
					this.json.nodes[i].contributor_id[id_count] = this.json.edges[x].id;
					this.json.nodes[i].contributor_name[id_count] = this.json.edges[x].name;
					this.json.nodes[i].contributor_roles[id_count] = this.json.edges[x].roles;					
					id_count++;
				}
				
			}
			if(this.json.edges[x].target == i){
				this.json.nodes[i].linkDegree++;
				
				if (!contains(this.json.nodes[i].neighbors, this.json.edges[x].source)){
					this.json.nodes[i].neighbors[neighbor_count] = this.json.edges[x].source;
					neighbor_count++;
				}

				if (!contains(this.json.nodes[i].contributor_index, x)){
					this.json.nodes[i].contributor_index[index_count] = x;
					index_count++;
				}
				if (!contains(this.json.nodes[i].contributor_id, this.json.edges[x].id)){
					this.json.nodes[i].contributor_id[id_count] = this.json.edges[x].id;
					this.json.nodes[i].contributor_name[id_count] = this.json.edges[x].name;
					this.json.nodes[i].contributor_roles[id_count] = this.json.edges[x].roles;					
					id_count++;
				}

			}			
		}			
	}
	return [beforeCount, afterCount];		
}

EventViewerClass.prototype.getDupEdges = function(){
	//get count of duplicate lines AND add x and y position of source and target nodes. 
	var tempArray = [];
	//loop through the edges.
		var edge = new Object();
		edge.source = this.json.edges[0].source;
		edge.target = this.json.edges[0].target;
		edge.count = 0;
		tempArray[tempArray.length] = edge;
		contribCount = [];
		
	for(i = 0; i< this.json.edges.length; i ++){

		//get count of individual contributors
		if(!contains(contribCount, this.json.edges[i].id)){
			contribCount.push(this.json.edges[i].id);	
		}
		
		
		this.json.edges[i].index = i;
			
		this.json.edges[i].count = 1;	
		//for each edge, 
		//look for a match in the temp array
		var noMatch = true;
		for(y = 0; y< tempArray.length; y ++){
			if (this.json.edges[i].source == tempArray[y].source){  
					if(this.json.edges[i].target == tempArray[y].target){
						tempArray[y].count = tempArray[y].count + 1;
						this.json.edges[i].count = tempArray[y].count;
						noMatch == false;
					}
			}
		}
		if (noMatch == true){ 
				edge = new Object();
				edge.source = this.json.edges[i].source;
				edge.target = this.json.edges[i].target;
				edge.count = this.json.edges[i].count;
				tempArray[tempArray.length] = edge;
		}
	//end count duplicate lines
	////////////////////

		//get the source and target x and y values and store in the edge data.
		this.json.edges[i].targetNode = {index: this.json.edges[i].target,
									  parentIndex: i};
		this.json.edges[i].sourceNode = {index: this.json.edges[i].source,
									  parentIndex: i};

	
	}
	this.contributorCount = contribCount.length;
	
}

EventViewerClass.prototype.getDateAxis = function(){
	//format the startDate field to a date
	for(i = 0;i<this.json.nodes.length; i ++){
		this.json.nodes[i].startDate = new Date (this.json.nodes[i].startDate.substring(0,4),
											  this.json.nodes[i].startDate.substring(5,7)-1,
											  this.json.nodes[i].startDate.substring(7, 9));
	}

	//set the global start date to the min date less 12 months
	this.startDate = new Date (pv.min(this.json.nodes, function(d) {return d.startDate}));
	this.startDate.setMonth(this.startDate.getMonth()-11);

	//set the global end date to the max date plus 12 months
	this.endDate = new Date (pv.max(this.json.nodes, function(d) {return d.startDate}));	
	this.endDate.setMonth(this.endDate.getMonth()+11);

	//set the x and y axis scale.
	this.xAxis = pv.Scale.linear(this.startDate, this.endDate).range(0, this.w);
	this.yAxis = pv.Scale.linear(0, this.json.nodes.length).range(0, this.h);
}

//layout the nodes
EventViewerClass.prototype.layout = function(beforeCount, afterCount){
	
	var range = this.json.nodes.length/2;
	var step;
	if (beforeCount > afterCount){
			step = (range)/beforeCount;
	}
	else step = (range)/afterCount;
	
	var alternate = 0;
	var before = true;
	var afterIndex = 2;
	var linkDegree = 0;
	var x = 1;
	var xafter;
	var iafter;
	
	for(i = 0;i<this.json.nodes.length; i ++){
		
		if (this.json.nodes[i].central){
			this.json.nodes[i].top = range;
			before = false;
		}
	
		if (before){
			switch (alternate){
				case 0: 
						if(linkDegree < this.json.nodes[i].linkDegree){
							this.json.nodes[i].top = range - (step*x);
							x++;
						}
						else{
							this.json.nodes[i].top = range - (step*i);
						}
	        				alternate = 1;
	        			break;
	        				
				case 1: 
						if(linkDegree < this.json.nodes[i].linkDegree){
							this.json.nodes[i].top = range + (step*x);
							x++;
						}
						else{
							this.json.nodes[i].top = range + (step*i);
						}
	    				alternate = 0; 
						break;
				}	
			}
		  	if(this.json.nodes[i].central){
		  		xafter = x;
			  	iafter = i;
	  		}
	  
		  	if (!before && (!this.json.nodes[i].central)){
				switch (alternate){
					case 0: 
							if(linkDegree < this.json.nodes[i].linkDegree){
								this.json.nodes[i].top = range - (step*(xafter));
								xafter--;
							}
							else{						
								this.json.nodes[i].top = range - (step*(iafter));
								iafter--;
							}
			        		alternate = 1;

			        		break;
		        				
					case 1: 
							if(linkDegree < this.json.nodes[i].linkDegree){
								this.json.nodes[i].top = range + (step*(xafter));
								xafter--;
							}
							else{				
								this.json.nodes[i].top = range + (step*(iafter));
								iafter--;
							}
		    				alternate = 0;
							break;
			}	  
		}
		linkDegree = this.json.nodes[i].linkDegree;
	}	
}

 
/* DISPLAY FUNCTIONS - UPDATE/ALTER or LOAD THE GRAPH - 
   SHOW OR HIDE ELEMENTS
====================================================================================================================*/ 
function initEventGraph (obj){	

	obj.vis = new pv.Panel().canvas(PANEL)
	.fillStyle(obj.colors.panelColor)
    .width(function(){return obj.w})
    .height(function(){return obj.h})
    .bottom(15);

	// X-axis and ticks. 
	obj.vis.add(pv.Rule)
    	.data(function() {return obj.xAxis.ticks()})
    	.strokeStyle("lightgrey")
    	.left(obj.xAxis)
			.anchor("bottom").add(pv.Label)
		    .text(obj.xAxis.tickFormat);

	// Use an invisible panel to capture pan & zoom events. 
 	obj.vis.add(pv.Panel)
    	.events("all")
	    .event("mousedown", pv.Behavior.pan().bound(1))
	    .event("mousewheel", pv.Behavior.zoom(0.4).bound(1))
    	.event("pan", transformPan)
	    .event("zoom", transform)
	    .event("click", function(d) {obj.onClick(null, CLEAR, null); return obj.focus;})
		;

	//create new panel to hold the nodes and edges
	obj.focus = obj.vis.add(pv.Panel); 

	//add the edges
	for(var i = 0; i < obj.json.edges.length; i++){
		obj.focus.add(pv.Line)
			.data([obj.json.edges[i].sourceNode, obj.json.edges[i].targetNode])
			.left(function(d){return obj.xAxis(obj.json.nodes[d.index].startDate)})
			.top(function(d){return obj.getLinePlacement(d)}) 
	        .strokeStyle(function(d){return obj.getEdgeStyle(d)})
	        .lineWidth(function(d){return obj.getLineWidth(d)}) 
			.visible(function(d){return obj.isVisibleEdge(d)})		        			
			//events
			.event("click", function(d) {obj.onClick(d, EDGE, this); 
										 return obj.focus;})    	    							 
			.event("mouseover", function(d) {	obj.edgeIndexPoint = d.parentIndex;
												obj.edgeIdPoint = obj.json.edges[d.parentIndex].id;
												obj.pointingAt = EDGE;
	  							 				return obj.focus;})    	    
			.event("mouseout", function(d) {	obj.edgeIndexPoint = -1;
												obj.edgeIdPoint = -1;
												obj.pointingAt = "";
	  							 				return obj.focus;});  	  		 
	}//end for loop
   
   	// add a bar to the centre of each line. To act as a background for the labels
	obj.focus.add(pv.Bar)	  							 				
		.data(obj.json.edges)
	 	.top(function(d){ return obj.getBarPlacement(d.sourceNode, this, "top")})	
		.left(function(d) { return obj.getBarPlacement(d.sourceNode, this, "left")})
		.width(function(d){ return measureText(d.name)* Math.pow(this.scale,-0.75)})
		.height(function(){ return obj.fontHeight* Math.pow(this.scale,-0.75)})		
		.fillStyle(function(d){return obj.getEdgeLabelStyle(d)})
		.strokeStyle(function(d){return obj.getEdgeLabelStyle(d)})
		.visible(function(d) {return obj.isVisible(d.sourceNode, EDGE, this)})
		//add events		
		.event("click", function(d) {obj.onClick(d.sourceNode, EDGE, this); 
									 return obj.focus;})
		.event("mouseover", function(d) {	
											obj.edgeIndexPoint = d.index;
											obj.edgeIdPoint = d.id;
											obj.pointingAt = EDGE;
	  						 				return obj.focus;})    	   
		.event("mouseout", function(d) {	obj.edgeIndexPoint = -1;
											obj.edgeIdPoint = -1;
											obj.pointingAt = "";
	  						 				return obj.focus;})   										 
	  	//add the label to the box
	  	.anchor() 
		.add(pv.Label)
		.textStyle("white")
		.font(function(d){return obj.getFont(d.sourceNode, EDGE, this)})
		.visible(function(d) {return obj.isVisible(d.sourceNode, EDGE, this)})
		.text(function(d) {return d.name});

	//add the nodes 
	obj.focus.add(pv.Dot)
    	.data(obj.json.nodes)
	    .left(function(d) {return obj.xAxis(d.startDate)})
    	.top(function(d) {return obj.yAxis(d.top)})
		.fillStyle(function(d) {return obj.getNodeFill(d, this)})
		.strokeStyle(function(d) {return obj.getNodeStroke(d, this)})
		.lineWidth(function(d) {return obj.getNodeLineWidth(d, this)})	
		.size(function(d){return Math.pow(((d.linkDegree)*2), 1.25)* Math.pow(this.scale, -2) })
		.visible(function(d){return obj.isVisibleNode(d)})	
 	  	.event("mouseover", function() {	obj.nodeIndexPoint = this.index;
											obj.pointingAt = NODE;										
	  							 			return obj.focus;}) 
    	.event("mouseout", function() {		obj.nodeIndexPoint = -1;
											obj.pointingAt = "";										
	  							 			return obj.focus;})
		.event("dblclick", function(d){obj.onDblClick(d)})	  							 			 
		.event("click", function(d) {		obj.onClick(d, NODE, this);
									  		return obj.focus; } )	  						
    	.event("mousedown", pv.Behavior.drag())
		.event("drag", function(d){obj.dragNode(d); 
								   return obj.vis;})
			//label
			.anchor("top").add(pv.Label)
			.text(function(d){ return d.nodeName})
			.font(function(d){ return obj.getFont(d, NODE, this)})
			.textStyle(function(d){return obj.getNodeTextStyle(d, this)})
			.visible(function(d){return obj.isVisible(d, NODE, this)});


	//render the graph
    obj.render();
    obj.renderComplete=true; 	
}

EventViewerClass.prototype.showInteraction = function(){
	if(this.renderComplete){
		$("#exportContainer").show();
		$("#viewer_options_div").show();//show viewer options accordion
		$("#event_options_div").show();//show display label options
		if (this.nodeIndex>-1 || this.edgeIndex>-1){
			$('#network_details_div').show();
		}
		$("#network_properties_div").show();//show network properties	
	}
}

EventViewerClass.prototype.hideInteraction = function(){
	$("#exportContainer").hide();
	$("#viewer_options_div").hide();	
	$("#event_options_div").hide();//hide display label options
	$('#network_details_div').hide();
	$("#network_properties_div").hide();//hide network properties	
}

EventViewerClass.prototype.refreshGraph = function(typeOfRefresh){

}

/* TRANSFORM FUNCTIONS - handles zoom, pan, drag, click and window resize events.
====================================================================================================================*/

//pan handler... ha! repeates exactly what zoom handler does, but changes the drag indicator. 
//Couldn't work out how to combine the two. stoopid
function transformPan(){
	viewer.dragIndicator = true;
	var t = this.transform().invert();
		viewer.xAxis.domain(viewer.xAxis.invert(t.x + viewer.xAxis(viewer.startDate) *t.k)
							,viewer.xAxis.invert(t.x + viewer.xAxis(viewer.endDate) * t.k));				
		viewer.yAxis.domain(viewer.yAxis.invert(t.y + viewer.yAxis(0) *t.k), 
							viewer.yAxis.invert(t.y + viewer.yAxis(viewer.json.nodes.length) * t.k));
	viewer.vis.render();
}

// zoom handler 
function transform() {
	
	var t = this.transform().invert();
		viewer.xAxis.domain(viewer.xAxis.invert(t.x + viewer.xAxis(viewer.startDate) *t.k)
							,viewer.xAxis.invert(t.x + viewer.xAxis(viewer.endDate) * t.k));				
		viewer.yAxis.domain(viewer.yAxis.invert(t.y + viewer.yAxis(0) *t.k), 
							viewer.yAxis.invert(t.y + viewer.yAxis(viewer.json.nodes.length) * t.k));
	viewer.vis.render();
}

//window resize handler
EventViewerClass.prototype.windowResized = function(){		  

	  //reset the timeline range accordingly
	  this.xAxis.range(0, this.w);
	  this.yAxis.range(0, this.h);
}

//drag functionality
EventViewerClass.prototype.dragNode = function(d){
	
	var y = this.focus.mouse().y;		//get mouse position
	d.top = this.yAxis.invert(y);		//update the node position		

	this.dragIndicator = true;

	this.vis.render();
}

//click functionality
EventViewerClass.prototype.onClick = function(d, what, p){
	var _what = what;
	if(pv.event.altKey && (what==NODE || what==EDGE)){
		$("#custom_div").dialog( "option", "position", [pv.event.x, pv.event.y] );
		$("#custom_div").dialog("open");
		if(what == NODE){
			this.custNodeIndex = d.index;
			this.custEdgeIndex = -1;
		} else if (what == EDGE){
			this.custEdgeIndex = d.parentIndex;
			this.custNodeIndex = -1;
		}
	}
	else { 
		switch (what){
			case CLEAR: 
				if (!this.dragIndicator){
					this.currentFocus = "";
				    this.edgeId = -1;
				    this.edgeIndex = -1;
					this.nodeIndex = -1;	
				}else {
					what = this.currentFocus;
				} 
				this.dragIndicator = false;
				break;
		
			case EDGE: 
				this.currentFocus = EDGE;
			    this.edgeId = this.json.edges[d.parentIndex].id;
			    this.edgeIndex = d.parentIndex;
				this.nodeIndex = -1;		
				break;
		
			case NODE: 
				if (!this.dragIndicator){
					this.currentFocus = NODE;
					this.edgeId = -1;
					this.edgeIndex = -1;
  	   	   			this.nodeIndex = p.index;
				}else{
					what = this.currentFocus;
				}
				this.dragIndicator = false;
				break;
		}
		if(this.currentFocus == ""){this.currentFocus = CLEAR;}
		this.displayPanelInfo(_what);			
		this.windowResized();
	}
}

//double click - reload the graph.
EventViewerClass.prototype.onDblClick = function(d){
	
	viewerControl.displayNetwork('EVENT', d.id, 0);	
	
}	


/* GRAPH APPEARANCE FUNCTIONS - determine the fill, stroke and general appearance of marks
====================================================================================================================*/
//add custom color to a node
EventViewerClass.prototype.setColor = function(c){
	if(this.custNodeIndex!=-1){
		this.json.nodes[this.custNodeIndex].custColor = c;	
	}
	else if (this.custEdgeIndex!=-1){
		for(i = 0; i < this.json.edges.length; i++){
			if(this.json.edges[i].id == this.json.edges[this.custEdgeIndex].id){
				this.json.edges[i].custColor = c;
			}	
		}
		
	}
}
//remove the custom color for one node
EventViewerClass.prototype.resetColor = function(){
	if(this.custNodeIndex!=-1){this.json.nodes[this.custNodeIndex].custColor = null;}
	else if (this.custEdgeIndex!=-1){
		for(i = 0; i < this.json.edges.length; i++){
			if(this.json.edges[i].id == this.json.edges[this.custEdgeIndex].id){
				this.json.edges[i].custColor = null;
			}	
		}	
	}
}
//remove the custom color for all nodes
EventViewerClass.prototype.resetAllColors = function(){
	for (var i = 0; i<this.json.nodes.length; i++){
		this.json.nodes[i].custColor = null;	
	}
	for(i = 0; i< this.json.edges.length; i++){
		this.json.edges[i].custColor = null;
	}	
}
EventViewerClass.prototype.hideElement = function(){
	if(this.custNodeIndex!=-1){
		this.json.nodes[this.custNodeIndex].custVis = false;
		for (var i = 0; i < this.json.nodes[this.custNodeIndex].contributor_index.length; i++){
			this.json.edges[this.json.nodes[this.custNodeIndex].contributor_index[i]].custVis = false;
		}
	}
	else if(this.custEdgeIndex!=-1){
		for(i = 0; i < this.json.edges.length; i++){
			if(this.json.edges[i].id == this.json.edges[this.custEdgeIndex].id){
				this.json.edges[i].custVis = false;
			}	
		}	
	}
}

EventViewerClass.prototype.resetHiddenElements = function(){
 	for (var i = 0; i<this.json.nodes.length; i++){
		this.json.nodes[i].custVis = true;	
	}	
	for (var i = 0; i<this.json.edges.length; i++){
		this.json.edges[i].custVis = true;	
	}	
}


// function to place a bar in the centre of each line for labelling, takes in d:the mark, p:the panel, s:switch string
EventViewerClass.prototype.getBarPlacement = function(d, p, s){
	var from;
	var to;
	
	switch (s){
		
		case "top":
			from = this.yAxis(this.json.nodes[this.json.edges[d.parentIndex].source].top);
			to = this.yAxis(this.json.nodes[this.json.edges[d.parentIndex].target].top);
			
			if (this.json.edges[d.parentIndex].count == 1){ 
				return from + ((to-from)/2); break;
			} else{
				if(isEven(this.json.edges[d.parentIndex].count)){
					return from + ((to - from)/2)+((((this.json.edges[d.parentIndex].count)/2)*9)* Math.pow(p.scale, -0.75)); break;
				} else {
					return from + ((to - from)/2)-((((this.json.edges[d.parentIndex].count-1)/2)*9)* Math.pow(p.scale, -0.75)); break;
				}  	
 			}
 		
 		case "left":
 			from = this.xAxis(this.json.nodes[this.json.edges[d.parentIndex].source].startDate);
			to = this.xAxis(this.json.nodes[this.json.edges[d.parentIndex].target].startDate);
			return 	from +((to - from)/2);
			break;
	}
 }


// line placement method taking into account parallel lines
EventViewerClass.prototype.getLinePlacement = function(d){
	
	if (this.json.edges[d.parentIndex].count == 1) {return this.yAxis(this.json.nodes[d.index].top)} 
	else { if(isEven(this.json.edges[d.parentIndex].count)){
				return this.yAxis(this.json.nodes[d.index].top) + ((((this.json.edges[d.parentIndex].count)/2)*3)* Math.pow(this.focus.scale, -0.75))
		 } else {
				return this.yAxis(this.json.nodes[d.index].top) - ((((this.json.edges[d.parentIndex].count-1)/2)*3)* Math.pow(this.focus.scale, -0.75))
		 }
	 }
}

//return fill color for Label boxes
EventViewerClass.prototype.getEdgeLabelStyle = function(d){
	//if contributor has been selected highlight all contributor edges
	if (d.id == this.edgeId){
		return this.colors.selectedLabel;
	}
	 
	//if node selected, highlight all attached edges.
	if (d.source == this.nodeIndex || d.target == this.nodeIndex){
		return this.colors.relatedLabel;
	}		
	return this.colors.unselectedLabel;
}

// returns color used for edges
EventViewerClass.prototype.getEdgeStyle = function(d){
	if (this.json.edges[d.parentIndex].custColor && this.showCustColors){return this.json.edges[d.parentIndex].custColor}
	//if contributor has been selected highlight all contributor edges
	if (this.json.edges[d.parentIndex].id == this.edgeId){
		return this.colors.selectedEdge;
	}
	if (this.json.edges[d.parentIndex].id == this.edgeIdPoint){
		return this.colors.hoverEdge;
	}
	//if node selected, highlight all attached edges.
	if (this.json.edges[d.parentIndex].source == this.nodeIndex ||this.json.edges[d.parentIndex].target == this.nodeIndex){
		return this.colors.relatedEdge;
	}			
	return this.colors.unselectedEdge;
}	

// determine line width of edges
EventViewerClass.prototype.getLineWidth = function(d){
	//if selected
	if (this.currentFocus == EDGE && this.json.edges[d.parentIndex].id == this.edgeId){
		return this.thickLine;
	}
	//if hovering
	if (this.json.edges[d.parentIndex].id == this.edgeIdPoint){
		return this.thickLine;
	}	
	return this.thinLine;
}

// function to determine NODE fill color
EventViewerClass.prototype.getNodeFill = function(d, p){
	if(d.custColor && this.showCustColors){return d.custColor;} 	
	//if selected contributor passes through this node
	if(contains(d.contributor_id, this.edgeId)){
		return this.colors.relatedNode;
	}

	//if this node is selected
	if (p.index == this.nodeIndex){
		return this.colors.selectedNode;
	}	
	//if this node is related
	if (contains(d.neighbors, this.nodeIndex)){	
		return this.colors.relatedNode;
	}
	return this.colors.unselectedNode;	
}

//function to determine NODE outline
EventViewerClass.prototype.getNodeStroke = function (d, p){
	
	//if selected contributor passes through this node
	if(contains(d.contributor_id, this.edgeId)){
		return this.colors.relatedNodeBorder;	
	}
	//if this node is selected
	if (p.index == this.nodeIndex){
		return this.colors.selectedNodeBorder;
	}
	//if this node is related
	if (contains(d.neighbors, this.nodeIndex)){
		return this.colors.relatedNodeBorder;
	}
	return this.colors.unselectedNodeBorder;	
}

//determine the NODE outline width
EventViewerClass.prototype.getNodeLineWidth = function(d, p){
	
	if (p.index == this.nodeIndexPoint){	//if current node index == index of object selected.
		return this.thickLine;
	}
	if (p.index == this.nodeIndex){		//if current node index == index of object selected
		return this.thickLine;
	}
	if (contains(d.contributor_id, this.edgeId)){	//if selected contributor has passed through this node
		return this.thickLine;
	}
	else return this.thinLine;	
}

//determine font
EventViewerClass.prototype.getFont = function(d, what, p){
	//for edges
	if (what == EDGE){
		
		if(d.parentIndex == this.edgeIndexPoint){	//if current edge index == index of the object being pointed at.
			return this.largeFont;	
		}
		
		if(d.parentIndex == this.edgeIndex){	//if current edge index == index of object selected.
			return this.largeFont;
		}
		else
			return this.smallFont;		
	}
	//for Nodes
	if (what == NODE){	
				
		if (p.index == this.nodeIndexPoint){	//if current node index == index of object selected.
			return this.largeFont;
		}
		if (p.index == this.nodeIndex){		//if current node index == index of object selected
			return this.largeFont;
		}
		if (contains(d.contributor_id, this.edgeId)){	//if selected contributor has passed through this node
			return this.largeFont;
		}
		else 
			return this.smallFont;
	}	    
	else
	return this.smallFont;
}

//function to determine NODE outline
EventViewerClass.prototype.getNodeTextStyle = function(d, p){

	if(d.custColor && this.showCustColors){return d.custColor;} 		
	//if selected contributor passes through this node
	if(contains(d.contributor_id, this.edgeId)){
		return this.colors.relatedText;	
	}
	//if this node is selected
	if (p.index == this.nodeIndex){
		return this.colors.selectedText;
	}
	//if this node is related
	if (contains(d.neighbors, this.nodeIndex)){
		return this.colors.relatedText;
	}
	return this.colors.unselectedText;
}

EventViewerClass.prototype.isVisibleNode = function(d){
	if (!d.custVis && !this.showCustVis){return false}
	if (this.showRelatedNodes && (contains(d.neighbors, this.nodeIndex)
		||d.index == this.nodeIndex
		||contains(d.contributor_id, this.edgeId))){return true}
	if (!this.showAllNodes){return false}
	else return true
}

EventViewerClass.prototype.isVisibleEdge = function(d){
	if (!this.json.edges[d.parentIndex].custVis && !this.showCustVis){return false}
	if (this.showRelatedEdges && ((this.json.edges[d.parentIndex].source == this.nodeIndex 
		||this.json.edges[d.parentIndex].target == this.nodeIndex)
			||this.json.edges[d.parentIndex].id == this.edgeId)){return true}
	if (!this.showAllEdges){return false}
	else return true
}


// function to determine if text is visible. Requires data :d and what: variable either NODE or EDGE
EventViewerClass.prototype.isVisible = function(d, what, p){

	//for edges
	if (what == EDGE){
		if (!this.json.edges[d.parentIndex].custVis && this.showCustVis){return false}
		
		if(d.parentIndex == this.edgeIndexPoint){	//if current edge index == index of the object being pointed at.
			return true;	
		}
		else if(d.parentIndex == this.edgeIndex){	//if current edge index == index of object selected.
			return true;
		}	
		if (this.showAllEdgeLabels){
				if (this.showRelatedEdges && ((this.json.edges[d.parentIndex].source == this.nodeIndex 
				||this.json.edges[d.parentIndex].target == this.nodeIndex)
				||this.json.edges[d.parentIndex].id == this.edgeId)){return true}
				if (!this.showAllEdges){return false}
				else return true
		}
		if (this.showRelatedEdgeLabels && (this.showRelatedEdges || this.showAllEdges)){
			if((this.json.edges[d.parentIndex].source == this.nodeIndex 
			||this.json.edges[d.parentIndex].target == this.nodeIndex)
			||this.json.edges[d.parentIndex].id == this.edgeId){return true}
		}
		
	}
	//for Nodes
	if (what == NODE){	
		if (!d.custVis && !this.showCustVis){return false}
		if (this.showAllNodeLabels){
			//if (!d.custVis && this.showCustVis){return false}
			if (this.showRelatedNodes && (contains(d.neighbors, this.nodeIndex)||d.index == this.nodeIndex)){return true}
			if (!this.showAllNodes){return false}
			else return true;
		}
		if (this.showRelatedNodeLabels && contains(d.neighbors, this.nodeIndex)){return true}
		if (p.index == this.nodeIndexPoint){	//if current node index == index of object selected.
			return true;
		}
		if (p.index == this.nodeIndex){		//if current node index == index of object selected
			return true;
		}
		if (contains(d.contributor_id, this.edgeId)){	//if selected contributor has passed through this node
			return true;
		}
		
	}	    
	else
	return false;	 
}

EventViewerClass.prototype.recentre = function(){
	this.currentFocus = NODE;
	this.nodeIndex = this.centralNode;
	this.edgeId = -1;
	this.edgeIndex = -1;
	this.displayPanelInfo(NODE);
	this.render();
	
}


