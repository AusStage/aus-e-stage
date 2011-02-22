/*
 * This file is part of the Aus-e-Stage Beta Website
 *
 * The Aus-e-Stage Beta Website is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The Aus-e-Stage Beta Website is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
 */
package au.edu.ausstage.beta;

// import the ausstage utilities
import au.edu.ausstage.utils.InputUtils;
import au.edu.ausstage.utils.FileUtils;

// import additional java libraries
import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
 
 
/**
 * A class to manage the manipulation of the XML report into HTML using XSL
 *
 * @author corey.wallis@flinders.edu.au
 */
public class AnalyticsManager {
	
	/**
	 * A method to take convert the XML representation of the report into HTML using XSL
	 *
	 * @param reportDirectory  the directory where the report files live
	 * @param reportXSLFile    the name of the report XSL file, assumed to be in the reportDirectory
	 * @param reportXMLFile    the name of the report XML file, assumed to be in the reportDirectory
	 *
	 * @return                 the result of the transformation as a HTML encoded string
	 */
	public static String processXMLReport(String reportDirectory, String reportXSLFile, String reportXMLFile) {
	
		// double check the parameters
		if(InputUtils.isValid(reportDirectory) == false || InputUtils.isValid(reportXSLFile) == false || InputUtils.isValid(reportXMLFile) == false) {
			throw new IllegalArgumentException("All of the parameters are required");
		}
		
		if(FileUtils.doesDirExist(reportDirectory) == false) {
			throw new IllegalArgumentException("The specified reportDirectory path is invalid");
		}
		
		if(FileUtils.doesFileExist(reportDirectory + reportXSLFile) == false) {
			throw new IllegalArgumentException("The specified XSL file path is invalid");
		}
		
		if(FileUtils.doesFileExist(reportDirectory + reportXMLFile) == false) {
			throw new IllegalArgumentException("The specified XML file path is invalid");
		}
		
		// declare XML & XSLT related variables
		TransformerFactory factory;     // transformer factory
		Transformer        transformer; // transformer object to do the transforming
		Source 			   xslSource;   // object to hold the XSL
		Source 			   xmlSource;   // object to hold the XML
		StringWriter       writer;		// object to receive output of transformation
		StreamResult       result;		// object to receive output of transformation
		String             htmlOutput;  // object to hold the HTML output
		
		// open the XSL and XML files
		File xslFile = new File(reportDirectory + reportXSLFile);
		File xmlFile = new File(reportDirectory + reportXMLFile);
		
		// set up the source objects for the transform
		try {
			// get a transformer factory
			factory = TransformerFactory.newInstance();
			
			// load the XSLT source
			xslSource = new StreamSource(xslFile);
			
			// load the XML
			xmlSource = new StreamSource(xmlFile);
			
			// get a transformer using the XSL as a source of instructions
			transformer = factory.newTransformer(xslSource);
			
			// get objects to handle the output of the transformation
			writer = new StringWriter();
			result = new StreamResult(writer);
			
			// do the transformation
			transformer.transform(xmlSource, result);
			
			// get the results
			htmlOutput = writer.toString();
			
		} catch (javax.xml.transform.TransformerException ex) {
			throw new RuntimeException("The transformation of the XML using XSL failed");
		}
		
		// hack to fix wrong entities in the html output
		htmlOutput = htmlOutput.replace("&lt;", "<");
		htmlOutput = htmlOutput.replace("&gt;", ">");
		
		return htmlOutput;
	}
}
