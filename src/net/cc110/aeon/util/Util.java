package net.cc110.aeon.util;

import java.util.*;
import net.cc110.aeon.*;
import java.util.regex.*;
import java.util.concurrent.*;
import de.btobastian.javacord.entities.*;
import de.btobastian.javacord.entities.permissions.*;
import de.btobastian.javacord.entities.message.embed.*;

public class Util
{
	public static final Pattern MENTION_PATTERN = Pattern.compile("<@!?[0-9]{17,18}>");
	
	public static List<String> tokenise(String in)
	{
		ArrayList<String> result = new ArrayList<>();
		
		StringBuilder builder = new StringBuilder();
		
		boolean inQuote = false;
		boolean escape = false;
		
		for(char c : in.toCharArray())
		{
			if(escape)
			{
				escape = false;
				builder.append(c);
			}
			else
			{
				if(Character.isWhitespace(c))
				{
					if(inQuote) builder.append(c);
					else if(builder.length() != 0)
					{
						result.add(builder.toString());
						builder.setLength(0);
					}
				}
				else if(c == '"')
				{
					inQuote = !inQuote;
					
					if(builder.length() != 0)
					{
						result.add(builder.toString());
						builder.setLength(0);
					}
				}
				else if(c == '\\') escape = true;
				else builder.append(c);
			}
		}
		
		if(builder.length() != 0) result.add(builder.toString());
		
		return result;
	}
	
	public static boolean hasPermission(User user, Server server, PermissionType type, boolean orAdmin)
	{
		String id = user.getId();
		
		if(id.equals(Aeon.config.overlord) || (orAdmin && id.equals(server.getOwnerId()))) return true;
		
		for(Role role : user.getRoles(server))
		{
			Permissions perms = role.getPermissions();
			
			if(perms.getState(type) == PermissionState.ALLOWED
					|| (orAdmin && perms.getState(PermissionType.ADMINISTATOR) == PermissionState.ALLOWED))
				return true;
		}
		
		return false;
	}
	
	public static boolean hasPermission(User user, Server server, PermissionType type)
	{
		return hasPermission(user, server, type, true);
	}
	
	public static void sleep(long millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch(Exception e)
		{
			Aeon.lastError = e;
		}
	}
	
	public static int silentParseInt(String toParse, int error)
	{
		try
		{
			return Integer.parseInt(toParse);
		}
		catch(Exception e)
		{
			Aeon.lastError = e;
		}
		
		return error;
	}
	
	public static String getIDFromMention(String mention)
	{
		return MENTION_PATTERN.matcher(mention).matches()
				? mention.substring(mention.charAt(2) == '!' ? 3 : 2, mention.length() - 1) : null;
	}
	
	public static <T> T silentGetFuture(Future<T> future, long millis)
	{
		try
		{
			return future.get(millis, TimeUnit.MILLISECONDS);
		}
		catch(Exception e)
		{
			Aeon.lastError = e;
			return null;
		}
	}
	
	public static <T> T silentGetFuture(Future<T> future)
	{
		try
		{
			return future.get();
		}
		catch(Exception e)
		{
			Aeon.lastError = e;
			return null;
		}
	}
	
	public static String monoQuote(String in)
	{
		String quote = in.indexOf('\n') != -1 ? "\n```" : "`";
		return quote + in + quote;
	}
	
	public static String getErrorString(Throwable error)
	{
		String result = error.toString();
		
		Throwable cause = error.getCause();
		if(cause != null) result += "\n\n\tCaused by: " + getErrorString(cause);
		
		return result;
	}
	
	public static String repeat(String toRepeat, int count)
	{
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < count; i++)
		{
			builder.append(toRepeat);
		}
		
		return builder.toString();
	}
	
	public static EmbedBuilder getEmbed(String imageURL, String description, User user)
	{
		EmbedBuilder result = new EmbedBuilder().setFooter("Aeon " + Aeon.VERSION);
		
		if(imageURL != null) result.setImage(imageURL);
		if(description != null) result.setDescription(description);
		if(user != null) result.setAuthor(user.getName(), null, user.getAvatarUrl().toString());
		
		return result;
	}
	
	public static String concatenate(List<String> in, String separator)
	{
		StringBuilder result = new StringBuilder();
		boolean first = true;
		
		for(String s : in)
		{
			if(!first) result.append(separator);
			
			result.append(s);
			
			first = false;
		}
		
		return result.toString();
	}
	
	public static String concatenate(List<String> in)
	{
		return concatenate(in, ", ");
	}
}
