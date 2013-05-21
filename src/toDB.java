import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class toDB {
	public static void userProfile2DB(int offset) throws Exception{
		String file_place = "../data/user_profile.txt";
		String table_name = "user_profile";
		Parser.txt file = new Parser.txt(file_place); 
		Database db = new Database();
		String values;
        file.SkipToOffset(offset);
		
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
		
		db.close_connection();
	}
	public static void item2DB(int offset) throws Exception{
		String file_place = "../data/item.txt";
		String table_name = "item";
		Parser.txt file = new Parser.txt(file_place); 
		Database db = new Database();
		String values;
        file.SkipToOffset(offset);

		while(file.hasNext()){
			ArrayList<String> entry_values = new ArrayList<String>();
			Parser.Item u_p = new Parser.Item(file.next());	
			entry_values.add(Integer.toString(u_p.id));
			entry_values.add("'"+u_p.categoriesString+"'");
			entry_values.add("'"+u_p.keywordsString+"'");
			values = Database.valueFormatter(entry_values);
			db.insert(table_name, values);
		}
		
		db.close_connection();
	}
	public static void rec_log_train2DB(int offset) throws Exception{
		String file_place = "../data/rec_log_train.txt";
		String table_name = "rec_log_train";
		Parser.txt file = new Parser.txt(file_place); 
		Database db = new Database();
		String values;
		int autoid=offset+1;
        file.SkipToOffset(offset);


        // counter
        int counter = 0;

		while(file.hasNext()){
            ArrayList<String> entry_values = new ArrayList<String>();
			
			Parser.rec_log_train u_p = new Parser.rec_log_train(file.next());	

			entry_values.add(Integer.toString(autoid));
			entry_values.add(Integer.toString(u_p.userID));
			entry_values.add(Integer.toString(u_p.ItemID));
			entry_values.add(Integer.toString(u_p.result));
			entry_values.add(Integer.toString(u_p.timeStamp));
			values = Database.valueFormatter(entry_values);         //This is a string
            db.addToBatch(table_name,values);

            // counter
            if(counter%100000 == 0){
                System.out.println("Progression:   " + counter);
                db.executeBatch();
            }
            counter++;
            autoid++;
		}

        db.executeBatch();
		db.close_connection();
	}
	public static void user_action2DB(int offset) throws Exception{
		String file_place = "../data/user_action.txt";
		String table_name = "user_action";
		Parser.txt file = new Parser.txt(file_place); 
		Database db = new Database();
		String values;
		int autoid=offset+1;
        file.SkipToOffset(offset);

        int counter = 0;

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
            db.addToBatch(table_name,values);

            // counter
            if(counter%100000 == 0){
                System.out.println("Progression:   " + counter);
                db.executeBatch();
            }
            counter++;
            autoid++;
		}
		
		db.close_connection();
	}

	public static void user_keyword2DB() throws Exception{
		String file_place = "../Provided Data/KDD Cup Track 1 Data/track1/user_key_word.txt";
		String table_name = "user_keywords";
		Parser.txt file = new Parser.txt(file_place); 
		Database db = new Database();
		String values;
		int autoid=offset+1;
        file.SkipToOffset(offset);

		while(file.hasNext()){
			Parser.User_key_word user_keyword = new Parser.User_key_word(file.next());
			
			int userID = user_keyword.UserID;
			HashMap<Integer, Double> keywords = user_keyword.keywords;
			Iterator<Entry<Integer, Double>> iterator = keywords.entrySet().iterator();
			
			while (iterator.hasNext())
			{
				ArrayList<String> entry_values = new ArrayList<String>();
				Entry<Integer,Double> entry = iterator.next();
			
				entry_values.add(Integer.toString(autoid++));
				entry_values.add(Integer.toString(userID));
				entry_values.add(Integer.toString(entry.getKey()));
				entry_values.add(Double.toString(entry.getValue()));
				
				values = Database.valueFormatter(entry_values);
				db.insert(table_name, values);
			}
		}

		db.close_connection();
	}
	
	public static void user_sns2DB(int offset) throws Exception{
		String file_place = "../data/user_sns.txt";
		String table_name = "userSNS";
		Parser.txt file = new Parser.txt(file_place); 
		Database db = new Database();
		String values;
		int autoID = offset+1;
        file.SkipToOffset(offset);

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
		
		db.close_connection();
	}
}

