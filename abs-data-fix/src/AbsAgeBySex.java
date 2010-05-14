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
 * The AbsAgeBySex task is a task that opens an ABS Data File contain Age By Sex data
 * and constructs an intermediatary XML file for later processing
 */
public class AbsAgeBySex {

	// declare private variables
	File input;
	File output;

	/**
	 * Constructor for this class
	 *
	 * @param input  the input file
	 * @param output the output file
	 */
	public AbsAgeBySex(File input, File output) {
	
		// check on the parameters
		if(input == null || output == null) {
			throw new IllegalArgumentException("ERROR: This constructor requires valid file object as parameters");
		} else {
			this.input  = input;
			this.output = output;
		}
		
	} // end constructor
	
	/**
	 * A public class to undertake the required task
	 *
	 * @return true, if and only if the task completes successfully
	 */
	public boolean doTask() {
			
		/*
		 * declare helper variables
		 */
		// file manipulation
		BufferedReader inputReader = null;
		String[] dataElems;
		String   dataLine;
		
		// data format 
		final int SKIP_LINES = 9;
		final int ELEM_LENGTH = 65;
		final int DATA_COLUMNS = 21;
		
		// counters
		int lineCount = 1;
		
		// data storage
		String   district = null;
		int[]    people = new int[DATA_COLUMNS];
		String[] ages = {"0-4 years","5-9 years","10-14 years","15-19 years","20-24 years","25-29 years","30-34 years","35-39 years","40-44 years","45-49 years","50-54 years","55-59 years","60-64 years","65-69 years","70-74 years","75-79 years","80-84 years","85-89 years","90-94 years","95-99 years","100 years and over"};
		
		// average calculation
		float avgPartOne = 0;
		float avgPartTwo = 0;
		float average    = 0;
		
		// results
		String avgMaleAge   = null;
		String avgFemaleAge = null;
		String avgTotalAge  = null;
		
		// open the input file
		try {
			// open the log file
			inputReader = new BufferedReader(new FileReader(input));
		} catch(FileNotFoundException ex) {
			System.err.println("ERROR: Unable to open specified file for reading");
			return false;
		}
		
		// keep the user informed
		System.out.println("INFO: Skipping lines 1 - " + SKIP_LINES);
		
		// skip the required number of lines
		for(int i = 1; i <= SKIP_LINES; i++) {
			try{
				inputReader.readLine();
			} catch(java.io.IOException ex) {
				System.err.println("ERROR: Unable to read from the specified file");
				return false;
			}
			
			// increment line count
			lineCount++;
		}
		
		// read the data
		try {
		
			// loop through the file line by line
			while((dataLine = inputReader.readLine()) != null) {
			
				// split the line into elements
				dataElems = dataLine.split(",");
				
				// check on the line
				if(dataLine.startsWith(",\"") == false || dataLine.startsWith(",\"Total") == true) {
					// not a data line
					System.out.println("INFO: Skiping line: " + lineCount);
					
					// increment the line counter
					lineCount++;
				} else {
					// process this line
					
					// get the district
					district = dataElems[1];
					district = district.replaceAll("\"", "");
					
					// reset the people array
					people = new int[DATA_COLUMNS];
					
					// reset the average variables
					avgPartOne = 0;
					avgPartTwo = 0;
					average    = 0;
										
					// get the male populations
					for(int i = 0; i < DATA_COLUMNS; i++) {
						try {
							people[i] = Integer.parseInt(dataElems[i + 2]);
						} catch(NumberFormatException ex) {
							System.err.println("ERROR: Unable to parse Integer at Line: " + lineCount + " Column: " + (i + 2));
							return false;
						}
					}
					
					// get the first part of the average
					for(int i = 0; i < people.length; i++) {
						avgPartOne += people[i] * (i + 1);
					}
					
					// get the second part of the average
					for(int i = 0; i < people.length; i++) {
						avgPartTwo += people[i];
					}
					
					// get the average
					average = avgPartOne / avgPartTwo;
					
					// double check the average
					if(average > 0) {
						// get the average male age
						avgMaleAge = ages[(Math.round(average) - 1)];
					} else {
						avgMaleAge = "N/A";
					}
					
					// reset the people array
					people = new int[DATA_COLUMNS];
					
					// reset the average variables
					avgPartOne = 0;
					avgPartTwo = 0;
					average    = 0;
					
					/*
					 * get the female average age
					 */
					// get the female populations
					for(int i = 0; i < DATA_COLUMNS; i++) {
						try {
							people[i] = Integer.parseInt(dataElems[i + DATA_COLUMNS + 2]);
						} catch(NumberFormatException ex) {
							System.err.println("ERROR: Unable to parse Integer at Line: " + lineCount + " Column: " + (i + 2));
							return false;
						}
					}
					
					// get the first part of the average
					for(int i = 0; i < people.length; i++) {
						avgPartOne += people[i] * (i + 1);
					}
					
					// get the second part of the average
					for(int i = 0; i < people.length; i++) {
						avgPartTwo += people[i];
					}
					
					// get the average
					average = avgPartOne / avgPartTwo;
					
					// double check the average
					if(average > 0) {
						// get the average male age
						avgFemaleAge = ages[(Math.round(average) - 1)];
					} else {
						avgFemaleAge = "N/A";
					}
					
					// reset the people array
					people = new int[DATA_COLUMNS];
					
					// reset the average variables
					avgPartOne = 0;
					avgPartTwo = 0;
					average    = 0;
					
					/*
					 * get the total population average age
					 */
					// get the total populations
					for(int i = 0; i < DATA_COLUMNS; i++) {
						try {
							people[i] = Integer.parseInt(dataElems[i + DATA_COLUMNS + DATA_COLUMNS + 2]);
						} catch(NumberFormatException ex) {
							System.err.println("ERROR: Unable to parse Integer at Line: " + lineCount + " Column: " + (i + 2));
							return false;
						}
					}
					
					// get the first part of the average
					for(int i = 0; i < people.length; i++) {
						avgPartOne += people[i] * (i + 1);
					}
					
					// get the second part of the average
					for(int i = 0; i < people.length; i++) {
						avgPartTwo += people[i];
					}
					
					// get the average
					average = avgPartOne / avgPartTwo;
					
					// double check the average
					if(average > 0) {
						// get the average male age
						avgTotalAge = ages[(Math.round(average) - 1)];
					} else {
						avgTotalAge = "N/A";
					}
					
					// keep the user informed
					System.out.println("INFO: District: " + district + " Avg. Male Age: " + avgMaleAge + " Avg. Female Age: " + avgFemaleAge + " Avg. Population Age: " + avgTotalAge); 			
					
					// increment the line counter
					lineCount++;
				} // end data line check
			} // end while loop
		
		} catch(java.io.IOException ex) {
			System.err.println("ERROR: Unable to read from the specified file");
			return false;
		}
	
		// debug code
		return true;
	
	} // end doTask method

} // end class definition
