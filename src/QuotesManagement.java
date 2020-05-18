import java.util.*;
import java.io.*;
import java.net.*;
import java.sql.*;

public class QuotesManagement implements Runnable {

	private static final int SERVER_MGT_PORT = 20001;

	public void run() {
		ServerSocket sskt = null;

		try {
			// Register service on port SERVER_MGT_PORT
			sskt = new ServerSocket(SERVER_MGT_PORT);

			// Start servicing management requests
			while (true) {
				// Wait until a client request arrives
				Socket cskt = sskt.accept();

				ObjectInputStream ois = new ObjectInputStream(cskt.getInputStream());
				ObjectOutputStream oos = new ObjectOutputStream(cskt.getOutputStream());

				// Read management command
				String[] cmd = (String[]) ois.readObject();
				if (cmd[0].equals("stop")) {
					// Close DB Connection and exit
					QuotesDBConnection.closeConnection();
					System.exit(0);
				} else if (cmd[0].equals("list")) {
					Statement stmt = null;
					ResultSet rs = null;
					try {
						ArrayList<String[]> al = new ArrayList<String[]>();

						Connection conn = QuotesDBConnection.getConnection();
						stmt = conn.createStatement();
						rs = stmt.executeQuery("SELECT * FROM QUOTES_TABLE");

						while (rs.next()) {
							int quotenum = rs.getInt("ID");
							String quote = rs.getString("QUOTE");
							String author = rs.getString("AUTHOR");
							al.add(new String[] { Integer.toString(quotenum), quote, author });
						}
						oos.writeObject(al);
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						try {
							if (rs != null)
								rs.close();
							if (stmt != null)
								stmt.close();
						} catch (SQLException e) {
							System.err.println("Quotes Management: can not release DB resources.");
							e.printStackTrace();
						}
					}
				} else if (cmd[0].equals("del")) {
					Statement stmt = null;
					ResultSet rs = null;
					try {
						Connection conn = QuotesDBConnection.getConnection();

						stmt = conn.createStatement();
						stmt.executeUpdate("DELETE FROM QUOTES_TABLE WHERE id=" + cmd[1]);
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						try {
							if (rs != null)
								rs.close();
							if (stmt != null)
								stmt.close();
						} catch (SQLException e) {
							System.err.println("Quotes Management: can not release DB resources.");
							e.printStackTrace();
						}
					}
				} else if (cmd[0].equals("add")) {
					Statement stmt = null;
					ResultSet rs = null;
					try {
						Connection conn = QuotesDBConnection.getConnection();

						stmt = conn.createStatement();
						stmt.executeUpdate(
								"INSERT INTO QUOTES_TABLE (QUOTE, AUTHOR) VALUES ('" + cmd[1] + "', '" + cmd[2] + "')");
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						try {
							if (rs != null)
								rs.close();
							if (stmt != null)
								stmt.close();
						} catch (SQLException e) {
							System.err.println("Quotes Management: can not release DB resources.");
							e.printStackTrace();
						}
					}
				}
				ois.close();
				oos.close();
				cskt.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (sskt != null)
				try {
					sskt.close();
				} catch (IOException e) {
					System.err.println("Quotes Management: Can not close server socket.");
					e.printStackTrace();
				}
		}
	}
}
