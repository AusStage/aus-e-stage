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

//import java.io.*;
//
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

//import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * A class used to execute an XPath expression against a given XML file
 */

public class XMLDataManager {

	/**
	 * A method to execute an XPath expression against a supplied XML file and
	 * return a NodeList result
	 * 
	 * @param xmlFile
	 *            the XML file to execute the XPath expression against
	 * @param xpathStr
	 *            the XPath expression to execute
	 * 
	 * @return the NodeList result of the query
	 */
	public NodeList executeQuery(String xmlFile, String xpathStr)
			throws Exception {

		InputSource xml = new InputSource(xmlFile);   
		
		// Create a new XPath
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        
        Object result = null;
        try {
          // compile the XPath expression
          XPathExpression xpathExpr = xpath.compile(xpathStr);
          
          // Evaluate the XPath expression against the input document
          result = xpathExpr.evaluate(xml, XPathConstants.NODESET);
                    
          // Print the result to System.out.
          //printResult(result);
          
        }
        catch (Exception e) {
          e.printStackTrace();
        }        
	    //return result;
		return (NodeList)result;

	} // end executeQuery method

	
//    /**
//     * Print the type and value of the evaluation result.
//     */
//    static void printResult(Object result)
//      throws Exception    {
//        if (result instanceof Double) {
//            System.out.println("Result type: double");
//            System.out.println("Value: " + result);
//        }
//        else if (result instanceof Boolean) {
//            System.out.println("Result type: boolean");
//            System.out.println("Value: " + ((Boolean)result).booleanValue());
//        }	
//        else if (result instanceof String) {
//            System.out.println("Result type: String");
//             System.out.println("Value: " + result);
//        }
//        else if (result instanceof Node) {
//            Node node = (Node)result;
//            System.out.println("Result type: Node");
//            System.out.println("<output>");
//            printNode(node);
//            System.out.println("</output>");
//        }
//        else if (result instanceof NodeList) {
//            NodeList nodelist = (NodeList)result;
//            System.out.println("Result type: NodeList");
//            System.out.println("<output>");
//            printNodeList(nodelist);
//            System.out.println("</output>");
//        }
//    }
//          
//	/** Decide if the node is text, and so must be handled specially */
//	static boolean isTextNode(Node n) {
//		if (n == null)
//			return false;
//		short nodeType = n.getNodeType();
//		return nodeType == Node.CDATA_SECTION_NODE
//				|| nodeType == Node.TEXT_NODE;
//	}
//
//	static void printNode(Node node) throws Exception {
//		if (isTextNode(node)) {
//			System.out.println(node.getNodeValue());
//		} else {
//			// Set up an identity transformer to use as serializer.
//			Transformer serializer = TransformerFactory.newInstance()
//					.newTransformer();
//			serializer
//					.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//			serializer.transform(new DOMSource(node), new StreamResult(
//					new OutputStreamWriter(System.out)));
//		}
//
//	}
//    
//	static void printNodeList(NodeList nodelist) 
//    throws Exception
//    {
//    	Node n;
//
//    	// Set up an identity transformer to use as serializer.
//    	Transformer serializer = TransformerFactory.newInstance().newTransformer();
//    	serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//
//    	for (int i = 0; i < nodelist.getLength(); i++)
//    	{         
//    		n = nodelist.item(i);
//    		if (isTextNode(n)) {
//    			// DOM may have more than one node corresponding to a 
//    			// single XPath text node.  Coalesce all contiguous text nodes
//    			// at this level
//    			StringBuffer sb = new StringBuffer(n.getNodeValue());
//    			for (
//    					Node nn = n.getNextSibling(); 
//    					isTextNode(nn);
//    					nn = nn.getNextSibling()
//    			) {
//    				sb.append(nn.getNodeValue());
//    			}
//    			System.out.print(sb);
//    		}
//    		else {
//    			serializer.transform(new DOMSource(n), new StreamResult(new OutputStreamWriter(System.out)));
//    		}
//    		System.out.println();
//    	}
//    }
	
//	public static void main(String [] args) throws Exception{
//	
//		File xmlFile = null;
//		String xpathStr = null;
//		
//		if (args.length != 2) {
//			System.out.println("Please give the correct number of args");
//			return;
//		}
//		if ((args[0] != null) && (args[0].length() > 0)
//				            && (args[1] != null) && (args[1].length() > 0)) {
//						
//			xmlFile = new File(args[0]);
//			xpathStr = args[1];
//		
////			xmlFile = "foo.xml";
////			xpathStr ="/doc/name";
//			
//			XMLDataManager app = new XMLDataManager();
//			app.executeComplexQuery(xmlFile, xpathStr);
//			//app.executeSimpleQuery(xmlFile, xpathStr);
//			
//		} else {
//			System.out.println("args is null or length is 0");
//		}
//	}
}
