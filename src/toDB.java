import java.util.ArrayList;

public class toDB {
	public static void userProfile2DB() throws Exception{
		String file_place = "../Provided Data/KDD Cup Track 1 Data/track1/user_profile.txt";
		String table_name = "user_profile";
		Parser.txt file = new Parser.txt(file_place); 
		Database db = new Database();
		String values;
		
		while(file.hasNext()){
			ArrayList<String> entry_values = new ArrayList<String>();
			Parser.User_profile u_p = new Parser.User_profile(file.next());	
			entry_values.add(Integer.toString(u_p.userID));
			entry_values.add(Integer.toString(u_p.birthYear));
			entry_values.add(Integer.toString(u_p.gender));
			entry_values.add(Integer.toString(u_p.tweets));
			entry_values.add("'"+u_p.tagIDsString+"'");
			values = Database.valueFormatter(entry_values);
			db.insert(table_name, values);
		}
		
	}
	public static void item2DB() throws Exception{
		String file_place = "../Provided Data/KDD Cup Track 1 Data/track1/item.txt";
		String table_name = "item";
		Parser.txt file = new Parser.txt(file_place); 
		Database db = new Database();
		String values;
		
		while(file.hasNext()){
			ArrayList<String> entry_values = new ArrayList<String>();
			Parser.Item u_p = new Parser.Item(file.next());	
			entry_values.add(Integer.toString(u_p.id));
			entry_values.add("'"+u_p.categoriesString+"'");
			entry_values.add("'"+u_p.keywordsString+"'");
			values = Database.valueFormatter(entry_values);
			db.insert(table_name, values);
		}
		
	}
	public static void rec_log_train2DB() throws Exception{
		String file_place = "../Provided Data/KDD Cup Track 1 Data/track1/rec_log_train.txt";
		String table_name = "rec_log_train";
		Parser.txt file = new Parser.txt(file_place); 
		Database db = new Database();
		String values;
		int autoid=1;
		while(file.hasNext()){
			ArrayList<String> entry_values = new ArrayList<String>();
			
			Parser.rec_log_train u_p = new Parser.rec_log_train(file.next());	
			
			entry_values.add(Integer.toString(autoid));
			entry_values.add(Integer.toString(u_p.userID));
			entry_values.add(Integer.toString(u_p.ItemID));
			entry_values.add(Integer.toString(u_p.result));
			entry_values.add(Integer.toString(u_p.timeStamp));
			values = Database.valueFormatter(entry_values);
			autoid++;
			db.insert(table_name, values);
		}
		
	}

	public static void user_action2DB() throws Exception{
		String file_place = "../Provided Data/KDD Cup Track 1 Data/track1/user_action.txt";
		String table_name = "user_action";
		Parser.txt file = new Parser.txt(file_place); 
		Database db = new Database();
		String values;
		int autoid=1;

		while(file.hasNext()){
			ArrayList<String> entry_values = new ArrayList<String>();
			Parser.User_action u_p = new Parser.User_action(file.next());	
			entry_values.add(Integer.toString(autoid));
			entry_values.add(Integer.toString(u_p.userID));
			entry_values.add(Integer.toString(u_p.destinationUserID));
			entry_values.add(Integer.toString(u_p.atAction));
			entry_values.add(Integer.toString(u_p.reTweet));
			entry_values.add(Integer.toString(u_p.comment));
			values = Database.valueFormatter(entry_values);
			autoid++;

			db.insert(table_name, values);
		}
		
	}

	public static void user_key_word2DB() throws Exception{
		String file_place = "../Provided Data/KDD Cup Track 1 Data/track1/user_key_word.txt";
		String table_name = "user_key_word";
		Parser.txt file = new Parser.txt(file_place); 
		Database db = new Database();
		String values;
		int autoid=1;

		while(file.hasNext()){
			ArrayList<String> entry_values = new ArrayList<String>();
			Parser.User_action u_p = new Parser.User_action(file.next());	
			entry_values.add(Integer.toString(autoid));
			entry_values.add(Integer.toString(u_p.userID));
			entry_values.add(Integer.toString(u_p.destinationUserID));
			
			values = Database.valueFormatter(entry_values);
			db.insert(table_name, values);
			autoid++;
		}
		
	}
	
	public static void user_sns2DB() throws Exception{
		String file_place = "../Provided Data/KDD Cup Track 1 Data/track1/user_sns.txt";
		String table_name = "userSNS";
		Parser.txt file = new Parser.txt(file_place); 
		Database db = new Database();
		String values;
		int autoID = 1;
		while(file.hasNext()){
			ArrayList<String> entry_values = new ArrayList<String>();
			Parser.User_sns u_p = new Parser.User_sns(file.next());	
			entry_values.add(Integer.toString(autoID));
			entry_values.add(Integer.toString(u_p.followerUserID));
			entry_values.add(Integer.toString(u_p.followeeUserID));
			values = Database.valueFormatter(entry_values);
			db.insert(table_name, values);
			autoID++;
		}
		
	}
}

