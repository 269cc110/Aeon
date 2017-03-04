package net.cc110.aeon;

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
				message.reply(Aeon.serverCommands.getReply(channel.getServer().getId(), Util.tokenise(content.substring(2)).get(0)));
			}
		});
		
		api.registerListener((MessageDeleteListener)(DiscordAPI api_, Message message) -> Aeon.deletedMessages.put(message.getAuthor().getId(), message.getContent()));
		
		JavacordHandler handler = new JavacordHandler(api);
		
		handler.setDefaultPrefix(Aeon.config.prefix);

		handler.registerCommand(new CommandCat());
		handler.registerCommand(new CommandDisconnect());
		handler.registerCommand(new CommandUndelete());
		handler.registerCommand(new CommandDog());
		handler.registerCommand(new CommandCatFact());
		handler.registerCommand(new CommandCommand());
	}
}
