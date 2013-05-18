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


/*********
 HOW TO SELECT A SPECIFIC DATABASE FILE AND SET A DIRECTORY:
 _______________

 LOCAL_DB_PATH = "/home/leo/work/mydatabase.db" gives the following URL:
 Connection connection = DriverManager.getConnection("jdbc:sqlite:/home/leo/work/mydatabase.db");



 */

public class Database {
    public static String PATH_INSIDE_CURRENT_PROJECT = "../ML_projecter_twitter.sqlite";    // DATABASE NAME AND (optional) PATH
    public static final String JDBC_DRIVER = "org.sqlite.JDBC";
    public static final String JDBC_URL = "jdbc:sqlite:" + PATH_INSIDE_CURRENT_PROJECT;
    public static final String JDBC_USER = "root";
    public static final String JDBC_PASSWORD = "password";

    private Connection conn = null;
    private Statement stat = null;
    
    public Database() throws Exception{
    	open_connection();
    }
    public void insert(String table, String values) throws SQLException{
        stat.executeUpdate("INSERT INTO "+ table + "VALUES(" +values+ ")");
    }
    public void open_connection() throws SQLException{
        try {
            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            this.stat = conn.createStatement();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void close_connection() throws SQLException{
    	this.conn.close();
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
