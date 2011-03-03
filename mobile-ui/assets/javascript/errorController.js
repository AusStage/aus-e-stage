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


function errorController() {
		

		/*
		* Called when a error is found      
		*/ 	
			
		this.updateError = function (message, error)
		{
			window.console.log('------- ERROR -------' + error );
			$(window.$("#error_message")).css('display','block');
			$(window.$("#error_message")).append(message + " " + error );

		}
		
		
		/*
		* Called when a error needs to be turned off   
		*/ 	
			
		this.turnOffError = function ()
		{
			$(window.$("#error_message")).css('display','none');

		}
		
		
		 



		
} 



