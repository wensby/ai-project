import java.io.File;
import java.util.HashSet;
import java.util.Vector;

public class Util {
	// These constants are based upon the total database, if you are using another database...
	// any function that uses these will most definitely not work.
	public static final int TOTAL_DATABASE_ITEM_LENGTH 			    = 6095;
	public static final int TOTAL_DATABASE_ITEMKEY_LENGTH 			= 202802;
	public static final int TOTAL_DATABASE_REC_LOG_TRAIN_LENGTH 	= 73209272;
	public static final int TOTAL_DATABASE_REC_LOG_TEST_LENGTH 	    = 34910937;
	public static final int TOTAL_DATABASE_USERSNS_LENGTH 			= 50655143;
	public static final int TOTAL_DATABASE_USER_ACTION_LENGTH 		= 10620243;
	public static final int TOTAL_DATABASE_USER_KEYWORDS_LENGTH 	= 16150704;
	public static final int TOTAL_DATABASE_USER_PROFILE_LENGTH 	    = 2320895;
	
	/**
	 * Calculates the amount of elements that two integer vectors have in common.
	 */
	public static int calcCommonElements(HashSet<Integer> a, HashSet<Integer> b) {
		int commonElements = 0;
		for (Integer j : b) if (a.contains(j)) commonElements++;
		return commonElements;
	}
	
    public static void count_file_lines(String file_place) throws Exception{
        //EX: file_place = "../data/rec_log_train.txt";
        Parser.txt file = new Parser.txt(file_place);
        int counter = 0;
        while(file.hasNext()){
            file.next();
            counter++;
        }
        System.out.println("Final line count of file "+ file_place + " :   " + counter);
    }

    /**
     * Returns true if specified file exists.
     */
    public static boolean checkFileExistence(String filepath) {
    	File f = new File(filepath);
    	if (f.exists()) return true;
		return false;
    }

}
