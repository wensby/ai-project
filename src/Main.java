import com.sun.xml.internal.bind.v2.TODO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

/**
 * The Main-class.
 * @author Lukas J. Wensby
 * @version 2013-05-15
 */

public class Main{


    public static void main(String []args) throws Exception{



    	Database db = new Database("DB_JUN2");
    	db.openConnection();
        long startTime = System.currentTimeMillis();

        //DataPreparer dp = new DataPreparer(db,1000);

        SvmInterface.Example.TestSimpleSvm();


        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Time in Ms: " + elapsedTime);
    	db.closeConnection();
        System.out.println("Done!");


        //Tormod_classifier.test_Svm();

    }
}
