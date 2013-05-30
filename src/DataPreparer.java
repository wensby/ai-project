import java.io.File;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Vector;

/**
 * This function reads the training and test sets,
 */
public class DataPreparer {
    private final int data_size_to_use;
    private final int tbl_rec_log_train_size;
    private int discarded_sample_count;

    private final Database db;
    private final HashMap<Integer, Item> Items = new HashMap();
    private User cached_user;

    NumberFormat defaultFormat = NumberFormat.getPercentInstance();

    public DataPreparer(Database db ,int data_size_to_use) throws Exception{
        this.db = db;
        this.data_size_to_use = data_size_to_use;
        this.tbl_rec_log_train_size = db.length("rec_log_train");
        this.discarded_sample_count = 0;
        prepareTrainingSet();
    }

    public int getDiscarded_sample_count() {
        return discarded_sample_count;
    }

    private void prepareTrainingSet()throws Exception{
    	Debug.pl("> Preparing training set... 0%");
    	Object[] obj_list = null;
        Item tmp_item = null;
        User tmp_user = null;
        Vector<Integer> tmp_features = null;
        int tmp_userId;
        int tmp_itemId;
        int tmp_class;
        StringBuilder builder = new StringBuilder();

        try{
            for (int i = 0; i < this.tbl_rec_log_train_size && i < this.data_size_to_use; i++){
                obj_list   = db.getOneRow("rec_log_train", i);
                
                tmp_userId = (Integer)obj_list[1];
                tmp_itemId = (Integer)obj_list[2];
                tmp_class  = (Integer)obj_list[3];
                
//                Debug.p(" (itemID: " + tmp_itemId + ", userID: " + tmp_userId + ", Class: " + tmp_class);

                if (!this.Items.containsKey(tmp_itemId)){
                    tmp_item = new Item(tmp_itemId, this.db);
                    this.Items.put(tmp_item.getUserID(), tmp_item);
                }
                
                if (this.cached_user == null || this.cached_user.getUserID() != tmp_userId){
                    tmp_user = new User(tmp_userId, this.db);
                    this.cached_user = tmp_user;
                }

                tmp_features = Feature.getFeatureVector(tmp_user, tmp_item);
                if(tmp_features != null){
                    builder.append(tmp_class + format_featureVector_for_SVM(tmp_features) + "\n");
                } else {
                    // ignore sample
                    this.discarded_sample_count ++;
                }
                
                Debug.pl("> Preparing training set... " + defaultFormat.format((float)(i+1) / data_size_to_use));
            }
            File filepath = new File("../Logs/");
            if (!filepath.exists()) filepath.mkdir();
            String txt_file_path = "../Logs/SVM_training_data__size_" + this.data_size_to_use + ".txt";
            this.writeStringBuilderToFile(txt_file_path , builder);
            Debug.pl("Number of discarded samples: " + this.discarded_sample_count);
        }
        catch (Exception e){
            e.printStackTrace();
        }
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
