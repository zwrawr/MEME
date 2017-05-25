package meme.client;

import static org.junit.Assert.*;

import java.awt.Component;

import javax.swing.JList;

import org.junit.Before;
import org.junit.Test;

import meme.common.VideoFile;

public class VideoFileCellRendererTest {

	
	VideoFileCellRenderer vfcr;
	
	@Before
	public void setUp() throws Exception {
		this.vfcr = new VideoFileCellRenderer();
	}

	@Test
	public void testGetMinimumSize() {
		
		double width = vfcr.getMinimumSize().getWidth();
		double height = vfcr.getMinimumSize().getHeight();
		
		//Test to make sure the minimum width and height are within some reasonable constraints
		assertTrue(width >= 100 && width <= 500);
		assertTrue(height >= 100 && height <= 500);

	}

	@Test
	public void testGetPreferredSize() {
		double width = vfcr.getPreferredSize().getWidth();
		double height = vfcr.getPreferredSize().getHeight();
		
		//Test to make sure the preffered width and height are within some reasonable constraints
		assertTrue(width >= 100 && width <= 500);
		assertTrue(height >= 100 && height <= 500);
	}

	@Test
	public void testGetListCellRendererComponent() {
		VideoFile vf = new VideoFile("0000");
		vf.setTitle("Test");
		
		JList<VideoFile> list = new JList<VideoFile>();
		
		Component comp = vfcr.getListCellRendererComponent(list, vf, 0, false, false);
		
		// Make sure it renders something
		assertNotNull(comp);
		
		// I don't know of any way to properly check to see what it renders
		// Should find out if thats possible
		
	}

}
