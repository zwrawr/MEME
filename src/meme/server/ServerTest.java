package meme.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import meme.common.VideoFile;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;


public class ServerTest {
		
	private Thread thread;
	
	private int serverPort = 1338;
	private String serverIP = "127.0.0.1";
	
	private Server server;
	
	@Before
	public void setup(){
		
		System.out.println("TEST:: Starting Server");
		server = new Server();
	}
	
	@After
	public void cleanup(){
		this.server.Stop();
	}
	
	@Test
	public void socketEstablished(){
		// Test
		//127.0.0.1:1134
		
		Socket serverSocket;
		ObjectInputStream inputFromServer;
		List<VideoFile> videoList = null;

		try {
			serverSocket = new Socket("127.0.0.1", server.getPort());
			inputFromServer = new ObjectInputStream(serverSocket.getInputStream());
			videoList = (List<VideoFile>) inputFromServer.readObject();
			
		}catch (UnknownHostException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		// check to make sure we got some videoFile from the connection
		if (videoList != null){
			assertTrue(videoList instanceof List<?>);
			assertTrue(videoList.size() > 0);
			assertTrue(videoList.get(0) instanceof VideoFile);
		}
		else{
			fail("videoList was null");
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
