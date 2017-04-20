package net.cc110.aeon;

import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.message.*;

public class CommandHandler
{
	private ConcurrentHashMap<String, CommandExecutor> executors = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, String> aliasToCommand = new ConcurrentHashMap<>();
	
	private final Object MAP_LOCK = new Object();
	
	public void registerExecutor(CommandExecutor executor)
	{
		List<String> aliases = executor.getAliases();
		
		if(Aeon.config.caseInsensitiveCommands)
			aliases = aliases.stream().map(String::toLowerCase).collect(Collectors.toList());
		
		String command = aliases.get(0);
		
		synchronized(MAP_LOCK)
		{
			aliases.stream().forEach(t -> aliasToCommand.put(t, command));
			executors.put(command, executor);
		}
		
		executor.onRegistered(this);
	}
	
	public boolean tryExecute(DiscordAPI api, final Message message, List<String> tokens)
	{
		if(tokens.isEmpty()) return false;
		
		CommandExecutor executor = null;
		
		String command = Aeon.config.caseInsensitiveCommands ? tokens.get(0).toLowerCase() : tokens.get(0);
		
		synchronized(MAP_LOCK)
		{
			if(aliasToCommand.containsKey(command)) executor = executors.get(aliasToCommand.get(command));
			else return false;
		}
		
		final CommandExecutor executor_ = executor;
		
		Runnable execute = () ->
		{
			String reply = executor_.execute(api, message, tokens);
			if(reply != null) message.reply(reply);
		};

		if(executor instanceof AsyncCommandExecutor) Aeon.pool.getExecutorService().submit(execute);
		else execute.run();
		
		return true;
	}
	
	public List<String> getCommands()
	{
		synchronized(MAP_LOCK)
		{
			return Collections.unmodifiableList(executors.keySet().stream().collect(Collectors.toList()));
		}
	}
	
	public String getDescription(String command)
	{
		if(Aeon.config.caseInsensitiveCommands) command = command.toLowerCase();
		
		CommandExecutor executor = null;
		
		synchronized(MAP_LOCK)
		{
			if(executors.containsKey(command)) executor = executors.get(command);
			else return null;
		}
		
		return executor.getDescription();
	}
	
	public boolean commandExists(String command)
	{
		if(Aeon.config.caseInsensitiveCommands) command = command.toLowerCase();
		
		return executors.containsKey(command);
	}
	
	public boolean commandOrAliasExists(String command)
	{
		if(Aeon.config.caseInsensitiveCommands) command = command.toLowerCase();
		
		return aliasToCommand.containsKey(command);
	}
	
	public List<String> getAliasesFor(String command)
	{
		synchronized(MAP_LOCK)
		{
			return aliasToCommand.keySet().stream()
					.filter(t -> !command.equals(t) && aliasToCommand.get(t).equals(command)).collect(Collectors.toList());
		}
	}
}
