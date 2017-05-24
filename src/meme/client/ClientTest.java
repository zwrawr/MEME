package meme.client;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import meme.common.VideoFile; 

public class ClientTest {
	private Client client;
	
	@Before
	public void setUp() throws Exception {
		meme.server.Server.main(null);
		client = new Client();
	}
	
	@Test
	public void videoListNotNull(){
		// As client video list is instantiated as null, this checks that
		// it has updated successfully.
		assertTrue(client.videoList != null);
	}
	
	@Test
	public void videoListContentTest() {
		// Tests that video list has received correct data from server 
		// (confirming successful server communication)
		List<VideoFile> videoList = client.videoList;
		
		VideoFile videoFile = videoList.get(0);
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
