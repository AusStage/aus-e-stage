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

//set up for the initial page
$(document).ready(function(){

	//setup the tabs
	$("#tabs").tabs();
	
	// style the buttons
	styleButtons();
	
	// associate tipsy with the span element
    $('.use-tipsy').tipsy({live: true})
	
	//style the search results accordions - different to legend accordions- legend accordions have been defined by me to be able 
	//to use anchor links in the header
	$(".accordion").accordion({collapsible:true, active:false, autoHeight: false });

	//initialise the search class.
	searchObj = new SearchClass();
	searchObj.init();
	// check to see if this is a persistent link search request
    searchObj.doSearchFromLink();
	 
	viewer = new ContributorViewerClass(); 

	//initialise th viewer class
	viewerControl = new ViewerControlClass();
	viewerControl.init();	
	  
	// setup the add search result buttons
	$('.addSearchResult').live('click', addResultsClick);

	//bind click event to the search tab to hide legend when clicked.
	$('#tabs').bind('tabsselect', function(event, ui) {
		if(ui.index == 0){
			viewer.hideInteraction();	
		}
		if(ui.index == 1){
			viewer.showInteraction();
		}
	
	 });
	 
	//set up the find centre button
	$('#find_centre').live('click', function(){viewer.recentre()})
	
	//prepare dialogs and associate them to buttons	
	prepareDialogs();	 
	
	//check the url to see if they are using a bookmark
 	checkUrl();       
 	
});

//checks the url for task -either ego-centric or event-centric and id
function checkUrl(){
	
	var task = getUrlVar('task');
	var id = getUrlVar('id');
	var radius_simplify = getUrlVar('rs');
	//if the id is a number
	if (!isNaN(id)){		
		$('#tabs').tabs('select', 1);
		switch(task){
			case 'ego-centric': 	
				viewerControl.displayNetwork('CONTRIBUTOR', id, 0);
				break;
			case 'event-centric':
				viewerControl.displayNetwork('EVENT', id, 0, radius_simplify) 
				break;				
		}
	}
}

function addResultsClick(event){

	$('#searchAddContributorError').empty()
	$('#searchAddEventError').empty()	
	switch (this.id){
		case 'viewContributorNetwork':
			if(viewerControl.selectedContributors.id.length == 0){
				$('#searchAddContributorError').append(buildInfoMsgBox(searchObj.NO_CONTRIBUTOR_SELECTED));	
			}
			else if(viewerControl.selectedContributors.id.length == 1){
				//navigate to the viewer
				$('#tabs').tabs('select', 1);	
				viewerControl.displayNetwork('CONTRIBUTOR', viewerControl.selectedContributors.id[0], 1);
			}
			else if(viewerControl.selectedContributors.id.length == 2){
				alert("Contributor path data not yet available for "+viewerControl.selectedContributors.name[0]+
					' and '+viewerControl.selectedContributors.name[1]);
			}	
			break;
		case 'viewEventNetwork':
			if (viewerControl.selectedEvent.id.length <1){
				$('#searchAddEventError').append(buildInfoMsgBox(searchObj.NO_EVENT_SELECTED));	
				break;
			}			
		  	$('#tabs').tabs('select', 1);	
			viewerControl.displayNetwork('EVENT', viewerControl.selectedEvent.id[0], 1, $('#eventDegree option:selected').val());
			break;
	}	
}

function prepareDialogs(){
	//set up the search help
	 $("#help_search_div").dialog({
     	autoOpen: false,
        height: 400,
        width: 450,
		modal: true,
		buttons: {
			Close: function() {
					$(this).dialog('close');
				}
    	    },
	        open: function() {

			},
			close: function() {

			}
        });

        $("#show_search_help").click(function() {
                $("#help_search_div").dialog('open');
        });


	//set up the view event help
	$("#help_view_event_div").dialog({
     	autoOpen: false,
        height: 450,
        width: 600,
		modal: true,
		buttons: {
			Close: function() {
					$(this).dialog('close');
				}
    	    },
	    	open: function() {

			},
			close: function() {
		}
    });

	$("#view_event_help").live('click', function() {
        $("#help_view_event_div").dialog('open');
    });

	//set up the search help
	$("#help_view_contributor_div").dialog({
     	autoOpen: false,
        height: 400,
        width: 450,
		modal: true,
		buttons: {
		Close: function() {
				$(this).dialog('close');
			}
        },
	        open: function() {

		},
			close: function() {
		}
	});

	$("#view_contributor_help").live('click', function() {
		$("#help_view_contributor_div").dialog('open');
    });
        
	//set up the about networks
	$("#about_networks_div").dialog({
     	autoOpen: false,
        height: 400,
        width: 450,
		modal: true,
		buttons: {
			Close: function() {
					$(this).dialog('close');
				}
    	    },
	        open: function() {

			},
			close: function() {

			}
        });
        
    $("#about_networks_help").click(function() {
    	$("#about_networks_div").dialog('open');
	});


	//set up the about networks
	 $("#network_bookmark_div").dialog({
     	autoOpen: false,
        height: 400,
        width: 450,
		modal: true,
		buttons: {
			Close: function() {
					$(this).dialog('close');
				}
    	    },
	        open: function() {

			},
			close: function() {

			}
        });

    $("#bookmarkLink").click(function() {
    	$("#network_bookmark_div").dialog('open');
	});

	
}
