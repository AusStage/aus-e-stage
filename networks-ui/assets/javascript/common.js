/*
 * This file is part of the AusStage Mapping Service
 *
 * The AusStage Mapping Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Mapping Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
 */

var PANEL = 'viewer';
var EDGE = "edge";
var NODE = "node";
var CLEAR = "clear";
var allowToggle = true;


//function to apply correct style to buttons
function styleButtons() {
        $("button, input:submit").button();
        $("button, input:button").button();
}

// define a function to build an info message box
function buildErrorMsgBox(text) {
        return '<div class="ui-state-error ui-corner-all search-status-messages" id="error_message"><p><span class="ui-icon ui-icon-alert status-icon"></span>' + text + '</p></div>';
}

function buildInfoMsgBox(text) {
        return '<div class="ui-state-highlight ui-corner-all search-status-messages" id="status_message"><p><span class="ui-icon ui-icon-info 				status-icon"></span>' + text + '</p></div>';
}


 //create the legend
function createLegend(element, openFunction, closeFunction){
	$(element).button({ icons: {primary:'ui-icon-triangle-1-e',secondary:null}});
    //$(element).css({'text-align':'left', 'padding': '0 0 0 0', 'margin':'0 0 0 0'});
    
    $(element).click(function () {
    	if(allowToggle){
			$(this).toggleClass("open");
			if($(this).hasClass("open")){
				$(this).button( "option", "icons", {primary:'ui-icon-triangle-1-s',secondary:null} );
				if(openFunction){
					openFunction();
				}
			} else{
				$(this).button( "option", "icons", {primary:'ui-icon-triangle-1-e',secondary:null} );					
				if(closeFunction){
					closeFunction();
				}
				
			}
			$(element).next().slideToggle();
    	}else{
    		allowToggle=true;
    	}
    	$(".ellipsis").tipsy({gravity: $.fn.tipsy.autoNS});   					  			
    	$(".titleLink").click(function(){
			allowToggle = false;
		});  
	}); 
}

//reset the legend to its closed state 
function resetLegend(element){
	if ($(element+'_header').attr('class').indexOf("open") >=0){
		$(element+'_header').button( "option", "icons", {primary:'ui-icon-triangle-1-e',secondary:null} );
		$(element+'_header').toggleClass("open");
		$(element+'_body').hide();
	}
	
}

//sorting function
function sortByName(a, b) {
    var x = a.name.toLowerCase();
    var y = b.name.toLowerCase();
    return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}

function sortNumeric(a, b) {
    return a-b;
}

//used to measure and replace text with ellipses based on width.
function constrain(text, ideal_width, className, linkClass){

    var temp_item = ('<span class="'+className+'_hide" style="display:none;">'+ text +'</span>');
    $(temp_item).appendTo('body');
    var item_width = $('span.'+className+'_hide').width();
    var ideal = parseInt(ideal_width);
    var smaller_text = text;

    if (item_width>ideal_width){
        while (item_width > ideal) {
        	smaller_text = smaller_text.substr(0,(smaller_text.lastIndexOf(", ")));
            $('.'+className+'_hide').html(smaller_text + '&hellip;');
            item_width = $('span.'+className+'_hide').width();
        }
        smaller_text = smaller_text + '<a href="#" class="'+linkClass+'" title="'+text+'">&hellip;</a>'
    }
    $('span.'+className+'_hide').remove();
    return smaller_text;
}	

//quick function to check if an array contains a property	
function contains(a, obj){
	for(var i = 0; i < a.length; i++) {
    	if(a[i] == obj){
			return true;
    	}
  	}
  	return false;
}

//determine if a number is odd or even returns true if even, false if odd
function isEven(a){
	var num = a/2;
	var inum = Math.round(num);
	
	if (num == inum) return true;
	else return false;		
}

//safari fix for date handling
function parseDate(input) {
  var parts = input.match(/(\d+)/g);
  return new Date(parts[0], parts[1]-1, parts[2]);
}

//text measurement function to allow for solid backgrounds on labels.
function measureText(pText) {

 $("#ruler").empty(); 
 $("#ruler").append(pText);
 return $("#ruler").width();
 
}

// function to get the month from a integer index
function lookupMonthFromInt(value) {

        switch(value) {
                case 1:
                        return "January";
                        break;
                case 2:
                        return "February";
                        break;
                case 3:
                        return "March";
                        break;
                case 4:
                        return "April";
                        break;
                case 5:
                        return "May";
                        break;
                case 6:
                        return "June";
                        break;
                case 7:
                        return "July";
                        break;
                case 8:
                        return "August";
                        break;
                case 9:
                        return "September";
                        break;
                case 10:
                        return "October";
                        break;
                case 11:
                        return "November";
                        break;
                case 12:
                        return "December";
                        break;
                default:
                        return "";
        }
}

