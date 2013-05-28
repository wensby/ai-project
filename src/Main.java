/**
 * The Main-class.
 * @author Lukas J. Wensby
 * @version 2013-05-15
 */

public class Main{

    public static void main(String []args) throws Exception {
    	Database from = new Database("ML_twitter_database");
    	Database dest = new Database("dest");
    	
    	from.openConnection();
    	dest.openConnection();
    	
    	Database.transferTable(from, dest, "item");
    	
    	from.closeConnection();
    	dest.closeConnection();
    }
}