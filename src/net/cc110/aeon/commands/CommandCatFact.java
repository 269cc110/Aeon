package net.cc110.aeon.commands;

import java.io.*;
import java.net.*;
import net.cc110.aeon.*;
import de.btobastian.sdcf4j.*;
import de.btobastian.javacord.*;
import net.cc110.aeon.container.*;
import de.btobastian.javacord.entities.message.*;

public class CommandCatFact implements CommandExecutor
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
			meow = null;
			e.printStackTrace();
		}
		
		MEOW = meow;
	}
	
	@Command(aliases = {"catfact"}, description = "Random cat facts", async = true)
	public String onCommand(DiscordAPI api, Message message)
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
}
