package net.cc110.aeon.commands;

import java.util.*;
import net.cc110.aeon.*;
import net.cc110.aeon.util.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.message.*;

public class CommandTwoInchBrush implements AsyncCommandExecutor
{
	public String execute(DiscordAPI api, Message message, List<String> tokens)
	{
		int tokenCount = tokens.size();
		int index = tokenCount < 2 ? -1 : Util.silentParseInt(tokens.get(1), -1);
		
		String url =  "http://www.twoinchbrush.com/images/painting"
			+ ((index < 1 || (index > 129 && index < 282) || index > 411) ? Aeon.RANDOM.nextInt(129) + 282
					: (index < 282 ? index + 281 : index)) + ".png";
		
		if(Aeon.config.enableEmbeds)
		{
			message.reply(null, Util.getEmbed(url, null, message.getAuthor()));
			return null;
		}
		
		return url;
	}
	
	public List<String> getAliases()
	{
		return Collections.unmodifiableList(Arrays.asList("twoinchbrush"));
	}
	
	public String getDescription()
	{
		return "Returns a random painting by Bob Ross from the Joy of Painting";
	}
}
