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


import org.w3c.dom.*;
import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 *@author - Mary Jeroff
 * ProcessClass processes all base CSS into three different outputs
 */
public class TransformCSS 
{	
/** this method take in base css format code and processes it to the expected foreground-css format output
 * @param basecss the base CSS that will be processed
 * @return the processed CSS	
 */
public String getForegroundCSS(String basecss) 
	{
		String[] temp;
		String delimeter = "\n";
		String line = null;
		StringBuilder result = new StringBuilder();
	
	//split the string into an array 
		temp = basecss.split(delimeter);
	
	for(int i = 0; i<temp.length; i++)
	{
		//trim each line
		line = temp[i].trim();
		
		// check to see if the line is blank
		if(line.equals("") == false)
		{
			// line is not empty so proceed
			// is line the start of a CSS rule?
			if(line.startsWith(".") == true) 
			{
				//if line does follow CSS rule and begin with dot do replace
				line = line.replace(".", ".f-");
				result.append(line + "\n");
			
			} else if(line.startsWith("color") == true)
			{
				line = " "+ line;
				result.append(line +"\n");
			} else if(line.contains("}") == true)
			{
				//if line has curly closing bracket output } + \n
				result.append(line + "\n");
			}
		}
	}
			return result.toString();
	}

/**
 *  this method take in base css format code and processes it to the expected background-css format output
 * @param baseCSS the base CSS that will be processed  
 * @return	the processed CSS
 */
public String getBackgroundCSS(String baseCSS){
	
		String[] temp;
		String delimeter = "\n";
		String line = null;
		StringBuilder result = new StringBuilder();
	
		//split the string into array by delimiter 
		temp = baseCSS.split(delimeter);
	
	//trim line 
		for(int i = 0; i<temp.length; i++)
	{
		line = temp[i].trim();
		
		// check to see if the line is blank
		if(line.equals("") == false)
		{
			// line is not empty so proceed
			// is line the start of a CSS rule
			if(line.startsWith(".") == true) 
			{
				line = line.replace(".", ".b-");
				// add line to the result
				result.append(line + "\n");
				//if line starts with "color" add space @ beginning of line and place "\n" at end 
			} else if(line.startsWith("color:") == true) 
			{
				line = "background-"+ line;
				//append to result
				result.append(line +"\n");
			} else if(line.contains("}") == true)
			{
				//if line has curly closing bracket output } + \n
				result.append(line + "\n");
			}
		}

	}
			return result.toString();	
	}

/** this method take in base css format code and processes it to return the expected KML valid formatted output
 * @param baseCSS the base CSS that will be processed
 * @return	the processed KML code segment
 */

public String getKML(String baseCSS) {
	
		String[] temp;
		String delimeter = "\n";
		String ID = null;
		String KmlDataAsString = null;
		String colorcode = null;
	
		//split the string into array by delimiter
		temp = baseCSS.split(delimeter);	
		//trim line 
		String line = null;
	
	
	//start the building of the XML report
		Document xmlDoc;
		Element rootElement;
		Element element;
		Element parentElement = null;
		Comment comment;
	
	//start the DOM
	
	try	{
		//create the XML document builder factory object
		DocumentBuilderFactory factry = DocumentBuilderFactory .newInstance();
		
		//instantiate other supporting objects 
		DocumentBuilder builder = factry.newDocumentBuilder();
		DOMImplementation dom = builder.getDOMImplementation();
		
		// create the XML document
		xmlDoc  = dom.createDocument("http://beta.ausstage.edu.au/xmlns/kml-colours", "kml-colours", null);
		
		//get the root element  
		rootElement = xmlDoc.getDocumentElement();
		
		}

	 catch(javax.xml.parsers.ParserConfigurationException ex) 
	{
		System.out.println("Error occured with setting up XML classes");
		return null;
	}
	
	for(int i = 0 ; i<temp.length; i++)
	{
		line = temp[i].trim();
		
		if(line.equals("") == false )
		{
			if(line.startsWith("color") == true )
			{
				//retrieve KML color code 
				colorcode = getColorCode(line.substring(8));
				//check if the parentelement is not null
				
				if(parentElement !=null) 
				
				{
				element = xmlDoc.createElement("color");
				element.setTextContent(colorcode);
				
				parentElement.appendChild(element);
				
				element = xmlDoc.createElement("width");
				element.setTextContent("1");
				parentElement.appendChild(element); 
				} 	
			}
			else if(line.startsWith(".") == true ) 
		{
		//retrieve id number
		String[] p = line.split(" ");
		ID = p[0].substring(1);
			
		// create comment
		comment = xmlDoc.createComment("ID=" + ID);
		rootElement.appendChild(comment);
				
		//add linestyle element 
		element = xmlDoc.createElement("linestyle"); //create the element 
		//element.setTextContent("this is the line style area");
		rootElement.appendChild(element);	//add the element to the tree
		parentElement = element;//mark element as parent to keep track of the children of this element 
	}
	}
	} // end of for loop

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
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		DOMSource    source = new DOMSource(xmlDoc);
		
		// transform the xml document into a string
		transformer.transform(source, result);
		
		KmlDataAsString = writer.toString();
		
	} 
		catch(javax.xml.transform.TransformerException e) 
	{
		System.out.println("Error in transforming XML to string");
		return null;
	}

	//debug code
		System.out.println(KmlDataAsString);
	
		return KmlDataAsString;
}	
	
private String getColorCode(String data){
		//method to convert CSS colorcode to KML colorcode 

		String res = null;
		String p1 = data.substring(0, 2);
		String p2 = data.substring(2,4);
		String p3 = data.substring(4,6);

		res = "FF" + p3 + p2 + p1;

		return res;

		
	}
}

	
	
	
	

	
	
	
	


