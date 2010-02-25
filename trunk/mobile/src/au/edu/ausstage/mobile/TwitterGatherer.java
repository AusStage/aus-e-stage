/*
 * This file is part of the AusStage Audience Participation Service
 *
 * The AusStage Audience Participation Service is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage Audience Participation Service is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Audience Participation Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.mobile;

// import additional classes
import twitter4j.*;
import java.util.*;
 
/**
 * A class used to gather message from Twitter and add them to the system
 */ 
public class TwitterGatherer {

	// declare private class variables
	Twitter twitterClient = null;
	ArrayList <Status> mentionList = new ArrayList<Status>();

	/**
	 * Constructor for this class
	 *
	 * @param user     the username to authenticate with twitter
	 * @param password the password to authenticate with twitter
	 */
	public TwitterGatherer(String user, String password) {
		// get an instance of the twitter class
		twitterClient = new TwitterFactory().getInstance(user, password);
	} // end constructor

	/**
	 * use the Twitter API to get all of the new mentions
	 *
	 * @return  a count of the number of new messages
	 */
	@SuppressWarnings("unchecked")
	public int getNewMentions() throws Exception{
	
		// declare helper variables
		long sinceId = 0;      // most recent message from last run
		ResponseList mentions; // list of mentions
		Status message;		   // temporary storage of a message
		Paging page;           // used to page through responses
		int pageCount = 1;
	
		// get the highest existing message id
		
		// get any and all messages
		if(sinceId == 0) {
		
			// start at the first page
			page = new Paging(pageCount);
		} else {
		
			// start at the first page of tweets later than the ones
			// we already have
			page = new Paging(pageCount, sinceId);
		}
		
			
		// get a page of mentions
		try {
			mentions = twitterClient.getMentions(page);
		} catch (TwitterException te) {
			throw new Exception(te.getMessage());
		}
		
		// see if anything was returned
		if(mentions.isEmpty() == false) {
			// add these mentions to the larger list
			mentionList.addAll(mentions);
		} else {
			return 0;
		}
		
		// get any remaining mentions
		while(mentions.isEmpty() == false) {
		
			// increment the page count
			pageCount++;
			
			// update the paging object
			page.setPage(pageCount);
			
			// get a page of mentions
			try {
				mentions = twitterClient.getMentions(page);
			} catch (TwitterException te) {
				throw new Exception(te.getMessage());
			}
			
			// see if anything was returned
			if(mentions.isEmpty() == false) {
				// add these mentions to the larger list
				mentionList.addAll(mentions);
			}
		}
		
		// return the number of mentions
		return mentionList.size();
	
	} // end getNewMentions method
	
	/**
	 * Add a series of new mentions to the database
	 *
	 * @param debug if set to true a list of mentions will be returned
	 *
	 * @return list of added mentions
	 */
	public String addNewMentions(boolean debug) throws Exception {
	
		// check to see if there is data to work with
		if(mentionList.isEmpty() == true) {
			throw new Exception("No new mentions to add");
		}
		
		// declare helper variables
		StringBuilder data = new StringBuilder("-=Twitter Messages Added=-\nid|user|message|date/time\n");
		
		// loop through the list of mentions and compile debug output
		if(debug == true) {
			for (Status mention : mentionList) {
			
				// add details to the response
				data.append(mention.getId() + "|"); // twitter id
				data.append(mention.getUser().getScreenName() + "|"); // twitter user name
				data.append(mention.getText() + "|");
				data.append(mention.getCreatedAt().toString() + "\n");			
			}
		}
		
		// add mentions to the database
		
		//TODO add database code here
		
		if(debug == true) {
			return data.toString();
		} else {
			return null;
		}	
	} // end addNewMentions method
	
	/**
	 * Add a series of new mentions to the database
	 *
	 */
	public void addNewMentions() throws Exception {
		
		// execute the other method
		this.addNewMentions(false);
	
	} // end addNewMentions method

} // end class definition
