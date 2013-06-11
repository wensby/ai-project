import java.util.LinkedList;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class Main{


    public static void main(String []args) throws Exception{

    	Database db = new Database("DB_JUN5");
    	db.openConnection();
        long startTime = System.currentTimeMillis();

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



        SvmInterface.Svm_model model = SvmInterface.CreateSvm.GetBestOfRandomizedSVMs(db, ft_string,100,1000,1000);
        model.Save("testSvm2");
        //SvmInterface.CreateSvm.deleteThisIsPurelyATest(db);


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
}
