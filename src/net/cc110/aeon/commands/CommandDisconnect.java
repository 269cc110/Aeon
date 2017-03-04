package net.cc110.aeon.commands;

import net.cc110.aeon.*;
import de.btobastian.sdcf4j.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.message.*;

public class CommandDisconnect implements CommandExecutor
{
	@Command(aliases = {"disconnect"}, description = "Disconnects from Discord")
	public String onCommand(DiscordAPI api, Message message)
	{
		if(message.getAuthor().getId().equals(Aeon.config.overlord))
		{
			message.reply("Disconnecting :frowning:");
			Util.sleep(1000); // ensure message is sent
			api.disconnect();
			System.exit(0);
			return null;
		}
		else return "pleb level too high";
	}
}
