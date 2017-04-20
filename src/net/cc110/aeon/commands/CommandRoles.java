package net.cc110.aeon.commands;

import java.util.*;
import net.cc110.aeon.*;
import java.util.stream.*;
import net.cc110.aeon.util.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.*;
import de.btobastian.javacord.entities.message.*;

public class CommandRoles implements CommandExecutor
{
	public String execute(DiscordAPI api, Message message, List<String> tokens)
	{
		if(tokens.size() > 1)
		{
			String id = Util.getIDFromMention(tokens.get(1));
			
			if(id != null)
			{
				User user = Util.silentGetFuture(api.getUserById(id));
				
				if(user != null)
				{
					List<String> roles = user.getRoles(message.getChannelReceiver().getServer()).stream()
							.map(t -> "`" + t.getName() + "`").sorted().collect(Collectors.toList());
					
					return roles.isEmpty() ? "User " + user.getMentionTag() + " has no roles"
							: user.getMentionTag() + "'s roles: " + Util.concatenate(roles);
				}
				else return "Error";
			}
		}
		
		return null;
	}
	
	public List<String> getAliases()
	{
		return Collections.unmodifiableList(Arrays.asList("roles"));
	}
	
	public String getDescription()
	{
		return "Returns a user's roles";
	}
}
