import java.util.HashMap;


public class Main{


    public static void main(String []args) throws Exception{

    	Database db = new Database("DB_JUN11");
    	db.openConnection();
        long startTime = System.currentTimeMillis();

        /*
        Feature.FeatureStructureGenerator.clearNew();
        //Feature.FeatureStructureGenerator.useFeature(0);
        //Feature.FeatureStructureGenerator.useFeature(1);
        //Feature.FeatureStructureGenerator.useFeature(2);
        //Feature.FeatureStructureGenerator.useFeature(3);
        Feature.FeatureStructureGenerator.useFeature(4);
        Feature.FeatureStructureGenerator.useFeature(5);
        Feature.FeatureStructureGenerator.useFeature(6);
        Feature.FeatureStructureGenerator.useFeature(7);
        Feature.FeatureStructureGenerator.useFeature(8);
        Feature.FeatureStructureGenerator.useFeature(9);
        Feature.FeatureStructureGenerator.useFeature(10);
        Feature.FeatureStructureGenerator.useFeature(11);
        Feature.FeatureStructureGenerator.useFeature(12);
        Feature.FeatureStructureGenerator.useFeature(13);
        Feature.FeatureStructureGenerator.useFeature(14);
        Feature.FeatureStructureGenerator.useFeature(15);
        Feature.FeatureStructureGenerator.useFeature(15);
        Feature.FeatureStructureGenerator.useFeature(16);
        Feature.FeatureStructureGenerator.useFeature(17);
        Feature.FeatureStructureGenerator.useFeature(18);
        Feature.FeatureStructureGenerator.useFeature(19);
        String ft_string = Feature.FeatureStructureGenerator.getFeatureStructurePure();
        */


       // SvmInterface.Svm_model model = SvmInterface.CreateSvm.GetBestOfRandomizedSVMs(db, ft_string,100,1000,1000);
       // model.Save("testSvm2");
        //SvmInterface.CreateSvm.deleteThisIsPurelyATest(db);



        /*
        String feat_st = "00000110101000001011";
        String svm = "test_1000_train_1000_corr_0.569_fts_00000110101000001011";
        int num_features = feat_st.length();

        SvmInterface.Svm_model model = SvmInterface.Svm_model.LoadModel(svm, num_features);
        Double corr = SvmInterface.TestSvm.RunSingleSvm(db, model, feat_st, 100000);
        Debug.pl("Correctness: " + corr*100 + " %");

        */





        runMAP();





        //SvmSet set = SvmSet.load("../SvmModels/mySet2.txt");
        //SvmInterface.DoWithSvm.TrainSvmSet(db, set, 1000);
        //set.save();


        //double corr = SvmInterface.DoWithSvm.RunSvmSet(db,set,10000);
        //Debug.pl("Correctness: " + corr);


        /*
        int counter = 0;
        while(Debug.toggle){
            SvmInterface.CreateSvm.GetBestOfRandomizedSVMs(db,ft_string, 50, 100, 100);

            if(counter == 10){
                System.gc();
                counter = 0;
            }
            counter++;

        }

        */



        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Time in Ms: " + elapsedTime);
        db.closeConnection();
        System.out.println("Done!");





        /*  EXAMPLE IMPLEMENTATION OF THE SVM TREE
        Feature.FeatureStructureGenerator.clearNew();
        Feature.FeatureStructureGenerator.useFeature(Feature.ITEM_GENDER);

        String ft_string = Feature.FeatureStructureGenerator.getFeatureStructurePure();

        Debug.pl(ft_string);

        SvmTree tree = new SvmTree(ft_string);
        tree.addSvm("asdfSVM", ft_string);
        tree.getSvms(ft_string);*/

    }


    public static void runMAP() throws Exception{
        Database db = new Database("DB_JUN11");
        db.openConnection();
        long startTime = System.currentTimeMillis();


        SvmSet set = SvmSet.load("../SvmModels/mySet2.txt");

        int num_runs = 10;

        map3 m = new map3("../data/KDD_Track1_solution.csv", db);
        while(m.hasNextLine() && m.N < num_runs){
            boolean cont = true;
            Debug.pl("Start Line!!");
            int user_id = m.currentUser;
            User user = new User(user_id,db);
            
            while(m.hasNextItem() && cont){
                int item_id = m.nextItem();
                Debug.pl("Start classify");
                // CLASSIFY
                int result = (int)set.ClassifySample(db,user,item_id);
                Debug.pl("Finnished classify");
                if(result == +1){
                	Debug.pl("Add recommend");
                    cont = m.add_recommend(item_id);
                }
                Debug.pl("===================");
            }
            Debug.pl("Finnished Line!!");
            num_runs++;
        }
        double correctness = m.getMAP3();
        Debug.pl(correctness);


        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Time in Ms: " + elapsedTime);
        db.closeConnection();
        System.out.println("Done!");

    }


}
