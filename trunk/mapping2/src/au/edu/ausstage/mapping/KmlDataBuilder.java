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

// import additional AusStage libraries
import au.edu.ausstage.utils.*;
import au.edu.ausstage.mapping.types.*;

// import additional java libraries
import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*; 
import javax.xml.transform.*; 
import javax.xml.transform.dom.*; 
import javax.xml.transform.stream.*;

/**
 * a class responsible for building KML data
 */
public class KmlDataBuilder {

	/**
	 * define the base URL for icon images
	 */
	public static final String ICON_BASE_URL = "http://beta.ausstage.edu.au/mapping2/assets/images/kml-icons/";

	// declare private class variables
	private DocumentBuilderFactory factory;
	private	DocumentBuilder        builder;
	private DOMImplementation      domImpl;
	private Document               xmlDoc;
	private Element                rootElement;
	private Element				   rootDocument;
	private Element                rootFolder;

	/**
	 * constructor for this class
	 *
	 * @throws KmlDownloadException if something bad happens
	 */
	public KmlDataBuilder() throws KmlDownloadException {
		// create the xml document builder factory object
		factory = DocumentBuilderFactory.newInstance();
		
		// set the factory to be namespace aware
		factory.setNamespaceAware(true);
		
		// create the xml document builder object and get the DOMImplementation object
		try {
			builder = factory.newDocumentBuilder();
		} catch (javax.xml.parsers.ParserConfigurationException ex) {
			throw new KmlDownloadException(ex.toString());
		}
		
		domImpl = builder.getDOMImplementation();
		
		// create a document with the appropriare default namespace
		xmlDoc = domImpl.createDocument("http://www.opengis.net/kml/2.2", "kml", null);
		
		// get the root element
		rootElement = this.xmlDoc.getDocumentElement();
		
		// add atom namespace to the root element
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:atom", "http://www.w3.org/2005/Atom");
		
		// add google earth extension namespace to the root element
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:gx", "http://www.google.com/kml/ext/2.2");
		
		// add schema namespace to the root element
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		
		// add reference to the kml schema
		rootElement.setAttribute("xsi:schemaLocation", "http://www.opengis.net/kml/2.2 http://schemas.opengis.net/kml/2.2.0/ogckml22.xsd");
		
		// add the root document
		rootDocument = xmlDoc.createElement("Document");
		rootElement.appendChild(rootDocument);
		
		// add author information
		Element author = xmlDoc.createElement("atom:author");
		
		Element authorName = xmlDoc.createElement("atom:name");
		authorName.setTextContent("AusStage");
		author.appendChild(authorName);
		
		// add link information
		Element link = xmlDoc.createElement("atom:link");
		link.setAttribute("href", "http://www.ausstage.edu.au/");
		
		// add info to node tree
		rootDocument.appendChild(author);
		rootDocument.appendChild(link);
		
		// add diagnostic information
		rootElement.appendChild(xmlDoc.createComment("File created: " + DateUtils.getCurrentDateAndTime()));
		
		// add style information
		this.addStyleInformation();
		
		// add the root folder
		rootFolder = xmlDoc.createElement("Folder");
		
		// create the name element
		Element name = xmlDoc.createElement("name");
		name.setTextContent("AusStage Map Data");				
		
		// add the name element to the folder
		rootFolder.appendChild(name);
		
		// add the folder to the tree
		rootDocument.appendChild(rootFolder);
		
	}
	
	// private method to add the style information
	private void addStyleInformation() {
	
		// add informative comment to the KML
		rootDocument.appendChild(xmlDoc.createComment("Shared Styles for Contributor icons"));
	
		// declare helper variables
		Element style;
		Element iconStyle;
		Element icon;
		Element href;
		
		int start  = 39;
		int finish = 88;
		
		// loop through and add all of the contributor styles
		for(int i = start; i <= finish; i++) {
			
			// create the main style element
			style = xmlDoc.createElement("Style");
			
			// set the id to the right identifier
			style.setAttribute("id", "c-" + i);
			
			// build the iconStyle elements
			iconStyle = xmlDoc.createElement("IconStyle");
			icon = xmlDoc.createElement("Icon");
			iconStyle.appendChild(icon);
			
			// build the href
			href = xmlDoc.createElement("href");
			href.setTextContent(ICON_BASE_URL + "kml-c-" + i + ".png");
			icon.appendChild(href);
		
			// add the icon style and style to the document
			style.appendChild(iconStyle);
			rootDocument.appendChild(style);
		}
		
		// add informative comment to the KML
		rootDocument.appendChild(xmlDoc.createComment("Shared Styles for Organisation icons"));
		
		// loop through and add all of the contributor styles
		for(int i = start; i <= finish; i++) {
			
			// create the main style element
			style = xmlDoc.createElement("Style");
			
			// set the id to the right identifier
			style.setAttribute("id", "o-" + i);
			
			// build the iconStyle elements
			iconStyle = xmlDoc.createElement("IconStyle");
			icon = xmlDoc.createElement("Icon");
			iconStyle.appendChild(icon);
			
			// build the href
			href = xmlDoc.createElement("href");
			href.setTextContent(ICON_BASE_URL + "kml-o-" + i + ".png");
			icon.appendChild(href);
		
			// add the icon style and style to the document
			style.appendChild(iconStyle);
			rootDocument.appendChild(style);
		}
		
		// add informative comment to the KML
		rootDocument.appendChild(xmlDoc.createComment("Shared Styles for Venue and Event icons"));
		
		// create the main style element
		style = xmlDoc.createElement("Style");
		
		// set the id to the right identifier
		style.setAttribute("id", "v-133");
		
		// build the iconStyle elements
		iconStyle = xmlDoc.createElement("IconStyle");
		icon = xmlDoc.createElement("Icon");
		iconStyle.appendChild(icon);
		
		// build the href
		href = xmlDoc.createElement("href");
		href.setTextContent(ICON_BASE_URL + "kml-v-133.png");
		icon.appendChild(href);
	
		// add the icon style and style to the document
		style.appendChild(iconStyle);
		rootDocument.appendChild(style);
		
		// create the main style element
		style = xmlDoc.createElement("Style");
		
		// set the id to the right identifier
		style.setAttribute("id", "e-88");
		
		// build the iconStyle elements
		iconStyle = xmlDoc.createElement("IconStyle");
		icon = xmlDoc.createElement("Icon");
		iconStyle.appendChild(icon);
		
		// build the href
		href = xmlDoc.createElement("href");
		href.setTextContent(ICON_BASE_URL + "kml-e-88.png");
		icon.appendChild(href);
	
		// add the icon style and style to the document
		style.appendChild(iconStyle);
		rootDocument.appendChild(style);
	}
	
	/**
	 * public method to print the KML to the supplied output stream
	 *
	 * @param printWriter the printWriter to use for the output
	 *
	 * @throws KmlDownloadException if something bad happens
	 */
	public void print(PrintWriter printWriter) {
	
		try {
			// create a transformer 
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer        transformer  = transFactory.newTransformer();
			
			// set some options on the transformer
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			
			// get a transformer and supporting classes
			//StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(printWriter);
			DOMSource    source = new DOMSource(xmlDoc);
			
			// transform the xml document into a string
			transformer.transform(source, result);
			//return writer.toString();
		} catch (javax.xml.transform.TransformerException ex) {
			throw new KmlDownloadException(ex.toString());
		}
	}

}
