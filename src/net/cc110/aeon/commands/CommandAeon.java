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
			List<String> tokens = Util.tokenise(message.getContent().toLowerCase());
			int tokenCount = tokens.size();
			
			if(tokenCount > 1)
			{
				switch(tokens.get(1))
				{
					case "disconnect":
						message.reply("Disconnecting :frowning:");
						Util.sleep(1000); // ensure message is sent
						api.disconnect();
						System.exit(0);
						break;
						
					case "save":
						Aeon.save();
						break;
						
					case "autosave":
						if(tokenCount > 2)
						{
							switch(tokens.get(2))
							{
								case "on":
									Aeon.config.autosave = true;
									break;
									
								case "off":
									Aeon.config.autosave = false;
									break;
									
								case "interval":
									if(tokenCount > 3)
									{
										int interval = Util.silentParseInt(tokens.get(3), -1);
										if(interval > 0) Aeon.config.autosaveInterval = interval;
									}
									break;
							}
						}
						break;
				}
			}
		}
		
		return null;
	}
}
