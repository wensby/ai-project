import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class toDB {
	
	
	public static void userProfile2DB(Database database, int offset) throws Exception{
		Debug.pl("> Parsing userProfile into database " + database.name + " (offset: " + offset + ")");

		if (!database.hasOpenConnection()) {
			Debug.pl("Error: Database don't have an open connection");
			return;
		}
		
		String file_place = "../data/user_profile.txt";
		String table_name = "user_profile";
		Parser.txt file = new Parser.txt(file_place); 
		String values;
        file.SkipToOffset(offset);
		int tag_id = 1;
		int autoID = offset+1;
        int counter = 0;
		while(file.hasNext()){
			ArrayList<String> entry_values = new ArrayList<String>();
			Parser.User_profile u_p = new Parser.User_profile(file.next());	
			entry_values.add(Integer.toString(u_p.userID));
			entry_values.add(Integer.toString(u_p.birthYear));
			entry_values.add(Integer.toString(u_p.gender));
			entry_values.add(Integer.toString(u_p.tweets));
			entry_values.add("'"+u_p.tagIDsString+"'");
			
			Iterator<Integer> it = u_p.tagIDs.iterator();
			while(it.hasNext()){
				int tag = it.next();				
				ArrayList<String> tag_entry_values = new ArrayList<String>();
				tag_entry_values.add(Integer.toString(tag_id));
				tag_entry_values.add(Integer.toString(u_p.userID));
				tag_entry_values.add(Integer.toString(tag));
				String v_tag= Database.valueFormatter(tag_entry_values);
				database.addToBatch("tags", v_tag);
				tag_id++;
			}
			values = Database.valueFormatter(entry_values);
			database.addToBatch(table_name, values);
			autoID++;


            if(counter%1000000 == 0){
                Debug.pl("Progression:  " + counter);
            }
            counter++;
			if(autoID%30000==0){
				database.executeBatch();
			}
			database.insert(table_name, values);
		}
	}
	
	public static void item2DB(Database database, int offset) throws Exception{
		Debug.pl("> Parsing item into database " + database.name + " (offset: " + offset + ")");
		
		if (!database.hasOpenConnection()) {
			Debug.pl("Error: Database don't have an open connection");
			return;
		}
		database.executeBatch();
		String file_place = "../data/item.txt";
		String table_name = "item";
		Parser.txt file = new Parser.txt(file_place); 
		String values;
		if (offset < 0) file.SkipToOffset(offset);

        int c = 0; // counter, so that the insert can be done in batches.
        int totalC = 0;
		while(file.hasNext()){
			ArrayList<String> entry_values = new ArrayList<String>();
			Parser.Item u_p = new Parser.Item(file.next());	
			entry_values.add(Integer.toString(u_p.id));
			entry_values.add("'" + u_p.categoriesString + "'");
			entry_values.add("'" + u_p.keywordsString + "'");
			values = Database.valueFormatter(entry_values);
			database.addToBatch(table_name, values);
			
			c++;
			totalC++;
			
			if (c >= 10) {
				database.executeBatch();
				Debug.pl(totalC + " inserts done...");
				c = 0;
			}
		}
		database.executeBatch();
	}
	
	public static void rec_log_train2DB(Database database, int offset) throws Exception{
		Debug.pl("> Parsing rec_log_train into database " + database.name + " (offset: " + offset + ")");

		if (!database.hasOpenConnection()) {
			Debug.pl("Error: Database don't have an open connection");
			return;
		}
		String file_place = "../data/rec_log_train.txt";
		String table_name = "rec_log_train";
		Parser.txt file = new Parser.txt(file_place); 
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

            database.addToBatch(table_name,values);

            // counter
            if(counter%100000 == 0){
                System.out.println("Progression:   " + counter);
                database.executeBatch();

            }
            counter++;
            autoid++;
		}

		database.executeBatch();
	}

    public static void rec_log_test2DB(Database database, int offset) throws Exception{
        Debug.pl("> Parsing rec_log_test into database " + database.name + " (offset: " + offset + ")");

        if (!database.hasOpenConnection()) {
            Debug.pl("Error: Database don't have an open connection");
            return;
        }

        Debug.pl("> NB: Table index has been dropped for quicker insertion. ");

        //String file_place = "../data/rec_log_test.txt";
        String file_place = "/Volumes/Ram Disk/rec_log_test.txt";
        String table_name = "rec_log_test";
        Parser.txt file = new Parser.txt(file_place);
        String values;
        int autoid=offset+1;
        file.SkipToOffset(offset);

        database.turn_autoCommit_off();

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

            database.addToBatch(table_name,values);

            // counter
            if(counter%100000 == 0){
                System.out.println("Progression:   " + counter);
                database.executeBatch();
                database.commitTransaction();

            }
            counter++;
            autoid++;
        }

        database.executeBatch();
        database.commitTransaction();
        database.turn_autoCommit_on();

    }
	
	public static void user_action2DB(Database database, int offset) throws Exception{
		Debug.pl("> Parsing user_action into database " + database.name + " (offset: " + offset + ")");
		
		if (!database.hasOpenConnection()) {
			Debug.pl("Error: Database don't have an open connection");
			return;
		}
		
		String file_place = "../data/user_action.txt";
		String table_name = "user_action";
		Parser.txt file = new Parser.txt(file_place); 
		String values;
		int autoid=offset+1;
		
        if (offset > 0) file.SkipToOffset(offset);

        int c = 0;
        int totalC = 0;

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
			
			database.addToBatch(table_name,values);
			
			c++;
			totalC++;
			autoid++;

            // counter
            if(c >= 1000){
                database.executeBatch();
                System.out.println("Progression:   " + totalC);
                c = 0;
            }
		}
		
		database.executeBatch(); // execute whatever left-over inserts that surplussed the last 10000 mark
	}

	public static void user_keyword2DB(Database database, int offset) throws Exception{
		Debug.pl("> Parsing user_keyword into database " + database.name + " (offset: " + offset + ")");

		if (!database.hasOpenConnection()) {
			Debug.pl("Error: Database don't have an open connection");
			return;
		}
		
		String file_place = "../data/user_key_word.txt";
		String table_name = "user_keywords";
		Parser.txt file = new Parser.txt(file_place); 
		database.ensureKeywordsTableExist();
		
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
				database.addToBatch(table_name, values);
				
				if (autoid % 300000 == 0)
				{
					database.executeBatch();
				}
			}
			
			database.executeBatch();
		}
	}
	
	public static void user_sns2DB(Database database, int offset) throws Exception{
		Debug.pl("> Parsing user_sns into database " + database.name + " (offset: " + offset + ")");

		if (!database.hasOpenConnection()) {
			Debug.pl("Error: Database don't have an open connection");
			return;
		}

        //String file_place = "../data/user_sns.txt";
        String file_place = "/Volumes/Ram Disk/user_sns.txt";
        String table_name = "userSNS";
        Parser.txt file = new Parser.txt(file_place);
        String values;
        int autoID = offset+1;
        file.SkipToOffset(offset);
        database.turn_autoCommit_off();

        while(file.hasNext()){

            ArrayList<String> entry_values = new ArrayList<String>();
            Parser.User_sns u_p = new Parser.User_sns(file.next());
            entry_values.add(Integer.toString(autoID));
            entry_values.add(Integer.toString(u_p.followerUserID));
            entry_values.add(Integer.toString(u_p.followeeUserID));
            values = Database.valueFormatter(entry_values);
            database.insert(table_name, values);
            autoID++;

            if(autoID%100000 == 0){
                Debug.pl("Remaining:  "  + (50655143-autoID));
                database.commitTransaction();
            }
        }

        database.commitTransaction();
        database.turn_autoCommit_on();
        database.closeConnection();
	}

	public static void itemKey2DB(Database database) throws Exception{
		//String file_place = "../data/item.txt";
        String file_place = "/Volumes/Ram Disk/item.txt";
		String table_name = "itemKey";
		Parser.txt file = new Parser.txt(file_place); 
	
		int autoid=1;

        database.turn_autoCommit_off();

		while(file.hasNext()){
			Parser.Item item_object = new Parser.Item(file.next());
			ArrayList<Integer> keywords = item_object.keywords;
			Iterator<Integer> it = keywords.iterator();
			while(it.hasNext()){
				int key = it.next();
				ArrayList<String> entry_values = new ArrayList<String>();
				entry_values.add(Integer.toString(autoid++));
				entry_values.add(Integer.toString(item_object.id));
				entry_values.add(Integer.toString(key));
				autoid++;
				
				String values = Database.valueFormatter(entry_values);
				database.addToBatch(table_name, values);
				
				if (autoid % 300000 == 0)
				{
					database.executeBatch();
                    database.commitTransaction();
				}
			}
			database.executeBatch();
            database.commitTransaction();
			
		}
        database.turn_autoCommit_on();

		database.closeConnection();
	}
}

