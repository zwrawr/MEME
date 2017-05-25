package meme.client;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ThemeLoaderTest {

	private final String themePath = "./bin/meme/client/theme.xml";

	private Element ele;
	private HashMap<String,Color> dict;
	
	@Before
	public void setUp() throws Exception {
		
		Document doc;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Couldn't build doc");
			return;
		}

		
		
		dict = new HashMap<String,Color>();
		
		ele = doc.createElement("color");
		ele.setAttribute("name", "test-color");
		ele.setTextContent("#012345");
		
	}

	@Test
	public void testGetTheme() {
		HashMap<String,Color> theme = ThemeLoader.getTheme(this.themePath);
		assertNotNull(theme);
		assertTrue(theme.size() >= 1);
	}

	@Test
	public void testLoadThemeFile() {
		
		Document doc = ThemeLoader.loadThemeFile(this.themePath);
		
		assertNotNull(doc);
		assertTrue(doc.getFirstChild().getNodeName() == "theme");
		
	}

	@Test
	public void testAddValues() {

		ThemeLoader.addValues(this.dict, this.ele);
		
		System.out.println(dict.get("test-color"));
		System.out.println(Color.decode("#012345"));

		assertTrue(dict.containsKey("test-color"));
		assertTrue(dict.get("test-color").equals(Color.decode("#012345")));
	
		
	}

}
