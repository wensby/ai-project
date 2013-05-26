/**
 * The Main-class.
 * @author Lukas J. Wensby
 * @version 2013-05-15
 */

public class Main{

    public static void main(String []args) throws Exception {
    	Database test = new Database("test");
    	test.openConnection();
    	toDB.user_action2DB(test, 0);
    	test.closeConnection();
    }
}