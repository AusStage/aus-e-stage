		//Function definitions. 

		//loadNodeNeighbors takes the json data and loads each node as the first value in a 3d array. Any 
		// associated nodes are then added into the second array. 
			function loadNodeNeighbors(){
			var nodeHash = new Array();
			var firstIndex=0;
			for(firstIndex; firstIndex < contributors.nodes.length; firstIndex++){
				var secondIndex = 0;	
				nodeHash[firstIndex] = new Array();
				for(var x=0; x<contributors.edges.length; x++){
					if (contributors.edges[x].source == firstIndex){	
						nodeHash[firstIndex][secondIndex]=contributors.edges[x].target;
						secondIndex++;						
					}
					if (contributors.edges[x].target == firstIndex){
						nodeHash[firstIndex][secondIndex]=contributors.edges[x].source;
						secondIndex++;
					}
					}
				}
			return nodeHash;
			}


	function setDateRanges(startYear, startMonth, endYear, endMonth){
			var monthList=new Array(12);
			monthList[0]="Jan";
			monthList[1]="Feb";
			monthList[2]="Mar";
			monthList[3]="April";
			monthList[4]="May";
			monthList[5]="June";
			monthList[6]="July";
			monthList[7]="Aug";
			monthList[8]="Sept";
			monthList[9]="Oct";
			monthList[10]="Nov";
			monthList[11]="Dec";
				
			var firstRun = true;
			var finishPopulate = false;
			var currYear = startYear;
			var currMonth = startMonth;
			var startList = document.getElementById("startDate");
			var endList = document.getElementById("endDate");
				
		while (startList.hasChildNodes()) {
 			 startList.removeChild(startList.firstChild);
		}
		while (endList.hasChildNodes()) {
 			 endList.removeChild(endList.firstChild);
		}

		for(currYear; currYear<=endYear; currYear++){ 
				var yearGroup = document.createElement("optgroup")
				var yearGroup2 = document.createElement("optgroup")						
				yearGroup.label = currYear;
				yearGroup2.label = currYear						
						
				if (!firstRun) currMonth = 1; 
						
				for(currMonth; currMonth<=12 ; currMonth++){
					if(!finishPopulate){
						var month = document.createElement("option");
						var month2 = document.createElement("option");							
							
						month2.value = monthList[currMonth-1];
						month2.appendChild(document.createTextNode(monthList[currMonth-1] + " "+ currYear));							
						month.value = monthList[currMonth-1];
						month.appendChild(document.createTextNode(monthList[currMonth-1]+ " "+ currYear));
						if (firstRun){
								month.selected = true;
								firstRun = false; 
						}
						if (currYear == endYear && currMonth == endMonth){
								month2.selected = true;
								finishPopulate=true;
						}
						yearGroup.appendChild(month);
						yearGroup2.appendChild(month2);							
					}
				}
				startList.appendChild(yearGroup);
				endList.appendChild(yearGroup2);
						
						
		}
	}

    function displayAllInfo(activeNode){
    	updateNetworkInfo();
    	updateInfoWindow(activeNode);
    	}
    
    //function to set the network statistics
    function updateNetworkInfo(){
    	var networkDetails = document.getElementById("nodeCount");
    		networkDetails.innerHTML = "<b> Node Count: </b>"+contributors.nodes.length;
    	networkDetails = document.getElementById("edgeCount");
    		networkDetails.innerHTML = "<b> Edge Count: </b>"+contributors.edges.length;
    	networkDetails = document.getElementById("eventCount");
    		networkDetails.innerHTML = "<b> Event Count: </b>Undefined.";
    	networkDetails = document.getElementById("diameter");
    		networkDetails.innerHTML = "<b> Diameter: </b>Undefined.";

    	}
    
    function updateInfoWindow(activeNode){
    	clearInfoWindow("edge");
	//set the contributor name
    	var infoDetails = document.getElementById("contName");
    	infoDetails.innerHTML = "<b>"+contributors.nodes[activeNode].nodeName + 		" ("+contributors.nodes[activeNode].id+")</b>";
    	infoDetails.href = contributors.nodes[activeNode].collabUrl;
    //set the event range
    	infoDetails = document.getElementById("eventRange");
    	infoDetails.innerHTML = "<br><b>Event Range:</b><br>"+contributors.nodes[activeNode].firstDate+" to "+contributors.nodes[activeNode].lastDate; 
    //set the functions
    	infoDetails = document.getElementById("contFunct");
    	infoDetails.innerHTML = "<b>Functions:</b><br>"+contributors.nodes[activeNode].functions.replace(/ \| /g, "<br>");	
    //set the degree of connection
    	infoDetails = document.getElementById("degCon");
    	infoDetails.innerHTML = "<b>Degree of Connection:</b> "+nodeLinkDegree;	
	
    	}

	function clearInfoWindow(info){
		if (info == "node"){
	    //remove the contributor name
    		var infoDetails = document.getElementById("contName");
    		infoDetails.innerHTML = " ";
    	//remove the event range
    		infoDetails = document.getElementById("eventRange");
    		infoDetails.innerHTML = " "; 
    	//remove the functions
    		infoDetails = document.getElementById("contFunct");
    		infoDetails.innerHTML = " ";	
    	//remove the degree of connection
    		infoDetails = document.getElementById("degCon");
    		infoDetails.innerHTML = " ";	
		}
		else if (info == "edge"){
			infoDetails = document.getElementById("collab1");
    		infoDetails.innerHTML = " ";
			infoDetails = document.getElementById("collab2");
    		infoDetails.innerHTML = " ";
		//remove the edge weight
			infoDetails = document.getElementById("noOfCollab");
    		infoDetails.innerHTML = " ";
		}
	}
    
    function updateInfoWindowEdge(edgeInformation){
		//clear the infoWindow
			clearInfoWindow("node");
		//set collaborator information
	    	var infoDetails = document.getElementById("collab1");
    		infoDetails.innerHTML = "<b>"+edgeInformation.sourceNode.nodeName + " ("+edgeInformation.sourceNode.id+")</b>";
    		infoDetails.href = edgeInformation.sourceNode.collabUrl;
    		var infoDetails = document.getElementById("collab2");
    		infoDetails.innerHTML = "<b>"+edgeInformation.targetNode.nodeName + " ("+edgeInformation.targetNode.id+")</b>";
    		infoDetails.href = edgeInformation.targetNode.collabUrl;

		//set the edge weight
			infoDetails = document.getElementById("noOfCollab");
    		infoDetails.innerHTML = "<br><b>Number of collaborations: </b>" + edgeInformation.value;

    }
    
    //quick function to check if an array contains a property	
	function contains(a, obj){
		for(var i = 0; i < a.length; i++) {
    		if(a[i] == obj){
		      return true;
    		}
  		}
  		return false;
	}