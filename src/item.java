import java.util.ArrayList;
import java.util.List;

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
    private final List<Integer> categories = new ArrayList<Integer>();
    private final List<Integer> keywords = new ArrayList<Integer>();

    Item(int id, String categories, String keywords) {
        this.id = id;
        parseCategories(categories);
        parseKeywords(keywords);
    }


    private void parseCategories(String categories){

        this.categories.add(123);



        ///////////
    }

    private void parseKeywords(String keywords){



        this.keywords.add(321);


        /////////

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
