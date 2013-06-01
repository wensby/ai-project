/**
 * @author Lukas J. Wensby
 * @version 2013-05-31
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

public class User {
	private int userID;
	private int birthyear;
	private int gender;
	private int numTweets;
	private int numFollowing;
	private HashMap<Integer, Integer> numComments = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> numAtActions = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> numReTweets = new HashMap<Integer, Integer>();
	private Vector<Integer> following = new Vector<Integer>();
	
	/**
	 * Constructs a User object of user with specified ID from the data in the specified database.
	 */
    public User(int userID, Database database) throws Exception{
    	if (!database.hasOpenConnection()) {
    		Debug.pl("! ERROR: Can't create User object when the database connection is closed.");
    		throw new IllegalArgumentException("Database object must have an open connection.");
    	}

    	try {

    		ResultSet userProfileResult; // will contain the result set from the user_profile query
    		ResultSet userSNSResult; // will contain the result set from the userSNS query
    		ResultSet userActionResult; // will contain the result set from the userAction query

    		// Fetch all the data
    		Statement statProfile = database.createStatement();
    		statProfile.execute("SELECT * FROM user_profile WHERE UserID = " + userID + " LIMIT 1;");
			userProfileResult = statProfile.getResultSet();
			
			Statement statSNS = database.createStatement();
			statSNS.execute("SELECT * FROM userSNS WHERE followerUserID = " + userID + ";");
			userSNSResult = statSNS.getResultSet();
			
			Statement statAction = database.createStatement();
			statAction.execute("SELECT * FROM user_action WHERE userID = " + userID + ";");
			userActionResult = statAction.getResultSet();
			
			// Build it, and he will come (the User, that is)
            if (userProfileResult.next()){
				this.userID = userProfileResult.getInt("UserID");
				this.birthyear = userProfileResult.getInt("birthYear");
				this.gender = userProfileResult.getInt("gender");
				this.numTweets = userProfileResult.getInt("tweets");
		    	initNumFollowing(userSNSResult);
		    	initActions(userActionResult);
            }else{
                throw new Exception("User not found, with id: " + this.userID);
			}
	    	
	    	// Clear the result sets, no need for those.
	    	userProfileResult.close();
	    	userSNSResult.close();
	    	userActionResult.close();

	    	statAction.close();
	    	statSNS.close();
	    	statProfile.close();
	    	
		} catch (SQLException e) { e.printStackTrace(); }
    }
    
    private void initActions(ResultSet userActionResult) throws SQLException {
    	while (userActionResult.next()) {
    		int destUserID = userActionResult.getInt("destinationUserID");
    		int numComments = userActionResult.getInt("comment");
    		int numAtActions = userActionResult.getInt("atAction");
    		int numReTweets = userActionResult.getInt("reTweet");
    		this.numComments.put(destUserID, numComments);
    		this.numAtActions.put(destUserID, numAtActions);
    		this.numReTweets.put(destUserID, numReTweets);
    	}
	}

    /**
     * This method initializes two attributes of this user:<br>
     * 1. The integer vector of all the user IDs that this user follows.<br>
     * 2. The amount of users that this user follows.
     */
    private void initNumFollowing(ResultSet userSNSResult) throws SQLException {
    	int numFollowing = 0;
    	while (userSNSResult.next()) {
    		following.add(userSNSResult.getInt(3));
    		numFollowing++;
    	}
    	this.numFollowing = numFollowing;
    	userSNSResult.absolute(1); // move back the ResultSet cursor
    }
    
    public int getBirthYear() {
    	return birthyear;
    }
    
    public int getGender() {
    	return gender;
    }
    
    public int getNumTweets() {
    	return numTweets;
    }
    
    public int getUserID() {
    	return userID;
    }
    
    public int getNumFollowing() {
    	return numFollowing;
    }
    
    public HashMap<Integer, Integer> getNumComments() {
    	return numComments;
    }
    
    public HashMap<Integer, Integer> getNumAtActions() {
    	return numAtActions;
    }
    
    public HashMap<Integer, Integer> getNumReTweets() {
    	return numReTweets;
    }

    public HashMap<Integer, Double> getKeywords(){
        Debug.pl("FUNCTION get Keywords NOT IMPLEMENTED");
        return null;
    }
    
    /**
     * Returns the vector of user IDs that this user is following.
     */
    public Vector<Integer> getFollowing() {
    	return following;
    }
}
