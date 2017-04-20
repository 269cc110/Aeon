package net.cc110.aeon.commands;

import java.util.*;
import net.cc110.aeon.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.message.*;

public class CommandRepeat implements AsyncCommandExecutor
{
	public String execute(DiscordAPI api, Message message, List<String> tokens)
	{
		if(message.getAuthor().getId().equals(Aeon.config.overlord) && tokens.size() > 2)
		{
			
		}
		
		return null;
	}
	
	public List<String> getAliases()
	{
		return Collections.unmodifiableList(Arrays.asList("repeat"));
	}
	
	public String getDescription()
	{
		return "Repeats a string a specific number of times";
	}
}
