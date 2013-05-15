import java.util.StringTokenizer;


public class ActionParser {
	
	public Action readAction(String line)
	{
		StringTokenizer st = new StringTokenizer(line);
		
		int userSource = Integer.parseInt(st.nextToken());
		int userDestination = Integer.parseInt(st.nextToken());
		int references = Integer.parseInt(st.nextToken());
		int retweets = Integer.parseInt(st.nextToken());
		int comments = Integer.parseInt(st.nextToken());
		
		return new Action(userSource, userDestination,
				references, retweets, comments);
	}
}

