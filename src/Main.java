/**
 * The Main-class.
 * @author Lukas J. Wensby
 * @version 2013-05-15
 */

public class Main{


    public static void main(String []args) throws Exception{            ///REMOVE THROWS EXCEPTION

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

    	toDB.item2DB();
    	toDB.rec_log_train2DB();
    	toDB.user_action2DB();
    	toDB.user_key_word2DB();
    	toDB.user_sns2DB();
    	toDB.userProfile2DB();
    }

    // Can you see this
    // YES I can! :D
}