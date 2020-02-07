package control;
import java.util.Iterator;
import java.util.Set;

import model.*;

public class Actuator 
{
	public CloudSystem system;
	public int adaptations = 0;
	
	//constructor
	public Actuator(CloudSystem system)
	{
		this.system = system;
	}
//////////////////////////////////////////////////////////////////////////ACTIONS OVER VMs/////////////////////////////////////////	
	//deploy ONE VM and turn it ON
	public void deployVM(VM v)
	{
		//this.VMs.add(v); //static number of VMs
		this.system.VMs.add(v);
		System.out.println("Virtual Machine "+v.id+" succesfully deployed");
		adaptations++;
	}
	//deploy A SET of VMS and turn them ON
	public void deployVMs(Set<VM> v)
	{
		//this.VMs.addAll(v); //static number of VMs
		Iterator<VM> iterator = v.iterator();
		while(iterator.hasNext())
		{
			VM element = iterator.next();
			this.deployVM(element);
		}
	}
	//destroy ONE VM and turn it OFF
	public void destroyVM(VM v)
	{
		System.out.print("Number of VMs in the system : "+this.system.VMs.size());
		String id = v.id;
		this.system.VMs.remove(v);
		System.out.println(" virtual machine "+id+" destroyed - Remaining :"+this.system.VMs.size());
		adaptations++;
	}
	//destroy A SET OF VMs and turn them OFF
	public void destroyVMs(Set<VM> v)
	{
		this.system.VMs.clear();
		System.out.print("All Virtual Machines destroyed");
		adaptations++;
	}
////////////////////////////////////////////////////////////////////ACTIONS OVER Services////////////////////////////////////////////	
	//deploy ONE service
	public void deployService(VM v, Service s) 
	{
		v.services.add(s);
		system.services.add(s);
		s.host = v;
		System.out.println("Service instance "+s.id+" deployed to "+v.id);
		adaptations++;
	}
	//deploy A SET of services
	public void deployServices(VM v, Set<Service> s) 
	{
		v.services.addAll(s);
		system.services.addAll(s);
		System.out.println("all services deployed");
	}
	//destroy ONE service
	public void destroyService(Service s) 
	{
		System.out.print("Number of services in the system : "+this.system.services.size());
		String id = s.id;
		s.host.services.remove(s);
		this.system.services.remove(s);
		System.out.println(" Service instance"+id+" destroyed - Remaining : "+this.system.services.size());
		adaptations++;
	}
	//destroy A SET of services
	public void destroyServices(Set<Service> s) 
	{
		system.services.removeAll(s);
		System.out.println("all services destroyed");
	}	
/////////////////////////////////////////////////////////////////////ACTIONS OVER Requests/////////////////////////////////////////////
	//fill system queue with arrivals
	public void queueRequests(Queue qg, int q)
	{
		if (qg.length >= qg.capacity) 
		{
			system.lost += q;
		}
		else
			if (q > qg.capacity - qg.length)
			{
				system.lost += q - (qg.capacity - qg.length);
				qg.length = qg.capacity;
			}
			else
			{
				qg.length += q;
			}
	}
	//transfer from system queue to service queue
	public int systemQtoServiceQ(Service s, int q)
	{
		int result = 0;
		if (s.getLoad() >= system.W) 
		{
			return result;
		}
		else
			if (q >= system.W - s.getLoad())
			{
				result = (system.W - s.getLoad());
				s.q.length = system.W;
				return result;
			}
			else
			{
				s.q.length += q;
				return q;
			}
	}
	//serve requests
	public int releaseRequest(Service s, int q)
	{
		int result = 0;
		if (q >= s.getLoad()) 
		{
			result = s.getLoad();
			s.q.length = 0;
			return result;
		}
		else 
		{
			s.q.length -= q;
			return q;
		}
	}
}