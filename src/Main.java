/**
 * The Main-class.
 * @author Lukas J. Wensby
 * @version 2013-05-15
 */

public class Main{
    public static void main(String []args) throws Exception {
    	Database db = new Database();
    	db.getOneRow(0, "item");
    	// loadEverythingIntoDatabase();
    }
    
    public static void loadEverythingIntoDatabase() throws Exception {
    	toDB.item2DB();
    	toDB.rec_log_train2DB();
    	toDB.user_action2DB();
    	toDB.user_key_word2DB();
    	toDB.user_sns2DB();
    	toDB.userProfile2DB();
    }
    
    
}