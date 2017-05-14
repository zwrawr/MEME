package meme.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import meme.common.VideoFile;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.test.basic.PlayerControlsPanel;

public class ClientGUI {
	
	private int width;
	private int heigth;

	// GUI Components
	private JFrame frame;
	
	private JLabel TitleLabel;
	private JLabel FileNameLabel;
	private JLabel IDLabel;
	
	DefaultListModel<String> videoListModel;
	
	// VLC
	private final String vlcLibraryPath = "..\\vlc-2.0.1";
	
	private List<VideoFile> videoList;
	private Integer currentIndex = null;
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer mediaPlayer;
	private PlayerControlsPanel controlsPanel;
	
	// Event stuff
	List<Consumer<VideoFile>> onSelectionChanged;
	
	public ClientGUI (int w, int h){
		this.width = w;
		this.heigth = h;
		
		this.setUpVLC();
		this.SetUpGUI();
	}
	
	private void setUpVLC() {
		System.out.println("Client:: Setting up VLC");
		
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), vlcLibraryPath);
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
	}
	
	private void SetUpGUI(){
		
		System.out.println("Client:: Setting up GUI");
		
		// Main Frame initialisation
		this.frame = new JFrame();
		this.frame.setSize(this.width, this.heigth);
		this.frame.setLayout(new BorderLayout());
		
		// Main Frame Listener
		this.frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				System.out.println("CLIENT :: CLOSING");
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

		if (i == null){
			return;
		}
		
		this.currentIndex = i;
		
		VideoFile vf = this.videoList.get(i);
		
		this.IDLabel.setText("ID: " + vf.getID());
		this.TitleLabel.setText("Title: " + vf.getTitle());
		this.FileNameLabel.setText("FileName: " + vf.getFilename());

		if (onSelectionChanged != null){
			for(Consumer<VideoFile> c : onSelectionChanged){
				c.accept(vf);
			}
		}
	
		// We made changes to the GUI so we should re validate the frame.
		this.frame.validate();
	}
	
	public void updateVideoList(List<VideoFile> vl){
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

	// Setters
	public void setStreamURL(String streamURL) {
		if (this.mediaPlayer != null){
			this.mediaPlayer.playMedia(streamURL);
		}
		else
		{
			System.out.println("CLIENT:: Cannot set the streaming url before the player has been created!");
		}
	}
	
	// Adders
	public void addOnSelectionChangedConsumer(Consumer<VideoFile> c){
		if(this.onSelectionChanged == null){
			this.onSelectionChanged = new ArrayList<Consumer<VideoFile>>();
		}
		
		this.onSelectionChanged.add(c);
	}
}
