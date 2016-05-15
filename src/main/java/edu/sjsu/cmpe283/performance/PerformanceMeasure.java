package edu.sjsu.cmpe283.performance;



import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.HashMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.spring.controller.WebAppInit;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.PerfCounterInfo;
import com.vmware.vim25.PerfEntityMetricBase;
import com.vmware.vim25.PerfEntityMetricCSV;
import com.vmware.vim25.PerfMetricId;
import com.vmware.vim25.PerfMetricSeriesCSV;
import com.vmware.vim25.PerfProviderSummary;
import com.vmware.vim25.PerfQuerySpec;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.PerformanceManager;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

import edu.sjsu.cmpe283.scaling.ScaleIn;
import edu.sjsu.cmpe283.scaling.ScaleOut;
import edu.sjsu.cmpe283.util.MongoDBConnection;


public class PerformanceMeasure 
{
	//Host memory CPU usage. Need to change it later. Instead use CPU memory usage.


	

	public static VirtualMachine vm;
	public String[] PerfCounters = { "cpu.usage.average"};
	private PerformanceManager perfMgr;
	private HashMap<Integer, PerfCounterInfo> countersInfoMap;
	private HashMap<String, Integer> countersMap;
	private PerfMetricId[] pmis;
	ServiceInstance si = null;
	
	 HashMap<String, Integer> vmCpuUsage = null;
	public PerformanceMeasure(ServiceInstance si2, VirtualMachine vm, HashMap<String, Integer> vmCpuUsage1) throws RemoteException, IOException 
	{
		si = si2;
		this.vm  = vm;
		vmCpuUsage = vmCpuUsage1;
		continueProgram();
	}

	public void continueProgram() {
		perfMgr = si.getPerformanceManager();
		PerfCounterInfo[] pcis = perfMgr.getPerfCounter();

		// create map between counter ID and PerfCounterInfo, counter name and
		// ID
		countersMap = new HashMap<String, Integer>();
		countersInfoMap = new HashMap<Integer, PerfCounterInfo>();

		for (int i = 0; i < pcis.length; i++) {
			countersInfoMap.put(pcis[i].getKey(), pcis[i]);
			countersMap.put(pcis[i].getGroupInfo().getKey() + "."
					+ pcis[i].getNameInfo().getKey() + "."
					+ pcis[i].getRollupType(), pcis[i].getKey());
		}

		pmis = createPerfMetricId(PerfCounters);
		System.out.println("Performance manager is set up.");

		try {
			printPerf(vm);
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private PerfMetricId[] createPerfMetricId(String[] counters) {
		PerfMetricId[] metricIds = new PerfMetricId[counters.length];
		for (int i = 0; i < counters.length; i++) {
			PerfMetricId metricId = new PerfMetricId();
			metricId.setCounterId(countersMap.get(counters[i]));
			metricId.setInstance("*");
			metricIds[i] = metricId;
		}
		return metricIds;
	}

	public void printPerf(ManagedEntity me) throws Exception {
		PerfProviderSummary pps = perfMgr.queryPerfProviderSummary(me);
		int refreshRate = pps.getRefreshRate().intValue();
		// only return the latest one sample
		PerfQuerySpec qSpec = createPerfQuerySpec(me, 1, refreshRate);

		PerfEntityMetricBase[] pValues = perfMgr
				.queryPerf(new PerfQuerySpec[] { qSpec });
		if (pValues != null) {
			displayValues(pValues);
		}
	}

	private void displayValues(PerfEntityMetricBase[] values) throws FileNotFoundException {
		for (int i = 0; i < values.length; ++i) {
			printPerfMetricCSV((PerfEntityMetricCSV) values[i]);
		}
	}

	private void printPerfMetricCSV(PerfEntityMetricCSV pem)
			throws FileNotFoundException {

		try {
			PerfMetricSeriesCSV[] csvs = pem.getValue();
			HashMap<Integer, PerfMetricSeriesCSV> stats = new HashMap<Integer, PerfMetricSeriesCSV>();

			for (int i = 0; i < csvs.length; i++) {
				stats.put(csvs[i].getId().getCounterId(), csvs[i]);
			}

			for (String counter : PerfCounters) 
			{
				Integer counterId = countersMap.get(counter);
				PerfCounterInfo pci = countersInfoMap.get(counterId);
				String value = null;
				System.out.println("Counter id: " + counterId);

				if (stats.containsKey(counterId))
					value = stats.get(counterId).getValue();
				if (value == null ||Integer.parseInt(value)<0 || value.length() == 0) {
					value = "0";
				}

				int val = Integer.parseInt(value);
				val = val/100;
				value  = String.valueOf(val);
				
				BasicDBObject nullAllVM = new BasicDBObject("VM Name", vm.getName()).append("VM IP", null);
				DBCollection allvmTable = MongoDBConnection.db.getCollection("allvm");
				DBCursor nullVMCursor = allvmTable.find(nullAllVM);
				if(nullVMCursor.hasNext())
				{
					System.out.println("null vm updated");
					BasicDBObject document = new BasicDBObject();
					document.put("VM Name", vm.getName());
					document.put("VM IP", vm.getGuest().getIpAddress());
					//System.out.println("vm ip---" + vm.getGuest().getIpAddress());
					allvmTable.update(nullAllVM, document);
				}
				
				BasicDBObject dbObj = new BasicDBObject("VM Name", vm.getName());
				DBCollection table = MongoDBConnection.db.getCollection("performance");
				DBCursor cursor1 = table.find(dbObj);
				if(cursor1.hasNext())
				{
					BasicDBObject document = new BasicDBObject();
					
					document.put("VM Name", vm.getName());
					document.put("VM IP", vm.getGuest().getIpAddress());
					//System.out.println("vm ip---" + vm.getGuest().getIpAddress());
					document.put("vCPU usage", val);
					BasicDBObject searchQuery = new BasicDBObject().append("VM Name", vm.getName()
							).append("VM IP", vm.getGuest().getIpAddress());
					table.update(searchQuery, document);
				}
				
				else
				{
					BasicDBObject document3 = new BasicDBObject();
				
					document3.put("VM Name", vm.getName());
					document3.put("VM IP", vm.getGuest().getIpAddress());
					//System.out.println("vm ip---" + vm.getGuest().getIpAddress());
					document3.put("vCPU usage", val);
					table.insert(document3);
				}
				
				//System.out.println("value inserted");

				//System.out.println("vm ip---" + vm.getGuest().getIpAddress());
				System.out.println( "Vm name: " + vm.getName() + "  " + "Value is " + value);

				
				
				//if in next ping cycle, threshold of vm is greater than upper threshold and 
				//vm is already present in
				//healthy vm then don't put it in healthy vm and remove it from HVM
				if(Integer.parseInt(value) <= Integer.parseInt(WebAppInit.getProp().getProperty("upperThresholdUsage_Performance")))
				{
					
					
				
					//check if present
					//If not present --> insert
					// If present --> update
					BasicDBObject query = new BasicDBObject("VM Name", vm.getName());
					DBCollection table1 = MongoDBConnection.db.getCollection("healthyvm");
					
					DBCursor cursor = table1.find(query);
					if(cursor.hasNext())
					{
						BasicDBObject newDocument = new BasicDBObject();
						newDocument.put("vCPU usage", val);
						newDocument.put("VM Name", vm.getName());
						newDocument.put("VM IP", vm.getGuest().getIpAddress());
						BasicDBObject searchQuery = new BasicDBObject().append("VM Name", vm.getName());
						table1.update(searchQuery, newDocument);
					}
					else
					{
						BasicDBObject document1 = new BasicDBObject();
						value = stats.get(counterId).getValue();
						document1.put("VM Name", vm.getName());
						//store vcpu usage and vmname in hashmap
						document1.put("vCPU usage", val);
						document1.put("VM IP", vm.getGuest().getIpAddress());
						table1.insert(document1);
						
					}
					vmCpuUsage.put(vm.getName(), Integer.parseInt(value));
				System.out.println("value inserted");
				
					
					// call this outside for only once
					
				}
				
				//If vCpu usage is greater than upper threshold and if the VM is present in 
				//healthy vm then remove it from the healthy vm list
				else if(Integer.parseInt(value) > Integer.parseInt(WebAppInit.getProp().getProperty("upperThresholdUsage_Performance")))
				{
					
					BasicDBObject query = new BasicDBObject("VM Name", vm.getName());
					DBCollection table1 = MongoDBConnection.db.getCollection("healthyvm");
					
					DBCursor cursor = table1.find(query);
					if(cursor.hasNext())
					{
						//remove
						table1.remove(cursor.next());
						
					}
					else
					{
							
					}
					//vmCpuUsage.put(vm.getName(), Integer.parseInt(value));
				System.out.println("value inserted");
				cursor.close();	
					
					// call this outside for only once
				}
			
				
			}
			
			

		} catch (Exception e) {
			
		}
	}

	synchronized private PerfQuerySpec createPerfQuerySpec(
			ManagedEntity me, int maxSample, int interval) {

		PerfQuerySpec qSpec = new PerfQuerySpec();
		qSpec.setEntity(me.getMOR());
		// set the maximum of metrics to be return
		qSpec.setMaxSample(new Integer(maxSample));
		qSpec.setMetricId(pmis);
		qSpec.setFormat("csv");
		qSpec.setIntervalId(new Integer(interval));
		return qSpec;
	}



}
