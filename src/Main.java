import java.util.LinkedList;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class Main{


    public static void main(String []args) throws Exception{

    	Database db = new Database("DB_JUN5");
    	db.openConnection();
        long startTime = System.currentTimeMillis();


        DataPreparer dp = new DataPreparer(db,10);

        //Debug.pt("t1");
        //Debug.p("  getFolloweesFromDB"); Debug.pt("t2");
        //Debug.p("  setCategoriFromDB"); Debug.pt("t3");
        //Debug.p("  setFollowerKeysFromDB"); Debug.pt("t4");
        //Debug.p("  setFollowerTagsFromDB"); Debug.pt("t5");
        //Debug.pt("t10");
        Debug.pt("hash");
        Debug.pt("hash2");


        //SvmInterface.Example.TestSimpleSvm();

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Time in Ms: " + elapsedTime);
    	db.closeConnection();
        System.out.println("Done!");

        //Tormod_classifier.test_Svm();

        /*
    	SvmTree tree = new SvmTree("1110");
    	tree.addSvm("filepath1", "1000");
    	tree.addSvm("filepath2", "0100");
    	LinkedList<String> svms = tree.getSvms("0100");
    	Debug.pl(svms.getFirst());
        */
    }
}
