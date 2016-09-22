import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientManager implements Runnable {

	private BufferedReader br;

	private String name;

	private PrintWriter pw;

	private final Socket socket;

	private ServerGameManager server;

	public ClientManager(Socket socket, ServerGameManager server) {
		this.socket = socket;
		this.server = server;
	}

	public void dispatch(final String message) {
		if (pw != null && message != null) {
			pw.println(message);
		}
	}

	public String getName() {
		return name;

	}

	@Override
	public void run() {
		try {
			server.setReady(this);
			final boolean running = true;
			while (running) {
				final String buffer = br.readLine();
				server.received(buffer);
			}
		} catch (final IOException e) {
			System.out.println("Client disconnected: " + name);
		}

	}

	String setup() throws IOException {
		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		pw = new PrintWriter(socket.getOutputStream(), true);
		name = br.readLine();
		server.dispatch(server.getConnectedClientNames(), null);
		return name;
	}
}
