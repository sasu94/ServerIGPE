import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class mainServer extends JFrame {
	JPanel panel;
	JLabel lblPortField;
	JTextField port;
	ServerSocket server;
	Socket connectionSocket;
	JLabel connectionState;
	JButton startServer;
	JButton closeServer;
	boolean connected = false;
	private boolean running = true;

	public mainServer() {
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(350, 100));
		panel.setLayout(null);
		setContentPane(panel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		lblPortField = new JLabel("Insert port number");
		lblPortField.setBounds(10, 10, 300, 20);
		panel.add(lblPortField);

		port = new JTextField("8080");
		port.setBounds(150, 10, 50, 20);
		panel.add(port);

		connectionState = new JLabel("closed");
		connectionState.setForeground(Color.RED);
		connectionState.setBounds(10, 30, 50, 20);
		panel.add(connectionState);

		startServer = new JButton("Start");
		startServer.setBounds(220, 10, 80, 20);
		startServer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (port.getText().isEmpty() || !(isNumeric(port.getText())))
					JOptionPane.showMessageDialog(null, "insert a number please");
				else
					avviaServer(Integer.parseInt(port.getText()));

				connectionState.setText("open");
				connectionState.setForeground(Color.GREEN);
			}
		});
		panel.add(startServer);

		pack();

	}

	public static void main(String[] args) {
		mainServer f = new mainServer();
		f.setVisible(true);

	}

	private void avviaServer(int porta) {
		new Thread() {
			@Override
			public void run() {

				try {
					runServer(porta);
				} catch (BindException b) {
					JOptionPane.showMessageDialog(null, "port already used");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};

		}.start();

	}

	private void runServer(int porta) throws IOException {
		server = new ServerSocket(porta);
		while (running) {
			System.out.println("waiting for connection");
			final ServerGameManager gameManager = new ServerGameManager();
			final Socket socket1 = server.accept();
			final ClientManager cm1 = new ClientManager(socket1, gameManager);
			gameManager.add(cm1);
			System.out.println("client 1 connected");
			final Socket socket2 = server.accept();
			final ClientManager cm2 = new ClientManager(socket2, gameManager);
			gameManager.add(cm2);
			System.out.println("client 2 connected");
			gameManager.startGame();
			System.out.println("game created");
		}
	}

	public boolean isNumeric(String s) {
		return s.matches("[-+]?\\d*\\.?\\d+");
	}

}
