package net.cc110.aeon.commands;

import java.util.*;

import net.cc110.aeon.*;
import net.cc110.aeon.util.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.*;
import de.btobastian.javacord.entities.message.*;
import de.btobastian.javacord.entities.permissions.*;

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
					Collection<Role> roles = user.getRoles(message.getChannelReceiver().getServer());
					
					if(!roles.isEmpty())
					{
						StringBuilder builder = new StringBuilder();
						
						boolean first = true;
						
						for(Role role : roles)
						{
							if(!first) builder.append(", ");
							builder.append("`" + role.getName() + "`");
							first = false;
						}
						
						return user.getMentionTag() + "'s roles: " + builder.toString();
					}
					else return "User " + user.getMentionTag() + " has no roles";
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
}
