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



function model(name) {
		
		 this.results = new Array();
		 this.controllers = new Array(); 
		 this.errorController = new errorController();
		 this.errorStatus = 0;  

		/** 
	    * 
		*  
		*/

		this.getPerformances = function (start,end,target){
		   
		   	   //figure out which server this running on what the data type should be  
			   host = this.buildHost();
			   var mydataType = this.getDataType();
			   
			   //set up the date
			   //var startdate = new Date('Dec 12 2010'); // FOR TESTING
                            var startdate = new Date();

			   //enddate.setDate(startdate.getDate() + 10);
                           
			   startdate.setDate(startdate.getDate() - start);
			   ////window.console.log(startdate.toDateString());
                           //
                           //Now formatted the into yyyy-mm-dd

                           startdateString = reFormatDatetoYYYYMMDD(startdate);
			   ////window.console.log(startdateString);

                           
                            //var today = new Date('Dec 12 2010'); // FOR TESTING
                            var today = new Date();
                            var enddate = today;
                            
                            enddate.setDate(today.getDate() + end);

                           //Now formatted the into yyyy-mm-dd
                            enddateString = reFormatDatetoYYYYMMDD(enddate);
			    ////window.console.log(enddateString);
                            
                            if (end == 0 && start == 0 ) {				  // build the query string
                               //don't include the end date'
                               var params = {
                                             task:'date',
                                             startdate: startdateString,

                                     };
                             } else {
                               var params = {
                                             task:'date',
                                             startdate: startdateString,
                                             enddate: enddateString,


                                     };
                             }
				 
				 var values = jQuery.param(params);//encode thos
				
				var source =  host + '/mobile/lookup?' +  values;
				
				////window.console.log(source);
								
				$.ajax({
						type:   'GET',
						url: source, 
						dataType:  mydataType,
						cache: false,
						success: function (data) {
						////window.console.log(data);
						//see if we have some data only the display the current performances and load them  
						if (data.length !=  0) {
							//Sort by the startDateTime
							
							//data.reverse();
							
							//Take the loaiding class off and the display the block inside of that 	
							$(target).removeClass("loading");
							$(target).css("display","block");
							
								//Got the date and now load that into the current performances.  
								//Clear what is there current 
								$(target + ".Performances").empty();
		
									// add the list of feedback
									for(var i = 0; i < data.length; i++) {
										//window.console.log(data);
										var item = data[i];
										//$(target + ".Performances").append('<li class="arrow">' +
										$(target + ".Performances").append('<li class="arrow">' +
												'<a href="add.html?performance=' +
												item.id +
                                                                                                '" ><div class="event">' +
													item.event + '</div></a>' +
			
												'<span class="organisation"> ' + item.organisation  + ', </span>' +
												'<span class="venue">' + item.venue +',</span>' +
												'<span class="startDateTime">' + item.startDateTime + '</span>' +
                                                                                                '</li>');
									}
									
							} else {
								//Nothing found so we turn off the display
								//$(target).css("display","none");
								$(target).removeClass("loading");
								$(target + ".Performances").empty();

								$(target + ".Performances").append('<li>' +
											
												 '<div class="event">There are no performances currently seeking feedback' +
													'</div></li>');
											
											
								
							}
																	
						},
					//Some thing has gone wrong, show the user an error screen.  
					error:function (xhr, ajaxOptions, thrownError){

                	},
					async: true 
					}); 
					
		}

                this.searchPerformances = function () {
		   
		   	   //figure out which server this running on what the data type should be  
			  	host = this.buildHost();
			    var mydataType = this.getDataType(); 
			   
			   //set up the date 
			   var startdate = new Date('Jan 01 2010');
			  //var startdate = new Date();

			  // startdate.setDate( startdate.getDate() + start );
			   ////window.console.log(startdate.toDateString());
			
			   //Now formatted the into yyyy-mm-dd
			    startdateString = reFormatDatetoYYYYMMDD(startdate); 
				
			   var  enddate = new Date();
			   //enddate.setDate( startdate.getDate() + end);
			   
   				//Now formatted the into yyyy-mm-dd
			    enddateString = reFormatDatetoYYYYMMDD(enddate); 
				
							  // build the query string			    
			   var params = {
					 
					 task:'date',
					 startdate: startdateString,
					// enddate:   enddateString,

				 };
				 
				 var values = jQuery.param(params);//encode thos
				
				var source =  host + '/mobile/lookup?' +  values;

				////window.console.log(source);
				
				current = this; // in the success function we normal scope this, so we put this in to current so called stuff in a few moments.

				$.ajax({
						type:   'GET',
						url: source, 
						dataType:  mydataType,
						cache: false,
						success: function (data) {
							if(current.errorStatus 	== 1) {current.errorController.turnOffError ()};

                                                        data.reverse();
                                                        
							////window.console.log(data);
	 						current.results.push(data);
							current.refreshControllers();							//see if we have some data only the display the current performances and load them  
							
                                                        
							/*if (data.length !=  0) {
								//Sort by the startDateTime
								
								data.reverse(); 
								
								//Take the loaiding class off and the display the block inside of that 	
								
								$(target).removeClass("loading");
								$(target).css("display","block");
								
									//Got the date and now load that into the current performances.  
									//Clear what is there current 
									$(target + ".Performances").empty();
			
										// add the list of feedback
										for(var i = 0; i < data.length; i++) {
											////window.console.log(data);
											item = data[i];
											//$(target + ".Performances").append('<li class="arrow">' +
											$(target + ".Performances").append('<li class="arrow">' +
													'<a href="#FeedbackDisplayWithForm" ' + 
													 ' onClick="loadFeedbackForm(event)" ><div class="event">' +
														item.event + '</div>' + 
													'<span class="startDateTime">' + item.startDateTime + '</span><br / >' +			
													'<span class="organisation"> ' + item.organisation  + ', </span>' +
													'<span class="venue">' + item.venue +' </span>' +
													'<span class="performance">' +
														item.id +
												'</span></a></li>');
										}
										
								} else {
									//Nothing found so we turn off the display
									//$(target).css("display","none");
									$(target).removeClass("loading");
									$(target + ".Performances").empty();
	
									$(target + ".Performances").append('<li class="arrow">' +
													'<a href="#home" ' + 
													 '  ><div class="event"> No Performances Found' +
														'</div></a></li>');
												
												
									
								}*/
																		
							},
					//Some thing has gone wrong, show the user an error screen.  
					error:function (xhr, ajaxOptions, thrownError){
						
                    	//alert(xhr.status);
                    	//alert(thrownError);
						current.errorController.updateError("Something has gone wrong",xhr.status);
						current.errorStatus = 1; 
                	},
					async: true 
					}); 
					
		}
		
		/*
		* Because we have new data we loop over the controllers and send them the results   
		* 
		*/ 		
		
		this.refreshControllers = function ()
				{
					////window.console.log('refreshing the controllers');
					////window.console.log(this.controllers);
					//this.controllers[0].refresh(this.results);
					
					////window.console.log(this.results);
					
					try { //inside a try because we might not controllers  
							for (var i = 0; i < this.controllers.length; i++) { //loop over the controllers 
								this.controllers[i].refresh(this.results);
							}	
					}
					catch (err) {
						//alert('no controllers found - or there is an error in ');
					}
	}
	
	

		/* -------- UTILITY FUNCTIONS --------------- */
		
		
		/**
		* Takes data objects and converts it to yyyy-mm-dd string  
		* @param  date  is he data object that is going be converted  
		* @returns returns the date in yyyy-mm-dd form  
		*/	
			
		function reFormatDatetoYYYYMMDD (date){
			
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
		
		
		/*
		* Get the current host name from the location string   
		* @returns host as a string    
		*/ 
		this.buildHost = function () {
			
			 var host = $(location).attr('host');
			  if (host != 'http://beta.ausstage.edu.au')  { 
					  host = 'http://beta.ausstage.edu.au';				  
			  }
			  return host;
		}
		/*
		* See if the data types should be jsonp or json, it does his based on host. The only server that josn will work on is http://beta.ausstage.edu.au   
		* 
		* @returns data as a string, it will be json or json.     
		*/ 
		this.getDataType  = function () {
				
			 	var host = $(location).attr('host');
			   if (host != 'http://beta.ausstage.edu.au')  { 
					  return 'jsonp';
				  } else {
					  return 'json';
				  }
		
		}
				
		
		
} 



