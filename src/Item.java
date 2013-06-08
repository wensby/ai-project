import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author Lukas J. Wensby
 * @author Tormod
 * @author Jimmy Chau
 */
public class Item extends User{
	private ArrayList<Integer> categori;
	private HashMap<Integer, Integer> followerKeywords;
	private HashMap<Integer, Integer> followerTags;
	
	public Item(int itemID, Database db) throws Exception{
		super(itemID,db);
        Debug.start("t3");
        //this.setCategoriFromDB(itemID, db);
        Debug.stop("t3");
        Debug.start("t4");
		//this.setFollowerKeysFromDB(itemID, db);
        Debug.stop("t4");
        Debug.start("t5");
		//this.setFollowerTagsFromDB(itemID, db);
        Debug.stop("t5");
	}
	private void setCategoriFromDB(int itemID, Database db) throws Exception{
		this.categori = new ArrayList<Integer>();
		String query_getCats =			"SELECT cat1,cat2,cat3,cat4 FROM itemCat WHERE itemID ="+itemID+";";
		Statement stat_getCats = db.createStatement();
		ResultSet res_getCats = stat_getCats.executeQuery(query_getCats);
		if(res_getCats.next()){
			this.categori.add(0, res_getCats.getInt("cat1"));
			this.categori.add(1, res_getCats.getInt("cat2"));
			this.categori.add(2, res_getCats.getInt("cat3"));
			this.categori.add(3, res_getCats.getInt("cat4"));
		}else{
			throw new Exception("could not found any entry with itemID "+Integer.toString(itemID));
		}
		stat_getCats.close();
	}
	private void setFollowerKeysFromDB(int itemID, Database db) throws SQLException{
		this.followerKeywords = new HashMap<Integer, Integer>();
		String query_getFollowerKeys = 	"SELECT Keyword,COUNT(*) AS Number " +
				"FROM userSNS " +
				"INNER JOIN user_keywords ON user_keywords.userID = userSNS.followerUserID " +
				"WHERE userSNS.followeeUserID = "+itemID+" " +
				"GROUP BY Keyword;";
		Statement stat = db.createStatement();
		ResultSet res = stat.executeQuery(query_getFollowerKeys);
		while(res.next()){
			this.followerKeywords.put(res.getInt("Keyword"), res.getInt("Number"));
		}
		stat.close();
	}
	private void setFollowerTagsFromDB(int itemID, Database db) throws SQLException{
		this.followerTags = new HashMap<Integer, Integer>();
		String query_getFollowerTags = 	"SELECT tag,COUNT(*) AS number " +
				"FROM userSNS " +
				"INNER JOIN tags ON tags.userID = userSNS.followerUserID " +
				"WHERE userSNS.followeeUserID = "+itemID+" " +
				"GROUP BY tag;";
		Statement stat = db.createStatement();
		ResultSet res = stat.executeQuery(query_getFollowerTags);
		while(res.next()){
			this.followerTags.put(res.getInt("tag"), res.getInt("Number"));
		}
		stat.close();
	}
	public ArrayList<Integer> getCategori() {
		return categori;
	}
	public HashMap<Integer, Integer> getFollowerKeywords() {
		return followerKeywords;
	}
	public HashMap<Integer, Integer> getFollowerTags() {
		return followerTags;
	}
	public Integer getItemID() {
		return this.getUserID();
	}

	
	
	
}