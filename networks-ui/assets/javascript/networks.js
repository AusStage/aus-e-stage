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
	$("button, input:submit").button();
	
	//style the search results accordions - different to legend accordions- legend accordions have been defined by me to be able 
	//to use anchor links in the header
	 $(".accordion").accordion({collapsible:true, active:false, autoHeight: false });

	//initialise the search class.
	 searchObj = new SearchClass();
	 searchObj.init();
	 
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

});

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
			//navigate to the viewer
		  	alert('only static data is available for event to event netwoks at present')
		  	$('#tabs').tabs('select', 1);	
			viewerControl.displayNetwork('EVENT', viewerControl.selectedEvent.id[0], 1);
			break;
	}	
}
