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
		
		  this.UPDATE_DELAY = 5000;

		 this.results = new Array();
		 this.controllers = new Array(); 
		 this.errorController = new errorController();
		 this.errorStatus = 0;  
		 this.lastFeedbackID = 0;   
		 
		 this.feedback_count = 0;
		 this.last_feedback_count = 0; 
		 

		/*
		* Get the URL var and takes the user to the current form for that performance.    
		* 
		*/ 		
		this.startLoading = function ()
				{
				//alert('starting up');
				CurrentPerformance = this.getUrlVars();
				
				/*
				 * check on the value of the parameters. If they're "undefined" the URL didn't include what we require to continue
				 */
				if(typeof(CurrentPerformance ) == "undefined") {
					// execute a function to show an error message
					this.errorController.updateError("We need to know the ID of the performance to visualise. At the end of url add something like ?performance=46",0 );

				} else {
					//Load the actual performance 					
					this.getPerformanceData(CurrentPerformance);
				}
					
		}

		/*
		* Load the peformance data and send's that current controller refresh data function
		* 
		* @param  CurrentPerformance the current performane we are working thi   
		*/ 
				  
		this.getPerformanceData = function (CurrentPerformance) {
				
				
				//find out what server we are running on make sure the data type is right 
				host = this.buildHost();
			 	mydataType = this.getDataType(); 
				
				//alert(CurrentPerformance);
				
				//now build the url/calll  for API 
				
						 
				var source =  host + '/mobile/feedback?task=initial&performance=' + CurrentPerformance  ;  //TODO code to see what server the system is running on and build this URL 
				
				window.console.log(source);
				
				//The important bit more  
				current = this; // in the success function we normal scope this, so we put this in to current so called stuff in a few moments.
				$.ajax({
						type:   'GET',
						url: source, 
						dataType: 'jsonp',
						cache: false,
						success: function (data) {
							if(current.errorStatus 	== 1) {current.errorController.turnOffError ()};
							
							feedbackLength =  data.feedback.length -1; 
							///window.console.log(data.feedback[feedbackLength].id);
							current.lastFeedbackID = data.feedback[feedbackLength].id; 
							
							window.console.log(data);
							

							 data.feedback.reverse(); //maybe should do that in cotrol  
							 current.results.push(data);
							 current.buildControllers();
							//set up the loop that will start the automatic updating process every x seconds
							setTimeout("current.updatePerformanceData(CurrentPerformance)", current.UPDATE_DELAY);
						},
					
					error:function (xhr, ajaxOptions, thrownError){
                    	//alert(xhr.status); 
                    	//alert(thrownError);
						current.errorController.updateError("Couldn't load that performance",xhr.status);
						current.errorStatus = 1; 
                	},	
				
					async: true 
					});
					
		};		
		
		
		/*
		* Updates current peformance data and send's that current controller refresh data function
		* 
		* @param  CurrentPerformance the current performane we are working thi   
		*/ 
				  
		this.updatePerformanceData = function (CurrentPerformance) {
				
				
				//find out what server we are running on make sure the data type is right 
				host = this.buildHost();
			 	mydataType = this.getDataType(); 
				
				//this.lastFeedbackID = 95; //for testin so we don't need to keep on writing back the server. 
								
				var source =  host + '/mobile/feedback?task=update&performance=' + CurrentPerformance + '&lastid='  + this.lastFeedbackID   ;  
				
				window.console.log(source);
				
				//The important bit more  
				current = this; // in the success function we normal scope this, so we put this in to current so called stuff in a few moments.
				$.ajax({
						type:   'GET',
						url: source, 
						dataType: 'jsonp',
						cache: false,
						success: function (data) {
						
							if(current.errorStatus 	== 1) {current.errorController.turnOffError ()};
															
							//window.console.log(data);
							if(data.length != 0) {
								 //alert('got new data');
								 window.console.log('got new data');
								 
								 for (var a = 0; a < data.length; a++) { //loop over each bit of the array 
								  current.results[0].feedback.push(data[a]);// TODO - this will need to change because th  
								 }
								 
								 //window.console.log(current.results);
								 //data.feedback.reverse(); //maybe should do that in cotrol  
								 current.refreshControllers(data);
								//set up the loop that will start the automatic updating process every x seconds
								setTimeout("current.updatePerformanceData(CurrentPerformance)", current.UPDATE_DELAY);
								
							}
							
						},
					
					error:function (xhr, ajaxOptions, thrownError){
                    	//alert(xhr.status); 
                    	//alert(thrownError);
						current.errorController.updateError("Couldn't load that performance",xhr.status);
						current.errorStatus = 1; 
                	},	
				
					async: true 
					});
					
		};		



		/*
		* We have the new data so loop over the controllers and send them the results   
		* 
		*/ 		
		
		this.buildControllers = function ()
				{
					window.console.log('Building the controllers');
					//window.console.log(this.controllers);
					//this.controllers[0].refresh(this.results);
					//window.console.log(this.results);
					try { //inside a try because we might not controllers  
							for (var i = 0; i < this.controllers.length; i++) { //loop over the controllers 
								this.controllers[i].build(this.results);
							}	
					}
					catch (err) {
						//alert('no controllers found - or there is an error in ');
					}
		}
		
		
		/*
		* Because we have new data we loop over the controllers and send them the results   
		* 
		*/ 		
		
		this.refreshControllers = function (data)
				{
					window.console.log('refreshing the controllers');
					//window.console.log(this.controllers);
					//this.controllers[0].refresh(this.results);
					
					window.console.log(this.results);
					
					try { //inside a try because we might not controllers  
							for (var i = 0; i < this.controllers.length; i++) { //loop over the controllers 
								this.controllers[i].refresh(data);
							}	
					}
					catch (err) {
						//alert('no controllers found - or there is an error in ');
					}
		}
		
		
							
		/*
		* Get the URL var and takes the user to the current form for that performance.    
		* 
		*/ 		
		this.getUrlVars = function ()
				{
				
				//Slice the url varibles up 	
				var vars = [], hash;
				var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
				for(var i = 0; i < hashes.length; i++)
				{
					hash = hashes[i].split('=');
					vars.push(hash[0]);
					vars[hash[0]] = hash[1];
				}
				//alert(vars['performance']);

				return vars['performance'];
					
		}

	
		/* -------- UTILITY FUNCTIONS --------------- */
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



