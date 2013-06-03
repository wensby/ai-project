import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * An SVM tree is a tree structure of svm files.
 * @author Lukas J. Wensby
 * @version 2013-06-02
 */
public class SvmTree {
	private interface TreeNode { }

	private TreeNode root; // the root of this tree
	private LinkedList<TreeNode[]> layers; // will have the SVM tree structure in a layered format
	
	/**
	 * One branching node in the SvmTree structure, it will branch into two different subtrees 
	 * based on a feature which are either used or not used in the different subtrees. So it will
	 * branch to two more Nodes, which can be either a SvmSetNode or another BranchNode
	 * @author Lukas J. Wensby
	 */
	private class BranchNode implements TreeNode {
		private int featureSeparator; // must be any of the static constants found in Feautre
		
		private TreeNode childA;
		private TreeNode childB;
		
		public BranchNode(int featureSeparator) {
			this.featureSeparator = featureSeparator;
		}
		
		public void setChildA(TreeNode A) {
			this.childA = A;
		}
		
		public void setChildB(TreeNode B) {
			this.childB = B;
		}
	}
	
	/**
	 * A SvmSetNode contains actual SVM's that can be run.
	 * @author Lukas J. Wensby
	 */
	private class SvmSetNode implements TreeNode {
		
	}
	
	/**
	 * Will create an SVM tree that takes into consideration the features that are specified in the
	 * argument featureStrucutre.
	 * @param featureStructure must look like "00101...00101" where the number of 1's and 0's is 
	 * smaller or equal to {@link Feature#NUM_FEATURES}.
	 */
	public SvmTree(String featureStructure) {
		if (!Pattern.matches("[0-9]+", featureStructure)) {
		    throw new IllegalArgumentException("featureStructure format was rejected");
		}
		
		// Initialize some variables
		layers = new LinkedList<TreeNode[]>();
		
		for (int i = 0; i < featureStructure.length(); i++) {
			if (featureStructure.charAt(i) != '0') {
				branch(i);
			}
		}
	}
	
	/**
	 * Will perform a branching of the SVM tree based on the specified feature
	 * @param featureSeparator
	 */
	private void branch(int featureIndex) {
		// If this is the first branch, set it as the root
		if (root == null) {
			root = new BranchNode(featureIndex);
			TreeNode[] layer = new TreeNode[1];
			layer[0] = root;
			layers.add(layer);
		}
		else {
			int numBranchNodes = layers.size() * 2; // these many branchNodes has to be created
			for (int b = 0; b < numBranchNodes; b++) {
				BranchNode branch = new BranchNode(featureIndex);
				
			}
		}
	}
}
