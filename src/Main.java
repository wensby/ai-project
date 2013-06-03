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

    	Debug.start("test");
    	Debug.start("test2");
    	
    	float a = 100000000;
    	for (int i = 0; i < a; i++) {
    		float b = 2;
    		b = a + b;
    		Debug.stop("test2");
    		Debug.start("test2");
    	}
    	
    	Debug.stop("test");
    	
    	Debug.pt("test");
    	Debug.pt("test2");
    	
    	/**

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
		*/
    }
}
