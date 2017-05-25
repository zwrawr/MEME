package meme.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import meme.common.VideoFile;
import uk.co.caprica.vlcj.binding.LibVlc; 
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class Server {
	
	// Video list data
	private List<VideoFile> videos;
	private final String videoListDirectory = "bin/videoList.xml";
	
	// Networking
	private final int port = 1338;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private ObjectOutputStream outputToClient;
	private ObjectInputStream inputFromClient;
	
	// VLC
	private final String vlcLibraryPath = ".\\vlc-2.0.1";
	
	// Server Operation
	Thread socketThread;
	private MediaPlayerFactory mediaPlayerFactory ;
	private HeadlessMediaPlayer mediaPlayer;
	
	// Singleton design pattern
	private static Server Instance;
	
	// Bool to see if we should keep listening
	private static boolean running = false;
	
	
	private Server () {
		
		//Add shutdown hook
		addShutdownHook();
		
		//Setup VLC
		setUpVLC();
		
		// Process XML video list and store
		XMLReader reader = new XMLReader();
		videos = reader.getList(videoListDirectory);
		
		// Generate Thumbnails for each VideoFile in videos
		for(VideoFile vf : videos){
			vf.setImagename(ScreenShotter.getScreenShot(vf.getFilename()));
		}
		
		// Create media player factory
		this.mediaPlayerFactory = new MediaPlayerFactory();
		// Create new headless media player
		this.mediaPlayer = mediaPlayerFactory.newHeadlessMediaPlayer();
		
		Thread comm = new Thread(){
			@Override
		    public void run()
		    {
				// Open socket, establish connection with client, and open stream
				openSocket();
				openOutputstream();
				
				// Start socket thread
				socketThread = makeSocketThread();
				socketThread.start();
		    }
		};
		comm.start();
		System.out.println("SERVER:: finish init");
	}
	
	public static Server getInstance(){
		if (Server.Instance == null){
			System.out.println("SERVER:: CREATING NEW SERVER");
			Server.Instance = new Server();
		}
		
		Server.running = true;
		return Server.Instance;
	}
	
	public static boolean isRunning(){
		return Server.running;
	}
	
	private Thread makeSocketThread(){
		return new Thread("Socket") {
			/* 
			 * This thread is designed to be run in future releases for 
			 * each of multiple clients. 
			 * 
			 */
			public void run() {
				// Store thumbnail in VideoFiles and send list to client.
				// (allowing thumbnails to be stored in server, but visible in client)
				videos.forEach((vf)->{vf.loadImage();});
				writeToOutputStream(videos);
				
				
				// Report
				System.out.println("Server:: doing stream");
				
				while(Server.running){
					//System.out.println("Running : " + Server.running );
					doStream();
				}
			}
		};
	}
	
	
	private void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
		    @Override
		    public void run()
		    {
				System.out.println("SERVER:: closing");

	        	try {
	        		if(outputToClient != null){
	        			outputToClient.close();
	        		}
				} catch (IOException e) {
					System.out.println("SERVER:: couldn't close output stream");
					e.printStackTrace();
				}		        
	        	try {
	        		if(inputFromClient != null){
	        			inputFromClient.close();
	        		}
				} catch (IOException e) {
					System.out.println("SERVER:: couldn't close input stream");
					e.printStackTrace();
				}
	        	try {
	        		if(clientSocket != null && !clientSocket.isClosed()){
	        			clientSocket.close();
	        		}
				} catch (IOException e) {
					System.out.println("SERVER:: couldn't close client socket");
					e.printStackTrace();
				}
	        	try {
	        		if(serverSocket != null && !serverSocket.isClosed()){
	        			serverSocket.close();
	        			serverSocket.setReuseAddress(true);
	        		}
				} catch (IOException e) {
					System.out.println("SERVER:: couldn't close server socket");
					e.printStackTrace();
				}
		    }
		});
		
	}

	
	
	private void openSocket(){
		/*
		 * Currently, this opens a socket, listens for connection, and
		 * stores connected socket information for one connection.
		 * Future iterations will allow for more than one connection.
		 */
		System.out.println("Server:: Opening Sockets");
		
		// Create Socket
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Server:: Could not listen on port : " + port);
			e.printStackTrace();
		}
		System.out.println("Server:: Opened serverSocket : " + serverSocket.toString());

		// Waits for client to connect, then stores socket information
		try {
			clientSocket = serverSocket.accept();
		} catch (IOException e) {
			System.out.println("Server:: Could not open client connection");
			e.printStackTrace();
		}
		// Report that client connected.
		System.out.println("Server:: Opened clientSocket : " + clientSocket.toString());

	}

	private void openOutputstream(){
		try {
			outputToClient = new ObjectOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Server:: Could not open client output stream ");
			e.printStackTrace();
		}
		// Report
		System.out.println("Server:: Started output stream ");
		
	}
	
	private void openInputStream(){
		
		if (!clientSocket.isClosed()){
			System.out.println("SERVER:: Attempted to read from a closed socket!");
			return;
		}
		
		try {
			inputFromClient = new ObjectInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			System.out.println("Server:: Could not open client input stream ");
			e.printStackTrace();
		}
		// Report
		System.out.println("Server:: Started input stream ");
	}
	
	private void doStream(){
		
		// Make sure that an input stream exists
		if (inputFromClient == null){
			openInputStream();
		}
		
		// Receive object from stream (blocking), and process received
		// object depending on type.
		
		final Object obj = readFromInputStream();
		
		
		if(obj instanceof VideoFile){
			/* If read object is a video file (request to play new video),
			   start playing new video. */
			
			// Store VideoFile
			final VideoFile vf = (VideoFile)obj;
			
			// Instruct mediaPlayer to stream new media based on vf data
			String filename = "../"+vf.getFilename();
			String options = formatRtpStream("127.0.0.1", 5555);
			mediaPlayer.playMedia(filename, options, ":no-sout-rtp-sap", ":no-sout-standardsap",
					":sout-all", ":sout-keep");
			
			// Confirm/report that server has started streaming new media
			System.out.println("Server:: Streaming " + vf.getTitle());
		}
	}
	
	private void setUpVLC() {
		// Set up VLC libraries
		System.out.println("Server:: Setting up VLC");

		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), vlcLibraryPath);
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		
	}
	
	private Object readFromInputStream(){
		
		if(inputFromClient == null || clientSocket == null || clientSocket.isClosed()){
			return null;
		}
		
		// tries reading from stream, and returns object if successful
		Object obj = null;
		try {
			obj = inputFromClient.readObject();
		} catch (ClassNotFoundException e) {
			System.out.println("Server:: Unable to read from stream!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Server:: Unable to read from stream!");
			e.printStackTrace();
		}
		return obj;
	}
	
	private void writeToOutputStream(Object obj){
		// tries writing object to output stream
		try {
			outputToClient.writeObject(obj);
		} catch (IOException e) {
			System.out.println("Server:: Unable to send object: " + obj.toString());
			e.printStackTrace();
		}
	}
	
	public static void Stop(){
		// When called, stop threads, and close sockets.
				
		if (Server.Instance == null){
			return;
		}
		
		Server.running = false;
		
		
		Server s = Server.Instance;
		s.socketThread.interrupt();
		try {
			s.clientSocket.close();
		} catch (IOException e) {
			System.out.println("Server:: Unable to close client socket");
			e.printStackTrace();
		}
		try {
			s.serverSocket.close();
		} catch (IOException e) {
			System.out.println("Server:: Unable to close server socket");
			e.printStackTrace();
		}
		
		// kill this instance by removing its reference
		Server.Instance = null;
	}
	
	private String formatRtpStream(String serverAddress, int serverPort) {
		// Builds string recognised by media player for streaming over IP
		StringBuilder sb = new StringBuilder(60);
		sb.append(":sout=#rtp{dst=");
		sb.append(serverAddress);
		sb.append(",port=");
		sb.append(serverPort);
		sb.append(",mux=ts}");
		return sb.toString();
	}
	
	//////////////////////////// GETTERS ////////////////////////////
	public List<VideoFile> getList(){
		return this.videos;
	}
	
	public int getPort(){
		return this.port;
	}
	
	
	///////////////////////////// MAIN //////////////////////////////
	
	public static void main(String[] args) {
		System.out.println("Server:: Starting");
		Server s = Server.getInstance();
	}
	


	
}

