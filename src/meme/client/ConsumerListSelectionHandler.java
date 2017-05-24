package meme.client;

import java.util.function.Consumer;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ConsumerListSelectionHandler implements ListSelectionListener {
	////////////////////////// DESCRIPTION //////////////////////////
	/* A list selection listener that stores selected index in an 
	integer that is used to identify the VideoFile */
	
	////////////////////////// ATTRIBUTES ///////////////////////////
	Integer selected = null; 
	Consumer<Integer> onSelected;
	
	////////////////////////// CONSTRUCTOR //////////////////////////
	public ConsumerListSelectionHandler( Consumer<Integer> onSelected){
		super();
		this.onSelected = onSelected;
	}
	
	//////////////////////////// METHODS ////////////////////////////
	@Override
	public void valueChanged(ListSelectionEvent e) {
		ListSelectionModel lsm = (ListSelectionModel)e.getSource();

       	int Index = lsm.getLeadSelectionIndex();
        
       	if (!lsm.isSelectionEmpty() && !(selected != null && Index == selected)) {
    	   selected = Index;
           onSelected.accept(selected);
        }
    }
}
