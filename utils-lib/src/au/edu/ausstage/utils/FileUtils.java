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
import java.io.*;

/**
 * A class of methods useful when validating file related tasks
 */
public class FileUtils {

	/**
	 * A method to determine if a directory is valid
	 *
	 * @param path the path to be validated
	 * @param mustNotExist the file must not already exists
	 *
	 * @return             true if, and only if, the path is valid
	 */
	public static boolean isValidDir(String path, boolean mustNotExist) {
	
		// validate the parameter
		if(InputUtils.isValid(path) == false) {
			return false;
		}
	
		// instantiate a file object
		File pathToCheck = new File(path);
		
		// declare helper variable
		boolean status = false;
		
		try {
			// can the file already exists
			if(mustNotExist == true) {
				// no
				if(pathToCheck.isDirectory() == true) {
					// path exists therefore is false
					status = false;
				}
			} else {
				// yes
				if(pathToCheck.isDirectory() == true && pathToCheck.canRead() == true && pathToCheck.canWrite() == true) {
					status = true;
				} else {
					status = false;
				}
			}
		} catch (java.lang.SecurityException ex) {
			status = false;
		}
		
		return status;
	} // end the isValidDir method
	
	/**
	 * A convenience method to determine if a directory exists
	 * 
	 * @param path the path to be validated
	 * @return             true if, and only if, the path is valid
	 */
	public static boolean doesDirExist(String path) {
		return isValidDir(path, false);
	}
	
	/**
	 * A method to determing if a file is valid
	 *
	 * @param path         the path to be validated
	 * @param mustNotExist the file must not already exists
	 *
	 * @return             true if, and only if, the path is valid
	 */
	public static boolean isValidFile(String path, boolean mustNotExist) {
	
		// validate the parameter
		if(InputUtils.isValid(path) == false) {
			return false;
		}
	
		// instantiate a file object
		File pathToCheck = new File(path);
		
		// declare helper variable
		boolean status = false;
		
		try {
			// can the file already exists
			if(mustNotExist == true) {
				// no
				if(pathToCheck.isFile() == true) {
					// path exists therefore is false
					status = false;
				}
			} else {
				// yes
				if(pathToCheck.isFile() == true && pathToCheck.canRead() == true && pathToCheck.canWrite() == true) {
					status = true;
				} else {
					status = false;
				}
			}
		} catch (java.lang.SecurityException ex) {
			status = false;
		}
		
		return status;
	} // end isValidFile method
	
	/**
	 * A convenience method to determine if a directory exists
	 * 
	 * @param path the path to be validated
	 * @return             true if, and only if, the path is valid
	 */
	public static boolean doesFileExist(String path) {
		return isValidFile(path, false);
	}
	
	/**
	 * A method to get the absolute path of a given path
	 *
	 * @param path the path to check
	 * 
	 * @return     the absolute path
	 */
	public static String getAbsolutePath(String path) {
	
		// validate the parameter
		if(InputUtils.isValid(path) == false) {
			return null;
		}
		
		// instantiate a file object
		File pathToCheck = new File(path);
		
		try {
			return pathToCheck.getAbsolutePath();
		} catch (java.lang.SecurityException ex) {
		}
		
		return null;
	} // end getAbsolutePath method
	
	/**
	 * A method to get the absolute path of a given path
	 *
	 * @param path the path to check
	 * 
	 * @return     the absolute path
	 */
	public static String getCanonicalPath(String path) {
	
		// validate the parameter
		if(InputUtils.isValid(path) == false) {
			return null;
		}
		
		// instantiate a file object
		File pathToCheck = new File(path);
		
		try {
			return pathToCheck.getCanonicalPath();
		} catch (java.lang.SecurityException ex) {}
		  catch (java.io.IOException ex) {}
		
		return null;
	} // end getAbsolutePath method
	
	
} // end class definition
