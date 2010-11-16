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

package au.edu.ausstage.beta;
 
 
/**
 * A class to manage the manipulation of the XML report into HTML using XSL
 */
public class AnalyticsManager {
	
	/**
	 * A method to take convert the XML representation of the report into HTML using XSL
	 *
	 * @param reportDirectory, the directory where the report files live
	 * @param reportXSLFile    the name of the report XSL file, assumed to be in the reportDirectory
	 * @param reportXMLFile    the name of the report XML file, assumed to be in the reportDirectory
	 *
	 * @return                 the result of the transformation as a HTML encoded string
	 */
	public static String processXMLReport(String reportDirectory, String reportXSLFile, String reportXMLFile) {
	
		return "";
	}
}
