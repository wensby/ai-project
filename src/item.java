import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class Item {
    private final int id;
    private final ArrayList<Integer> categories = new ArrayList<Integer>();
    private final ArrayList<Integer> keywords = new ArrayList<Integer>();

    Item(int id, String categories, String keywords) {
        this.id = id;
        parseCategories(categories);
        parseKeywords(keywords);
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
