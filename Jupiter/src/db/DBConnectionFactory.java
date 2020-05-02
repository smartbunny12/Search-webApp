package db;

import db.mysql.MySQLConnection;

public class DBConnectionFactory {
	private static final String DEFULT_DB = "mysql";
	
	public static DBConnection getConnection(String db) {
		switch(db) {
		case "mysql":
			return new MySQLConnection();
		case "mongodb" :
			//return new MongoDBConnection();
			return null;
		default:
			throw new IllegalArgumentException("Invalid connection");

		}
	}
	
	public static DBConnection getConnection() {
		return getConnection(DEFULT_DB);
	}
}