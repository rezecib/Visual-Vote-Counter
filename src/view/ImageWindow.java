package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;

public class ImageWindow extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private static ImageWindow self = null;

	public static ImageWindow getInstance()
	{
		if(self == null) self = new ImageWindow();
		return self;
	}
	
	protected ImageWindow()
	{
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);

    	setLayout(new GridBagLayout());
    	GridBagConstraints g = new GridBagConstraints();
    	g.gridx = 0;
    	g.gridy = 0;
    	g.weightx = 1;
    	g.weighty = 1;
    	g.fill = GridBagConstraints.BOTH;

    	ImagePanel ip = ImagePanel.getInstance();
    	add(ip, g);
	}
	
	/** Overrides to synchronize with the model before painting */
	public void repaint()
	{
		super.repaint();
		ClickWindow.getInstance().repaint();
	}
}
