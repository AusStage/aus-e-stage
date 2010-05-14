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
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*; 
import javax.xml.transform.*; 
import javax.xml.transform.dom.*; 
import javax.xml.transform.stream.*;


/**
 * The AbsAgeBySex task is a task that opens an ABS Data File contain Age By Sex data
 * and constructs an intermediatary XML file for later processing
 */
public class AbsAgeBySex extends Tasks {

	/**
	 * Constructor for this class
	 *
	 * @param input  the input file
	 * @param output the output file
	 */
	public AbsAgeBySex(File input, File output) {
	
		super(input, output);
		
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
		
		// results
		int    average      = 0;
		String avgMaleAge   = null;
		String avgFemaleAge = null;
		String avgTotalAge  = null;
		
		// Data Element manage tasks
		DataElement dataElem = null;
		HashSet<DataElement> dataElements = new HashSet<DataElement>();
		
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
					
					// create a new dataElement and add it to the collection
					dataElem = new DataElement(district);
					dataElements.add(dataElem);
					
					// reset the people array
					people = new int[DATA_COLUMNS];
										
					// get the male populations
					for(int i = 0; i < DATA_COLUMNS; i++) {
						try {
							people[i] = Integer.parseInt(dataElems[i + 2]);
						} catch(NumberFormatException ex) {
							System.err.println("ERROR: Unable to parse Integer at Line: " + lineCount + " Column: " + (i + 2));
							return false;
						}
					}
					
					// calculate the average
					average = calculateAverage(people);
					
					// double check the average
					if(average > 0) {
						// get the average male age
						avgMaleAge = ages[average - 1];
					} else {
						avgMaleAge = "N/A";
					}
					
					// save the male average
					dataElem.setAvgMaleAge(avgMaleAge);
					
					// build and save the table
					dataElem.addHtml(buildTable("Male Age by 5 Year Age Groups (Place of Usual Residence)", people, ages));
					
					// reset the people array
					people = new int[DATA_COLUMNS];
					
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
					
					// calculate the average
					average = calculateAverage(people);
					
					// double check the average
					if(average > 0) {
						// get the average male age
						avgFemaleAge = ages[average - 1];
					} else {
						avgFemaleAge = "N/A";
					}
					
					// save the male average
					dataElem.setAvgFemaleAge(avgFemaleAge);
					
					// build and save the table
					dataElem.addHtml(buildTable("Female Age by 5 Year Age Groups (Place of Usual Residence)", people, ages));
					
					// reset the people array
					people = new int[DATA_COLUMNS];
					
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
					
					// calculate the average
					average = calculateAverage(people);
					
					// double check the average
					if(average > 0) {
						// get the average male age
						avgTotalAge = ages[average - 1];
					} else {
						avgTotalAge = "N/A";
					}
					
					// save the male average
					dataElem.setAvgTotalAge(avgTotalAge);
					
					// build and save the table
					dataElem.addHtml(buildTable("Total Population Age by 5 Year Age Groups (Place of Usual Residence)", people, ages));
					
					// reset the people array
					people = new int[DATA_COLUMNS];
					
					// keep the user 
					System.out.println("INFO: District: " + district + " Avg. Male Age: " + avgMaleAge + " Avg. Female Age: " + avgFemaleAge + " Avg. Population Age: " + avgTotalAge); 			
					
					// increment the line counter
					lineCount++;
				} // end data line check
			} // end while loop
		
		} catch(java.io.IOException ex) {
			System.err.println("ERROR: Unable to read from the specified file");
			return false;
		}
		
		// output the XML
		try {
			writeXmlDataset(dataElements, output);
		} catch (Exception ex) {
			System.err.println("ERROR: Unable to write the XML file");
			return false;
		}
	
		// debug code
		return true;
	
	} // end doTask method
	
	/**
	 * A method to calculate the average 
	 *
	 * @param values an array of values
	 *
	 * @return       the calculated average rounded to the nearest int
	 */
	private int calculateAverage(int[] values) {
	
		// average calculation
		float avgPartOne = 0;
		float avgPartTwo = 0;
		float average    = 0;
		
		// get the first part of the average
		for(int i = 0; i < values.length; i++) {
			avgPartOne += values[i] * (i + 1);
		}
		
		// get the second part of the average
		for(int i = 0; i < values.length; i++) {
			avgPartTwo += values[i];
		}
		
		// get the average
		average = avgPartOne / avgPartTwo;
		
		// double check the average
		if(average > 0) {
			// get the average male age
			return Math.round(average);
		} else {
			return 0;
		}
	} // end calculateAverage method
	
	/**
	 * A method to build the data table
	 *
	 * @param caption the caption of the table
	 * @param values  the values of the data row of the table
	 * @param headers the values of the header row
	 *
	 * @return the completed table
	 */
	private String buildTable(String caption, int[] values, String[] headers) {
	
		// declare helper variables
		StringBuilder table = new StringBuilder();
		
		// start the table
		table.append("<table>\n<caption>" + caption + "</caption>\n");
		table.append("<thead><tr>");
		
		// add the headers
		for(int i = 0; i < headers.length; i++) {
			table.append("<th>" + headers[i] + "</th>");
		}
		
		// finalise the header
		table.append("</tr></thead>\n");
		
		// start the body of the table
		table.append("<tbody><tr>");
		
		// add the values
		for(int i = 0; i < values.length; i++) {
			table.append("<td>" + Integer.toString(values[i]) + "</td>");
		}
		
		// finalse the table
		table.append("</tr></tbody>\n</table>\n");
		
		// return the generated table
		return table.toString();

	} // end buildTable method
	
	/**
	 * A method to build and write the XML file
	 *
	 * @param dataElements the data elements to use to build the dataset
	 * @param outputFile   the output file to write the XML to
	 */
	private void writeXmlDataset(HashSet<DataElement> dataElements, File outputFile) {
	
		// build and write the XML
		try {
			// create the xml document object
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder        builder = factory.newDocumentBuilder();
			Document			   xmlDoc  = builder.newDocument();
			
			// add the root element
			Element rootElement = xmlDoc.createElement(ROOT_SNIPPET_ELEMENT_NAME);
			xmlDoc.appendChild(rootElement);
			
			// get an iterator over the collection
			Iterator iterator = dataElements.iterator();
			
			// iterate over the contributors
			while(iterator.hasNext()) {
			
				// get a data element
				DataElement dataElem = (DataElement)iterator.next();
				
				// build the XML for this collection district
				Element district = xmlDoc.createElement(DISTRICT_ELEMENT_NAME);
				
				// add the collection district id
				district.setAttribute("id", dataElem.getId());
				
				// add the averages
				Element xmlElement = xmlDoc.createElement("AvgMaleAge");
				xmlElement.setTextContent(dataElem.getAvgMaleAge());
				district.appendChild(xmlElement);
				
				xmlElement = xmlDoc.createElement("AvgFemaleAge");
				xmlElement.setTextContent(dataElem.getAvgFemaleAge());
				district.appendChild(xmlElement);
				
				xmlElement = xmlDoc.createElement("AvgTotalAge");
				xmlElement.setTextContent(dataElem.getAvgTotalAge());
				district.appendChild(xmlElement);
				
				// add the description
				xmlElement = xmlDoc.createElement("table");
				xmlElement.appendChild(xmlDoc.createCDATASection(dataElem.getHtml()));
				district.appendChild(xmlElement);
				
				// add this element to the tree
				rootElement.appendChild(district);
			}
			
			// create a transformer 
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer        transformer  = transFactory.newTransformer();
			
			// set some options on the transformer
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			// get a transformer and supporting classes
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			DOMSource    source = new DOMSource(xmlDoc);
			
			// transform the xml document into a string
			transformer.transform(source, result);
			
			// open the output file
			FileWriter output = new FileWriter(outputFile);
			output.write(writer.toString());
			output.close();			
			
		} catch(javax.xml.parsers.ParserConfigurationException ex) {
			throw new RuntimeException("ERROR: Unable to build xml", ex);
		} catch(javax.xml.transform.TransformerException e) {
			throw new RuntimeException("ERROR: Unable to build xml", e);
		}catch (java.io.IOException ex) {
			throw new RuntimeException("ERROR: Unable to write xml file" + ex);
		}
			
	} // end buildXmlDataset

} // end class definition

/**
 * A class to manage the age by sex data for a collection district
 */
class DataElement {

	// declare private variables
	private String id;           // collection district id
	private String avgMaleAge;   // calculated average male age
	private String avgFemaleAge; // calculated average female age
	private String avgTotalAge;  // calculated total age
	private StringBuilder html;  // html version of the data including tables

	/**
	 * Constructor for this class
	 *
	 * @param id the collection district id
	 */
	public DataElement (String id) {
	
		this.id = id;
		
		html = new StringBuilder();
		
	} // end constructor
	
	/*
	 * get and set methods
	 */
	public String getId() {
		return id;
	}
		 
	public void setAvgMaleAge(String value) {
		avgMaleAge = value;
	}
	
	public String getAvgMaleAge() {
		return avgMaleAge;
	}
	
	public void setAvgFemaleAge(String value) {
		avgFemaleAge = value;
	}
	
	public String getAvgFemaleAge() {
		return avgFemaleAge;
	}
	
	public void setAvgTotalAge(String value) {
		avgTotalAge = value;
	}
	
	public String getAvgTotalAge() {
		return avgTotalAge;
	}
	
	/*
	 * html methods
	 */
	public void addHtml(String value) {
		html.append(value);
	}
	
	public String getHtml() {
		return html.toString();
	}
} // end class definition
