import java.util.LinkedList;

/**
 * Holds multiple SVM's which you can iterate through.
 * @author Lukas J. Wensby
 * @version 2013-06-11
 */
public class SvmSet {
	private LinkedList<String> filepaths;
	private LinkedList<String> features;
	
	public SvmSet() {
		filepaths = new LinkedList<String>();
		features  = new LinkedList<String>();
	}
	
	/**
	 * Adds an svm to this svm set.
	 * @param filepath has to be formated so that the last part of the name is like 
	 * "[...]1001001001101.txt"
	 */
	public void add(String filepath) {
		filepaths.add(filepath);
		// Now subtract the part of the filename which is the feature structure string
		String featureStructureString = filepath.substring(filepath.length() - 4 - Feature.NUM_FEATURES, filepath.length() - 4);
		features.add(featureStructureString);
	}
}
