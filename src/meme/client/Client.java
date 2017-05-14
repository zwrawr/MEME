package meme.client;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;

import com.sun.jna.*;

import meme.common.VideoFile;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.test.basic.PlayerControlsPanel;

public class Client {
	
	// Server Comm.
	public List<VideoFile> videoList;
	private int serverPort = 1338;
	private String serverIP = "127.0.0.1";
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	
	String streamURL = "rtp://@" + serverIP + ":" + "5555";

	//GUI
	private ClientGUI gui;

	public Client(){
		this.gui = new ClientGUI(600,400);
		this.gui.setStreamURL(streamURL);
		
		this.gui.addOnSelectionChangedConsumer((vf) -> {updateServerStreaming(vf);});
		
		setUpNetworkConnection();
	}
		
	private void setUpNetworkConnection(){
		
		System.out.println("Client:: Setting up Networking");
		
		try {
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

	}
	
	private void openInputStream(){
		try {
			this.input = new ObjectInputStream(this.socket.getInputStream());
			this.videoList = (List<VideoFile>)this.input.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		this.gui.updateVideoList(this.videoList);
		System.out.println("Client:: Got " + this.videoList.size() +", videos");
	}
	
	private void openOutputStream(){
		try {
			this.output = new ObjectOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Client:: Finsihed Networking setup");
	}
	
	public void updateServerStreaming(VideoFile vf){
		try {
			this.output.writeObject(vf);
		} catch (IOException e) {
			System.out.println("CLIENT:: Couldn't tell the server to change what it was streaming."); 
			e.printStackTrace();
		}
		System.out.println("CLIENT:: Told server to change stream"); 

	}
	
	public static void main (String[] args){
		System.out.println("Client:: Starting");

		meme.server.Server.main(null);
		Client client = new Client();
	}
}
