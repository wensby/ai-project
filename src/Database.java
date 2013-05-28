

//package com.rungeek.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Database {
    public static String PATH_INSIDE_CURRENT_PROJECT = "../Database/ML_twitter_database.sqlite";
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

    public void backup_DB()throws Exception{
        // Set up new connection:

        String new_DB_name = "backup_" + System.currentTimeMillis();
        String nURL = "jdbc:sqlite:" + "../Database/Backups/" + new_DB_name + ".sqlite";
        Class.forName(JDBC_DRIVER);
        Connection nConn = DriverManager.getConnection(nURL, JDBC_USER, JDBC_PASSWORD);
        Statement nStat = nConn.createStatement();
        System.out.println("New database created to location:  " + "../Database/Backups" + new_DB_name);

        // Create tables:
        nStat.executeUpdate("CREATE TABLE \"item\" (\"itemID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"categoriesString\" TEXT NOT NULL , \"keywordsString\" TEXT NOT NULL );");
        nStat.executeUpdate("CREATE TABLE \"rec_log_train\" (\"autoID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"UserID\" INTEGER NOT NULL , \"ItemId\" INTEGER NOT NULL , \"result\" INTEGER NOT NULL , \"timeStamp\" );");
        nStat.executeUpdate("CREATE TABLE \"userSNS\" (\"userSnsID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"followerUserID\" INTEGER NOT NULL , \"followeeUserID\" INTEGER NOT NULL );");
        nStat.executeUpdate("CREATE TABLE \"user_action\" (\"actionID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , \"userID\" INTEGER NOT NULL , \"destinationUserID\" INTEGER NOT NULL , \"atAction\" INTEGER NOT NULL , \"reTweet\" INTEGER NOT NULL , \"comment\" INTEGER NOT NULL );");
        nStat.executeUpdate("CREATE TABLE user_keywords (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT, \"UserID\" INTEGER NOT NULL, \"Keyword\" INTEGER NOT NULL, \"Weight\" DOUBLE NOT NULL);");
        nStat.executeUpdate("CREATE TABLE \"user_profile\" (\"userID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"birthYear\" INTEGER NOT NULL , \"gender\" INTEGER NOT NULL , \"tweets\" INTEGER NOT NULL , \"tagIDstring\" TEXT NOT NULL );");
        nStat.executeUpdate("CREATE TABLE \"tags\" (\"autoID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , \"userID\" INTEGER NOT NULL , \"tag\" INTEGER NOT NULL );");
        nStat.executeUpdate("CREATE TABLE \"itemKey\" (\"autoID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , \"itemID\" INTEGER NOT NULL , \"key\" INTEGER NOT NULL );");

        System.out.println("Tables created in backup");

        // Attach the old database to the new one
        nStat.execute("ATTACH '"+ PATH_INSIDE_CURRENT_PROJECT +"' AS oldDatabase");
        nStat.execute("ATTACH '../Database/Backups/"+ new_DB_name +".sqlite' AS newDatabase");

        System.out.println("Databases attached");

        // Now start to load it into the new stuff
        nStat.executeUpdate("INSERT INTO newDatabase.item(itemID, categoriesString, keywordsString) SELECT * FROM oldDatabase.item;");
        System.out.println("Table items transferred");
        nStat.executeUpdate("INSERT INTO newDatabase.rec_log_train(autoID, UserID, ItemId, result, timeStamp) SELECT * FROM oldDatabase.rec_log_train;");
        System.out.println("Table rec_log_train transferred");
        nStat.executeUpdate("INSERT INTO newDatabase.userSNS(userSnsID, followerUserID, followeeUserID) SELECT * FROM oldDatabase.userSNS;");
        System.out.println("Table userSNS transferred");
        nStat.executeUpdate("INSERT INTO newDatabase.user_action(actionID, userID, destinationUserID, atAction, reTweet, comment) SELECT * FROM oldDatabase.user_action;");
        System.out.println("Table user_action transferred");
        nStat.executeUpdate("INSERT INTO newDatabase.user_keywords(ID, UserID, Keyword, Weight) SELECT * FROM oldDatabase.user_keywords;");
        System.out.println("Table user_keywords transferred");
        nStat.executeUpdate("INSERT INTO newDatabase.user_profile(UserID, birthYear, gender, tweets, tagIDstring) SELECT * FROM oldDatabase.user_profile;");
        System.out.println("Table user_profile transferred");
        nStat.executeUpdate("INSERT INTO newDatabase.tags(autoID, userID, tag) SELECT * FROM oldDatabase.tags;");
        System.out.println("Table tags transferred");
        nStat.executeUpdate("INSERT INTO newDatabase.itemKey(autoID, itemID, key) SELECT * FROM oldDatabase.itemKey;");
        System.out.println("Table itemKey transferred");

        Debug.pl("Backup finished!");
    }
}










