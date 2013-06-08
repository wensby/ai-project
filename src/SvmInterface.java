import libsvm.*;

import java.io.File;
import java.util.ArrayList;
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
 *
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
        public Svm_parameter(int gamma, double c, double tol) {
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
            parameter.eps = 0.01;
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
            } catch (Exception e){ e.printStackTrace();}
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
            if(this.problem == null) throw new IllegalArgumentException("Problem object null.");
            if(this.parameter == null) throw new IllegalArgumentException("Parameter object null");
            this.model = svm.svm_train(this.problem,this.parameter);
        }

        public svm_model GetModel(){
            return this.model;
        }

        public int GetNumFeatures(){
            return this.num_features;
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
    public static double PredictSingleDataPoint(Svm_model model, Vector<Double> vector){
        if(vector== null) throw new IllegalArgumentException("Input feature vector NULL");
        if(model == null) throw new IllegalArgumentException("Input model NULL");
        if(vector.size() != model.GetNumFeatures()) throw new IllegalArgumentException("Number of features for the model and feature vector does not match");
        if(model.GetModel() == null) throw new IllegalArgumentException("Input model NULL");

        return svm.svm_predict(model.GetModel(), createNodeArray(vector));
    }

    public static void CrossValidate(){

    }

    private static svm_node[] createNodeArray(Vector<Double> features){
        if(features == null) throw new IllegalArgumentException("Method does not accept NULL input.");
        if(features.size() == 0) throw new IllegalArgumentException("Method does not take an input vector of zero size");

        svm_node[] tmp_arr = new svm_node[features.size()];
        for (int i = 0; i < features.size(); i++){
            if(features.get(i) == null) throw  new IllegalArgumentException("Feature vector element null.");
            tmp_arr[i] = createSvmNode(i+1,features.get(i));
        }
        return tmp_arr;
    }

    private static svm_node createSvmNode(Integer index, double val){
        if(index <= 0) throw new IllegalArgumentException("A node cannot take a negative or zero index, it starts at 1.");
        svm_node node = new svm_node();
        node.value = val;
        node.index = index;
        if(node == null) throw new NullPointerException("Null node created");
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
}
