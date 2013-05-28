import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Database {
    public static final String PROJECT_RELATIVE_PATH_WITHOUT_FILE = "../Database/";
    public static final String JDBC_DRIVER = "org.sqlite.JDBC";
    public static final String JDBC_URL_WITHOUT_FILE = "jdbc:sqlite:" + PROJECT_RELATIVE_PATH_WITHOUT_FILE;
    public static final String JDBC_USER = "root";
    public static final String JDBC_PASSWORD = "";
    public static final String FILENAME_EXTENSION = ".sqlite";
    
    public String name;
    public String nameWithExtension;
    private boolean openConnection = false; // true if connection is open

    private Connection conn = null;
    private Statement stat = null;
    private ResultSet rs = null;
    private PreparedStatement prep = null;
    
    /**
     * Constructor. The filename can be specified as "test.sqlite" or as "test", either way works.
     */
    public Database(String filename) throws Exception {
    	// Set the inner variable names for this database
    	if (filename.endsWith(FILENAME_EXTENSION)) {
    		name = filename.split(FILENAME_EXTENSION)[0];
    		nameWithExtension = filename;
    	}
    	else {
    		name = filename;
    		nameWithExtension = name + FILENAME_EXTENSION;
    	}
    	
    	// Check if it exists, otherwise, create new empty one with specified filename
    	if (!Util.checkFileExistence(PROJECT_RELATIVE_PATH_WITHOUT_FILE + nameWithExtension)) {
    		Debug.pl("Error: " + PROJECT_RELATIVE_PATH_WITHOUT_FILE + nameWithExtension + " does not exist.");
    		Debug.pl("Creates empty database at " + PROJECT_RELATIVE_PATH_WITHOUT_FILE + nameWithExtension);
    		createEmpty(nameWithExtension);
    	}
    }
    
    public boolean hasOpenConnection() {
    	return openConnection;
    }
    
    /**
     * Creates an empty database file with the correct tables.
     */
    public void createEmpty(String filename) throws SQLException, ClassNotFoundException {
        String jdbcUrl = "jdbc:sqlite:" + PROJECT_RELATIVE_PATH_WITHOUT_FILE + filename;
        Class.forName(JDBC_DRIVER);
        Connection conn = DriverManager.getConnection(jdbcUrl, JDBC_USER, JDBC_PASSWORD);
        Statement stat = conn.createStatement();

        // Create tables:
        stat.executeUpdate("CREATE TABLE \"item\" (\"itemID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"categoriesString\" TEXT NOT NULL , \"keywordsString\" TEXT NOT NULL );");
        stat.executeUpdate("CREATE TABLE \"rec_log_train\" (\"autoID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"UserID\" INTEGER NOT NULL , \"ItemId\" INTEGER NOT NULL , \"result\" INTEGER NOT NULL , \"timeStamp\" );");
        stat.executeUpdate("CREATE TABLE \"userSNS\" (\"userSnsID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"followerUserID\" INTEGER NOT NULL , \"followeeUserID\" INTEGER NOT NULL );");
        stat.executeUpdate("CREATE TABLE \"user_action\" (\"actionID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , \"userID\" INTEGER NOT NULL , \"destinationUserID\" INTEGER NOT NULL , \"atAction\" INTEGER NOT NULL , \"reTweet\" INTEGER NOT NULL , \"comment\" INTEGER NOT NULL );");
        stat.executeUpdate("CREATE TABLE user_keywords (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT, \"UserID\" INTEGER NOT NULL, \"Keyword\" INTEGER NOT NULL, \"Weight\" DOUBLE NOT NULL);");
        stat.executeUpdate("CREATE TABLE \"user_profile\" (\"userID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"birthYear\" INTEGER NOT NULL , \"gender\" INTEGER NOT NULL , \"tweets\" INTEGER NOT NULL , \"tagIDstring\" TEXT NOT NULL );");
        stat.executeUpdate("CREATE TABLE \"tags\" (\"autoID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , \"userID\" INTEGER NOT NULL , \"tag\" INTEGER NOT NULL );");
        stat.executeUpdate("CREATE TABLE \"itemKey\" (\"autoID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , \"itemID\" INTEGER NOT NULL , \"key\" INTEGER NOT NULL );");
        
        conn.close();
        
        Debug.pl("New database created at location:  " + PROJECT_RELATIVE_PATH_WITHOUT_FILE + filename);
    }
    
    /**
     * This method requires that the inner variable names for this database has been created, which they do in
     * the constructor for Database. 
     */
    public void openConnection() throws SQLException{
    	if (openConnection) return; // why open it when it is open?
        try {
            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(JDBC_URL_WITHOUT_FILE + nameWithExtension, JDBC_USER, JDBC_PASSWORD);
            this.stat = conn.createStatement();
            Debug.pl("Database opened from location: " + PROJECT_RELATIVE_PATH_WITHOUT_FILE + nameWithExtension);	
            openConnection = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void closeConnection() throws SQLException{
    	if (!openConnection) return; // whye close it when it is not open?
    	conn.close();
    	stat.close();
    	openConnection = false;
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
    	for(String v : values){
    		returnvalue += v + ",";
    	}
    	returnvalue = returnvalue.substring(0, returnvalue.length()-1);
    	returnvalue += ")";
    	return returnvalue;
    }    

    /**
     * Returns an array of Object, where each element corresponds to the different columns in that table.
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
    
    //Queries 
    public void insert(String table, String values) throws SQLException{
    	stat.executeUpdate("INSERT INTO "+ table + " VALUES " +values+ ";");
    }

    public int length(String table) throws SQLException{
    	rs = stat.executeQuery("SELECT COUNT(*) FROM " + table + ";");

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

    public static void get_read_performance_of_rec_log(String filename) throws Exception{
        String table_name = "rec_log_train";
        Database db = new Database(filename);

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
     * Creates a backup .sqlite-file of this database.
     */
    public void backup() throws Exception{
        // Set up new connection:

        String new_DB_name = name + "_backup_" + System.currentTimeMillis();
        String nURL = "jdbc:sqlite:" + "../Database/Backups/" + new_DB_name + ".sqlite";
        Class.forName(JDBC_DRIVER);
        Connection nConn = DriverManager.getConnection(nURL, JDBC_USER, JDBC_PASSWORD);
        Statement nStat = nConn.createStatement();
        Debug.pl("New database created to location:  " + "../Database/Backups" + new_DB_name);

        // Create tables:
        nStat.executeUpdate("CREATE TABLE \"item\" (\"itemID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"categoriesString\" TEXT NOT NULL , \"keywordsString\" TEXT NOT NULL );");
        nStat.executeUpdate("CREATE TABLE \"rec_log_train\" (\"autoID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"UserID\" INTEGER NOT NULL , \"ItemId\" INTEGER NOT NULL , \"result\" INTEGER NOT NULL , \"timeStamp\" );");
        nStat.executeUpdate("CREATE TABLE \"userSNS\" (\"userSnsID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"followerUserID\" INTEGER NOT NULL , \"followeeUserID\" INTEGER NOT NULL );");
        nStat.executeUpdate("CREATE TABLE \"user_action\" (\"actionID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , \"userID\" INTEGER NOT NULL , \"destinationUserID\" INTEGER NOT NULL , \"atAction\" INTEGER NOT NULL , \"reTweet\" INTEGER NOT NULL , \"comment\" INTEGER NOT NULL );");
        nStat.executeUpdate("CREATE TABLE user_keywords (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT, \"UserID\" INTEGER NOT NULL, \"Keyword\" INTEGER NOT NULL, \"Weight\" DOUBLE NOT NULL);");
        nStat.executeUpdate("CREATE TABLE \"user_profile\" (\"userID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"birthYear\" INTEGER NOT NULL , \"gender\" INTEGER NOT NULL , \"tweets\" INTEGER NOT NULL , \"tagIDstring\" TEXT NOT NULL );");
        nStat.executeUpdate("CREATE TABLE \"tags\" (\"autoID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , \"userID\" INTEGER NOT NULL , \"tag\" INTEGER NOT NULL );");
        nStat.executeUpdate("CREATE TABLE \"itemKey\" (\"autoID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , \"itemID\" INTEGER NOT NULL , \"key\" INTEGER NOT NULL );");

        Debug.pl("Tables created in backup database");

        // Attach the old database to the new one
        nStat.execute("ATTACH '" + PROJECT_RELATIVE_PATH_WITHOUT_FILE + nameWithExtension + "' AS oldDatabase");
        nStat.execute("ATTACH '" + PROJECT_RELATIVE_PATH_WITHOUT_FILE + "Backups/" + new_DB_name + ".sqlite' AS newDatabase");

        Debug.pl("Databases attached");

        // Now start to load it into the new stuff
        nStat.executeUpdate("INSERT INTO newDatabase.item(itemID, categoriesString, keywordsString) SELECT * FROM oldDatabase.item;");
        Debug.pl("Table items transferred");
        nStat.executeUpdate("INSERT INTO newDatabase.rec_log_train(autoID, UserID, ItemId, result, timeStamp) SELECT * FROM oldDatabase.rec_log_train;");
        Debug.pl("Table rec_log_train transferred");
        nStat.executeUpdate("INSERT INTO newDatabase.userSNS(userSnsID, followerUserID, followeeUserID) SELECT * FROM oldDatabase.userSNS;");
        Debug.pl("Table userSNS transferred");
        nStat.executeUpdate("INSERT INTO newDatabase.user_action(actionID, userID, destinationUserID, atAction, reTweet, comment) SELECT * FROM oldDatabase.user_action;");
        Debug.pl("Table user_action transferred");
        nStat.executeUpdate("INSERT INTO newDatabase.user_keywords(ID, UserID, Keyword, Weight) SELECT * FROM oldDatabase.user_keywords;");
        Debug.pl("Table user_keywords transferred");
        nStat.executeUpdate("INSERT INTO newDatabase.user_profile(UserID, birthYear, gender, tweets, tagIDstring) SELECT * FROM oldDatabase.user_profile;");
        Debug.pl("Table user_profile transferred");
        nStat.executeUpdate("INSERT INTO newDatabase.tags(autoID, userID, tag) SELECT * FROM oldDatabase.tags;");
        Debug.pl("Table tags transferred");
        nStat.executeUpdate("INSERT INTO newDatabase.itemKey(autoID, itemID, key) SELECT * FROM oldDatabase.itemKey;");
        Debug.pl("Table itemKey transferred");

        Debug.pl("Backup finished!");
    }
    
    public void executeUpdate(String query) throws SQLException {
    	stat.executeUpdate(query);
    }
    
    public Statement getStatement() {
    	return stat;
    }
    
    /**
     * Transfer a table from a specified database to another specified database.
     * Warning: the table specified must both exist in the from and destination database.
     * @throws SQLException 
     */
    public static void transferTable(Database from, Database dest, String table) throws SQLException {
    	Debug.pl("> Transfering table " + table + " in " + from.name + " to " + dest.name + ".");
    	
    	if (!(from.hasOpenConnection() && dest.hasOpenConnection())) {
    		Debug.pl("! ERROR: One of the databases did not have an open connection.");
    		return;
    	}

    	// Attach the from database to the destination database
        dest.getStatement().execute("ATTACH '" + PROJECT_RELATIVE_PATH_WITHOUT_FILE + from.nameWithExtension + "' AS orig");
        dest.getStatement().execute("ATTACH '" + PROJECT_RELATIVE_PATH_WITHOUT_FILE + dest.nameWithExtension + "' AS dest");
        
        switch (table) {
	        case ("item") :
	        	dest.getStatement().executeUpdate("INSERT OR IGNORE INTO item(itemID, categoriesString, keywordsString) SELECT * FROM orig.item;");
	        	break;
	        case ("rec_log_train") :
	        	dest.getStatement().executeUpdate("INSERT OR IGNORE INTO dest.rec_log_train(autoID, UserID, ItemId, result, timeStamp) SELECT * FROM orig.rec_log_train;");
	        	break;
	        case ("userSNS") :
	        	dest.getStatement().executeUpdate("INSERT OR IGNORE INTO dest.userSNS(userSnsID, followerUserID, followeeUserID) SELECT * FROM orig.userSNS;");
	        	break;
	        case ("user_action") :
	        	dest.getStatement().executeUpdate("INSERT OR IGNORE INTO dest.user_action(actionID, userID, destinationUserID, atAction, reTweet, comment) SELECT * FROM orig.user_action;");
	        	break;
	        case ("user_keywords") : 
	        	dest.getStatement().executeUpdate("INSERT OR IGNORE INTO dest.user_keywords(ID, UserID, Keyword, Weight) SELECT * FROM orig.user_keywords;");
	        	break;
	        case ("user_profile") :
	        	dest.getStatement().executeUpdate("INSERT OR IGNORE INTO dest.user_profile(UserID, birthYear, gender, tweets, tagIDstring) SELECT * FROM orig.user_profile;");
	        	break;
	        case ("tags") :
	        	dest.getStatement().executeUpdate("INSERT OR IGNORE INTO dest.tags(autoID, userID, tag) SELECT * FROM orig.tags;");
	        	break;
	        case ("itemKey") :
	        	dest.getStatement().executeUpdate("INSERT OR IGNORE INTO dest.itemKey(autoID, itemID, key) SELECT * FROM orig.itemKey;");
	        	break;
	        default :
	        	Debug.pl("! ERROR: Did not recognize table name.");
	        	break;
        }
        
        // Detach the from database from the destination database
        dest.getStatement().execute("DETACH orig;");
        dest.getStatement().execute("DETACH dest;");
        
        Debug.pl("> Transferred table " + table + " in " + from.name + " to " + dest.name + "... 100%");
    }
}