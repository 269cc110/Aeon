package net.cc110.aeon.commands;

import java.io.*;
import java.net.*;
import java.util.*;
import net.cc110.aeon.*;
import net.cc110.aeon.util.*;
import de.btobastian.javacord.*;
import net.cc110.aeon.container.*;
import de.btobastian.javacord.entities.message.*;

public class CommandCat implements AsyncCommandExecutor
{
	private static final URL MEOW;
	
	static
	{
		URL meow;
		
		try
		{
			meow = new URL("http://random.cat/meow");
		}
		catch(Exception e)
		{
			Aeon.lastError = e;
			meow = null;
			e.printStackTrace();
		}
		
		MEOW = meow;
	}
	
	public String execute(DiscordAPI api, Message message, List<String> tokens)
	{
		try
		{
			InputStream in = MEOW.openConnection().getInputStream();
			
			Cat cat = Aeon.GSON.fromJson(new InputStreamReader(in), Cat.class);
			
			in.close();
			
			if(Aeon.config.enableEmbeds)
			{
				message.reply(null, Util.getEmbed(cat.file, null, message.getAuthor()));
				return null;
			}
			
			return cat.file;
		}
		catch(Exception e)
		{
			Aeon.lastError = e;
			e.printStackTrace();
			return "Error";
		}
	}
	
	public List<String> getAliases()
	{
		return Collections.unmodifiableList(Arrays.asList("cat", "pussy"));
	}
	
	public String getDescription()
	{
		return "Returns a random cat picture from random.cat";
	}
}
