import com.sun.xml.internal.bind.v2.TODO;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 * The Main-class.
 * @author Lukas J. Wensby
 * @version 2013-05-15
 */

public class Main{


    public static void main(String []args) throws Exception{



    	Database db = new Database("DB_JUN3");
    	db.openConnection();
        long startTime = System.currentTimeMillis();



        DataPreparer dp = new DataPreparer(db,100);

        Debug.pt("t1");
        Debug.pt("t2");


        //SvmInterface.Example.TestSimpleSvm();

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Time in Ms: " + elapsedTime);
    	db.closeConnection();
        System.out.println("Done!");

        //Tormod_classifier.test_Svm();

    }
}
