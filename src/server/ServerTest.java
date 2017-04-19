package server;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class ServerTest {
	
	private Server server;
	
	@Before
	public void setup(){
		server = new Server();
	}
	
	
	@Test
	public void serverTest(){
		// assume the server is working if it's returning a list with the correct number of videofiles
		assertTrue(server.getList().size() == 3 );
	}

}
