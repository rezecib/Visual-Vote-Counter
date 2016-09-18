package control;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import model.Company;
import model.Model;
import model.Photo;
import view.ControlWindow;
import view.ImagePanel;

public class ImageListener implements Runnable
{
	public static final String FOLDER = "img/colorpatch/";
	public boolean run = false;
	private ArrayList<String> queue;
	private Set<String> filenames;

	public void run()
	{
		queue = new ArrayList<String>();
		filenames = new HashSet<String>();
		run = true;
		while(run)
		{
			checkForFiles();
			try { Thread.sleep(1000); }
			catch(Exception e) {}
		}
	}
	
	public static BufferedImage loadImage(String filename)
	{
		BufferedImage img = null;
		try { img = ImageIO.read(new File(FOLDER + filename)); }
		catch(IOException e) { System.out.println(e.getStackTrace()); }
		return img;
	}
	
	public void checkForFiles()
	{
		ArrayList<String> q = new ArrayList<String>();
		File img = new File(FOLDER);
		File[] images = img.listFiles();
		for(File image : images)
		{
			if(!image.getName().toLowerCase().matches(".*\\.jpg")) continue;
			if(!filenames.contains(image.getName()))
			{
				if(queue.contains(image.getName()))
				{
					BufferedImage big = loadImage(image.getName());
					ImagePanel ip = ImagePanel.getInstance();
					Image small = big.getScaledInstance(ip.getWidth(), ip.getHeight(), BufferedImage.SCALE_SMOOTH);
					Photo p = new Photo(small, image.getName(), big.getWidth(), big.getHeight());
					p.company = Company.UNCATEGORIZED;
					Model.companies.get(Model.uncategorized).add(p);
					ControlWindow.getInstance().repaint();
					filenames.add(image.getName());
				}
				else
					q.add(image.getName());
			}
		}
		queue = q;
		
		int free = (int)(Runtime.getRuntime().freeMemory()/((long)(1024*1024)));
		int total = (int)(Runtime.getRuntime().totalMemory()/((long)(1024*1024)));
		double percent = ((free*1.0)/(total*1.0))*100.0;
		String percentStr = "" + percent;
		percentStr = percentStr.substring(0, Math.min(percentStr.length(), 5));
		ControlWindow.getInstance().setTitle(free + "Mb / " + total + "Mb\t (" + percentStr + "%)");
		System.gc();
	}
}
