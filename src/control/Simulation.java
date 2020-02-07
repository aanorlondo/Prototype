package control;

import java.util.Set;
import java.util.TreeSet;

import model.CloudSystem;
import model.Poisson;
import model.Queue;
import model.Service;
import model.VM;

public class Simulation 
{
	/// This is a simulation for a cloud system with ONE single server (hardware server)
	/// Capacity = Maximum online VMs
	/// K = Maximum Service instances per VM
	/// W = Maximum requests per service instance
	/// lambda = arriving requests per unit of time
	/// mu = service rate per server (service instance)
	
	///the initial setup of the system is 1 online VM hosting 1 service instance
	
	public static void main(String args[])
	{
		//the parameters
		int inf = 999999999;
		int capacity = 10; //max VMs in the system
		int K = 4; //max Services per VM
		int W = 50; //max requests per Service
		int buffer = inf; //max system queue size
		double lambdaMax = 500.0; //arrival rate
		double mu = 150.0; //service rate
		int v_scale = 2;   ///  H strategy at infrastructure level
		int s_scale = 2;	/// H strategy at service level
		int steps = 5000; //simulation steps
		
		
		//output file name (according to parameters)
		String buf; if (buffer ==  inf) buf="INF" ; else buf = ""+buffer;
		
		//String outputFile = "C:/Users/Khaled/Google Drive/Réplication Doctorat/2018-2019/Rédaction/chapitres/Versions WORD/5 - Expérimentation/results/essais"
				//+ "output_C"+capacity+"_K"+K+"_W"+W+"_B"+buf+"_lambda"+lambdaMax+"_mu"+mu+"_V"+v_scale+"_S"+s_scale+"_sim"+steps+"_"+System.currentTimeMillis()+".csv";
		
		String outputFile = "d:/results/output_C"+capacity+"_K"+K+"_W"+W+"_B"+buf+"_lambda"+lambdaMax+"_mu"+mu+"_V"+v_scale+"_S"+s_scale+"_sim"+steps+"_"+System.currentTimeMillis()+".csv";

		//the initial VMs
		Set<VM> VMs = new TreeSet<VM>();
		VM v = new VM(K);
		VMs.add(v);
		
		//the initial Services
		Set<Service> services = new TreeSet<Service>();
		Service s = new Service(W);
		//Service s2 = new Service(W);
		services.add(s);
		//services.add(s2);
		
		//The system queue
		Queue queue = new Queue(buffer); //infinite calls
		
		//Creating the system and the MAPE elements
		CloudSystem CS = new CloudSystem(K,W,lambdaMax,mu,VMs,services,queue,capacity,buffer);
		Monitor monitor = new Monitor(CS);
		Actuator actuator = new Actuator(CS);
		Elasticity elast = new Elasticity(monitor, actuator);
	
		//initial deployment
		elast.actuator.deployVM(v);
		elast.actuator.deployService(v, s);
		//elast.actuator.deployService(v, s2);
		
		//the output file
		monitor.openFile(outputFile);
		
		//sim lost / processing restart
		monitor.refresh();
		int workload = monitor.workload;
		
		
		//the initial header print with metrics and average results calculation in csv
		monitor.printState("Capacity;"+capacity+"; ;Total Received Requests;=SOMME(E12:E"+Math.addExact(12, steps)+"); ;Average VM Deployment;=MOYENNE(B12:B"+Math.addExact(12, steps)+")");	 //1
		monitor.printState("K;"+K+"; ;Treated Requests;=SOMME(H12:H"+Math.addExact(12, steps)+"); ;System VM Capacity Usage;=(H1/B1)*100;%");	//2
		monitor.printState("W;"+W+"; ;Lost Requests;=SOMME(I12:I"+Math.addExact(12, steps)+")");	//3
		monitor.printState("Buffer;"+buf+"; ; ; ; ;Average Service Deployment;=MOYENNE(C12:C"+Math.addExact(12, steps)+")");	//4
		monitor.printState("Lambda;"+lambdaMax+"; ;Treated Requests Rate;=(E2/E1)*100;%;System Service Capacity Usage;=(H4/(B1*B2))*100;%");	//5
		monitor.printState("Mu;"+mu+"; ;Loss Rate;=(E3/E1)*100;%");	//6
		monitor.printState("V-scale;"+v_scale+"; ;Pending;=100-E5;%;Average System Load;=MOYENNE(H12:H"+Math.addExact(12, steps)+")");	//7
		monitor.printState("S-scale;"+s_scale+"; ;Delay;=(SOMME(G12:G"+Math.addExact(12, steps)+")/E1)*100;%;Average Service Load;=H7/(H4*B3)*100;%");	//8
		monitor.printState("Steps;"+steps+"; ;Average Workload;=MOYENNE(E12:E"+Math.addExact(12, steps)+"); ;Adaptations;=A"+Math.addExact(12, steps));	//9
		monitor.printState("--------------------------------------------------------");
		monitor.printState("Step;Virtual Machines;Service Instances;Capacity;Workload;System Queue;Service Queue;System Load;Lost Requests");

		
		//double lambda2 = lambda;
		double lambda = 0.1;
		int tick = 0;
		
		//the running loop
		while (tick < steps)
		{	
			tick++;
			/*if (tick == 2) lambda = 2500;
			else if (tick == 4) lambda = 5000;
			else if (tick == 8) lambda = 10000;
			else if (tick == 10) lambda = 12500;
			else if (tick == 20) lambda = 10000;
			else if (tick == 30) lambda = 17500;
			else if (tick == 40) lambda = 25000;
			else if (tick == 50) lambda = 22500;
			else if (tick == 70) lambda = 27500;
			else if (tick == 80) lambda = 35000;
			else if (tick == 90) lambda = 50000;
			else if (tick == 100) lambda = 47500;
			else if (tick == 110) lambda = 40000;
			else if (tick == 130) lambda = 25000;
			else if (tick == 140) lambda = 20000;
			else if (tick == 150) lambda = 10000;
			else if (tick == 160) lambda = 5000;
			else if (tick == 170) lambda = 2000;
			else if (tick == 180) lambda = 500;
			else if (tick == 190) lambda = 50;*/
			
			/*if (tick == 2) lambda = (lambdaMax * 0.05);
			else if (tick == 4) lambda = (lambdaMax * 0.1);
			else if (tick == 8) lambda = (lambdaMax * 0.2);
			else if (tick == 10) lambda = (lambdaMax * 0.25);
			else if (tick == 20) lambda = (lambdaMax * 0.2);
			else if (tick == 30) lambda = (lambdaMax * 0.35);
			else if (tick == 40) lambda = lambdaMax * 0.5;
			else if (tick == 50) lambda = lambdaMax * 0.45;
			else if (tick == 70) lambda = (lambdaMax * 0.55);
			else if (tick == 80) lambda = lambdaMax * 0.7;
			else if (tick == 90) lambda = lambdaMax;
			else if (tick == 100) lambda = lambdaMax * 0.95;
			else if (tick == 110) lambda = lambdaMax * 0.8;
			else if (tick == 130) lambda = lambdaMax * 0.5;
			else if (tick == 140) lambda = lambdaMax * 0.4;
			else if (tick == 150) lambda = (lambdaMax * 0.2);
			else if (tick == 160) lambda = (lambdaMax * 0.1);
			else if (tick == 170) lambda = (lambdaMax * 0.05);
			else if (tick == 180) lambda = (lambdaMax * 0.01);
			else if (tick == 190) lambda = (lambdaMax * 0.001);*/
			
			workload = Poisson.getPoisson(lambda);
			
			
			System.out.println("\ntick = "+tick);
			//incoming requests
			workload = Poisson.getPoisson(lambda); //input
			System.out.println("Incoming workload = "+workload);
			actuator.queueRequests(queue, workload);
			monitor.refreshStates();
			//dispatch requests
			int munext = Poisson.getPoisson(mu); //output
			elast.distributeRequests(munext);
			monitor.refreshStates();
			//adapting
			elast.scale(v_scale,s_scale);
			monitor.refreshStates();
			//serve requests
			elast.serveRequests(munext);
			monitor.refreshStates();
			int offering = monitor.getAllServices().size()*(int)mu;
			monitor.printState(tick+";"+monitor.getVMs()+";"+monitor.getServices()+";"+offering+";"+workload+";"+monitor.getRequests()+";"+Math.abs(workload-monitor.getHandledRequests()-monitor.getRequests())+";"+monitor.getHandledRequests()+";"+monitor.getLost());
			monitor.refresh();
		}
		
		//close file
		monitor.printState(actuator.adaptations+" ");
		monitor.closeFile();
	}
	
}