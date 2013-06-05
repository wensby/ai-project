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
		
		private TreeNode childFeatureOn; // the branch were the feature is on
		private TreeNode childFeatureOff; // the branch were the feature is off
		
		public BranchNode(int featureSeparator) {
			this.featureSeparator = featureSeparator;
		}
		
		public void setFeatureOn(TreeNode node) {
			this.childFeatureOn = node;
		}
		
		public void setFeatureOff(TreeNode node) {
			this.childFeatureOff = node;
		}
		
		public int getFeatureSeparator() {
			return featureSeparator;
		}
		
		public TreeNode getFeatureOn() {
			return childFeatureOn;
		}
		
		public TreeNode getFeatureOff() {
			return childFeatureOff;
		}
	}
	
	/**
	 * A SvmSetNode contains actual SVM's that can be run.
	 * @author Lukas J. Wensby
	 */
	private class SvmSetNode implements TreeNode {
		private LinkedList<String> svmFilepaths = new LinkedList<String>();
		private LinkedList<String> svmFeatureStructures = new LinkedList<String>();
		private int numSvms = 0;
		
		public SvmSetNode() {
			
		}
		
		public void addSvm(String filepath, String featureStructure) {
			svmFilepaths.add(filepath);
			svmFeatureStructures.add(featureStructure);
			numSvms++;
		}
		
		public LinkedList<String> getSvmFilepaths() {
			return svmFilepaths;
		}
		
		public LinkedList<String> getSvmFeatureStructures() {
			return svmFeatureStructures;
		}
		
		public int getNumSvms() {
			return numSvms;
		}
	}
	
	/**
	 * Will create an SVM tree that takes into consideration the features that are specified in the
	 * argument featureStrucutre.
	 * @param featureStructure must look like "00101...00101" where the number of 1's and 0's is 
	 * smaller or equal to {@link Feature#NUM_FEATURES}.
	 */
	public SvmTree(String featureStructure) {
		Debug.pl("> Start creating featureStructure");
		if (!Pattern.matches("[0-9]+", featureStructure)) {
			Debug.pl("! ERROR: featureStructure format was rejected.");
		    throw new IllegalArgumentException("featureStructure format was rejected");
		}
		
		// Initialize some variables
		layers = new LinkedList<TreeNode[]>();
		
		// Create the branching
		for (int i = 0; i < featureStructure.length(); i++) {
			if (featureStructure.charAt(i) != '0') {
				branch(i);
			}
		}
		
		// Create the SVM containers at the end of the branches
		initializeSvmNodes();
		
		Debug.pl("> Ended creating featureStructure");
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
		// Else, this should go
		else {
			// Construct the new layer array
			TreeNode[] layer = new TreeNode[(int)Math.pow(2, layers.size())];
			int i = 0;
			// Create new branches for this feature in every child of the last layer
			for (TreeNode n : layers.getLast()) {
				// Now for every previous branch's two children, create a new branch node
				((BranchNode)n).setFeatureOn (new BranchNode(featureIndex));
				((BranchNode)n).setFeatureOff(new BranchNode(featureIndex));
				layer[i + 0] = ((BranchNode)n).childFeatureOn;
				layer[i + 1] = ((BranchNode)n).childFeatureOff;
				i += 2;
			}
			layers.add(layer);
		}
	}
	
	/**
	 * When all the branching has been done, the tree should be topped off with SvmSetNodes
	 */
	private void initializeSvmNodes() {
		if (layers.size() == 0) root = new SvmSetNode();
		else {
			for (TreeNode n : layers.getLast()) {
				((BranchNode)n).setFeatureOn (new SvmSetNode());
				((BranchNode)n).setFeatureOff(new SvmSetNode());
			}
		}
	}
	
	public void printInformation() {
		System.out.println("> SVM Tree Information");
		System.out.println(">   Layers: " + (layers.size() + 1));
	}
	
	public void addSvm(String filepath, String featureStructure) {
		// First we need to find the right SvmSetNode to put this SVM in...
		SvmSetNode svmSet = treeCrawl(featureStructure, root);
		
		// add to the found svm set
		svmSet.addSvm(filepath, featureStructure);
	}
	
	/**
	 * Recursively crawls down the branches of the tree to find the matching SVM Set Node.
	 */
	private SvmSetNode treeCrawl(String featureStructure, TreeNode current) {
		if (current.getClass() == BranchNode.class) {
			if (featureStructure.charAt(((BranchNode)current).getFeatureSeparator()) != 0) {
				return treeCrawl(featureStructure, ((BranchNode)current).getFeatureOn());
			}
			else
				return treeCrawl(featureStructure, ((BranchNode)current).getFeatureOff());
		}
		else return (SvmSetNode)current;
	}
	
	/**
	 * Will retrieve a linked list of filepaths to the svms that pertain to the featurestrucutre specified
	 */
	public LinkedList<String> getSvms(String featureStructure) {
		// First we need to find the right SvmSetNode
		SvmSetNode svmSet = treeCrawl(featureStructure, root);
		
		return svmSet.getSvmFilepaths();
	}
}
