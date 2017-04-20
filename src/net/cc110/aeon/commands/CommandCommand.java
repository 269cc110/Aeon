package net.cc110.aeon.commands;

import java.util.*;
import net.cc110.aeon.*;
import java.util.stream.*;
import net.cc110.aeon.util.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.*;
import de.btobastian.javacord.entities.message.*;
import de.btobastian.javacord.entities.permissions.*;

public class CommandCommand implements CommandExecutor
{
	public String execute(DiscordAPI api, Message message, List<String> tokens)
	{
		boolean pm = message.isPrivateMessage();
		
		Channel channel = null;
		Server server = null;
		
		User user = message.getAuthor();
		
		if(!pm)
		{
			channel = message.getChannelReceiver();
			server = channel.getServer();
		}
		
		boolean auth = pm || Util.hasPermission(user, server, PermissionType.MANAGE_SERVER);
		
		int tokenCount = tokens.size();
		
		String id = pm ? user.getId() : server.getId();
		
		if(tokens.size() > 1)
		{
			switch(tokens.get(1).toLowerCase())
			{
				case "set":
					if(auth && tokenCount > 3)
					{
						String command = tokens.get(2);
						String reply = tokens.get(3);
						Aeon.customCommands.set(pm, id, command, reply);
						
						message.reply("Set " + Util.monoQuote(Aeon.config.prefix + Aeon.config.prefix + command)
							+ " -> " + Util.monoQuote(reply));
					}
					break;
					
				case "delete":
					if(auth && tokenCount > 2)
					{
						String command = tokens.get(2);
						
						return (Aeon.customCommands.remove(pm, id, command)
							? "Deleted " : "Not found: ") + Util.monoQuote(command);
					}
					
					break;
					
				case "list":
					String commands = getCommandList(pm, id);
					
					return commands == null ? "No custom commands" + (pm ? "" : " for this server")
							: "Custom commands" + (pm ? ": " : " for this server: ") + commands;
			}
		}
		else
		{
			String commands = getCommandList(pm, id);
			
			return commands == null ? "No custom commands" + (pm ? "" : " for this server")
					: "Custom commands" + (pm ? ": " : " for this server: ") + commands;
		}
		
		return null;
	}
	
	public List<String> getAliases()
	{
		return Collections.unmodifiableList(Arrays.asList("command"));
	}
	
	public String getDescription()
	{
		return "Manages custom commands";
	}
	
	private static String getCommandList(boolean pm, String id)
	{
		String prefix = Aeon.config.prefix + Aeon.config.prefix;
		
		Set<String> commands = Aeon.customCommands.getCommands(pm, id);
		
		return commands.size() == 0 ? null
				: Util.concatenate(commands.stream().sorted()
						.map(t -> Util.monoQuote(prefix + t)).collect(Collectors.toList()));
	}
}
