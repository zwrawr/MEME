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
	
	
	// GUI Components
	private JFrame frame;
	private final int width = 800, height = 600;
	
	private JLabel TitleLabel;
	private JLabel FileNameLabel;
	private JLabel IDLabel;
	
	DefaultListModel<String> videoListModel;
	
	// VLC
	
	private String vlcLibraryPath = "..\\vlc-2.0.1";
	private VideoFile currentVideoFile = null;
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer mediaPlayer;
	
	private PlayerControlsPanel controlsPanel;
	
	public Client(){
		setUpVLC();
		SetUpGUI();
		setUpNetworkConnection();
	}
	
	/*----------------- Setup Methods -------------------*/
	
	
	private void setUpVLC() {
		System.out.println("Client:: Setting up VLC");

		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), vlcLibraryPath);
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		
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
			this.updateVideoList((List<VideoFile>)this.input.readObject());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
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

	private void SetUpGUI(){
		
		System.out.println("Client:: Setting up GUI");
		
		// Main Frame initialisation
		this.frame = new JFrame();
		this.frame.setSize(this.width, this.height);
		this.frame.setLayout(new BorderLayout());
		
		// Main Frame Listener
		this.frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				mediaPlayerComponent.release();
			}
		});

		// Create and add all components to main frame
		this.frame.add(createHeader(), BorderLayout.NORTH);
		this.frame.add(createFooter(), BorderLayout.SOUTH);
		this.frame.add(createVideo(), BorderLayout.CENTER);
		this.frame.add(createList(), BorderLayout.WEST);
		
		// Display on screen
		this.frame.setVisible(true);
		
	}

	/* ---------------------- GUI Methods -----------------------*/
	
	private JPanel createList(){
		JPanel listPanel = new JPanel();
		
		this.videoListModel = new DefaultListModel<String>();
				
		JList<String> list = new JList<String>(this.videoListModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		
		Consumer<Integer> consumer = (i) -> updateSelection(i);
		list.getSelectionModel().addListSelectionListener(new ConsumerListSelectionHandler(consumer));
		
		listPanel.add(list);
		
		return listPanel;
	}
	
	private void updateSelection (Integer i){
		System.out.println("Client:: A New Video Was Selected : " + i );
		
		VideoFile vf = this.videoList.get(i);
		
		this.IDLabel.setText("ID: " + vf.getID());
		this.TitleLabel.setText("Title: " + vf.getTitle());
		this.FileNameLabel.setText("FileName: " + vf.getFilename());

		this.currentVideoFile = vf;
		try {
			this.output.writeObject(vf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String stream = "rtp://@" + serverIP + ":" + "5555";
		mediaPlayer.playMedia(stream);
		
		//String media = currentFileName;
		//this.mediaPlayer.playMedia(media);
		this.frame.validate();
	}
	
	private void updateVideoList(List<VideoFile> vl){
		this.videoList = vl;
		
		for(VideoFile vf : this.videoList){
			this.videoListModel.addElement(vf.getTitle());
			System.out.println(vf);
		}
	}
	
	private JPanel createVideo(){
		
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		mediaPlayer = mediaPlayerComponent.getMediaPlayer();
		controlsPanel = new PlayerControlsPanel(mediaPlayer);
		
		JPanel videoPanel = new JPanel();
		videoPanel.setLayout(new BorderLayout());
		videoPanel.add(mediaPlayerComponent, BorderLayout.CENTER);
		videoPanel.add(controlsPanel, BorderLayout.SOUTH);
		
		return videoPanel;
		
	}
	
	private JPanel createFooter(){
		JPanel footerPanel = new JPanel();
		footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.PAGE_AXIS));
		
		this.IDLabel = new JLabel();
		this.TitleLabel = new JLabel();
		this.FileNameLabel = new JLabel(); 
		
		// File data display
		this.IDLabel.setText("ID: ");
		this.TitleLabel.setText("Title: ");
		this.FileNameLabel.setText("FileName: ");
		
		footerPanel.add(this.TitleLabel);
		footerPanel.add(this.FileNameLabel);
		footerPanel.add(this.IDLabel);
		
		return footerPanel;
	}
	
	private JPanel createHeader(){
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new FlowLayout());
		
		JLabel Title = new JLabel();
		Title.setText("Player");
		
		headerPanel.add(Title);
		return headerPanel;
	}
	
	
	
	public static void main (String[] args){
		System.out.println("Client:: Starting");

		meme.server.Server.main(null);
		Client client = new Client();
	}
}
