package meme.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)

@SuiteClasses({ 
	meme.server.ServerTest.class, meme.server.XMLReaderTest.class, meme.client.ClientTest.class
})

public class AllTests {
} 
