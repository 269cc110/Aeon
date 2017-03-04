package net.cc110.aeon.commands;

import java.io.*;
import java.net.*;
import net.cc110.aeon.*;
import de.btobastian.sdcf4j.*;
import de.btobastian.javacord.*;
import net.cc110.aeon.container.*;
import de.btobastian.javacord.entities.message.*;

public class CommandCat implements CommandExecutor
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
			meow = null;
			e.printStackTrace();
		}
		
		MEOW = meow;
	}
	
	@Command(aliases = {"cat"}, description = "Random cat pictures", async = true)
	public String onCommand(DiscordAPI api, Message message)
	{
		try
		{
			InputStream in = MEOW.openConnection().getInputStream();
			
			Cat cat = Aeon.GSON.fromJson(new InputStreamReader(in), Cat.class);
			
			in.close();
			
			return cat.file;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "Error";
		}
	}
}
