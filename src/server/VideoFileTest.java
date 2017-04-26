package server;

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
		File = new VideoFile("TestVideoFileID");
		File.setFilename("TestVideoFileFileName");
		File.setTitle("TestVideoFile");
	}

	@Test
	public void test() {

		assertTrue(File instanceof java.io.Serializable);
		
		//Should really try it and make sure the data doesn't get garbled?
		
		try {
			new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(File);
		} catch (IOException e) {
			fail("Couldn't write VideoFile to stream");
			e.printStackTrace();
		}
	}

}
