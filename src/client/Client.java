package client;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;

import server.VideoFile;

public class Client {
	
	public List<VideoFile> videoList;
	private int serverPort = 1138;
	private String serverIP = "127.0.0.1";
	private Socket socket;
	private ObjectInputStream input;
	private JFrame frame;

	private final int width = 800, height = 600;
	
	private JLabel TitleLabel;
	private JLabel FileNameLabel;
	private JLabel IDLabel;

	public Client(){
		setUpNetworkConnection();
	}
	
	private void SetUpGUI(){
		this.frame = new JFrame();
		this.frame.setSize(this.width, this.height);
		this.frame.setLayout(new BorderLayout());
		

		// Add all components to main frame
		this.frame.add(createHeader(), BorderLayout.NORTH);
		this.frame.add(createFooter(), BorderLayout.SOUTH);
		this.frame.add(createVideo(), BorderLayout.CENTER);
		this.frame.add(createList(), BorderLayout.WEST);
		
		this.frame.setVisible(true);
		
	}
	
	private JPanel createList(){
		JPanel listPanel = new JPanel();
		
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		
		for(VideoFile vf : this.videoList){
			listModel.addElement(vf.getTitle());
			System.out.println(vf);
		}
		
		JList<String> list = new JList<String>(listModel);
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

		this.frame.validate();
	}
	
	private JPanel createVideo(){
		return new JPanel();
	}
	
	private JPanel createFooter(){
		JPanel footerPanel = new JPanel();
		footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.PAGE_AXIS));
				
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
		Title.setText("Fuck you");
		
		headerPanel.add(Title);
		return headerPanel;
	}
	
	private void setUpNetworkConnection(){
		try {
			this.socket = new Socket(this.serverIP, this.serverPort);
		} catch (UnknownHostException e) {
			System.out.println("Unable to create socket, UnknownHost");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Unable to create socket, IOExectpion");
			e.printStackTrace();
		}
		
		try {
			this.input = new ObjectInputStream(this.socket.getInputStream());
			this.videoList = (List<VideoFile>)this.input.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main (String[] args){
		server.Server.main(null);
		Client client = new Client();
		client.SetUpGUI();
	}
}
