package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLTableCreation {
	// run this as Java application to reset db schema
	public static void main(String[] args) {
		try {
			// use java.sql.connection
			Connection conn = null;
			//1. connect to MySQL
			try {
				System.out.println("Connecting to " + MySQLDBUtil.URL);
				Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
				conn = DriverManager.getConnection(MySQLDBUtil.URL);
			} catch (SQLException e) {
				System.out.println("fail!!! ");
				e.printStackTrace();
			}
			
			if (conn == null) {
				return;
			}
			
			// step2: Drop table in case they exist
			//DROP TABLE IF EXISTS table_name;

			Statement stmt = conn.createStatement();
			String sql = "DROP TABLE IF EXISTS categories";
			stmt.executeUpdate(sql);
						
			sql = "DROP TABLE IF EXISTS history"; 
			stmt.executeUpdate(sql);
				
			sql = "DROP TABLE IF EXISTS items"; 
			stmt.executeUpdate(sql);
						
			sql = "DROP TABLE IF EXISTS users"; 
			stmt.executeUpdate(sql);
		
			// step3: Create table
			sql = "CREATE TABLE items"
					+ "(item_id VARCHAR(255) NOT NULL,"
					+ " name VARCHAR(255),"
					+ " rating FLOAT,"
					+ " address VARCHAR(255),"
					+ " image_url VARCHAR(255),"
					+ " url VARCHAR(255),"
					+ " distance FLOAT,"
					+ " PRIMARY KEY (item_id))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE categories"
					+ "(item_id VARCHAR(255) NOT NULL,"
					+ "category VARCHAR(255) NOT NULL,"
					+ "PRIMARY KEY (item_id, category),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE users"
					+ "(user_id VARCHAR(255) NOT NULL,"
					+ "password VARCHAR(255) NOT NULL,"
					+ "first_name VARCHAR(255),"
					+ "last_name VARCHAR(255),"
					+ "PRIMARY KEY (user_id))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE history ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ "PRIMARY KEY (user_id, item_id),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id),"
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id))";
			stmt.executeUpdate(sql);
			
			// step4: 
			
			System.out.println("Import is done successfully");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
