import java.util.LinkedList;

public class Main{


    public static void main(String []args) throws Exception{

    	
    	// ############### JUST TESTING ################### //
    	
    	String treeStruct = Feature.FeatureStructureGenerator.getAllOnes();
    	
    	Feature.FeatureStructureGenerator.clearNew();
    	Feature.FeatureStructureGenerator.useFeature(Feature.ITEM_BIRTH_YEAR);
    	String b = Feature.FeatureStructureGenerator.getFeatureStructurePure();
    	
    	Feature.FeatureStructureGenerator.clearNew();
    	Feature.FeatureStructureGenerator.useFeature(Feature.DIFF_YEARS);
    	Feature.FeatureStructureGenerator.useFeature(Feature.COMMENT_RATIO);
    	String c = Feature.FeatureStructureGenerator.getFeatureStructurePure();
    	
    	SvmTreeNew tree = new SvmTreeNew(treeStruct);
    	
    	tree.addSvm("filepath1", treeStruct);
    	tree.addSvm("filepath2", b);
    	tree.addSvm("filepath3", c);
    	
    	LinkedList<SvmTreeNew.Svm> svms = tree.getSvms(c);
    	
    	for (SvmTreeNew.Svm svm : svms) {
    		Debug.pl(svm.getFilepath());
    	}
    	

    }
}
