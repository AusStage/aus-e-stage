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

		//Configure for the slide show 
		 this.fx =  'fade';  // choose your transition type, ex: fade, scrollUp, shuffle, etc...	
		 this.speed =  200;  
		 this.timeout =  400; 
						
						
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
			window.console.log('about to build the controller for this');
			//this.results = newResults; 
			this.BuildView(); // just do a simple redraw because in this case we not going to do much with data.
				
				//For more more information about the slide plug that is used see http://jquery.malsup.com/cycle/
						
				$('.feedback_messages').cycle({
					fx: this.fx, 
					speed: this.speed, 
					timeout: this.timeout, 	 	
				});	
							
				$('.feedback_messages').show();

		}
		
				
		/*
		* Build the timeline view with the current results.    
		*/
		this.BuildView  = function (newResults)
		{	
			
		    results = this.model.results						
							
									
			for (var a = 0; a < results.length; a++) { //loop over the controllers 
				
				//Show the performance name etc 
				 $('.theQuestion').html(results[a].question);
				  $(".tag").html(results[a].tag);

									
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
		* There is a more elegant whay to do this. 
		*/
		this.refreshView  = function (newResults)
		{	
		//$('.feedback_messages').hide();
		
		$('.feedback_messages').cycle(
				'destroy' 		
		);


               $.each(newResults, function() {
                        /window.console.log('in the performances loop ');
                        //window.console.log(this);
                         
                         $.each(this, function() {
                          //  window.console.log('in the feedback loop ');
                            //window.console.log(this.content);

                            $(".feedback_messages").prepend('<span class="feedback"><div class="content">' + this.content + '</div><span class="feedback-about"><span class="date">' + this.date  + '</span><span class="time">' + this.time + ' </span><span class="type">' +  this.type + ' </span></span></span>');
                          });
               });


        
         
				
		//REBUILD Add it back 
		$('.feedback_messages').cycle({
			fx: this.fx, 
			speed: this.speed, 
			timeout: this.timeout, 	 	
		});			 
		}	
		
		
				


		



		
} 



