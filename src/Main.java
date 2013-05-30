
/**
 * The Main-class.
 * @author Lukas J. Wensby
 * @version 2013-05-15
 */

public class Main{


    public static void main(String []args) throws Exception{            ///REMOVE THROWS EXCEPTION
    	
    	// Transferring tables
    	Database database = new Database("total");
    	database.openConnection();
    	
    	DataPreparer preparer = new DataPreparer(database, 3);
    	
    	database.closeConnection();
    }
}