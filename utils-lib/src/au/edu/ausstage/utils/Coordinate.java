/*
 * This file is part of the AusStage Utilities Package
 *
 * The AusStage Utilities Package is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Utilities Package is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Utilities Package.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.utils;

// import additional libraries
import java.text.DecimalFormat;

/**
 * A class to represent a latitude and longitude
 */
public class Coordinate {

	// declare private class level variables
	private float latitude;
	private float longitude;
	private DecimalFormat format;
	
	/**
	 * Constructor for this class
	 *
	 * @param latitude a latitude coordinate in decimal notation
	 * @param longitude a longitude coordinate in decimal notation
	 */
	public Coordinate(float latitude, float longitude) {
	
		if(CoordinateManager.isValidLatitude(latitude) == true && CoordinateManager.isValidLongitude(longitude) == true) {
			this.latitude = latitude;
			this.longitude = longitude;
		} else {
			throw new IllegalArgumentException("The parameters did not pass validation as defined by the CoordinateManager class");
		}
		
		this.format = new DecimalFormat("##.######");
	}
	
	/*
	 * get and set methods
	 */
	public float getLatitude() {
		return latitude;
	}
	
	public float getLongitude() {
		return longitude;
	}
	
	public void setLatitude(float latitude) {
		if(CoordinateManager.isValidLatitude(latitude) == true) {
			this.latitude = latitude;
		} else {
			throw new IllegalArgumentException("The parameter did not pass validation as defined by the CoordinateManager class");
		}
	}
	
	public void setLongitude(float longitude) {
		if(CoordinateManager.isValidLongitude(longitude) == true) {	
			this.longitude = longitude;
		} else {
			throw new IllegalArgumentException("The parameter did not pass validation as defined by the CoordinateManager class");
		}
	}
	
	public String getLatitudeAsString() {
	
		return format.format(latitude);
	}
	
	public String getLongitudeAsString() {
		return format.format(longitude);
	}
}
