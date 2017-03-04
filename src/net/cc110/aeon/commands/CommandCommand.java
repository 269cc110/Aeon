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
			
			if(tokens.size() > 1)
			{
				switch(tokens.get(1))
				{
					case "set":
						if(tokenCount > 3) Aeon.serverCommands.set(message.getChannelReceiver().getServer().getId(), tokens.get(2), tokens.get(3));
						break;
					case "delete":
						if(tokenCount > 2) if(!Aeon.serverCommands.remove(message.getChannelReceiver().getServer().getId(), tokens.get(2))) return "Command not found: " + tokens.get(2);
						break;
				}
			}
		}
		
		return null;
	}
}
