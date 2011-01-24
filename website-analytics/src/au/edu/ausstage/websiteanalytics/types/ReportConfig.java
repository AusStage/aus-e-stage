/*
 * This file is part of the AusStage Website Analytics app
 *
 * The AusStage Website Analytics app is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Website Analytics app is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AAusStage Website Analytics app.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.websiteanalytics.types;

// import additional ausstage packages
import au.edu.ausstage.utils.*;

/**
 * A class to hold the report config
 */
public class ReportConfig {
	
	/*
	 * private class level variables
	 */
	private String         reportTitle          = null;
	private String         fileName             = null;
	private StringBuilder  description          = null;
	private String         descriptionTitle     = null;
	private String         urlPattern           = null;
	private String         tableId              = null;
	private boolean        currentMonthSection  = false;
	private boolean        previousMonthSection = false;
	private boolean        currentYearSection   = false;
	private boolean        previousYearSection  = false;
	
	/*
	 * get a set methods
	 */
	/**
	 * get the title of the report
	 *
	 * @return the title of the report
	 */
	public String getReportTitle() {
		return reportTitle;
	}
	
	/**
	 * Set the title to a new value
	 *
	 * @param value the new value
	 */
	public void setReportTitle(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The new value must be a valid non zero length string");
		}
		
		reportTitle = value;
	}
	
	/**
	 * get the title of the description section
	 *
	 * @return the title of the description section
	 */
	public String getDescriptionTitle() {
		return descriptionTitle;
	}
	
	/**
	 * Set the title to a new value
	 *
	 * @param value the new value
	 */
	public void setDescriptionTitle(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The new value must be a valid non zero length string");
		}
		
		descriptionTitle = value;
	}
	
	/**
	 * get the file name for the report
	 *
	 * @return the file name for the report
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Set the file name to a new value
	 *
	 * @param value the new value
	 */
	public void setFileName(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The new value must be a valid non zero length string");
		}
		
		fileName = value;
	}
	
	/**
	 * get the description for the report
	 *
	 * @return the description for the report
	 */
	public String getDescription() {
		return description.toString().trim();
	}
	
	/**
	 * Set the description to a new value
	 *
	 * @param value the new value
	 */
	public void setDescription(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The new value must be a valid non zero length string");
		}
		
		description = new StringBuilder(value);
	}
	
	/**
	 * Append a character sequence to the string
	 *
	 * @param c     the character sequence
	 * @param start the index of the start of the characters in the index
	 * @param end   the index of the end of the characters in the index
	 */
	public void appendCharacters(char[] c, int start, int end) {
		if(description == null) {
			description = new StringBuilder();
		}
		description.append(c, start, end);
	}
	
	/**
	 * get the URL pattern for the report
	 *
	 * @return the URL pattern for the report
	 */
	public String getUrlPattern() {
		return urlPattern;
	}
	
	/**
	 * Set the URL pattern to a new value
	 *
	 * @param value the new value
	 */
	public void setUrlPattern(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The new value must be a valid non zero length string");
		}
		
		urlPattern = value;
	}
	
	/**
	 * get the table id for the report
	 *
	 * @return the table id for the report
	 */
	public String getTableId() {
		return tableId;
	}
	
	/**
	 * Set the table id to a new value
	 *
	 * @param value the new value
	 */
	public void setTableId(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The new value must be a valid non zero length string");
		}
		
		tableId = value;
	}
	
	/**
	 * Set the current month section flag
	 *
	 * @param value the new value
	 */
	public void setCurrentMonthFlag(boolean value) {
		currentMonthSection = value;
	}
	
	/**
	 * Determine if a current month section is required
	 *
	 * @return true if an only if, the current month flag has been set to true
	 */
	public boolean doCurrentMonthSection() {
		return currentMonthSection;
	}
	
	/**
	 * Set the previous month section flag
	 *
	 * @param value the new value
	 */
	public void setPreviousMonthFlag(boolean value) {
		previousMonthSection = value;
	}
	
	/**
	 * Determine if a previous month section is required
	 *
	 * @return true if an only if, the current month flag has been set to true
	 */
	public boolean doPreviousMonthSection() {
		return previousMonthSection;
	}
	
	/**
	 * Set the current year section flag
	 *
	 * @param value the new value
	 */
	public void setCurrentYearFlag(boolean value) {
		currentYearSection = value;
	}
	
	/**
	 * Determine if a current year section is required
	 *
	 * @return true if an only if, the current month flag has been set to true
	 */
	public boolean doCurrentYearSection() {
		return currentYearSection;
	}
	
	/**
	 * Set the previous year section flag
	 *
	 * @param value the new value
	 */
	public void setPreviousYearFlag(boolean value) {
		previousYearSection = value;
	}
	
	/**
	 * Determine if a previous year section is required
	 *
	 * @return true if an only if, the current month flag has been set to true
	 */
	public boolean doPreviousYearSection() {
		return previousYearSection;
	}
}
