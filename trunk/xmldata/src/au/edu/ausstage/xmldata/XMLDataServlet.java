/*
 * This file is part of the AusStage XML Data Manager Service
 *
 * The AusStage XML Data Manager Service is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage XML Data Manager Service is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage XML Data Manager Service. 
 * If not, see <http://www.gnu.org/licenses/>.
 */

package au.edu.ausstage.xmldata;

//import additional classes
import java.io.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The main driving class for the this service
 */
public class XMLDataServlet extends HttpServlet {
	
	// declare private variables
	private ServletConfig servletConfig;
	
       
     /*
	 * initialise this instance
	 */
	public void init(ServletConfig conf) throws ServletException {
		// execute the parent objects init method
		super.init(conf);
		
		// store configuration for later
		servletConfig = conf;
		
	} // end init method

	/**
	 * Method to respond to a get request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// get the parameters
		String xmlFileName = request.getParameter("file");  // parameter to name which XML file to query
		String xPathString = request.getParameter("xpath"); // xpath parameter to use as the query

		// check the parameters

		/*
		 * Check for null values
		 * trim the parameters
		 * check for empty strings
		 */
		xmlFileName = xmlFileName.trim();
		xPathString = xPathString.trim();
		
		if ((xmlFileName == null) || (xmlFileName.length() == 0)
				&& (xPathString == null) || (xPathString.length() == 0)) {
			throw new IllegalArgumentException(
            	"null argument or its length is 0 in method call.");
		}
		// build file path

		/*
		 * need to take into account the path seperator as well before joining
		 * the two variables
		 */

		String fullPathToXML = null;
		String rootDirectory = servletConfig.getServletContext()
				.getInitParameter("rootDirectory");

		if (rootDirectory.endsWith(File.separator))
			// add root directory to file name
			fullPathToXML = rootDirectory.trim() + xmlFileName;
		else
			fullPathToXML = rootDirectory.trim() + File.separator + xmlFileName;

		System.out.println("fullPathToXML:============= " + fullPathToXML);
		System.out.println("xPathString: =============" + xPathString);
		// check the file
		//File xmlFile = new File(fullPathToXML);

		/*
		 * check that the file exists check that the file can be read
		 */

		// get an instance of the XMLDataManager object
		XMLDataManager dataManager = new XMLDataManager();

		// execute the query against the XML file and get the results
		String resultStr = null;
		NodeList resultNodeList = null;
				
		try {
			
			resultNodeList = dataManager.executeQuery(fullPathToXML,
					xPathString);
			if (resultNodeList == null || resultNodeList.getLength() == 0) 
	             resultStr = "<output>Query result is Null or empty!</output> ";
			else 
				 resultStr = serialize(resultNodeList);
			
			System.out.println(resultStr);
			// return the results to the browser
			// set the appropriate content type
			response.setContentType("text/xml; charset=UTF-8");

			// set the appropriate headers to disable caching
			// particularly for IE
			response.setHeader("Cache-Control",
					"max-age=0,no-cache,no-store,post-check=0,pre-check=0");

			// get the output print writer
			PrintWriter out = response.getWriter();

			// send some output
			out.print(resultStr);
			out.close();
	        out.flush();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	} // end doGet method

	/** Decide if the node is text, and so must be handled specially */
	protected boolean isTextNode(Node n) {
		if (n == null)
			return false;
		short nodeType = n.getNodeType();
		return nodeType == Node.CDATA_SECTION_NODE
				|| nodeType == Node.TEXT_NODE;
	}
	

	/**
	 * Serialise the supplied W3C DOM subtree.
	 * 
	 * @param nodeList The DOM subtree as a NodeList.
	 * @throws DOMException Unable to serialise the DOM.
	 */
	protected String serialize(NodeList nodeList) {

		if (nodeList == null) {
			throw new IllegalArgumentException(
					"null 'subtree' NodeIterator arg in method call.");
		}
		
		StringWriter writer = new StringWriter();
		writer.write("<output>");
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer;

			factory.setAttribute("indent-number", new Integer(4));

			transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			int listLength = nodeList.getLength();

			// Iterate through the Node List.
			for (int i = 0; i < listLength; i++) {
				Node node = nodeList.item(i);

				if (isTextNode(node)) {
					writer.write(node.getNodeValue());
					writer.write(" ");
				} else if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
					writer.write(((Attr) node).getValue());
					writer.write(" ");
				} else if (node.getNodeType() == Node.ELEMENT_NODE) {
					transformer.transform(new DOMSource(node),
							new StreamResult(writer));
				}
			}
		} catch (TransformerConfigurationException tce) {
			
			System.out.println("* Transformer Factory error:");
			System.out.println("  " + tce.getMessage());

			// Use the contained exception, if any
			Throwable x = tce;
			if (tce.getException() != null)
				x = tce.getException();
			x.printStackTrace();
		} catch (TransformerException te) {
			
			System.out.println("* Transformation error:");
			System.out.println("  " + te.getMessage());

			// Use the contained exception, if any
			Throwable x = te;
			if (te.getException() != null)
				x = te.getException();
			x.printStackTrace();

		} catch (Exception e) {
			
			DOMException domExcep = new DOMException(
					DOMException.INVALID_ACCESS_ERR,
					"Unable to serailise DOM subtree.");
			domExcep.initCause(e);
			throw domExcep;
		}
        writer.write("</output>");
        return writer.toString();
    }// end serialize method
	
	/**
	 * Method to respond to a post request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// shouldn't use post requests with this service
		throw new ServletException("Incorrect request type detected");

	}// end doPost method


} // end class definition

