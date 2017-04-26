package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {
	private List<VideoFile> videos;
	private String filename = "bin/videoList.xml";
	
	private ServerSocket serverSocket;
	private final int port = 1138;
	Socket clientSocket;
	
	Thread socketThread;
	
	ObjectOutputStream outputToClient;
		
	public Server () {
		XMLReader reader = new XMLReader();
		videos = reader.getList(filename);
			
		socketThread = new Thread("Socket") {
			public void run() {
				try {
					openSocket();
					writeListToSocket();
					clientSocket.close();
					serverSocket.close();
				} 
				catch (IOException e) {
					System.out.println("ERROR on socket connection.");
					e.printStackTrace();
				}
			}
		};
		socketThread.start();
	}
	
	private void openSocket(){
	
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Could not listen on port : " + port);
			e.printStackTrace();
		}

		try {
			clientSocket = serverSocket.accept();
		} catch (IOException e) {
			System.out.println("Could not open client connection");
			e.printStackTrace();
		}

		try {
			outputToClient = new ObjectOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Could not open client output stream ");
			e.printStackTrace();
		}
		
		System.out.println("Opened socket on : " + port + ", waiting for client.");

	}
	
	private void writeListToSocket() throws IOException{
		List<VideoFile> list = this.getList();
		outputToClient.writeObject(list);
	}

	
	public List<VideoFile> getList(){
		return this.videos;
	}
	
	public int getPort(){
		return this.port;
	}

	public static void main(String[] args) {
		new Server();
	}
}

