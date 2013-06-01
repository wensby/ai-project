import libsvm.svm_problem;

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
        //prepareTrainingSet();
        SvmExample();
        //gridParameterCheckForSVM();
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


    public void SvmExample() throws Exception{
        // Get one entry from rec_log_train:
        Object[] obj_list = db.rand_getOneRow("rec_log_train");
        int tmp_userId = (Integer)obj_list[1];
        int tmp_itemId = (Integer)obj_list[2];
        int tmp_class  = (Integer)obj_list[3];
        int num_positive_samples = 0;

        // Get feature vector for the given item and user
        Feature featureSet = new Feature(new User(tmp_userId,db), new Item(tmp_itemId,db));
        featureSet.useFeature(Feature.ITEM_BIRTH_YEAR);
        featureSet.useFeature(Feature.USER_BIRTH_YEAR);
        featureSet.finish();
        Vector<Integer> v = featureSet.getFeatureVector();

        // Create problem set (training set) for the svm: specify the number of features on creation
        SvmInterface.Svm_problem prob = new SvmInterface.Svm_problem(v.size());


        for (int i= 0; i < data_size_to_use; i++){

            obj_list = db.rand_getOneRow("rec_log_train");
            //Debug.pal(obj_list);
            tmp_userId = (Integer)obj_list[1];
            tmp_itemId = (Integer)obj_list[2];
            tmp_class  = (Integer)obj_list[3];
            featureSet = new Feature(new User(tmp_userId,db), new Item(tmp_itemId,db));
            featureSet.useFeature(Feature.ITEM_BIRTH_YEAR);
            featureSet.useFeature(Feature.USER_BIRTH_YEAR);
            featureSet.finish();
            v = featureSet.getFeatureVector();

            // Append data points (outcome, features) to the problem set. Do this for all data points.
            prob.AppendTrainingPoint(tmp_class,v);

        }

        // When done appending data points, finalize the problem set. The set cannot be changed after this
        prob.FinalizeTrainingSet();

        // Create parameter object for the svm. This example uses all default values
        SvmInterface.Svm_parameter param = new SvmInterface.Svm_parameter(1,10,0.01);

        // See if the model with the given parameters are legal:
        boolean valid;
        valid = SvmInterface.CheckParameterValidity(prob,param);

        Debug.pl("Parameter set is valid: " + valid);

        // Now create the model for the SVM (this is in principle a trained SVM)
        Debug.pl("Training svm...");
        SvmInterface.Svm_model model = new SvmInterface.Svm_model(prob,param);

        // At this point you might want to save the trained svm (the svm model)
        model.Save();   // not yet implemented


        // You may now test the SVM with 10-fold cross-validation:

        double resulting_class;
        int num_samples = data_size_to_use/10;
        int correct_samples = 0;
        double correctness = 0.0;
        for (int i= 0; i < data_size_to_use/10; i++){

            obj_list = db.rand_getOneRow("rec_log_train");
            tmp_userId = (Integer)obj_list[1];
            tmp_itemId = (Integer)obj_list[2];
            tmp_class  = (Integer)obj_list[3];
            if(tmp_class == 1) num_positive_samples++;
            featureSet = new Feature(new User(tmp_userId,db), new Item(tmp_itemId,db));
            featureSet.useFeature(Feature.ITEM_BIRTH_YEAR);
            featureSet.useFeature(Feature.USER_BIRTH_YEAR);
            featureSet.finish();
            v = featureSet.getFeatureVector();

            // Append data points (outcome, features) to the problem set. Do this for all data points.
            resulting_class = SvmInterface.PredictSingleDataPoint(model, v);

            if((int)resulting_class == tmp_class) correct_samples ++;
        }

        correctness = (double)correct_samples/(double)num_samples;
        Debug.pl("Final correctness: " + correctness*100 + "%");
        Debug.pl("Percentage of samples of class +1: " + ((double)num_positive_samples/(double)num_samples)*100 + "%");
    }

    public void gridParameterCheckForSVM() throws Exception{
        // Get one entry from rec_log_train:
        Object[] obj_list = db.rand_getOneRow("rec_log_train");
        int tmp_userId = (Integer)obj_list[1];
        int tmp_itemId = (Integer)obj_list[2];
        int tmp_class  = (Integer)obj_list[3];
        int num_positive_samples = 0;

        // Get feature vector for the given item and user
        Feature featureSet = new Feature(new User(tmp_userId,db), new Item(tmp_itemId,db));
        featureSet.useFeature(Feature.ITEM_BIRTH_YEAR);
        featureSet.useFeature(Feature.USER_BIRTH_YEAR);
        featureSet.finish();
        Vector<Integer> v = featureSet.getFeatureVector();

        // Create problem set (training set) for the svm: specify the number of features on creation
        SvmInterface.Svm_problem prob = new SvmInterface.Svm_problem(v.size());


        for (int i= 0; i < data_size_to_use; i++){

            obj_list = db.rand_getOneRow("rec_log_train");
            //Debug.pal(obj_list);
            tmp_userId = (Integer)obj_list[1];
            tmp_itemId = (Integer)obj_list[2];
            tmp_class  = (Integer)obj_list[3];
            featureSet = new Feature(new User(tmp_userId,db), new Item(tmp_itemId,db));
            featureSet.useFeature(Feature.ITEM_BIRTH_YEAR);
            featureSet.useFeature(Feature.USER_BIRTH_YEAR);
            featureSet.finish();
            v = featureSet.getFeatureVector();

            // Append data points (outcome, features) to the problem set. Do this for all data points.
            prob.AppendTrainingPoint(tmp_class,v);

        }

        // When done appending data points, finalize the problem set. The set cannot be changed after this
        prob.FinalizeTrainingSet();


        for( int g = 0; g<1000; g++){
            for(double c=0; c<1000; c+=0.1){
                // Create parameter object for the svm. This example uses all default values
                SvmInterface.Svm_parameter param = new SvmInterface.Svm_parameter(g,c,0.01);

                // See if the model with the given parameters are legal:
                boolean valid;
                valid = SvmInterface.CheckParameterValidity(prob,param);
                if(valid) Debug.pl("Parameter set is valid: gamma = " + g +"   C = " + c);
            }
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
