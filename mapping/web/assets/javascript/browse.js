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
var mapData = null;
var markerCluster = null;

// define some constants
var MAX_ZOOM_LEVEL = 10;
var MIN_EVENT_COUNT_FOR_TIMELINE = 10;
var INFO_WINDOW_WIDTH = 580;
var INFO_WINDOW_HEIGHT = 400;
var TIME_SLIDER_LABELS = 14;

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

	// get the marker xml data
	$.get("/mapping/browse?action=markers", null, function(data) {
	
		// build the initial map
		showMap(data);
		
		// build the time slider
		buildTimeSlider(data)
		
		// store reference to the map data
		mapData = data;
		
		// override the advanced display options form with our own function
		$("#reload_map").click(advFormSubmit);
		
		// close the loading dialog
		$("#map_loading").dialog("close");

	}, "xml");
});

// function to show the map
function showMap(data, focus, start, finish) {

	// tidy up and remove any references to any existing map
	GUnload();

	// init the Google Maps library
	map = new GMap2(document.getElementById("map"));
	
	switch(focus){
		case '1':
			//map.setCenter(new GLatLng(-30.058333, 135.763333), 6); //SA
			map.setCenter(new GLatLng(-32, 135.763333), 6); //SA
			break;
		case '2':
			map.setCenter(new GLatLng(-25.328055, 122.298333), 5); //WA
			break;
		case '3':
			map.setCenter(new GLatLng(-32.163333, 147.016666), 6); //NSW
			break;
		case '4':
			map.setCenter(new GLatLng(-22.486944, 144.431666), 5); //QLD
			break;
		case '5':
			map.setCenter(new GLatLng(-42.021388, 146.593333), 7); //TAS
			break;
		case '6':
			map.setCenter(new GLatLng(-36.854166, 144.281111), 6); //VIC
			break;
		case '7':
			map.setCenter(new GLatLng(-35.49, 149.001388), 9); //ACT
			break;
		case '8':
			map.setCenter(new GLatLng(-19.383333, 133.357777), 6); //NT
			break;
		case '9':
			map.setCenter(new GLatLng(-25.947028, 133.209639), 2); //outside Aus
			break;
		case 'a':
			map.setCenter(new GLatLng(-25.947028, 133.209639), 4); // Aus only
			break;
		default:
			map.setCenter(new GLatLng(-25.947028, 133.209639), 4); // defult, AUS
			break;
	}
	
	// finish setting up the map
	map.setUIToDefault();
	
	// extract the markers from the xml
	var markers = data.documentElement.getElementsByTagName("marker");
	
	// object to hold marker locations
	var locations = {};
	
	// object to hold built markers
	var markersToMap = [];
	
	// build a group of markers
	for (var i = 0; i < markers.length; i++) {
		
		// filter markers
		var okToAdd = false;
	
		// filter markers by date
		if(start != null) {
			// filter using date
			var fDate = parseInt(markers[i].getAttribute("fyear"));
			var lDate = parseInt(markers[i].getAttribute("lyear"));
		
			// check on the fdate
			if((fDate >= start && fDate <= finish) || (lDate >= start && lDate <= finish)) {
				// filter markers by state
				if(focus != null && focus != "nolimit") {
					// use the state to filter
					var state = markers[i].getAttribute("state")
		
					if(focus == "a" && state != "9") {
						okToAdd = true;
					}else if(focus == "1" && state == "1") {
						okToAdd = true;
					}else if(focus == "2" && state == "2") {
						okToAdd = true;
					}else if(focus == "3" && state == "3") {
						okToAdd = true;
					}else if(focus == "4" && state == "4") {
						okToAdd = true;
					}else if(focus == "5" && state == "5") {
						okToAdd = true;
					}else if(focus == "6" && state == "6") {
						okToAdd = true;
					}else if(focus == "7" && state == "7") {
						okToAdd = true;
					}else if(focus == "8" && state == "8") {
						okToAdd = true;
					}else if(focus == "9" && state == "9") {
						okToAdd = true;
					}			
				} else {
					okToAdd = true;
				}
			}
		
		} else {
			// filter markers by state
			if(focus != null && focus != "nolimit") {
				// use the state to filter
				var state = markers[i].getAttribute("state")
		
				if(focus == "a" && state != "9") {
					okToAdd = true;
				}else if(focus == "1" && state == "1") {
					okToAdd = true;
				}else if(focus == "2" && state == "2") {
					okToAdd = true;
				}else if(focus == "3" && state == "3") {
					okToAdd = true;
				}else if(focus == "4" && state == "4") {
					okToAdd = true;
				}else if(focus == "5" && state == "5") {
					okToAdd = true;
				}else if(focus == "6" && state == "6") {
					okToAdd = true;
				}else if(focus == "7" && state == "7") {
					okToAdd = true;
				}else if(focus == "8" && state == "8") {
					okToAdd = true;
				}else if(focus == "9" && state == "9") {
					okToAdd = true;
				}			
			} else {
				okToAdd = true;
			}
		}
		
		if(okToAdd) {
	
			// build a hash of this location
			var lat = parseFloat(markers[i].getAttribute("lat"));
			var lng = parseFloat(markers[i].getAttribute("lng"));
			var latlngHash = (lat.toFixed(6) + "" + lng.toFixed(6));
			latlngHash     = latlngHash.replace(".","").replace(",", "").replace("-","");
		
			if(locations[latlngHash] == null) {
				// not seen this location before
				// add to hash
				locations[latlngHash] = true;
			} else {
				// have seen this location before
				// adjust the lat and lng
				var randomNumber = Math.floor(Math.random()*3) + 1;
				randomNumber = "0.000" + randomNumber;
				randomNumber = parseFloat(randomNumber);
			
				lat = lat + randomNumber;
				lng = lng + randomNumber;
			}
		
			// build a latlng object for this marker
			var latlng = new GLatLng(lat, lng);
		
			// get the colour of the icon
			var eventCount = parseInt(markers[i].getAttribute("cnt"));
			var colour;
	
			if(eventCount == 1) {
				colour = "#CCBAD7";
			}else if(eventCount < 6) {
				colour = "#9A7BAB";
			}else if(eventCount > 5 && eventCount < 16) {
				colour = "#7F649B";
			}else if(eventCount > 15 && eventCount < 31) {
				colour = "#69528E";
			} else {
				colour = "#4D3779";
			}
		
			// get the marker icon
			var newIcon = MapIconMaker.createMarkerIcon({width: 32, height: 32, primaryColor: colour});
		
			// get the venue name
			var venueName = markers[i].getAttribute("name");
		
			// get the first and last years
			var fyear = markers[i].getAttribute("fyear");
			var lyear = markers[i].getAttribute("lyear");
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
		
			// make a new marker
			var marker = new GMarker(latlng, {icon: newIcon, title: venueName});
		
			// build the url for more information
			var url = "/mapping/browse?action=lookup&id=" + markers[i].getAttribute("id") +"&lyear=" + lyear;
				
			// add an event listener to listen for the click on a marker
			if(eventCount < MIN_EVENT_COUNT_FOR_TIMELINE + 1) {
				// get a function to respond to the click
				var fn = markerClickNoTabs(url, latlng);
				GEvent.addListener(marker, "click", fn);
			
			} else {
				// get a function to respond to the click
				var fn = markerClickWithTabs(url, latlng);
				GEvent.addListener(marker, "click", fn);
			}
		
			// add the marker to the list of markers
			markersToMap.push(marker);
		}		
	}
	
	// define options for marker cluster library
	var markerClusterOptions = {gridSize: 50, maxZoom: MAX_ZOOM_LEVEL};
	
	// build the map
	markerCluster = new MarkerClusterer(map, markersToMap, markerClusterOptions);
	
	// scroll to the map
	$.scrollTo("#map");
	
}

// function to respond to the click on a marker
function markerClickWithTabs(url, latlng) {
	return function() {
	
		// get the dates from the time slider
		var startDate  = $("#event_start").val();
		var finishDate = $("#event_finish").val();
		
		url = url + "&start=" + startDate + "&finish=" + finishDate;
		
		// open an info window and dynamically pull in the information
		$.get(url, function(html) {
			// define an array to hold the tabs
			var content = [];
			
			// get the id
			var id    = url.substr(url.indexOf("&id=") + 4, (url.indexOf("&lyear=") - url.indexOf("&id=")) - 4);
			var lyear = url.substr(url.indexOf("&lyear=") + 7, (url.indexOf("&start=") - url.indexOf("&lyear=") - 7));
			
			// define a tab to hold the event list
			content.push(new GInfoWindowTab("Event List", html));
			
			// get the address
			var address = html.substr(0, (html.indexOf("</p>") + 4));
			
			var timelineHtml =  address + "<div id=\"timeline_" + id + "_container\" style=\"height: 355px\">"
			//var timelineHtml =  address + "<div id=\"timeline_" + id + "_container\">"
			timelineHtml     += "<p>A timeline is a graphical representation of event data as a series of bars and points. Events are displayed in one of three formats:</p>";
			timelineHtml     += "<ul><li>A point is used to display an event that occured on a single day</li>";
			timelineHtml     += "<li>A pale blue bar indicates that the event occured at some time during the indicated time period</li>";
			timelineHtml     += "<li>A solid blue bar indicates that the event occured over the indicated time period</li></ul>";
			timelineHtml     += "<p>The timeline is automatically centred on the middle of the last year of events and lists all events that have occured at the venue</p>";
			timelineHtml     += "<p><a href=\"#\" onclick=\"loadTimeline('" + id + "','" + lyear + "'); return false;\">Load the Timeline by clicking this link.</a></p></div>";
			
			// define a tab to hold the timeline
			content.push(new GInfoWindowTab("Timeline", timelineHtml));
			
			// open the window
			map.openInfoWindowTabsHtml(latlng, content, {maxWidth: INFO_WINDOW_WIDTH, maxHeight: INFO_WINDOW_HEIGHT, autoScroll:true });
			
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
			map.openInfoWindowHtml(latlng, html, {maxWidth: INFO_WINDOW_WIDTH, maxHeight: INFO_WINDOW_HEIGHT, autoScroll:true });
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
	
	// check to ensure map data is present
	if(mapData == null) {
		$("#map").empty();
		$("#map").append('<p style="text-align: center"><strong>Error: </strong>An error occured whilst loading markers, please start again.<br/>If the problem persists please contact the site administrator.</p>'); 
		return false;
	}

	// get the start date
	var startDate  = $("#event_start").val();
	var finishDate = $("#event_finish").val();
	
	// empty the marker cluster object
	markerCluster.clearMarkers()
	
	// reload the map with trajectory information
	showMap(mapData, $("#state").val(), startDate, finishDate);
}

// function to build the time slider
function buildTimeSlider(data) {

	// build the time slider
	// get the first date and last date of the slider
	var element = data.getElementsByTagName("lastdate");
	var lastDate = element[0].getAttribute("value");
	lastDate = parseInt(lastDate)
	
	var element = data.getElementsByTagName("firstdate");
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
	
	// clear the time slider
	$("#event_start").removeOption(/./);
	$("#event_finish").removeOption(/./);
	
	// add the new options
	$("#event_start").addOption(dates, false);
	$("#event_finish").addOption(dates, false);
	
	// clear any current selected values
	$("#event_start").selectOptions('clear');
	$("#event_finish").selectOptions('clear');
	
	// sort the options
	$("#event_start").sortOptions();
	$("#event_finish").sortOptions();
	
	// select the last and first options before building the time slider
	$("#event_start option:first").attr("selected", "selected");
	$("#event_finish option:last").attr("selected", "selected");
	
	// remove any existing slider
	$("#sliderComponent").remove();
	
	// create the slider
	$(".slider").selectToUISlider({labels: TIME_SLIDER_LABELS}).hide();
	$(".tohide").hide();
	
	addSliderDescription();
}

// add a description to the slider
function addSliderDescription() {
	// add some descriptive text
	//$("#sliderComponent").append('<p style="text-align: center;">Use the above time slider to select a date range.<br/>Only venues where all events fall outside the selected date range will be removed.</p>');
	//$("#sliderComponent").append('<p style="text-align: center;">Use the above time slider to select a date range.</p>');
}

// Make Google API Scripts clean up
$(document).unload(function(){
	GUnload();
});
