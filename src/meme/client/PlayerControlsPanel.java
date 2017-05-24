package meme.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.binding.LibVlcConst;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class PlayerControlsPanel extends JPanel {
	
	////////////////////////// DESCRIPTION //////////////////////////
	/*
	 * This class covers the controls panel at the base of the player
	 * This file is based of a sample implimentaion provided in the jvlc
	 * This version contains our own modifications to the class.
	 */
	
	private static final long serialVersionUID = 965571066165214265L;
	
    private final EmbeddedMediaPlayer mediaPlayer;

    private JLabel timeLabel;
    private JSlider positionSlider;

    private JButton stopButton;
    private JButton pauseButton;
    private JButton playButton;

    private JButton toggleMuteButton;
    private JSlider volumeSlider;

    private JButton captureButton;
    private JButton fullScreenButton;
    
    private boolean mousePressedPlaying = false;

	private ImageIcon muteSoundIcon;
	private ImageIcon SoundIcon;
	
	
	////////////////////////// CONSTRUCTOR //////////////////////////
	
    public PlayerControlsPanel(EmbeddedMediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;        
        createUI();
    }

    
	//////////////////////////// METHODS ////////////////////////////
    
    private void createUI() {
        createControls();
        layoutControls();        
        registerListeners();
    }

    private void createControls() {
    	
        timeLabel = new JLabel("hh:mm:ss");

        stopButton = new JButton();
        stopButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("meme/client/icon/control_stop.png")));
        stopButton.setToolTipText("Stop");
        stopButton.putClientProperty("JComponent.sizeVariant", "mini");

        pauseButton = new JButton();
        pauseButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("meme/client/icon/control_pause.png")));
        pauseButton.setToolTipText("Play/pause");
        pauseButton.putClientProperty("JComponent.sizeVariant", "mini");

        playButton = new JButton();
        playButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("meme/client/icon/control_play.png")));
        playButton.setToolTipText("Play");
        playButton.putClientProperty("JComponent.sizeVariant", "mini");


        muteSoundIcon = new ImageIcon(getClass().getClassLoader().getResource("meme/client/icon/sound_mute.png"));
        SoundIcon = new ImageIcon(getClass().getClassLoader().getResource("meme/client/icon/sound.png"));
        toggleMuteButton = new JButton();
        toggleMuteButton.setIcon(SoundIcon);
        toggleMuteButton.setToolTipText("Toggle Mute");
        toggleMuteButton.putClientProperty("JComponent.sizeVariant", "mini");

        volumeSlider = new JSlider();
        volumeSlider.setOrientation(JSlider.HORIZONTAL);
        volumeSlider.setMinimum(LibVlcConst.MIN_VOLUME);
        volumeSlider.setMaximum(LibVlcConst.MAX_VOLUME);
        volumeSlider.setPreferredSize(new Dimension(100, 40));
        volumeSlider.setToolTipText("Change volume");

        captureButton = new JButton();
        captureButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("meme/client/icon/camera.png")));
        captureButton.setToolTipText("Take picture");
        captureButton.putClientProperty("JComponent.sizeVariant", "mini");

        /* Not yet used in this release: */
        positionSlider = new JSlider();
        positionSlider.setMinimum(0);
        positionSlider.setMaximum(1000);
        positionSlider.setValue(0);
        positionSlider.setToolTipText("Position");

        fullScreenButton = new JButton();
        fullScreenButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("meme/client/icon/fullscreen.png")));
        fullScreenButton.setToolTipText("Toggle full-screen");
        fullScreenButton.putClientProperty("JComponent.sizeVariant", "mini");
    }

    private void layoutControls() {
        setBorder(new EmptyBorder(4, 4, 4, 4));
        setLayout(new BorderLayout());

        JPanel positionPanel = new JPanel();
        positionPanel.setLayout(new GridLayout(1, 1));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(8, 0));

        topPanel.add(timeLabel, BorderLayout.WEST);
        topPanel.add(positionPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel();

        bottomPanel.setLayout(new FlowLayout());

        bottomPanel.add(stopButton);
        bottomPanel.add(pauseButton);
        bottomPanel.add(playButton);
        bottomPanel.add(volumeSlider);
        bottomPanel.add(toggleMuteButton);
        bottomPanel.add(captureButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /*
     * Broken out position setting, handles updating mediaPlayer
     */
    private void setSliderBasedPosition() {
        if(!mediaPlayer.isSeekable()) {
            return;
        }
        float positionValue = positionSlider.getValue() / 1000.0f;
        // Avoid end of file freeze-up
        if(positionValue > 0.99f) {
            positionValue = 0.99f;
        }
        mediaPlayer.setPosition(positionValue);
    }

    private void updateUIState() {
        if(!mediaPlayer.isPlaying()) {
            // Resume play or play a few frames then pause to show current position in video
            mediaPlayer.play();
 
            if(!mousePressedPlaying) {
                try {
                    // Half a second probably gets an iframe
                    Thread.sleep(500);
                }
                catch(InterruptedException e) {
                    // Don't care if unblocked early
                }
                mediaPlayer.pause();
            }
        }
    }

    private void registerListeners() {

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.stop();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.pause();
            }
        });

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.play();
            }
        });

        toggleMuteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.mute();
                toggleMuteButton.setIcon(mediaPlayer.isMute()?muteSoundIcon:SoundIcon);
            }
        });

        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                mediaPlayer.setVolume(source.getValue());
            }
        });

        captureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.saveSnapshot();
            }
        });
        
        /* NOT AVAILABLE IN THIS RELEASE
        positionSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(mediaPlayer.isPlaying()) {
                    mousePressedPlaying = true;
                    mediaPlayer.pause();
                }
                else {
                    mousePressedPlaying = false;
                }
                setSliderBasedPosition();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setSliderBasedPosition();
                updateUIState();
            }
        });
		 */
    	
    }
}