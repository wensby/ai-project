/**
 * The Main-class.
 * @author Lukas J. Wensby
 * @version 2013-05-15
 */

public class Main{


    public static void main(String []args) throws Exception{            ///REMOVE THROWS EXCEPTION

        // CURRENTLY RUNNING
    	Database db = new Database("DB_MAY30");
    	db.openConnection();

        Database.get_read_performance_of_rec_log(db);

    	db.closeConnection();
        System.out.println("Done!");


/*
        // TESTING CONFIGURATION
        Database db = new Database("extensionDB1");
        db.openConnection();





        long startTime = System.currentTimeMillis();

        DataPreparer dp = new DataPreparer(db, 1000);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Time in Ms: " + elapsedTime);


        //toDB.userProfile2DB(db,0);

        //User user = new User(100136,db);



        db.closeConnection();
        System.out.println("Done!");
*/
    }
}