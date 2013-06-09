import java.util.LinkedList;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class Main{


    public static void main(String []args) throws Exception{

    	Feature.FeatureStructureGenerator.clearNew();
    	Feature.FeatureStructureGenerator.useFeature(Feature.DIFF_YEARS);
    	Feature.FeatureStructureGenerator.useFeature(Feature.ITEM_AGE_RANK);
    	String treeStruct = Feature.FeatureStructureGenerator.getFeatureStructurePure();
    	
    	Feature.FeatureStructureGenerator.clearNew();
    	Feature.FeatureStructureGenerator.useFeature(Feature.ITEM_BIRTH_YEAR);
    	String b = Feature.FeatureStructureGenerator.getFeatureStructurePure();
    	
    	Feature.FeatureStructureGenerator.clearNew();
    	Feature.FeatureStructureGenerator.useFeature(Feature.USER_GENDER);
    	String c = Feature.FeatureStructureGenerator.getFeatureStructurePure();
    	
    	SvmTreeNew tree = new SvmTreeNew(treeStruct);
    	
    	tree.addSvm("filepath1", treeStruct);
    	tree.addSvm("filepath2", b);
    	tree.addSvm("filepath3", c);
    	
    	LinkedList<SvmTreeNew.Svm> svms = tree.getSvms(b);
    	
    	for (SvmTreeNew.Svm svm : svms) {
    		Debug.pl(svm.getFilepath());
    	}
    	

    }
}
