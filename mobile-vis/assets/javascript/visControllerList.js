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


function visControllerList(newModel) {
		
		 this.model = newModel; 
 
		/*
		* Called when the results are updated     
		*/ 	
			
		this.refresh = function (newResults,newPerformances)
		{
			//window.console.log('About to refresh the controller for this');
			//this.results = newResults; 
			this.refreshView(newResults,newPerformances); // just do a simple redraw because in this case we not going to do much with data.
		}
		
		
		/*
		* Called when the results are first are found.   
		*/ 	
			
		this.build = function ()
		{
			//window.console.log('about to build the controller for this');
			//this.results = newResults; 
			this.BuildView(); // just do a simple redraw because in this case we not going to do much with data.
		}
		
		
		
		/*
		* BuildView the List view with the current results.
		*/
		
		this.BuildView  = function ()
		{	
			
			//results = this.model.results;
                        ////window.console.log(results.length);

                        //window.console.log(this.model.results);

                        results = this.model.results;

                        if(results.length == 1){
			
                        //Show the performance name etc for just on peformance  - just is the most common way of doing this
                            //window.console.log(results[0]);

                             $("span.event").html('<a href='+ results[0].eventUrl + '>' + results[0].event + '</a>');

                             $("span.organisation").html(results[0].organisation);
                             $(".venue").html(results[0].venue);
                             $(".date").html(results[0].date);
                             $("span.question").html(results[0].question);

				for(var i = 0; i < results[0].feedback.length; i++) {
					var item = results[0].feedback[i];
					//Make the update acutally hoppen
					////window.console.log(item);
					$("#table_anchor").append('<tr><td class="feedback">' + item.content + '</td><td class="date">' + item.date  + '</td><td class="time">' + item.time + '</td><td class="type">' + item.type + '</td></tr>');
				}

                        } else {
                              //add a new column the heading table heading 

                              $("table#feedback_messages thead tr.heading").prepend('<th scope="col">Event</th>');

                              // turn off the info area at the top
                               $(".info").hide();

                              for(var a = 0; a < results.length; a++) {
                                //now update for muliple.
                                for(var i = 0; i < results[a].feedback.length; i++) {
					item = results[a].feedback[i];
					//Make the update acutally hoppen
					////window.console.log(item);
					$("#table_anchor").append('<tr><td class="event">' + results[a].event + '</td><td class="feedback">' + item.content + '</td><td class="date">' + item.date  + '</td><td class="time">' + item.time + '</td><td class="type">' + item.type + '</td></tr>');
				}
                              }

                        }
							
			 
		}	

		
		/*
		* Refresh the List view with the current results.
		*/
		
		this.refreshView  = function (newResults)
		{	
                     ////window.console.log(newPerformance);
                      
                      if (newResults.length == 1 ) {// we only have one performance so don't need to worry about where that
                           $.each(newResults, function() {
                                    ////window.console.log('in the performances loop ');
                                    ////window.console.log(this);
                                     $.each(this, function() {					//Make the update acutally hoppen
                                                    //This works well if all the results are from the same peformaance
                                                    $("#table_anchor").prepend('<tr><td class="feedback">' + this.content + '</td><td class="date">' + this.date  + '</td><td class="time">' + this.time + '</td><td class="type">' + this.type + '</td></tr>');
                                                    ////window.console.log(item);
                                       });
                           });
                        } else {
                               //we have multiple results being refresh so have fit this to the first spot.
                               ////window.console.log('New Performance');
                              // //window.console.log(newPerformance);

                              for(var a = 0; a < newResults.length; a++) {
                                 //   //window.console.log('in the performances loop ');

                                   // //window.console.log(newPerformance[a].event);

                                    for(var i = 0; i < this.model.results.length; i++) {
                                               //look at the performance is on first piece of feedback in the ara 
                                               if (this.model.results[i].id == newResults[a][0].performanceID){
                                                         var performanceName = this.model.results[i].event;
                                                  }
                                     }
                                     
                                     for(var i = 0; i < newResults[a].length; i++) {

                                                   var item = newResults[a][i];//This works well if all the results are from the same peformaance
                                                   
                                                   //search for current performance in main model results
                                                   //for(var i = 0; i < this.model.results.length; i++) {
                                                            //  if (this.model.results[i].id == item.performanceID){
                                                              //  var performanceName = this.model.results[i].event;
                                                             // }
                                                     //}


                                                    $("#table_anchor").prepend('<tr><td class="event">'  + performanceName +   '</td><td class="feedback">' + item.content + '</td><td class="date">' + item.date  + '</td><td class="time">' + item.time + '</td><td class="type">' + item.type + '</td></tr>');
                                                    ////window.console.log(item);
                                       }
                            }
                        }
			 
		}	
		
		

		
} 



