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

	// declare private class level variables
	private static final String ENCODING = "UTF8";

	/**
	 * A method to determine if a directory is valid
	 *
	 * @param path the path to be validated
	 * @param mustNotExist the file must not already exists
	 *
	 * @return             true if, and only if, the path is valid
	 */
	public static boolean isValidDir(String path, boolean mustNotExist) {
	
		// check on the parameters
		if(InputUtils.isValid(path) == false) {
			throw new IllegalArgumentException("The path parameter must not be null");
		}
	
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
		
		// check on the parameters
		if(InputUtils.isValid(path) == false) {
			throw new IllegalArgumentException("The path parameter must not be null");
		}
		
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
	
		// check on the parameters
		if(InputUtils.isValid(path) == false) {
			throw new IllegalArgumentException("The path parameter must not be null");
		}
	
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
	 * A convenience method to determine if a file exists
	 * 
	 * @param path the path to be validated
	 * @return             true if, and only if, the path is valid
	 */
	public static boolean doesFileExist(String path) {
		
		// check on the parameters
		if(InputUtils.isValid(path) == false) {
			throw new IllegalArgumentException("The path parameter must not be null");
		}
		
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
		
		// check on the parameters
		if(InputUtils.isValid(path) == false) {
			throw new IllegalArgumentException("The path parameter must not be null");
		}
	
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
	
		// check on the parameters
		if(InputUtils.isValid(path) == false) {
			throw new IllegalArgumentException("The path parameter must not be null");
		}
	
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
	
	/**
	 * A method to write a file given the path and contents
	 *
	 * @param path     the path to the file to create
	 * @param contents the contents of the file
	 *
	 * @return         true, if an only if, the file is written successfully
	 */
	public static boolean writeNewFile(String path, String contents) {
	
		// check on the parameters
		if(InputUtils.isValid(path) == false || InputUtils.isValid(contents) == false) {
			throw new IllegalArgumentException("Both parameters to this method are required");
		}
	
		// check to ensure the path is valid
		if(doesFileExist(path) == true) {
			return false;
		}
		
		// get the canonical path
		path = getCanonicalPath(path);
		
		// double check the path
		if(path == null) {
			return false;
		}
		
		// write the data to the file
		try {
		
			// instantiate a new printWriter object
			PrintWriter output = new PrintWriter(path, ENCODING);
			
			// write the contents of the file
			output.write(contents);
			
			// check on the status of the write
			if(output.checkError() == true) {
				return false;
			} 
			
			// close the output stream
			output.close();
						
		} catch (java.io.FileNotFoundException ex) {
			return false;
		} catch (java.io.IOException ex) {
			return false;
		}
		
		// if we get this far everything is OK
		return true;
	}
	
	/**
	 * A method to rename a file given the old and new names
	 *
	 * @param oldName the old name of the file
	 * @param newName the new name of the file
	 *
	 * @return true if, and only if, the file is renamed successfully
	 */
	public static boolean renameFile(String oldName, String newName) {
	
		// check on the parameters
		if(InputUtils.isValid(oldName) == false || InputUtils.isValid(newName) == false) {
			throw new IllegalArgumentException("Both parameters to this method are required");
		}
	
		// check to ensure the path is valid
		if(doesFileExist(oldName) == false) {
			return false;
		}
		
		// check the new Name doesn't exist
		if(isValidFile(newName, true) == true) {
			return false;
		}
		
		File oldFile = new File(oldName);
		File newFile = new File(newName);
		
		if(oldFile.renameTo(newFile) == true) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * A method to retrieve a list of files based on a common extension
	 * from a given directory
	 *
	 * @param directory the directory to look inside for files
	 * @param extension the file extension to look for
	 */
	public static String[] getFileNameListByExtension(String directory, String extension) {
	
		// check on the parameters
		if(InputUtils.isValid(directory) == false || InputUtils.isValid(extension) == false) {
			throw new IllegalArgumentException("Both parameters to this method are required");
		}
		
		if(doesDirExist(directory) == false) {
			throw new IllegalArgumentException("Unable to locate the specified directory");
		}
		
		// get the list of files
		File dir = new File(directory);
		File[] files = dir.listFiles(new FileExtensionFilter(extension));
		
		// check on what was returned
		if(files.length == 0) {
			return new String[0];
		}
		
		// build a list of string
		String[] fileNames = new String[files.length];
		
		// loop through all of the files and get the file names
		for(int i = 0; i < files.length; i++) {
			try {
				fileNames[i] = files[i].getCanonicalPath();
			} catch (java.io.IOException ex) {
				return new String[0];
			}
		}		
		
		// return the list of file names
		return fileNames;
	
	}
	
	/**
	 * Get the name of the file from a given pathname
	 *
	 * @param path the path to the file
	 */
	public static String getFileName(String path) {
		
		File file = new File(path);
		return file.getName();
	}

/**
 * A class used to filter a list of files by extension
 */
static private class FileExtensionFilter implements FilenameFilter {

	// declare helper variables
	private String extension = null;
	
	/**
	 * Constructor for this class
	 *
	 * @param extension the extension used to filter the list of files
	 */
	public FileExtensionFilter(String extension) {
		if(InputUtils.isValid(extension) == false) {
			throw new IllegalArgumentException("The extension parameter is required");
		}
		
		this.extension = extension;	
	 }	
	
	/**
	 * Method to test if the specified file matches the predetermined
	 * extension requirements
	 *
	 * @param dir the directory in which the file was found.
	 * @param filename the name of the file
	 *
	 * @return true if and only if the file should be included
	 */
	public boolean accept(File dir, String filename) {
	
		if(filename != null) {
			if(filename.endsWith(extension) == true) {
				return true;
			} else {
				return false;
			}		
		} else {
			return false;
		}
	}
}
	
} // end class definition
