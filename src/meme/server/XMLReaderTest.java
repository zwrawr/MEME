package meme.server;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import meme.common.VideoFile;

public class XMLReaderTest {
	
	private XMLReader reader;
	private List<VideoFile> videoList;
	private final String file = "bin/videoList.xml";

	@Before
	public void setUp() throws Exception {
		this.reader = new XMLReader();
		videoList = reader.getList(file);
	}

	@Test
	public void test_CreateListOfVideos() {
		assertTrue(videoList instanceof List);
	}
	
	@Test
	public void listContainsVideoFiles(){
		assertTrue(videoList.get(0) instanceof VideoFile);
	}
	
	@Test
	public void videoFileReturnsCorrectFields(){
		VideoFile videoFile = videoList.get(0);
		assertNotNull(videoFile.getID());
		assertNotNull(videoFile.getTitle());
		assertNotNull(videoFile.getFilename());	
	}

	@Test
	public void XMLReaderFromFile(){
		VideoFile videoFile;
		
		videoFile = videoList.get(0);
		assertEquals("20120213a2", videoFile.getID());
		assertEquals("Monsters Inc.", videoFile.getTitle());
		assertEquals("monstersinc_high.mpg", videoFile.getFilename() );
		
		videoFile = videoList.get(1);
		assertEquals("20120102b7", videoFile.getID());
		assertEquals("Avengers", videoFile.getTitle());
		assertEquals("avengers-featurehp.mp4", videoFile.getFilename() );
		
		videoFile = videoList.get(2);
		assertEquals("20120102b4", videoFile.getID());
		assertEquals("Prometheus", videoFile.getTitle());
		assertEquals("prometheus-featureukFhp.mp4", videoFile.getFilename() );
	}
	
}
