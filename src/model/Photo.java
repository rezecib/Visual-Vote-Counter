package model;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import control.ImageListener;
import view.ImagePanel;
import view.ImageWindow;

public class Photo implements Comparable<Photo>, Runnable
{
	public boolean buffering;
	public Image metaImage;
	public Image view;
	private String name;
	public String company;
	public Integer threshold;
	public Integer cluster;
	public Set<Point> corners;
	public int width;
	public int height;
	
	public Photo(Image view, String name, int width, int height)
	{
		this.metaImage = null;
		this.view = view;
		this.name = name;
		this.width = width;
		this.height = height;
		buffering = false;
		threshold = null;
		cluster = null;
		corners = new HashSet<Point>();
	}
	
	public String getName() { return name; }
	
	/** Allows a separate thread to be run to load the image to prevent UI lockup */
	public void run()
	{
		buffering = true;
		detectVotes();
		if(buffering)
		{
			ImagePanel.getInstance().repaint();
		}
		buffering = false;
	}
	
	public void detectVotes()
	{
		ImageWindow.getInstance().setTitle("Processing...");
		detectBlobs();
		ImageWindow.getInstance().setTitle("");		
		ImagePanel.getInstance().repaint();
	}
	
	private void detectBlobs()
	{
		int min = 1000;
		int max = 2200;
		
		int label = 1;
		BufferedImage image = ImageListener.loadImage(name);
		int[][] labels = new int[image.getWidth()][image.getHeight()];
		DisjointSet<Integer> ds = new DisjointSet<Integer>();
		ImageWindow.getInstance().setTitle("Processing... (first pass: labeling)");
		Color[] targetBlobs = {
				//Second-gen blob flag, live images
				new Color(189, 24, 38),		//Red
				new Color(33, 59, 84),		//Blue, ends up gray
				new Color(56, 71, 32),		//Green
				new Color(195, 150, 109),	//White
		};
		for(int x = 0; x < image.getWidth(); x++) //first pass: labeling
			for(int y = min; y < max; y++)
			{
				boolean sameAsNorth = false, sameAsWest = false;
				boolean target = false;
				for(Color c : targetBlobs)
					if(isSimilar(c.getRGB(), image.getRGB(x, y), 4.0f))
						target = true;
				if(target)
				{
					float h = (1f*y)/(image.getHeight()*1f);
					float blobMult = 0.7f;
					if(h > 0.5f)
						blobMult = blobMult * (1-h) * 2f;
					if(y > 0 && labels[x][y-1] > 0)
						sameAsNorth = isSimilar(image.getRGB(x, y), image.getRGB(x, y-1), blobMult);
					if(x > 0 && labels[x-1][y] > 0)
						sameAsWest = isSimilar(image.getRGB(x, y), image.getRGB(x-1, y), blobMult);
					if(sameAsNorth && sameAsWest) //north and west need to be merged
						labels[x][y] = ds.merge(labels[x-1][y], labels[x][y-1]);
					else if(y > 0 && sameAsNorth)
						labels[x][y] = labels[x][y-1];
					else if(x > 0 && sameAsWest)
						labels[x][y] = labels[x-1][y];
					else //both are false, make a new label
					{
						labels[x][y] = label;
						ds.add(label);
						label++;
					}
				}
				else
					labels[x][y] = -1;
			}
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		Map<Integer, Double[]> colorChannels = new HashMap<Integer, Double[]>(); 
		ImageWindow.getInstance().setTitle("Processing... (second pass: consolidation)");
		for(int x = 0; x < image.getWidth(); x++) //second pass: consolidating labels and calculating color averages
			for(int y = min; y < max; y++)
			{
				if(labels[x][y] == -1) continue; //skip the discarded pixels (ones too far from target colors)
				//Consolidating labels
				int lbl = ds.find(labels[x][y]);
				labels[x][y] = lbl;
				//Counting number of occurrences of this label so far
				if(!counts.containsKey(lbl))
				{
					counts.put(lbl, 1);
					
					Color c = new Color(image.getRGB(x, y));
					Double[] color = {1.0*c.getRed(), 1.0*c.getGreen(), 1.0*c.getBlue(), 1.0*x, 1.0*y};
					colorChannels.put(lbl, color);
				}
				else
				{
					counts.put(lbl, counts.get(lbl)+1);
					
					//Adding the color at this pixel to the average of others with the same label
					int count = counts.get(lbl);
					double nextAdj = 1.0/(1.0*count);
					double prevAdj = (count-1)*nextAdj;
					Double[] color = colorChannels.get(lbl);
					Color c = new Color(image.getRGB(x, y));
					color[0] = prevAdj*color[0] + c.getRed()*nextAdj;
					color[1] = prevAdj*color[1] + c.getGreen()*nextAdj;
					color[2] = prevAdj*color[2] + c.getBlue()*nextAdj;
					color[3] = prevAdj*color[3] + x*nextAdj;
					color[4] = prevAdj*color[4] + y*nextAdj;
				}
			}
		//Minor pass through colors of blobs: discards blobs that are too big or too different from target colors
		Map<Integer, Color> colors = new HashMap<Integer, Color>();
		short[][] blobs = new short[image.getWidth()][image.getHeight()];
		colors.put(-1, Color.BLACK);
		for(Integer lbl : colorChannels.keySet())
		{
			Double[] ch = colorChannels.get(lbl);
			Color blob = new Color(ch[0].intValue(), ch[1].intValue(), ch[2].intValue());
			Color result = new Color(255, 0, 255);
			int target = -1;
			for(int i = 0; i < targetBlobs.length; i++)
			{
				Color c = targetBlobs[i];
				if(isSimilar(c.getRGB(), blob.getRGB(), 3.0f))
					target = i;
			}
			if(counts.get(lbl) < 50 || counts.get(lbl) > 250000) //100 works perfectly for front-center
			{
				target = -1;
				result = Color.BLUE;
			}
			if(target >= 0)
			{
				colors.put(lbl, blob);
				blobs[ch[3].intValue()][ch[4].intValue()] = (short)(target+1);
			}
			else
				colors.put(lbl, result);
		}
		BufferedImage meta = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		
		ImageWindow.getInstance().setTitle("Processing... (third pass: coloring)");
		for(int x = 0; x < image.getWidth(); x++) //third pass: painting color averages onto diagnostic image
			for(int y = min; y < max; y++)
				meta.setRGB(x, y, colors.get(labels[x][y]).getRGB());
		
		ImageWindow.getInstance().setTitle("Processing... (third pass: checking for votes (0%))");
		int windowSize = 30;
		int increment = windowSize/10;
		for(int yo = min; yo < max-160; yo+=increment)
		{
			if(yo % 50 == 0 )
			{
				String percent = "" + (100.0*(yo-min))/(1.0*max);
				ImageWindow.getInstance().setTitle("Processing... (third pass: checking for votes ("
						+ percent.substring(0, percent.indexOf('.')) + "%))");
			}
			windowSize = windowSize(yo); //Clamp: func between 30 and 160
			increment = windowSize/10;
			int half = windowSize/2;
			int rem = windowSize%2;
			for(int xo = 0; xo < image.getWidth()-windowSize; xo+=increment)
			{
				boolean red = false;
				boolean grn = false;
				boolean wht = false;
				int xr, yr = 0,
//						xb, yb = 0,
						xg, yg = 0,
						xw, yw = 0;
				red:for(xr = xo; xr <= xo + half; xr++)
					for(yr = yo; yr <= yo + half; yr++)
						if(blobs[xr][yr] == 1)
						{
							red = true;
							break red;
						}
				grn:for(xg = xo; xg <= xo + half; xg++)
					for(yg = yo+half+rem; yg <= yo + windowSize; yg++)
						if(blobs[xg][yg] == 3)
						{
							grn = true;
							break grn;
						}
				wht:for(xw = xo+half+rem; xw <= xo + windowSize; xw++)
					for(yw = yo+half+rem; yw <= yo + windowSize; yw++)
						if(blobs[xw][yw] == 4)
						{
							wht = true;
							break wht;
						}
				if(
						red
//						&& blu //keeping track of blue actually degraded accuracy-- probably color shifts due to lighting?
						&& grn
						&& wht)
				{
					blobs[xr][yr] = -1;
//					blobs[xb][yb] = -1;
					blobs[xg][yg] = -1;
					blobs[xw][yw] = -1;
					Point n = new Point(xo+half, yo+half);
					boolean close = false;
					for(Point p : corners)
						if(p.distance(n) < windowSize)
							close = true;
					if(!close) corners.add(n);
				}
			}
		}
		
		metaImage = meta.getScaledInstance(ImagePanel.getInstance().getWidth(),
				ImagePanel.getInstance().getHeight(),
				BufferedImage.SCALE_SMOOTH);
	}
	
	public static int windowSize(int yo)
	{
		double func = 0.0001871658*(yo*yo) - 0.38048128*yo + 223.315508;
		return (int)Math.max(30.0, Math.min(func, 160.0));
	}
	
	private boolean isSimilar(int center, int other, float multiplier)
	{
		int thresh = (int)(25 * multiplier);
		Color c = new Color(center);
		Color o = new Color(other);
		int rDif = Math.abs(c.getRed() - o.getRed());
		int gDif = Math.abs(c.getGreen() - o.getGreen());
		int bDif = Math.abs(c.getBlue() - o.getBlue());
		int tDif = rDif+gDif+bDif;
		return tDif < thresh;
	}
	
	@Override
	public int compareTo(Photo that)
	{
		return this.name.compareTo(that.name);
	}
	
	public String toString() { return name; }
}
