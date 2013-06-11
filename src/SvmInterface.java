import com.oracle.jrockit.jfr.InvalidEventDefinitionException;
import libsvm.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

/**
 * This class allows for the following actions:
 *
 *  1. Create svm_problem objects
 *      1a. Set parameters for svm_problem objects
 *      1b.
 *  2. Create svm_parameters objects
 *      2a. Set parameters for the svm_parameter objects
 *      2b.
 *  3. Scale the input through svm_scale to a -1 to 1 range
 *  4. Check that the parameter and model is usable through svm.check_parameters
 *  5. Run training with a svm_problem and svm_parameter as input, giving a svm_model as output
 *  6. Save and load svm_model objects to/from files
 *  7. Predict result for a single data point
 *  8. Find optimized parameters through a parameter grid search
 */
/*

Parameter options: (stripped down)
        -d degree : set degree in kernel function (default 3)
        -g gamma : set gamma in kernel function (default 1/num_features)
        -r coef0 : set coef0 in kernel function (default 0)
        -c cost : set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1)
        -e epsilon : set tolerance of termination criterion (default 0.001)
        The k in the -g option means the number of attributes in the input data.
*/

public abstract class SvmInterface {
    // SVM types: (default C_SVC)
    private static final int C_SVC = 0;

    //Kernel types: (default radial_basis RBF)
    private static final int rbf = 2;

    public static class Svm_problem extends SvmInterface {
        private boolean finished;
        private svm_problem problem = new svm_problem();
        private int training_set_length;
        private int num_features;
        private ArrayList<Integer> training_outcomes = new ArrayList<Integer>();
        private ArrayList<ArrayList<svm_node>> training_features = new ArrayList<ArrayList<svm_node>>();

        /**
         * Creates a svm problem object, which can be considered the training set object for the svm.
         * Use the AppendTrainingPoint function to insert data into the training set.
         */
        public Svm_problem(int num_features) {
            if(num_features <= 0) throw new IllegalArgumentException("Cannot create an svm problem set with a zero or negative number of features");
            this.num_features = num_features;
            training_set_length = 0;
            finished = false;
        }

        /**
         * Append a data point with it's features and outcome to the svm problem (training) set.
         */
        public void AppendTrainingPoint(Integer outcome, Vector<Double> features){
            try{
                if (finished) throw new IllegalArgumentException("The current training set is already finished, and can therefore not be appended.");
                if (outcome != 1 && outcome != -1) throw new IllegalArgumentException("Illegal outcome value: " + outcome);
                if(features == null) throw  new IllegalArgumentException("The input feature vector cannot have a NULL value.");
                if(features.size()==0) throw  new IllegalArgumentException("The input feature vector is empty.");

                    ArrayList<svm_node> tmp_features = new ArrayList<svm_node>();
                for (int i = 0; i<features.size(); i++){
                    tmp_features.add(createSvmNode(i+1,features.get(i)));
                }

                training_features.add(tmp_features);
                training_outcomes.add(outcome);
                training_set_length ++;
            } catch (Exception e){e.printStackTrace();}
        }

        /**
         * Convert the temporary svm problem set into a proper format. The problem set cannot be appended after finalization.
         */
        public void FinalizeTrainingSet(){
            double[] tmp_y = new double[training_set_length];
            svm_node[][] tmp_x = new svm_node[training_set_length][num_features];

            for(int i = 0; i<training_set_length; i++){
                tmp_y[i] = training_outcomes.get(i);
                for (int j = 0; j < training_features.get(i).size(); j++){
                    tmp_x[i][j] = training_features.get(i).get(j);
                }
            }
            problem.l = training_set_length;
            problem.x = tmp_x;
            problem.y = tmp_y;
            finished = true;
        }

        public svm_problem GetProblem() throws InstantiationException{
            if(!finished) throw new InstantiationException("The svm problem cannot be accessed as it is not finalized.");
            return this.problem;
        }

        public boolean isFinished(){
            return this.finished;
        }

        public int GetNumFeatures(){
            return this.num_features;
        }


        public int GetSize() {
            return training_set_length;
        }

        public void scale(){
            //Discontinued
        }

    }

    /**
     * Creates a RBF kernel using default parameters, but with variable gamma, C and stopping tolerance.
     */
    public static class Svm_parameter extends SvmInterface{
        private svm_parameter parameter = new svm_parameter();

        /**
         * Creates the parameter set for an svm.
         * @param gamma Gamma parameter for the radial basis function kernel. Default 1/num_features.
         * @param c     Slack variable. A high value will allow more slack. Default 1.
         * @param tol   Termination criterion tolerance value. Default 0.01.
         */
        public Svm_parameter(double gamma, double c, double tol) {
            parameter.kernel_type = rbf;
            parameter.svm_type = C_SVC;
            parameter.degree = 3;
            parameter.cache_size = 10;
            SetGamma(gamma);
            SetC(c);
            SetStoppingTolerance(tol);
        }

        public Svm_parameter(int num_features) {
            parameter.kernel_type = rbf;
            parameter.svm_type = C_SVC;
            parameter.degree = 3;
            parameter.cache_size = 10;
            parameter.eps = 0.001;
            parameter.C = 1;
            parameter.gamma = 1.0/(double)num_features;
        }

        public void SetGamma(double gamma){
            if (gamma >= 0){
                this.parameter.gamma = gamma;
            } else {
                Debug.pl("> Setting parameter failed: Cannot set negative gamma.");
            }
        }

        public void SetC(double c){
            if (c >= 0){
                this.parameter.C = c;
            } else {
                Debug.pl("> Setting parameter failed: Cannot set negative C.");
            }
        }

        public void SetStoppingTolerance(double tol){
            if (tol >= 0){
                this.parameter.eps = tol;
            } else {
                Debug.pl("> Setting parameter failed: Cannot set negative stopping tolerance.");
            }
        }

        public svm_parameter GetParameter(){
            return this.parameter;
        }

        public Svm_parameter Copy(){
            return new Svm_parameter(parameter.gamma, parameter.C, parameter.eps);
        }


    }

    public static class Svm_model extends SvmInterface {
        private svm_problem problem;
        private svm_parameter parameter;
        private svm_model model;
        private int num_features;

        public Svm_model(Svm_problem prob, Svm_parameter param) {
            try{
                if(prob == null || param == null) throw new NullPointerException("Svm model cannot take NULL as input.");
                if(!prob.isFinished()) throw new NullPointerException("Svm problem not finalized.");
                if(svm.svm_check_parameter(prob.GetProblem(),param.GetParameter()) != null) throw new IllegalArgumentException("SVM parameter check failed.");
                this.problem = prob.GetProblem();
                this.parameter = param.GetParameter();
                this.num_features = prob.GetNumFeatures();
                train();
            } catch (Exception e){e.printStackTrace();}
        }

        public void Save(String filename){
            try{
                if(filename == null || filename.equals("")) throw new IllegalArgumentException("No filename provided");
                File path = new File("../SvmModels/");
                if (!path.exists()) path.mkdir();
                // Save model:
                svm.svm_save_model("../SvmModels/"+filename+".txt",this.model);
            }
            catch (Exception e) {e.printStackTrace();}
        }

        public void Save(String filename, String path){
            try{
                if(filename == null || filename.equals("")) throw new IllegalArgumentException("No filename provided");
                if(path == null || path.equals("")) throw new IllegalArgumentException("No path provided");
                File dir = new File(path);
                if (!dir.exists()) dir.mkdir();
                // Save model:
                svm.svm_save_model(path+"/"+filename+".txt",this.model);
            }
            catch (Exception e) {e.printStackTrace();}
        }

        public void Load(String filename){
            try{
                if(filename == null || filename.equals("")) throw new IllegalArgumentException("No filename provided");
                File path = new File("../SvmModels/");
                if (!path.exists()) throw new IllegalArgumentException("The default folder ../SvmModels/ does not exist");
                String filepath  = "../SvmModels/"+filename+".txt";
                File file = new File(filepath);
                if (!file.exists()) throw new IllegalArgumentException("The file "+ file +" does not exist.");
                // Load model:
                this.model = svm.svm_load_model(filepath);
            }
            catch (Exception e) {e.printStackTrace();}
        }

        public void Load(String filename, String path){
            try{
                if(filename == null || filename.equals("")) throw new IllegalArgumentException("No filename provided");
                if(path == null || path.equals("")) throw new IllegalArgumentException("No path provided");
                File dir = new File(path);
                if (!dir.exists()) throw new IllegalArgumentException("The given folder " + path + " does not exist");
                String filepath  = path+"/"+filename+".txt";
                File file = new File(filepath);
                if (!file.exists()) throw new IllegalArgumentException("The file "+ file +" does not exist.");
                // Load model:
                this.model = svm.svm_load_model(filepath);
            }
            catch (Exception e) {e.printStackTrace();}

        }

        private void train(){
            this.model = svm.svm_train(this.problem,this.parameter);
        }

        public svm_model GetModel(){
            return this.model;
        }

        public int GetNumFeatures(){
            return this.num_features;
        }

        public Svm_model Copy(Svm_problem prob, Svm_parameter param){
            return new Svm_model(prob,param);
        }

    }


    public static class Svm_test_obj extends SvmInterface {
        private svm_problem set_obj = new svm_problem();
        private int test_set_length;
        private int num_features;
        private ArrayList<Integer> test_outcomes = new ArrayList<>();
        private ArrayList<ArrayList<svm_node>> test_features = new ArrayList<>();

        /**
         * Creates a svm test container object, which can be considered the test set for the svm.
         * Use the AppendTestSet function to insert data into the training set.
         * Unlike the Svm_problem object, this object does not require finalization, but is merely a structured way of
         * creating test sets containing several test points.
         */
        public Svm_test_obj(int num_features) {
            if(num_features <= 0) throw new IllegalArgumentException("Cannot create an svm test set with a zero or negative number of features");
            this.num_features = num_features;
            test_set_length = 0;
        }

        /**
         * Append a data point with it's features and outcome to the svm problem (training) set.
         */
        public void AppendTestPoint(Integer outcome, Vector<Double> features) throws IllegalArgumentException {
            if (outcome != 1 && outcome != -1) throw new IllegalArgumentException("Illegal outcome value: " + outcome);
            if(features == null) throw  new IllegalArgumentException("The input feature vector cannot have a NULL value.");
            if(features.size()==0) throw  new IllegalArgumentException("The input feature vector is empty.");

            ArrayList<svm_node> tmp_features = new ArrayList<>();
            for (int i = 0; i<features.size(); i++){
                tmp_features.add(createSvmNode(i+1,features.get(i)));
            }

            test_features.add(tmp_features);
            test_outcomes.add(outcome);
            test_set_length ++;
        }

        public int GetNumFeatures(){
            return this.num_features;
        }

        public int GetSize() {
            return test_set_length;
        }

        public int GetOutcome(int index){
            if(index < 0 || index >= test_outcomes.size()) throw new IllegalArgumentException("Index out of bounds: " + index + " Array.size() = " + test_outcomes.size());
            return test_outcomes.get(index);
        }

        public svm_node[] GetFeatureNodeArray(int index){
            if(index < 0 || index >= test_features.size()) throw new IllegalArgumentException("Index out of bounds: " + index + " Array.size() = " + test_features.size());
            return (svm_node[]) test_features.get(index).toArray(new svm_node [test_features.get(index).size()]);
        }
    }

    public static class Example extends SvmInterface{
        private static Vector<Double> getPositiveFeatureVector(){
            Vector<Double> v = new  Vector<Double>();
            v.add(1.0);
            v.add(-1.0);
            v.add(1.0);
            v.add(-1.0);
            return v;
        }

        private static Vector<Double> getNegativeFeatureVector(){
            Vector<Double> v = new  Vector<Double>();
            v.add(-1.0);
            v.add(1.0);
            v.add(-1.0);
            v.add(1.0);
            return v;
        }

        public static void TestSimpleSvm(){
            int num_features = 4;
            int num_training_samples = 100;
            Svm_parameter param = new Svm_parameter(num_features);
            Svm_problem prob = new Svm_problem(num_features);

            for(int i=0; i<num_training_samples/2;i++){
                prob.AppendTrainingPoint(1,getPositiveFeatureVector());
            }

            for(int i=0; i<num_training_samples/2;i++){
                prob.AppendTrainingPoint(-1, getNegativeFeatureVector());
            }

            prob.FinalizeTrainingSet();

            if (CheckParameterValidity(prob,param)){
                Debug.pl("Parameter validity check passed.");
            }

            Svm_model model = new Svm_model(prob,param);
            model.Save("model1");
            model.Load("model1");

            int correct = 0;
            int num_test_samples = num_training_samples/10;
            for(int i=0; i<num_test_samples/2;i++){
                if(1 == PredictSingleDataPoint(model,getPositiveFeatureVector())) correct++;
                if(-1 == PredictSingleDataPoint(model, getNegativeFeatureVector())) correct++;
            }
            double correctness = (double)correct/(double)num_test_samples;

            Debug.pl("Finished SVM example with correctness of: " + correctness*100 + "%");
        }
    }

    /**
     * Predicts the class for a single data point, and returns the class.
     */
    public static double PredictSingleDataPoint(Svm_model model, Vector<Double> features){
        if(model == null) throw new IllegalArgumentException("Input model Null");
        if(model.GetModel() == null) throw new IllegalArgumentException("Input model GetModel Null");
        if(model.GetNumFeatures() != features.size()) throw new IllegalArgumentException("The number of features in the feature vector and the model does not match.");
        return svm.svm_predict(model.GetModel(), createNodeArray(features));
    }

    /**
     * Calculates the average correctness for the model on the test_object set.
     */
    public static double PredictDataSets(Svm_model model, Svm_test_obj test){
        if(model == null || test == null) throw new IllegalArgumentException("Input model or test set Null");
        if(model.GetModel() == null) throw new IllegalArgumentException("Input model GetModel Null");
        int test_length = test.GetSize();

        double avg_corr = 0.0;
        for(int i = 0; i < test_length; i++){
            if(test.GetOutcome(i) == svm.svm_predict(model.GetModel(), test.GetFeatureNodeArray(i))){
                avg_corr +=1;
            }
        }
        avg_corr = (double)avg_corr/(double)test_length;
        return avg_corr;
    }

    public static void CrossValidate(){

    }

    private static svm_node[] createNodeArray(Vector<Double> v){
        if(v == null) throw new IllegalArgumentException("Method does not accept NULL input.");
        if(v.size() == 0) throw new IllegalArgumentException("Method does not take an input vector of zero size");

        svm_node[] tmp_arr = new svm_node[v.size()];
        for (int i = 0; i < v.size(); i++){
            tmp_arr[i] = createSvmNode(i+1,v.get(i));
        }
        return tmp_arr;
    }

    private static svm_node createSvmNode(int index, double val){
        if(index <= 0) throw new IllegalArgumentException("A node cannot take a negative or zero index, it starts at 1.");
        svm_node node = new svm_node();
        node.value = val;
        node.index = index;
        return node;
    }

    /**
     * This method allows for manual checking of parameter validity. Bear in mind that this will automatically
     * happen when a SVM model object is created. Returns TRUE if the model-parameter combination is valid.
     */
    public static Boolean CheckParameterValidity(Svm_problem prob, Svm_parameter param){
        boolean check = false;
        try{
            if(svm.svm_check_parameter(prob.GetProblem(),param.GetParameter()) == null){
                check = true;
            } else{
                Debug.pl("Parameter validity check ERROR: "+ svm.svm_check_parameter(prob.GetProblem(),param.GetParameter()));
            }
        } catch (Exception e){ e.printStackTrace();}
        return check;
    }

    public static void Use_custom_print(){
        svm_print_interface my_print_func = new svm_print_interface(){
            public void print(String s)
            {
                // your own format
            }
        };
        svm.svm_set_print_string_function(my_print_func);
    }

    public static void Disable_prints(){
        svm_print_interface my_print_func = new svm_print_interface(){
            public void print(String s)
            {
                // Empty print function
            }
        };
        svm.svm_set_print_string_function(my_print_func);
    }

    public static void Enable_prints(){
        svm.svm_set_print_string_function(null);
    }

    /**
     * Evaluates the input problem and test set for a parameter range above and below the default parameters.
     * @param prob              Problem (training set) to be tested for different parameters.
     * @param test              Test set containing all test points that will be used to get parameter accuracy.
     * @param num_runs          Number of runs for the given parameter range. Will in practice affect the granularity of the search.
     * @param c_prec_offset     Offset from the default C parameter value to test for - in (decimal) percentage above and below the default value.
     * @param g_prec_offset     Offset from the default gamma parameter value to test for - in (decimal) percentage above and below the default value.
     * @return                  The set of parameters that yielded the best result from the grid search. If no parameters are found, default parameters are returned.
     */
    public static Svm_model GridOptimizeParameters(Svm_problem prob, Svm_test_obj test, int num_runs, double c_prec_offset, double g_prec_offset){
        if (prob == null || test  == null) throw new IllegalArgumentException("Input test or training set null");
        if (num_runs < 1 ) throw new IllegalArgumentException("Cannot run grid search with 0 or negative runs.");
        if (c_prec_offset < 0.0 || g_prec_offset < 0.0 ) throw new IllegalArgumentException("Negative percentage values.");
        if (c_prec_offset > 10.0 || g_prec_offset > 10.0 ) throw new IllegalArgumentException("Percentage values too high, please set the percentages as decimal values, ie. 0.5 for 50% etc.");

        int num_features = prob.GetNumFeatures();
        int num_training_samples = prob.GetSize();
        Svm_parameter param = new Svm_parameter(num_features);

        Svm_parameter best_param = new Svm_parameter(num_features);
        Svm_model best_model = new Svm_model(prob,param);
        double best_correctness = 0.5;
        double correctness;

        double tol = 0.001;                             //Default tolerance
        double gamma = (double)1/(double)num_features;  //Default gamma
        double c = 1.0;                                 //Default c

        // Set search boundaries
        double upper_g = gamma + gamma*g_prec_offset;
        double upper_c = c + c*c_prec_offset;
        double lower_g = gamma - gamma*g_prec_offset;
        double lower_c = c - c*c_prec_offset;
        double range_g = upper_g - lower_g;
        double range_c = upper_c - lower_c;
        double step_g = range_g/num_runs;
        double step_c = range_c/num_runs;

        Disable_prints();

        // Test within search boundaries
        for(double ind_g = lower_g; ind_g < upper_g; ind_g += step_g ){
            for (double ind_c = lower_c; ind_c < upper_c; ind_c += step_c){
                if(ind_g>0 && ind_c>0){
                    Debug.toggle = false;
                    param.SetC(ind_c);
                    param.SetGamma(ind_g);
                    Debug.toggle = true;
                    if (CheckParameterValidity(prob,param)){
                        Svm_model model = new Svm_model(prob,param);
                        correctness = PredictDataSets(model,test);
                        if(correctness > best_correctness){
                            best_correctness = correctness;
                            best_param = param.Copy();
                            best_model = model.Copy(prob,best_param);
                            Debug.pl("> > Grid search: Correctness = " + best_correctness*100 + " with parameters: c = " + ind_c + ", g = " + ind_g);
                        }
                    }
                }
            }
        }
        //Debug.pl("Grid search done");
        return new Svm_model(prob,best_param);
    }

    /**
     * Class designated to create the
     */
    public static class CreateSvm{
        private static ArrayList<Integer> test_user_set = new ArrayList<>();
        private static ArrayList<Integer> test_item_set = new ArrayList<>();
        private static ArrayList<Integer> test_classes = new ArrayList<>();
        private static int test_set_size;
        private static ArrayList<Integer> training_user_set = new ArrayList<>();
        private static ArrayList<Integer> training_item_set = new ArrayList<>();
        private static ArrayList<Integer> training_classes = new ArrayList<>();
        private static int training_set_size;

        /**
         * Randomizes the active features of the input string. The first 4 digits will always remain unchanged.
         */
        public static String RandomizeFeatureString(String feature_string){
            if(feature_string.length() != Feature.NUM_FEATURES) throw new IllegalArgumentException("Feature string does has an illegal number of features or length.");
            StringBuilder sb = new StringBuilder(Feature.NUM_FEATURES);
            sb.insert(0,feature_string);

            // Randomize active features
            for(int i = 4; i<Feature.NUM_FEATURES; i++) if(sb.charAt(i) != '0') sb.setCharAt(i, Character.forDigit(getRand(0,1),10));
            return sb.toString();
        }

        /**
         * Get random value between, and including, the lower and upper bound.
         */
        private static int getRand(int lower_bound, int upper_bound){
            Random rand = new Random();
            int range = upper_bound-lower_bound+1;
            return lower_bound+ rand.nextInt(range);
        }

        /**
         *  This method does the following procedure:
         *  1. Get one training and test set to evaluate all svms with.
         *  2. Randomize the feature selection for each svm
         *  3. Optimize the parameters for each svm
         *  3. Test the performance, and return the best
         *  @param db                       Database to get training samples from
         *  @param features_string          Features to randomize. All features set to 1 will be randomized.
         *  @param training_set_size        Size of the training set to train each svm from.
         *  @param test_set_size            Size of the test set to evaluate each svm with.
         */
        public static Svm_model GetBestOfRandomizedSVMs(Database db, String features_string, int num_svms, int training_set_size, int test_set_size){
            Svm_model best_model = null;
            Double best_correctness = 0.01;
            SvmInterface.Disable_prints();

            // Collect samples for constant training and test sets
            collectRandomBalancedTestSet(db, test_set_size);
            collectRandomBalancedTrainingSet(db, training_set_size);
            Debug.pl("> Test and training set loaded");

            for(int i = 0; i< num_svms; i++){
                Debug.pl("> Svm " + (i+1) + " ...");
                // Get features for training and test set
                String rand_ft_string = RandomizeFeatureString(features_string);
                Svm_problem prob = generateTrainingSet(db, rand_ft_string);
                Svm_test_obj test_obj = generateTestObject(db, rand_ft_string);

                // Find locally optimal model around default parameters
                Svm_model model = GridOptimizeParameters(prob,test_obj,51,4.0,4.0);

                // Check correctness, keep if better
                Double correctness = PredictDataSets(model,test_obj);
                if(best_correctness < correctness){
                    best_model = model;
                    best_correctness = correctness;
                    Debug.pl("> Best correctness so far: " + PredictDataSets(best_model,test_obj)*100 + " Feature string: " + rand_ft_string);
                }
            }
            Debug.pl("> Final best correctness = " + best_correctness*100);
            return best_model;
        }

        public static void deleteThisIsPurelyATest(Database db){
            collectRandomBalancedTrainingSet(db,10);
            collectRandomBalancedTestSet(db,10);
            Svm_problem prob = generateTrainingSet(db,"1111");
            Svm_test_obj test_obj = generateTestObject(db,"1111");
            Svm_model model = new Svm_model(prob, new Svm_parameter(prob.GetNumFeatures()));
        }


        /**
         * Generates a Svm-problem object (training set) consisting of equally many negative and positive samples.
         * The samples are picked at random.
         * @param db                Database to extract the samples from
         * @param size              Total size of the desired training set
         * @param feature_string    FeatureStructure string; a bit string containing what features to calculate
         * @return
         */
        public static Svm_problem  GenerateRandomBalancedTrainingSet(Database db, int size, String feature_string){
            Svm_problem prob = new Svm_problem(Feature.countNumFeatures(feature_string));
            try{
                for(int i = 0; i < size/2; i++){
                    // Get negative sample
                    Object[] obj_list = db.rand_getOneNegative();
                    int tmp_userId = (Integer)obj_list[1];
                    int tmp_itemId = (Integer)obj_list[2];
                    int tmp_class  = (Integer)obj_list[3];
                    Feature featureSet = new Feature(new User(tmp_userId,db), new Item(tmp_itemId,db), feature_string);
                    featureSet.getFeatureVector();
                    prob.AppendTrainingPoint(tmp_class,featureSet.getFeatureVector());
                    // Get positive sample
                    obj_list = db.rand_getOnePositive();
                    tmp_userId = (Integer)obj_list[1];
                    tmp_itemId = (Integer)obj_list[2];
                    tmp_class  = (Integer)obj_list[3];
                    featureSet = new Feature(new User(tmp_userId,db), new Item(tmp_itemId,db), feature_string);
                    featureSet.getFeatureVector();
                    prob.AppendTrainingPoint(tmp_class,featureSet.getFeatureVector());
                }
            } catch (Exception e){e.printStackTrace();}
            prob.FinalizeTrainingSet();
            return prob;
        }

        public static Svm_test_obj  GenerateRandomBalancedTestObject(Database db, int size, String feature_string){
            Svm_test_obj test_obj = new Svm_test_obj(Feature.countNumFeatures(feature_string));
            try{
                for(int i = 0; i < size/2; i++){
                    // Get negative sample
                    Object[] obj_list = db.rand_getOneNegative();
                    int tmp_userId = (Integer)obj_list[1];
                    int tmp_itemId = (Integer)obj_list[2];
                    int tmp_class  = (Integer)obj_list[3];
                    Feature featureSet = new Feature(new User(tmp_userId,db), new Item(tmp_itemId,db), feature_string);
                    featureSet.getFeatureVector();
                    test_obj.AppendTestPoint(tmp_class, featureSet.getFeatureVector());
                    // Get positive sample
                    obj_list = db.rand_getOnePositive();
                    tmp_userId = (Integer)obj_list[1];
                    tmp_itemId = (Integer)obj_list[2];
                    tmp_class  = (Integer)obj_list[3];
                    featureSet = new Feature(new User(tmp_userId,db), new Item(tmp_itemId,db), feature_string);
                    featureSet.getFeatureVector();
                    test_obj.AppendTestPoint(tmp_class, featureSet.getFeatureVector());
                }
            } catch (Exception e){e.printStackTrace();}
            return test_obj;
        }

        private static void collectRandomBalancedTestSet(Database db, int size){
            test_user_set.clear();
            test_item_set.clear();
            test_classes.clear();
            test_set_size = size;
            try{
                for(int i = 0; i < size/2; i++){
                    // Get negative sample
                    Object[] obj_list = db.rand_getOneNegative();
                    int tmp_userId = (Integer)obj_list[1];
                    int tmp_itemId = (Integer)obj_list[2];
                    int tmp_class  = (Integer)obj_list[3];
                    test_user_set.add(tmp_userId);
                    test_item_set.add(tmp_itemId);
                    test_classes.add(tmp_class);

                    // Get positive sample
                    obj_list = db.rand_getOnePositive();
                    tmp_userId = (Integer)obj_list[1];
                    tmp_itemId = (Integer)obj_list[2];
                    tmp_class  = (Integer)obj_list[3];
                    test_user_set.add(tmp_userId);
                    test_item_set.add(tmp_itemId);
                    test_classes.add(tmp_class);
                }
            } catch (Exception e){e.printStackTrace();}
        }

        private static void collectRandomBalancedTrainingSet(Database db, int size){
            training_user_set.clear();
            training_item_set.clear();
            training_classes.clear();
            training_set_size = size;
            try{
                for(int i = 0; i < size/2; i++){
                    // Get negative sample
                    Object[] obj_list = db.rand_getOneNegative();
                    int tmp_userId = (Integer)obj_list[1];
                    int tmp_itemId = (Integer)obj_list[2];
                    int tmp_class  = (Integer)obj_list[3];
                    training_user_set.add(tmp_userId);
                    training_item_set.add(tmp_itemId);
                    training_classes.add(tmp_class);

                    // Get positive sample
                    obj_list = db.rand_getOnePositive();
                    tmp_userId = (Integer)obj_list[1];
                    tmp_itemId = (Integer)obj_list[2];
                    tmp_class  = (Integer)obj_list[3];
                    training_user_set.add(tmp_userId);
                    training_item_set.add(tmp_itemId);
                    training_classes.add(tmp_class);
                }
            } catch (Exception e){e.printStackTrace();}
        }

        private static Svm_test_obj generateTestObject(Database db, String feature_string){
            Svm_test_obj test_obj = new Svm_test_obj(Feature.countNumFeatures(feature_string));
            try{
                for(int i = 0; i < test_set_size; i++){
                    // Create features for each sample to fit the feature string
                    Feature featureSet = new Feature(new User(test_user_set.get(i),db), new Item(test_item_set.get(i),db), feature_string);
                    featureSet.getFeatureVector();
                    test_obj.AppendTestPoint(test_classes.get(i), featureSet.getFeatureVector());
                }
            } catch (Exception e){e.printStackTrace();}
            return test_obj;
        }

        private static Svm_problem generateTrainingSet(Database db, String feature_string){
            Svm_problem prob = new Svm_problem(Feature.countNumFeatures(feature_string));
            try{
                for(int i = 0; i < training_set_size; i++){
                    // Create features for each sample to fit the feature string
                    Feature featureSet = new Feature(new User(training_user_set.get(i),db), new Item(training_item_set.get(i),db), feature_string);
                    featureSet.getFeatureVector();
                    prob.AppendTrainingPoint(training_classes.get(i), featureSet.getFeatureVector());
                }
            } catch (Exception e){e.printStackTrace();}
            prob.FinalizeTrainingSet();
            return prob;
        }


    }

}