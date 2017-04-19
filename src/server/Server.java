package server;

import java.util.List;

public class Server {
	private List<VideoFile> videos;
	private String filename = "bin/videoList.xml";
	
	public Server () {
		XMLReader reader = new XMLReader();
		videos = reader.getList(filename);
	}
	
	public List<VideoFile> getList(){
		return this.videos;
	}

}

