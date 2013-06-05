import java.util.LinkedList;


/**
 * The Main-class.
 * @author Lukas J. Wensby
 * @version 2013-05-15
 */

public class Main{


    public static void main(String []args) throws Exception{
    	SvmTree tree = new SvmTree("1110");
    	tree.addSvm("filepath1", "1000");
    	tree.addSvm("filepath2", "0100");
    	LinkedList<String> svms = tree.getSvms("0100");
    	Debug.pl(svms.getFirst());
    }
}
