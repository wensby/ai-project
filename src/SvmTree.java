/**
 * An SVM tree is a tree structure of svm files.
 * @author Lukas J. Wensby
 * @version 2013-06-02
 * TODO not finished, at all..
 */
public class SvmTree {
	/**
	 * One single node in the entire tree structure.
	 * @author Lukas J. Wensby
	 */
	private class TreeNode {
		private int featureSeparator;
		private TreeNode branch;
		private String svmPathfile;
	}
	
	public SvmTree() {
		
	}
	
	/**
	 * 
	 * @param filepath
	 */
	public void addSvm(String filepath) {
		
	}
}
