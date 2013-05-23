/**
 * The Main-class.
 * @author Lukas J. Wensby
 * @version 2013-05-15
 */

public class Main{
    public static void main(String []args) throws Exception {
    	Database db = new Database();
    	db.MoveIntoEmptyDatabase("hopefully_not_corrupted.sqlite");
    }
    
    public static void loadEverythingIntoDatabase() throws Exception {
        /*
        // Start timer
        long startTime = System.currentTimeMillis();

        System.out.println("start");
        //TwitterData.ReadFile_UserProfile();
        //Database.testDB();
        //Database.testDatabaseSettings();
        System.out.println("stop");

        // Stop timer
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);

        */

        /*
        Database.count_file_lines("../data/rec_log_train.txt");
        Database.count_file_lines("../data/item.txt");
        Database.count_file_lines("../data/user_profile.txt");
        Database.count_file_lines("../data/user_action.txt");
        Database.count_file_lines("../data/user_sns.txt");
        Database.count_file_lines("../data/user_key_word.txt");
        */

        //Database.get_read_performance_of_rec_log();

    	//toDB.item2DB();
    	//toDB.rec_log_train2DB(50059843);
/*
        System.out.println("Running user_action");
    	toDB.user_action2DB(6220243);

        System.out.println("Running user_key_word");          //TODO This function runs actions not keywords!
    	//toDB.user_key_word2DB(0);

        System.out.println("Running user_sns");
    	toDB.user_sns2DB(0);

        System.out.println("Running user_profile");
    	toDB.userProfile2DB(0);

        System.out.println("Done!");
*/


//    	toDB.rec_log_train2DB(0);
//        System.out.println("Running user_action");
//    	toDB.user_action2DB(185610+1);
//        System.out.println("Running user_key_word");
//    	toDB.user_key_word2DB(0);
//        System.out.println("Running user_sns");
//    	toDB.user_sns2DB(0);
//        System.out.println("Running user_profile");
//    	toDB.userProfile2DB(0);
//        System.out.println("Done!");
    }
}