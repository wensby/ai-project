import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: tormodhau
 * Date: 5/15/13
 * Time: 1:15 PM
 * To change this template use File | Settings | File Templates.
 * 
 * @author Lukas J. Wensby
 * @author Tormod
 */
public class Item {
    private final int id;
    private final ArrayList<Integer> categories = new ArrayList<Integer>();
    private final ArrayList<Integer> keywords = new ArrayList<Integer>();

    Item(int id) {
        this.id = id;
    }

    private void parseCategories(String catString){
    	StringTokenizer st = new StringTokenizer(catString,".");
    	while(st.hasMoreTokens())
    	{
    		categories.add(Integer.parseInt(st.nextToken()));
    	}
    }

    private void parseKeywords(String keys){
    	StringTokenizer st = new StringTokenizer(keys,";");
    	while(st.hasMoreTokens())
    	{
    		keywords.add(Integer.parseInt(st.nextToken()));
    	}
    }
 
    public List<Integer> getKeywords() {
        return keywords;
    }

    public List<Integer> getCategories() {
        return categories;
    }

    public int getId() {
        return id;
    }
}
