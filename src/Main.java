import java.util.HashMap;

/**
 * The Main-class.
 * @author Lukas J. Wensby
 * @version 2013-05-15
 */

public class Main{


    public static void main(String []args) throws Exception{            ///REMOVE THROWS EXCEPTION

        // TESTING CONFIGURATION
        Database db = new Database("test");
        db.openConnection();

        System.out.println("Loading items");
        HashMap<Integer,Item> items = db.getItems();
        System.out.println("Item loaded");
        
        Solver slvr = new Solver();
        slvr.train(db, items);



        db.closeConnection();
        System.out.println("Done!");

    }
}