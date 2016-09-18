package model;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Model
{
	public static Integer copyThreshold;
	public static Integer copyCluster;
	public static int threshold;
	public static int cluster;
	public static final int MAXTHRESHOLD = 255;
	public static final int MAXCLUSTER = 500;
	public static Map<Company, Set<Photo>> companies;
	public static Company uncategorized;
	public static Company currentCompany;
	public static Photo currentPhoto;
	public static boolean showCorners;
	
	public static void initialize()
	{
		showCorners = false;
		copyThreshold = null;
		copyCluster = null;
		threshold = 254;
		cluster = 31;
		companies = new TreeMap<Company, Set<Photo>>();
		uncategorized = new Company(null);
		currentCompany = uncategorized;
		String[] comps = {
				"Lifeline Response",
				"Mind's Eye Innovations",
				"Secure My Social",
				"Try the World"
		};
		for(String s : comps) companies.put(new Company(s), new TreeSet<Photo>());
		companies.put(uncategorized, new TreeSet<Photo>());
	}
	
	public static void setThreshold(int value)
	{
		threshold = value;
		if(currentPhoto != null) currentPhoto.threshold = value;
	}
	
	public static void setCluster(int value)
	{
		cluster = value;
		if(currentPhoto != null) currentPhoto.cluster = value;
	}
	
	public static void setCurrentPhoto(Photo p)
	{
		currentPhoto = p;
		if(p.threshold == null) p.threshold = threshold;
		else threshold = p.threshold;
		if(p.cluster == null) p.cluster = cluster;
		else cluster = p.cluster;
	}	
}
