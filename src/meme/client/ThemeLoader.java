package meme.client;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.util.HashMap;

public class ThemeLoader {
	
	public static HashMap<String,Color> getTheme(String path){
		
		HashMap<String,Color> theme = new HashMap<String,Color>();
		
		try {
			
			// Load theme file into a document
			// The theme file is small so we can use a DOM parser
			// rather than a SAX parser this time.
			Document doc = loadThemeFile(path);
			if (doc == null){
				return null;
			}

			// Assume there is only one theme in the file
			// This could be extended to deal with multiple themes
			Node themeNode = doc.getChildNodes().item(0);
			NodeList Colors = themeNode.getChildNodes();

			for (int j = 0; j < Colors.getLength(); j++) {
				Node c = Colors.item(j);
				
				if (c != null && c.getNodeName() != null && c.getNodeName().equals("color")){
					
					addValues(theme, c);
				}
			}
	    } 
		catch (Exception e) {
	    	System.out.println("THEMELOADER:: could not load theme!");
	    	e.printStackTrace();
	    	return null;
	    }
		
		return theme;
	}
	
	
	public static Document loadThemeFile(String path){
		// Create our DOM parser
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			System.out.println("Unable to create document builder");
			e.printStackTrace();
			return null;
		}
				
		Document doc;
		try {
			doc = dBuilder.parse(new File(path));
		} catch (SAXException | IOException e) {
			System.out.println("Unable to parse " + path);
			e.printStackTrace();
			return null;
		}

		// clean up the tree, remove unwanted whitespace
		doc.getDocumentElement().normalize();		
		return doc;
	}
	
	public static void addValues(HashMap<String,Color> dict, Node node){
		String name = node.getAttributes().getNamedItem("name").getTextContent().toString();
		String color = node.getTextContent();
		
		dict.put(name, Color.decode(color));
	}
}
