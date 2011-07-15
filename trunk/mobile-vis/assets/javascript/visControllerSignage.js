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
﻿  ﻿
﻿  ﻿   this.model = newModel;

﻿  ﻿  //Configure for the slide show
﻿  ﻿   this.fx =  'fade';  // choose your transition type, ex: fade, scrollUp, shuffle, etc...﻿
﻿  ﻿   this.speed =  3000;
﻿  ﻿   this.timeout =  500;
﻿  ﻿  ﻿  ﻿  ﻿  ﻿
﻿  ﻿  ﻿  ﻿  ﻿  ﻿
﻿  ﻿  /*
﻿  ﻿  * Called when the results are updated
﻿  ﻿  */ ﻿
﻿  ﻿  ﻿
﻿  ﻿  this.refresh = function (newResults)
﻿  ﻿  {
﻿  ﻿  ﻿  //window.console.log('about to refresh the controller for signage' );
﻿  ﻿  ﻿  //this.results = newResults;
﻿  ﻿  ﻿  this.refreshView(newResults); // just do a simple redraw because in this case we not going to do much with data.

﻿  ﻿  }
﻿  ﻿
﻿  ﻿
﻿  ﻿  /*
﻿  ﻿  * Called when the results are first are found.
﻿  ﻿  */ ﻿
﻿  ﻿  ﻿
﻿  ﻿  this.build = function (newResults)
﻿  ﻿  {
﻿  ﻿  ﻿  //window.console.log('about to build the controller for this');
﻿  ﻿  ﻿  //this.results = newResults;
﻿  ﻿  ﻿  this.BuildView(); // just do a simple redraw because in this case we not going to do much with data.
﻿  ﻿  ﻿  ﻿
﻿  ﻿  ﻿  ﻿  //For more more information about the slide plug that is used see http://jquery.malsup.com/cycle/
﻿  ﻿  ﻿  ﻿  ﻿  ﻿
﻿  ﻿  ﻿  ﻿  $('.feedback_messages').cycle({
﻿  ﻿  ﻿  ﻿  ﻿  fx: this.fx,
﻿  ﻿  ﻿  ﻿  ﻿  speed: this.speed,
﻿  ﻿  ﻿  ﻿  ﻿  timeout: this.timeout,
﻿          timeoutFn: ﻿this.calcTimeOut,
﻿  ﻿  ﻿  ﻿  });﻿
﻿  ﻿  ﻿  ﻿  ﻿  ﻿  ﻿


﻿  ﻿  }
﻿  ﻿
    ﻿  ﻿  ﻿this.calcTimeOut  = function (currElement, nextElement, opts, isForward) {

                //window.console.log($(currElement).attr('data-duration'));
                var newDuration =  $(currElement).attr('data-duration');
                return parseInt(newDuration);

        }
﻿  ﻿  /*
﻿  ﻿  * Build the signage view with the current results.
﻿  ﻿  */
﻿  ﻿  this.BuildView  = function (newResults)
﻿  ﻿  {﻿
﻿  ﻿  ﻿
﻿  ﻿      results = this.model.results﻿  ﻿  ﻿  ﻿  ﻿  ﻿
﻿  ﻿  ﻿  ﻿  ﻿  ﻿  ﻿
﻿  ﻿  ﻿  ﻿  ﻿  ﻿  ﻿  ﻿  ﻿
﻿  ﻿  ﻿  for (var a = 0; a < results.length; a++) { //loop over the controllers
﻿  ﻿  ﻿  ﻿
﻿  ﻿  ﻿  ﻿  //Show the performance name etc
﻿  ﻿  ﻿  ﻿   $('.theQuestion').html(results[a].question);
﻿  ﻿  ﻿  ﻿   $(".tag").html(results[a].tag);


                                //feedbackLength =  results[a].feedback.length -1;
                                //if(feedbackLength == - 1) {
                                    for(var i = 0; i < results[a].feedback.length; i++) {
                                            var item = results[a].feedback[i];
                                            //Make the update acutally hoppen
                                           ;
                                        var wordcount = item.content.split(' ').length/10;

                                        //based on http://en.wikipedia.org/wiki/Words_per_minute

                                        var feedbackDuration = (wordcount * .4)*10000;

                                        //with font count $(".feedback_messages").append('<span class="feedback ' + item.id + '"><div class="content" style="font-size:'+ wordcount + 'em;" >' + item.content + '</div><span class="feedback-about"><span class="date">' + item.date  + '</span><span class="time">' + item.time + ' </span><span class="type">' + item.type + ' </span></span></span>');
                                        $(".feedback_messages").append('<span class="feedback ' + item.id + '" data-duration="'+  feedbackDuration +'"><div class="content" >' + item.content + '</div><span class="feedback-about"><span class="date">' + item.date  + '</span><span class="time">' + item.time + ' </span><span class="type">' + item.type + ' </span></span></span>');
                                        this.reSizeFeedback(item);



                                    }
                              // }
﻿  ﻿  ﻿  }﻿
﻿  ﻿  ﻿  ﻿  ﻿  ﻿  ﻿
﻿  ﻿  }﻿

                /*
﻿  ﻿  * Check the size of feedback
﻿  ﻿  * .
﻿  ﻿  */
﻿  ﻿  this.reSizeFeedback  = function (item)
﻿  ﻿  {
                                                        var minHeight = $(".feedback." + item.id).css('min-height');

                                                        minHeight = Number(minHeight.replace(/px$/, ''));

                                                       //window.console.log(minHeight);

                                                       /// window.console.log('new ' +$(".feedback." + item.id).height());
                                                        if($(".feedback." + item.id).height() > minHeight) {

                                                          //  window.console.log('old ' +$(".feedback." + item.id).height());

                                                           while ($(".feedback." + item.id).height() > minHeight)
                                                          {

                                                              var newSize =  $(".feedback." + item.id).css('font-size');
                                                              newSize = Number(newSize.replace(/px$/, ''));
                                                              newSize = newSize * .9;

                                                             // window.console.log(newSize);

                                                              $(".feedback." + item.id).css('font-size',newSize);
                                                            }

                                                           // window.console.log('new ' +$(".feedback." + item.id).height());


                                                    }
                }

﻿  ﻿  /*
﻿  ﻿  * Refresh the signage view with the current results.
﻿  ﻿  * There is a more elegant whay to do this.
﻿  ﻿  */
﻿  ﻿  this.refreshView  = function (newResults,newPerformances)
﻿  ﻿  {﻿

                //window.console.log('In the signage view refresh');

                  $('#feedback').fadeOut('slow', function() {

                        $('.feedback_messages').cycle(
                                 'destroy'
                          );

                        $.each(newResults, function() {
                        ////window.console.log('in the performances loop ');
                        ////window.console.log(this);
                             $.each(this, function() {
                              //  //window.console.log('in the feedback loop ');
                                ////window.console.log(this.content);

                                     $(".feedback_messages").prepend('<span class="feedback"><div class="content">' + this.content + '</div><span class="feedback-about"><span class="date">' + this.date  + '</span><span class="time">' + this.time + ' </span><span class="type">' +  this.type + ' </span></span></span>');
                                     reSizeFeedback(item);

                         });
                         });


                        //REBUILD Add it back
                       $('.feedback_messages').cycle({
                                    fx: this.fx,
                                    speed: this.speed,
                                    timeout: this.timeout,
                                    timeoutFn: ﻿this.calcTimeOut,

                         });



                        $('#feedback').fadeIn('slow');
                   });

﻿

                }


﻿  ﻿  ﻿  ﻿
﻿  ﻿

             ﻿
﻿  ﻿
﻿  ﻿
﻿  ﻿  ﻿  ﻿


﻿  ﻿



﻿  ﻿
}



