package net.cc110.aeon;

import java.util.concurrent.*;

public class ServerCommands
{
	private ConcurrentHashMap<String, ConcurrentHashMap<String, String>> commands = new ConcurrentHashMap<>();
	
	public void set(String server, String command, String response)
	{
		ConcurrentHashMap<String, String> serverMap;
		
		if(commands.containsKey(server)) serverMap = commands.get(server);
		else commands.put(server, serverMap = new ConcurrentHashMap<String, String>());
		
		serverMap.put(command, response);
	}
	
	public boolean remove(String server, String command)
	{
		if(commands.containsKey(server))
		{
			ConcurrentHashMap<String, String> serverMap = commands.get(server);
			
			if(serverMap.containsKey(command))
			{
				serverMap.remove(command);
				return true;
			}
		}
		
		return false;
	}
	
	public String getReply(String server, String command)
	{
		return commands.containsKey(server) ? commands.get(server).get(command) : null;
	}
	
	public boolean commandExists(String server, String command)
	{
		return commands.containsKey(server) && commands.get(server).containsKey(command);
	}
}
