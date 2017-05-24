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
	
	public ClientGUI (int w, int h){
		super("Java Streaming Video Player");
		
		this.width = w;
		this.heigth = h;
		
		this.setUpCustomTheme();		
		this.setUpLookAndFeel();
		
		this.setUpVLC();
		this.SetUpGUI();
	}
	
	private void setUpLookAndFeel() {
		System.out.println("CLIENTGUI:: Setting up Look and Feel");
		
		try {
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
		HashMap<String,Color> theme = ThemeLoader.getTheme(this.themePath);
		
		if (theme == null){
			return;
		}
		
		theme.forEach((k,v)->{
			UIManager.put(k,v);
		});
	}
	
	private void setUpVLC() {
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

		// Create and add all components to main frame
		
		this.header = createHeader();
		this.footer = createFooter();
		this.video = createVideo();
		this.menu = createList();
		
		this.add(this.header, BorderLayout.NORTH);
		this.add(this.footer, BorderLayout.SOUTH);
		this.add(this.video, BorderLayout.CENTER);
		this.add(this.menu, BorderLayout.WEST);
		
        SwingUtilities.updateComponentTreeUI(this);
        this.setVisible(true);
	}

	private JPanel createList(){
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BorderLayout());

		this.videoListModel = new DefaultListModel<VideoFile>();
				
		JList<VideoFile> list = new JList<VideoFile>(this.videoListModel);
        list.setCellRenderer(new VideoFileCellRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		
		Consumer<Integer> consumer = (i) -> updateSelection(i);
		list.getSelectionModel().addListSelectionListener(new ConsumerListSelectionHandler(consumer));
		
		JScrollPane listScroll = new JScrollPane(list);		
		listPanel.add(listScroll,BorderLayout.CENTER);
		
		return listPanel;
	}
	
	private void updateSelection (Integer i){
		if (i == null || i == this.currentIndex){
			return;
		}
		System.out.println("CLIENTGUI:: A New Video Was Selected : " + i );
		this.currentIndex = i;

		
		VideoFile vf = this.videoList.get(this.currentIndex);
		
		this.IDLabel.setText("ID: " + vf.getID());
		this.TitleLabel.setText("Title: " + vf.getTitle());
		this.FileNameLabel.setText("FileName: " + vf.getFilename());

		if (onSelectionChanged != null){
			for(Consumer<VideoFile> c : onSelectionChanged){
				c.accept(vf);
			}
		}
	
		// We made changes to the GUI so we should re validate the frame.
		this.validate();
	}
	
	public void updateVideoList(List<VideoFile> vl){
		this.videoList = vl;
		
		for(VideoFile vf : this.videoList){
			this.videoListModel.addElement(vf);
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
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.LINE_AXIS));
		
		JLabel Title = new JLabel();
		Title.setText("Streaming Media Player");
		Title.putClientProperty("JComponent.sizeVariant", "large");

		JButton menuButton = new JButton();
		menuButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("meme/client/icon/menu.png")));
		menuButton.setToolTipText("Menu");
		menuButton.putClientProperty("JComponent.sizeVariant", "mini");
		
		menuButton.addActionListener((e) -> {
			this.menu.setVisible(!this.menu.isVisible());
			this.validate();
		});
		
		JPanel titlePanel = new JPanel();
		titlePanel.add(Title);
		
		headerPanel.add(menuButton,BorderLayout.WEST);
		headerPanel.add(Box.createRigidArea(new Dimension(10,0)));

		headerPanel.add(titlePanel,BorderLayout.CENTER);
		
		return headerPanel;
	}

	// Setters
	public void setStreamURL(String streamURL) {
		if (this.mediaPlayer != null){
			this.mediaPlayer.playMedia(streamURL);
		}
		else
		{
			System.out.println("CLIENTGUI:: Cannot set the streaming url before the player has been created!");
		}
	}
	
	// Adders
	public void addOnSelectionChangedConsumer(Consumer<VideoFile> c){
		if(this.onSelectionChanged == null){
			this.onSelectionChanged = new ArrayList<Consumer<VideoFile>>();
		}
		
		this.onSelectionChanged.add(c);
	}

	public void setVideoLength(Long length) {
		System.out.println("CLIENT:: media length " + mediaPlayer.getLength());
		return;
	}
}
