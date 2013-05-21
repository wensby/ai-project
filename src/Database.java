
/**
 * Created with IntelliJ IDEA.
 * User: tormodhau
 * Date: 5/13/13
 * Time: 11:51 PM
 * To change this template use File | Settings | File Templates.
 */

//package com.rungeek.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Database {
    public static String PATH_INSIDE_CURRENT_PROJECT = "/Users/tormodhau/Dropbox/Fag/Fag/Machine Learning (CS570)/Project/Database/ML_projecter_twitter.sqlite";    // DATABASE NAME AND (optional) PATH
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



    
    //Useful tools
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
     * TODO finish this stuff, not finished
     */
    public Object[] getOneRow(int offset, String tableName) throws SQLException {
		stat.execute("SELECT * FROM " + tableName + " LIMIT 1 OFFSET " + offset);
		ResultSet result = stat.getResultSet();
		Debug.pl(result.getString(3));
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

    //Queries
    public void addToBatch(String table, String values) throws SQLException{
        this.stat.addBatch("INSERT INTO "+ table + " VALUES " +values+ ";");
    }

    public void executeBatch()throws Exception{
        this.stat.executeBatch();
    }

    public static void count_file_lines(String file_place) throws Exception{
        //EX: file_place = "../data/rec_log_train.txt";
        Parser.txt file = new Parser.txt(file_place);
        int counter = 0;
        while(file.hasNext()){
            file.next();
            counter++;
        }
        System.out.println("Final line count of file "+ file_place + " :   " + counter);
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
}














    



//   CREATE  TABLE "main"."item" ("ItemID" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , "ItemCat" TEXT NOT NULL , "Keywords" TEXT NOT NULL ) {

/////////////////////////////
///** ORIGINAL EXAMPLE
///////////////////////////
// * Created with IntelliJ IDEA.
// * User: tormodhau
// * Date: 5/13/13
// * Time: 11:51 PM
// * To change this template use File | Settings | File Templates.
// */
//
////package com.rungeek.sqlite;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.Statement;
//
//
///*********
// HOW TO SELECT A SPECIFIC DATABASE FILE AND SET A DIRECTORY:
// _______________
//
// LOCAL_DB_PATH = "/home/leo/work/mydatabase.db" gives the following URL:
// Connection connection = DriverManager.getConnection("jdbc:sqlite:/home/leo/work/mydatabase.db");
//
//
//
// */
//
//public class Database {
//    public static String PATH_INSIDE_CURRENT_PROJECT = "Data.sqlite";    // DATABASE NAME AND (optional) PATH
//    public static final String JDBC_DRIVER = "org.sqlite.JDBC";
//    public static final String JDBC_URL = "jdbc:sqlite:" + PATH_INSIDE_CURRENT_PROJECT;
//    public static final String JDBC_USER = "root";
//    public static final String JDBC_PASSWORD = "";
//
//    /*
//    Creates a
//    Returns a set of entries if the connection to the
//     */
//    public static void testDatabaseSettings() throws Exception{
//        Connection conn = null;
//        ResultSet rs = null;
//        Statement stat = null;
//
//        try {
//            Class.forName(JDBC_DRIVER);
//            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
//            stat = conn.createStatement();
//            stat.executeUpdate("drop table if exists people_test;");
//            stat.executeUpdate("create table people_test (name, occupation);");
//            PreparedStatement prep = conn.prepareStatement(
//                    "insert into people_test values (?, ?);");
//
//            prep.setString(1, "Gandhi");
//            prep.setString(2, "politics");
//            prep.addBatch();
//            prep.setString(1, "Turing");
//            prep.setString(2, "computers");
//            prep.addBatch();
//            prep.setString(1, "Wittgenstein");
//            prep.setString(2, "smartypants");
//            prep.addBatch();
//
//            conn.setAutoCommit(false);
//            prep.executeBatch();
//            conn.setAutoCommit(true);
//
//            rs = stat.executeQuery("select * from people_test;");
//            while (rs.next()) {
//                System.out.println("name = " + rs.getString("name"));
//                System.out.println("job = " + rs.getString("occupation"));
//            }
//            rs.close();
//            conn.close();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    public static void insertUser() throws Exception {
//
//        // If user exists, then update user
//    }
//
//    public static void updateUser() throws Exception {
//
//    }
//
//    public static void getUserFromID(int id) throws Exception{
//
//    }
//
//    public static void insertItem(int id, String cat, String keywords) throws Exception{
//
//    }
//
//    public static void updateItem(int id, String cat, String keywords) throws Exception{
//
//    }
//
//    public static void getItemFromID(int id) throws Exception{
//
//    }
//
//
//
//
//}
//
//
////   CREATE  TABLE "main"."item" ("ItemID" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , "ItemCat" TEXT NOT NULL , "Keywords" TEXT NOT NULL ) {
//
//


//
//
//
//    // INSERTING 10 000 000 elements takes 48 seconds.
//    // READING 10 000 000 elements with individual searches takes 150 seconds. This was done by rowID of people_test
//    // After INDEXING it takes
//    public static void testDB(){
//        Connection conn = null;
//        ResultSet rs = null;
//        Statement stat = null;
//
//        try {
//            Class.forName(JDBC_DRIVER);
//            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
//            stat = conn.createStatement();
//            //stat.executeUpdate("drop table if exists people_test;");
//            //stat.executeUpdate("create table people_test (name, occupation);");
//            //PreparedStatement prep = conn.prepareStatement("insert into people_test values (?, ?);");
//        /*
//            for ( int i = 0; i<10000000;i++){
//                prep.setString(1,"lalalalallaallala alalalalall;;;;ALKSDLKJDLKAM;SD;MLAKSND");
//                prep.setString(2,"ksdjkj ksjdkjsd ");
//                prep.addBatch();
//
//                if (i % 1000 ==0){
//                    conn.setAutoCommit(false);
//                    prep.executeBatch();
//                    conn.setAutoCommit(true);
//                }
//
//            }
//
//            conn.setAutoCommit(false);
//            prep.executeBatch();
//            conn.setAutoCommit(true);
//        */
//
//            String sql = "SELECT * FROM people_test WHERE rowid = ?";
//            PreparedStatement prep = conn.prepareStatement(sql);
//
//            for (int i = 0; i<10000000; i++){
//                prep.setInt(1,i);
//                ResultSet rs1 = prep.executeQuery();
//                while (rs1.next()){
//                    rs1.getString(1);
//                    rs1.getString(2);
//                }
//            }
//        /*
//            rs = stat.executeQuery("select * from people_test;");
//            while (rs.next()) {
//                System.out.println("name = " + rs.getString("name"));
//                System.out.println("job = " + rs.getString("occupation"));
//            }
//            rs.close();
//
//        */
//            conn.close();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//    }
//
//}
