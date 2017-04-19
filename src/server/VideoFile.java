package server;

public class VideoFile {

	private String id;
	private String title = "title";
	private String filename = "filename";
	
	public VideoFile(String id) {
		this.id = id;
	}
	
	public void setTitle (String newTitle){
		this.title = newTitle;
	}
	
	public void setFilename (String newFilename){
		this.filename = newFilename;
	}

	public Object getID() {
		return this.id;
	}

	public Object getTitle() {
		return this.title;
	}

	public Object getFilename() {
		return this.filename;
	}

}
