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

// define the package
package au.edu.ausstage.beta;

// import additional classes
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

// import the XML / XSLT processing packages
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

/**
 * A class used to generate the analytics pages for the root website
 */
public class AnalyticsServlet extends HttpServlet {

	// declare private variables
	private ServletConfig servletConfig;
	
	// declare XML & XSLT related variables
	TransformerFactory factory;     // transformer factory
	Transformer        transformer; // transformer object to do the transforming
	Source 			   xslSource;   // object to hold the XSL
	Source 			   xmlSource;   // object to hold the XML
	File 			   xslFile;     // file object for the XSL
	File 			   xmlFile;     // file object for the XML
	StringWriter       writer;		// object to receive output of transformation
	StreamResult       result;		// object to receive output of transformation
	
	// declare other private variables
	String htmlOutput;
	

	/**
	 * initialise this servlet
	 *
	 * @param conf the ServletConfig object for this container
	 */
	public void init(ServletConfig conf) throws ServletException {
	
		// execute the parent objects init method
		super.init(conf);
		
		// store a reference to this servlet config
		servletConfig = conf;
	
	} // end init method
	
	/**
	 * Method to respond to a get request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// get what type of data is incoming
		String type = request.getParameter("type");
		
		// double check the paramerter
		if(type == null) {
			// invalid source
			throw new ServletException("Missing Type Parameter");
		}
		
		// determine what analytics to produce
		if(type.equals("exchange")) {
			// AusStage Exchange Service Analytics
			
			// get the file paths
			String xsltPath = servletConfig.getServletContext().getInitParameter("analyticsXslt");
			String xmlPath  = servletConfig.getServletContext().getInitParameter("analyticsXml");
			
			// check on the parameters
			if(xsltPath == null || xmlPath == null) {
				throw new ServletException("Unable to read required parameters from Servlet Config");
			}
			
			if(xsltPath.equals("") || xmlPath.equals("")) {
				throw new ServletException("Missing required parameters from Servlet Config");
			}
			
			// open the XSL file
			try {
				xslFile = new File(xsltPath);
				
				// check to ensure we can read this file
				if(xslFile.canRead() == false) {
					throw new ServletException("Unable to read XSLT file");
				}
				
			} catch (SecurityException ex) {
				throw new ServletException("Unable to read XSLT file");
			}
			
			// open the XML file
			try {
				xmlFile = new File(xmlPath);
				
				// check to ensure we can read this file
				if(xmlFile.canRead() == false) {
					throw new ServletException("Unable to read XSLT file");
				}
				
			} catch (SecurityException ex) {
				throw new ServletException("Unable to read the XML file");
			}
			
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
				throw new ServletException("Unable to transform the XML into HTML");
			}
			
			// hack to fix wrong entities in the html output
			htmlOutput = htmlOutput.replace("&lt;", "<");
			htmlOutput = htmlOutput.replace("&gt;", ">");
			
			// ouput the XML
			// set the appropriate content type
			response.setContentType("text/plain; charset=UTF-8");
			
			// set the appropriate headers to disable caching
			// particularly for IE
			response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
			
			//get the output print writer
			PrintWriter out = response.getWriter();
			
			// send some output
			out.print(htmlOutput);
			
		} else {
			// invalid type
			throw new ServletException("Invalid Type Detected");
		}
	
	} // end doGet method
	
	/**
	 * Method to respond to a post request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		throw new ServletException("Inavlid request type");
	
	} // end doPost method


} // end class defintion
