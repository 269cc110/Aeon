package net.cc110.aeon.commands;

import java.io.*;
import java.net.*;
import java.util.*;
import net.cc110.aeon.*;
import de.btobastian.javacord.*;
import net.cc110.aeon.container.*;
import de.btobastian.javacord.entities.message.*;

public class CommandCatFact implements AsyncCommandExecutor
{
	private static final URL MEOW;
	
	static
	{
		URL meow;
		
		try
		{
			meow = new URL("http://catfacts-api.appspot.com/api/facts?number=1");
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
			
			CatFact cat = Aeon.GSON.fromJson(new InputStreamReader(in), CatFact.class);
			
			in.close();
			
			return cat.success ? cat.facts[0] : "Error";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "Error";
		}
	}
	
	public List<String> getAliases()
	{
		return Collections.unmodifiableList(Arrays.asList("catfact"));
	}
	
	public String getDescription()
	{
		return "Returns a random cat fact";
	}
}
