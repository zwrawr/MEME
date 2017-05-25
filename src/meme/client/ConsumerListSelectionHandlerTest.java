package meme.client;

import static org.junit.Assert.*;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import org.junit.Before;
import org.junit.Test;

public class ConsumerListSelectionHandlerTest {

	boolean didCallHandler = false;
	Integer valuePassed = null;

	int selected = 1;
	
	@Before
	public void setUp() throws Exception {
		didCallHandler = false;
	}

	@Test
	public void test() {
		
		// set up test list
		DefaultListModel<String> model = new DefaultListModel<String>();
		model.addElement("test0");
		model.addElement("test1");
		model.addElement("test2");
		JList<String> list = new JList<String>(model); 
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// add out handler
		ConsumerListSelectionHandler handler = new ConsumerListSelectionHandler((i) -> {testCallback(i);});
		list.getSelectionModel().addListSelectionListener(handler);

		// create event
		list.setSelectedIndex(this.selected);
		
		// check that event ran
		assertTrue(didCallHandler);
		assertTrue(valuePassed == selected);
		
	}
	
	// Simple test method that sets a fkag to say it was called
	public void testCallback(int i){
		this.didCallHandler = true;
		this.valuePassed = i;
	}

}
