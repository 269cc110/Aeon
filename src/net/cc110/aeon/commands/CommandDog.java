package net.cc110.aeon.commands;

import java.io.*;
import java.net.*;
import de.btobastian.sdcf4j.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.message.*;

public class CommandDog implements CommandExecutor
{
	private static final URL WOOF;
	
	static
	{
		URL woof;
		
		try
		{
			woof = new URL("http://random.dog/woof");
		}
		catch(Exception e)
		{
			woof = null;
			e.printStackTrace();
		}
		
		WOOF = woof;
	}
	
	@Command(aliases = {"dog"}, description = "Random dog pictures", async = true)
	public String onCommand(DiscordAPI api, Message message)
	{
		try
		{
			InputStream in = WOOF.openConnection().getInputStream();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			
			String result = reader.readLine();
			
			reader.close();
			
			return "http://random.dog/" + result;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "Error";
		}
	}
}
