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

// import other Java classes / libraries
import java.sql.ResultSet;
import java.io.PrintWriter;

/**
 * A class used to prepare KML ready for download
 */
public class KmlDownloadManager {

	// declare private class variables
	DbManager database;
	KmlDataBuilder builder;
	
	/**
	 * Constructor for this class
	 *
	 * @param database a valid DbManager object
	 */
	public KmlDownloadManager(DbManager database) {
		this.database = database;
	}
	
	/**
	 * Prepare KML that includes the supplied contributors, organisations, venues and events
	 *
	 * @param contributors a list of contributor ids
	 * @param organisations a list of organisation ids
	 * @param venues a list of venue ids
	 * @param events a list of event ids
	 *
	 * @param throws KmlDownloadException if something bad happens
	 */
	public void prepare(String[] contrbutors, String[] organiations, String[] venues, String[] events) throws KmlDownloadException {
	
		// instantiate the required object
		builder = new KmlDataBuilder();
	
	
	}
	
	/**
	 * Print the prepared KML to the supplied print writer
	 *
	 * @param writer the print writer to use to print the KML
	 *
	 * @param throws KmlDownloadException if something bad happens
	 */
	public void print(PrintWriter writer) throws KmlDownloadException {
	
		builder.print(writer);
	
	}

}
