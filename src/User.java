

import java.util.*;

public class User {
	private final int userID;
	private final int year_of_birth;
	private final int gender;
	private final int numberOfTweets;
	private final HashMap<Integer, Double> keywords = new HashMap();
	private final int[][][][] actions;
	private final int[] follows;
	
    public User(int userID) {
        this.userID = userID;
        this.year_of_birth = 0;
        this.gender = 0;
        this.numberOfTweets = 0;
        this.actions = null;
        this.follows = null;
    }


    /**
     * Searches the
     * @param userID
     * @return
     * @throws Exception
     */
    public static User getUser(int userID)throws Exception{





        return null;
    }

    public int getUserID() {
        return userID;
    }
}
