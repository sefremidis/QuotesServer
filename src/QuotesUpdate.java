import java.util.*;
import java.sql.*;

public class QuotesUpdate implements Runnable {

	private ArrayList<String[]> quotes;
	private Connection conn = null;

	public QuotesUpdate(ArrayList<String[]> quotes) {
		this.quotes = quotes;
		conn = QuotesDBConnection.getConnection();
	}

	private void update() {
		if (conn == null)
			return;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			// read all quotes from the DB
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM QUOTES_TABLE");
			// fill in quotes list
			synchronized (quotes) {
				quotes.clear();
				while (rs.next()) {
					quotes.add(new String[] { rs.getString("QUOTE"), rs.getString("AUTHOR") });
				}
			}
		} catch (SQLException e) {
			System.err.println("Quotes Server: Error while reading DB.");
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				System.err.println("Quotes Server: Can not close SQL Statement and/or ResultSet.");
				e.printStackTrace();
			}
		}
	}

	public void run() {
		try {
			while (true) {
				update();
				Thread.sleep(1 * 60 * 1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
