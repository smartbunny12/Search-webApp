package db.mysql;

public class MySQLDBUtil {
	private static final String HOSTNAME = "localhost";
	private static final String PORT_NUM ="8889";
	public static final String DB_NAME = "eventproject";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";
	public static final String URL ="jdbc:mysql://" 
			+ HOSTNAME + ":"+ PORT_NUM +"/"+ DB_NAME 
			+ "?user=" + USERNAME + "&password=" + "PASSWORD" 
			+ "&autoReconnect=true&serverTimeZone=UTC";
	//jdbc:mysql://localhost:8889/eventproject?user=root&password=root&&autoReconnect=true&serverTimeZone=UTC
}