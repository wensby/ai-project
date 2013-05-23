/**
 * The Main-class.
 * @author Lukas J. Wensby
 * @version 2013-05-15
 */

public class Main{

    public static void main(String []args) throws Exception{            ///REMOVE THROWS EXCEPTION

        //System.out.println("Running user_key_word");
   	    //toDB.user_keyword2DB(0);

        //System.out.println("Running rec_log_train");
        //toDB.rec_log_train2DB(53729440+1);

        //System.out.println("Running user_sns");
    	//toDB.user_sns2DB(975678+1);

        //Database.get_read_performance_of_rec_log();

        //System.out.println("Running user_profile");
    	//toDB.userProfile2DB(0);

        Database old_db = new Database();
        old_db.backup_DB();




        System.out.println("Done!");
    }
}