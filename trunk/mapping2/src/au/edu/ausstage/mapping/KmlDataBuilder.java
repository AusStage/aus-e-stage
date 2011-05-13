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

import java.util.Set;
import java.util.Iterator;

/**
 * a class responsible for building KML data
 */
public class KmlDataBuilder {

	/**
	 * define the base URL for icon images
	 */
	public static final String ICON_BASE_URL = "http://beta.ausstage.edu.au/mapping2/assets/images/kml-icons/";
	
	/**
	 * icon colour codes for contributors
	 */
	 public static final String[] CON_ICON_COLOUR_CODES = {"50", "49", "48", "47", "46", "45", "44", "43", "42", "41", "40", "39", "86", "85", "84", "83", "82", "81", "80", "79", "78", "77", "76", "75", "74", "73", "72", "71", "70", "69", "68", "67", "66", "65", "64", "63", "62", "61", "60", "59", "58", "57", "56", "55", "54", "53", "52", "51"};

	/**
	 * icon colour codes for organisations
	 */
	public static final String[] ORG_ICON_COLOUR_CODES = {"66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "60", "61", "62", "63", "64", "65", "59", "58", "57", "56", "55", "54", "53", "52", "51", "50", "49", "48", "47", "46", "45", "44", "43", "42", "41", "40", "39"};
	


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
		
		// create the name and description elements
		Element name = xmlDoc.createElement("name");
		name.setTextContent("AusStage");
		
		Element description = xmlDoc.createElement("description");
		description.setTextContent("Map data from <a href=\"http://www.ausstage.edu.au\">AusStage</a>");
		
		// add the name element to the folder
		rootFolder.appendChild(name);
		rootFolder.appendChild(description);
		
		// add the folder to the tree
		rootDocument.appendChild(rootFolder);
	}
	
	private Element addDocument(String name, String description) {
	
		// check on the parameters
		if(InputUtils.isValid(name) == false || InputUtils.isValid(description) == false) {
			throw new IllegalArgumentException("both parameters are required");
		}
		
		// create the new folder
		Element document = xmlDoc.createElement("Document");
		
		// add the name and description elements
		Element n = xmlDoc.createElement("name");
		n.setTextContent(name);
		
		Element d = xmlDoc.createElement("description");
		d.setTextContent(description);
		
		document.appendChild(n);
		document.appendChild(d);
		
		rootFolder.appendChild(document);
		
		return document;
	}
	
	private Element addFolder(String name, String description) {
	
		// check on the parameters
		if(InputUtils.isValid(name) == false || InputUtils.isValid(description) == false) {
			throw new IllegalArgumentException("both parameters are required");
		}
		
		// create the new folder
		Element folder = xmlDoc.createElement("Folder");
		
		// add the name and description elements
		Element n = xmlDoc.createElement("name");
		n.setTextContent(name);
		
		Element d = xmlDoc.createElement("description");
		d.setTextContent(description);
		
		folder.appendChild(n);
		folder.appendChild(d);
		
		rootFolder.appendChild(folder);
		
		return folder;
	}
	
	private Element addDocument(Element folder, String name, String description) {
	
		if(folder == null) {
			return addDocument(name, description);
		}
	
		// check on the parameters
		if(InputUtils.isValid(name) == false || InputUtils.isValid(description) == false) {
			throw new IllegalArgumentException("both parameters are required");
		}
		
		// create the new folder
		Element document = xmlDoc.createElement("Document");
		
		// add the name and description elements
		Element n = xmlDoc.createElement("name");
		n.setTextContent(name);
		
		Element d = xmlDoc.createElement("description");
		d.setTextContent(description);
		
		document.appendChild(n);
		document.appendChild(d);
		
		folder.appendChild(document);
		
		return document;
	}
	
	/**
	 * Add a document containing contributor information
	 * 
	 * @param list the list of contributors
	 * 
	 * @throws KmlDownloadException if something bad happens
	 */
	public void addContributors(ContributorList list) {
	
		if(list == null) {
			throw new IllegalArgumentException("The contributors parameter must be a valid object");
		}
		
		Set<Contributor> contributors = list.getContributors();
		
		if(contributors.isEmpty() == true) {
			throw new IllegalArgumentException("There must be at least one contributor in the supplied list");
		}
			
		Element folder = addFolder("Contributors", "Maps of events grouped by contributor");
		Element document;
		
		Element placemark;
		Element elem;
		Element subElem;
		CDATASection cdata;
		
		String content;
		
		
		Iterator iterator = contributors.iterator();
		Contributor contributor;
		Event       event;
		
		int colourIndex = -1;
		
		/*
		 <Placemark>
                    <name>The Ballad of Angel's Alley</name>
                    <atom:link
                        href="http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&amp;f_event_id=18750"/>
                    <description><![CDATA[<p>The Old Tote Theatre, Kensington, 25 September 1963 <br/><a href="http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_event_id=18750">More Information</a></p>]]></description>
                    <styleUrl>basic-event</styleUrl>
                    <Point>
                        <coordinates>151.2283510,-33.9157990</coordinates>
                    </Point>
                </Placemark>
        */
		
		// loop through and add the placemarks
		while(iterator.hasNext()) {
		
			contributor = (Contributor)iterator.next();
			
			if(contributor.getEventCount() == 0) {
				throw new KmlDownloadException("there were no events associated with contributor '" + contributor.getId() + "'");
			}
			
			document = addDocument(folder, contributor.getName(), "Map of events associated with: " + contributor.getName() + " <br/> <a href=\"" + contributor.getUrl() + "\">More Information</a>");
			
			// determine which style to use
			if(colourIndex ==  CON_ICON_COLOUR_CODES.length) {
				colourIndex = 0;
			} else {
				colourIndex++;
			}
			
			Iterator events = contributor.getEvents().iterator();
			
			while(events.hasNext()) {
				event = (Event)events.next();
				
				placemark = xmlDoc.createElement("Placemark");
				elem = xmlDoc.createElement("name");
				elem.setTextContent(event.getName());
				placemark.appendChild(elem);
				
				elem = xmlDoc.createElement("atom:link");
				elem.setAttribute("href", event.getUrl());
				placemark.appendChild(elem);
				
				elem = xmlDoc.createElement("description");
				content = "<p>" + event.getVenue() + "<br/>" + "<a href=\"" + event.getUrl() + "\">More Information</a></p>";
				elem.appendChild(xmlDoc.createCDATASection(content));
				placemark.appendChild(elem);
				
				elem = xmlDoc.createElement("TimeSpan");
				
				subElem = xmlDoc.createElement("begin");
				subElem.setTextContent(event.getSortFirstDate());
				elem.appendChild(subElem);
				
				subElem = xmlDoc.createElement("end");
				subElem.setTextContent(event.getSortLastDate());
				elem.appendChild(subElem);
				
				placemark.appendChild(elem);
				
				elem = xmlDoc.createElement("styleUrl");
				elem.setTextContent("#c-" + CON_ICON_COLOUR_CODES[colourIndex]);
				placemark.appendChild(elem);
				
				elem = xmlDoc.createElement("Point");
				subElem = xmlDoc.createElement("coordinates");
				subElem.setTextContent(event.getLongitude() + "," + event.getLatitude());
				elem.appendChild(subElem);
				placemark.appendChild(elem);
				
				document.appendChild(placemark);
			}			
		}
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
			StreamResult result = new StreamResult(printWriter);
			DOMSource    source = new DOMSource(xmlDoc);
			
			// transform the internal objects into XML and print it
			transformer.transform(source, result);

		} catch (javax.xml.transform.TransformerException ex) {
			throw new KmlDownloadException(ex.toString());
		}
	}

}
