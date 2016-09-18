package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;

public class ClickWindow extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private static ClickWindow self = null;

	public static ClickWindow getInstance()
	{
		if(self == null) self = new ClickWindow();
		return self;
	}
	
	private ClickWindow()
	{
		setTitle("ClickPanel");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);

    	setLayout(new GridBagLayout());
    	GridBagConstraints g = new GridBagConstraints();
    	g.gridx = 0;
    	g.gridy = 0;
    	g.weightx = 1;
    	g.weighty = 1;
    	g.fill = GridBagConstraints.BOTH;

    	ClickPanel ip = ClickPanel.getInstance();
    	add(ip, g);
	}
}
