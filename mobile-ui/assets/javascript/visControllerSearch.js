/*
 * This file is part of the AusStage Mobile Service
 *
 * The AusStage Mobile Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Mobile Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mobile Service.  
 * If not, see <http://www.gnu.org/licenses/>.
 */


function visControllerSearch(newModel) {
		
		 this.model = newModel; 

		/*
		* Called when the results are updated     
		*/ 	
			
		this.refresh = function (newResults)
		{
			window.console.log('about to refresh the controller for this');
			//this.results = newResults; 
			this.UpdateView(); // just do a simple redraw because in this case we not going to do much with data.
		}
		
		/*
		* Redraw  Update the timeline view with the current results.    
		*/
		this.UpdateView  = function (newResults)
		{	
			results = this.model.results;
			window.console.log(results );

			for (var a = 0; a < results.length; a++) { //loop over the controllers 
				for(var i = 0; i < results[a].length; i++) { //loop of over results in the 
				 	
					item  = results[a][i];
				 	 
					 window.console.log(item );
							
							    $("#table_anchor").after('<tr>' +
													'<td class="event">'  + item.event + '</td>' + 
													'<td class="organisation"> ' + item.organisation  + ' </td>' +
												    '<td class="startDateTime">' + item.startDateTime + '</td>' +			
													'<td class="venue">' + item.venue +' </td>' +
													'<td class="view"><a href="timeline.html?performance=' + item.id + '">Timeline</a>, ' +
													'<a href="signage.html?performance=' + item.id + '">Signage</a> ' +
													'</td>' +
													'</tr>'
													);
													
												
												
				}
					
			}	
							
			 
		}	



		
} 



