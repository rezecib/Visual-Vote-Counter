package model;

import java.util.ArrayList;

public class Photos extends ArrayList<Photo>
{
	private static final long serialVersionUID = 1L;
	
	private static Photos self = null;
	
	public static Photos getInstance()
	{
		if(self == null)
			self = new Photos();
		return self;
	}
	
	public boolean hasPhoto(String name)
	{
		for(Photo p : self)
			if(p.getName().equals(name))
				return true;
		return false;
	}
}
