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


function visControllerImageSequence(newModel) {
		
		 this.model = newModel;
                 this.key = '8dc8cea8944f148aa635d951abf8c72d';

           
             
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

                        this.buildImageSequence();

                        
                }


                 /*
		* Get's the current words as object s
                * @param  word  - the current words so know which div to add it to
                * return word object
		*/

                this.getWord = function (word){

                    //loop over the words
                    for(var a = 0; a <  this.model.words.length; a++) {
                        //this this current word ?
                        if (this.model.words[a].word == word) {
                           return this.model.words[a];
                        }
                        //window.console.log(this.model.words[a].word);
                        //window.console.log(result);
                    }

                }

                /*
		* Get's and ficker user id for aId
                * @param  id
                * @param currentWord - the current words so know which div to add it to
		*/

                this.getUserID = function (id,word){

                     
                      var params = {
                               api_key: this.key,// aus-stage
                               //api_key:'5a6cc7f302878d0267bd81f040b5b2a0',// sprout labs
                               user_id:id,
                              format:'json'
                      };

                      var values = jQuery.param(params);//encode thos
                      var source = 'http://api.flickr.com/services/rest/?method=flickr.people.getInfo&' + values + '&nojsoncallback=1';


                      current = this; // in the success function we normal scope this, so we put this in to current so called stuff in a few moments.

                         this.ajaxQueue = $.manageAjax.create(id, {
                            queue: true,
                            cacheResponse: false
                         });


                         $.manageAjax.add(word, {
                            success: function (data, textStatus, jqXHR,options) {

                                   //parse those results
                                   var results = jQuery.parseJSON(data);
                                  //window.console.log(results);

                                  if(results.person.realname != null) {
                                      var name= results.person.realname._content;
                                  } else {
                                      var name=results.person.username._content;
                                  }

                                  //now actually write the credit to spane with the ID
                                 
                                  var target = '#' + word + ' .owner';
                                  //window.console.log(target);

                                  $(target).append(name);
                                  
                                  //now actually turn the on
                                  var target = '#' + word + ' .Attributions';
                                  $(target).show();

                                 //window.console.log(target);

                              
                            },
                    //Some thing has gone wrong, show the user an error screen.
                            error:function (xhr, ajaxOptions, thrownError){

                                    alert(xhr.status);
                                    alert(thrownError);

                            },
                            type:   'GET',
                            url: source,
                            cache: false,
                            async: true,
                        });


                }

                   


               /*
		* Writes the actual images and tooltips to the DOM   
                * @param  word  
                * 
		*/

                this.updatesImage = function (word,results){


                    var ran = Math.floor(Math.random()*11);
                    var imageData = results.photos.photo[ran];

                    if(imageData) {
                    var flickrURL = 'http://farm' + imageData.farm + '.static.flickr.com/' + imageData.server + '/' + imageData.id +  '_' + imageData.secret + '.jpg';

                    //now get get the id of the
                    //window.console.log(imageData);

                     var flickrURL = 'http://farm' + imageData.farm + '.static.flickr.com/' + imageData.server + '/' + imageData.id +  '_' + imageData.secret + '.jpg';

                     //now get the word - now this so we can make decisions about the scale of the image

                     var wordObj = this.getWord(word);
                     ///window.console.log(this.getWord(word));
                     var imageScale = wordObj.count * 40;

                    $(".feedback_messages").append('<div class="feedback" id="' + word + '"\n\
                       "><div class="content">\n\
                    \n\
                    <img src="' + flickrURL + '" width="' + imageScale + '">\n\
                        </div>\n\
                            <span class="word">' +
                        '' + word +
                        '</span><span class = "Attributions"> Creative Commons Licensed flickr photo shared by\n\
                          <a href="http://www.flickr.com/photos/' +
                          imageData.owner +  '/' + imageData.id +
                          '"><span class="owner"></span></a>' +

                        '</div>'
                        );

                     //turn of the Attributions until we have the for owner info

                       var target = '#' + word + ' .Attributions';
                       $(target).hide();
                          
                      this.getUserID(imageData.owner,word);
                     }
                    
                }


                /*
		* Builds the image sequence
                * @param  word the word we are looking for  
                * 
		*/

                this.findFlickrImage = function (word){
                      //alert(word);
                      //build the params for the flikr search this just give a list image to start with
                      var params = {
                               api_key: this.key,// aus-stage
                               //api_key:'5a6cc7f302878d0267bd81f040b5b2a0',// sprout labs
                               tags:word,
                               sort:'interestingness-desc',
                               license:4,
                               format:'json',
                               
                       };

                       //window.console.log(word);
                       
                       var values = jQuery.param(params);//encode thos
                       var source = 'http://api.flickr.com/services/rest/?method=flickr.photos.search&' + values + '&nojsoncallback=1';

                       //window.console.log('WORD = ' + word);

                       //window.console.log(source);
                        
                        current = this; // in the success function we normal scope this, so we put this in to current so called stuff in a few moments.

                         this.ajaxQueue = $.manageAjax.create(word, {
                            queue: true,
                            cacheResponse: false
                         });


                         $.manageAjax.add(word, {
                            success: function (data, textStatus, jqXHR,options) {

                                if (data.length !=  0) {

                                   var results = jQuery.parseJSON(data);

                                    //window.console.log(options);
                                    //window.console.log(word);

                                   //window.console.log(imageData);
                                   //window.console.log(flickrURL);
                                    current.updatesImage(word,results);


                                } else {
                                    //TODO add what happens if don't get any results back'
                                }
                            },
                    //Some thing has gone wrong, show the user an error screen.
                            error:function (xhr, ajaxOptions, thrownError){

                                    alert(xhr.status);
                                    alert(thrownError);

                            },
                            type:   'GET',
                            url: source,
                            cache: false,
                            async: true,
                        });


                        
                            

                        /* - the old working version of this without the ajax manager/
                         * $.ajax({
                            type:   'GET',
                            url: source,
                            cache: false,
                            async: false,

                            success: function (data, textStatus, jqXHR) {

                                if (data.length !=  0) {

                                   var results = jQuery.parseJSON(data);
                                   
                                   //window.console.log(results);
                                   
                                   //window.console.log(imageData);
                                   //window.console.log(flickrURL);
                                    current.updatesImage(results);
                                   

                                } else {
                                    //TODO add what happens if don't get any results back'
                                }
                            },
                    //Some thing has gone wrong, show the user an error screen.
                            error:function (xhr, ajaxOptions, thrownError){

                                    alert(xhr.status);
                                    alert(thrownError);

                            },
                            async: true
                            });*/
                }



                 




                /*
		* Builds the image sequence
                *
		*/

               	this.buildImageSequence = function ()
		{
			//window.console.log('about to build the controller for the tag cloud');
			//this.results = newResults;
                        //
                        //window.console.log(this.model.results.length);

                        var displayPerformance = false;

                        //TODO - rework this section - do we need it ?
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

                                if(this.model.words[a].count > 3 ) { // do this when we have more than two pieces of feedback with this work

                                   //get the flickr imag for that work
                                   //
                                    //render the words

                                   //The flickr URL to the word object
                                   this.model.words[a].word.flickrURL =   this.findFlickrImage(this.model.words[a].word);

 
                                   $('#tagCloud').append(
                                   '<span class="' +
                                       this.model.words[a].word
                                   +
                                   '"  style = "font-size:' + this.model.words[a].count/3 + 'em" > ' +
                                   this.model.words[a].word
                                   + '</span>'
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



                                          ;
                                          
                                         
                                          
                                         htmlFeedBackList =   htmlFeedBackList +  '</li>';


                                         //window.console.log(this.model.words[a].feedback[f]);
                                     }
                                  ;


                                  $('.' + this.model.words[a].word ).qtip({
                                       
                                        content: {
                                            text:'<ul>' +  htmlFeedBackList + '</ul>'
                                        },
                                        position: {
                                             at: 'bottom right',
                                             viewport: $(window),
                                             adjust: {scroll: true}
                                         
                                       },

                                         style: {
                                              classes: 'ui-tooltip-austage ui-tooltip-shadow',
                                              tip: {
                                                  corner:true
                                              }
                                           },
                                         show: {
                                                effect: function() {$(this).fadeIn(100);} // Show
                                            },

                                         hide: {
                                               
                                                effect: function() {$(this).fadeOut(100);} // Hide

                                            },

                                      }


                                                        )


                                }
                         }

                    
                                  

                }
                

                
			

		
		
		

		
} 



