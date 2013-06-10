/**
 * @author Lukas J. Wensby
 * @version 2013-05-31
 */

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class User {
	private int userID;
	private int birthyear;
	private int gender;
	private int numTweets;
	private int numFollowee;
	private int numFollowers;


	private HashMap<Integer, Integer> numComments = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> numAtActions = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> numReTweets = new HashMap<Integer, Integer>();
	private HashSet<Integer> followers;
	private HashSet<Integer> followees;
	

	/**
	 * Constructs a User object of user with specified ID from the data in the specified database.
	 * @throws Exception 
	 */
	
    public User(int userID, Database database) throws Exception{
    	if (!database.hasOpenConnection()) {
    		Debug.pl("! ERROR: Can't create User object when the database connection is closed.");
    		throw new IllegalArgumentException("Database object must have an open connection.");
    	}
        this.followers = this.getFollowersFromDB(userID, database);
        this.numFollowers = this.followers.size();
        this.followees = this.getFolloweesFromDB(userID, database);
        this.numFollowee = this.followees.size();
    	this.setUserID_birthYear_gender_num_Tweets_FromDB(userID, database);
    	this.setActionsFromDB(userID, database);
    }


	private HashSet<Integer>  getFollowersFromDB(int userID, Database db) throws SQLException{
    	Statement statm = db.createStatement();
    	ResultSet res = statm.executeQuery("SELECT followerUserID FROM userSNS WHERE followeeUserID = " + userID + ";");
		HashSet<Integer> followers = new HashSet<Integer>();
    	while(res.next()){
			followers.add(res.getInt("followerUserID"));
		}
		statm.close();
		res.close();
		return followers;
    }
    private HashSet<Integer>  getFolloweesFromDB(int userID, Database db) throws SQLException{
    	Statement statm = db.createStatement();
    	ResultSet res = statm.executeQuery("SELECT followeeUserID FROM userSNS WHERE followerUserID = " + userID + ";");
    	HashSet<Integer> followees = new HashSet<Integer>();
    	while(res.next()){
			followees.add(res.getInt("followeeUserID"));
		}
		statm.close();
		res.close();
		return followees;
    }
	private void setUserID_birthYear_gender_num_Tweets_FromDB(int userID, Database database) throws Exception{
		ResultSet userProfileResult; // will contain the result set from the user_profile query
		Statement statProfile = database.createStatement();
		statProfile.execute("SELECT * FROM user_profile WHERE UserID = " + userID + " LIMIT 1;");
		userProfileResult = statProfile.getResultSet();
		if (userProfileResult.next()){
			this.userID = userProfileResult.getInt("UserID");
			this.birthyear = userProfileResult.getInt("birthYear");
			this.gender = userProfileResult.getInt("gender");
			this.numTweets = userProfileResult.getInt("tweets");
			}else{
               throw new Exception("User not found, with id: " + this.userID);
		}
		statProfile.close();
		userProfileResult.close();
	}
    private void setActionsFromDB(int userID, Database db) throws SQLException {
    	ResultSet userActionResult; // will contain the result set from the userAction query
		Statement statAction = db.createStatement();
		statAction.execute("SELECT * FROM user_action WHERE userID = " + userID + ";");
		userActionResult = statAction.getResultSet();
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
    
    public int getNumFollowees() {
    	return numFollowee;
    }
    
    public HashSet<Integer> getFollowees() {
		return followees;
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
	public int getNumFollowers() {
		return numFollowers;
	}
    public HashSet<Integer> getFollowers() {
		return followers;
	}

	public HashSet<Integer> getfollowing() {
		return followees;
	}
    /**
     * Returns the vector of user IDs that this user is following.
     */

}
