package net.cc110.aeon.commands;

import java.io.*;
import java.net.*;
import java.util.*;
import net.cc110.aeon.*;
import net.cc110.aeon.util.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.message.*;

public class CommandDog implements AsyncCommandExecutor
{
	private static final URL WOOF;
	
	static
	{
		URL woof;
		
		try
		{
			woof = new URL("https://random.dog/woof");
		}
		catch(Exception e)
		{
			Aeon.lastError = e;
			woof = null;
			e.printStackTrace();
		}
		
		WOOF = woof;
	}
	
	public String execute(DiscordAPI api, Message message, List<String> tokens)
	{
		try
		{
			InputStream in = WOOF.openConnection().getInputStream();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			
			String result = reader.readLine();
			
			reader.close();
			
			if(Aeon.config.enableEmbeds)
			{
				message.reply(null, Util.getEmbed("https://random.dog/" + result, null, message.getAuthor()));
				return null;
			}
			
			return "https://random.dog/" + result;
		}
		catch(Exception e)
		{
			Aeon.lastError = e;
			e.printStackTrace();
			
			return "Unable to find valid certification path to requested target";
		}
	}
	
	public List<String> getAliases()
	{
		return Collections.unmodifiableList(Arrays.asList("dog", "pupper"));
	}
	
	public String getDescription()
	{
		return "Returns a random dog picture from random.dog";
	}
}
