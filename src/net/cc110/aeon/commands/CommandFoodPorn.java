package net.cc110.aeon.commands;

import java.io.InputStream;
import java.net.URL;
import java.util.*;
import net.cc110.aeon.*;
import net.cc110.aeon.util.Util;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.message.*;

public class CommandFoodPorn implements AsyncCommandExecutor
{
	private static final URL FOOD;
	
	static
	{
		URL food;
		
		try
		{
			food = new URL("http://foodporndaily.com/");
		}
		catch(Exception e)
		{
			Aeon.lastError = e;
			food = null;
			e.printStackTrace();
		}
		
		FOOD = food;
	}
	
	public String execute(DiscordAPI api, Message message, List<String> tokens)
	{
		try
		{
			InputStream in = FOOD.openConnection().getInputStream();
			
			Scanner scanner = new Scanner(in);
			StringBuilder builder = new StringBuilder();
			
			while(scanner.hasNextLine()) builder.append(scanner.nextLine());
			
			scanner.close();
			
			String content = builder.toString();
			int begin = content.indexOf("mainPhoto\" src=\"") + 16;
			int end = content.indexOf('\"', begin);
			
			String link = content.substring(begin, end);
			
			begin = end + 7;
			end = content.indexOf('\"', begin);
			
			String description = content.substring(begin, end);
			
			if(Aeon.config.enableEmbeds)
			{
				message.reply(null, Util.getEmbed(link, description, message.getAuthor()));
				return null;
			}
			
			return content;
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
		return Collections.unmodifiableList(Arrays.asList("foodporn"));
	}
}
