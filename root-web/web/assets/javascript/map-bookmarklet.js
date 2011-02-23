/*
 * This file is part of the Aus-e-Stage Beta Root Website
 *
 * The Aus-e-Stage Beta Root Website is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The Aus-e-Stage Beta Root Website is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Aus-e-Stage Beta Root Website.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

/*
 * When this bookmarkmlet is used it links for records in AusStage directly to the mapping service
 *
 * currently only organisation records are supported
 */
 
// declare helper functions
// adapted from http://jquery-howto.blogspot.com/2009/09/get-url-parameters-values-with-jquery.html
// get all of the parameters in the URL as a hash
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

// get the specified param
function getUrlVar(param){
	return getUrlVars()[param];
}

// get the parameter that is the organisation id
var orgId = getUrlVar('f_org_id');

// check to make sure the id was found
if(orgId != null) {
	// yes it was, so use it
	window.location.href = 'http://beta.ausstage.edu.au/mapping/maplinks.jsp?type=org&id=' + orgId + '&source=bookmarklet';
} else {
	// no it wasn't so show error
	alert('This bookmarklet works only with organisation records in AusStage.\nIf this problem persists please contact us with the URL of this page');
}
