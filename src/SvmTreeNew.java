import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

/**
 * A structure for storing and getting SVMs (not a tree).
 * @author Lukas J. Wensby
 * @version 2013-06-09
 */
public class SvmTreeNew {
	/**
	 * Holds a set of SVMs.
	 * @author Lukas J. Wensby
	 * @version 2013-06-09
	 */
	public class Svm {
		private String filepath;
		private String featureStructure;
		
		public Svm(String filepath, String featureStructure) {
			this.filepath = filepath;
			this.featureStructure = featureStructure;
		}
		
		public String getFilepath() {
			return filepath;
		}
		
		public String getFeatureStructure() {
			return featureStructure;
		}
	}
	
	private HashMap<String, LinkedList<Svm>> structure;
	private String featureDiscriminator;
	
	/**
	 * Creates a new SVM structure with specified feature discriminator. If two different SVMs are 
	 * inserted into this SVM, where they both have all of these features in common, but differ in
	 * some other features, they will still be stored in the same position.
	 * @param featureStructure determines which features this structure should discriminate 
	 * between. Example argument: "01001001110000".
	 */
	public SvmTreeNew(String featureStructure) {
		structure = new HashMap<String, LinkedList<Svm>>();
		featureDiscriminator = featureStructure; // set which features this structure will use
		
		if (Debug.toggle) {
			Debug.pl("> Created a new SVM Tree Structure with slots: ");
			LinkedList<String> perm = new LinkedList<String>();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < Feature.NUM_FEATURES; i++) sb.append('0');
			permutations(perm, sb.toString(), "", 0);
			for (String s : perm) {
				Debug.pl(">   Slot: " + formatStructurePosition(s));
			}
		}
	}
	
	/**
	 * Adds a SVM to this structure. It will be stored according to what features it is using.
	 * @param filepath is the filepath to this SVM's .txt file.
	 * @param featureStructure is the features that this SVM is using. Example argument: "0110000".
	 */
	public void addSvm(String filepath, String featureStructure) {
		// Quick argument checking
		if (featureStructure.length() != featureDiscriminator.length()) 
			throw new IllegalArgumentException("the featureStructure's length and the featureDiscriminator's length doesn't match");
		
		// First we need to get the place in which we will store this feature, this will be 
		// acquired using a sort of AND operator between the argument featureStructure and this 
		// structure's featureDiscriminator.
		String position = getStructurePosition(featureStructure);
		
		// Now we just make sure that there is an initialized SvmSet at that position
		if (structure.get(position) == null) {
			LinkedList<Svm> temp = new LinkedList<Svm>();
			structure.put(position, temp);
		}
		
		// And now just add the SVM to that correct position
		Svm svm = new Svm(filepath, featureStructure);
		structure.get(position).add(svm);
		Debug.pl("> Added svm " + filepath + " to position " + formatStructurePosition(position));
	}
	
	/**
	 * Returns a linked list of svm objects that uses the specified features.
	 * @param featureStructure is a string of the features that you look for
	 * @return a linked list of svm objects.
	 */
	public LinkedList<Svm> getSvms(String featureStructure) {
		LinkedList<Svm> result = new LinkedList<Svm>();
		
		// Get all the positions where we need to look in the structure
		LinkedList<String> structurePositions = new LinkedList<String>();
		permutations(structurePositions, featureStructure, "", 0);
		
		for (String s : structurePositions) {
			LinkedList<Svm> temp = structure.get(s);
			if (temp != null && temp.size() != 0)
				result.addAll(structure.get(s));
		}
		
		Debug.pl("> Fetched svms that uses features " + featureStructure);
		return result;
	}
	
	private void permutations(LinkedList<String> permutations, String locked, String soFar, int index) {
	    if(index == Feature.NUM_FEATURES) {
	    	if (!permutations.contains(getStructurePosition(soFar))) 
	    		permutations.add(getStructurePosition(soFar));
	    }
	    else {
	    	if (featureDiscriminator.charAt(index) == '1' && locked.charAt(index) == '0') {
		    	permutations(permutations, locked, soFar + "0", index + 1);
		    	permutations(permutations, locked, soFar + "1", index + 1);
	    	}
	    	else if (locked.charAt(index) == '1') {
	    		permutations(permutations, locked, soFar + "1", index + 1);
	    	}
	    	else {
	    		permutations(permutations, locked, soFar + "0", index + 1);
	    	}
	    }
	}
	
	private String getStructurePosition(String featureStructure) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < featureStructure.length(); i++) {
			if ((featureStructure.charAt(i) != '0') && (featureDiscriminator.charAt(i) != '0')) 
				sb.append('1');
			else
				sb.append('0');
		}
		return sb.toString();
	}
	
	private String formatStructurePosition(String featureStructure) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < featureStructure.length(); i++) {
			if (featureDiscriminator.charAt(i) == '0') 
				sb.append('_');
			else 
				sb.append(featureStructure.charAt(i));
		}
		return sb.toString();
	}

}
