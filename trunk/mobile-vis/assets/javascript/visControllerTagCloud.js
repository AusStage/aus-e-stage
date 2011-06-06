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


function visControllerTagCloud(newModel) {
		
		 this.model = newModel; 
                 
                 

 
		/*
		* Called when the results are updated     
		*/ 	
			
		this.refresh = function (newResults,newPerformances)
		{
			//TODO - work how do we work refresh  - do just call it all again 
		}
		
		
		/*
		* Called when the results are first are found.   
		*/ 	
			
		this.build = function ()
		{
			//window.console.log('about to build the controller for the tag cloud');
                        //window.console.log(this.model.words);
			//this.results = newResults;

                        this.buildTagCloud();

                        
                }

                /*
		* Draws/Builds the tag cloud.  
                *
		*/

               	this.buildTagCloud = function ()
		{
			//window.console.log('about to build the controller for the tag cloud');
			//this.results = newResults;
                        //
                        //window.console.log(this.model.results.length);

                        var displayPerformance = false;
                        
                        if(this.model.results.length == 1){

                        //Show the performance name etc for just on peformance  - just is the most common way of doing this
                             $(window.$(".info")).css('display','block');

                             $("span.event").html(this.model.results[0].event);
                             $("span.organisation").html(this.model.results[0].organisation);
                             $(".venue").html(this.model.results[0].venue);
                             $(".date").html(this.model.results[0].date);
                             $("span.question").html(this.model.results[0].question);


                        } else {
                              //we have than performance - and that needs to be added to the content on of tag
                                $(window.$(".info")).css('display','none');

                                displayPerformance = true;

                        }

                        
                         //loops over the words
                         for(var a = 0; a <  this.model.words.length; a++) {
                                
                                //window.console.log(this.model.words[a]);

                                if(this.model.words[a].count > 2 ) { // do this when we have more than two pieces of feedback with this work

                                    //render the words

                                   var size = (this.model.words[a].count/this.model.wordMaxCount * 8);

                                   $('#tagCloud').append(
                                   '<div class ="word" id="' +
                                       this.model.words[a].word
                                   +
                                   '"  style = "font-size:' + size + 'em" > ' +
                                   this.model.words[a].word
                                   + '<span class="FeedbackList"><ul></ul></span></div>'
                                   );

                                  //add the tooltip to the works

                                  //build the list of feedback
                                     var htmlFeedBackList = "";
                                                                             

                                     for(var f = 0; f <  this.model.words[a].feedback.length; f++) {
                                         htmlFeedBackList =   htmlFeedBackList +  '<li>' + this.model.words[a].feedback[f];

                                      //    window.console.log(this.model.words[a].performance[f]);
                                          var PerformancePosInList = this.model.words[a].performance[f];
                                          //window.console.log(this.model.results[PerformancePosInList].organisation);

                                          htmlFeedBackList =  htmlFeedBackList +  '<br /> <span class="feedbackInfo">';
                                           if(this.model.results.length > 1){
                                             htmlFeedBackList =  htmlFeedBackList +  this.model.results[PerformancePosInList].event + ', ' +
                                             this.model.results[PerformancePosInList].organisation + ', ';
                                            }

                                            //window.console.log(this.model.words[a].feedback[f])
                                            // if where where feedback is on the performance

                                            //var feedBackPos = this.model.results[PerformancePosInList].feedback.content.indexOf(this.model.words[a].feedback[f]);

                                           // window.console.log(this.model.results[PerformancePosInList].feedback);

                                            var currentFeedbackArray = this.model.results[PerformancePosInList].feedback;

                                            //hmm this is not perfect but it works

                                            for(var c = 0; c < currentFeedbackArray.length; c++) {
                                               if (this.model.words[a].feedback[f] == currentFeedbackArray[c].content) {
                                                   var currentFeedback =  currentFeedbackArray[c];
                                                };
                                             };
                                              
                                            //window.console.log(this.model.results[PerformancePosInList].feedback);
                                            //window.console.log(currentFeedback);

                                            htmlFeedBackList =  htmlFeedBackList +  'Via ' +  currentFeedback.type + ', ';
                                            htmlFeedBackList =  htmlFeedBackList +  currentFeedback.date  + ' ' + currentFeedback.time +  '</span>';

                                         
                                          
                                         htmlFeedBackList =   htmlFeedBackList +  '</li>';
                                      };


                                      var target = '#' + this.model.words[a].word + " .FeedbackList" ;

                                       $(target).hide();

                                       $(target).html(htmlFeedBackList);
                                       $(target).highlight(this.model.words[a].word );

                                        var target = '#' + this.model.words[a].word;
                                        //window.console.log(target);

                                           $(target).click(function(eventData ) {
                                                  //window.console.log(eventData.target.id);
                                                  var word = eventData.target.id;

                                                  var myTarget = '#' + eventData.target.id + " .FeedbackList";
                                                  //window.console.log(myTarget);
                                                  
                                                  $(myTarget).dialog({
                                                        height: $(window).height() *.7,
                                                        width: $(window).width() *.8,
                                                        title: word,
                                                        closeOnEscape: true,
                                                        modal: true,
                                                        draggable: false
                                                     });


                                           });

                                     


                                          


                                }
                         }

                    
                                  

                }
                

              
                
			

		
		
		

		
} 



