package meme.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import meme.common.VideoFile;


public class Client {
	////////////////////////// DESCRIPTION //////////////////////////
	/* The CLient class is designed for communication between itself and the
	server. Methods stored within this class are related to streaming data
	between the two. All GUI code is stored within a seperate class:
	ClientGUI. */
	
	////////////////////////// ATTRIBUTES ///////////////////////////
	// Server Communication
	public List<VideoFile> videoList;
	private int serverPort = 1338;
	private String serverIP = "127.0.0.1";
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	
	String streamURL = "rtp://@" + serverIP + ":" + "5555";

	// GUI 
	private ClientGUI gui;
	
	////////////////////////// CONSTRUCTOR //////////////////////////
	public Client(){
		// Set up GUI
		this.gui = new ClientGUI(900,600);
		this.gui.setStreamURL(streamURL);
		// When videofile selected, send to server
		this.gui.addOnSelectionChangedConsumer((vf) -> {writeToOutputStream(vf);});

		// Set up Client-Server communication
		setUpNetworkConnection();
		
		while(true){
			Object obj = readFromInputStream();
			if (obj instanceof List<?>){
				this.videoList = (List<VideoFile>)obj;
				// update GUI with new list data
				this.gui.updateVideoList(this.videoList);
				// Report
				System.out.println("Client:: Got " + this.videoList.size() +", videos");
			}
		}
	}
		
	
	//////////////////////////// METHODS ////////////////////////////
	
	private void setUpNetworkConnection(){
		
		System.out.println("Client:: Setting up Networking");
		
		try {
			// Attempt to connect to server socket
			this.socket = new Socket(this.serverIP, this.serverPort);
		} catch (UnknownHostException e) {
			System.out.println("Client:: Unable to create socket, UnknownHost");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Client:: Unable to create socket, IOExectpion");
			e.printStackTrace();
		}
		System.out.println("Client:: Opened Socket : "  + this.socket.toString());

		// Setup input stream
		openInputStream();
		// Setup Output Stream
		openOutputStream();
		
		System.out.println("Client:: Finsihed Networking setup");

	}
	
	private void openInputStream(){
		try {
			// Attempt creating input stream for VideoFile data
			this.input = new ObjectInputStream(this.socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void openOutputStream(){
		try {			
			// Create output stream for communication
			this.output = new ObjectOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Object readFromInputStream(){
		Object obj = null;
		try {
			obj = this.input.readObject();
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("Client:: Unable to read from input stream");
			e.printStackTrace();
		}
		return obj;
	}
	
	public void writeToOutputStream(Object obj){
		try {
			this.output.writeObject(obj);
		} catch (IOException e) {
			System.out.println("CLIENT:: Couldn't write object to stream: " + obj.toString()); 
			e.printStackTrace();
		}	
		return;
	}
	
	public static void main (String[] args){
		System.out.println("Client:: Starting");
		meme.server.Server.main(null);
		new Client();
	}
}
