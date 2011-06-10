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
		 this.errorController = new errorController();
		/*
		* Called when the results are updated     
		*/ 	
			
		this.refresh = function (newResults)
		{
			//window.console.log('about to refresh the controller for this');
			//this.results = newResults; 
			this.UpdateView(); // just do a simple redraw because in this case we not going to do much with data.
		}
		
		/*
		* Redraw  Update the view with the current results.    
		*/
		this.UpdateView  = function (newResults)
		{	
			results = this.model.results;
			//window.console.log(results );

			for (var a = 0; a < results.length; a++) { //loop over the controllers 
				for(var i = 0; i < results[a].length; i++) { //loop of over results in the 

                                        
					var item  = results[a][i];
				 	 
					 //window.console.log(item );
							
							    $("#table_anchor").after('<tr>' +
                                                                                                        '<td class="select"> <input type="checkbox"  name="performance" id="' + item.id + '" value="' + item.id + '" /> </td>' +
                                                                                                        '<td class="event">'  + item.event + '</td>' +
													'<td class="organisation"> ' + item.organisation  + ' </td>' +
												        '<td class="startDateTime">' + item.startDateTime + '</td>' +
													'<td class="venue">' + item.venue +' </td>' +
													'<td class="view">\n\
\n\
                                                                                                         <span class="visList"><a href="list.html?performance=' + item.id + '"><img src ="assets/images/list-icon.png" alt="list" > <br /><span class="label">List</span></a></span> ' +
													'<span class="visSign"><a href="signage.html?performance=' + item.id + '">\n\
                                                                                                          <img src ="assets/images/sign-icon.png" alt="Signage" > <br /><span class="label">Signage</span></a></span>' +

                                                                                                          '<span class="visCloud"><a href="tagcloud.html?performance=' + item.id + '">\n\
                                                                                                          <img src ="assets/images/tagcloud-icon.png" alt="Cloud" > <br /><span class="label">Tag Cloud</span></a></span>' +

                                                                                                         '<span class="visImage"><a href="image-sequence.html?performance=' + item.id + '">\n\
                                                                                                          <img src ="assets/images/imagesquence.png" alt="Image Sequence" > <br /><span class="label">Image Sequence</span></a></span>' +
													'</td>' +
													'<td class="totalFeedbackCount">' + item.totalFeedbackCount +' </td>' +

													'</tr>'
													);
													
												
												
				}
					
			}	
							

		}	


         

		
} 



