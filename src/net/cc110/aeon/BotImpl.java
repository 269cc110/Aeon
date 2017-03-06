package net.cc110.aeon;

import java.util.*;
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
	}
	
	public void onSuccess(DiscordAPI api)
	{
		System.out.println("Connected");
		
		api.registerListener((MessageCreateListener)(DiscordAPI api_, Message message) ->
		{
			Channel channel = message.getChannelReceiver();
			String content = message.getContent();
			
			System.out.println((channel != null ? (channel.getServer().getName() + "#" + channel.getName()) : "PM") + "#" + message.getAuthor().getName() + ": " + message.getContent());
			
			if(content.startsWith(Aeon.config.prefix + Aeon.config.prefix))
			{
				User user = message.getAuthor();
				Server server = channel.getServer();
				
				List<String> tokens = Util.tokenise(content.substring(2));
				
				String reply = Aeon.serverCommands.getReply(channel.getServer().getId(), tokens.get(0))
						.replace("^", "^C")
						.replace("\\\\", "^S")
						.replace("\\%", "^P")
						.replace("%caller.name%", user.getName())
						.replace("%caller.nick%", user.hasNickname(server) ? user.getNickname(server) : user.getName())
						.replace("%caller.id%", user.getId());
				
				for(int i = 1; i < tokens.size(); i++)
				{
					reply = reply.replace("%target" + (i - 1) + "%", tokens.get(i));
				}
				
				reply = reply.replace("^P", "%")
						.replace("^S", "\\")
						.replace("^C", "^");
				
				message.reply(reply);
			}
		});
		
		api.registerListener((MessageDeleteListener)(DiscordAPI api_, Message message) -> Aeon.deletedMessages.put(message.getAuthor().getId(), message.getContent()));
		
		JavacordHandler handler = new JavacordHandler(api);
		
		handler.setDefaultPrefix(Aeon.config.prefix);

		handler.registerCommand(new CommandCat());
		handler.registerCommand(new CommandUndelete());
		handler.registerCommand(new CommandDog());
		handler.registerCommand(new CommandCatFact());
		handler.registerCommand(new CommandCommand());
		handler.registerCommand(new CommandAeon());
		handler.registerCommand(new CommandHeresy());
	}
}
