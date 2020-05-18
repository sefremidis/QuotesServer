import java.sql.*;

public class QuotesDBConnection {

	private static final String connURL = "jdbc:mysql://localhost:3306/quotes_db";
	private static final String driver = "com.mysql.cj.jdbc.Driver";
	private static final String connUser = "root";
	private static final String connPasswd = "root";

	private static Connection conn = null;
	
	private QuotesDBConnection() {
		
	}
	
	public static Connection getConnection() {
		if (conn == null) {
			try {
				Class.forName(driver);
				conn = DriverManager.getConnection(connURL, connUser, connPasswd);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return conn;
	}
	
	public static void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			System.err.println("Quotes Server: can not close connection to DB.");
			e.printStackTrace();
		}
	}
}
