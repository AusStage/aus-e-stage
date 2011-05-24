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
import java.util.HashMap;
import java.util.Collection;
import java.util.TreeSet;

/**
 * a class responsible for building KML data
 */
public class KmlDataBuilder {

	/**
	 * define the base URL for icon images
	 */
	public static final String ICON_BASE_URL = "http://beta.ausstage.edu.au/mapping2/assets/images/kml-icons/";
	public static final String ALT_ICON_BASE_URL = "http://beta.ausstage.edu.au/mapping2/assets/images/iconography/";
	
	/**
	 * icon colour codes for contributors
	 */
	 public static final String[] CON_ICON_COLOUR_CODES = {"50", "49", "48", "47", "46", "45", "44", "43", "42", "41", "40", "39", "86", "85", "84", "83", "82", "81", "80", "79", "78", "77", "76", "75", "74", "73", "72", "71", "70", "69", "68", "67", "66", "65", "64", "63", "62", "61", "60", "59", "58", "57", "56", "55", "54", "53", "52", "51"};

	/**
	 * icon colour codes for organisations
	 */
	public static final String[] ORG_ICON_COLOUR_CODES = {"66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "60", "61", "62", "63", "64", "65", "59", "58", "57", "56", "55", "54", "53", "52", "51", "50", "49", "48", "47", "46", "45", "44", "43", "42", "41", "40", "39"};
	
	/**
	 * colour codes for use in the style of icons in the balloon content
	 */
	public static final String[] COLOUR_CODES = {"#276abd", "#317db9", "#3b91b5", "#44a4b1", "#4eb8ad", "#3b9d8f", "#278271", "#146853", "#004d35", "#1a6b3a", "#33893e", "#4da743", "#66c547", "#8cc43d", "#b3c333", "#d9d121", "#ffe010", "#ffd117", "#ffc11f", "#ffb320", "#ffa521", "#ff9821", "#ff8a22", "#fc792c", "#f96835", "#f6573f", "#f34648", "#dc3738", "#c52829", "#af1819", "#980909", "#a31d34", "#ad315f", "#b7458a", "#c259b5", "#b556b6", "#a754b7", "#9951b7", "#8c4eb8", "#7758c0", "#6262c7", "#4d6bcf", "#3875d7", "#2a5fd4", "#1c48d1", "#0e31cf", "#001bcc", "#1442c4", "#afc8ef", "#88ace7", "#ffcd4c"};
	
	/**
	 * icon colour code for venues
	 */
	public static final String VENUE_ICON_COLOUR_CODE = "133";
	
	/**
	 * icon colour code for events
	 */
	public static final String EVENT_ICON_COLOUR_CODE = "88";
	

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
		String[] date = DateUtils.getCurrentDateTimeAsArray();
		
		description.setTextContent("Data from <a href=\"http://www.ausstage.edu.au\">AusStage</a> on " + DateUtils.buildDisplayDate(date[0], date[1], date[2]) + " at " + date[3] + ":" + date[4] + ":" + date[5]);
		
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
		if(InputUtils.isValid(name) == false) {
			throw new IllegalArgumentException("name parameter is required");
		}
		
		// create the new folder
		Element folder = xmlDoc.createElement("Folder");
		
		// add the name and description elements
		Element n = xmlDoc.createElement("name");
		n.setTextContent(name);
		folder.appendChild(n);
		
		if(InputUtils.isValid(description) == true) {
			Element d = xmlDoc.createElement("description");
			d.setTextContent(description);
			folder.appendChild(d);
		}
		
		rootFolder.appendChild(folder);
		
		return folder;
	}
	
	private Element addDocument(Element folder, String name, String description) {
	
		if(folder == null) {
			return addDocument(name, description);
		}
	
		// check on the parameters
		if(InputUtils.isValid(name) == false ) {
			throw new IllegalArgumentException("the name parameter is required");
		}
		
		// create the new folder
		Element document = xmlDoc.createElement("Document");
		
		// add the name and description elements
		Element n = xmlDoc.createElement("name");
		n.setTextContent(name);
		document.appendChild(n);
		
		if(InputUtils.isValid(description) == true ) {
		
			Element d = xmlDoc.createElement("description");
			d.setTextContent(description);
			document.appendChild(d);
		}
		
		folder.appendChild(document);
		
		return document;
	}
	
	/**
	 * Add a section to the KML that includes organisation information
	 * 
	 * @param list the list of contributors
	 * 
	 * @throws KmlDownloadException if something bad happens
	 */
	public void addContributors(ContributorList list) {
	
		if(list == null) {
			throw new IllegalArgumentException("The contributors parameter must be a valid object");
		}
		
		Set<Contributor> contributors = list.getSortedContributors(ContributorList.CONTRIBUTOR_ALT_NAME_SORT);
		
		if(contributors.isEmpty() == true) {
			throw new IllegalArgumentException("There must be at least one contributor in the supplied list");
		}
			
		Element folder = addFolder("Contributors", null);
		Element document;
		
		Element placemark;
		Element elem;
		Element subElem;
		CDATASection cdata;
		
		String content;
		
		
		Iterator iterator = contributors.iterator();
		Contributor contributor;
		Event       event;
		
		KmlVenue kmlVenue;
		HashMap<Integer, KmlVenue> kmlVenues;
		
		int colourIndex = -1;
		int contentCount = 0;
		
		// loop through and add the placemarks
		while(iterator.hasNext()) {
		
			contributor = (Contributor)iterator.next();
			
			if(contributor.getKmlVenueCount() == 0) {
				throw new KmlDownloadException("there were no events associated with contributor '" + contributor.getId() + "'");
			}
			
			document = addDocument(folder, contributor.getName(), contributor.getFunctions());

			// determine which style to use
			if(colourIndex ==  CON_ICON_COLOUR_CODES.length) {
				colourIndex = 0;
			} else {
				colourIndex++;
			}
			
			kmlVenues = contributor.getKmlVenues();
			
			Set<KmlVenue> venues = new TreeSet<KmlVenue>(new KmlVenueComparator());
			venues.addAll(kmlVenues.values());
			Iterator venueIterator = venues.iterator();
			
			
			while(venueIterator.hasNext()) {
			
				kmlVenue = (KmlVenue)venueIterator.next();
				
				placemark = xmlDoc.createElement("Placemark");
				elem = xmlDoc.createElement("name");
				elem.setTextContent(kmlVenue.getName());
				placemark.appendChild(elem);
				
				elem = xmlDoc.createElement("atom:link");
				elem.setAttribute("href", kmlVenue.getUrl());
				placemark.appendChild(elem);
				
				elem = xmlDoc.createElement("snippet");
				elem.setTextContent(kmlVenue.getShortAddress());
				placemark.appendChild(elem);
				
				elem = xmlDoc.createElement("description");
				
				content = "<table><tr><th><span class=\"icon\"><img src=\"" + ALT_ICON_BASE_URL + "contributor.png\" width=\"32\" height=\"32\"></span>";
				
				content += " <a href=\"" + contributor.getUrl() + "\">" + contributor.getName() + "</a><br/>" + contributor.getFunctions() + "</th></tr>";
				contentCount = 0;
				
				Set<Event> events = kmlVenue.getEvents();
				Iterator eventIterator = events.iterator();
				
				while(eventIterator.hasNext()) {
					event = (Event)eventIterator.next();
					
					// check if content count is odd or not
					if (contentCount % 2 != 0) {
						content += "<tr class=\"odd\">";
					} else {
						content += "<tr>";
					} 
					
					content += "<td><a href=\"" + event.getUrl() + "\">" + event.getName() + "</a>, " + kmlVenue.getName() + ", " + kmlVenue.getShortAddress() + ", " + event.getFirstDisplayDate() + "</td></tr>";
					
					contentCount++;
				}
				
				content += "</table>";
				
				elem.appendChild(xmlDoc.createCDATASection(content));
				placemark.appendChild(elem);
				
				String[] timespanValues = kmlVenue.getTimespanValues();

				elem = xmlDoc.createElement("TimeSpan");
				
				subElem = xmlDoc.createElement("begin");
				subElem.setTextContent(timespanValues[0]);
				elem.appendChild(subElem);
				
				subElem = xmlDoc.createElement("end");
				subElem.setTextContent(timespanValues[1]);
				elem.appendChild(subElem);
				
				placemark.appendChild(elem);

				
				elem = xmlDoc.createElement("styleUrl");
				elem.setTextContent("#c-" + CON_ICON_COLOUR_CODES[colourIndex]);
				placemark.appendChild(elem);
				
				elem = xmlDoc.createElement("Point");
				subElem = xmlDoc.createElement("coordinates");
				subElem.setTextContent(kmlVenue.getLongitude() + "," + kmlVenue.getLatitude());
				elem.appendChild(subElem);
				placemark.appendChild(elem);
				
				document.appendChild(placemark);
			}			
		}
	}
	
	/**
	 * Add a section to the KML that includes organisation information
	 * 
	 * @param list the list of organisations
	 * 
	 * @throws KmlDownloadException if something bad happens
	 */
	public void addOrganisations(OrganisationList list) {
	
		if(list == null) {
			throw new IllegalArgumentException("The organisations parameter must be a valid object");
		}
		
		Set<Organisation> organisations = list.getSortedOrganisations(OrganisationList.ORGANISATION_NAME_SORT);
		
		if(organisations.isEmpty() == true) {
			throw new IllegalArgumentException("There must be at least one organisation in the supplied list");
		}
			
		Element folder = addFolder("Organisations", null);
		Element document;
		
		Element placemark;
		Element elem;
		Element subElem;
		CDATASection cdata;
		
		String content;
		
		
		Iterator iterator = organisations.iterator();
		Organisation organisation;
		Event       event;
		
		KmlVenue kmlVenue;
		HashMap<Integer, KmlVenue> kmlVenues;
		
		int colourIndex = -1;
		int contentCount = 0;
		
		// loop through and add the placemarks
		while(iterator.hasNext()) {
		
			organisation = (Organisation)iterator.next();
			
			if(organisation.getKmlVenueCount() == 0) {
				throw new KmlDownloadException("there were no events associated with organisation '" + organisation.getId() + "'");
			}
			
			document = addDocument(folder, organisation.getName(), organisation.getAddress());

			// determine which style to use
			if(colourIndex ==  ORG_ICON_COLOUR_CODES.length) {
				colourIndex = 0;
			} else {
				colourIndex++;
			}
			
			kmlVenues = organisation.getKmlVenues();
			
			Set<KmlVenue> venues = new TreeSet<KmlVenue>(new KmlVenueComparator());
			venues.addAll(kmlVenues.values());
			Iterator venueIterator = venues.iterator();
			
			
			while(venueIterator.hasNext()) {
			
				kmlVenue = (KmlVenue)venueIterator.next();
				
				placemark = xmlDoc.createElement("Placemark");
				elem = xmlDoc.createElement("name");
				elem.setTextContent(kmlVenue.getName());
				placemark.appendChild(elem);
				
				elem = xmlDoc.createElement("atom:link");
				elem.setAttribute("href", kmlVenue.getUrl());
				placemark.appendChild(elem);
				
				elem = xmlDoc.createElement("snippet");
				elem.setTextContent(kmlVenue.getShortAddress());
				placemark.appendChild(elem);
				
				elem = xmlDoc.createElement("description");
				
				content = "<table><tr><th><span class=\"icon\"><img src=\"" + ALT_ICON_BASE_URL + "organisation.png\" width=\"32\" height=\"32\"></span>";
				
				content += " <a href=\"" + organisation.getUrl() + "\">" + organisation.getName() + "</a><br/>" + kmlVenue.getShortAddress() + "</th></tr>";
				contentCount = 0;
				
				Set<Event> events = kmlVenue.getEvents();
				Iterator eventIterator = events.iterator();
				
				while(eventIterator.hasNext()) {
					event = (Event)eventIterator.next();
					
					// check if content count is odd or not
					if (contentCount % 2 != 0) {
						content += "<tr class=\"odd\">";
					} else {
						content += "<tr>";
					} 
					
					content += "<td><a href=\"" + event.getUrl() + "\">" + event.getName() + "</a>, " + kmlVenue.getName() + ", " + kmlVenue.getShortAddress() + ", " + event.getFirstDisplayDate() + "</td></tr>";
					
					contentCount++;
				}
				
				content += "</table>";
				
				elem.appendChild(xmlDoc.createCDATASection(content));
				placemark.appendChild(elem);
				
				String[] timespanValues = kmlVenue.getTimespanValues();

				elem = xmlDoc.createElement("TimeSpan");
				
				subElem = xmlDoc.createElement("begin");
				subElem.setTextContent(timespanValues[0]);
				elem.appendChild(subElem);
				
				subElem = xmlDoc.createElement("end");
				subElem.setTextContent(timespanValues[1]);
				elem.appendChild(subElem);
				
				placemark.appendChild(elem);

				
				elem = xmlDoc.createElement("styleUrl");
				elem.setTextContent("#o-" + ORG_ICON_COLOUR_CODES[colourIndex]);
				placemark.appendChild(elem);
				
				elem = xmlDoc.createElement("Point");
				subElem = xmlDoc.createElement("coordinates");
				subElem.setTextContent(kmlVenue.getLongitude() + "," + kmlVenue.getLatitude());
				elem.appendChild(subElem);
				placemark.appendChild(elem);
				
				document.appendChild(placemark);
			}			
		}
	}
	
	/**
	 * Add a section to the KML that includes venue information
	 * 
	 * @param list the list of venues
	 * 
	 * @throws KmlDownloadException if something bad happens
	 */
	public void addVenues(Set<KmlVenue> list) {
	
		if(list == null) {
			throw new IllegalArgumentException("The organisations parameter must be a valid object");
		}
		
		if(list.size() == 0) {
			throw new IllegalArgumentException("There must be at least one venue in the supplied list");
		}
			
		//Element folder = addFolder("Venues", null);
		Element document = addDocument(rootFolder, "Venues", null);
		
		Element placemark;
		Element elem;
		Element subElem;
		CDATASection cdata;
		
		String content;
		int    contentCount;
		
		Event       event;
		
		KmlVenue kmlVenue;
		Iterator venueIterator = list.iterator();
		
		while(venueIterator.hasNext()) {
		
			kmlVenue = (KmlVenue)venueIterator.next();
			
			placemark = xmlDoc.createElement("Placemark");
			elem = xmlDoc.createElement("name");
			elem.setTextContent(kmlVenue.getName());
			placemark.appendChild(elem);
			
			elem = xmlDoc.createElement("atom:link");
			elem.setAttribute("href", kmlVenue.getUrl());
			placemark.appendChild(elem);
			
			elem = xmlDoc.createElement("snippet");
			elem.setTextContent(kmlVenue.getShortAddress());
			placemark.appendChild(elem);
			
			elem = xmlDoc.createElement("description");
			
			content = "<table><tr><th><span class=\"icon\"><img src=\"" + ALT_ICON_BASE_URL + "venue-arch.png\" width=\"32\" height=\"32\"></span>";
			
			content += " <a href=\"" + kmlVenue.getUrl() + "\">" + kmlVenue.getName() + "</a><br/>" + kmlVenue.getAddress() + "</th></tr>";
			contentCount = 0;
			
			Set<Event> events = kmlVenue.getEvents();
			Iterator eventIterator = events.iterator();
			
			while(eventIterator.hasNext()) {
				event = (Event)eventIterator.next();
				
				// check if content count is odd or not
				if (contentCount % 2 != 0) {
					content += "<tr class=\"odd\">";
				} else {
					content += "<tr>";
				} 
				
				content += "<td><a href=\"" + event.getUrl() + "\">" + event.getName() + "</a>, " + event.getFirstDisplayDate() + "</td></tr>";
				
				contentCount++;
			}
			
			content += "</table>";
			
			elem.appendChild(xmlDoc.createCDATASection(content));
			placemark.appendChild(elem);
			
			String[] timespanValues = kmlVenue.getTimespanValues();

			elem = xmlDoc.createElement("TimeSpan");
			
			subElem = xmlDoc.createElement("begin");
			subElem.setTextContent(timespanValues[0]);
			elem.appendChild(subElem);
			
			subElem = xmlDoc.createElement("end");
			subElem.setTextContent(timespanValues[1]);
			elem.appendChild(subElem);
			
			placemark.appendChild(elem);

			elem = xmlDoc.createElement("styleUrl");
			elem.setTextContent("#v-133");
			placemark.appendChild(elem);
			
			elem = xmlDoc.createElement("Point");
			subElem = xmlDoc.createElement("coordinates");
			subElem.setTextContent(kmlVenue.getLongitude() + "," + kmlVenue.getLatitude());
			elem.appendChild(subElem);
			placemark.appendChild(elem);
			
			document.appendChild(placemark);
		}
	}
	
	/**
	 * Add a section to the KML that includes venue information
	 * 
	 * @param list the list of venues
	 * 
	 * @throws KmlDownloadException if something bad happens
	 */
	public void addEvents(TreeSet<KmlEvent> list) {
	
		if(list == null) {
			throw new IllegalArgumentException("The organisations parameter must be a valid object");
		}
		
		if(list.size() == 0) {
			throw new IllegalArgumentException("There must be at least one venue in the supplied list");
		}
			
		//Element folder = addFolder("Venues", null);
		Element document = addDocument(rootFolder, "Events", null);
		
		Element placemark;
		Element elem;
		Element subElem;
		CDATASection cdata;
		
		String content;
		int    contentCount;
		
		KmlEvent kmlEvent;
		Iterator eventIterator = list.iterator();
		
		while(eventIterator.hasNext()) {
		
			kmlEvent = (KmlEvent)eventIterator.next();
			
			placemark = xmlDoc.createElement("Placemark");
			elem = xmlDoc.createElement("name");
			elem.setTextContent(kmlEvent.getName());
			placemark.appendChild(elem);
			
			elem = xmlDoc.createElement("atom:link");
			elem.setAttribute("href", kmlEvent.getUrl());
			placemark.appendChild(elem);
			
			elem = xmlDoc.createElement("snippet");
			elem.setTextContent(kmlEvent.getVenueAddress());
			placemark.appendChild(elem);
			
			elem = xmlDoc.createElement("description");
			
			content = "<table><tr><th><span class=\"icon\"><img src=\"" + ALT_ICON_BASE_URL + "event.png\" width=\"32\" height=\"32\"></span>";
			content += " <a href=\"" + kmlEvent.getVenueUrl() + "\">" + kmlEvent.getVenueName() + "</a><br/>" + kmlEvent.getShortVenueAddress() + "</th></tr>";
			
			content += "<tr><td><a href=\"" + kmlEvent.getUrl() + "\">" + kmlEvent.getName() + "</a><br/>" + kmlEvent.getVenueName() + ", " + kmlEvent.getVenueAddress() + ", " + kmlEvent.getFirstDate() + "</td></tr></table>";
			
			elem.appendChild(xmlDoc.createCDATASection(content));
			placemark.appendChild(elem);
			
			elem = xmlDoc.createElement("TimeSpan");
			
			subElem = xmlDoc.createElement("begin");
			subElem.setTextContent(kmlEvent.getSortFirstDate());
			elem.appendChild(subElem);
			
			subElem = xmlDoc.createElement("end");
			subElem.setTextContent(kmlEvent.getSortLastDate());
			elem.appendChild(subElem);
			
			placemark.appendChild(elem);

			elem = xmlDoc.createElement("styleUrl");
			elem.setTextContent("#e-88");
			placemark.appendChild(elem);
			
			elem = xmlDoc.createElement("Point");
			subElem = xmlDoc.createElement("coordinates");
			subElem.setTextContent(kmlEvent.getLongitude() + "," + kmlEvent.getLatitude());
			elem.appendChild(subElem);
			placemark.appendChild(elem);
			
			document.appendChild(placemark);
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
			style.appendChild(createLabelStyle());
			style.appendChild(createBalloonStyle(i));
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
			style.appendChild(createLabelStyle());
			style.appendChild(createBalloonStyle(i));
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
		style.appendChild(createLabelStyle());
		style.appendChild(createBalloonStyle(89));
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
		style.appendChild(createLabelStyle());
		style.appendChild(createBalloonStyle(88));
		rootDocument.appendChild(style);
		
	}
	
	// private method to create the balloon style elements
	private Element createBalloonStyle(int index) {
		
		index = index - 39;
	
		Element balloonStyle = xmlDoc.createElement("BalloonStyle");
		
		String styleText = "<style type=\"text/css\">\n";
		styleText += "* {font-family: Helvetica, Verdana, Arial, sans-serif;}\n";
		styleText += "table {width: 500px;}\n";
		styleText += "th { background-color: #AAAAAA; color: #FFFFFF; text-align: left; font-weight: normal;}\n";
		styleText += "th a {color: #FFFFFF; text-decoration: none;}\n";
		styleText += "th a:visited {color: #ffffff; text-decoration: none;} \n";
		styleText += "th a:hover {color: #ffffff; text-decoration: underline;} \n";
		styleText += "tr.odd {background-color: #eeeeee; }\n";
		styleText += "a {color: #001bcc; text-decoration: none;}\n";
		styleText += "a:visited {color: #001bcc; text-decoration: none;} \n";
		styleText += "a:hover {color: #001bcc; text-decoration: underline;} \n";
		styleText += ".icon {width: 32px; height: 32px;	float: left; margin: 3px 5px 5px 5px; background-color: " + COLOUR_CODES[index] + "}\n";
		styleText += "</style>\n";
		styleText += "$[description]\n";
		
		Element text         = xmlDoc.createElement("text");
		text.appendChild(xmlDoc.createCDATASection(styleText));
		
		balloonStyle.appendChild(text);
		
		return balloonStyle;	
	}
	
	// private method to create the label style elements
	private Element createLabelStyle() {

		Element labelStyle = xmlDoc.createElement("LabelStyle");
		Element scale      = xmlDoc.createElement("scale");
		scale.setTextContent("0");
		
		labelStyle.appendChild(scale);
		
		return labelStyle;
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
