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
	private String filename = "bin/videoList.xml";
	
	// Networking
	private final int port = 1338;
	private ServerSocket serverSocket;
	Socket clientSocket;
	
	Thread socketThread;
	
	ObjectOutputStream outputToClient;
	ObjectInputStream inputFromClient;
	
	// VLC
	private String vlcLibraryPath = ".\\vlc-2.0.1";
		
	public Server () {
		
		//Add shoutsown hook
		addShutdownHook();
		
		//Setup VLC
		setUpVLC();
		
		// get video list
		XMLReader reader = new XMLReader();
		videos = reader.getList(filename);
		
		for(VideoFile vf : videos){
			vf.setImagename(ScreenShotter.getScreenShot(vf.getFilename()));
		}
					
		// Start networking thread
		socketThread = makeSocketThread();
		socketThread.start();
	}
	
	private void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
		    @Override
		    public void run()
		    {
				System.out.println("SERVER:: closing");

	        	try {
					outputToClient.close();
				} catch (IOException e) {
					System.out.println("SERVER:: couldn't close output stream");
					e.printStackTrace();
				}		        
	        	try {
	        		inputFromClient.close();
				} catch (IOException e) {
					System.out.println("SERVER:: couldn't close input stream");
					e.printStackTrace();
				}
	        	try {
	        		clientSocket.close();
				} catch (IOException e) {
					System.out.println("SERVER:: couldn't close client socket");
					e.printStackTrace();
				}
	        	try {
	        		serverSocket.close();
				} catch (IOException e) {
					System.out.println("SERVER:: couldn't close server socket");
					e.printStackTrace();
				}
		    }
		});
		
	}

	private Thread makeSocketThread(){
		return new Thread("Socket") {
			public void run() {
				
				MediaPlayerFactory mediaPlayerFactory;
				HeadlessMediaPlayer mediaPlayer;
				
				try {
					openSocket();
					openOutputstream();
					writeListToSocket();
					
					mediaPlayerFactory = new MediaPlayerFactory();
					mediaPlayer = mediaPlayerFactory.newHeadlessMediaPlayer();
					
					while(true){
						//serverSocket.accept();
						doStream(mediaPlayer);
					}
					
				} 
				catch (IOException e) {
					System.out.println("Server:: ERROR on socket connection.");
					e.printStackTrace();
				}
			}
		};
	}
	
	private void openSocket(){
		System.out.println("Server:: Opening Sockets");

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Server:: Could not listen on port : " + port);
			e.printStackTrace();
		}
		System.out.println("Server:: Opened serverSocket : " + serverSocket.toString());


		try {
			clientSocket = serverSocket.accept();
		} catch (IOException e) {
			System.out.println("Server:: Could not open client connection");
			e.printStackTrace();
		}
		System.out.println("Server:: Opened clientSocket : " + clientSocket.toString());

	}

	private void openOutputstream(){
		try {
			outputToClient = new ObjectOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Server:: Could not open client output stream ");
			e.printStackTrace();
		}
		System.out.println("Server:: Started output stream ");
		
	}
	
	private void openInputStream(){
		try {
			inputFromClient = new ObjectInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			System.out.println("Server:: Could not open client input stream ");
			e.printStackTrace();
		}
		System.out.println("Server:: Started input stream ");
	}
	
	private void doStream(HeadlessMediaPlayer mediaPlayer){
		
		System.out.println("Server:: doing stream");
		
		if (inputFromClient == null){
			openInputStream();
		}
		
		VideoFile vf = null;

		try {
			vf = (VideoFile)inputFromClient.readObject();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Server:: Error getting selected videofile from client ");
			e.printStackTrace();
		}
		System.out.println("Server:: Streaming " + vf.getTitle());

		String filename = "../"+vf.getFilename();
				
		String options = formatRtpStream("127.0.0.1", 5555);
		
		
		mediaPlayer.playMedia(filename, options, ":no-sout-rtp-sap", ":no-sout-standardsap",
				":sout-all", ":sout-keep");
		mediaPlayer.parseMedia();

		
		System.out.println("SERVER :: Length of video is : " + mediaPlayer.getLength()/1000);
		
		try {
			outputToClient.writeObject((Long)mediaPlayer.getLength());
		} catch (IOException e) {
			System.out.println("SERVER :: Could not tell client length of video");
			e.printStackTrace();
		}
	}
	
	private void setUpVLC() {
		System.out.println("Server:: Setting up VLC");

		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), vlcLibraryPath);
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		
	}
	
	private void writeListToSocket() throws IOException{
		List<VideoFile> list = this.getList();
		list.forEach((vf)->{vf.loadImage();});
		
		outputToClient.writeObject(list);
	}

	
	public List<VideoFile> getList(){
		return this.videos;
	}
	
	public int getPort(){
		return this.port;
	}

	public static void main(String[] args) {
		System.out.println("Server:: Starting");

		new Server();
	}
	
	public void Stop(){
		
		socketThread.interrupt();
		
		try {
			this.clientSocket.close();
		} catch (IOException e) {
			System.out.println("Server:: Unable to close client socket");
			e.printStackTrace();
		}
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			System.out.println("Server:: Unable to close server socket");
			e.printStackTrace();
		}
	}
	
	private String formatRtpStream(String serverAddress, int serverPort) {
		StringBuilder sb = new StringBuilder(60);
		sb.append(":sout=#rtp{dst=");
		sb.append(serverAddress);
		sb.append(",port=");
		sb.append(serverPort);
		sb.append(",mux=ts}");
		return sb.toString();
	}
	
}

