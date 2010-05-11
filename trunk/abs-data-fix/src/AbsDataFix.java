/*
 * This file is part of the AusStage ABS Data Fix App
 *
 * The AusStage ABS Data Fix App is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage ABS Data Fix App is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage ABS Data Fix App.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

// import additional packages
import java.io.*;


/**
 * Main driving class for the AusStage ABS Data Fix App
 */
public class AbsDataFix {

	/**
	 * Main driving method for the AusStage ABS Data Fix App
	 */
	public static void main(String args[]) {
	
		// declare helper variables
		String[] fixTypes = {"agebysex", "fix"};
		File inputFile;
		File outputFile;		
	
		// get the command line options
		SimpleCommandLineParser parser = new SimpleCommandLineParser(args); // use a Google class to do manage command line params
		
		// get the parameters
		String fixType    = parser.getValue("fix", "fixtype");
		String input  = parser.getValue("input");
		String output = parser.getValue("output");
		
		// check on the parameters
		if(fixType == null || input == null || output == null) {
			// output usage instructions
			System.err.println("ERROR: the following parameters are expected");
			System.err.println("-fixtype   the type of fix to undertake");
			System.err.println("-input     the input file of ABS Data");
			System.err.println("-output    the output file\n");
			System.err.println("Valid fix types are:");
			System.err.println(java.util.Arrays.toString(fixTypes).replaceAll("[\\]\\[]", ""));
			System.exit(-1);
		}
		
		// check on the fix type
		boolean validFixType = false;
		
		for(int i = 0; i < fixTypes.length; i++) {
			if(fixTypes[i].equals(fixType)) {
				validFixType = true;
			}
		}
		
		if(validFixType != true) {
			System.err.println("ERROR: the specified fix type is invalid");
			System.err.println("Valid fix types are:");
			System.err.println(java.util.Arrays.toString(fixTypes).replaceAll("[\\]\\[]", ""));
			System.exit(-1);
		}
		
		// check the input file
		inputFile = new File(input);
		
		if(inputFile.isFile() != true) {
			System.err.println("ERROR: Unable to locate the input file");
			System.exit(-1);
		}
		
		if(inputFile.canRead() == false) {
			System.err.println("ERROR: Unable to access the input file");
			System.exit(-1);
		}		
		
		// check the output file
		outputFile = new File(output);
			
		if(outputFile.isFile() == true) {
			System.err.println("ERROR: Output file already exists, refusing to delete / overwrite");
			System.exit(-1);
		} else {
			// try to create the file
			try {
				boolean stat = outputFile.createNewFile();
				if(stat == false) {
					System.err.println("ERROR: Unable to create the output file");
					System.exit(-1);
				}
			} catch (java.io.IOException ex) {
				System.err.println("ERROR: Unable to create the output file");
				System.err.println("Exception details are: " + ex.toString());
				System.exit(-1);
			}
		}
		
		// undertake the required task
		if(fixType.equals("agebysex") == true) {
			// undertake the age by sex task
			System.out.println("INFO: Undertaking the Age By Sex task");
			
			AbsAgeBySex task = new AbsAgeBySex(inputFile, outputFile);
			
			// run the task
			boolean stat = task.doTask();
			
			if(stat == false) {
				System.err.println("ERROR: The specified task has failed, see previous error messae for details");
				System.exit(-1);
			}
		}
		
	} // end main method

} // end class definition
