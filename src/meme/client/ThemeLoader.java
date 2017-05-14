package meme.client;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

import java.awt.Color;
import java.util.HashMap;

public class ThemeLoader {
	
	public static HashMap<String,Color> getTheme(String path){
		
		HashMap<String,Color> theme = new HashMap<String,Color>();
		
		try {

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(path));

			// clean up the tree
			doc.getDocumentElement().normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			// Assume there is only one theme in the file
			Node themeNode = doc.getChildNodes().item(0);
			
			NodeList ColorGroups = themeNode.getChildNodes();

			for (int i = 0; i < ColorGroups.getLength(); i++) {
				Node cg = ColorGroups.item(i);
				
				if (cg.getNodeName().equals("colorgroup")){
					NodeList Colors = cg.getChildNodes();
	
					for (int j = 0; j < Colors.getLength(); j++) {
						Node c = Colors.item(j);
						
						if (c != null && c.getNodeName() != null && c.getNodeName().equals("color")){
							
							String name = c.getAttributes().getNamedItem("name").getTextContent().toString();
							String color = c.getTextContent();
							
							System.out.println("name: " + name);
							System.out.println("color: " + color);
							
							theme.put(name, Color.decode(color));
						}

					}
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
}
