package model;

public class Company implements Comparable<Company>
{
	public static final String UNCATEGORIZED = "Uncategorized";
	
	private String name;

	public Company(String name)
	{
		this.name = name;
		if(name == null) this.name = UNCATEGORIZED;
	}
	
	public String toString()
	{
		return name;
	}
	
	public int compareTo(Company that)
	{
		if(this.name == that.name) return 0;
		if(name == UNCATEGORIZED) return -1;
		else return this.name.compareTo(that.name);
	}
}
