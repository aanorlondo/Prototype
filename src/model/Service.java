package model;

public class Service extends Entity implements Comparable<Service>
{
	public Queue q;
	public VM host;
	public int rejected;
	
	//constructor
	public Service(int threshold)
	{
		this.id = "S@"+this.hashCode()+System.currentTimeMillis();
		this.q = new Queue(threshold);
		this.state = 3;
		this.rejected = 0;
	}
	//get the workload (number of handled requests)
	public int getLoad()
	{
		return this.q.length;
	}
	@Override
	
	
	public int compareTo(Service o) 
	{
		if (this.id == o.id) return 0;
		return 1;
	}
}