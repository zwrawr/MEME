package meme.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import meme.common.VideoFile;

public class VideoFileCellRenderer extends JPanel implements ListCellRenderer<VideoFile>{

	private static final long serialVersionUID = 4592886249018816517L;
	
	JLabel title;
	JLabel image;
	
	ImageIcon imageicon;

	public VideoFileCellRenderer(){
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		title = new JLabel();
		image = new JLabel();
		
		this.add(title);
		this.add(Box.createRigidArea(new Dimension(0,1)));

		this.add(image);
	}
	
	@Override
    public Dimension getMinimumSize() {
        return new Dimension(150, 100);
    }
	
    @Override
    public Dimension getPreferredSize() {
    	if (this.imageicon != null){
            return new Dimension(this.imageicon.getIconWidth()+5, this.imageicon.getIconHeight()+20);
    	}
    	
        return this.getMinimumSize();
    }

	@Override
	public Component getListCellRendererComponent(JList<? extends VideoFile> list, VideoFile vf, int index,
			boolean isSelected, boolean cellHasFocus) {
		
		setComponentOrientation(list.getComponentOrientation());

        Color bg = null;
        Color fg = null;
        
        JList.DropLocation dropLocation = list.getDropLocation();
        if (dropLocation != null
                        && !dropLocation.isInsert()
                        && dropLocation.getIndex() == index) {

            bg = UIManager.getColor("List.dropCellBackground");
            fg = UIManager.getColor("List.dropCellForeground");

            isSelected = true;
        }

        if (isSelected) {
            setBackground(bg == null ? list.getSelectionBackground() : bg);
            setForeground(fg == null ? list.getSelectionForeground() : fg);
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        
        this.title.setText(vf.getTitle());
        
        Image img = vf.getImage();
        if(img != null){
        	this.imageicon = new ImageIcon(img);
        	this.image.setIcon(this.imageicon);
        	System.out.println("Adding image for "+vf.getTitle());
        }
        
        setEnabled(list.isEnabled());

        Border border = null;
        if (cellHasFocus) {
            if (isSelected) {
                border = UIManager.getBorder("List.focusSelectedCellHighlightBorder");
            }
            if (border == null) {
                border = UIManager.getBorder("List.focusCellHighlightBorder");
            }
        } else {
            border = new EmptyBorder(1, 1, 1, 1);
        }
        
        setBorder(border);
        
		return this;
	}
}
