import java.util.Calendar;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * 
 * @author Lukas J. Wensby
 * @version 2013-06-09
 */
public class Feature {
	/**
	 * A singleton class that helps with generating a feature structure string.
	 * @author Lukas J. Wensby
	 * @version 2013-06-09
	 */
	public static class FeatureStructureGenerator {
		private static StringBuilder structure = new StringBuilder();
		
		/**
		 * This method has to be called before generating a new feature structure string
		 */
		public static void clearNew() {
			structure = new StringBuilder();
			for (int i = 0; i < Feature.NUM_FEATURES; i++) {
				structure.append('0');
			}
		}
		
		public static String getAllOnes() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < Feature.NUM_FEATURES; i++) sb.append('1');
			return sb.toString();
		}
		
		public static String getAllZeros() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < Feature.NUM_FEATURES; i++) sb.append('0');
			return sb.toString();
		}
		
		public static void useFeature(int featureIndex) {
			structure.setCharAt(featureIndex, '1');
		}
		
		public static String getFeatureStructure() {
			return "FEATURE_STRUCTURE(" + structure.toString() + ")";
		}
		
		public static String getFeatureStructurePure() {
			return structure.toString();
		}
	}
	
	// The amount of different possible features
	public static final int NUM_FEATURES = 20;
	
	// Feature array indices
	// Remember, ONLY add to this list, and use completely new indices (also increase the NUM_FEATURES)
	public static final int USER_BIRTH_YEAR		 	= 0;
	public static final int USER_GENDER 			= 1;
    public static final int ITEM_BIRTH_YEAR		 	= 2;
    public static final int ITEM_GENDER			 	= 3;
	public static final int USER_NUM_TWEETS		 	= 4;
	public static final int USER_NUM_FOLLOWING		= 5;
	public static final int ITEM_NUM_TWEETS		 	= 6;
	public static final int ITEM_NUM_FOLLOWING 	 	= 7;
	public static final int NUM_COMMENTS_BETWEEN 	= 8;
	public static final int NUM_AT_ACTION_BETWEEN	= 9;
	public static final int NUM_RETWEETS_BETWEEN	= 10;
	public static final int NUM_FOLLOWERS_IN_COMMON = 11;
	public static final int DIFF_YEARS 				= 12;
	public static final int COMMENT_RATIO			= 13;
	public static final int AT_ACTION_RATIO			= 14;
	public static final int RETWEETS_RATIO			= 15;
	public static final int ITEM_NUM_FOLLOWERS		= 16;
	public static final int USER_AGE_RANK			= 17;
	public static final int ITEM_AGE_RANK			= 18;
	public static final int NUM_FOLLOWED_FOLLOWERS	= 19;

	
	private Double[] featureVector;
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
		featureVector = new Double[NUM_FEATURES];
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
					" features than is currently implemented.");
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < parsed.length(); i++) {
			if (parsed.charAt(i) != '0') useFeature(i);
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
	public Vector<Double> getFeatureVector() {
		if (!finished) {
			Debug.pl("! ERROR: The Feature object instance has to be set as finished before featureVector can be retrieved.");
			return null;
		}
		
		Vector<Double> returner = new Vector<Double>();
		for (Double f : featureVector) {
			if (f != null) {
                returner.add(f);
            } else {
                returner.add(-1.0);
            }
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
				featureVector[USER_BIRTH_YEAR] = (double) user.getBirthYear();
				break;
			case(USER_GENDER) :
				featureVector[USER_GENDER] = (double) user.getGender();
				break;
			case(USER_NUM_TWEETS) :
				featureVector[USER_NUM_TWEETS] = (double) user.getNumTweets();
				break;
			case(USER_NUM_FOLLOWING) :
				featureVector[USER_NUM_FOLLOWING] = (double) user.getNumFollowees();
				break;
			case(ITEM_BIRTH_YEAR) :	
				featureVector[ITEM_BIRTH_YEAR] = (double) item.getBirthYear();
				break;
			case(ITEM_GENDER) :
				featureVector[ITEM_GENDER] = (double) item.getGender();
				break;
			case(ITEM_NUM_TWEETS) :
				featureVector[ITEM_NUM_TWEETS] = (double) item.getNumTweets();
				break;
			case(ITEM_NUM_FOLLOWING) : 
				featureVector[ITEM_NUM_FOLLOWING] = (double) item.getNumFollowees();
				break;
			case(NUM_COMMENTS_BETWEEN) :
				featureVector[NUM_COMMENTS_BETWEEN] = (double) calcNumCommentsBetween(user, item);
				break;
			case(NUM_AT_ACTION_BETWEEN) :
				featureVector[NUM_AT_ACTION_BETWEEN] = (double) calcNumAtActionBetween(user, item);
				break;
			case(NUM_RETWEETS_BETWEEN) :
				featureVector[NUM_RETWEETS_BETWEEN] = (double) calcNumReTweetsBetween(user, item);
				break;
			case(NUM_FOLLOWERS_IN_COMMON) :
				featureVector[NUM_FOLLOWERS_IN_COMMON] = (double) calcNumFolloweesInCommon(user, item);
				break;
			case (DIFF_YEARS):
				featureVector[DIFF_YEARS] = (double) (user.getBirthYear() - item.getBirthYear());
				break;
			case (COMMENT_RATIO):
				featureVector[COMMENT_RATIO] = ((double) calcNumCommentsBetween(user,item) / user.getNumTweets());
				break;
			case (AT_ACTION_RATIO):
				featureVector[AT_ACTION_RATIO] = ((double) calcNumAtActionBetween(user,item) / user.getNumTweets());
			break;
			case (RETWEETS_RATIO):
				featureVector[RETWEETS_RATIO] = ((double) calcNumReTweetsBetween(user,item) / user.getNumTweets());
				break;
			case (ITEM_NUM_FOLLOWERS):
				featureVector[ITEM_NUM_FOLLOWERS] = ((double) item.getNumFollowers());
				break;
			case (USER_AGE_RANK):
				featureVector[USER_AGE_RANK] = ((double) calcAgeRank(user.getBirthYear()));
				break;
			case (ITEM_AGE_RANK):
				featureVector[ITEM_AGE_RANK] = ((double) calcAgeRank(item.getBirthYear()));
				break;
			case (NUM_FOLLOWED_FOLLOWERS) :
				featureVector[NUM_FOLLOWED_FOLLOWERS] = ((double) calcNumFollowedFollowers(user, item));
				break;
			default :
				Debug.pl("! ERROR: Did not recognize the featureIndex.");
		}
	}

	private static int calcNumReTweetsBetween(User user, Item item) {
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

	private static int calcNumAtActionBetween(User user, Item item) {
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

	private static int calcNumCommentsBetween(User user, Item item) {
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
	private static int calcNumFolloweesInCommon(User user, Item item) {
		return Util.calcCommonElements(user.getFollowees(), item.getFollowees());
	}
	
	/**
	 * Checks the number of followees that the user that follows the given item.
	 * @param user is the user that will be used in this feature construction.
	 * @param item is the item that will be used in this feature construction.
	 * @return an integer number that represents how many user followee follows the items
	 */
	private static int calcNumFollowedFollowers(User user, Item item) {
		return Util.calcCommonElements(user.getFollowees(), item.getFollowers());
	}
	
	/**
	 * Calculate an age rank
	 * @param birthYear
	 * @return
	 */
	private double calcAgeRank(int birthYear) {
		float age = Calendar.getInstance().get(Calendar.YEAR) - birthYear;
		int rank = 1;
		
		if      (age < 17)  rank = 2;
		else if (age < 21)	rank = 3;
		else if (age < 25)	rank = 4;
		else if (age < 32)	rank = 5;
		else if (age < 40)	rank = 6;
		else if (age < 55)	rank = 7;
		else				rank = 8;
		
		return rank;
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
		for (Double f : featureVector) {
			if (f != null) sb.append("1");
			else sb.append("0");
		}
		sb.append(")");
		
		return sb.toString();
	}

    /**
     * Will construct a featureStructure String that specifies the set of features this Feature
     * object has used. This method only works if this object has been set as finished.<br>
     * The string will look like: "00110101"
     */
    public String generateFeatureStructureStringPure() {
        if (!finished) {
            Debug.pl("! ERROR: The Feature object instance has to be set as finished before the feature structure string can be generated.");
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Double f : featureVector) {
            if (f != null) sb.append("1");
            else sb.append("0");
        }
        return sb.toString();
    }
	
	/**
	 * Simple method that just reformats a String formated as "FEATURE_STRUCTURE(00110101)" to
	 * a String formated as "00110101".
	 */
	public static String trimFeatureStructureString(String featureStructure) {
		if (Pattern.matches("[0-9]+", featureStructure))
		    return featureStructure;
		else
			return featureStructure.substring(18, featureStructure.length() - 1);
	}

    /**
     * Counts the number of active features (ones) in the input feature structure string.
     */
    public static int countNumFeatures(String featureStructure){
        int count = 0;
        for(int i = 0; i<featureStructure.length();i++) if(featureStructure.charAt(i) == '1') count ++;
        //return count;       //TODO CHANGE BACK IF NEEDED. This change was made to incorporate the new getFeatureVector method
                              //TODO that sets all null feature values to -1 instead of leaving them out.
        return featureStructure.length();
    }

}
