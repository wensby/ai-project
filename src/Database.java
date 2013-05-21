
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


}










