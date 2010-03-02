/*
 * This file is part of the AusStage Exchange Analytics application
 *
 * The AusStage Exchange Analytics application is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage Exchange Analytics application is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Exchange Analytics application.  
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
	private final String URL_START = "http://chart.apis.google.com/chart?";

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
	public String buildBarChart(String width, String height, String data, String title, String[] xLabels, String yMax) {
		
		// set chart type to a bar char
		// set the bar width to be automatically resized
		// show the x and y axis labels
		final String DEFAULT_PARAMS = "cht=bvs&chbh=a&chxt=x,y"; // auto resize the size of the bars and show x and y labels
		
		// declare helper variables
		StringBuilder url = new StringBuilder(this.URL_START + DEFAULT_PARAMS);
		
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
	

} // end class definition
