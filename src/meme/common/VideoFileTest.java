package meme.common;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.junit.Before;
import org.junit.Test;

public class VideoFileTest {
	
	VideoFile File;
	@Before
	public void setUp() throws Exception {
		// Create mock video file
		File = new VideoFile("TestVideoFileID");
		File.setFilename("TestVideoFileFileName");
		File.setTitle("TestVideoFile");
	}

	@Test
	public void testGetters(){
	}
	
	@Test
	public void isWriteableToStream() {
		// Must be serializeable to write
		assertTrue(File instanceof java.io.Serializable);
		
		try {
			// Try writing to a stream
			new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(File);
		} catch (IOException e) {
			fail("Couldn't write VideoFile to stream");
			e.printStackTrace();
		}
	}

}
