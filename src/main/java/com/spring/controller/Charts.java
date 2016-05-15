package com.spring.controller;


import java.util.ArrayList;
import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import edu.sjsu.cmpe283.util.MongoDBConnection;
@Controller
public class Charts {

	@RequestMapping(value = "/performance", method = RequestMethod.GET)
	public ModelAndView getPerformance()throws Exception
	{
		ArrayList<String>  vm=new ArrayList<String>();
		ArrayList<String> vCPU=new ArrayList<String>();
		ArrayList<String> iP=new ArrayList<String>();
		ModelAndView m=new ModelAndView("performance");
		//Mongo Fetch Code
		DBCollection col=MongoDBConnection.db.getCollection("healthyvm");
		DBCursor cursor=col.find();
		System.out.println(col.count());
		while(cursor.hasNext())
		{
			DBObject ob=cursor.next();
			vm.add(ob.get("VM Name").toString());
			if(ob.get("vCPU usage")==null)
			{
				vCPU.add("1");
			}
			else
			{
				vCPU.add(ob.get("vCPU usage").toString());
			}
			
			if((String)ob.get("VM IP")==null)
			{
				iP.add("");
			}
			else
			{
				iP.add((String)ob.get("VM IP"));
			}
			
		}		
		
		// Adding Attributes
		m.addObject("VM", vm);
		m.addObject("cpu", vCPU);
		m.addObject("ipaddr", iP);
		return m;
	}

	@RequestMapping(value = "/vmdata", method = RequestMethod.GET)
	public ModelAndView getVMInfo()throws Exception
	{
		ModelAndView m=new ModelAndView("VMData");
		ArrayList<String>  vm=new ArrayList<String>();
		ArrayList<String> vCPU=new ArrayList<String>();
		ArrayList<String> iP=new ArrayList<String>();
		//Mongo Fetch Code
		DBCollection col=MongoDBConnection.db.getCollection("performance");
		DBCursor cursor=col.find();
		System.out.println(col.count());
		while(cursor.hasNext())
		{
			DBObject ob=cursor.next();
			vm.add(ob.get("VM Name").toString());
			if(ob.get("vCPU usage")==null)
			{
				vCPU.add("1");
			}
			else
			{
				vCPU.add(ob.get("vCPU usage").toString());
			}
			
			if((String)ob.get("VM IP")==null)
			{
				iP.add("");
			}
			else
			{
				iP.add((String)ob.get("VM IP"));
			}
		}
		
		// Adding Attrbutes
		m.addObject("VM", vm);
		m.addObject("cpu", vCPU);
		m.addObject("ipaddr", iP);
		return m;						
	}


}