package meme.common;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

public class VideoFile implements Serializable{  

	private static final long serialVersionUID = 7030558376409956459L;
	
	private String id;
	private String title = "title";
	private String filename = "filename";
	private String imagename = "imagename";
	private transient Image img = null; //has to be transient so that we can use custom serialization
	
	public VideoFile(String id) {
		this.id = id;
	}
	
	public void setTitle (String newTitle){
		this.title = newTitle;
	}
	
	public void setFilename (String newFilename){
		this.filename = newFilename;
	}

	public void setImagename (String newImagename){
		this.imagename = newImagename;
	}
	
	public void loadImage (){
		
		if (this.img != null)
		{
			return;
		}
		
		try {
			this.img = ImageIO.read(new File(this.imagename));
		} catch (IOException e) {
			System.out.println("VIDEOFILE: Cannot load image at "+this.imagename);
			e.printStackTrace();
		}
		System.out.println("VIDEOFILE: loaded image at "+this.imagename);
	}
	
	public void unloadImage (){
		this.img = null;
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

	public Image getImage(){
		return this.img;
	}
	
	@Override
	public String toString() {
		return "VideoFile [id=" + id + ", title=" + title + ", filename=" + filename + "]";
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        
        if (this.img != null){
        	out.writeBoolean(true);
        	ImageIO.write((RenderedImage) this.img, "png", out);
        }
    	out.writeBoolean(false);

    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        
        if (in.readBoolean()==true){
        	this.img = ImageIO.read(in);
        }
    }
}
