/*
 * This file is part of the Aus-e-Stage Beta Website
 *
 * The Aus-e-Stage Beta Website is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The Aus-e-Stage Beta Website is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @author corey.wallis@flinders.edu.au
 */

// this function adapted from http://jquery-howto.blogspot.com/2009/09/get-url-parameters-values-with-jquery.html
function getUrlVars(){
	var vars = [], hash;
	var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
	for(var i = 0; i < hashes.length; i++)
	{
		hash = hashes[i].split('=');
		vars.push(hash[0]);
		vars[hash[0]] = hash[1];
	}
	return vars;
}

function getUrlVar(param){
	return getUrlVars()[param];
}

if(getUrlVar('f_org_id') != null) {
	window.location.href =  'http://beta.ausstage.edu.au/mapping2/?simple-map=true&type=organisation&id=' + getUrlVar('f_org_id') + '&source=bookmarklet';
}

if(getUrlVar('f_contrib_id') != null) {
	window.location.href =  'http://beta.ausstage.edu.au/mapping2/?simple-map=true&type=contributor&id=' + getUrlVar('f_contrib_id') + '&source=bookmarklet';
}

if(getUrlVar('f_venue_id') != null) {
	window.location.href =  'http://beta.ausstage.edu.au/mapping2/?simple-map=true&type=venue&id=' + getUrlVar('f_venue_id') + '&source=bookmarklet';
}

if(getUrlVar('f_event_id') != null) {
	window.location.href =  'http://beta.ausstage.edu.au/mapping2/?simple-map=true&type=event&id=' + getUrlVar('f_event_id') + '&source=bookmarklet';
}

// if we get this far something bad happened
alert('Unable to locate the record identifier.\nThis bookmarklet only works with the AusStage Index Drill Down page');
