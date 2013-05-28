/**
 * The Main-class.
 * @author Lukas J. Wensby
 * @version 2013-05-15
 */

public class Main{


    public static void main(String []args) throws Exception{            ///REMOVE THROWS EXCEPTION

        /* CURRENTLY RUNNING
    	Database db = new Database("extensionDB1");
    	db.openConnection();
        toDB.rec_log_train2DB(db,55908215);
        db.backup();
        toDB.user_sns2DB(db,3353921);
    	db.closeConnection();

        System.out.println("Done!");
        */

        // TESTING CONFIGURATION
        Database db = new Database("old");
        db.openConnection();





        db.closeConnection();
        System.out.println("Done!");

    }
}