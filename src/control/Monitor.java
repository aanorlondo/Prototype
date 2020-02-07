package control;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;

import model.*;

public class Monitor 
{
	public int processing;
	public int workload;
	
	public CloudSystem system;
	PrintWriter writer;
	
	//constructor and init (link the monitor to a cloud system)
	public Monitor (CloudSystem s)
	{
		system = s;
	}
///////////////////////////////////////////////////////////////////////DATA REFRESH///////////////////////////////////////////////////
	//sim metrics refresh
	public void refresh()
	{
		this.processing = 0;
		this.workload = 0;
		this.system.lost = 0;
	}
	public void refreshStates()
	{
		this.setVStates(); //refresh VMs states
		this.setSStates(); //refresh Services states	
	}
//////////////////////////////////////////////////////////////////////////////UPDATING STATES////////////////////////////////////////
	//set VMs States // K : VM capacity threshold //// 0 = normal / 1 = overloaded / 2 = unused / 3 = new
	public void setVStates()
	{
		Iterator<VM> iterator = this.system.VMs.iterator();
		while(iterator.hasNext())
		{
			VM element = iterator.next();
			if (element.getLoad() < getVMmax() && element.getLoad() > 0 && element.state != 0) element.state = 0;
			else if (element.getLoad() == 0 && element.state != 2 && element.state != 3) element.state = 2;
			else if (element.getLoad() >= getVMmax() && element.state != 1) element.state = 1;
		}
	}
	//set Services states  // W : service workload threshold
	public void setSStates()
	{
		Iterator<Service> iterator = this.system.services.iterator();
		while(iterator.hasNext())
		{
			Service element = iterator.next();
			if (element.getLoad() < getSmax() && element.getLoad() > 0 && element.state != 0) element.state = 0;
			else if (element.getLoad() == 0 && element.state != 2 && element.state != 3) element.state = 2;
			else if (element.getLoad() >= getSmax() && element.state != 1) element.state = 1;
		}
	}
////////////////////////////////////////////////////////////////////////////////MONITORING METRICS//////////////////////////////////////////////
	//return VMs threshold
	public int getVMmax()
	{
		return this.system.K;
	}
	//return Services threshold
	public int getSmax()
	{
		return this.system.W;
	}
	//number of requests in the all services queues
	public int getRequests()
	{
		return this.getQueue().length;
	}
	//number of handled requets by all service instances
	public int getHandledRequests()
	{
		Iterator<Service> iterator = getAllServices().iterator();
		while(iterator.hasNext())
		{
			Service element = iterator.next();
			if (isEmpty(element))
			{
				processing += element.getLoad();
			}
		}
		return processing;
	}
	//general request queue of the system
	public Queue getQueue()
	{
		return this.system.queue;
	}
	
	//Number of rejected requets
	public int getLost()
	{
		return system.lost;
	}
	
	//number of services in the system
	public int getServices()
	{
		return this.system.services.size();
	}
	//number of vms in the system
	public int getVMs()
	{
		return this.system.VMs.size();
	}
/////////////////////////////////////////////////////////////////////////////MONITORING VIRTUAL MACHINES/////////////////////////////////////
	//less loaded VM
	public VM lessVM()
	{
		VM temp = null;
		Iterator<VM> iterator = this.system.VMs.iterator();
		if (iterator.hasNext())
		{
			temp = iterator.next();
		}
		while(iterator.hasNext())
		{
			VM element = iterator.next();
			if (element.getLoad() <= temp.getLoad()) temp = element;
		}
		return temp;
	}
	//most loaded VM
	public VM mostVM()
	{
		VM temp = null;
		Iterator<VM> iterator = this.system.VMs.iterator();
		if (iterator.hasNext())
		{
			temp = iterator.next();
		}
		while(iterator.hasNext())
		{
			VM element = iterator.next();
			if (element.getLoad() >= temp.getLoad()) temp = element;
		}
		return temp;
	}
	
	//VM is unused
	public boolean isEmpty(VM v)
	{
		if (v.state == 2) return true; 
		return false;
	}
	//there exists an unused VM instance
	public VM existEmptyVM(Set<VM> set) //envoi par référence 
	{
		if (set.isEmpty()) return null;
		Iterator<VM> iterator = set.iterator();
		while(iterator.hasNext())
		{
			VM element = iterator.next();
			if (isEmpty(element))
			{
				return element;
			}
		}
		return null;
	}
	//all VMs are empty
	public boolean allEmptyV(Set<VM> set)
	{
		if (set.isEmpty()) return false;
		Iterator<VM> iterator = set.iterator();
		while(iterator.hasNext())
		{
			VM element = iterator.next();
			if (!isEmpty(element))
			{
				return false;
			}
		}
		return true;
	}
	//VM is overloaded
	public boolean isOverloaded(VM v)
	{
		if (v.state == 1) return true; else return false;
	}
	//there exists an oveloaded VM instance
	public boolean existOverloadedVM(Set<VM> set) //envoi par référence 
	{
		if (set.isEmpty()) return false;
		Iterator<VM> iterator = set.iterator();
		while(iterator.hasNext())
		{
			VM element = iterator.next();
			if (isOverloaded(element))
			{
				return true;
			}
		}
		return false;
	}
	//all VMs are overloaded
	public boolean allOverloadedV(Set<VM> set)
	{
		if (set.isEmpty()) return false;
		Iterator<VM> iterator = set.iterator();
		while(iterator.hasNext())
		{
			VM element = iterator.next();
			if (!isOverloaded(element))
			{
				return false;
			}
		}
		return true;
	}
	//get all VMs
	public Set<VM> getAllVMs()
	{
		return this.system.VMs;
	}
////////////////////////////////////////////////////////////////////////////MONITORING SERVICES//////////////////////////////////////////////////
	//less loaded Service
	public Service lessService()
	{
		Service temp = null;
		Iterator<Service> iterator = this.system.services.iterator();
		if (iterator.hasNext())
		{
			temp = iterator.next();
		}
		while(iterator.hasNext())
		{
			Service element = iterator.next();
			if (element.getLoad() <= temp.getLoad()) temp = element;
		}
		return temp;
	}
	//service is unused
	public boolean isEmpty(Service s)
	{
		if (s.q.length == 0 && s.state == 2) return true; 
		else return false;
	}
	//there exists an unused Service instance
	public Service existEmptyService(Set<Service> set) //envoi par référence 
	{
		if (set.isEmpty()) return null;
		Iterator<Service> iterator = set.iterator();
		while(iterator.hasNext())
		{
			Service element = iterator.next();
			if (isEmpty(element))
			{
				return element;
			}
		}
		return null;
	}
	//all services are empty
	public boolean allEmptyS(Set<Service> set)
	{
		if (set.isEmpty()) return false;
		Iterator<Service> iterator = set.iterator();
		while(iterator.hasNext())
		{
			Service element = iterator.next();
			if (!isEmpty(element))
			{
				return false;
			}
		}
		return true;
	}
	//service is overloaded
	public boolean isOverloaded(Service s)
	{
		if (s.state == 1) return true; 
		return false;
	}
	//all services are overloaded
	public boolean allOverloadedS(Set<Service> set)
	{
		if (set.isEmpty()) return false;
		Iterator<Service> iterator = set.iterator();
		while(iterator.hasNext())
		{
			Service element = iterator.next();
			if (!isOverloaded(element))
			{
				return false;
			}
		}
		return true;
	}
	//there exists an oveloaded Service instance
	public boolean existOverloadedS(Set<Service> set) //envoi par référence 
	{
		if (set.isEmpty()) return false;
		Iterator<Service> iterator = set.iterator();
		while(iterator.hasNext())
		{
			Service element = iterator.next();
			if (isOverloaded(element))
			{
				return true;
			}
		}
		return false;
	}
	//get all services (objects)
	public Set<Service> getAllServices()
	{
		return this.system.services;
	}
/////////////////////////////////////////////////////////////////////////OUTPUT/////////////////////////////////////////////////////////
	//display state
	public void openFile(String outputFile)
	{
		try 
		{
			this.writer = new PrintWriter(outputFile, "UTF-8");
		} 
		catch (FileNotFoundException | UnsupportedEncodingException e) 
		{
			System.out.println("///////////Output file could not be Open/////////");
		}
	}
	public void printState(String state)
	{
		this.writer.println(state);
	}
	public void closeFile()
	{
		this.writer.close();
	}
}