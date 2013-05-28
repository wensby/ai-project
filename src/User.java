/**
 * @author Lukas J. Wensby (The most awesome of them all)
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class User {
	private int userID;
	private int birthyear;
	private int gender;
	private int numTweets;
	private int numFollowing;
	private HashMap<Integer, Integer> numComments = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> numAtActions = new HashMap<Integer, Integer>();;
	private HashMap<Integer, Integer> numReTweets = new HashMap<Integer, Integer>();;
	
	ResultSet userProfileResult; // will contain the result set from the user_profile query
	ResultSet userSNSResult; // will contain the result set from the userSNS query
	ResultSet userActionResult; // will contain the result set from the userAction query

	/**
	 * Constructs a User object of user with specified ID from the data in the specified database.
	 */
    public User(int userID, Database database) {
    	if (!database.hasOpenConnection()) {
    		Debug.pl("! ERROR: Can't create User object when the database connection is closed.");
    		throw new IllegalArgumentException("Database object must have an open connection.");
    	}
    	
    	// We want the statement object so we can execute queries
    	Statement stat = database.getStatement();
    	
    	try {




    		// Fetch all the data

            stat.execute("SELECT * FROM user_profile WHERE UserID = " + userID + " LIMIT 1;");
            Debug.pl("Executed user_profile");
			this.userProfileResult = stat.getResultSet();

			stat.execute("SELECT * FROM userSNS WHERE followerUserID = " + userID + ";");
            Debug.pl("Executed userSNS");
			this.userSNSResult = stat.getResultSet();

			stat.execute("SELECT * FROM user_action WHERE userID = " + userID + ";");
            Debug.pl("Executed user_action");
			this.userActionResult = stat.getResultSet();
			
			// Build it, and he will come (the User, that is)
			setUserID();
            Debug.pl("UserID set");
	    	setBirthYear();
            Debug.pl("Birthyear set");
	    	setGender();
            Debug.pl("Gender set");
	    	setNumTweets();
            Debug.pl("Tweets set");
	    	setNumFollowing();
            Debug.pl("Following# set");
	    	setActions();
            Debug.pl("Setting done");


	    	// Clear the result sets, no need for those.
	    	userProfileResult.close();
	    	userSNSResult.close();
	    	userActionResult.close();
		} catch (SQLException e) { e.printStackTrace(); }
    	
    	
    }
    
    private void setActions() throws SQLException {
    	do {
    		int destUserID = userActionResult.getInt("destinationUserID");
    		int numComments = userActionResult.getInt("comment");
    		int numAtActions = userActionResult.getInt("atAction");
    		int numReTweets = userActionResult.getInt("reTweet");
    		this.numComments.put(destUserID, numComments);
    		this.numAtActions.put(destUserID, numAtActions);
    		this.numReTweets.put(destUserID, numReTweets);
    	} while (userActionResult.next());
	}

	/**
     * Sets the User ID
     */
    private void setUserID() throws SQLException {
    	this.userID = userProfileResult.getInt("UserID");
    }
    
    /**
     * Will check up the birth year from the database result
     */
    private void setBirthYear() throws SQLException {
    	this.birthyear = userProfileResult.getInt(2);   //birthYear
    }
    
    private void setGender() throws SQLException {
    	this.birthyear = userProfileResult.getInt(3);   //Gender
    }
    
    private void setNumTweets() throws SQLException {
    	this.numTweets = userProfileResult.getInt(4);   //tweets
    }
    
    private void setNumFollowing() throws SQLException {
    	int numFollowing = 0;
    	while (userSNSResult.next()) {
            Debug.pl("hello");
            userSNSResult.getInt(1);
    		numFollowing++;
            Debug.pl(numFollowing);
    	}
        Debug.pl("bye");
    	userSNSResult.first();
    	this.numFollowing = numFollowing;
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
}
