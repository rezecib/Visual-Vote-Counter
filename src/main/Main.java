package main;

import control.ImageListener;
import model.Model;
import view.ClickWindow;
import view.ControlWindow;
import view.ImageWindow;

public abstract class Main
{
	public static void main(String[] args)
	{
		System.setProperty("awt.useSystemAAFontSettings","on");
		System.setProperty("swing.aatext", "true");
		Model.initialize();
		ImageWindow.getInstance().setVisible(true);
		ClickWindow.getInstance().setVisible(true);
		ControlWindow.getInstance().setVisible(true);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ControlWindow.fixCompanyHeights();
    	new Thread(new ImageListener()).start();	
	}

}
