/*
 * This file is part of the AusStage Mapping Service
 *
 * The AusStage Mapping Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Mapping Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.mapping;

// import additional libraries
import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*; 
import javax.xml.transform.*; 
import javax.xml.transform.dom.*; 
import javax.xml.transform.stream.*; 

/**
 * A class used to represent and manipulate a KML export file
 *

 */
public class KMLExportFile {

	// declare private class variables
	private DocumentBuilderFactory factory;
	private	DocumentBuilder        builder;
	private DOMImplementation      domImpl;
	private Document               xmlDoc;
	private Element                rootElement;
	private Element				   folderElement = null;
	private Element				   firstDocElement = null; 

	/**
	 * Constructor for this class
	 */
	public KMLExportFile() throws java.lang.Exception {
	
		try {
			// create the xml document builder factory object
			factory = DocumentBuilderFactory.newInstance();
			
			// set the factory to be namespace aware
			factory.setNamespaceAware(true);
			
			// create the xml document builder object and get the DOMImplementation object			
			builder = factory.newDocumentBuilder();
			domImpl = builder.getDOMImplementation();
			
			// create a document with the appropriare default namespace
			xmlDoc = domImpl.createDocument("http://www.opengis.net/kml/2.2", "kml", null);
			
			// get the root element
			rootElement = this.xmlDoc.getDocumentElement();
			
			// add atom namespace to the root element
			rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:atom", "http://www.w3.org/2005/Atom");
			
			// add schema namespace to the root element
			rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			
			// add reference to the kml schema
			rootElement.setAttribute("xsi:schemaLocation", "http://www.opengis.net/kml/2.2 http://schemas.opengis.net/kml/2.2.0/ogckml22.xsd");	
		} catch(Exception ex) {
			throw new java.lang.Exception ("Unable to construct an KMLExportFile object", ex);
		}
	
	} // end constructor
	
	/**
	 * A method to create an element to be added to this KML export file
	 * 
	 * @param name the name of the element in the XML tree
	 *
	 * @return    an Element object
	 */
	public Element createElement(String name) throws java.lang.Exception{
		try {	
			// add a comment to the root node		
			return xmlDoc.createElement(name);
		} catch (Exception ex) {
			throw new java.lang.Exception ("Unable to create an XML element", ex);
		}
	} // end createElement method
	
	/**
	 * A method to create a CDATASection element to be added to this KML export file
	 *
	 * @param content the content of this element
	 *
	 * @return       the CDATASection element
	 */
	public CDATASection createCDATASection(String content) throws java.lang.Exception {
	
		try {	
			// add a comment to the root node		
			return xmlDoc.createCDATASection(content);
		} catch (Exception ex) {
			throw new java.lang.Exception ("Unable to create an CDATASection element", ex);
		}
	} // end createCDATASection method
	
	/**
	 * A method to create a TimeSpan element to be added to this KML export file
	 *
	 * @param begin the date for the begin element inside this TimeSpan element
	 * @param end   the date for the end element inside this TimeSpan Element
	 *
	 * @return    an Element object
	 */
	public Element createTimeSpanElement(String begin, String end) throws java.lang.Exception {
	
		try {
			// create the element
			Element timeSpan  = this.createElement("TimeSpan");
			
			// add the begin element
			Element timeBegin = this.createElement("begin");
			timeBegin.setTextContent(begin);
			timeSpan.appendChild(timeBegin);
			
			// add the end element
			Element timeEnd = this.createElement("end");
			
			if(!end.equals("")) {
				// set end element to actual known end						
				timeEnd.setTextContent(end);
			} else {
				// set end element to same as begin
				timeEnd.setTextContent(begin);
			}
			
			// add the end element
			timeSpan.appendChild(timeEnd);
			
			// return the new element
			return timeSpan;
		} catch (Exception ex) {
			throw new java.lang.Exception ("Unable to create an CDATASection element", ex);
		}	
	
	} // end createTimeSpanElement method
	
	/**
	 * A method to add a comment element to the to the KML file
	 *
	 * @param content the content of the comment
	 */
	public void addComment(String content) throws java.lang.Exception {
		
		try {	
			// add a comment to the root node		
			rootElement.appendChild(xmlDoc.createComment(content));
		} catch (Exception ex) {
			throw new java.lang.Exception ("Unable to add a comment to the KML Export File", ex);
		}
	} // end add comment method
	
	/**
	 * A method to add a folder element to the KML file
	 *
	 * @param name the name of the folder
	 *
	 * @return     the element representing this folder
	 */
	public Element addFolder(String name) throws java.lang.Exception {
	
		try {
			// create the folder element
			Element folder = xmlDoc.createElement("Folder");
			
			// create the name element
			Element folderName = xmlDoc.createElement("name");
			folderName.setTextContent(name);
			
			// add the name element to the folder
			folder.appendChild(folderName);
			
			// add the folder to the tree
			rootElement.appendChild(folder);
			
			// store reference to this folder
			folderElement = folder;
			
			// return this Element
			return folder;		
		
		} catch (Exception ex) {
			throw new java.lang.Exception ("Unable to add a folder to the KML Export File", ex);
		}
		
	} // end add Folder method
	
	/**
	 * A method to add a folder element to the KML file
	 *
	 * @param parent the parent folder
	 * @param name the name of the folder
	 *
	 * @return     the element representing this folder
	 */
	public Element addFolder(Element parent, String name) throws java.lang.Exception {
	
		try {
			// create the folder element
			Element folder = xmlDoc.createElement("Folder");
			
			// create the name element
			Element folderName = xmlDoc.createElement("name");
			folderName.setTextContent(name);
			
			// add the name element to the folder
			folder.appendChild(folderName);
			
			// add the folder to the tree
			parent.appendChild(folder);
			
			// store reference to this folder
			folderElement = folder;
			
			// return this Element
			return folder;		
		
		} catch (Exception ex) {
			throw new java.lang.Exception ("Unable to add a folder to the KML Export File", ex);
		}
		
	} // end add Folder method

	/**
	 * A method to add a document element to the KML file
	 *
	 * @param name the name of the document
	 *
	 * @return     the element representing this document
	 */
	public Element addDocument(String name) throws java.lang.Exception {
	
		try {
			// create the document element
			Element document = xmlDoc.createElement("Document");
			
			// create the name element
			Element documentName = xmlDoc.createElement("name");
			documentName.setTextContent(name);
			
			// add the name element to the folder
			document.appendChild(documentName);
			
			// add the document to the tree
			rootElement.appendChild(document);
			
			// store reference to this document if it is the first one
			if(firstDocElement == null) {
				firstDocElement = document;
			}
			
			// return this Element
			return document;
		
		} catch (Exception ex) {
			throw new java.lang.Exception ("Unable to add a document to the KML Export File", ex);
		}
	
	} // end addDocument method
	
	/**
	 * A method to add a document element to a folder inside the KML file
	 *
	 * @param folder the parent folder element of the new document element
	 * @param name   the name of the document
	 *
	 * @return       the element representing this document
	 */
	public Element addDocument(Element folder, String name) throws java.lang.Exception {
	
		try {
			// create the document element
			Element document = xmlDoc.createElement("Document");
			
			// create the name element
			Element documentName = xmlDoc.createElement("name");
			documentName.setTextContent(name);
			
			// add the name element to the folder
			document.appendChild(documentName);
			
			// add the document to the tree
			folder.appendChild(document);
			
			// store reference to this document if it is the first one
			if(firstDocElement == null) {
				firstDocElement = document;
			}
			
			// return this Element
			return document;
		
		} catch (Exception ex) {
			throw new java.lang.Exception ("Unable to add a document to the KML Export File", ex);
		}
	
	} // end addDocument method
	
	/**
	 * A method to add author information to an element
	 * such as a folder or a document
	 *
	 * @param parent the parent element in the DOM for this element
	 * @param name   the name of the author
	 * @param href   the href to a webpage containing this map
	 */
	public void addAuthorInfo(Element parent, String name, String href) throws java.lang.Exception {
	
		try{
			// add author information
			Element author = xmlDoc.createElement("atom:author");
			
			Element authorName = xmlDoc.createElement("atom:name");
			authorName.setTextContent(name);
			author.appendChild(authorName);
			
			// add link information
			Element link = xmlDoc.createElement("atom:link");
			link.setAttribute("href", href);
			
			// add these nodes to the tree
			parent.appendChild(author);
			parent.appendChild(link);
			
		} catch (Exception ex) {
			throw new java.lang.Exception ("Unable to add a author information to the KML Export File", ex);
		}	
	} // end addAuthorInfoToFolder method
	
	/**
	 * A method to add a description element to a parent element
	 * such as a folder or document
	 *
	 * @param parent the parent element in the DOM for this element
	 * @param text   the text of the description
	 */
	public void addDescription(Element parent, String text) throws java.lang.Exception {
	
		try{
			Element description = xmlDoc.createElement("description");
			description.setTextContent(text);
			parent.appendChild(description);
		} catch (Exception ex) {
			throw new java.lang.Exception ("Unable to add a description element to the KML Export File", ex);
		}
		
	} // end addDescription method
	
	/**
	 * A method to add an icon style element as a child to the folder element
	 *
	 * @param styleId the id attribute for this style
	 * @param iconUrl the url to the icon
	 */
	public void addIconStyle(String styleId, String iconUrl) throws java.lang.Exception {
	
		try {
			// create the element
			Element style = xmlDoc.createElement("Style");
			
			// add the id attribute
			style.setAttribute("id", styleId);
			
			// add the icon style element
			Element iconStyle = xmlDoc.createElement("IconStyle");
			style.appendChild(iconStyle);
			
			// add the icon element
			Element icon = xmlDoc.createElement("Icon");
			iconStyle.appendChild(icon);
			
			// add the href element
			Element href = xmlDoc.createElement("href");
			icon.appendChild(href);
			
			// add the url to the icon
			href.setTextContent(iconUrl);
			
			// add the style to the tree before the first document
			if(folderElement != null && firstDocElement != null) {
				folderElement.insertBefore(style, firstDocElement);
			} else {
				throw new java.lang.Exception ("Unable to add style element, KML must contain a folder and document element before adding style elements");
			}
		} catch (Exception ex) {
			throw new java.lang.Exception ("Unable to add style element to the KML Export File", ex);
		}
	} // end addIconStyle method
	
	/**
	 * A method to add an icon style element as a child to the folder element
	 *
	 * @param folder  the folder to add the style to
	 * @param styleId the id attribute for this style
	 * @param iconUrl the url to the icon
	 */
	public void addIconStyle(Element folder, String styleId, String iconUrl) throws java.lang.Exception {
	
		try {
			// create the element
			Element style = xmlDoc.createElement("Style");
			
			// add the id attribute
			style.setAttribute("id", styleId);
			
			// add the icon style element
			Element iconStyle = xmlDoc.createElement("IconStyle");
			style.appendChild(iconStyle);
			
			// add the icon element
			Element icon = xmlDoc.createElement("Icon");
			iconStyle.appendChild(icon);
			
			// add the href element
			Element href = xmlDoc.createElement("href");
			icon.appendChild(href);
			
			// add the url to the icon
			href.setTextContent(iconUrl);
			
			// add the style to the folder
			folder.appendChild(style);
			
		} catch (Exception ex) {
			throw new java.lang.Exception ("Unable to add style element to the KML Export File", ex);
		}
	} // end addIconStyle method
	
	/**
	 * A method used to add the standard icon styles to be used by default
	 */
	public void addIconStyles() throws java.lang.Exception {
	
		// add the style information
		this.addIconStyle("grp-1-event",       "http://chart.apis.google.com/chart?cht=mm&chs=32x32&chco=FFFFFF,CCBAD7,000000&ext=.png");
		this.addIconStyle("grp-2-5-event",     "http://chart.apis.google.com/chart?cht=mm&chs=32x32&chco=FFFFFF,9A7BAB,000000&ext=.png");
		this.addIconStyle("grp-6-15-event",    "http://chart.apis.google.com/chart?cht=mm&chs=32x32&chco=FFFFFF,7F649B,000000&ext=.png");
		this.addIconStyle("grp-16-30-event",   "http://chart.apis.google.com/chart?cht=mm&chs=32x32&chco=FFFFFF,69528E,000000&ext=.png");
		this.addIconStyle("grp-30-plus-event", "http://chart.apis.google.com/chart?cht=mm&chs=32x32&chco=FFFFFF,4D3779,000000&ext=.png");
		
	} // end addIconStyles method
	
	/**
	 * A method to add a line style element as a child to the folder element
	 *
	 * @param styleId    the id attribute for this style
	 * @param lineColour the colour of this line
	 * @param lineWidth  the width of the line
	 */
	public void addLineStyle(String styleId, String lineColour, String lineWidth) throws java.lang.Exception {
	
		try {
			// create the element
			Element style = xmlDoc.createElement("Style");
			
			// add the id attribute
			style.setAttribute("id", styleId);
			
			// add the line style element
			Element lineStyle = xmlDoc.createElement("LineStyle");
			style.appendChild(lineStyle);
			
			// add the colour element
			Element color = xmlDoc.createElement("color");
			lineStyle.appendChild(color);
			color.setTextContent(lineColour);
			
			// add the width element
			Element width = xmlDoc.createElement("width");
			lineStyle.appendChild(width);
			width.setTextContent(lineWidth);
			
			// add the style to the tree before the first document
			if(folderElement != null && firstDocElement != null) {
				folderElement.insertBefore(style, firstDocElement);
			} else {
				throw new java.lang.Exception ("Unable to add style element, KML must contain a folder and document element before adding style elements");
			}
		} catch (Exception ex) {
			throw new java.lang.Exception ("Unable to add style element to the KML Export File", ex);
		}
	} // end addLineStyle method
	
	/**
	 * A method to determine the colour in a gradient at a particular index, assuming a zero based index
	 * The gradient is from pure red to pure yellow, with the alpha channel set to fully opaque
	 *
	 * @param index   the index number for this part of the colour gradient
	 * @param maximum the maximum number of parts in the gradient
	 *
	 * @return    the colour value in KML notation - AABBGGRR
	 */
	public String getGradientColour(int index, int maximum) {
	
		// declare helper variables
		String redHex   = "FF";
		String greenHex = null;
		String blueHex  = "00";
		
		// declare values for the formula
		float minGreen = 0;
		float maxGreen = 255;
		
		// change the integers to floats
		float myIndex = index;
		float myMaximum = maximum;
		
		// calculate the green value & round to nearest integer
		int greenValue =  Math.round(minGreen + ((maxGreen - minGreen) * (myIndex / (myMaximum -1))));
		
		// convert the green value to hex
		greenHex = Integer.toHexString(greenValue).toUpperCase();
		
		// ensure the hex is the right length
		if(greenHex.length() == 1) {
			greenHex = "0" + greenHex;
		}
		
		// return the colour value
		return "FF" + blueHex + greenHex + redHex;
	
	} // end getGradientColour method
	
	/**
	 * A method to normalise a potential colour index into the allowed range
	 *
	 * @param index   the current index
	 * @param maximum the current maximum index
	 *
	 * @return        the normalised value
	 */
	public int getNormalisedColourIndex(int index, int maximum) {
	
		// declare constants
		final float allowedMin = 1;
		final float allowedMax = 255;
		final float rangeMin   = 1;
		
		// convert parameters to floats
		float rangeIndex = index;
		float rangeMax   = maximum;
		
		int newIndex =  Math.round(((allowedMax - allowedMin) / (rangeMax - rangeMin)) * (rangeIndex - rangeMin) + allowedMin);
		if(newIndex < 2) {
			return 2;
		}else {
			return newIndex;
		}

	} // end getNormalisedColourIndex method
	
	/**
	 * A method to return the string representation of this KML file
	 *
	 * @return a string containing the XML that makes up this KML file
	 */
	public String toString() {
	
		try {
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
			return writer.toString();
		} catch (javax.xml.transform.TransformerException ex) {
			return "";
		}
	
	} // end toString method

} // end class definition
