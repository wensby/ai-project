import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Database {
    public static final String PROJECT_RELATIVE_PATH_WITHOUT_FILE = "../Database/";
    //public static final String PROJECT_RELATIVE_PATH_WITHOUT_FILE = "/Volumes/Ram Disk/";
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
        stat.executeUpdate("CREATE TABLE \"rec_log_test\" (\"autoID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"UserID\" INTEGER NOT NULL , \"ItemId\" INTEGER NOT NULL , \"result\" INTEGER NOT NULL , \"timeStamp\" );");
        stat.executeUpdate("CREATE TABLE \"rec_log_train_pos\" (\"autoID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"UserID\" INTEGER NOT NULL , \"ItemId\" INTEGER NOT NULL , \"result\" INTEGER NOT NULL , \"timeStamp\" );");
        stat.executeUpdate("CREATE TABLE \"rec_log_train_neg\" (\"autoID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"UserID\" INTEGER NOT NULL , \"ItemId\" INTEGER NOT NULL , \"result\" INTEGER NOT NULL , \"timeStamp\" );");
        stat.executeUpdate("CREATE TABLE \"userSNS\" (\"userSnsID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"followerUserID\" INTEGER NOT NULL , \"followeeUserID\" INTEGER NOT NULL );");
        stat.executeUpdate("CREATE TABLE \"user_action\" (\"actionID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , \"userID\" INTEGER NOT NULL , \"destinationUserID\" INTEGER NOT NULL , \"atAction\" INTEGER NOT NULL , \"reTweet\" INTEGER NOT NULL , \"comment\" INTEGER NOT NULL );");
        stat.executeUpdate("CREATE TABLE user_keywords (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT, \"UserID\" INTEGER NOT NULL, \"Keyword\" INTEGER NOT NULL, \"Weight\" DOUBLE NOT NULL);");
        stat.executeUpdate("CREATE TABLE \"user_profile\" (\"userID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"birthYear\" INTEGER NOT NULL , \"gender\" INTEGER NOT NULL , \"tweets\" INTEGER NOT NULL , \"tagIDstring\" TEXT NOT NULL );");
        stat.executeUpdate("CREATE TABLE \"tags\" (\"autoID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , \"userID\" INTEGER NOT NULL , \"tag\" INTEGER NOT NULL );");
        stat.executeUpdate("CREATE TABLE \"itemKey\" (\"autoID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , \"itemID\" INTEGER NOT NULL , \"key\" INTEGER NOT NULL );");
        stat.executeUpdate("CREATE TABLE \"itemCat\" (\"autoid\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , \"itemID\" INTEGER NOT NULL , \"cat1\" INTEGER NOT NULL , \"cat2\" INTEGER NOT NULL , \"cat3\" INTEGER NOT NULL , \"cat4\" INTEGER NOT NULL )");

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
    	if (!openConnection) return; // why close it when it is not open?
        stat.close();
    	conn.close();
    	openConnection = false;
    	Debug.pl("> Closed the connection to database " + name);
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

    //SELECT * FROM sqlite_master WHERE type = 'index';

    /**
   * Returns an array of Object, where each element corresponds to the different columns in that table.

     * @param tableName
     * @param index
     * @return
     * @throws Exception
     */
    public Object[] iter_getOneRow(String tableName, int index) throws Exception {
        if (tableName.equals("rec_log_train") && tableName.equals("rec_log_test")){
            throw new Exception("Table "+ tableName + " cannot be iteratively looped through using autoID.");
        } else if (index == 0){
            throw new Exception("The autoID numeration does not start from 0, but 1. Set the input index accordingly.");
        }

        ResultSet result = stat.executeQuery("SELECT * FROM " + tableName + " WHERE autoID = " + index);
        int numColumns = result.getMetaData().getColumnCount();
        Object[] arrayResult = new Object[numColumns];
        for (int column = 0; column < numColumns; column++) {
            arrayResult[column] = result.getObject(column + 1);
        }
        return arrayResult;
    }

    /**
     * Gets one random row from tableName. Only works with rec_log_train and rec_log_test.
     * @param tableName
     * @return
     * @throws Exception
     */
    public Object[] rand_getOneRow(String tableName) throws Exception{
        Random rand = new Random();
        int index = 1;
        switch (tableName){
            case ("rec_log_train"):
                index = rand.nextInt(Util.TOTAL_DATABASE_REC_LOG_TRAIN_LENGTH) + 1;
                break;
            case ("rec_log_test"):
                index = rand.nextInt(Util.TOTAL_DATABASE_REC_LOG_TEST_LENGTH) + 1;
                break;
            default:
                throw new Exception("Table "+ tableName + " is not liable for random picking.");
        }

        ResultSet result = stat.executeQuery("SELECT * FROM " + tableName + " WHERE autoID = " + index);
        int numColumns = result.getMetaData().getColumnCount();
        Object[] arrayResult = new Object[numColumns];
        for (int column = 0; column < numColumns; column++) {
            arrayResult[column] = result.getObject(column + 1);
        }
        return arrayResult;
    }

    public Object[] rand_getOnePositive() throws Exception{
        Random rand = new Random();
        int index = rand.nextInt(Util.TOTAL_DATABASE_REC_LOG_TRAIN_POS_LENGTH) + 1;
        ResultSet result = stat.executeQuery("SELECT * FROM rec_log_train_pos WHERE autoID = " + index);
        int numColumns = result.getMetaData().getColumnCount();
        Object[] arrayResult = new Object[numColumns];
        for (int column = 0; column < numColumns; column++) {
            arrayResult[column] = result.getObject(column + 1);
        }
        return arrayResult;
    }

    public Object[] rand_getOneNegative() throws Exception{
        Random rand = new Random();
        int index = rand.nextInt(Util.TOTAL_DATABASE_REC_LOG_TRAIN_NEG_LENGTH) + 1;
        ResultSet result = stat.executeQuery("SELECT * FROM rec_log_train_neg WHERE autoID = " + index);
        int numColumns = result.getMetaData().getColumnCount();
        Object[] arrayResult = new Object[numColumns];
        for (int column = 0; column < numColumns; column++) {
            arrayResult[column] = result.getObject(column + 1);
        }
        return arrayResult;
    }





    /**
     * Gets one random positive row from tableName. Only works with rec_log_train table....
     */
    public Object[] rand_getPositiveRecord() throws Exception{
            // SOMEONE FIGURE THIS ONE OUT!
         return null;
    }



    
    //Queries 
    public void insert(String table, String values) throws SQLException{
    	stat.executeUpdate("INSERT OR IGNORE INTO "+ table + " VALUES " +values+ ";");
    }

    public void commitTransaction() throws Exception{
        this.conn.commit();
    }

    public void turn_autoCommit_off()throws Exception{
        this.conn.setAutoCommit(false);
    }

    public void turn_autoCommit_on()throws Exception{
        this.conn.setAutoCommit(true);
    }

    public int length(String table) throws SQLException{
    	switch (table) {
	        case ("item") : return Util.TOTAL_DATABASE_ITEM_LENGTH;
	        case ("rec_log_train") : return Util.TOTAL_DATABASE_REC_LOG_TRAIN_LENGTH;
	        case ("userSNS") : return Util.TOTAL_DATABASE_USERSNS_LENGTH;
	        case ("user_action") : return Util.TOTAL_DATABASE_USER_ACTION_LENGTH;
	        case ("user_keywords") : return Util.TOTAL_DATABASE_USER_KEYWORDS_LENGTH;
	        case ("user_profile") : return Util.TOTAL_DATABASE_USER_PROFILE_LENGTH;
	        case ("tags") : return 0; // ?????
	        case ("itemKey") : return Util.TOTAL_DATABASE_ITEMKEY_LENGTH;
	        default : Debug.pl("! ERROR: Did not recognize table name."); return 0;
    	}
    }

    /**
     * Use this for batch insert to query several SQL statements iteratively.
     * To execute the batch, call the function executeBatch()
     */
    public void addToBatch(String table, String values) throws SQLException{
        this.stat.addBatch("INSERT OR IGNORE INTO " + table + " VALUES " + values + ";");
    }

    public void executeBatch()throws Exception{
        this.stat.executeBatch();
    }

    public static void get_read_performance_of_rec_log(Database database) throws Exception{
        String table_name = "rec_log_train";

        // Start timer
        long startTime = System.currentTimeMillis();

        Debug.pl("Performance testing: " + table_name);

        int table_length = database.length(table_name);
        String sql = "SELECT * FROM " + table_name + " WHERE autoID = ?";
        database.prep = database.conn.prepareStatement(sql);

        int i = 0;
        while(table_length > i){
        	database.prep.setInt(1,i);
            ResultSet rs1 = database.prep.executeQuery();
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
        nStat.executeUpdate("CREATE TABLE \"rec_log_test\" (\"autoID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"UserID\" INTEGER NOT NULL , \"ItemId\" INTEGER NOT NULL , \"result\" INTEGER NOT NULL , \"timeStamp\" );");
        nStat.executeUpdate("CREATE TABLE \"userSNS\" (\"userSnsID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"followerUserID\" INTEGER NOT NULL , \"followeeUserID\" INTEGER NOT NULL );");
        nStat.executeUpdate("CREATE TABLE \"user_action\" (\"actionID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , \"userID\" INTEGER NOT NULL , \"destinationUserID\" INTEGER NOT NULL , \"atAction\" INTEGER NOT NULL , \"reTweet\" INTEGER NOT NULL , \"comment\" INTEGER NOT NULL );");
        nStat.executeUpdate("CREATE TABLE user_keywords (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT, \"UserID\" INTEGER NOT NULL, \"Keyword\" INTEGER NOT NULL, \"Weight\" DOUBLE NOT NULL);");
        nStat.executeUpdate("CREATE TABLE \"user_profile\" (\"userID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"birthYear\" INTEGER NOT NULL , \"gender\" INTEGER NOT NULL , \"tweets\" INTEGER NOT NULL , \"tagIDstring\" TEXT NOT NULL );");
        nStat.executeUpdate("CREATE TABLE \"tags\" (\"autoID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , \"userID\" INTEGER NOT NULL , \"tag\" INTEGER NOT NULL );");
        nStat.executeUpdate("CREATE TABLE \"itemKey\" (\"autoID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , \"itemID\" INTEGER NOT NULL , \"key\" INTEGER NOT NULL );");
        nStat.executeUpdate("CREATE TABLE \"itemCat\" (\"autoid\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , \"itemID\" INTEGER NOT NULL , \"cat1\" INTEGER NOT NULL , \"cat2\" INTEGER NOT NULL , \"cat3\" INTEGER NOT NULL , \"cat4\" INTEGER NOT NULL );");
        nStat.executeUpdate("CREATE TABLE \"rec_log_train_pos\" (\"autoID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"UserID\" INTEGER NOT NULL , \"ItemId\" INTEGER NOT NULL , \"result\" INTEGER NOT NULL , \"timeStamp\" );");
        nStat.executeUpdate("CREATE TABLE \"rec_log_train_neg\" (\"autoID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"UserID\" INTEGER NOT NULL , \"ItemId\" INTEGER NOT NULL , \"result\" INTEGER NOT NULL , \"timeStamp\" );");


        Debug.pl("Tables created in backup database");

        // Attach the old database to the new one
        nStat.execute("ATTACH '" + PROJECT_RELATIVE_PATH_WITHOUT_FILE + nameWithExtension + "' AS oldDatabase");
        nStat.execute("ATTACH '" + PROJECT_RELATIVE_PATH_WITHOUT_FILE + "Backups/" + new_DB_name + ".sqlite' AS newDatabase");

        Debug.pl("Databases attached");

        // Now start to load it into the new stuff
        nStat.executeUpdate("INSERT INTO newDatabase.item(itemID, categoriesString, keywordsString) SELECT * FROM oldDatabase.item;");
        Debug.pl("Table items transferred");
        nStat.executeUpdate("INSERT INTO newDatabase.rec_log_test(autoID, UserID, ItemId, result, timeStamp) SELECT * FROM oldDatabase.rec_log_test;");
        Debug.pl("Table rec_log_test transferred");
        nStat.executeUpdate("INSERT INTO newDatabase.rec_log_train(autoID, UserID, ItemId, result, timeStamp) SELECT * FROM oldDatabase.rec_log_train;");
        Debug.pl("Table rec_log_train transferred");
        nStat.executeUpdate("INSERT INTO newDatabase.rec_log_train_pos(autoID, UserID, ItemId, result, timeStamp) SELECT * FROM oldDatabase.rec_log_train_pos;");
        Debug.pl("Table rec_log_train_pos transferred");
        nStat.executeUpdate("INSERT INTO newDatabase.rec_log_train_neg(autoID, UserID, ItemId, result, timeStamp) SELECT * FROM oldDatabase.rec_log_train_neg;");
        Debug.pl("Table rec_log_train_neg transferred");
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
        nStat.executeUpdate("INSERT INTO newDatabase.itemCat(autoID, itemID, cat1, cat2, cat3, cat4) SELECT * FROM oldDatabase.itemCat;");


        Debug.pl("Backup finished!");
    }

    public static void indexTable(Database database, String table) throws Exception {
        Debug.pl("> Indexing table " + table + " in database " + database.name);

        if (!(database.hasOpenConnection())) {
            Debug.pl("! The databases does not have an open connection.");
            return;
        }

        switch (table) {
            case ("item") :
                database.getStatement().executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS itemIndex ON item (itemID);");
                break;
            case ("userSNS") :
                database.getStatement().executeUpdate("CREATE INDEX IF NOT EXISTS userSNSIndex1 ON userSNS (followerUserID);");
                database.getStatement().executeUpdate("CREATE INDEX IF NOT EXISTS userSNSIndex2 ON userSNS (followeeUserID);");
                database.getStatement().executeUpdate("CREATE INDEX IF NOT EXISTS userSNSIndex3 ON userSNS (followeeUserID, followerUserID);");
                break;
            case ("user_action") :
                database.getStatement().executeUpdate("CREATE INDEX IF NOT EXISTS userActionIndex1 ON user_action (userID);");
                database.getStatement().executeUpdate("CREATE INDEX IF NOT EXISTS userActionIndex2 ON user_action (destinationUserID);");
                break;
            case ("user_keywords") :
                database.getStatement().executeUpdate("CREATE INDEX IF NOT EXISTS userKeywordsIndex1 ON user_keywords (UserID);");
                database.getStatement().executeUpdate("CREATE INDEX IF NOT EXISTS userKeywordsIndex2 ON user_keywords (Keyword);");
                break;
            case ("user_profile") :
                database.getStatement().executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS userProfileIndex ON user_profile (UserId);");
                break;
            case ("tags") :
                database.getStatement().executeUpdate("CREATE INDEX IF NOT EXISTS tagsIndex1 ON tags (userID);");
                database.getStatement().executeUpdate("CREATE INDEX IF NOT EXISTS tagsIndex2 ON tags (tag);");
                break;
            case ("itemKey") :
                database.getStatement().executeUpdate("CREATE INDEX IF NOT EXISTS itemKeyIndex ON itemKey (itemID);");
                break;
            case ("rec_log_train") :
                database.getStatement().executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS recLogTrainIndex ON rec_log_train (autoID);");
                break;
            case ("rec_log_train_pos") :
                database.getStatement().executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS recLogTrainPosIndex ON rec_log_train_pos (autoID);");
                break;
            case ("rec_log_train_neg") :
                database.getStatement().executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS recLogTrainNegIndex ON rec_log_train_neg (autoID);");
                break;
            case ("rec_log_test") :
                database.getStatement().executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS recLogTestIndex ON rec_log_test (autoID);");
                break;
            case ("itemCat") :
                database.getStatement().executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS itemCatIndex1 ON itemCat (itemID);");
                database.getStatement().executeUpdate("CREATE INDEX IF NOT EXISTS itemCatIndex2 ON itemCat (cat1);");
                database.getStatement().executeUpdate("CREATE INDEX IF NOT EXISTS itemCatIndex3 ON itemCat (cat2);");
                database.getStatement().executeUpdate("CREATE INDEX IF NOT EXISTS itemCatIndex4 ON itemCat (cat3);");
                database.getStatement().executeUpdate("CREATE INDEX IF NOT EXISTS itemCatIndex5 ON itemCat (cat4);");
                break;
            default :
                Debug.pl("! ERROR: Did not recognize table name.");
                break;
        }
        Debug.pl("> Table " + table + " from database " + database.name + " has been indexed.");
    }

    public static void dropTableIndex(Database database, String table) throws Exception {
        Debug.pl("> Dropping index for table " + table + " in database " + database.name);

        if (!(database.hasOpenConnection())) {
            Debug.pl("! The databases does not have an open connection.");
            return;
        }

        switch (table) {
            case ("item") :
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS itemIndex;");
                break;
            case ("userSNS") :
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS userSNSIndex1;");
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS userSNSIndex2;");
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS userSNSIndex3;");
                break;
            case ("user_action") :
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS userActionIndex1;");
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS userActionIndex2;");
                break;
            case ("user_keywords") :
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS userKeywordsIndex1;");
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS userKeywordsIndex2;");
                break;
            case ("user_profile") :
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS userProfileIndex;");
                break;
            case ("tags") :
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS tagsIndex1;");
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS tagsIndex2;");
                break;
            case ("itemKey") :
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS itemKeyIndex;");
                break;
            case ("rec_log_train") :
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS recLogTrainIndex;");
                break;
            case ("rec_log_train_pos") :
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS recLogTrainPosIndex;");
                break;
            case ("rec_log_train_neg") :
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS recLogTrainNegIndex;");
                break;
            case ("rec_log_test") :
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS recLogTestIndex;");
                break;
            case ("itemCat") :
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS itemCatIndex1;");
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS itemCatIndex2;");
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS itemCatIndex3;");
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS itemCatIndex4;");
                database.getStatement().executeUpdate("DROP INDEX IF EXISTS itemCatIndex5;");
                break;
            default :
                Debug.pl("! ERROR: Did not recognize table name.");
                break;
        }
        Database.dropAllOldTableIndexes(database);
        Debug.pl("> Table " + table + " from database " + database.name + " is no longer indexed.");
    }

    public static void vacuumDatabase(Database database){
        try{
            Debug.pl("Vacuuming database " + database.name + ". This will take some time...");
            database.getStatement().executeUpdate("VACUUM;");
            Debug.pl("Vacuuming completed.");
        }
        catch (Exception e){ e.printStackTrace();}
    }

    public static void indexAllTables(Database database){
        try {
            Database.indexTable(database,"item");
            Database.indexTable(database,"userSNS");
            Database.indexTable(database,"user_action");
            Database.indexTable(database,"user_keywords");
            Database.indexTable(database,"user_profile");
            Database.indexTable(database,"tags");
            Database.indexTable(database,"itemKey");
            Database.indexTable(database,"rec_log_train");
            Database.indexTable(database,"rec_log_train_pos");
            Database.indexTable(database,"rec_log_train_neg");
            Database.indexTable(database,"rec_log_test");
            Database.indexTable(database, "itemCat");
        }
        catch (Exception e){ e.printStackTrace();}
    }
    public static void dropAllTableIndexes(Database database){
        try {
            Database.dropAllOldTableIndexes(database);
            Database.dropTableIndex(database,"item");
            Database.dropTableIndex(database,"userSNS");
            Database.dropTableIndex(database,"user_action");
            Database.dropTableIndex(database,"user_keywords");
            Database.dropTableIndex(database,"user_profile");
            Database.dropTableIndex(database,"tags");
            Database.dropTableIndex(database,"itemKey");
            Database.dropTableIndex(database,"rec_log_train");
            Database.dropTableIndex(database,"rec_log_train_pos");
            Database.dropTableIndex(database,"rec_log_train_neg");
            Database.dropTableIndex(database,"rec_log_test");
            Database.dropTableIndex(database, "itemCat");
        }
        catch (Exception e){ e.printStackTrace();}
    }

    public static void dropAllOldTableIndexes(Database database){
        try{
            database.getStatement().executeUpdate("DROP INDEX IF EXISTS indUserID;");
            database.getStatement().executeUpdate("DROP INDEX IF EXISTS indItemID;");
            database.getStatement().executeUpdate("DROP INDEX IF EXISTS indID;");
            database.getStatement().executeUpdate("DROP INDEX IF EXISTS indFollID;");
            database.getStatement().executeUpdate("DROP INDEX IF EXISTS indAutoID;");
            database.getStatement().executeUpdate("DROP INDEX IF EXISTS userKeywordsIndex;");
            database.getStatement().executeUpdate("DROP INDEX IF EXISTS tagsIndex;");
        }
        catch (Exception e){e.printStackTrace();}
    }

    public static void refactorDatbase(Database database){
        try{
            Debug.pl("Refactoring database " + database.name +". This might take some time...");
            Database.dropAllTableIndexes(database);
            Database.vacuumDatabase(database);
            Database.indexAllTables(database);
        }
        catch (Exception e){e.printStackTrace();}
    }

    public void executeUpdate(String query) throws SQLException {
    	stat.executeUpdate(query);
    }
    
    public Statement getStatement() throws Exception{
    	return this.conn.createStatement();
    }

    public Statement createStatement() {
    	Statement stat = null;
    	try {
			stat = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return stat;
    }
    
    /**
     * Transfer a table from a specified database to another specified database.
     * Warning: the table specified must both exist in the from and destination database.
     * @throws SQLException 
     */

    public static void transferTable(Database from, Database dest, String table) throws Exception {
    	Debug.pl("> Transfering table " + table + " in " + from.name + " to " + dest.name + "... 0%");

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
            case ("rec_log_train_pos") :
                dest.getStatement().executeUpdate("INSERT OR IGNORE INTO dest.rec_log_train_pos(autoID, UserID, ItemId, result, timeStamp) SELECT * FROM orig.rec_log_train_pos;");
                break;
            case ("rec_log_train_neg") :
                dest.getStatement().executeUpdate("INSERT OR IGNORE INTO dest.rec_log_train_neg(autoID, UserID, ItemId, result, timeStamp) SELECT * FROM orig.rec_log_train_neg;");
                break;
            case ("rec_log_test") :
	        	dest.getStatement().executeUpdate("INSERT OR IGNORE INTO dest.rec_log_test(autoID, UserID, ItemId, result, timeStamp) SELECT * FROM orig.rec_log_test;");
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
            case ("itemCat") :
                dest.getStatement().executeUpdate("INSERT OR IGNORE INTO dest.itemCat(autoID, itemID, cat1, cat2, cat3, cat4) SELECT * FROM orig.itemCat;");
                break;
	        default :
	        	Debug.pl("! ERROR: Did not recognize table name.");
	        	break;
        }
        
        // Detach the from database from the destination database
        dest.getStatement().execute("DETACH orig;");
        dest.getStatement().execute("DETACH dest;");
        
        Debug.pl("> Transfering table " + table + " in " + from.name + " to " + dest.name + "... 100%");
    }

    public void TransferRecLogTrainIntoTwoTables() throws Exception {
        this.getStatement().executeUpdate("INSERT OR IGNORE INTO rec_log_train_pos(UserID, ItemId, result, timeStamp) SELECT UserID, ItemId, result, timeStamp FROM rec_log_train WHERE result = 1;");
        Debug.pl("Transferring to rec_log_train_pos complete.");
        this.getStatement().executeUpdate("INSERT OR IGNORE INTO rec_log_train_neg(UserID, ItemId, result, timeStamp) SELECT UserID, ItemId, result, timeStamp FROM rec_log_train WHERE result = -1;");
        Debug.pl("Transferring to rec_log_train_neg complete.");
    }

    public void ResetAutoIdForRecLogTrainDividedTables() throws Exception{
        for(int i=0;i<Util.TOTAL_DATABASE_REC_LOG_TRAIN_POS_LENGTH;i++){
            this.getStatement().executeUpdate( "UPDATE"     );
        }
    }

        /**
         * Retrieve a user using its id
         * @param id The user id
         */
    public User getUserUsingID(int id)throws Exception{
    	User u = null;
    	
    	try {
			ResultSet userRes =
					stat.executeQuery("SELECT * FROM user_profiles WHERE UserID=" + id);
			
			userRes.first();
			
			u = new User(id,this);
			
		} catch (SQLException e) {
			System.out.println("An error occured while trying to retrieve user from ID");
			e.printStackTrace();
		}
    	
    	return u;
    }
    
    /**
     * Retrieve a item using its id
     * @param id The item id
     */
    public Item getItemUsingID(int id)throws Exception{
    	Item item = null;
    	
    	try {
			ResultSet res =
					stat.executeQuery("SELECT * FROM item WHERE itemID=" + id);
			
			res.first();
			
			item = new Item(id,this);
			
		} catch (SQLException e) {
			System.out.println("An error occured while trying to retrieve user from ID");
			e.printStackTrace();
		}
    	
    	return item;
    }
    
    public HashMap<Integer,Item> getItems(){
    	HashMap<Integer,Item> results = new HashMap<Integer, Item>();

    	Statement itemStat = createStatement();

    	try {
    		// TODO optimize
			ResultSet set = itemStat.executeQuery("SELECT itemID FROM item");
			while (set.next()) {
				int id = set.getInt("itemID");
				results.put(id, new Item(id, this));
			}
			itemStat.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return results;
    }

	/**
     * Retrieve keywords matching with the given user
     * @param userID
     * @return
     */
	public HashMap<Integer, Double> getKeywords(int userID)
	{
		HashMap<Integer, Double> results = new HashMap<Integer, Double>();
		
		try {
			ResultSet rs = 
					this.stat.executeQuery("SELECT * FROM user_keywords WHERE UserID=" + userID);
						
			while (rs.next()) {
				results.put(rs.getInt("Keyword"), rs.getDouble("Weight"));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<IntegerPair> getTrainDataFor(int userID) {

		ArrayList<IntegerPair> results = new ArrayList<IntegerPair>();

		try {
			Statement trainDataStat = createStatement();
			ResultSet rSet =  trainDataStat.executeQuery(
					"SELECT UserID, ItemId, result FROM rec_log_train WHERE UserID=" + userID + ";");

			while (rSet.next())
			{
				results.add(new IntegerPair(rSet.getInt("ItemId"),rSet.getInt("result")));
			}

			rSet.close();
			trainDataStat.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return results;
	}
}
