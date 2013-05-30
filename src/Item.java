import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 
 * @author Lukas J. Wensby
 * @author Tormod
 * @author Jimmy Chau
 */
public class Item extends User{
	private ArrayList<Integer> categori;
	private ArrayList<Integer> keywords;
	

	public Item(int itemID, Database db) throws Exception{
		super(itemID,db);
		Statement stat = db.createStatement();
		String query_string = "SELECT * FROM item WHERE itemID = "+ Integer.toString(itemID) + " LIMIT 1;";
		ResultSet res = stat.executeQuery(query_string);
		if(res.next()){
			this.categori = Parser.dot_Integer_parser(res.getString("categoriesString"));
			//this.keywords = Parser.semiColon_Integer_parser(res.getString("keywordsString"));
		}else{
			throw new Exception("could not found any entry with itemID "+Integer.toString(itemID));
		}
		
		stat.close();
	}


	public ArrayList<Integer> getCategori() {
		return categori;
	}

}
