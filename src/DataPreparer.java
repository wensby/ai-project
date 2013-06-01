import java.io.File;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Vector;

/**
 * This function reads the training and test sets, and creates output files and structures directly applicable for classification algorithms.
 */
public class DataPreparer {
    private final int data_size_to_use;
    private final int MAX_RW_BULK_SIZE = 3000000;   // This is the maximum size that can be read at a time as one unit before Java runs out of heap space
    private int discarded_sample_count;
    private int log_files_created = 0;

    private final Database db;
    private final HashMap<Integer, Item> Items = new HashMap<Integer, Item>();
    private User cached_user;

    NumberFormat defaultFormat = NumberFormat.getPercentInstance();

    public DataPreparer(Database db ,int data_size_to_use) throws Exception{
        this.db = db;
        this.data_size_to_use = data_size_to_use;
        this.discarded_sample_count = 0;
        prepareTrainingSet();
    }

    public int getDiscarded_sample_count() {
        return discarded_sample_count;
    }

    private void prepareTrainingSet()throws Exception{
    	Object[] obj_list = null;
        Item tmp_item = null;
        User tmp_user = null;
        Vector<Integer> tmp_features = null;
        int tmp_userId;
        int tmp_itemId;
        int tmp_class;
        StringBuilder builder = new StringBuilder();
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime;
        long startTime = stopTime;
        long prev_elapsed = 0;

        try{
            for (int i = 1; i < Util.TOTAL_DATABASE_REC_LOG_TRAIN_LENGTH && i < this.data_size_to_use; i++){
                obj_list   = db.iter_getOneRow("rec_log_train",i);
                tmp_userId = (Integer)obj_list[1];
                tmp_itemId = (Integer)obj_list[2];
                tmp_class  = (Integer)obj_list[3];
                
//              Debug.p(" (itemID: " + tmp_itemId + ", userID: " + tmp_userId + ", Class: " + tmp_class);

                if (!this.Items.containsKey(tmp_itemId)){
                    tmp_item = new Item(tmp_itemId, this.db);
                    this.Items.put(tmp_item.getItemID(), tmp_item);
                }
                
                if (this.cached_user == null || this.cached_user.getUserID() != tmp_userId){
                    tmp_user = new User(tmp_userId, this.db);
                    this.cached_user = tmp_user;
                }
                
                // Constructing the feature
                Feature featureSet = new Feature(tmp_user, tmp_item);
                featureSet.useFeature(Feature.ITEM_BIRTH_YEAR);
                featureSet.useFeature(Feature.USER_BIRTH_YEAR);
                featureSet.finish();
                
                tmp_features = featureSet.getFeatureVector();
                
                if(tmp_features != null){
                    createLogFiles(builder,tmp_class + format_featureVector_for_SVM(tmp_features) + "\n",i);
                } else {
                    Debug.pl("Sample ignored");
                    this.discarded_sample_count ++;
                }

                // Runtime information and analysis
                startTime = System.currentTimeMillis();
                if(i%(this.data_size_to_use/100) == 0){
                    elapsedTime = startTime - stopTime;
                    stopTime = startTime;
                    Debug.pl("> Preparing training set... " + defaultFormat.format((float)(i) / data_size_to_use) + " Time increase since last progress(ms): " + (elapsedTime-prev_elapsed) + " Total running time since last progress: " + elapsedTime);
                    prev_elapsed = elapsedTime;
                }
            }
            commitRemainingLogFiles(builder);
            Debug.pl("Number of discarded samples: " + this.discarded_sample_count);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Creates a set of log files that does not go above the max_heap_size of Java by creating several text files
     * @param builder   The string builder used to build the strings
     * @param input     The string to append to the string builder
     * @param index     The current iteration of the algorithm
     */
    private void createLogFiles(StringBuilder builder, String input, int index)throws Exception{
        if (index%MAX_RW_BULK_SIZE == 0){
            // Create log file location if not exist
            File filepath = new File("../Logs/SVM_training_data__size_" + this.data_size_to_use);
            if (!filepath.exists()) filepath.mkdir();

            // Create log file
            String file = "F"+ log_files_created +".txt";
            this.writeStringBuilderToFile(filepath + "/" + file , builder);
            log_files_created ++;

            //Reset builder
            builder.delete(0,builder.length());
        } else {
            builder.append(input);
        }
    }

    public void commitRemainingLogFiles(StringBuilder builder)throws Exception{
        // Create log file location if not exist
        File filepath = new File("../Logs/SVM_training_data__size_" + this.data_size_to_use);
        if (!filepath.exists()) filepath.mkdir();

        // Create log file
        String file = "F"+ log_files_created +".txt";
        this.writeStringBuilderToFile(filepath + "/" + file , builder);
        log_files_created ++;

        //Reset builder
        builder.delete(0,builder.length());
    }

    private void writeStringBuilderToFile(String file, StringBuilder builder)throws Exception{
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.print(builder.toString());
        writer.close();
    }

    private String format_featureVector_for_SVM(Vector<Integer> v){
        String out = "";
        for (int i = 0; i<v.size(); i++){
            out += " "+(i+1)+":"+v.elementAt(i);
        }
        return out;
    }
}
