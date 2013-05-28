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
		Statement stat = db.getStatement();
		String query_string = "SELECT TOP 1 FROM item WHERE itemID="+Integer.toString(itemID)+";";
		ResultSet res = stat.executeQuery(query_string);
		if(res.next()){
			this.categori = Parser.dot_Integer_parser(res.getString("categoriesString"));
			this.keywords = Parser.semiColon_Integer_parser(res.getString("keywordsString"));
		}else{throw new Exception("could not found any entry with itemID"+Integer.toString(itemID));}
	}


	public ArrayList<Integer> getCategori() {
		return categori;
	}
	public ArrayList<Integer> getKeywords() {
		return keywords;
	}
}
