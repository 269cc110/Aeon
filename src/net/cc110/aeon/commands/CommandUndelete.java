package net.cc110.aeon.commands;

import net.cc110.aeon.*;
import de.btobastian.sdcf4j.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.*;
import de.btobastian.javacord.entities.message.*;

public class CommandUndelete implements CommandExecutor
{
	@Command(aliases = {"undelete"}, description = "Undeletes the last deleted message")
	public String onCommand(DiscordAPI api, Message message)
	{
		User user = message.getAuthor();
		String id = user.getId();
		
		if(Aeon.deletedMessages.containsKey(id))
		{
			String reply = Aeon.deletedMessages.get(id);
			Aeon.deletedMessages.remove(id);
			return user.getMentionTag() + ": " + reply;
		}
		else return "No deleted messages stored for " + user.getMentionTag();
	}
}
