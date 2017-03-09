package net.cc110.aeon.commands;

import java.util.*;
import net.cc110.aeon.*;
import de.btobastian.sdcf4j.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.*;
import de.btobastian.javacord.entities.message.*;
import de.btobastian.javacord.entities.permissions.*;

public class CommandCommand implements CommandExecutor
{
	@Command(aliases = {"command"}, description = "Edits custom commands")
	public String onCommand(DiscordAPI api, Message message)
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
		
		if(pm || user.getId().equals(Aeon.config.overlord) || Util.hasPermission(user, server, PermissionType.MANAGE_SERVER))
		{
			List<String> tokens = Util.tokenise(message.getContent());
			int tokenCount = tokens.size();
			
			if(tokens.size() > 2)
			{
				String command = tokens.get(2);
				
				switch(tokens.get(1).toLowerCase())
				{
					case "set":
						if(tokenCount > 3)
						{
							String reply = tokens.get(3);
							Aeon.customCommands.set(pm, pm ? user.getId() : server.getId(), command, reply);
							message.reply("Set " + Aeon.config.getPrefix() + Aeon.config.getPrefix() + command + " -> \"" + reply + "\"");
						}
						break;
						
					case "delete":
						if(!Aeon.customCommands.remove(pm, pm ? user.getId() : server.getId(), command)) return "Command not found: " + command;
						break;
				}
			}
		}
		
		return null;
	}
}
