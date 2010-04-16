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
 
// declare global variables
var map = null;
var points = [];
var loaded = true;

// load the initial map
$(document).ready(function(){

	// show a load message
	// set common options
	var options = {autoOpen: true, 
				   bgiframe: true, 
				   height: 240, 
				   width: 400, 
				   modal: true, 
				   title: "Loading Map Markers"
				  };
	
	// setup the dialogs
	$("#map_loading").dialog(options);

	// declare helper variables
	var markerUrl = "/mapping/browse?action=markers";
	var markers = [];
	
	// create a new map and centre it on australia
	map = new GMap2(document.getElementById("map"));
	map.setCenter(new GLatLng(-25.947028, 133.209639), 4);
	map.setUIToDefault();
	
	// get the marker data
    GDownloadUrl(markerUrl, function(data) {
		
		// store list of coord hashes
		var coords = [];
    
    	// get the xml data
    	var xml = GXml.parse(data);
    	
    	// extract the markers from the xml
    	points = xml.documentElement.getElementsByTagName("marker");
    	
    	// detect an error
    	if(points.length == 0) {
    		// close the loading dialog
			$("#map_loading").dialog("close");
			$("#map_loading").empty();
			$("#map_loading").append("<p><strong>Error: </strong>An error has occured while loading markers. Please try again and if the problem persists please contact the system administrator</p>");
			$("#map_loading").dialog("open");
			loaded = false;
		}
    	
    	// build a group of markers
    	for (var i = 0; i < points.length; i++) {
    	
    		// get the coordinates
    		var hash = points[i].getAttribute("lat") + points[i].getAttribute("lng");
    		hash = hash.replace(".","").replace(",", "").replace("-","");
    		
    		// check to see if we've seen this hash before
    		if(coords[hash] == null) {
    			// get coordinate object
	    		var latlng = new GLatLng(parseFloat(points[i].getAttribute("lat")), parseFloat(points[i].getAttribute("lng")));
	    		
	    		// store an indicator that we've seen this point before
	    		coords[hash] = 1;
	    	} else {
	    		
	    		// add some randomness to this point
	    		var lat = parseFloat(points[i].getAttribute("lat")) + (Math.random() -.5) / 15000;
	    		var lng = parseFloat(points[i].getAttribute("lng")) + (Math.random() -.5) / 15000;
	    		
	    		// get the coordinate object
	    		var latlng = new GLatLng(lat.toFixed(6), lng.toFixed(6));
	    	}
    		
    		// determine the appropriate colour of the icon
    		var count = points[i].getAttribute("cnt");
    		var iconColour;
    		
    		if(count == 1) {
    			iconColour = "#CCBAD7";
    		} else if(count > 1 && count < 6) {
    			iconColour = "#9A7BAB";
    		} else if(count > 5 && count < 16) {
    			iconColour = "#7F649B";
    		} else if(count > 15 && count < 31) {
    			iconColour = "#69528E";
    		} else {
    			iconColour = "#4D3779";
    		}
    		
    		// get an icon
			var newIcon = MapIconMaker.createMarkerIcon({width: 32, height: 32, primaryColor: iconColour});
			
			// get the venue name
			var venueName = points[i].getAttribute("name");
			
			// get the first and last years
			var fyear = points[i].getAttribute("fyear");
			var lyear = points[i].getAttribute("lyear");
			var myear = null;
			
			// add years to the tooltip including the venue name
			// also determine the middle year
			if(fyear != lyear) {
				venueName = venueName + " (" + fyear + " - " + lyear + ")";
				myear = Math.round((lyear - fyear) / 2) + parseInt(fyear);
			} else {
				venueName = venueName + " (" + fyear + ")";
				myear = fyear
			}
    		
    		// create the marker
    		var marker = new GMarker(latlng, {icon: newIcon, title: venueName});
    		
    		// build the url for the info
    		//var url = "/mapping/browse?action=lookup&id=" + points[i].getAttribute("id") +"&myear=" + myear;
    		var url = "/mapping/browse?action=lookup&id=" + points[i].getAttribute("id") +"&lyear=" + lyear;
    		
    		// add an event listener to listen for the click on a marker
    		if(count < 11) {
    			// get a function to respond to the click
	    		var fn = markerClickNoTabs(url, latlng);
    			GEvent.addListener(marker, "click", fn);
    			
    		} else {
    			// get a function to respond to the click
	    		var fn = markerClickWithTabs(url, latlng);
    			GEvent.addListener(marker, "click", fn);
    		}
    		   		
    		// add the marker to the list
    		map.addOverlay(marker);
   		}
   		
   		if(loaded == true) {
   		
	   		// get the first date and last date of the slider
	   		var element = xml.documentElement.getElementsByTagName("lastdate");
	   		var lastDate = element[0].getAttribute("value");
	   		lastDate = parseInt(lastDate)
	   		
	   		var element = xml.documentElement.getElementsByTagName("firstdate");
	   		var firstDate = element[0].getAttribute("value");
	   		firstDate = parseInt(firstDate);
	   		
	   		// calculate all of the years between the first and last date
	   		// and build an array
	   		lastDate = lastDate - firstDate;
	   		
	   		var dates = [];
	   		var date = firstDate;
	   		
	   		for(var i = 0; i <= lastDate; i++) {
	   			date++;
	   			dates[date] = date;
	   		}
	   		
	   		// add array of years to select boxes
	   		// adding array faster than adding options one by one
	   		$("#event_start").addOption(dates, false);
	   		$("#event_finish").addOption(dates, false);
			
			// clear any current selected values
			$("#event_start").selectOptions('clear');
			$("#event_finish").selectOptions('clear');
			
			$("#event_start option:first").attr("selected", "selected");
			$("#event_finish option:last").attr("selected", "selected");
			
			// remove any existing slider
			$("#sliderComponent").remove();
			
			// create the slider
			$(".slider").selectToUISlider({labels: 15}).hide();
			$(".tohide").hide();
			
			// override the advanced display options form with our own function
			$("#reload_map").click(advFormSubmit);
			
			// close the loading dialog
			$("#map_loading").dialog("close");
		}
		
   	});
});

// function to respond to the click on a marker
function markerClickWithTabs(url, latlng) {
	return function() {
		// download the event information
		GDownloadUrl(url, function(html) {
			// open an info window with the information
						
			// define an array to hold the tabs
			var content = [];
			
			// get the id
			//var id = url.substr(33);
			var id    = url.substr(url.indexOf("&id=") + 4, (url.indexOf("&lyear=") - url.indexOf("&id=")) - 4);
			var lyear = url.substr(url.indexOf("&lyear=") + 7);
			
			// define a tab to hold the event list
			content.push(new GInfoWindowTab("Event List", html));
			
			// get the address
			var address = html.substr(0, (html.indexOf("</p>") + 4));
			
			var timelineHtml =  address + "<div id=\"timeline_" + id + "_container\">"
			timelineHtml     += "<p>A timeline is a graphical representation of event data as a series of bars and points. Events are displayed in one of three formats:</p>";
			timelineHtml     += "<ul><li>A point is used to display an event that occured on a single day</li>";
			timelineHtml     += "<li>A pale blue bar indicates that the event occured at some time during the indicated time period</li>";
			timelineHtml     += "<li>A solid blue bar indicates that the event occured over the indicated time period</li></ul>";
			timelineHtml     += "<p>The timeline is automatically centred on the middle of the last year of events</p>";
			timelineHtml     += "<p><a href=\"#\" onclick=\"loadTimeline('" + id + "','" + lyear + "'); return false;\">Load the Timeline by clicking this link.</a></p></div>";
			
			// define a tab to hold the timeline
			content.push(new GInfoWindowTab("Timeline", timelineHtml));
			
			// open the window
			map.openInfoWindowTabsHtml(latlng, content, {maxWidth:450, maxHeight:400, autoScroll:true });
		});			
	}
}

// function to respond to the click on a marker
function markerClickNoTabs(url, latlng) {
	return function() {
		// download the event information
		GDownloadUrl(url, function(html) {
			// open an info window with the information
			
			// open the window
			map.openInfoWindowHtml(latlng, html, {maxWidth:450, maxHeight:400, autoScroll:true });
		});			
	}
}

// function to load a timeine
function loadTimeline(id, lyear) {

	// build the timeline event data url
	var eventUrl = "/mapping/browse?action=timeline&id=" + id;
	
	$("#timeline_" + id + "_container").empty();
	
	$("#timeline_" + id + "_container").append("<div id=\"timeline_" + id + "\" class=\"timeline-default\" style=\"height: 325px; border: 1px solid #aaa\"></div>");
	//$("#timeline_" + id + "_container").append("<p><strong>Please Note:</strong></p><ul><li>Timelines are automatically centered on a year between the first and last dates in the timline.</li><li>Those events shown with a pale blue bar occured at somepoint during the indicated time period.</li></ul>");

	// build the timeline			
	var eventSource = new Timeline.DefaultEventSource();
	
	var bandInfos = [
		Timeline.createBandInfo({
			eventSource:    eventSource,
			date:           "Jul 2 " + lyear + " 00:00:00 GMT",
			width:          "85%", 
			intervalUnit:   Timeline.DateTime.MONTH, 
			intervalPixels: 70
		}),
		Timeline.createBandInfo({
			overview: true,	
			eventSource:    eventSource,
			date:           "Jul 2 " + lyear + " 00:00:00 GMT",
			width:          "15%", 
			intervalUnit:   Timeline.DateTime.YEAR, 
			intervalPixels: 70
		})
	];
	
	// keep the bands in sync and highlight the summary band
	bandInfos[1].syncWith = 0;
	bandInfos[1].highlight = true;
	
	// override the default date formatter
	Timeline.DefaultEventSource.Event.prototype.fillTime = function(elmt, labeller) {
		var month = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
		var startDate = this._start;
		var endDate = this._end;
		var startString = startDate.getDate() + " " + month[startDate.getMonth()] + " " + startDate.getFullYear();
		var endString = endDate.getDate() + " " + month[endDate.getMonth()] + " " + endDate.getFullYear();
		if (this._instant) {
			if (this.isImprecise()) {
				elmt.appendChild(elmt.ownerDocument.createTextNode("Approximate Dates:"));
				elmt.appendChild(elmt.ownerDocument.createElement("br"));
				if(endString != "") {
					elmt.appendChild(elmt.ownerDocument.createTextNode(startString + " - " + endString));
				} else {
					elmt.appendChild(elmt.ownerDocument.createTextNode(startString));
				}
			} else {
				if(endString != "") {
					elmt.appendChild(elmt.ownerDocument.createTextNode(startString + " - " + endString));
				} else {
					elmt.appendChild(elmt.ownerDocument.createTextNode(startString));
				}
			}
		} else {
			if (this.isImprecise()) {
				elmt.appendChild(elmt.ownerDocument.createTextNode("Approximate Dates:"));
				elmt.appendChild(elmt.ownerDocument.createElement("br"));
				if(endString != "") {
					elmt.appendChild(elmt.ownerDocument.createTextNode(startString + " - " + endString));
				} else {
					elmt.appendChild(elmt.ownerDocument.createTextNode(startString));
				}
			} else {
				if(endString != "") {
					elmt.appendChild(elmt.ownerDocument.createTextNode(startString + " - " + endString));
				} else {
					elmt.appendChild(elmt.ownerDocument.createTextNode(startString));
				}
			}
		}
	}; 
	
	// change the way the info bubble displays
	//var oldFullInfoBubble = Timeline.DefaultEventSource.event.prototype.fillInfoBubble;
	Timeline.DefaultEventSource.Event.prototype.fillInfoBubble = function (elmt, theme, labeller) {
        var doc = elmt.ownerDocument;
        
        var title = this.getText();
        var link = this.getLink();
        var image = this.getImage();
        
        if (image != null) {
            var img = doc.createElement("img");
            img.src = image;
            
            theme.event.bubble.imageStyler(img);
            elmt.appendChild(img);
        }
        
        var divTitle = doc.createElement("div");
        var textTitle = doc.createTextNode(title);
        if (link != null) {
            var a = doc.createElement("a");
            a.href = link;
            a.target = "_blank";
            a.appendChild(textTitle);
            divTitle.appendChild(a);
        } else {
            divTitle.appendChild(textTitle);
        }
        theme.event.bubble.titleStyler(divTitle);
        elmt.appendChild(divTitle);
        
        var divBody = doc.createElement("div");
        this.fillDescription(divBody);
        theme.event.bubble.bodyStyler(divBody);
        elmt.appendChild(divBody);
        
        var divTime = doc.createElement("div");
        this.fillTime(divTime, labeller);
        theme.event.bubble.timeStyler(divTime);
        elmt.appendChild(divTime);
        
        /*
        var divWiki = doc.createElement("div");
        this.fillWikiInfo(divWiki);
        theme.event.bubble.wikiStyler(divWiki);
        elmt.appendChild(divWiki);	
        */
	};
	
	// add the timeline to the html
	var tl = Timeline.create(document.getElementById("timeline_" + id), bandInfos);

	// load the data into the timeline
	tl.loadXML(eventUrl, function(xml, url) { eventSource.loadXML(xml, url); });
	
	return false;
}

// function to start the process of reloading the map
function advFormSubmit() {

	// show the loading dialog
	$("#map_loading").dialog("open");
	
	// delay the start of the reloadMap function to let the window redraw
	setTimeout("reloadMap()",500);

}
// function to reload the map
function reloadMap() {

	// clear any existing overlays
	map.clearOverlays();

	// declare helper variables
	// store the created placemarks
	var markers = [];
	
	// store list of coord hashes
	var coords = [];

	// get the first date and last dates from the select boxes
	var minDate = $("#event_start").val();
	var maxDate  = $("#event_finish").val();
	
	// get the limiter
	var limit = $("#limit").val();
	
	// build a list of points
	for (var i = 0; i < points.length; i++) {
	
		// get the first and last years
		var firstYear = points[i].getAttribute("fyear");
		var lastYear  = points[i].getAttribute("lyear");
		
		// get the state
		var state = points[i].getAttribute("state");
		var include = true;
		
		if(limit != "nolimit") {
			// set the include flag
			include = false;
			
			// limiting by geographic region
			// update the include flag accordingly
			switch(limit) {
				case '1':
					if(state == '1') {
						include = true;
					}
					break;
				case '2':
					if(state == '2') {
						include = true;
					}
					break;
				case '3':
					if(state == '3') {
						include = true;
					}
					break;
				case '4':
					if(state == '4') {
						include = true;
					}
					break;
				case '5':
					if(state == '5') {
						include = true;
					}
					break;
				case '6':
					if(state == '6') {
						include = true;
					}
					break;
				case '7':
					if(state == '7') {
						include = true;
					}
					break;
				case '8':
					if(state == '8') {
						include = true;
					}
					break;
				case '9':
					if(state == '9') {
						include = true;
					}
					break;
				case 'a':
					//all australia
					if(state >= 1 && state <= 8) {
						include = true;
					}
					break;
				default:
					include = false;
					break;
				}
		}

		if(((firstYear >= minDate && firstYear <= maxDate) || (lastYear >= minDate && lastYear <= maxDate)) && include == true) {
		
			// get the coordinates
    		var hash = points[i].getAttribute("lat") + points[i].getAttribute("lng");
    		hash = hash.replace(".","").replace(",", "").replace("-","");
    		
    		// check to see if we've seen this hash before
    		if(coords[hash] == null) {
    			// get coordinate object
	    		var latlng = new GLatLng(parseFloat(points[i].getAttribute("lat")), parseFloat(points[i].getAttribute("lng")));
	    		
	    		// store an indicator that we've seen this point before
	    		coords[hash] = 1;
	    	} else {
	    		
	    		// add some randomness to this point
	    		var lat = parseFloat(points[i].getAttribute("lat")) + (Math.random() -.5) / 15000;
	    		var lng = parseFloat(points[i].getAttribute("lng")) + (Math.random() -.5) / 15000;
	    		
	    		// get the coordinate object
	    		var latlng = new GLatLng(lat.toFixed(6), lng.toFixed(6));
	    	}
    		
    		// determine the appropriate colour of the icon
    		var count = points[i].getAttribute("cnt");
    		var iconColour;
    		
    		if(count == 1) {
    			iconColour = "#CCBAD7";
    		} else if(count > 1 && count < 6) {
    			iconColour = "#9A7BAB";
    		} else if(count > 5 && count < 16) {
    			iconColour = "#7F649B";
    		} else if(count > 15 && count < 31) {
    			iconColour = "#69528E";
    		} else {
    			iconColour = "#4D3779";
    		}
    		
    		// get an icon
			var newIcon = MapIconMaker.createMarkerIcon({width: 32, height: 32, primaryColor: iconColour});
			
			// get the venue name
			var venueName = points[i].getAttribute("name");
			
			// get the first and last years
			var fyear = points[i].getAttribute("fyear");
			var lyear = points[i].getAttribute("lyear");
			var myear = null;
			
			// add years to the tooltip including the venue name
			// also determine the middle year
			if(fyear != lyear) {
				venueName = venueName + " (" + fyear + " - " + lyear + ")";
				myear = Math.round((lyear - fyear) / 2) + parseInt(fyear);
			} else {
				venueName = venueName + " (" + fyear + ")";
				myear = fyear
			}
    		
    		// create the marker
    		var marker = new GMarker(latlng, {icon: newIcon, title: venueName});
    		
    		// build the url for the info
    		var url = "/mapping/browse?action=lookup&id=" + points[i].getAttribute("id") +"&lyear=" + lyear;
    		
    		// add an event listener to listen for the click on a marker
    		if(count < 11) {
    			// get a function to respond to the click
	    		var fn = markerClickNoTabs(url, latlng);
    			GEvent.addListener(marker, "click", fn);
    			
    		} else {
    			// get a function to respond to the click
	    		var fn = markerClickWithTabs(url, latlng);
    			GEvent.addListener(marker, "click", fn);
    		}
    		   		
    		// add the marker to the list    		
    		map.addOverlay(marker);
		}	
	}
	
	//recentre the map
	switch(limit){
		case '1':
			//map.setCenter(new GLatLng(-30.058333, 135.763333), 6);
			map.setCenter(new GLatLng(-32, 135.763333), 6); //SA
			break;
		case '2':
			map.setCenter(new GLatLng(-25.328055, 122.298333), 5);
			break;
		case '3':
			map.setCenter(new GLatLng(-32.163333, 147.016666), 6);
			break;
		case '4':
			map.setCenter(new GLatLng(-22.486944, 144.431666), 5);
			break;
		case '5':
			map.setCenter(new GLatLng(-42.021388, 146.593333), 7);
			break;
		case '6':
			map.setCenter(new GLatLng(-36.854166, 144.281111), 6);
			break;
		case '7':
			map.setCenter(new GLatLng(-35.49, 149.001388), 9);
			break;
		case '8':
			map.setCenter(new GLatLng(-19.383333, 133.357777), 6);
			break;
		case '9':
			map.setCenter(new GLatLng(-25.947028, 133.209639), 2);
			break;
		case 'a':
			map.setCenter(new GLatLng(-25.947028, 133.209639), 4);
			break;
		default:
			map.setCenter(new GLatLng(-25.947028, 133.209639), 4);
			break;
	}
	
	// close the loading dialog
	$("#map_loading").dialog("close");
}

// Make Google API Scripts clean up
$(document).unload(function(){
	GUnload();
});
