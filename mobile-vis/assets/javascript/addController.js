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


function  addController(newModel) {
		
		 this.model = newModel;
                 this.errorController = new errorController();

		
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
				
		}
		
				
		/*
		* Build the addview  view with the current results.
		*/
		this.BuildView  = function (newResults)
		{	
			
		    results = this.model.results						

                    if(results.length == 1){

                        //Show the performance name etc for just on peformance  - just is the most common way of doing this

                             $("span.CurrentPerformance").html(results[0].id);
                        }


			for (var a = 0; a < results.length; a++) { //loop over the controllers 
				
				//Show the performance name etc 
				 //$('.theQuestion').html(results[a].question);
				 //$(".tag").html(results[a].tag);

									
				for(var i = 0; i < results[a].feedback.length; i++) {
					item = results[a].feedback[i];	
					//Make the update acutally hoppen
					//window.console.log(item);
					
				    //$(".feedback_messages").append('<span class="feedback"><div class="content">' + item.content + '</div><span class="feedback-about"><span class="date">' + item.date  + '</span><span class="time">' + item.time + ' </span><span class="type">' + item.type + ' </span></span></span>');
									
				}
				
			}	
							
		}	
		
		/*
		* Refresh the signage view with the current results.
		* There is a more elegant whay to do this. 
		*/
		this.refreshView  = function (newResults,newPerformances)
		{	

                window.console.log('In the signage view refresh');

                 

	

                }

               /**
		* Takes data objects and converts it to yyyy-mm-dd string
		* @param  date  is he data object that is going be converted
		* @returns returns the date in yyyy-mm-dd form
		*/

		 this.reFormatDatetoYYYYMMDD = function  (date){

			var year = date.getFullYear();
			var month = date.getMonth() +1;//correct for Jan being 00
			//Reformat the month if it's be 10
			if(month < 10) {
				var month  =  '0' + month;
				};


			var day = date.getDate();
			//Reformat the  day if it's less than 10
			if(day < 10) {
				var day =  '0' + day;
			};

			var dataString =  '' + year + '-' + month + '-' + day;
			return dataString;
		}




		/**
		* Called form the submit button.  Reads the information form the current form submits and the reloads the page.
		*/

		this.submitForm  = function  (e){

                          e.preventDefault();

                          //window.console.log("about to submit the form");

                                //do some simple validation
                                var message =  $('[name=message]').val();

				//get the current peformance from the form.
				gCurrentPerformance = $(".CurrentPerformance").html();

                                if(message == ''){
                                         //alert('feedback has be included')
                                         this.errorController.updateMessage("Please add some feedback");
                                         return;
                                } else {
                                          this.errorController.turnOffError();
                                }


				$('#feedback_messages').hide();//turn off the feedback
                                $('.feedback').addClass("loading");//add the loading screen
                                //
				//figure out which server we one

				host = this.model.buildHost();
			        var mydataType = this.model.getDataType();


                                
                                var params = {

					 type:'mobile-web',
					 performance:gCurrentPerformance,
					 message:message

				 };
				 var values = jQuery.param(params);//encode those

				 var source =  host + '/mobile/gatherer?' +  values;

				window.console.log(source);
                                var current = this;
                                
				//remove what is currently is being shown

				$.ajax({
						type:   'GET',
						url: source,
						cache: false,
						dataType: mydataType,
						success: function (data) {
							//window.console.log(data);
							$(".feedbackForm").empty();

                                                        $("#table_anchor").empty();

							 data.feedback.reverse();
							// add the list of feedback
							for(var i = 0; i < data.feedback.length; i++) {
								item = data.feedback[i];
								//window.console.log(item.content);

							         $("#table_anchor").append('<tr><td class="feedback">' + item.content + '</td><td class="date">' + item.date  + '</td><td class="time">' + item.time + '</td><td class="type">' + item.type + '</td></tr>');

							}
                                                         current.errorController.turnOffError();

                                                         $('#feedback_messages').show();//turn on the feedback
                                                         $('.feedback').removeClass("loading");//take the loading screen
                                                        //current.errorController.updateMessage("Feedback added ");



						},
						error:function (xhr, ajaxOptions, thrownError){
							alert('the error');
                                                        current.errorController.updateError("Something has gone wrong with the system",error);
							current.errorStatus = 1;


                	},

						async: true
			});
                        
		}


                

         
				
		

             	
		
		
				


		



		
} 



