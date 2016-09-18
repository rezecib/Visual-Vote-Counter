package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import control.ImageListener;
import model.Company;
import model.Model;
import model.Photo;
import model.Point;

public class ImagePanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static ImagePanel self = null;
	private BufferedImage logo;
	private static final String LOGOFILE = "SharkTank.png";
	
	public static ImagePanel getInstance()
	{
		if(self == null) self = new ImagePanel();
		return self;
	}
	
	private ImagePanel()
	{
		logo = null;
		setBackground(new Color(0, 160, 168));
	}
	
    @Override
    protected void paintComponent(Graphics go)
    {
    	Graphics2D g = (Graphics2D)go;
    	int bannerHeight = 200;
        super.paintComponent(g);
        String banner = (Model.currentPhoto == null ? Company.UNCATEGORIZED : Model.currentPhoto.company);
        if(banner.equals(Company.UNCATEGORIZED)) banner = null;
        Photo ph = Model.currentPhoto;
        if(ph != null)
        {
        	g.drawImage((ph.metaImage != null && Model.showCorners ? ph.metaImage : ph.view), 0, 0, getWidth(), getHeight(), null); // see javadoc for more info on the parameters
        	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        	g.setColor(Color.GREEN);
        	Stroke oldStroke = g.getStroke();
        	g.setStroke(new BasicStroke(3.0f));
        	if(!Model.currentPhoto.buffering && ph.corners.size() < 10000)
        	for(Point point : ph.corners)
        	{
        		double yscale = (1.0*getHeight())/(1.0*ph.height);
        		double xscale = (1.0*getWidth())/(1.0*ph.width);
        		Point p = new Point((int)(xscale*point.x),
        				(int)(yscale*point.y));
        		int size = (int)(Photo.windowSize(point.y)*yscale);
        		g.drawRect(p.x-(size/4), p.y-(size/4), size, size);
        	}
        	g.setStroke(oldStroke);
        }
        else
        {
        	if(logo == null) logo = ImageListener.loadImage(LOGOFILE);
        	int W = getWidth();
        	int H = getHeight();
        	int h = logo.getHeight();
        	int ho = (H-h)/2;
        	g.drawImage(logo, 0, ho, W, h, null);
        }
        if(banner != null)
        {
        	if(!ph.corners.isEmpty())
        		banner = banner + ": " + ph.corners.size();
        	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        	g.setColor(new Color(0, 0, 0, 150));
        	g.fillRect(0, 0, getWidth(), bannerHeight);
        	g.setColor(new Color(255, 255, 255, 50));
        	g.setColor(Color.WHITE);
        	g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 90));
        	FontMetrics fm = g.getFontMetrics();
        	g.drawString(banner, 
        			(getWidth() - fm.stringWidth(banner))/2, //horizontally centered
        			(bannerHeight + fm.getAscent() - fm.getDescent())/2); //vertically centered
        }
    }
    
	/** Overrides to synchronize with the model before painting */
	public void repaint()
	{
		super.repaint();
		ClickPanel.getInstance().repaint();
	}

}
