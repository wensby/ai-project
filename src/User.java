

import java.util.HashMap;

public class User {
	private final int userID;
	private final int year_of_birth;
	private final int gender;
	private final int numberOfTweets;
	private final HashMap<Integer, Double> keywords;
	private final int[][][][] actions;
	private final int[] follows;
	
    public User(int userID, int year_of_birth, int gender, int numberOfTweets,
    		HashMap<Integer, Double> keywords,
    		int[][][][] actions, int[] follows) {
    	
        this.userID = userID;
        this.year_of_birth = year_of_birth;
        this.gender = gender;
        this.numberOfTweets = numberOfTweets;
        this.actions = actions;
        this.follows = follows;
        this.keywords = keywords;
    }
    
    public HashMap<Integer, Double> getKeywords()
    {
    	return keywords;
    }
}
