package net.cc110.aeon.commands;

import java.util.*;
import net.cc110.aeon.*;
import de.btobastian.sdcf4j.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.message.*;

public class CommandAeon implements CommandExecutor
{
	@Command(aliases = {"aeon"}, description = "Aeon management")
	public String onCommand(DiscordAPI api, Message message)
	{
		if(message.getAuthor().getId().equals(Aeon.config.overlord))
		{
			List<String> tokens = Util.tokenise(message.getContent());
			int tokenCount = tokens.size();
			
			if(tokenCount > 1)
			{
				switch(tokens.get(1).toLowerCase())
				{
					case "disconnect":
						message.reply("Disconnecting :frowning:");
						Util.sleep(1000); // ensure message is sent
						api.disconnect();
						System.exit(0);
						break;
						
					case "save":
						Aeon.save();
						message.reply("Save queued");
						break;
						
					case "autosave":
						if(tokenCount > 2)
						{
							switch(tokens.get(2))
							{
								case "on":
									Aeon.config.autosave = true;
									message.reply("Autosave on");
									break;
									
								case "off":
									Aeon.config.autosave = false;
									message.reply("Autosave off");
									break;
									
								case "interval":
									if(tokenCount > 3)
									{
										int interval = Util.silentParseInt(tokens.get(3), -1);
										if(interval > 0)
										{
											Aeon.config.autosaveInterval = interval;
											message.reply("Set autosave interval -> " + interval);
										}
									}
									break;
							}
						}
						break;
						
					case "game":
						if(tokenCount > 2)
						{
							switch(tokens.get(2))
							{
								case "set":
									if(tokenCount > 3)
									{
										String game = tokens.get(3);
										Aeon.config.setGame(api, game);
										message.reply("Set game -> \"" + game + "\"");
									}
									break;
									
								case "clear":
									Aeon.config.setGame(api, null);
									message.reply("Game cleared");
									break;
							}
						}
						break;
						
					case "version":
						message.reply(Aeon.VERSION);
						break;
				}
			}
			else message.reply("Aeon " + Aeon.VERSION);
		}
		
		return null;
	}
}
