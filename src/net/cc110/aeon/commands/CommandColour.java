package net.cc110.aeon.commands;

import java.util.*;
import java.awt.image.*;
import net.cc110.aeon.*;
import net.cc110.aeon.util.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.*;
import de.btobastian.javacord.entities.message.*;
import de.btobastian.javacord.entities.message.embed.*;

public class CommandColour implements CommandExecutor
{
	public String execute(DiscordAPI api, Message message, java.util.List<String> tokens)
	{
		if(tokens.size() > 1)
		{
			String hexColour = tokens.get(1).toLowerCase();

			if(hexColour.startsWith("0x")) hexColour = hexColour.substring(2);
			else if(hexColour.startsWith("#")) hexColour = hexColour.substring(1);
			
			int hexLength = hexColour.length();
			
			if(hexLength < 6) hexColour = Util.repeat("0", 6 - hexLength) + hexColour;
			else if(hexLength > 6) hexColour = hexColour.substring(hexLength - 6);
			
			int colour = 0;
			
			try
			{
				colour = Integer.parseInt(hexColour, 16);
			}
			catch(NumberFormatException e)
			{
				return "Invalid hex string";
			}
			
			int size = Aeon.config.colourImageSize;
			
			BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
			
			for(int y = 0; y < size; y++)
			{
				for(int x = 0; x < size; x++)
				{
					image.setRGB(x, y, colour);
				}
			}
			
			try
			{
				String imageLink = null;
				
				synchronized(Aeon.config)
				{
					synchronized(Aeon.colourCache)
					{
						boolean cached = Aeon.config.cacheColours && Aeon.colourCache.containsKey(hexColour);
						imageLink = cached ? Aeon.colourCache.get(hexColour) : Aeon.uploadHandler.upload(image);
						
						if(Aeon.config.cacheColours && !cached) Aeon.colourCache.put(hexColour, imageLink);
					}
				}
				
				if(imageLink != null)
				{
					User author = message.getAuthor();
					
					message.reply(null, new EmbedBuilder()
							.setFooter("Aeon " + Aeon.VERSION)
							.setAuthor(author.getName(), null, author.getAvatarUrl().toString())
							.setImage(imageLink));
				}
			}
			catch(Exception e)
			{
				Aeon.lastError = e;
				e.printStackTrace();
				
				return "Error";
			}
		}
		
		return null;
	}
	
	public List<String> getAliases()
	{
		return Collections.unmodifiableList(Arrays.asList("colour", "color"));
	}
	
	public String getDescription()
	{
		return "Returns an image of a colour in hex";
	}
}
