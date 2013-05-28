/**
 * The Main-class.
 * @author Lukas J. Wensby
 * @version 2013-05-15
 */

public class Main{


    public static void main(String []args) throws Exception{            ///REMOVE THROWS EXCEPTION
    	svm.svm_predict a = new svm.svm_predict();
    	a.
        //System.out.println("Running user_key_word");
   	    //toDB.user_keyword2DB(0);

       // System.out.println("Running rec_log_train");
       // toDB.rec_log_train2DB(55786612);

       // System.out.println("Running user_sns");
       // toDB.user_sns2DB(3353921+1);

        //Database.get_read_performance_of_rec_log();

        //System.out.println("Running user_profile");
    	//toDB.userProfile2DB(0);

        System.out.println("Done!");

    	Database test = new Database("test");
    	test.openConnection();
    	toDB.user_action2DB(test, 0);
    	test.closeConnection();
    }
}