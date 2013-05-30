
/**
 * The Main-class.
 * @author Lukas J. Wensby
 * @version 2013-05-15
 */

public class Main{


    public static void main(String []args) throws Exception{            ///REMOVE THROWS EXCEPTION
    	
    	// Transferring tables
    	Database from = new Database("extensionDB1_done2");
        Database dest = new Database("hopefully_not_corrupted");
        from.openConnection();
        dest.openConnection();

        Database.transferTable(from, dest, "itemKey");
        Database.transferTable(from, dest, "user_profile");
        Database.transferTable(from, dest, "rec_log_train");
        Database.transferTable(from, dest, "userSNS");
        
        from.closeConnection();
        dest.closeConnection();
    }
}