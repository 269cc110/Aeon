package net.cc110.aeon;

import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.message.*;

public class CommandHandler
{
	private ConcurrentHashMap<String, CommandExecutor> executors = new ConcurrentHashMap<>();
	
	void registerExecutor(CommandExecutor executor)
	{
		synchronized(executors)
		{
			List<String> aliases = executor.getAliases();
			
			if(Aeon.config.caseInsensitiveCommands)
				aliases = aliases.stream().map(t -> t.toLowerCase()).collect(Collectors.toList());
			
			for(String alias : executor.getAliases())
			{
				executors.put(alias, executor);
			}
		}
		
		executor.onRegistered(this);
	}
	
	boolean tryExecute(DiscordAPI api, final Message message, List<String> tokens)
	{
		if(tokens.isEmpty()) return false;
		
		synchronized(executors)
		{
			String command = Aeon.config.caseInsensitiveCommands ? tokens.get(0).toLowerCase() : tokens.get(0);
			
			if(executors.containsKey(command))
			{
				CommandExecutor executor = executors.get(command);

				if(executor instanceof AsyncCommandExecutor) Aeon.pool.getExecutorService().submit(() ->
				{
					String reply = executor.execute(api, message, tokens);
					if(reply != null) message.reply(reply);
				});
				else
				{
					String reply = executor.execute(api, message, tokens);
					if(reply != null) message.reply(reply);
				}
				
				return true;
			}
			else return false;
		}
	}
}
