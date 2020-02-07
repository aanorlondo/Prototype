package control;
import java.util.Iterator;
import java.util.Set;

import model.Service;
import model.VM;

public class Elasticity 
{
	Monitor monitor;
	Actuator actuator;
	
	//constructor
	public Elasticity(Monitor m, Actuator a)
	{
		this.monitor = m;
		this.actuator = a;	
	}
///////////////////////////////////////////////////////////////////IMPLEMENTING THE ACTIONS FOR STRATEGIES//////////////////////////////
	//how requests are routed to service instances
	public void distributeRequests(int q)
	{
		while (this.monitor.getRequests() > 0 && !monitor.getAllServices().isEmpty() && !monitor.allOverloadedS(monitor.getAllServices()))
		{  
			Iterator<Service> iterator = this.monitor.getAllServices().iterator();
			while(iterator.hasNext() && this.monitor.getRequests() > 0)
			{
				Service element = iterator.next();
				if (element.getLoad() < this.monitor.getSmax()) 
				{
						System.out.print("System Queue length = "+monitor.getRequests());
						int t = actuator.systemQtoServiceQ(element, Math.min(monitor.getRequests(),q));
						System.out.print(" transferring "+t+" requests to "+element.id);
						monitor.getQueue().length -= t;
						System.out.println(" NEW System Queue length = "+monitor.getRequests());
				}
			}
			monitor.refreshStates();
		}
	}
	//consume requests
	public void serveRequests(int q)
	{
		Iterator<Service> iterator = this.monitor.getAllServices().iterator();
		while(iterator.hasNext())
		{
			Service element = iterator.next();
			if (element.getLoad() > 0) monitor.processing += actuator.releaseRequest(element, q);
		}
	}
	//how service instances chose their host VM
	public boolean map(Service s)
	{
		VM vm = monitor.lessVM();
		if (vm == null) return false;
		else
			if (vm.getLoad() < this.monitor.getVMmax()) 
			{
				this.actuator.deployService(vm, s);
				return true;
			}
			else return false;
	}
/////////////////////////////////////////////////////////THE SCALING ACTIONS////////////////////////////////////////////////////////////////
	//VMs////////////////////////////////////
	//scaling out VMs (VM replication)
	public void scaleOutV(int cas)
	{
		boolean base = monitor.getAllVMs().size() == 0  && (monitor.getQueue().length > 0);
		boolean predicate = false;
		switch (cas)
		{
			case 1 : 
			{
				monitor.refreshStates();
				predicate = monitor.existOverloadedVM(monitor.getAllVMs()) && (monitor.existEmptyVM(monitor.getAllVMs()) == null); break; //ONE VM L --> + VM
			}
			case 2 : 
			{
				monitor.refreshStates();
				predicate = monitor.allOverloadedV(monitor.getAllVMs()); break; //ALL VM L --> + VM
			}
			default : break;
		}
		//creates new VM if no offline VM and if max capacity of online VMs not reached
		monitor.refreshStates();
		if (base || (predicate && (monitor.getVMs() < monitor.system.capacity))) actuator.deployVM(new VM(monitor.getVMmax()));
	}
	//scaling in VMs (VM destruction)
	public void scaleInV()
	{
		monitor.refreshStates();
		if (monitor.allEmptyV(monitor.getAllVMs()) && monitor.getRequests() == 0  && monitor.workload == 0) 
			actuator.destroyVMs(monitor.getAllVMs());
		else
		{
			VM v = monitor.existEmptyVM(monitor.getAllVMs());
			//if (v != null && !monitor.existOverloadedVM(monitor.getAllVMs())) // && (monitor.getAllVMs().size()-1 > 0))
			if (v != null)
				actuator.destroyVM(v); //the empty VM is returned in variable v
		}
	}
	//SERVICES////////////////////////////////
	//scaling out services (service replication)
	public void scaleOutS(int cas)
	{
		boolean base =  (monitor.getAllServices().size() ==0 ) && (monitor.getQueue().length > 0 ); 
		boolean predicate = false;
		switch (cas)
		{
			case 1 : 
			{
				predicate = (monitor.existOverloadedS(monitor.getAllServices()) && ((monitor.existEmptyService(monitor.getAllServices()) == null))); break; //ONE S L --> + S
			}
			case 2 : 
			{
				predicate = monitor.allOverloadedS(monitor.getAllServices()); break; //ALL S L --> + S
			}
			default : break;
		}
		if ((predicate || base) && (!monitor.allOverloadedV(monitor.getAllVMs()))) map(new Service(monitor.getSmax()));
	}
	//scaling in Services (services destruction)
	public void scaleInS()
	{
		if (monitor.allEmptyS(monitor.getAllServices()) && monitor.getRequests() == 0 && monitor.workload == 0) //ALL S U && Q = 0 ---> - ALL S
		{
			actuator.destroyServices(monitor.getAllServices());
		}
		else //ONE S U && Exist S ! L ---> - S U
		{
			Service s = monitor.existEmptyService(monitor.getAllServices());
			//if (s != null)
			if ((s != null) && !monitor.existOverloadedS(monitor.getAllServices()) && monitor.getServices() > 1)
			{
				actuator.destroyService(s); //the empty S is returned in variable s
			}
		}
	}
	
	public void MigrateS()
	{
		Set<VM> VMs = monitor.getAllVMs();
		Service s = null;
		if (monitor.existOverloadedVM(VMs))
		{
			s = monitor.mostVM().services.iterator().next();
		}
		if (s != null ) 
		{
			VM vm = monitor.lessVM();
			if(vm != null && (vm.getLoad()+1 < this.monitor.getVMmax()))
			{
				String prev = s.host.id;
				s.host.services.remove(s);
				vm.services.add(s);
				s.host=vm;
				System.out.println("Service instance "+s.id+" migrated from "+prev+" to "+vm.id+" TOTAL VMs = "+VMs.size()+"/ Services ="+monitor.getAllServices().size());
			}
		}
		
	}
	
	public void scale(int v, int s)
	{
		monitor.refreshStates();
		scaleInS();
		monitor.refreshStates();
		scaleInV();
		monitor.refreshStates();
		MigrateS();
		monitor.refreshStates();
		scaleOutV(v);
		monitor.refreshStates();
		scaleOutS(s);
		monitor.refreshStates();
	}
}