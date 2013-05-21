/**
 * The Main-class.
 * @author Lukas J. Wensby
 * @version 2013-05-15
 */

public class Main{


    public static void main(String []args) throws Exception{
    	System.out.println("Starting item2DB");
    	toDB.item2DB();
    	System.out.println("Starting rec_log_train2DB");
    	toDB.rec_log_train2DB();
    	System.out.println("Starting user_action2DB");
    	toDB.user_action2DB();
    	System.out.println("Starting user_key_word2DB");
    	toDB.user_key_word2DB();
    	System.out.println("Starting user_sns2DB");
    	toDB.user_sns2DB();
    	System.out.println("Starting userProfile2DB");
    	toDB.userProfile2DB();
    	System.out.println("Finnished");
    }

    // Can you see this
    // YES I can! :D
}