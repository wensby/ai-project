import libsvm.*;

/**
 * This class contains tests of SVMs
 */
public abstract class Tormod_classifier {

    public static void test_Svm(){
        //svm_train t = new svm_train();
        String trainingFile = "";
        String trainingParameters = "";

        // WORKFLOW:

        // Create
        svm_problem prob = new svm_problem();
        svm_parameter param = new svm_parameter();

        // Check
        svm.svm_check_parameter(prob,param);

        // Train and create
        svm_model model = svm.svm_train(prob,param);




    }
}
