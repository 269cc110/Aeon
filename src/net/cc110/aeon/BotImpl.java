package net.cc110.aeon;

import java.util.*;
import net.cc110.aeon.util.*;
import de.btobastian.javacord.*;
import net.cc110.aeon.commands.*;
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
		
		Aeon.uploadHandler.init();
		Aeon.config.init(api);
		
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
			
			System.out.println((pm ? "PM" : server.getName() + " #" + channel.getName()) + " " + user.getName() + ": " + message.getContent());
			
			String prefix = Aeon.config.prefix;
			int prefixLength = prefix.length();
			
			if(content.length() > prefixLength
					&& (Aeon.config.scanOwnMessages || !message.getAuthor().isYourself()) && content.startsWith(prefix))
			{
				final String content_ = content.substring(prefixLength);
				final Server server_ = server;
				
				if(content_.length() > prefixLength)
				{
					if(content_.startsWith(prefix))
					{
						if(Aeon.config.asyncCustomCommands) Aeon.pool.getExecutorService().execute(()
									-> handleCustomCommand(content_, prefixLength, pm, id, user, server_, game, message));
						else handleCustomCommand(content_, prefixLength, pm, id, user, server_, game, message);
					}
					else Aeon.commandHandler.tryExecute(api_, message, Util.tokenise(content_));
				}
			}
		});
		
		api.registerListener((MessageDeleteListener)(DiscordAPI api_, Message message) ->
		{
			User user = message.getAuthor();
			String userID = user.getId();
			
			if(message.isPrivateMessage())
				Aeon.deletedMessages.deletePMMessage(message.getAuthor().getId(), message.getContent());
			else
			{
				Channel channel = message.getChannelReceiver();
				String channelID = channel.getId();
				
				Server server = channel.getServer();
				String serverID = server.getId();
				
				Aeon.deletedMessages.deleteServerMessage(serverID, channelID, userID, message.getContent());
			}
		});

		Aeon.commandHandler.registerExecutor(new CommandCat());
		Aeon.commandHandler.registerExecutor(new CommandUndelete());
		Aeon.commandHandler.registerExecutor(new CommandDog());
		Aeon.commandHandler.registerExecutor(new CommandCatFact());
		Aeon.commandHandler.registerExecutor(new CommandCommand());
		Aeon.commandHandler.registerExecutor(new CommandAeon());
		Aeon.commandHandler.registerExecutor(new CommandHeresy());
		Aeon.commandHandler.registerExecutor(new CommandTwoInchBrush());
		Aeon.commandHandler.registerExecutor(new CommandRoles());
		Aeon.commandHandler.registerExecutor(new CommandColour());
		Aeon.commandHandler.registerExecutor(new CommandFoodPorn());
		
		System.out.println("Ready");
	}
	
	private void handleCustomCommand(String content, int prefixLength, boolean pm, String id, User user, Server server, String game, Message message)
	{
		List<String> tokens = Util.tokenise(content.substring(prefixLength));
		
		String command = tokens.get(0);
		
		String reply = null;
		
		synchronized(Aeon.customCommands)
		{
			if(Aeon.customCommands.commandExists(pm, id, command)) reply = Aeon.customCommands.getReply(pm, id, command);
		}
		
		if(reply != null)
		{
			reply = reply.replace("^", "^C")
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
}
