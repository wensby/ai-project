

//package com.rungeek.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Database {
//	public static String PATH_INSIDE_CURRENT_PROJECT = "../Database/ML_projecter_twitter.sqlite";    // DATABASE NAME AND (optional) PATH
	public static String PATH_INSIDE_CURRENT_PROJECT = "../corrupt/corrupted.sqlite";    // DATABASE NAME AND (optional) PATH
    public static final String JDBC_DRIVER = "org.sqlite.JDBC";
    public static final String JDBC_URL = "jdbc:sqlite:" + PATH_INSIDE_CURRENT_PROJECT;
    public static final String JDBC_USER = "root";
    public static final String JDBC_PASSWORD = "";

    private Connection conn = null;
    private Statement stat = null;
    private ResultSet rs = null;
    private PreparedStatement prep = null;
    
    //Connection stuff
    public Database() throws Exception{
    	open_connection();
    } 
    public void open_connection() throws SQLException{
        try {
            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            this.stat = conn.createStatement();
            System.out.println("Database opened from location:  " + PATH_INSIDE_CURRENT_PROJECT);
            
            		
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void close_connection() throws SQLException{

        // If user exists, then update user
    }
    
    public void ensureKeywordsTableExist()
    {
        try {
			this.stat.executeUpdate("CREATE TABLE IF NOT EXISTS user_keywords ("
					+ "\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "\"UserID\" INTEGER NOT NULL, "
					+ "\"Keyword\" INTEGER NOT NULL, "
					+ "\"Weight\" DOUBLE NOT NULL)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public static void updateUser() throws Exception {

    }

    public static void getUserFromID(int id) throws Exception{

    }

    public static void insertItem(int id, String cat, String keywords) throws Exception{

    }

    public static void updateItem(int id, String cat, String keywords) throws Exception{

    }

    public static void getItemFromID(int id) throws Exception{

    }


    /**
     * Takes a list of string values, and formats this into a VALUES statement used for SQL
     * @param values
     * @return
     */
    public static String valueFormatter(ArrayList<String> values){
    	String returnvalue = "("; 
    	for(String v: values){
    		returnvalue += v+",";
    	}
    	returnvalue= returnvalue.substring(0, returnvalue.length()-1);
    	returnvalue += ")";
    	return returnvalue;
    }    

    /**
     * Returns an array of Object, where each element corresponds to the columns.
     * This method may be prone to send exceptions.
     * @return an array of Object, corresponding to each column of that row
     */
    public Object[] getOneRow(String tableName, int offset) throws SQLException {
		ResultSet result = stat.executeQuery("SELECT * FROM " + tableName + " LIMIT 1 OFFSET " + offset);
		int numColumns = result.getMetaData().getColumnCount(); 
		
		Object[] arrayResult = new Object[numColumns];
		for (int i = 0; i < numColumns; i++) {
			arrayResult[i] 	= result.getObject(i + 1);
		}
		
    	return arrayResult;
    }
    
    /**
     * TODO not finished
     */
    public Object[][] getMultipleRows(String tableName, int offset, int numRow) throws SQLException {
    	ResultSet result = stat.executeQuery("SELECT * FROM " + tableName + " LIMIT " + numRow + " OFFSET " + offset);
//    	int numColumns
    	
		return null;
    }
    
    //Queries 
    public void insert(String table, String values) throws SQLException{
    	//System.out.println("INSERT INTO "+ table + " VALUES" +values+ "");
    	stat.executeUpdate("INSERT INTO "+ table + " VALUES " +values+ ";");
    }

    public int length(String table) throws SQLException{
    	//System.out.println("INSERT INTO "+ table + " VALUES" +values+ "");
    	rs = stat.executeQuery("SELECT COUNT(*) FROM "+ table + ";");

        if (rs.next()){
            return rs.getInt(1);
        } else{
            return 0;
        }
    }

    /**
     * Use this for batch insert to query several SQL statements iteratively.
     * To execute the batch, call the function executeBatch()
     */
    public void addToBatch(String table, String values) throws SQLException{
        this.stat.addBatch("INSERT INTO "+ table + " VALUES " +values+ ";");
    }

    public void executeBatch()throws Exception{
        this.stat.executeBatch();
    }

    public static void get_read_performance_of_rec_log() throws Exception{
        String table_name = "rec_log_train";
        Database db = new Database();

        // Start timer
        long startTime = System.currentTimeMillis();

        int table_length = db.length(table_name);
        String sql = "SELECT * FROM " + table_name + " WHERE autoID = ?";
        db.prep = db.conn.prepareStatement(sql);

        int i = 0;
        while(table_length > i){
            db.prep.setInt(1,i);
            ResultSet rs1 = db.prep.executeQuery();
            while (rs1.next()){
                // System.out.println(rs1.getString(1) + "   " + rs1.getString(2) +"   "  + rs1.getString(3) + "   " +
                //        rs1.getString(4)+ "  "+ rs1.getString(5));
                rs1.getString(1);
                rs1.getString(2);
                rs1.getString(3);
                rs1.getString(4);
                rs1.getString(5);
            }
            if(i%1000000 == 0){
                System.out.println("Progression:  " + i);
            }
            i++;
        }

        // Stop timer
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Time elapsed to extract " + table_length + " elements from table " + table_name + " in Ms: " + elapsedTime);
    }

    /**
     * Copies every entry in this database into the database into another specified database.
     * The specified database file does have to have the exact same table structure.
     */
    public void MoveIntoEmptyDatabase(String pathfile) throws SQLException, ClassNotFoundException {
    	// FOR TORMOD TO DO:
    	// Do so that the sqlite file used below is constructed.
    	// So you must create a new database with correct table structures.
    	
    	String NEW_PATH_INSIDE_CURRENT_PROJECT = "../corrupt/" + pathfile + ".sqlite";    // DATABASE NAME AND (optional) PATH
        String NEW_JDBC_URL = "jdbc:sqlite:" + NEW_PATH_INSIDE_CURRENT_PROJECT;
        
        // Open connection
        Class.forName(JDBC_DRIVER);
        Connection 	newConn = DriverManager.getConnection(NEW_JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        Statement 	newStat = newConn.createStatement();
        System.out.println("Database opened from location:  " + NEW_PATH_INSIDE_CURRENT_PROJECT);
        Debug.pl("Succesfull connections between two databases.");
        
        // Attach the old database to the new one
        newStat.execute("ATTACH '../corrupt/corrupted.sqlite' AS oldDatabase");
        newStat.execute("ATTACH '../Database/hopefully_not_corrupted.sqlite' AS newDatabase");
        
        // Now start to load it into the new stuff
        newStat.executeUpdate("INSERT INTO newDatabase.item(itemID, categoriesString, keywordsString) SELECT * FROM oldDatabase.item;");
        newStat.executeUpdate("INSERT INTO newDatabase.rec_log_train(autoID, UserID, ItemId, result, timeStamp) SELECT * FROM oldDatabase.rec_log_train;");
        newStat.executeUpdate("INSERT INTO newDatabase.userSNS(userSnsID, followerUserID, followeeUserID) SELECT * FROM oldDatabase.userSNS;");
        newStat.executeUpdate("INSERT INTO newDatabase.user_action(actionID, userID, destinationUserID, atAction, reTweet, comment) SELECT * FROM oldDatabase.user_action;");
        newStat.executeUpdate("INSERT INTO newDatabase.user_keywords(ID, UserID, Keyword, Weight) SELECT * FROM oldDatabase.user_keywords;");
        newStat.executeUpdate("INSERT INTO newDatabase.user_profile(UserID, birthYear, gender, tweets, tagIDstring) SELECT * FROM oldDatabase.user_profile;");
        
        Debug.pl("Finished!");
    }
}










