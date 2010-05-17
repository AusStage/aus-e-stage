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

// import additional java libraries
import java.util.Iterator;
import javax.xml.*;
import javax.xml.namespace.NamespaceContext;


/**
 * A class to return the appropriate Namespace context
 * for xPath execution against KML files
 */
public class KmlNamespaceContext implements NamespaceContext {

	/**
	 * A method to return the Namespace URI of a given
	 * namespance prefix
	 *
	 * @param prefix the prefix to math
	 *
	 * @return       the matched namespace URI
	 */
	public String getNamespaceURI(String prefix) {

		// determine which namespace to return
		if(prefix == null) {
			throw new NullPointerException("Null prefix"); // no prefix specified
		} else if("kml".equals(prefix)) { 
			return "http://www.opengis.net/kml/2.2"; // kml namespace
		} else if("atom".equals(prefix)) {
			return "http://www.w3.org/2005/Atom"; // atom namespace
		}else if("xml".equals(prefix)) {
			return XMLConstants.XML_NS_URI; // default namespace
		} else {
			return XMLConstants.XML_NS_URI; // default namespace
		}
	}

	/**
	 * This method isn't necessary for XPath processing.
	 */
	public String getPrefix(String uri) {
		throw new UnsupportedOperationException();
	}

	/**
	 * This method isn't necessary for XPath processing.
	 */
	public Iterator getPrefixes(String uri) {
		throw new UnsupportedOperationException();
	}

}
