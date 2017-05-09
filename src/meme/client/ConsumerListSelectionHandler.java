package meme.client;

import java.util.function.Consumer;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ConsumerListSelectionHandler implements ListSelectionListener {

	int selected = 0; 
	Consumer<Integer> onSelected;
	
	public ConsumerListSelectionHandler( Consumer<Integer> onSelected){
		super();
		this.onSelected = onSelected;
	}
	
	@Override
	 public void valueChanged(ListSelectionEvent e) {
        ListSelectionModel lsm = (ListSelectionModel)e.getSource();

        int Index = lsm.getLeadSelectionIndex();
        
        if (!lsm.isSelectionEmpty() && Index != selected) {
            //System.out.println("CLSHS:: Element : " + Index);
            selected = Index;
            onSelected.accept(selected);
        }
    }
}
