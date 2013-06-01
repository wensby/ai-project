import java.util.Vector;
import java.util.regex.Pattern;

/**
 * Feature class.
 * @author Lukas J. Wensby
 * @version 2013-06-01
 */

public class Feature {
	// The amount of different possible features
	public static final int NUM_FEATURES = 12;
	
	// Feature array indices
	// Remember, if you add or remove from this list, use completely new indices.
	public static final int USER_BIRTH_YEAR		 	= 0;
	public static final int USER_GENDER 			= 1;
	public static final int USER_NUM_TWEETS		 	= 2;
	public static final int USER_NUM_FOLLOWING		= 3;
	public static final int ITEM_BIRTH_YEAR		 	= 4;
	public static final int ITEM_GENDER			 	= 5;
	public static final int ITEM_NUM_TWEETS		 	= 6;
	public static final int ITEM_NUM_FOLLOWING 	 	= 7;
	public static final int NUM_COMMENTS_BETWEEN 	= 8;
	public static final int NUM_AT_ACTION_BETWEEN	= 9;
	public static final int NUM_RETWEETS_BETWEEN	= 10;
	public static final int NUM_FOLLOWERS_IN_COMMON = 11;
	
	private Vector<Integer> featureVector = new Vector<Integer>(NUM_FEATURES);
	private User user = null;
	private Item item = null;
	private boolean finished = false;
	
	/**
	 * Default constructor where we specify which features should be used in this particular 
	 * instance. <p>
	 * Warning: When this feature is later finished, meaning that every feature that you want has
	 * been loaded into it, it should be set as finished. Doing so will remove the connections
	 * it had with the User and Item objects, so that the garbage collector is free to work its 
	 * magic.
	 */
	public Feature(User user, Item item) {	
		this.user = user;
		this.item = item;
	}
	
	/**
	 * Constructs, fills and finishes a new Feature object based on specified on a feature 
	 * structure String.
	 * @param featureStructure is the string that specifies the structure of this Feature. This 
	 * string MUST either look like "FEATURE_STRUCTURE(0011...0101)" or "01010...0101" where the 
	 * number of 1 or 0 is smaller or equal to {@link Feature#NUM_FEATURES}.
	 * @see Feature#generateFeatureStructureString()
	 */
	 
	public Feature(User user, Item item, String featureStructure) {
		this(user, item);
		
		// Interpret feature structure string
		String parsed;
		if (Pattern.matches("[0-9]+", featureStructure)) {
		    parsed = featureStructure;
		}
		else {
			parsed = featureStructure.substring(18, featureStructure.length() - 1);
		}
		if (parsed.length() > NUM_FEATURES) {
			Debug.pl("! ERROR: The data in the featureStructure argument seems to indicate more" +
					" features than is currently possible.");
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < parsed.length(); i++) {
			if (parsed.charAt(i) != '0')
				useFeature(i);
		}

		
		finish();
	}
	
	/**
	 * Removes this Feature instance's connection with the User and Item objects. So after this
	 * method has been called, no further features can be loaded into this Feature instance.
	 * This method MUST be called once a feature is considered finished. Since we don't want to
	 * have many that User and Item loaded into runtime.
	 */
	public void finish() {
		user = null;
		item = null;
		finished = true;
	}
	
	/**
	 * Will return the features that are loaded into this Feature instance, as a vector of Integers.
	 * If, for example, 'user birth year' was not put into this feature, it will not have a place 
	 * in this returned vector.<br>
	 * As a side note: This method can only return a featureVector if this Feature instance has
	 * been "finished" 
	 * @see Feature#finish()
	 */
	public Vector<Integer> getFeatureVector() {
		if (!finished) {
			Debug.pl("! ERROR: The Feature object instance has to be set as finished before featureVector can be retrieved.");
			return null;
		}
		
		Vector<Integer> returner = new Vector<Integer>();
		for (Integer f : featureVector) {
			if (f != null) returner.add(f);
		}
		return returner;
	}
	
	/**
	 * Will make this Feature instance use the feature specified, that is, you can select if this
	 * user for example should use the item's birth year.
	 * @param featureIndex is any of the static integers from the Feature class. For example: Feature.ITEM_BIRTH_YEAR
	 */
	public void useFeature(int featureIndex) {
		switch(featureIndex) {
			case(USER_BIRTH_YEAR) : 
				featureVector.set(USER_BIRTH_YEAR, user.getBirthYear());
				break;
			case(USER_GENDER) :
				featureVector.set(USER_GENDER, user.getGender());
				break;
			case(USER_NUM_TWEETS) :
				featureVector.set(USER_NUM_TWEETS, user.getNumTweets());
				break;
			case(USER_NUM_FOLLOWING) :
				featureVector.set(USER_NUM_FOLLOWING, user.getNumFollowing());
				break;
			case(ITEM_BIRTH_YEAR) :	
				featureVector.set(ITEM_BIRTH_YEAR, item.getBirthYear());
				break;
			case(ITEM_GENDER) :
				featureVector.set(ITEM_GENDER, item.getGender());
				break;
			case(ITEM_NUM_TWEETS) :
				featureVector.set(ITEM_NUM_TWEETS, item.getNumTweets());
				break;
			case(ITEM_NUM_FOLLOWING) : 
				featureVector.set(ITEM_NUM_FOLLOWING, item.getNumFollowing());
				break;
			case(NUM_COMMENTS_BETWEEN) :
				featureVector.set(NUM_COMMENTS_BETWEEN, calcNumCommentsBetween(user, item));
				break;
			case(NUM_AT_ACTION_BETWEEN) :
				featureVector.set(NUM_AT_ACTION_BETWEEN, calcNumAtActionBetween(user, item));
				break;
			case(NUM_RETWEETS_BETWEEN) :
				featureVector.set(NUM_RETWEETS_BETWEEN, calcNumReTweetsBetween(user, item));
				break;
			case(NUM_FOLLOWERS_IN_COMMON) :
				featureVector.set(NUM_FOLLOWERS_IN_COMMON, calcNumFolloweesInCommon(user, item));
				break;
			default :
				Debug.pl("! ERROR: Did not recognize the featureIndex.");
		}
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

	
	/**
	 * Will construct a featureStructure String that specifies the set of features this Feature 
	 * object has used. This method only works if this object has been set as finished.<br>
	 * The string will look like: "FEATURE_STRUCTURE(00110101)"
	 */
	public String generateFeatureStructureString() {
		if (!finished) {
			Debug.pl("! ERROR: The Feature object instance has to be set as finished before the feature structure string can be generated.");
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("FEATURE_STRUCTURE(");
		for (Integer f : featureVector) {
			if (f != null) sb.append("1");
			else sb.append("0");
		}
		sb.append(")");
		
		return sb.toString();
	}
}
