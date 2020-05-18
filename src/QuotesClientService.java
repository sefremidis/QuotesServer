import java.util.*;
import java.io.*;
import java.net.*;

public class QuotesClientService implements Runnable {

	private ArrayList<String[]> quotes;
	private Socket cskt;

	public QuotesClientService(ArrayList<String[]> quotes, Socket cskt) {
		this.quotes = quotes;
		this.cskt = cskt;
	}

	public void run() {
		try {
			ObjectInputStream ois = new ObjectInputStream(cskt.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(cskt.getOutputStream());

			// read the client request - unused
			ois.readObject();

			String[] quote;
			synchronized (quotes) {
				// pick a random quote from the list
				if (quotes.size() == 0)
					quote = new String[] { "?", "?" };
				else
					quote = quotes.get(new Random().nextInt(quotes.size()));
			}
			oos.writeObject(quote);
			oos.close();
			ois.close();
			cskt.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
