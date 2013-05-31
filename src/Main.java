import java.util.ArrayList;
import java.util.Iterator;

/**
 * The Main-class.
 * @author Lukas J. Wensby
 * @version 2013-05-15
 */

public class Main{


    public static void main(String []args) throws Exception{


/*    	Database db = new Database("DB_MAY30");
    	db.openConnection();
        long startTime = System.currentTimeMillis();

        //Database.vacuumDatabase(db);
        Database.refactorDatbase(db);
        //Database.dropAllTableIndexes(db);

        //DataPreparer dp = new DataPreparer(db,10000000);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Time in Ms: " + elapsedTime);
    	db.closeConnection();
        System.out.println("Done!");*/


        //Tormod_classifier.test_Svm()
    	 ArrayList<Integer> a= Parser.semiColon_Integer_parser("78;117;71;112;77;80;83;110;55;51");
    	 Iterator<Integer> it=a.iterator();
    	 while(it.hasNext()){
    		 Debug.pl(it.next());
    	 }

    }
}
