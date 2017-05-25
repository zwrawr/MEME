package meme.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import meme.common.VideoFile;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class ClientGUI extends JFrame{
	////////////////////////// DESCRIPTION //////////////////////////
	/* ClientGUI handles all GUI methods within the Client. */
	
	////////////////////////// ATTRIBUTES ///////////////////////////
	private static final long serialVersionUID = -2014216896640647119L;
	private int width;
	private int heigth;
	private final String themePath = "./bin/meme/client/theme.xml";
	
	// GUI Components	
	private JLabel TitleLabel;
	private JLabel FileNameLabel;
	private JLabel IDLabel;
	
	private JPanel header;
	private JPanel footer;
	private JPanel video;
	private JPanel menu;

	DefaultListModel<VideoFile> videoListModel;
	
	// VLC
	private final String vlcLibraryPath = ".\\vlc-2.0.1";
	
	private List<VideoFile> videoList;
	private Integer currentIndex = null;
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer mediaPlayer;
	private PlayerControlsPanel controlsPanel;
	
	// Event stuff
	List<Consumer<VideoFile>> onSelectionChanged;
	
	
	////////////////////////// CONSTRUCTOR //////////////////////////
	
	public ClientGUI (int w, int h){
		
		// Create the GUI JFrame and set title
		super("Java Streaming Video Player");
		
		// Set Frame Dimensions
		this.width = w;
		this.heigth = h;
		
		// Graphics
		this.setUpCustomTheme();		
		this.setUpLookAndFeel();
		
		// Set up media playing component
		this.setUpVLC();
		
		// Add all components to the User Interface
		this.SetUpGUI();
	}
	
	
	//////////////////////////// METHODS ////////////////////////////

	private void setUpLookAndFeel() {
		// Setup the Player's theme
		
		System.out.println("CLIENTGUI:: Setting up Look and Feel");

		try {
			// Search for, and loan the "Nimbus" theme
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, then just use the default look and feel
			System.out.println("CLIENTGUI:: couldn't load the nibus look and feel");
			e.printStackTrace();
		}
	}
	
	private void setUpCustomTheme(){
		// Read theme.xml file containing all custom theme parameters
		HashMap<String,Color> theme = ThemeLoader.getTheme(this.themePath);
		
		// If read unsuccessful, return.
		if (theme == null){
			return;
		}
		
		// Read each line in the theme, and instruct UIManager to set/change
		// relevant colours.
		theme.forEach((k,v)->{
			UIManager.put(k,v);
		});
	}
	
	private void setUpVLC() {
		// Set up VLC Library
		System.out.println("CLIENTGUI:: Setting up VLC");
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), vlcLibraryPath);
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
	}
	
	private void SetUpGUI(){
		
		System.out.println("CLIENTGUI:: Setting up GUI");
		
		// Main Frame initialisation
		this.setSize(this.width, this.heigth);
		this.setLayout(new BorderLayout());
		
		// Main Frame Listener
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				System.out.println("CLIENTGUI :: CLOSING");
				mediaPlayerComponent.getMediaPlayer().stop();
				mediaPlayerComponent.release();
			}
		});

		// Create and add all components to  clientGUI frame
		this.header = createHeader();
		this.footer = createFooter();
		this.video = createVideo();
		this.menu = createList();
		
		this.add(this.header, BorderLayout.NORTH);
		this.add(this.footer, BorderLayout.SOUTH);
		this.add(this.video, BorderLayout.CENTER);
		this.add(this.menu, BorderLayout.WEST);
		
        SwingUtilities.updateComponentTreeUI(this);
        this.validate();
        this.setVisible(true);
	}

	private JPanel createList(){
		// Creates the JPanel that contains the selectable list of
		// videos available to stream.
		
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BorderLayout());
		
		this.videoListModel = new DefaultListModel<VideoFile>();
		
		// Create visible list.
		JList<VideoFile> list = new JList<VideoFile>(this.videoListModel);
        list.setCellRenderer(new VideoFileCellRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		
		// Create consumer to detect and process item selection in the list.
		Consumer<Integer> consumer = (i) -> updateSelection(i);
		list.getSelectionModel().addListSelectionListener(new ConsumerListSelectionHandler(consumer));
		
		// Make the list scroll when it has a lot of items in it
		JScrollPane listScroll = new JScrollPane(list);	
		listScroll.setPreferredSize(new Dimension(162,162));
		
		// Add list to panel
		listPanel.add(listScroll,BorderLayout.CENTER);

		// Return panel
		return listPanel;
	}
	
	private void updateSelection (Integer i){
		
		// Check selected item isn't already playing (or is null)
		if (i != null && i != this.currentIndex){
			// Report selection change
			System.out.println("CLIENTGUI:: A New Video Was Selected : " + i );
			this.currentIndex = i;

			// Get the ith item in the list, and update labels in GUI
			VideoFile vf = this.videoList.get(this.currentIndex);
			
			this.IDLabel.setText("ID: " + vf.getID());
			this.TitleLabel.setText("Title: " + vf.getTitle());
			this.FileNameLabel.setText("FileName: " + vf.getFilename());

			if (onSelectionChanged != null){
				for(Consumer<VideoFile> c : onSelectionChanged){
					c.accept(vf);
				}
			}
			// Made changes to the GUI, so re-validate the frame.
			this.validate();
		}
	}
	
	public void updateVideoList(List<VideoFile> vl){
		this.videoList = vl;
		for(VideoFile vf : this.videoList){
			this.videoListModel.addElement(vf);
		}
	}
	
	private JPanel createVideo(){
		// Create panel containing video
		JPanel videoPanel = new JPanel();
		
		// Create media player for videoPanel
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		mediaPlayer = mediaPlayerComponent.getMediaPlayer();
		
		// Create controls panel for media player
		controlsPanel = new PlayerControlsPanel(mediaPlayer);
		
		// Add components
		videoPanel.setLayout(new BorderLayout());
		videoPanel.add(mediaPlayerComponent, BorderLayout.CENTER);
		videoPanel.add(controlsPanel, BorderLayout.SOUTH);
		
		return videoPanel;
		
	}
	
	private JPanel createFooter(){
		// Creates panel containing all information on currently playing video
		
		JPanel footerPanel = new JPanel();
		footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.PAGE_AXIS));
		
		this.IDLabel = new JLabel("ID: ");
		this.TitleLabel = new JLabel("Title: ");
		this.FileNameLabel = new JLabel("Filename: "); 
		
		footerPanel.add(this.TitleLabel);
		footerPanel.add(this.FileNameLabel);
		footerPanel.add(this.IDLabel);
		
		return footerPanel;
	}
	
	private JPanel createHeader(){
		
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.LINE_AXIS));
		
		// Create title panel
		JPanel titlePanel = new JPanel();
		JLabel Title = new JLabel();
		Title.setText("Streaming Media Player");
		Title.putClientProperty("JComponent.sizeVariant", "large");
		titlePanel.add(Title);
		
		// Menu toggle button
		JButton menuButton = new JButton();
		menuButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("meme/client/icon/menu.png")));
		menuButton.setToolTipText("Menu");
		menuButton.putClientProperty("JComponent.sizeVariant", "mini");
		// at each click of menuButton, toggle visibility of menu
		menuButton.addActionListener((e) -> {
			this.menu.setVisible(!this.menu.isVisible());
			this.validate();
		});
				
		headerPanel.add(menuButton,BorderLayout.WEST);
		headerPanel.add(Box.createRigidArea(new Dimension(10,0))); // Spacer
		headerPanel.add(titlePanel,BorderLayout.CENTER);
		
		return headerPanel;
	}
		
	public void addOnSelectionChangedConsumer(Consumer<VideoFile> c){
		if(this.onSelectionChanged == null){
			this.onSelectionChanged = new ArrayList<Consumer<VideoFile>>();
		}
		this.onSelectionChanged.add(c);
	}
		
	public void setStreamURL(String streamURL) {
		if (this.mediaPlayer != null){
			this.mediaPlayer.playMedia(streamURL);
		}
		else {
			System.out.println("CLIENTGUI:: Cannot set the streaming url before the player has been created!");
		}
	}


}
