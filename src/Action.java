/**
 * Action class
 * @author Abou "SteelStyle" Konaté
 */

public class Action {
	int userSource;
	int userDestination;
	
	int references;
	int retweets;
	int comments;
	
	/**
	 * 
	 * @param source The ID of the user making the action
	 * @param dest The ID of the user the action is destinated to
	 * @param refNb Number of reference
	 * @param retweetNb Number of retweets
	 * @param commentNb Number of comments
	 */
	Action (int source, int dest, int refNb, int retweetNb, int commentNb)
	{
		userSource = source;
		userDestination = dest;
		references = refNb;
		retweets = retweetNb;
		comments = commentNb;
	}
}
