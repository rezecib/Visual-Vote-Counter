package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.Company;
import model.Model;
import model.Photo;
import model.Point;
import view.ClickPanel;
import view.ControlWindow;
import view.ImagePanel;
import view.ImageWindow;

public class Controller implements ActionListener, ChangeListener, ListSelectionListener, MouseListener
{
	private static Controller self = null;
	
	//ActionCommands for buttons
	public static final String COPY = "Copy";
	public static final String PASTE = "Paste";
	public static final String DETECT = "Detect";
	public static final String SWITCH = "Switch";
	
	//ActionCommands for sliders
	public static final String THRESHOLD = "Threshold";
	public static final String CLUSTER = "Cluster";
	
	//ActionCommands for list selectors
	public static final String COMPANY = "Company";
	public static final String PHOTO = "Photo";

	public static Controller getInstance()
	{
		if(self == null) self = new Controller();
		return self;
	}
	
	public static void button(String actionCommand)
	{
		if(actionCommand == COPY)
		{
			Model.copyThreshold = ControlWindow.getThreshold();
			Model.copyCluster = ControlWindow.getCluster();
			ControlWindow.getInstance().repaint();
		}
		else if(actionCommand == PASTE)
		{
			if(Model.copyThreshold == null || Model.copyCluster == null) return;
			Model.setThreshold(Model.copyThreshold);
			Model.setCluster(Model.copyCluster);
			ControlWindow.getInstance().repaint();
		}
		else if(actionCommand == DETECT && Model.currentPhoto != null)
		{
			Model.currentPhoto.threshold = Model.threshold;
			Model.currentPhoto.cluster = Model.cluster;
			new Thread(Model.currentPhoto).start();
		}
		else if(actionCommand == SWITCH)
		{
			Model.showCorners = !Model.showCorners;
			ImagePanel.getInstance().repaint();
		}
		else if(actionCommand.length() == 1)
		{
			if(Model.currentPhoto == null) return;
			Company c = ControlWindow.getCompany(Integer.parseInt(actionCommand));
			if(Model.currentCompany == c) return;
			Model.companies.get(Model.currentCompany).remove(Model.currentPhoto);
			Model.companies.get(c).add(Model.currentPhoto);
			Model.currentPhoto.company = c.toString();
			Model.currentCompany = c;
			ImagePanel.getInstance().repaint();
			ControlWindow.getInstance().repaint();
		}
	}
	
	public static void slider(JSlider slider, String actionCommand)
	{
		int value = slider.getValue();
		JFormattedTextField field = ControlWindow.getField(slider);
		field.setText("" + value);
		field.setValue(value);
		adjustValue(value, actionCommand);
	}
	
	public static void field(JFormattedTextField field, String actionCommand)
	{
		getInstance();
		int value = (Integer)field.getValue();
		JSlider slider = ControlWindow.getSlider(field);
		slider.setValue(value);
		adjustValue(value, actionCommand);
	}
	
	private static void adjustValue(int value, String actionCommand)
	{
		if(actionCommand == THRESHOLD)
		{
			Model.threshold = value;
			if(Model.currentPhoto != null)
				Model.currentPhoto.threshold = value;
		}
		if(actionCommand == CLUSTER)
		{
			Model.cluster = value;
			if(Model.currentPhoto != null)
				Model.currentPhoto.cluster = value;
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		@SuppressWarnings("rawtypes")
		JList list = (JList)e.getSource();
		if(list.getParent().getParent().toString() == COMPANY)
		{
			Company c = (Company)list.getSelectedValue();
			if(Model.currentCompany != c)
			{
				Model.currentCompany = c;
				ControlWindow.getInstance().repaint();
			}
		}
		else if(list.getParent().getParent().toString() == PHOTO)
		{
	    	ImageWindow.getInstance().setTitle("");
			Photo p = (Photo)list.getSelectedValue();
			if(p != null && Model.currentPhoto != p)
				Model.setCurrentPhoto(p);
			ImageWindow.getInstance().repaint();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		JSlider source = (JSlider)e.getSource();
		Controller.slider(source, source.toString());
	}

	/**
	 * On a button press, the ActionCommand for the button is processed.
	 * On the enter key being pressed in a text field, accepts/rejects the input.
	 */
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() instanceof AbstractButton)
		{
			button(e.getActionCommand());
		}
		else if(e.getSource() instanceof JFormattedTextField)
		{
			JFormattedTextField source = (JFormattedTextField)e.getSource();
			field(source, source.toString());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		ClickPanel cp = ClickPanel.getInstance();
		Photo ph = Model.currentPhoto;
		int x = (int)(e.getX()*((1.0*ph.width)/(1.0*cp.getWidth())));
		int y = (int)(e.getY()*((1.0*ph.height)/(1.0*cp.getHeight())));
		Point point = new Point(x, y);
		
		boolean left = e.getButton() == MouseEvent.BUTTON1;
		if(left)
		{	//Add a new vote at the clicked location
			ph.corners.add(point);
		}
		else
		{	//Remove the nearest vote
			double minDist = 10000.0;
			Point closest = null;
			for(Point p : ph.corners)
			{
				double dist = point.distance(p);
				if(dist < minDist)
				{
					closest = p;
					minDist = dist;
				}
			}
			if(minDist < 500) //prevents the case where the click was far from any vote
				ph.corners.remove(closest);
		}
		ImagePanel.getInstance().repaint();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}
}
