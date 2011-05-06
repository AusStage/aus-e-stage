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
 
package au.edu.ausstage.mapping;

/**
 * Represent an exception that is caused when something bad happens with KML generations
 */
public class KmlDownloadException extends RuntimeException {

	String error = null;
	
	public KmlDownloadException() {
		super();
		error = "unknown";
	}
	
	public KmlDownloadException(String err) {
		super(err);
		error = err;
	}
	
	public String getError() {
		return error;
	}
}
