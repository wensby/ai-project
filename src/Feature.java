import java.util.Vector;

/**
 * Feature class
 * @author Lukas J. Wensby
 */

public class Feature {
	public static final int NUM_FEATURES = 12;
	
	// Feature array indices
	private static final int USER_BIRTH_YEAR		 = 0;
	private static final int USER_GENDER 			 = 1;
	private static final int USER_NUM_TWEETS		 = 2;
	private static final int USER_NUM_FOLLOWING		 = 3;
	private static final int ITEM_BIRTH_YEAR		 = 4;
	private static final int ITEM_GENDER			 = 5;
	private static final int ITEM_NUM_TWEETS		 = 6;
	private static final int ITEM_NUM_FOLLOWING 	 = 7;
	private static final int NUM_COMMENTS_BETWEEN 	 = 8;
	private static final int NUM_AT_ACTION_BETWEEN	 = 9;
	private static final int NUM_RETWEETS_BETWEEN	 = 10;
	private static final int NUM_FOLLOWERS_IN_COMMON = 11;
	
	/**
	 * Empty constructor since only the static method getFeatureVector will be used.
	 */
	public Feature(int userID, int itemID, Database db) {	
		
	}
	
	/**
	 * Will construct the feature vector of Integers and return it.
	 */
	public static Vector<Integer> getFeatureVector(User user, Item item) {
		// Construct the holder for the features
		Vector<Integer> featureVector = new Vector<Integer>();
		featureVector.ensureCapacity(NUM_FEATURES);
		
		// Fill it away, Scotty!
		featureVector.add(USER_BIRTH_YEAR, user.getBirthYear());
		featureVector.add(USER_GENDER, user.getGender());
		featureVector.add(USER_NUM_TWEETS, user.getNumTweets());
		featureVector.add(USER_NUM_FOLLOWING, user.getNumFollowing()); 
		featureVector.add(ITEM_BIRTH_YEAR, item.getBirthYear());
		featureVector.add(ITEM_GENDER, item.getGender());
		featureVector.add(ITEM_NUM_TWEETS, item.getNumTweets());
		featureVector.add(ITEM_NUM_FOLLOWING, item.getNumFollowing()); 
		featureVector.add(NUM_COMMENTS_BETWEEN, calcNumCommentsBetween(user, item));
		featureVector.add(NUM_AT_ACTION_BETWEEN, calcNumAtActionBetween(user, item));
		featureVector.add(NUM_RETWEETS_BETWEEN, calcNumReTweetsBetween(user, item));
		featureVector.add(NUM_FOLLOWERS_IN_COMMON, calcNumFolloweesInCommon(user, item));
		
		return featureVector;
	}
	
	private static Integer calcNumReTweetsBetween(User user, Item item) {
		int fromUser = 0;
		int toUser = 0;
		
		if (user.getNumReTweets().containsKey(item.getUserID())){
			fromUser = user.getNumReTweets().get(item.getUserID());
		}
		
		if (item.getNumReTweets().containsKey(user.getUserID())){
			toUser = item.getNumReTweets().get(user.getUserID());
		}
		
		return fromUser + toUser;
	}

	private static Integer calcNumAtActionBetween(User user, Item item) {
		int fromUser = 0;
		int toUser = 0;
		
		if (user.getNumAtActions().containsKey(item.getUserID())){
			fromUser = user.getNumAtActions().get(item.getUserID());
		}
		
		if (item.getNumAtActions().containsKey(user.getUserID())){
			toUser = item.getNumAtActions().get(user.getUserID());
		}
		
		return fromUser + toUser;
	}

	private static Integer calcNumCommentsBetween(User user, Item item) {
		int fromUser = 0;
		int toUser = 0;
		try {
            if (user.getNumComments().containsKey(item.getUserID())){
                fromUser = user.getNumComments().get(item.getUserID());
            }

            if (item.getNumComments().containsKey(user.getUserID())){
                toUser = item.getNumComments().get(user.getUserID());
            }
        } catch (Exception e){
            Debug.pl("Failed on UserID: " + user.getUserID() + " and ItemID: " + item.getItemID());
            e.printStackTrace();
        }
		return fromUser + toUser;
	}
	
	/**
	 * Checks the number of followees that the user and the item have in common.
	 * @param user is the user that will be used in this feature construction.
	 * @param item is the item that will be used in this feature construction.
	 * @return an integer number that represents how many followees they have in common.
	 */
	private static Integer calcNumFolloweesInCommon(User user, Item item) {
		return Util.calcCommonElements(user.getFollowing(), item.getFollowing());
	}
	
	//Gather necessary data:
}
