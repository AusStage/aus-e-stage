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


function visControllerWordStats(newModel) {
		
		 this.model = newModel; 
                 
                 // Adapted from <http://www.perseus.tufts.edu/Texts/engstop.html>
                 
                 this.stopwords =  [ "a", "about", "above", "accordingly", "after",
                  "again", "against", "ah", "all", "also", "although", "always", "am", "among", "amongst", "an",
                  "and", "any", "anymore", "anyone", "are", "as", "at", "away", "be", "been",
                  "begin", "beginning", "beginnings", "begins", "begone", "begun", "being",
                  "below", "between", "but", "by", "ca", "can", "cannot", "come", "could",
                  "did", "do", "doing", "during", "each", "either", "else", "end", "et",
                  "etc", "even", "ever", "far", "ff", "following", "for", "from", "further", "furthermore",
                  "get", "go", "goes", "going", "got", "had", "has", "have", "he", "her",
                  "hers", "herself", "him", "himself", "his", "how", "i", "if", "in", "into",
                  "is", "it", "its", "itself", "last", "lastly", "less", "many", "may", "me",
                  "might", "more", "must", "my", "myself", "near", "nearly", "never", "new",
                  "next", "now", "o", "of", "off", "often", "oh", "on", "only",
                  "or", "other", "otherwise", "our", "ourselves", "out", "over", "perhaps",
                  "put", "puts", "quite", "s", "said", "saw", "say", "see", "seen", "shall",
                  "she", "should", "since", "so", "some", "such", "t", "than", "that", "the",
                  "their", "them", "themselves", "then", "there", "therefore", "these", "they",
                  "this", "those", "though", "throughout", "thus", "to", "too",
                  "toward", "unless", "until", "up", "upon", "us", "ve", "very", "was", "we",
                  "were", "what", "whatever", "when", "where", "which", "while", "who",
                  "whom", "whomever", "whose", "why", "with", "within", "without", "would",
                  "yes", "your", "yours", "yourself", "yourselves" ];

              

 
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
			//window.console.log('about to build the controller for this');
			//this.results = newResults; 
                        this.BuildStats();
                }

                /*
		* Counts the words on that are founded on the feedback and store as part of the
                * model object. 
                *
		*/

                this.CountWords = function ()
                {
                           var wordsList = new Array();
                           this.model.words = new Array();

                                for(var a = 0; a <  this.model.results.length; a++) {
                                //now update for muliple.
                                    for(var i = 0; i < this.model.results[a].feedback.length; i++) {
                                          feedbackWords = this.model.results[a].feedback[i].words;
                                          //window.console.log(feedbackWords);
                                          
                                         //now loop over the words in this piece of feedback
                                         for(var w = 0; w < feedbackWords.length; w++) {

                                                //see if it's the word is in our list already
                                                if (wordsList.indexOf(feedbackWords[w]) != -1) {

                                                   // window.console.log('found ' + feedbackWords[w]);
                                                    //window.console.log('current work list  ' + wordsList );

                                                    var index = wordsList.indexOf(feedbackWords[w]);
                                                    this.model.words[index].count ++;
                                                    
                                                    //need to check to make sure we don't have this feedback already 

                                                     //window.console.log(this.model.words[index].feedback.indexOf(this.model.results[a].feedback[i].content));
                                                     
                                                     if (this.model.words[index].feedback.indexOf(this.model.results[a].feedback[i].content) == -1) {
                                                            this.model.words[index].performance.push(a); // where it's in the list list of results
                                                            this.model.words[index].feedback.push(this.model.results[a].feedback[i].content);
                                                     }


                                                } else {
                                                    //if not in the list add to word list
                                                    
                                                    wordsList.push(feedbackWords[w]);//this is just our simple tracking list not where they are actually stored
                                                    
                                                    //make a new object to store the information about the feedbacl
                                                    var foundWord = new Object();
                                                    foundWord.word = feedbackWords[w];
                                                    foundWord.count = 1;

                                                    foundWord.performance = new Array(); // where it's in the list list of results
                                                    foundWord.performance.push(a);

                                                    foundWord.feedback = new Array(this.model.results[a].feedback[i].content);

                                                    //foundWord.feedback = this.model.results[a].feedback[i].content;

                                                    this.model.words.push(foundWord);

                                                    //window.console.log('added ' + feedbackWords[w]);
                                                    //window.console.log(this.model.words);

                                                }
                                         }
                                            
                                      }

				}

                //window.console.log(this.model.words);
                //window.console.log(this.model.results);
                }
                
		this.RemoveStopWords = function ()
                {
                           //window.console.log(this.model);
                              

                                for(var a = 0; a <  this.model.results.length; a++) {
                                //now update for muliple.
                                    for(var i = 0; i < this.model.results[a].feedback.length; i++) {
                                            item = this.model.results[a].feedback[i];
                                            var wordsString = "";
                                            //window.console.log(item);
                                           //
                                           //Make it lower case
                                           wordsString = item.content.toLowerCase();
                                           //window.console.log(wordsString);
                                           //
                                           //take out all the syntax stuff
                                           wordsString = wordsString.replace(/[^A-Z\xC4\xD6\xDCa-z\xE4\xF6\xFC\xDF0-9_]/g," ");

                                           //now remove the stop words
                                        
                                        ///wordsString = wordsString.replace(this.stopwords, "X");

                                           for(var s=0; s < this.stopwords.length; s++ ) {

                                                 var replace = new RegExp("\\b" + this.stopwords[s] + "\\b","g");
                                                 wordsString = wordsString.replace(replace , "");
                                           }


                                           //window.console.log(wordsString);
                                           

                                           //now  remove the tag form the feedback
                                           var tag = "#" + this.model.results[a].tag;
                                           wordsString = wordsString.replace(tag,"");
                                           //window.console.log(wordsString);
                                           
                                           wordsString = wordsString.replace(this.model.results[a].tag,"");

                                           
                                           var words = wordsString.split(' ');
                                           //remove all the blank bits 
                                           words = $.grep(words,function(n,i){
                                                    return(n);
                                                });
                                           ///window.console.log(words);

                                           //update the actual data in the model
                                           this.model.results[a].feedback[i].words = words;
                                           


                                      }

				}
                //window.console.log(this.model.results);
                }

                
		
		/*
		* BuildStats   
		*/
		
		this.BuildStats  = function ()
		{	
			
                        this.RemoveStopWords();
                        this.CountWords();
			 
		}	

		
		
		

		
} 



