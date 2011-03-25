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


function visControllerChooseView(doc) {
		
		this.doc = doc;

//                $('form#performanceForm').submit(function(){
//                    alert('here');
//                });


                // add a click method to buttons to see what view has been choose
                $('#chooseList, #chooseSignage').click(function () {
                    
                    this.errorController = new errorController();// hmm not the best way to do this 

                    //choose the view type
                    if (this.id == 'chooseList') {
                        var nextView = 'timeline.html';
                        //alert('Button chooseList as clicekd');
                    }
                    else if (this.id == 'chooseSignage') {
                       //alert('chooseSignage ');
                        var nextView = 'signage.html';

                    }

                    this.errorController.updateMessage("Please choose the performances you would like view",9 );

                     var choices = $('input[name=performance]:checked');
                     //window.console.log(choices);
                     var param  = '?performance='

                     if (choices.length > 1) {

                         $(choices).each(function(pos,val){
                                //do stuff here with this
                               param = param   + val.id ;
                               if (pos != (choices.length-1)) { param = param   + "+"};
                         });

                         } else {
                              param = param  + '' + choices[0].id;
                             // param = param   + this.id + "+";
                        }

                 
                     
                    //get the options from the form
                    document.location.href = nextView + param ;

                    //send the post request for this .
                });


     
		
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
				
							

		}	


         

		
} 



