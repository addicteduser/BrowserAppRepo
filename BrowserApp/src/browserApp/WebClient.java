package browserApp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WebClient implements Runnable{
	/**
	 * Web server port = 80
	 */
	private Thread T;
	
	private final int port = 8080;
	private final String serverName = "localhost";
	private Socket connection;
	private BufferedReader input;
	private PrintWriter output;
	private int i;
	
	public WebClient(int i) {
		this.i = i;
	}
	
	public void start(){
		T = new Thread(this);
		Driver.ThreadPool(T);
		//T.start();

	}
	
//	public static void main(String[] args) {
//		new WebClient(0).start();
//	}
	@Override
	public void run(){
		connectToWebServer();
		initializeIOStreams();
		sendHttpGetRequest(); //send HTTP request
		receiveResponse(); //receive server response
		//receiveResponse2();
		closeConnection();
	}

	/**
	 * Makes the connection to the web server via the port and the IP address of the serverName.
	 */
	private void connectToWebServer() {
		try {
			connection = new Socket(InetAddress.getByName(serverName), port);
			//System.out.println("[CLIENT] Connected to server...");
		} catch (IOException e) {
			System.err.println("ERROR: " + e);
		}
	}

	/**
	 * Initialize the client I/O streams.
	 */
	private void initializeIOStreams() {
		try {
			input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			output = new PrintWriter(new BufferedOutputStream(connection.getOutputStream()), true);
			//System.out.println("[CLIENT] Initialized I/O streams...");
		} catch (IOException e) {
			System.err.println("ERROR: " + e);
		}
	}

	/**
	 * Sends HTTP GET request to server.
	 */
	private void sendHttpGetRequest(){
		String message = "GET / HTTP/1.1\r\n" + "Host: "+serverName+"\r\n\r\n";
		//System.out.println(message);
		output.println(message);
		//System.out.println("[CLIENT] HTTP GET request sent to [HOST:"+serverName+"]...");
	}

	/**
	 * Receives the HTML file from the server and saves it to a text file.
	 */
	private void receiveResponse() {
		String httpResponse;
		FileWriter fileWriter = null;

		System.out.println("[CLIENT] Message received...");

		//create a temporary file
		String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		File logFile = new File(".\\GET Requests\\"+timeLog+"_"+serverName+".txt");
		
		try {
			fileWriter = new FileWriter(logFile);

			System.out.println("[CLIENT] Writing response to text file. Please wait...");
			while((httpResponse = input.readLine())!=null){
				//System.out.println(httpResponse);
				fileWriter.write(httpResponse+"\r\n");
			}

			fileWriter.flush();
		} catch (SocketException e) {
			// Connection reset: Do nothing
			System.err.println("ERROR: " + e);
		} catch (IOException e) {
			System.err.println("ERROR: " + e);
		} finally {
			try {
				System.out.println("[CLIENT] File saved at: "+logFile.getCanonicalPath());
				fileWriter.close();
			} catch (IOException e) {
				System.err.println("ERROR: " + e);
			}
		}
	}
	
	private void receiveResponse2() {
		String httpResponse;
		
		try {
		while((httpResponse = input.readLine())!=null){
			// Do nothing
		}
		} catch (IOException e) {
			System.err.println("ERROR: " + e);
		} finally {
			System.out.println("["+i+"] OK");
		}
	}

	/**
	 * Closes the I/O streams and the socket connection.
	 */
	private void closeConnection() {
		try {
			input.close();
			output.close();
			connection.close();
		} catch (IOException e) {
			System.err.println("ERROR: " + e);
		} finally {
			//System.out.println("[CLIENT] Closed socket connection and I/O streams...");
		}
	}
}
