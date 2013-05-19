

import java.util.*;

public class User {
	private final int userID;
	private final int year_of_birth;
	private final int gender;
	private final int numberOfTweets;
	private final HashMap<Integer, Double> keywords = new HashMap();
	private final int[][][][] actions;
	private final int[] follows;
	
    public User(int userID, int year_of_birth, int gender, int numberOfTweets, int[][][][] actions, int[] follows) {
        this.userID = userID;
        this.year_of_birth = year_of_birth;
        this.gender = gender;
        this.numberOfTweets = numberOfTweets;
        this.actions = actions;
        this.follows = follows;
    }
}
