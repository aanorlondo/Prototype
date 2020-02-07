package model;

import java.util.Set;

public class CloudSystem 
{
	public int capacity; //max number of VMs
	public int K; //VMs max threshold
	public int W; //Services max threshold
	public int buffer;
	public double lambda; //arrivals
	public double mu; //service rate
	public Set<VM> VMs;
	public Set<Service> services;
	public Queue queue;
	public int lost;

	//constructor
	public CloudSystem (int K, int W, double lambda, double mu, Set<VM> VMs, Set<Service> services, Queue q, int capacity, int buffer)
	{
		this.K = K;
		this.W = W;
		this.lambda = lambda;
		this.mu = mu;
		this.VMs = VMs;
		this.services = services;
		this.queue = q;
		this.capacity = capacity;
	}
}