package net.cc110.aeon;

import java.util.*;
import net.cc110.aeon.util.*;
import de.btobastian.javacord.*;
import net.cc110.aeon.commands.*;
import de.btobastian.sdcf4j.handler.*;
import de.btobastian.javacord.entities.*;
import com.google.common.util.concurrent.*;
import de.btobastian.javacord.entities.message.*;
import de.btobastian.javacord.listener.message.*;

public class BotImpl implements FutureCallback<DiscordAPI>
{
	public void onFailure(Throwable t)
	{
		t.printStackTrace();
		
		if(!Aeon.config.debug) t.printStackTrace(Aeon.STDERR);
		
		System.exit(1);
	}
	
	public void onSuccess(DiscordAPI api)
	{
		System.out.println("Connected");
		
		JavacordHandler handler = new JavacordHandler(api);
		
		Aeon.config.init(api, handler);
		
		api.registerListener((MessageCreateListener)(DiscordAPI api_, Message message) ->
		{
			Channel channel = null;
			Server server = null;

			User user = message.getAuthor();
			String game = user.getGame();
			
			boolean pm = message.isPrivateMessage();
			
			if(!pm)
			{
				channel = message.getChannelReceiver();
				server = channel.getServer();
			}
			
			String id = pm ? user.getId() : server.getId();
			String content = message.getContent();
			
			System.out.println((pm ? "PM" : server.getName() + "#" + channel.getName()) + "#" + user.getName() + ": " + message.getContent());
			
			if(content.length() > 2 && content.startsWith(Aeon.config.getPrefix() + Aeon.config.getPrefix()))
			{
				List<String> tokens = Util.tokenise(content.substring(2));
				
				String command = tokens.get(0);
				
				if(Aeon.customCommands.commandExists(pm, id, command))
				{
					String reply = Aeon.customCommands.getReply(pm, id, command)
							.replace("^", "^C")
							.replace("\\\\", "^S")
							.replace("\\%", "^P")
							.replace("%caller.name%", user.getName())
							.replace("%caller.nick%", pm ? user.getName() : (user.hasNickname(server) ? user.getNickname(server) : user.getName()))
							.replace("%caller.id%", user.getId())
							.replace("%caller.discriminator%", user.getDiscriminator())
							.replace("%caller.game%", game == null ? "null" : game)
							.replace("%caller%", user.getMentionTag());
					
					for(int i = 1; i < tokens.size(); i++)
					{
						reply = reply.replace("%target" + (i - 1) + "%", tokens.get(i));
					}
					
					reply = reply.replace("^P", "%")
							.replace("^S", "\\")
							.replace("^C", "^");
					
					message.reply(reply);
				}
			}
		});
		
		api.registerListener((MessageDeleteListener)(DiscordAPI api_, Message message) -> Aeon.deletedMessages.put(message.getAuthor().getId(), message.getContent()));

		handler.registerCommand(new CommandCat());
		handler.registerCommand(new CommandUndelete());
		handler.registerCommand(new CommandDog());
		handler.registerCommand(new CommandCatFact());
		handler.registerCommand(new CommandCommand());
		handler.registerCommand(new CommandAeon());
		handler.registerCommand(new CommandHeresy());
		
		System.out.println("Ready");
	}
}
