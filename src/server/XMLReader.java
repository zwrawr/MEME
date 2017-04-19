package server;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class XMLReader extends DefaultHandler{

	private SAXParser saxParser;
	private List<VideoFile> videoList; 
	
	private enum elementType{
		VIDEOLIST,
		VIDEO,
		FILENAME,
		TITLE,
		OTHER
	}
	
	private Stack<elementType> elements;
	
	public XMLReader() {
		
		// Setting up the SAX parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try{
			saxParser = factory.newSAXParser();
		}
		catch (ParserConfigurationException pce){
			pce.printStackTrace();
		}
		catch(SAXException saxe){
			saxe.printStackTrace();
		}
	}

	public List<VideoFile> getList(String filename) {
		// setup for this file
		this.videoList = new ArrayList<VideoFile>();
		this.elements = new Stack<elementType>();
		this.elements.push(elementType.VIDEOLIST);
		
		// start parsing the file
		try {
			saxParser.parse(filename, this);
		} 
		catch (SAXException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return videoList;
	}

	@Override
	public void startElement(String uri, String localName, String qName, 
		Attributes attrs) throws SAXException {
		
		// figure out what element this is
		String elementName = localName;
		if ("".equals(elementName)) {
			elementName = qName;
		}
		
		switch (elementName){
			case "videoList":
				this.elements.push(elementType.VIDEOLIST);
				break;
				
			case "video":
				this.elements.push(elementType.VIDEO);
				
				// This is a new video element so create a videofile object
				this.videoList.add(new VideoFile(attrs.getValue("id")));
				break;
				
			case "title":
				this.elements.push(elementType.TITLE);
				break;
				
			case "filename":
				this.elements.push(elementType.FILENAME);
				break;
				
			default:
				this.elements.push(elementType.OTHER);
				break;
		}
		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) 
			throws SAXException{
		// Leaving the current element so pop it from the stack
		this.elements.pop();
	}
	
	@Override
	public void characters(char ch[], int start, int length) throws SAXException{
				
		VideoFile vf;
		switch (this.elements.lastElement()){
			case TITLE:
				vf = this.videoList.get(videoList.size()-1);
				char[] title = Arrays.copyOfRange(ch,start,start+length);
				vf.setTitle( new String(title));
				break;
				
			case FILENAME:
				vf = this.videoList.get(videoList.size()-1);
				char[] filename = Arrays.copyOfRange(ch,start,start+length);
				vf.setFilename( new String(filename));
				break;
				
			default:
				break;
		}
	}
	
	@Override
	public void startDocument() throws SAXException{
		
	}
	
	@Override
	public void endDocument() throws SAXException{
		
	}
}
