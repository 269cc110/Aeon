package net.cc110.aeon;

import java.util.concurrent.*;

public class DeletedMessages
{
	private ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<String, String>>> serverDeletions = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, String> pmDeletions = new ConcurrentHashMap<>();
	
	public String getServerDeletedMessage(String serverID, String channelID, String userID)
	{
		synchronized(serverDeletions)
		{
			if(serverDeletions.containsKey(serverID))
			{
				ConcurrentHashMap<String, ConcurrentHashMap<String, String>> channelMap = serverDeletions.get(serverID);

				if(channelMap.containsKey(channelID))
				{
					ConcurrentHashMap<String, String> userMap = channelMap.get(channelID);
					
					if(userMap.containsKey(userID)) return userMap.get(userID);
				}
			}
			
			return null;
		}
	}
	
	public void deleteServerMessage(String serverID, String channelID, String userID, String content)
	{
		synchronized(serverDeletions)
		{
			ConcurrentHashMap<String, ConcurrentHashMap<String, String>> channelMap;
			
			if(serverDeletions.containsKey(serverID)) channelMap = serverDeletions.get(serverID);
			else serverDeletions.put(serverID, channelMap = new ConcurrentHashMap<>());
	
			ConcurrentHashMap<String, String> userMap;
			
			if(channelMap.containsKey(channelID)) userMap = channelMap.get(channelID);
			else channelMap.put(channelID, userMap = new ConcurrentHashMap<>());
			
			userMap.put(userID, content);
		}
	}
	
	public String getPMDeletedMessage(String userID)
	{
		synchronized(pmDeletions)
		{
			return pmDeletions.containsKey(userID) ? pmDeletions.get(userID) : null;
		}
	}
	
	public void deletePMMessage(String userID, String message)
	{
		synchronized(pmDeletions)
		{
			pmDeletions.put(userID, message);
		}
	}
}
