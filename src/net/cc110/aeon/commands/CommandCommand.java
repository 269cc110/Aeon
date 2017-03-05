package net.cc110.aeon.commands;

import java.util.*;
import net.cc110.aeon.*;
import de.btobastian.sdcf4j.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.message.*;
import de.btobastian.javacord.entities.permissions.PermissionType;

public class CommandCommand implements CommandExecutor
{
	@Command(aliases = {"command"}, description = "Edits server commands")
	public String onCommand(DiscordAPI api, Message message)
	{
		if(message.getAuthor().getId().equals(Aeon.config.overlord) ||
				Util.hasPermission(message.getAuthor(), message.getChannelReceiver().getServer(), PermissionType.MANAGE_SERVER))
		{
			List<String> tokens = Util.tokenise(message.getContent());
			int tokenCount = tokens.size();
			
			if(tokens.size() > 2)
			{
				String command = tokens.get(2);
				
				switch(tokens.get(1))
				{
					case "set":
						if(tokenCount > 3)
						{
							String reply = tokens.get(3);
							Aeon.serverCommands.set(message.getChannelReceiver().getServer().getId(), command, reply);
							message.reply("Set " + Aeon.config.prefix + Aeon.config.prefix + command + " -> \"" + reply + "\"");
						}
						break;
						
					case "delete":
						if(!Aeon.serverCommands.remove(message.getChannelReceiver().getServer().getId(), command)) return "Command not found: " + command;
						break;
				}
			}
		}
		
		return null;
	}
}
