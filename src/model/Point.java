package model;

public class Point
{
	public int x;
	public int y;
	
	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public double distance(Point p)
	{
		int dx = Math.abs(p.x - x);
		int dy = Math.abs(p.y - y);
		return Math.sqrt(1.0*dx*dx + 1.0*dy*dy);
	}
	
	public String toString()
	{ return "(" + x + "," + y + ")"; }
}
