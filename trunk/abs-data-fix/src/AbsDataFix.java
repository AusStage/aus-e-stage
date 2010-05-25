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

	// Store the version information 
	private static final String VERSION    = "1.0.2";
	private static final String BUILD_DATE = "2010-05-25";
	private static final String INFO_URL   = "http://code.google.com/p/aus-e-stage/wiki/AbsDataFix";

	/**
	 * Main driving method for the AusStage ABS Data Fix App
	 */
	public static void main(String args[]) {
	
		// output some basic information
		System.out.println("AbsDataFix - Use ABS data to build overlays for AusStage Maps");
		System.out.println("Version: " + VERSION + " Build Date: " + BUILD_DATE);
		System.out.println("More Info: " + INFO_URL + "\n");
	
		// declare helper variables
		String[] fixTypes     = {"agebysex", "databuilder", "appendcdinfo", "prepkml", "mapagebysex"};
		String[] ageBySexSets = {"male", "female", "total"};
		File inputFile;
		File outputFile;		
	
		// get the command line options
		SimpleCommandLineParser parser = new SimpleCommandLineParser(args); // use a Google class to do manage command line params
		
		// get the parameters
		String fixType = parser.getValue("fix", "fixtype");
		String input   = parser.getValue("input");
		String output  = parser.getValue("output");
		String codes   = parser.getValue("codes");
		String dataset = parser.getValue("dataset");
		
		// check on the parameters
		if(fixType == null || input == null || output == null) {
			// output usage instructions
			System.err.println("ERROR: the following parameters are expected");
			System.err.println("-fixtype   the type of fix to undertake");
			System.err.println("-input     the input file of ABS Data");
			System.err.println("-output    the output file");
			System.err.println("-codes     (optional) a file containing codes such as district ids or colour codes");
			System.err.println("-dataset   (optional) the identifer for the dataset to map\n");
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
		
		// keep track of the status of the task
		boolean stat = false;
			
		// undertake the required task
		if(fixType.equals("agebysex") == true) {
			// undertake the age by sex task
			System.out.println("INFO: Undertaking the Age By Sex task");
			
			AbsAgeBySex task = new AbsAgeBySex(inputFile, outputFile);
			
			// run the task
			stat = task.doTask();			
		} else if(fixType.equals("databuilder") == true) {
			// undertake the data builder task
			System.out.println("INFO: Undertaking the Data Builder task");
			
			DataBuilder task = new DataBuilder(inputFile, outputFile);
			
			// run the task
			stat = task.doTask();
		} else if(fixType.equals("appendcdinfo") == true) {
			// undertake the append collection district info task
			System.out.println("INFO: Undertaking the Append CD Info task");
			
			// check on the additional parameters
			if(codes == null) {
				System.err.println("ERROR: the following additional parameter was expected");
				System.err.println("-codes the MID file listing the collection district code information");
				System.exit(-1);
			}
			
			// check the input file
			File codesFile = new File(codes);
	
			if(codesFile.isFile() != true) {
				System.err.println("ERROR: Unable to locate the collection district MID file");
				System.exit(-1);
			}
	
			if(codesFile.canRead() == false) {
				System.err.println("ERROR: Unable to access the collection district MID file");
				System.exit(-1);
			}
			
			AppendCDInfo task = new AppendCDInfo(inputFile, outputFile, codesFile);
			
			// run the task
			stat = task.doTask();
		} else if(fixType.equals("prepkml") == true) {
			// undertake the required task
			System.out.println("INFO: Undertaking the Prepare KML task");
			
			PrepareKML task = new PrepareKML(inputFile, outputFile);
			
			// run the task
			stat = task.doTask();
		} else if(fixType.equals("mapagebysex") == true) {
			// undertake the required task
			System.out.println("INFO: Undertaking building maps for the Age By Sex dataset");
			
			// check on the additional parameters
			if(codes == null) {
				System.err.println("ERROR: the following additional parameter was expected");
				System.err.println("-codes the XML file containing the age by sex dataset");
				System.exit(-1);
			}
			
			// check the input file
			File codesFile = new File(codes);
	
			if(codesFile.isFile() != true) {
				System.err.println("ERROR: Unable to locate the XML file containing the age by sex dataset");
				System.exit(-1);
			}
	
			if(codesFile.canRead() == false) {
				System.err.println("ERROR: Unable to access the XML file containing the age by sex dataset");
				System.exit(-1);
			}
			
			// check on the datset parameter
			if(dataset == null) {
				System.err.println("ERROR: You must specify the dataset to map, valid sets are:");
				System.err.println(java.util.Arrays.toString(ageBySexSets).replaceAll("[\\]\\[]", ""));
				System.exit(-1);
			}
			
			// check on the dataset
			boolean validSet = false;
	
			for(int i = 0; i < ageBySexSets.length; i++) {
				if(ageBySexSets[i].equals(dataset)) {
					validSet = true;
				}
			}
			
			if(validSet == false) {
				System.err.println("ERROR: Invalid dataset specified, valid sets are:");
				System.err.println(java.util.Arrays.toString(ageBySexSets).replaceAll("[\\]\\[]", ""));
				System.exit(-1);
			}
			
			// keep the user informed
			System.out.println("INFO: Working with the \"" + dataset + "\" dataset");
			
			MapAgeBySex task = new MapAgeBySex(inputFile, outputFile, codesFile);
			task.setDataSet(dataset);
			
			// run the task
			stat = task.doTask();
		}
		
		// check on the status of this task
		if(stat == false) {
			System.err.println("ERROR: The specified task has failed, see previous error message for details");
			System.exit(-1);
		} else {
			System.out.println("INFO: Output file successfully created at:\n" + outputFile.getAbsolutePath());
			System.exit(0);
		}
		
	} // end main method

} // end class definition
