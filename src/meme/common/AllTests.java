package meme.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)

@SuiteClasses({ 
	//meme.server.ServerTest.class,
	meme.server.XMLReaderTest.class,
	//meme.client.ClientTest.class,
	meme.client.ConsumerListSelectionHandlerTest.class,
	meme.client.ThemeLoaderTest.class,
	meme.client.VideoFileCellRendererTest.class
})

public class AllTests {
} 
