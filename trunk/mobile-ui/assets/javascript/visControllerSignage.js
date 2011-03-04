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

// define global variables


function  visControllerSignage(newModel) {
		
		 this.model = newModel; 

		/*
		* Called when the results are updated     
		*/ 	
			
		this.refresh = function (newResults)
		{
			window.console.log('about to refresh the controller for signage' );
			//this.results = newResults; 
			this.refreshView(newResults); // just do a simple redraw because in this case we not going to do much with data.

		}
		
		
		/*
		* Called when the results are first are found.   
		*/ 	
			
		this.build = function (newResults)
		{
			window.console.log('about to buill the controller for this');
			//this.results = newResults; 
			this.BuildView(); // just do a simple redraw because in this case we not going to do much with data.
			//For more more information about the slide plug that is used see http://slidesjs.com/
					$('#feedback').slides({
						container: 'feedback_messages',
						preload: true,
						preloadImage: 'img/loading.gif', //TODO - add the correct image
						play: 3000,
						pause: 5000,
						effect: 'fade',
						hoverPause: true,
						pagination: false,
						generatePagination: false
		
					});		
					
					
					}
		
				
		/*
		* Build the timeline view with the current results.    
		*/
		this.BuildView  = function (newResults)
		{	
			results = this.model.results;
			for (var a = 0; a < results.length; a++) { //loop over the controllers 
				
				//Show the performance name etc 
					$('.theQuestion').html(results[a].question);
									
				for(var i = 0; i < results[a].feedback.length; i++) {
					item = results[a].feedback[i];	
					//Make the update acutally hoppen
					//window.console.log(item);
					
				    $(".feedback_messages").append('<span class="feedback"><div class="content">' + item.content + '</div><span class="feedback-about"><span class="date">' + item.date  + '</span><span class="time">' + item.time + ' </span><span class="type">' + item.type + ' </span></span></span>');
									
									
									
				}
				
			}	
							
			 
		}	
		
		/*
		* Refresh the timeline view with the current results.    
		*/
		this.refreshView  = function (newResults)
		{	
		
		for (var a = 0; a < newResults.length; a++) { //loop over the controllers 
				
					item = newResults[a];	
				    $(".feedback_messages").prepend('<span class="feedback"><div class="content">' + item.content + '</div><span class="feedback-about"><span class="date">' + item.date  + '</span><span class="time">' + item.time + ' </span><span class="type">' + item.type + ' </span></span></span>'); // BUG this is not working 
					//make slide show go back to the start
 														
				
			}	
							
			 
		}	
		
		



		
} 



