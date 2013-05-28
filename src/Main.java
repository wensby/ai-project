/**
 * The Main-class.
 * @author Lukas J. Wensby
 * @version 2013-05-15
 */

public class Main{


    public static void main(String []args) throws Exception{            ///REMOVE THROWS EXCEPTION

        // CURRENTLY RUNNING
    	Database db = new Database("extensionDB1");
    	db.openConnection();
        //toDB.rec_log_train2DB(db,55908215);
        //db.backup();
        toDB.userProfile2DB(db,0);
        toDB.user_sns2DB(db,3353921);

        db.backup();
    	db.closeConnection();

        System.out.println("Done!");


        /*
        // TESTING CONFIGURATION
        Database db = new Database("extensionDB1");
        db.openConnection();





        long startTime = System.currentTimeMillis();
        // do something
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Time in Ms: " + elapsedTime);


        //toDB.userProfile2DB(db,0);
        //DataPreparer dp = new DataPreparer(db, 1000);
        //User user = new User(100136,db);



        db.closeConnection();
        System.out.println("Done!");
        */
    }
}