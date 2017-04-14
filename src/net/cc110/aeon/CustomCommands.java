package net.cc110.aeon;

import java.util.*;
import java.util.concurrent.*;

public class CustomCommands
{
	@SuppressWarnings("unchecked")
	private ConcurrentHashMap<String, ConcurrentHashMap<String, String>> commands[] =
	(ConcurrentHashMap<String, ConcurrentHashMap<String, String>>[])new ConcurrentHashMap[]
	{
		new ConcurrentHashMap<String, ConcurrentHashMap<String, String>>(), // server
		new ConcurrentHashMap<String, ConcurrentHashMap<String, String>>()  // pm
	};
	
	public void set(boolean pm, String id, String command, String response)
	{
		int i = pm ? 1 : 0;
		
		ConcurrentHashMap<String, String> serverMap;
		
		synchronized(commands[i])
		{
			if(commands[i].containsKey(id)) serverMap = commands[i].get(id);
			else commands[i].put(id, serverMap = new ConcurrentHashMap<String, String>());
		}
		
		serverMap.put(command, response);
	}
	
	public boolean remove(boolean pm, String id, String command)
	{
		int i = pm ? 1 : 0;
		
		synchronized(commands[i])
		{
			if(commands[i].containsKey(id))
			{
				ConcurrentHashMap<String, String> serverMap = commands[i].get(id);
				
				if(serverMap.containsKey(command))
				{
					serverMap.remove(command);
					return true;
				}
			}
		}
		
		return false;
	}
	
	public String getReply(boolean pm, String id, String command)
	{
		int i = pm ? 1 : 0;
		
		synchronized(commands[i])
		{
			return commands[i].containsKey(id) ? commands[i].get(id).get(command) : null;
		}
	}
	
	public boolean commandExists(boolean pm, String id, String command)
	{
		int i = pm ? 1 : 0;
		
		synchronized(commands[i])
		{
			return commands[i].containsKey(id) && commands[i].get(id).containsKey(command);
		}
	}
	
	public Set<String> getCommands(boolean pm, String id)
	{
		int i = pm ? 1 : 0;
		
		synchronized(commands[i])
		{
			if(commands[i].containsKey(id)) return Collections.unmodifiableSet(commands[i].get(id).keySet());
		}
		
		return null;
	}
}
