import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;

public class Parser {
	public static int parse_year(String birthYear){	
		Pattern p = Pattern.compile("(?:19)|(?:20)[0-9][0-9]");
		
		Matcher m = p.matcher(birthYear);
		
		Boolean four_characters = birthYear.length() == 4;
		Boolean approved_year= m.find();
		if(four_characters && approved_year){
			return Integer.parseInt(birthYear);
		}else{
			return 0;
		}
	}
	public static  ArrayList<Integer> semiColon_Integer_parser(String tagIDs_not_parsed){
		StringTokenizer stTagID = new StringTokenizer(tagIDs_not_parsed,";");
		ArrayList<Integer> tagIDs= new ArrayList<Integer>();
		while(stTagID.hasMoreTokens()){
			Integer tagID = Integer.parseInt(stTagID.nextToken());
					tagIDs.add(tagID);
		}
		return tagIDs;
	}
    public static ArrayList<Integer> dot_Integer_parser(String catString){
    	StringTokenizer st = new StringTokenizer(catString,".");
    	ArrayList<Integer> cat = new ArrayList<Integer>();
    	while(st.hasMoreTokens())
    	{
    		cat.add(Integer.parseInt(st.nextToken()));
    	}
		return cat;
    }
	
	public static class User_profile_txt{
		public int userID;
		public int birthYear;//Check if characters, check if all four character are digits, return 0 if not
		public int gender;
		public int tweets;
		public ArrayList<Integer> tagIDs;
		
		public User_profile_txt(String input_line){
			//Given line from user_profile_txt: Parse out (userID) (birthyear) (gender) (#tweets) (Tag-IDs) 
			StringTokenizer st = new StringTokenizer(input_line);
				this.userID =	Integer.parseInt(st.nextToken());
				this.birthYear =Parser.parse_year(st.nextToken());//Check if characters, check if all four character are digits, return 0 if not
				this.gender =	Integer.parseInt(st.nextToken());
				this.tweets =	Integer.parseInt(st.nextToken());
				this.tagIDs = 	Parser.semiColon_Integer_parser(st.nextToken());
			}
	}
	public static class Item_txt{
	    public int id;
	    public ArrayList<Integer> categories = new ArrayList<Integer>();
	    public ArrayList<Integer> keywords = new ArrayList<Integer>();
		
	    public Item_txt(String line)
		{
			StringTokenizer sTok = new StringTokenizer(line);
			this.id = Integer.parseInt(sTok.nextToken());
			this.categories = Parser.dot_Integer_parser(sTok.nextToken());
			this.keywords = Parser.semiColon_Integer_parser(sTok.nextToken());
		}
	}
	
	public static class rec_log_train_txt{
		public int userID;
		public int ItemID;
		public int result;
		public int timeStamp;
	
		public rec_log_train_txt(String line){
			StringTokenizer sTok = new StringTokenizer(line);
			this.userID = Integer.parseInt(sTok.nextToken());
			this.ItemID = Integer.parseInt(sTok.nextToken());
			this.result = Integer.parseInt(sTok.nextToken());
			this.timeStamp = Integer.parseInt(sTok.nextToken());
		}
	}
	
	public static class user_action_txt{
		public int userID;
		public int destinationUserID;
		public int atAction;
		public int reTweet;
		public int comment;
	
		public user_action_txt(String line){
			StringTokenizer sTok = new StringTokenizer(line);
			this.userID = Integer.parseInt(sTok.nextToken());
			this.destinationUserID = Integer.parseInt(sTok.nextToken());
			this.atAction = Integer.parseInt(sTok.nextToken());
			this.reTweet = Integer.parseInt(sTok.nextToken());
			this.comment = Integer.parseInt(sTok.nextToken());
		}
	}
	
	public static class user_sns_txt{
		public int followerUserID;
		public int followeeUserID;
	
		public user_sns_txt(String line){
			StringTokenizer sTok = new StringTokenizer(line);
			this.followerUserID = Integer.parseInt(sTok.nextToken());
			this.followeeUserID = Integer.parseInt(sTok.nextToken());
		}
	}
	
	public static class  user_key_word_txt{
		public int UserID;
		public HashMap<Integer, Integer> keywords = new HashMap<Integer, Integer>();
	
		public user_key_word_txt(String line){
			StringTokenizer sTok = new StringTokenizer(line);
			this.UserID = Integer.parseInt(sTok.nextToken());
			this.keywords = this.keyword_rank_parser(sTok.nextToken());
		}
		private HashMap<Integer, Integer> keyword_rank_parser(String keywords_not_parsed){
			HashMap<Integer, Integer> keyword= new HashMap<Integer, Integer>();
			
			StringTokenizer sTok = new StringTokenizer(keywords_not_parsed,";");
			
			while(sTok.hasMoreTokens()){
				StringTokenizer stKeyword = new StringTokenizer(sTok.nextToken(),":");
				Integer k = Integer.parseInt(stKeyword.nextToken());
				Integer w = Integer.parseInt(stKeyword.nextToken());
				keyword.put(k, w);
			}
			return keywords;
		}
	}
}
