import java.io.*;
import java.net.*;
import java.util.*;

public class QuotesServer {

	private static final int SERVER_PORT = 10001;

	public static void main(String[] args) {

		ArrayList<String[]> quotes = new ArrayList<String[]>();

		// start thread to periodically update list of quotes
		new Thread(new QuotesUpdate(quotes)).start();
		
		// start management thread
		new Thread(new QuotesManagement()).start();
		
		ServerSocket sskt = null;
		try {
			// Register service on port SERVER_PORT
			sskt = new ServerSocket(SERVER_PORT);

			// start servicing client requests
			while (true) {
				// Wait until a client request arrives
				Socket cskt = sskt.accept();
				// spawn a thread to service request
				new Thread(new QuotesClientService(quotes, cskt)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (sskt != null)
				try {
					sskt.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}
