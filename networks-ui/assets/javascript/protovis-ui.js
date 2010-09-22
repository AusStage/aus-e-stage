//global variables, to be used with protovis code.
var contributors = null ;
var vis = null;

// define variables for colour options.
    	
	var bgColour = "white";
    		
	var activeLinkStroke = "rgba(0,0,255,1)";
    var relatedLinkStroke = "rgba(46,46,46,0.7)"; 
    var inactiveLinkStroke = "rgba(170,170,170,0.7)";
    var mouseoverLinkStroke = "rgba(220,20,60,0.7)";
    var linkColourHolder;
    		
    var relatedNodeFill = "rgba(46,46,46,1)";
    var relatedNodeStroke = "rgba(46,46,46,1)";
    		
    var inactiveNodeFill = "rgba(170,170,170,1)";
    var inactiveNodeStroke = "rgba(170,170,170,1)";
    		
    var focusNodeFill = "blue";
    var focusNodeStroke = "blue";
			 
	var w = 796,
    	h = 600;

// define nodeFocus. True if focus is on nodes. False if on edges.
    var nodeFocus = true;    	

// define mouseOverEdge. True if mouse is over an edge.
    var mouseOver = false;
		
// store target and source node for edge focus operations
    var targetNodeSelect;
    var sourceNodeSelect;
    	
// store seperate target and source node info for mouse over operations.
    var targetNodeMO;
    var sourceNodeMO;	



function getNodes(){return contributors.nodes;}
function getEdges(){return contributors.edges;}



//create the network
function createNetwork(targetDiv){
	if (contributors != null){
    	
    	resetDateRangeVisibility();    		
    	////////////////////////////////////////////////////////	
		// construct a 3d array of nodes and their neighbors
			var nodeNeighbors = loadNodeNeighbors(contributors);

		///////////////////////////////////////////////////////
		//fix the central node in position. This is a fix to help the stability of the system
			contributors.nodes[0].fix = new pv.Vector(w/2,h/2);

		///////////////////////////////////////////////////////
		// keep a record of the currently selected node. Assuming, on load that the currently selected node is the first in the array.
			var activeNode = 0; 
			var nodeLinkDegree = contributors.nodes[0].collaborations;

		///////////////////////////////////////////////////////
		// new panel. 
		// Essentially the base element 
		// for all protovis visualisation
			vis = new pv.Panel().canvas(targetDiv)
			    .width(w)
			    .height(h)
			    .fillStyle(bgColour)
			    .event("mousedown", pv.Behavior.pan())
			    .event("mousewheel", pv.Behavior.zoom());

		///////////////////////////////////////////////////////
		//add a force layout to the Panel. 
			var	force = vis.add(pv.Layout.Force)
			    .nodes(getNodes())
			    .links(getEdges())
			    .bound(true)
			    .dragConstant(0.05)
			    .springConstant(0.05)
 			    .iterations(500)
			    .transform(pv.Transform.identity.scale(3).translate(-w/3,-h/3)) 

				;

		/////////////////////////////////////////////////////////
		//add the edges
  		   force.link.add(pv.Line) 
  		   		.strokeStyle(function(d, p){return mouseOver && p.targetNode.index == targetNodeMO && p.sourceNode.index == sourceNodeMO ?
			    					 		mouseoverLinkStroke :
			    								 nodeFocus ? 
			    									p.sourceNode.index == activeNode ?
			    									 relatedLinkStroke : 
		    									p.targetNode.index == activeNode ? relatedLinkStroke : inactiveLinkStroke : 
		    								p.targetNode.index == targetNodeSelect && p.sourceNode.index == sourceNodeSelect ? activeLinkStroke : 
		    								inactiveLinkStroke
			    })
			    .visible(function (d, p){ 
			    							if (($("#startDate").val()>p.firstDate)||($("#endDate").val()<p.firstDate)) {			    				
			    								return false; 
			    							}else 
			    							p.targetNode.visible = true;
			    							p.sourceNode.visible = true;
			    							return true;	
			    						})
		    				 
	//	    	.event("mouseover", function(d, p) {targetNodeMO = p.targetNode.index;
	//		    									sourceNodeMO = p.sourceNode.index;
	//		    									mouseOver = true;
	//		    									return vis;
	//	    										})
		    										
	//	    	.event("mouseout", function(d,p) {mouseOver = false;
	//	    									  targetNodeMO = -1;
	//	    									  sourceNodeMO = -1;
	//	    									  return vis;
	//	    									  })

			    .event("click", function(d, p) {updateInfoWindowEdge(p);
			    								targetNodeSelect = p.targetNode.index;
			    								sourceNodeSelect = p.sourceNode.index;
			    								targetNodeMO = -1;
			    								sourceNodeMO = -1;
			    								nodeFocus=false;
			    								vis.render();})
			;
			
		//add the nodes
			force.node.add(pv.Dot)
			    .size(function(d){return (d.linkDegree + 1) * Math.pow(this.scale, -1.5)})
			    .fillStyle(function(){return nodeFocus ? 
			    					  	activeNode == this.index ? focusNodeFill : 
			    							contains(nodeNeighbors[activeNode], this.index) ? relatedNodeFill :inactiveNodeFill : 
			    						 targetNodeSelect == this.index || sourceNodeSelect == this.index ? focusNodeFill : inactiveNodeFill})

//mouseover functionality removed. can be added to the function below.
//mouseOver && this.index == targetNodeMO || this.index == sourceNodeMO ? mouseoverLink : 

			    .strokeStyle(function() { return
			    						nodeFocus ? 
			    							activeNode == this.index ? focusNodeStroke : 
			    								contains(nodeNeighbors[activeNode], this.index) ? relatedNodeStroke:inactiveNodeStroke:
			    							targetNodeSelect == this.index || sourceNodeSelect == this.index ? focusNodeStroke : inactiveNodeStroke})

			    .lineWidth(2)
			    .visible(function (d){
			    					if (d.visible) return true;
			    					else return false;
			    					 })
			  
	 		  	.event("mouseover", function(d) {targetNodeMO = d.index;
		    									mouseOver = true;
		    									return vis;})
		    	.event("mouseout", function() {mouseOver = false;
		    								   targetNodeMO = -1;
											   return vis;})
			    .event("mousedown", pv.Behavior.drag())
			    .event("drag", force)
			    .event("click", function(d) { nodeLinkDegree = d.linkDegree;
    										  activeNode = this.index;
    										  nodeFocus = true;
    										  updateInfoWindow(activeNode, contributors);
    										  return vis;})
    										  
    			.event("dblclick", function(d){return findAndDisplayContributorNetwork(contributors.nodes[this.index].id)})
    			.anchor("top").add(pv.Label).text(function(d){return d.nodeName})
    				.font("9pt sans-serif")
					.textStyle(	function(){return nodeFocus ? activeNode == this.index ? focusNodeStroke : contains(nodeNeighbors[activeNode], this.index) ? relatedNodeStroke:inactiveNodeStroke: targetNodeSelect == this.index || sourceNodeSelect == this.index ? focusNodeStroke : inactiveNodeStroke})
					.textShadow("0.1em 0.1em 0.1em #fff")
					.visible(function(){return mouseOver && this.index == targetNodeMO || this.index == sourceNodeMO ? true :
										nodeFocus ? activeNode == this.index?true:false:
							    		targetNodeSelect == this.index || sourceNodeSelect == this.index ? true : false})
    			;



		
			}			
}

//reload network and render
function reloadNetwork(targetDiv){
		createNetwork(targetDiv);
		vis.render();	
	}
	
//refresh network for date range	
function refreshNetworkForDateRange(){
	resetDateRangeVisibility();
	vis.render();	
	}

//resets all the visibility fields for nodes
function resetDateRangeVisibility(){

	for (var i = 0; i<contributors.nodes.length; i++){

		contributors.nodes[i].visible=false;
		}
	}


