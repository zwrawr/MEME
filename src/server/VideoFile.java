package server;

import java.io.Serializable;

public class VideoFile implements Serializable{  

	private static final long serialVersionUID = 7030558376409956459L;
	
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

	public String getID() {
		return this.id;
	}

	public String getTitle() {
		return this.title;
	}

	public String getFilename() {
		return this.filename;
	}

	@Override
	public String toString() {
		return "VideoFile [id=" + id + ", title=" + title + ", filename=" + filename + "]";
	}

	
}
