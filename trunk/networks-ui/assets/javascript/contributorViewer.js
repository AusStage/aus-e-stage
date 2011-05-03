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
 
 /*contributor viewer - person to person network viewer */

 
function ContributorViewerClass(){
	
}

//constructor
function ContributorViewerClass(type){

	/*sizing*/
	this.spacer = 40;
	this.hSpacer = 60; 	
	this.w = $(window).width() - ($(".sidebar").width()+this.spacer);
	this.h = $(window).height() - ($(".header").height()+$(".footer").height()+$('#fix-ui-tabs').height()+this.hSpacer);
	
	
	this.viewType = type;
	
	this.vis = new pv.Panel();
	this.force;//?
	this.json = {'edges':[],'nodes':[]};
	this.renderComplete = false;
	
	/*timeline*/
	this.timelineObj = new TimelineClass();
	this.timelineObj.init();

	/* appearance variables */
	this.thickLine = 3;
	this.thinLine = 1.5;

	this.largeFont = "8 pt sans-serif";
	this.smallFont = "6 pt sans-serif";
	//edge, node and background colouring for normal browsing
	this.nodeColors = {	panelColor:			"white",
						selectedNode:		"rgba(0,0,255,1)",				//blue
						unselectedNode:		"rgba(170,170,170,1)",		//light grey
						relatedNode:		"rgba(46,46,46,1)",				//dark grey	
						outOfDateNode:		"rgba(170,170,170,0.5)",		//light grey transparent
						selectedNodeBorder:	"rgba(0,0,255,1)",	  			//blue								
						unselectedNodeBorder:"rgba(170,170,170,1)",			//light grey
						relatedNodeBorder:	"rgba(46,46,46,1)",				//dark grey
						outOfDateNodeBorder:"rgba(170,170,170,0.1)",		//light grey transparent	
						selectedEdge:		"rgba(0,0,255,1)",				//blue				
						unselectedEdge:		"rgba(170,170,170,0.7)",		//light grey
						relatedEdge:		"rgba(46,46,46,0.7)",			//dark grey
						outOfDateEdge:		"rgba(170,170,170,0.5)",
						selectedText:		"rgba(0,0,255,1)",				//blue
						unselectedText:		"rgba(170,170,170,1)",			//light grey
						relatedText:		"rgba(46,46,46,1)",				//dark grey
						outOfDateText:		"rgba(170,170,170,0.3)"
					  }
	//edge, node and background colouring for faceted browsing				  
	this.nodeColorsF = {panelColor:			"rgba(46, 46, 46, 1)",		
						selectedNode:		"rgba(170,170,170,1)",			//light grey
						unselectedNode:		"rgba(170,170,170,1)",			//light grey
						relatedNode:		"rgba(170,170,170,1)",			//light grey
						highlightNode:		"blue",							//blue
						selectedNodeBorder:	"white",						//white
						unselectedNodeBorder:"rgba(170,170,170,1)",			//light grey
						relatedNodeBorder:	"white",						//white
						highlightNodeBorder:"blue",							//blue
						selectedEdge:		"rgba(255,255,255,0.9)",		//white 
						unselectedEdge:		"rgba(170,170,170,0.5)",		//light grey
						relatedEdge:		"rgba(255,255,255,0.9)",		//white
						outOfDateEdge:		"rgba(170,170,170,0.2)",
						selectedText:		"rgba(170,170,170,1)",			//light grey
						unselectedText:		"rgba(170,170,170,1)",			//light grey
						relatedText:		"rgba(170,170,170,1)",			//light grey
						highlightText:		"blue",							//blue
					   }

	//switches - show labels and faceted view on/off
	this.showAllContributors = false;	//set to true if related checkbox is checked. Will display all contributor labels
	this.showRelatedContributors = false;	//set to true if related checkbox is checked. Will display related contributor labels
	this.showCustColors = true;
	this.showCustVis = true;
	this.viewFaceted = false;			//set to true if facteded browsing selected.
	this.showAllFaceted = true;			//set to true if all nodes are visible in faceted search

	this.centralNode = -1;
	//MOUSE OVER VARIABLES
	this.nodeIndexPoint = -1;	//stores the index of the node that the mouse is currently over. Used for mouse in/out. 
	this.edgeTIndexPoint = -1;  	//stores the target index of the edge that the mouse is currently over. Used for mouse in/out. 
	this.edgeSIndexPoint = -1;	//stores the source index of the edge that the mouse is currently over. Used for mouse in/out. 
 
	//CLICK VARIABLES
	this.custNodeIndex = -1;	//stores the index of the element to be altered
	this.nodeIndex = -1;		//stores the INDEX of the selected NODE	 
	this.edgeTIndex = -1;		//stores the target index of the edge selected
	this.edgeSIndex = -1;		//stores the source index of the edge selected
	
	//CLASS METHODS
	this.getNodes = function(){return this.json.nodes;}
	this.getEdges = function(){return this.json.edges;}
	this.render = function(){this.vis.render();}
}

//clear visualiser
ContributorViewerClass.prototype.destroy = function(){
	$('#'+PANEL).empty();
}

//render the graph
ContributorViewerClass.prototype.renderGraph = function(json){
	this.json = json;
	//prepare data for render
	this.prepareData();
	initGraph(this);
	this.displayNetworkProperties();
	this.displayPanelInfo(NODE);
}

/*graph base code*/
function initGraph(obj){
		obj.vis = new pv.Panel().canvas(PANEL)
				.fillStyle(function(){return obj.getPanelColor()})
			  	.width(function(){return obj.w})
			  	.height(function(){return obj.h})
			  	.event("mousedown", pv.Behavior.pan())
			  	.event("mousewheel", pv.Behavior.zoom())
			  	.event("click", function(d) {obj.onClick(null, CLEAR); return obj.vis;});

		//add a force layout to the Panel. 
		var force = obj.vis.add(pv.Layout.Force)
			  	.nodes(function(){return obj.json.nodes})
			    .links(function(){return obj.json.edges})
			    .bound(true)
			    .chargeConstant(-80)
			    .dragConstant(0.06)
			    .springConstant(0.05)
			    .chargeMaxDistance(600)			    
			    .springLength(150)
 			    .iterations(500)
 			    .transform(pv.Transform.identity.scale(1));

		//add the edges
  		force.link.add(pv.Line)
	  			.lineWidth(function(d,p){return obj.getEdgeLineWidth(p)})
	  			.strokeStyle(function(d,p){return obj.getEdgeStroke(p)})
	  			.visible(function(d,p){return obj.isVisibleEdge(p)})
	  			.event("click", function(d, p) {obj.onClick(p, EDGE); return obj.vis;})		
	  			.event("mouseover", function(d, p){	obj.edgeTIndexPoint = p.target;
	  											 	obj.edgeSIndexPoint = p.source;
	  											 	return obj.vis})
	  			.event("mouseout", function(d, p){	obj.edgeTIndexPoint = -1;
	  											 	obj.edgeSIndexPoint = -1;
	  											 	return obj.vis});
	  											 	
		//add the nodes
		force.node.add(pv.Dot)
			    .size(function(d){return (d.linkDegree + 1) * Math.pow(this.scale, -1.5)})
			   	.fillStyle(function(d){return obj.getNodeFill(d)})
			    .strokeStyle(function(d){return obj.getNodeStroke(d)})
			    .lineWidth(function(d) {return obj.getNodeLineWidth(d)})
			    .visible(function(d){return obj.isVisibleNode(d)})
			    	    
				.event("click", function(d) {obj.onClick(d, NODE); return obj.vis;})
				.event("dblclick", function(d){obj.onDblClick(d)})
			    .event("mouseover", function(d){obj.nodeIndexPoint = d.index;
			    								return obj.vis;})
			    .event("mouseout", function(d){obj.nodeIndexPoint = -1;
			    								return obj.vis;})
				.event("mousedown", pv.Behavior.drag())			    								
			    .event("drag", force)
    				.anchor("top").add(pv.Label)
    				.text(function(d){return d.nodeName})
    				.visible(function(d){return obj.isVisible(d)})
    				.font(function(d){return obj.getFont(d)})
    				.textStyle(function(d){return obj.getTextStyle(d)});

    	obj.render();
    	obj.renderComplete=true;
	
}

//refresh network 
ContributorViewerClass.prototype.refreshGraph = function(typeOfRefresh){
	if(typeOfRefresh == "dateRange"){
		this.resetDateRangeVisibility();
	}
	else if (typeOfRefresh == "faceted"){
		this.resetFacetedVisibility();
	}
	if (this.json != null){
		this.render();
	}
}

ContributorViewerClass.prototype.showInteraction = function(){
	if(this.renderComplete){
		$("#viewer_options_div").show();//show viewer options accordion
		$("#display_labels_div").show();//show display label options
		if (this.nodeIndex>-1 || this.edgeTIndex>-1){
			$('#network_details_div').show();
		}
		$("#network_properties_div").show();//show network properties	
		$("#faceted_browsing_btn_div").show();//show faceted browsing on/off		
		$('#timeSlider').show();
	}
}

ContributorViewerClass.prototype.hideInteraction = function(){
	$("#viewer_options_div").hide();
	$("#display_labels_div").hide();//show display label options
	$('#network_details_div').hide();
	$("#network_properties_div").hide();//show network properties	
	$("#faceted_browsing_btn_div").hide();//show faceted browsing on/off
	$('#faceted_browsing_div').dialog('close');			
	$('#timeSlider').hide();
}


//INTERACTION FUNCTIONS - resize, zoom etc
//=======================================================

//double click 
ContributorViewerClass.prototype.onDblClick = function(d){
	viewerControl.displayNetwork('CONTRIBUTOR', d.id, 0);	
	
}

//click functionality
ContributorViewerClass.prototype.onClick = function(d, what){
	if(pv.event.altKey && what==NODE){
		$("#custom_div").dialog( "option", "position", [pv.event.x, pv.event.y] );
		$("#custom_div").dialog("open");
		this.custNodeIndex = d.index;
	}
	else{
		switch (what){
			case CLEAR: 
				this.nodeIndex = -1;					
				this.edgeTIndex = -1;
				this.edgeSIndex = -1;
				break;
		
			case EDGE: 
				this.nodeIndex = -1;	
				this.edgeTIndex = d.target;
				this.edgeSIndex = d.source;	
				break;
		
			case NODE: 
  	   		    this.nodeIndex = d.index;
				this.edgeTIndex = -1;
				this.edgeSIndex = -1;  	   	    
				break;
		}
		this.displayPanelInfo(what);				
	}	
}

//window resize handler
ContributorViewerClass.prototype.windowResized = function(){		  
}

//GRAPH APPEARANCE FUNCTIONS
//=======================================================

//add custom color to a node
ContributorViewerClass.prototype.setColor = function(c){
	this.json.nodes[this.custNodeIndex].custColor = c;	
}
//remove the custom color for one node
ContributorViewerClass.prototype.resetColor = function(){
	this.json.nodes[this.custNodeIndex].custColor = null;
}
//remove the custom color for all nodes
ContributorViewerClass.prototype.resetAllColors = function(){
	for (var i = 0; i<this.json.nodes.length; i++){
		this.json.nodes[i].custColor = null;	
	}	
}
ContributorViewerClass.prototype.hideElement = function(){
	this.json.nodes[this.custNodeIndex].custVis = false;
	for (var i = 0; i < this.json.nodes[this.custNodeIndex].edgeList.length; i++){
		this.json.edges[this.json.nodes[this.custNodeIndex].edgeList[i]].custVis = false;
	}
}

ContributorViewerClass.prototype.resetHiddenElements = function(){
 	for (var i = 0; i<this.json.nodes.length; i++){
		this.json.nodes[i].custVis = true;	
	}	
	for (var i = 0; i<this.json.edges.length; i++){
		this.json.edges[i].custVis = true;	
	}	
}


ContributorViewerClass.prototype.getPanelColor = function(){
	return (this.viewFaceted) ? this.nodeColorsF.panelColor:this.nodeColors.panelColor;	
}

//node visibility
ContributorViewerClass.prototype.isVisibleNode = function(d){
	if (!d.custVis && this.showCustVis){return false}
	if (this.viewFaceted && !this.showAllFaceted){return (d.facetedMatch && d.withinDateRange)?true:false;}
	if (!d.withinDateRange){return false;}
	else return true;	
}
//edge visibility
ContributorViewerClass.prototype.isVisibleEdge = function(p){
	if (!p.custVis && this.showCustVis){return false} 
	if (this.viewFaceted && !this.showAllFaceted){
		return (p.targetNode.facetedMatch && p.sourceNode.facetedMatch &&p.targetNode.withinDateRange && p.sourceNode.withinDateRange)?true:false;}
	if(!p.targetNode.withinDateRange || !p.sourceNode.withinDateRange){return false}
	else return true;	
}

//label visibility
ContributorViewerClass.prototype.isVisible = function(d){
	if (!d.custVis && this.showCustVis){return false}
	if (this.viewFaceted && !this.showAllFaceted){if (!d.facetedMatch){return false}}
	if (!d.withinDateRange) {return false}
	//if mouse is over the node
	if(d.index==this.nodeIndexPoint){return true}
	//if node is selected
	else if (d.index==this.nodeIndex){return true}
	//if mouse is over a related edge
	else if (d.index == this.edgeTIndexPoint || d.index == this.edgeSIndexPoint){return true}
	//if related edge is selected
	else if (d.index == this.edgeTIndex || d.index == this.edgeSIndex){return true}
	//if show all contributors button is selected
	else if (this.showAllContributors){return true}
	//if show related contributors is selected
	else if (this.showRelatedContributors && (contains(d.neighbors, this.nodeIndex))){return true}
	else return false;
}


//determine font
ContributorViewerClass.prototype.getFont = function(d){
	
	//if current node index == index of object selected.			
	if (d.index == this.nodeIndexPoint){return this.largeFont;}
	//if current node index == index of object selected
	if (d.index == this.nodeIndex){return this.largeFont;}
	//if selected contributor is related to this node
	if (contains(d.neighbors, this.nodeIndex)){return this.largeFont;}
	//if mouse is over a related edge
	else if (d.index == this.edgeTIndexPoint || d.index == this.edgeSIndexPoint){return this.largeFont}
	//if related edge is selected
	else if (d.index == this.edgeTIndex || d.index == this.edgeSIndex){return this.largeFont}

	else 
		return this.smallFont;
}

ContributorViewerClass.prototype.getTextStyle = function(d){
	var color = (this.viewFaceted)? this.nodeColorsF:this.nodeColors;	
	if(d.custColor && this.showCustColors){return d.custColor;} 
	//if node/edge is out of the date range
	if (!d.withinDateRange && !this.viewFaceted){return color.outOfDateText;}
	//if node matches faceted criteria
	if (this.viewFaceted && d.facetedMatch){return color.highlightText}
	//if node is selected
	if (d.index == this.nodeIndex){return color.selectedText;}
	//if node is related to selected node
	else if (contains(d.neighbors, this.nodeIndex)){return color.relatedText;}
	//if node is related to selected edge
	else if (d.index == this.edgeTIndex || d.index == this.edgeSIndex){return color.relatedText}
	else return color.unselectedText;	
}


//line width
ContributorViewerClass.prototype.getNodeLineWidth = function(d){
	switch(d.index){
		case this.nodeIndexPoint:
			return this.thickLine;
			break;
		case this.nodeIndex:
			return this.thickLine;
			break;
		default : return this.thinLine;
	}
}

ContributorViewerClass.prototype.getNodeFill = function(d){
	var color = (this.viewFaceted)? this.nodeColorsF:this.nodeColors;	
	if (d.custColor && this.showCustColors){return d.custColor;}
	//check date range first. doesn't count if viewing faceted
	if (!d.withinDateRange && !this.viewFaceted){return color.outOfDateNode;}
	//if viewing faceted, change the color scheme, nodes are either selected or not
	if(this.viewFaceted){return (d.facetedMatch) ? color.highlightNode:color.unselectedNode}
	//if node is selected
	else if (d.index == this.nodeIndex){return color.selectedNode;}
	//if node is related to selected node
	else if (contains(d.neighbors, this.nodeIndex)){return color.relatedNode;}
	//if node is related to selected edge
	else if (d.index == this.edgeTIndex || d.index == this.edgeSIndex){return color.relatedNode}
	else return color.unselectedNode;	
}

ContributorViewerClass.prototype.getNodeStroke = function(d){
	var color = (this.viewFaceted) ? this.nodeColorsF:this.nodeColors;	
	//if node is out of the date range
	if (!d.withinDateRange && !this.viewFaceted){return color.outOfDateNodeBorder;}	
	//if node is selected
	else if (d.index == this.nodeIndex){return color.selectedNodeBorder;}
	//if node is related to selected node
	else if (contains(d.neighbors, this.nodeIndex)){return color.relatedNodeBorder;}
	//if node is related to selected edge
	else if (d.index == this.edgeTIndex || d.index == this.edgeSIndex){return color.relatedNodeBorder}	
	else if (this.viewFaceted && d.facetedMatch){return color.highlightNodeBorder}
	else return color.unselectedNodeBorder;
}

ContributorViewerClass.prototype.getEdgeStroke = function(p){
	var color = (this.viewFaceted) ? this.nodeColorsF:this.nodeColors;	
	//if edge is out of the date range
	if (!p.withinDateRange){return color.outOfDateEdge;}
	//if the edge connects to the selected node
	if (p.source == this.nodeIndex || p.target == this.nodeIndex){
		return color.relatedEdge;
	}
	else if(p.source == this.edgeSIndex && p.target == this.edgeTIndex){
		return color.selectedEdge;	
	}
	else return color.unselectedEdge;
}

ContributorViewerClass.prototype.getEdgeLineWidth = function(p){
	var i = p.value;
	if (i != 1){i = i/2;}
	
	//if mouse is over this edge
	if(p.source == this.edgeSIndexPoint && p.target == this.edgeTIndexPoint){
		return i+2;		
	}
	if(p.source == this.edgeSIndex && p.target == this.edgeTIndex){
		return i+2;		
	}
	else return i;
}

/* SIDE PANEL INFORMATION DISPLAY METHODS
====================================================================================================================*/
//display the network properties
ContributorViewerClass.prototype.displayNetworkProperties = function(){	
		var collabCount = 0;
		var tempHtml = "";
		for (i=0;i<this.json.edges.length;i++){
			collabCount = collabCount+this.json.edges[i].value;
		}
		var comma = "";	
		var contribUrl = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_contrib_id=";
	    var html = 	"<table>"+
	  				"<tr class=\"d0\"><td valign=top><b> Centre:</b></td><td>"+
	  				"<a href=" + contribUrl +""+ this.json.nodes[this.centralNode].id+" target=\"_blank\">"+	
	  				this.json.nodes[this.centralNode].nodeName+"</a><p>";
    	for (var i=0; i<this.json.nodes[this.centralNode].functions.length; i++){
    		tempHtml+= comma + this.json.nodes[this.centralNode].functions[i];
    		comma = ", ";
    	}
    	html += constrain(tempHtml, $(".sidebar").width()-70, "legendBody", "ellipsis");
    	html += "</p>";
    	html += "</td></tr>"+
    				"<tr class=\"d1\"><td><b>Contributors </b></td><td> "+this.json.nodes.length+"</td></tr>"+					
					"<tr class=\"d0\"><td><b>Relationships </b> </td><td>"+this.json.edges.length+"</td></tr></table>";			
					"<tr class=\"d1\"><td><b>Collaborations </b> </td><td>"+collabCount+"</td></tr></table>";							
 		$("#network_properties_body").empty();
		$("#network_properties_body").append(html);
		$(".ellipsis").tipsy({gravity: $.fn.tipsy.autoNS});

}

//display information about the selected element.
ContributorViewerClass.prototype.displayPanelInfo = function(what){
	
	var comma = "";
	var eventUrl = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_event_id="; 
	var contributorUrl = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_contrib_id="
	var titleHtml = ""
	var html = "<table width=100%>";
	var tempHtml = "";
	var dateFormat = pv.Format.date("%d %b %Y"); //create date formatter, format = dd mmm yyyy
	var tableClass = "";
	
	var contributorList = new Array();
	var eventList = [];
	
	//clear the info panel
	$("#selected_object_header").empty();
	$("#selected_object_body").empty();
	
	if (what == CLEAR){	
		html = " "; 
		$("#network_details_div").hide();
		resetLegend("#selected_object");
	}
	else{$("#network_details_div").show();}
			
	
	//***************/
	//NODE
	if (what == NODE){
	
		//set the title to the contributor.
		titleHtml = "<a class=\"titleLink\" href="+contributorUrl+this.json.nodes[this.nodeIndex].id+" target=\"_blank\">"+
					this.json.nodes[this.nodeIndex].nodeName+"</a> <p>";
    	// add the roles
    	for (var i=0; i<this.json.nodes[this.nodeIndex].functions.length; i++){
    		tempHtml+= comma + this.json.nodes[this.nodeIndex].functions[i];
    		comma = ", ";
    	}
		titleHtml += constrain(tempHtml, $(".sidebar").width()-50, "legendHeader", "ellipsis header");
	    tempHtml = "";

    	titleHtml +="</p>";
		
		//create an array of related contributors, sort by last name
    	for (i = 0; i < this.json.nodes[this.nodeIndex].neighbors.length; i++){

    		var lastName = this.json.nodes[this.json.nodes[this.nodeIndex].neighbors[i]].nodeName.split(" ")[1];
    		contributorList[i] = {name:lastName, 
    					   		  index:this.json.nodes[this.json.nodes[this.nodeIndex].neighbors[i]].index}		
    	}
    	contributorList.sort(sortByName);    	
    	
		//build the table of contributors
		for(i = 0; i < contributorList.length; i++){
			comma = "";
			if(isEven(i)) tableClass = "d0";
			 else tableClass = "d1";

			html += "<tr class=\""+tableClass+"\"><td><a href=" 
								+ contributorUrl +""+ this.json.nodes[contributorList[i].index].id
								+" target=\"_blank\">"+
								this.json.nodes[contributorList[i].index].nodeName +"</a>";
			
			html +="<p>";
			for (var x=0; x<this.json.nodes[contributorList[i].index].functions.length; x++){
    			tempHtml+= comma + this.json.nodes[contributorList[i].index].functions[x];
    			comma = ", ";
	    	}
	    	html += constrain(tempHtml, $(".sidebar").width()-20, "legendBody", "ellipsis");
	    	tempHtml = "";
			 
			html+="</p></td></tr>";
		}
		html += "</table><br>";	  
		
	}
	

	//***************/
	//EDGE
	if (what == EDGE){
	
		//add the target contributor
		titleHtml = "<a class=\"titleLink\" href="+contributorUrl+this.json.nodes[this.edgeTIndex].id+" target=\"_blank\">"+
					this.json.nodes[this.edgeTIndex].nodeName+"</a> <p>";
		//add target contributor roles
		for (var i=0; i<this.json.nodes[this.edgeTIndex].functions.length; i++){
    		tempHtml+= comma + this.json.nodes[this.edgeTIndex].functions[i];
    		comma = ", ";
    	}
    	titleHtml += constrain(tempHtml, $(".sidebar").width()-50, "legendHeader", "ellipsis header");
    	tempHtml = "";
    	titleHtml +="</p>";					

		//add the source contributor
		titleHtml += "<a class=\"titleLink\" href="+contributorUrl+this.json.nodes[this.edgeSIndex].id+" target=\"_blank\">"+
					this.json.nodes[this.edgeSIndex].nodeName+"</a> <p>";
		//add source contributor roles
		comma = "";
		for (var i=0; i<this.json.nodes[this.edgeSIndex].functions.length; i++){
    		tempHtml+= comma + this.json.nodes[this.edgeSIndex].functions[i];
    		comma = ", ";
    	}
    	titleHtml += constrain(tempHtml, $(".sidebar").width()-50, "legendHeader", "ellipsis header");
    	tempHtml = "";    	
    	titleHtml +="</p>";					

		//create the html to display the info.
    	for( i = 0;i<5; i++ ){
    		if(isEven(i)) tableClass = "d0";
			else tableClass = "d1";
			
    		html += "<tr class=\""+tableClass+"\"><td>events not yet supported</td></tr>"
    	}
    	
    	html+= "</table><br>";
		
	}
	
	$("#selected_object_header").button( "option", "label", titleHtml );					 		  		
	$("#selected_object_body").append(html); 
	$(".ellipsis").tipsy({gravity: $.fn.tipsy.autoNS});		

	$(".titleLink").click(function(){
		allowToggle = false;
	});     			

}

/* DATA MANIPULATION FUNCTIONS - manipulate retrieved data and prepare for visualisation.
====================================================================================================================*/

ContributorViewerClass.prototype.prepareData = function(){
	
	this.centralNode = this.json.nodes.length-1;
	//fix the central node in position. This is a fix to help the stability of the system	
	this.json.nodes[this.centralNode].fix = new pv.Vector(this.w/2, this.h/2);
	//set the active node
	this.nodeIndex = this.centralNode;
	//set list of adjoining nodes for each node
	this.loadNeighbors();
	//clean the date records
	this.cleanDates();
	//set the values for faceted browsing
	this.setFacetedOptions(this.getFacetedOptions());
	//set visibility fields for date range and date slider
	this.timelineObj.update();
	this.resetDateRangeVisibility();		
	
	
}


//adds extra array to each node storing neighboring node indexes
//also adds array of edge indexes relating to this node
ContributorViewerClass.prototype.loadNeighbors= function(){

	for(i = 0; i < this.json.nodes.length; i ++){

		//while we're already looping through the nodes array, best remove any double spaces
		this.json.nodes[i].nodeName = this.json.nodes[i].nodeName.replace("  ", " "); 
		//and set the custom fields
		this.json.nodes[i].custVis = true;
		this.json.nodes[i].custColor = null;
		//and double check nationality and gender are left empty
		if (this.json.nodes[i].gender == null){this.json.nodes[i].gender = "Unknown";}
		if (this.json.nodes[i].nationality == null){this.json.nodes[i].nationality = "Unknown";}		
		
		this.json.nodes[i].neighbors = [];
		this.json.nodes[i].edgeList = [];
		var neighbor_count = 0;
		
		for (x = 0; x < this.json.edges.length; x ++){
			//set custom fields
			this.json.edges[x].custVis = true;
			this.json.edges[x].custColor = null;
						
			if(this.json.edges[x].source == i){
				this.json.nodes[i].edgeList.push(x);
				if (!contains(this.json.nodes[i].neighbors, this.json.edges[x].target)){
					this.json.nodes[i].neighbors[neighbor_count] = this.json.edges[x].target;
					neighbor_count++;
				}
			}
			if(this.json.edges[x].target == i){
				this.json.nodes[i].edgeList.push(x);				
				if (!contains(this.json.nodes[i].neighbors, this.json.edges[x].source)){
					this.json.nodes[i].neighbors[neighbor_count] = this.json.edges[x].source;
					neighbor_count++;
				}
			}			
		}			
	}	
}


/* DATE BROWSE FUNCTIONS - set the time slider, reset visible values based on date range. 
====================================================================================================================*/

//validate and clean date records.
ContributorViewerClass.prototype.cleanDates = function(){
	for (i in this.json.edges){
		switch (this.json.edges[i].firstDate.length){
			case 6:
				this.json.edges[i].firstDate = this.json.edges[i].firstDate.substring(0,4)+"-01-01";
				break;
			case 8:
				this.json.edges[i].firstDate = this.json.edges[i].firstDate.substring(0,7)+"-01";
				break;
		}
		this.json.edges[i].firstDate = parseDate(this.json.edges[i].firstDate);	
	}	
}

//given the selected date range, reset nodes and edge visibility
ContributorViewerClass.prototype.resetDateRangeVisibility = function(){
	if (this.json != null){
		var minDate =new Date(viewer.timelineObj.selectedFirstDate);
		var maxDate =new Date(viewer.timelineObj.selectedLastDate); 
		//for each node
		for (n in this.json.nodes){
			var hasVisibleEdge = false;
	
			//for each edge related to the node
			for(e in this.json.nodes[n].edgeList){
				var edge=this.json.nodes[n].edgeList[e];
				//if edge is out of date range
				if(this.json.edges[edge].firstDate < minDate || this.json.edges[edge].firstDate > maxDate){
					this.json.edges[edge].withinDateRange = false;				
				} else {
					this.json.edges[edge].withinDateRange = true;
					hasVisibleEdge = true;				
				}
			}
			this.json.nodes[n].withinDateRange = hasVisibleEdge;		
		}
	}	
}

/* FACETED BROWSE FUNCTIONS - set the values for faceted browse, define the faceted window etc.
====================================================================================================================*/
//get all functions from the contributor list.
ContributorViewerClass.prototype.getFacetedOptions = function(){
	
	//function variables
	var functionList = [];
	//gender variables
	var genderList = [];
	//nationality variables
	var nationalityList = [];

	//loop through the nodelist.
	for (i in this.json.nodes){
		//find unique genders.
		if (!contains(genderList, this.json.nodes[i].gender)){
			genderList.push(this.json.nodes[i].gender);
		}
		//find unique nationality.
		if (!contains(nationalityList, this.json.nodes[i].nationality)){
			nationalityList.push(this.json.nodes[i].nationality);
		}			
		//loop through the function list. 
		for (x in this.json.nodes[i].functions){	
			//if - this function is not in function list, add it to the function list.
			if (!contains(functionList, this.json.nodes[i].functions[x] )){
				functionList.push(this.json.nodes[i].functions[x]);
			}
		//else do nothing
		}
	}
	return {functions: functionList.sort(), genders: genderList.sort(), nationalities:nationalityList.sort()};
}

//set the select box with functions
ContributorViewerClass.prototype.setFacetedOptions = function(list){
	var html = "";	
	var tableClass = "";
	//clear the functions list
	$("#functions_body").empty();
	//clear the gender list
	$("#gender_body").empty();
	//clear the nationality list
	$("#nationality_body").empty();
	//clear the description area
	$("#faceted_selection_p").empty();

	//create the function checkboxes
	html +="<table>";		
	
	html += '<tr class="d1"><td><input type="checkbox" id="select_all_functions" value="select_all_functions" />'+
			'<label for="select_all_functions">Select All</label></td></tr>';

	for(i in list.functions){
		tableClass = (isEven(i)) ? "d0":"d1";
		
		html += '<tr class="'+tableClass+'"><td><input type="checkbox" id="function" value="'+list.functions[i]
		+'" /> <label for="'+i+'">'+list.functions[i]+'</label></td></tr>';
	}	
	html +="</table>";
	$("#functions_body").append(html);
	
	//create the gender checkboxes
	html = "<table>";
	for(i in list.genders){
		tableClass = (isEven(i)) ? "d0":"d1";

		html+= ('<tr class="'+tableClass+'"><td><input type="checkbox" id="gender" value="'+list.genders[i]
		+'" /> <label for="'+i+'">'+list.genders[i]+'</label></td></tr>');
	}	
	html += "</table>";
	$('#gender_body').append(html);
	
	//create the nationality checkboxes
	html = "<table>";
	
	html += '<tr class="d1"><td><input type="checkbox" id="select_all_nationalities" value="select_all_nationalities" />'+
			'<label for="select_all_nationalities">Select All</label></td></tr>';
	for(i in list.nationalities){
		tableClass = (isEven(i)) ? "d0":"d1";

		html+= ('<tr class="'+tableClass+'"><td><input type="checkbox" id="nationality" value="'+list.nationalities[i]
		+'" /> <label for="'+i+'">'+list.nationalities[i]+'</label></td></tr>');
	}	
	html += "</table>";
	$('#nationality_body').append(html);
	

	$('input#function').click(function(){
							updateFacetedTerms();
							viewer.refreshGraph("faceted");
						});	
	$('input#nationality').click(function(){
							updateFacetedTerms();
							viewer.refreshGraph("faceted");
						});	
	$('input#gender').click(function(){
							updateFacetedTerms();
							viewer.refreshGraph("faceted");
						});								
	
	//select all functionality
	$("input#select_all_functions").click(function() { 
		//if checked, then set showAllFaceted to true, else set to false;
    	$("input#function").each(function () {
		    if($("input#select_all_functions").is(":checked")){
	        	$(this).attr("checked", true);
		    }else{$(this).attr("checked", false);}	   
		});
		updateFacetedTerms();
		viewer.refreshGraph("faceted");
	}); 	
	//select all functionality
	$("input#select_all_nationalities").click(function() { 
		//if checked, then set showAllFaceted to true, else set to false;
    	$("input#nationality").each(function () {
		    if($("input#select_all_nationalities").is(":checked")){
	        	$(this).attr("checked", true);
		    }else{$(this).attr("checked", false);}	   
		});
		updateFacetedTerms();
		viewer.refreshGraph("faceted");		
	}); 	
}

//update the display to show what's been selected
function updateFacetedTerms() {	
	//empty the existing terms	
	$("#criteria_body").empty();
	var displayStr = "";
	var startStr = "<b>Contributors who are: </b><br>";
	var middleStr = "<br><b>and </b>";
	var middleOrStr = "<br><b>or </b>";
	var functionStr = "";
	var genderStr = "";
	var nationalityStr = "";
	//if there are checked options	
	if($("input:checked").length>0){ displayStr = startStr;}    
	//loop through the checked functions 
    $("input#function:checked").each(function (index, value) {
    	functionStr += value.value + "<br>";
    });
    //if function string has been populated
    if(functionStr.length !=0){
    	functionStr += " ";
    }
    //remove the last space - and replace with s
	functionStr = functionStr.replace("<br> ","s ");
    //get selected gender
    $("input#gender:checked").each(function (index, value) {
          genderStr += value.value + " ";
    });
    //get the nationalities    
    $("input#nationality:checked").each(function (index, value) {
          nationalityStr += value.value + " "+ middleOrStr;
    });
    // insert 'or' between selected nationality values    
    if(nationalityStr.length!=0){
    	nationalityStr+= " ";
    }
    nationalityStr = nationalityStr.replace(middleOrStr+" "," ");
	//consolidate the strings - add the string of funcitons
	displayStr += functionStr;
	//if functions and gender has been selected add 'and'	
	if(functionStr.length !=0 && genderStr.length != 0){
		displayStr += middleStr;			
	}
	//add the gender string
	displayStr += genderStr;
		
	if((functionStr.length !=0 && genderStr.length != 0 && nationalityStr.length !=0)||
			(functionStr.length == 0 && genderStr.length !=0 && nationalityStr.length !=0)||
			(functionStr.length !=0 && genderStr.length ==0 && nationalityStr.length !=0) ){
				displayStr += middleStr;
	}
	displayStr += nationalityStr;
	//update the display                     
    $("#criteria_body").append(displayStr);
};

ContributorViewerClass.prototype.resetFacetedVisibility = function(){
/*set facetedMatch = true if matches criteria.
false if not. Used in the graph appearance functions
*/	
	var match;
	//get selected functions
	var functions = [];
	for (var f = 0; f < $("input#function:checked").length; f++){
		functions.push($("input#function:checked")[f].value);
	}
	//get selected gender
	var gender = [];
	for (var g = 0; g < $("input#gender:checked").length; g++){
		gender.push($("input#gender:checked")[g].value);
	}

	//get selected nationalities - put values into array
	var nationalities = [];
	for (var n = 0; n < $("input#nationality:checked").length; n++){
		nationalities.push($("input#nationality:checked")[n].value);
	}
	//loop through each node and compare to the criteria.
	for(i in this.json.nodes){
		
		match = (functions.length==0 && gender.length==0 && nationalities.length==0) ? false:true; 
		for(var f = 0; f < functions.length; f++){
			if(!contains(this.json.nodes[i].functions, functions[f])){match = false;}
			else{match = true; f=functions.length++;}
		}
		if(gender.length != 0 && !contains(gender, this.json.nodes[i].gender)){match = false;}
		if(nationalities.length != 0 && !contains(nationalities, this.json.nodes[i].nationality)){match = false;}
		//set the node value dependant on match found
		this.json.nodes[i].facetedMatch = match;
	}
}




