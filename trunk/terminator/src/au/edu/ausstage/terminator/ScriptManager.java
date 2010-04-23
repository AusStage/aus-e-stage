/*
 * This file is part of the AusStage Terminator Service
 *
 * The AusStage Terminator Service is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage Terminator Service is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Terminator Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/ 

package au.edu.ausstage.terminator;

// import additional packages
import javax.servlet.ServletConfig;
import java.io.*;

/**
 * A class to manage all aspects of the scripts that can be executed
 */
public class ScriptManager {

	// declare private variables
	private ServletConfig servletConfig = null;

	/** 
	 * Constructor for this class
	 *
	 * @param config the ServletConfig object for this servlet
	 */
	public ScriptManager(ServletConfig config) {
		// store reference for resuse
		servletConfig = config;
	}
	
	/**
	 * a method to build a list of script available for use
	 *
	 * @return the list of scripts as a HTML list
	 */
	public String getScriptList() {
	
		// get the list of scripts
		String scriptList = servletConfig.getServletContext().getInitParameter("scriptList");
		
		// check on the list of scripts
		if(scriptList == null) {
			throw new RuntimeException("Unable to load scriptList init parameter");
		}
		
		String[] scripts = scriptList.split("\\|");
	
		// start the list
		StringBuilder htmlList = new StringBuilder("<ul>");
		
		// build the list of scripts
		for(int i = 0; i < scripts.length; i++) {
			htmlList.append("<li><a href=\"javascript:doScript(" + i + ");\" title=\"Execute this Script\">" + scripts[i] + "</a></li>");
		}
		
		// finlise the list
		htmlList.append("</ul>");
		
		// return the string
		return htmlList.toString();	
	
	} // end getScriptList method
	
	/**
	 * a method to execute a script identified by its id number
	 *
	 * @param id the id number of the script to execute
	 * 
	 * @return   text indicating the status of the executing
	 */
	public String executeById(String id) {
	
		// check on the parameter
		if(id == null) {
			throw new RuntimeException("Missing id parameter");
		}
		
		try {
			Integer.parseInt(id);
		} catch (NumberFormatException ex) {
			throw new RuntimeException("ID parameter must be an integer");
		}
		
		// get the path to this script
		String scriptPath = servletConfig.getServletContext().getInitParameter("script-" + id);
		
		// check on the script path
		if(scriptPath == null) {
			throw new RuntimeException("Unable to load scriptPath init parameter for the specified id");
		}
		
		// check on the script to be executed
		File scriptFile = new File(scriptPath);
		
		try {
			// is it a file?
			if(scriptFile.isFile() == false) {
				throw new RuntimeException("The script with the specified id could not be found");
			}
			
			// can it be executed?
			if(scriptFile.canExecute() == false) {
				throw new RuntimeException("The script with the specified id is not able to be executed");
			}
			
		} catch (SecurityException ex) {
			throw new RuntimeException("A security exception was detected while checking the file", ex);
		}

		// declare helper variables
		String         lineFromScript;
		BufferedReader outputFromScript;
		StringBuilder  scriptOutput = new StringBuilder();

		// try to execute the file		
		try {
		
			// execute the script and capture its output
			Process newProcess = Runtime.getRuntime().exec(scriptFile.getCanonicalPath());
			
			// get a stream reader to capture the script output
			outputFromScript = new BufferedReader (new InputStreamReader(newProcess.getInputStream())); 
			
			// get the output from the script
			while ((lineFromScript = outputFromScript.readLine()) != null) { 
				scriptOutput.append(lineFromScript + "\n");
			}
			
			// close the stream reader
			outputFromScript.close();
			
		} catch (IOException ex) {
			throw new RuntimeException("Unable to execute the specified script", ex);
		}
		
		// return the output from the script
		return scriptOutput.toString();
	
	} // end executeById method

} // end class definition
