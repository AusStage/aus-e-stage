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


function visControllerTimeline(newModel) {
		
		 this.model = newModel; 
 
		/*
		* Called when the results are updated     
		*/ 	
			
		this.refresh = function (newResults)
		{
			window.console.log('about to refresh the controller for this');
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
		}
		
		
		
		/*
		* BuildView the timeline view with the current results.    
		*/
		
		this.BuildView  = function (newResults)
		{	
			
			results = this.model.results;
			for (var a = 0; a < results.length; a++) { //loop over the controllers 
				
				//Show the performance name etc 
				
				 $("span.event").html(results[a].event);
          		 $("span.organisation").html(results[a].organisation);
                 $(".venue").html(results[a].venue);
         		 $(".date").html(results[a].date);
				 $("span.question").html(results[a].question);			  
			  
				for(var i = 0; i < results[a].feedback.length; i++) {
					item = results[a].feedback[i];	
					//Make the update acutally hoppen
					//window.console.log(item);
					$("#table_anchor").append('<tr><td class="feedback">' +	item.id + ' ' + item.content + '</td><td class="date">' + item.date  + '</td><td class="time">' + item.time + '</td><td class="type">' + item.type + '</td></tr>');
				}
				
			}	
							
			 
		}	

		
		/*
		* Refresh the timeline view with the current results.    
		*/
		
		this.refreshView  = function (newResults)
		{	
			for (var a = 0; a < newResults.length; a++) { //loop over the controllers 
				
				//Show the performance name etc 
										
					item = newResults[a];	
					//Make the update acutally hoppen
					$("#table_anchor").prepend('<tr><td class="feedback">' +	item.id + ' ' + item.content + '</td><td class="date">' + item.date  + '</td><td class="time">' + item.time + '</td><td class="type">' + item.type + '</td></tr>');
					//window.console.log(item);
			}	
							
			 
		}	
		
		

		
} 



