package net.cc110.aeon.commands;

import java.util.*;
import net.cc110.aeon.*;
import net.cc110.aeon.util.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.*;
import de.btobastian.javacord.entities.message.*;
import de.btobastian.javacord.entities.permissions.PermissionType;

public class CommandUndelete implements CommandExecutor
{
	public String execute(DiscordAPI api, Message message, List<String> tokens)
	{
		boolean pm = message.isPrivateMessage();
		
		User author = message.getAuthor();
		User user = null;
		String userID = null;
		
		Channel channel = null;
		String channelID = null;
		
		Server server = null;
		String serverID = null;
		
		if(!pm)
		{
			channel = message.getChannelReceiver();
			channelID = channel.getId();
			
			server = channel.getServer();
			serverID = server.getId();
		}
		
		if(tokens.size() > 1)
		{
			if(pm || Util.hasPermission(author, server, PermissionType.MANAGE_MESSAGES))
			{
				String id = Util.getIDFromMention(tokens.get(1));
				
				if(id != null && (!pm || id.equals(author.getId())))
				{
					user = Util.silentGetFuture(api.getUserById(id));
					
					if(user == null) return null;
				}
			}
		}
		else user = message.getAuthor();
		
		userID = user.getId();
		
		String deletedMessage = pm
				? Aeon.deletedMessages.getPMDeletedMessage(userID)
						: Aeon.deletedMessages.getServerDeletedMessage(serverID, channelID, userID);
		
		if(deletedMessage != null)
		{
			if(Aeon.config.enableEmbeds)
			{
				message.reply(null, Util.getEmbed(null, deletedMessage, user));
				return null;
			}
			
			return user.getMentionTag() + ": " + deletedMessage;
		}
		
		return "No deleted message stored for " + user.getMentionTag();
	}
	
	public List<String> getAliases()
	{
		return Collections.unmodifiableList(Arrays.asList("undelete"));
	}
}
