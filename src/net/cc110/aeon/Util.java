package net.cc110.aeon;

import java.util.*;
import de.btobastian.javacord.entities.*;
import de.btobastian.javacord.entities.permissions.*;

public class Util
{
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
				else if(c == '#') break;
				else builder.append(c);
			}
		}
		
		if(builder.length() != 0) result.add(builder.toString());
		
		return result;
	}
	
	public static boolean hasPermission(User user, Server server, PermissionType type)
	{
		for(Role role : user.getRoles(server))
		{
			if(role.getPermissions().getState(type) == PermissionState.ALLOWED) return true;
		}
		
		return false;
	}
	
	public static void sleep(long millis)
	{
		try { Thread.sleep(millis); } catch(Exception e) {}
	}
}
