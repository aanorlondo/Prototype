package model;

import java.util.Set;
import java.util.TreeSet;

public class VM extends Entity implements Comparable<VM>
{
	public int threshold;
	public Set<Service> services = new TreeSet<Service>();
	
	//constructor
	public VM(int threshold)
	{
		this.id = "V@"+this.hashCode()+"$$"+System.currentTimeMillis();
		this.threshold = threshold;
		state = 3; //new state
	}
	//the number of running service instances
	public int getLoad()
	{
		return this.services.size();
	}
	
	
	@Override
	public int compareTo(VM o) 
	{
		if (this.id == o.id) return 0;
		return 1;
	}
}