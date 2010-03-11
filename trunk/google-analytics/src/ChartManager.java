/*
 * This file is part of the AusStage Exchange Analytics application
 *
 * The AusStage Google Analytics Report Generator application is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage Google Analytics Report Generator application is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Google Analytics Report Generator application.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

// import additional libraries
import java.util.*;

/**
 * A class to help in the construction of URLs for use with
 * the Google Chart API.
 *
 * Note this is not intended to be a replacement for existing libraries
 * or for the more advanced features. It is to help with the creation of 
 * simple charts only. 
 */
public class ChartManager {

	// declare class level variables
	private final String URL_START = "http://chart.apis.google.com/chart?";   // base URL
	
	// bar charts
	private final String BARCHART_DEFAULT_PARAMS = "cht=bvs&chbh=a&chxt=x,y"; // auto resize the size of the bars and show x and y labels
	private final String BARCHART_SERIES_COLOUR = "&chco=77A1CA";             // default bar chart colour	
	
	// pie charts
	private final String PIE_CHART_DEFAULT_PARAMS = "&cht=p3";
	private final String PIE_CHART_COLOURS        = "&chco=A1CA77|77A1CA|A177CA|CAA177";
	

	/**
	 * A method to encode an array of values using the simple encoding 
	 * method as specified by the Google Chart API
	 *
	 * Based on the code found here: 
	 * http://code.google.com/apis/chart/docs/data_formats.html#encoding_data
	 *
	 * @param values   an array of string values to encode
	 * @param maxValue the maximum value in the dataset
	 * @return       a string containing the encoded data
	 */
	public String simpleEncode(String[] values, int maxValue) {
	
		// define helper variables
		final String simpleEncoding = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder data = new StringBuilder("s:");
		int currentValue;
		
		// check to ensure maxValue is > 0
		// could be that the current month doesn't have any data so the max value is 0
		if(maxValue > 0) {
		
			// loop through the array encoding data as we go
			for (int i = 0; i < values.length; i++) {
				currentValue = Integer.parseInt(values[i]);
				
				// find the aprpropriate encoded data value
				// including scaling the value appropriately
				if(currentValue >= 0) {
					data.append(simpleEncoding.charAt(Math.round((simpleEncoding.length() - 1) * currentValue / maxValue)));
				} else {
					data.append("_");
				}
			}
		} else {
			// just add enough zeros to reflect the number of values
			for (int i = 0; i < values.length; i++) {
				data.append("A");
			}
		}
		
		// return the data
		return data.toString();		
	
	} // end simpleEncode method
	
	/**
	 * A method to build a bar chart
	 *
	 * @param title   the chart title
	 * @param xLabels an array of labels to use for the x axis
	 * @param yMax    the maximum y value
	 * @param data    the data to build the chart with
	 * @param width   the width of the chart image
	 * @param height  the height of the chart image
	 * 
	 * @return  the URL for the bar chart
	 */
	public String buildBarChart(String width, String height, String data, String title, String yMax, String[] xLabels) {
		
		// start url with appropriate defaults
		StringBuilder url = new StringBuilder(this.URL_START + this.BARCHART_DEFAULT_PARAMS + this.BARCHART_SERIES_COLOUR);
		
		// add the chart size
		url.append("&chs=" + width + "x" + height);
		
		// add the data
		url.append("&chd=" + data);
		
		try {
			// add the chart title
			url.append("&chtt=" + java.net.URLEncoder.encode(title, "UTF-8"));
		} catch (java.io.UnsupportedEncodingException ex) {
			System.out.println("WARN: Unable to encode title '" + title + "' during chart building.");
			url.append("&chtt=Default Title");
		}
		
		// add the x axis labels
		url.append("&chxl=0:");
		
		for(int i = 0; i < xLabels.length; i++) {
			url.append("|" + xLabels[i]);
		}
		
		// add the y axis range
		url.append("&chxr=1,0," + yMax);
		
		// return the url
		return url.toString();
	
	} // end buildBarChart method
	
	/**
	 * A method to build a bar chart, the x axis labels will be calculated automatically
	 *
	 * @param title   the chart title
	 * @param yMax    the maximum y value
	 * @param data    the data to build the chart with
	 * @param width   the width of the chart image
	 * @param height  the height of the chart image
	 * 
	 * @return  the URL for the bar chart
	 */
	public String buildBarChart(String width, String height, String data, String title, String yMax) {
	
		// declare helper variables
		String[] xLabels = new String[data.length() - 2];
	
		// calculate the default xLabels
		for(int i = 0; i < data.length() - 2; i++) {
			xLabels[i] = String.format("%02d", i + 1);
		}
	
		return this.buildBarChart(width, height, data, title, yMax, xLabels);	
	} // end buildBarChart method
	
	/**
	 * A method to build a pie chart
	 *
	 * @param width   the width of the chart image
	 * @param height  the height of the chart image
	 * @param data    the data to build the chart with
	 * @param title   the chart title
	 */
	public String buildPieChart(String width, String height, String data, String title, String[] labels) {
	
		// start url with appropriate defaults
		StringBuilder url = new StringBuilder(this.URL_START + this.PIE_CHART_DEFAULT_PARAMS + this.PIE_CHART_COLOURS);
		
		// add the chart size
		url.append("&chs=" + width + "x" + height);
		
		// add the data
		url.append("&chd=" + data);
		
		try {
			// add the chart title
			url.append("&chtt=" + java.net.URLEncoder.encode(title, "UTF-8"));
		} catch (java.io.UnsupportedEncodingException ex) {
			System.out.println("WARN: Unable to encode title '" + title + "' during chart building.");
			url.append("&chtt=Default Title");
		}
		
		// add the labels
		url.append("&chl=");
		
		for(int i = 0; i < labels.length; i++) {
			try {
				url.append(java.net.URLEncoder.encode(labels[i], "UTF-8") + "|");
			} catch (java.io.UnsupportedEncodingException ex) {
				System.out.println("WARN: Pie chart label '" + labels[i] + "' during chart building.");
				url.append("|Default label");
			}
		}
				
		// return the url
		return url.toString();
	} // end buildPieChart method
	

} // end class definition
