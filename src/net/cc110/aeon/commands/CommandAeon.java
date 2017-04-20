package net.cc110.aeon.commands;

import java.util.*;
import java.util.stream.Collectors;

import net.cc110.aeon.*;
import net.cc110.aeon.util.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.*;
import de.btobastian.javacord.entities.message.*;

public class CommandAeon implements CommandExecutor
{
	public String execute(DiscordAPI api, Message message, List<String> tokens)
	{
		boolean auth = message.getAuthor().getId().equals(Aeon.config.overlord);
		
		int tokenCount = tokens.size();
		
		if(tokenCount > 1)
		{
			switch(tokens.get(1).toLowerCase())
			{
				case "disconnect":
					if(auth)
					{
						message.reply("Disconnecting :frowning:");
						Util.sleep(1000); // ensure message is sent
						api.disconnect();
						System.exit(0);
					}
					break;
					
				case "save":
					if(auth)
					{
						Aeon.save();
						message.reply("Save queued");
					}
					break;
					
				case "autosave":
					if(auth && tokenCount > 2)
					{
						switch(tokens.get(2).toLowerCase())
						{
							case "enable":
								Aeon.config.autosave = true;
								message.reply("Autosave enabled");
								break;
								
							case "disable":
								Aeon.config.autosave = false;
								message.reply("Autosave disabled");
								break;
								
							case "interval":
								if(tokenCount > 3)
								{
									int interval = Util.silentParseInt(tokens.get(3), -1);
									if(interval > 0)
									{
										Aeon.config.autosaveInterval = interval;
										message.reply("Set autosave interval -> " + interval);
									}
								}
								break;
						}
					}
					break;
					
				case "game":
					if(auth && tokenCount > 2)
					{
						switch(tokens.get(2).toLowerCase())
						{
							case "set":
								if(tokenCount > 3)
								{
									String game = tokens.get(3);
									Aeon.config.setGame(api, game);
									message.reply("Set game -> `" + game + "`");
								}
								break;
								
							case "clear":
								Aeon.config.setGame(api, null);
								message.reply("Game cleared");
								break;
						}
					}
					break;
					
				case "version":
					message.reply(Aeon.VERSION);
					break;
					
				case "nickname": // TODO fixme
					if(auth && tokenCount > 2)
					{
						switch(tokens.get(2).toLowerCase())
						{
							case "set":
								if(tokenCount > 3)
								{
									String name = tokens.get(3);
									message.getChannelReceiver().getServer().updateNickname(api.getYourself(), name);
									message.reply("Set nickname -> `" + name + "`");
								}
								break;
								
							case "clear":
								try
								{
									message.getChannelReceiver().getServer().updateNickname(api.getYourself(), null).get();
									message.reply("Nickname cleared");
								}
								catch(Exception e)
								{
									Aeon.lastError = e;
									e.printStackTrace();
								}
								break;
						}
					}
					break;
					
				case "overlord":
					User overlord = Util.silentGetFuture(api.getUserById(Aeon.config.overlord));
					if(overlord != null) message.reply("Current overlord: " + overlord.getMentionTag());
					break;
					
				case "error":
					if(auth)
					{
						message.reply(Aeon.lastError != null
								? Util.monoQuote(Util.getErrorString(Aeon.lastError))
								: "No previous error");
						Aeon.lastError = null;
					}
					break;
					
				case "cache":
					if(auth && tokenCount > 2)
					{
						switch(tokens.get(2).toLowerCase())
						{
							case "count":
								message.reply(Aeon.colourCache.size() + " cached colour images");
								break;
								
							case "clear":
								Aeon.colourCache.clear();
								message.reply("Colour image cache cleared");
								break;
								
							case "enable":
								Aeon.config.cacheColours = true;
								message.reply("Colour image caching enabled");
								break;
								
							case "disable":
								Aeon.config.cacheColours = false;
								message.reply("Colour image caching disabled");
								break;
						}
					}
					break;
					
				case "featurereq":
					message.reply("Temporarily disabled");
					break;
					
				case "embed":
					if(auth && tokenCount > 2)
					{
						switch(tokens.get(2).toLowerCase())
						{
							case "enable":
								Aeon.config.enableEmbeds = true;
								message.reply("Embedding enabled");
								break;
	
							case "disable":
								Aeon.config.enableEmbeds = false;
								message.reply("Embedding disabled");
								break;
						}
					}
					break;
					
				case "commands":
				case "help":
					StringBuilder builder = new StringBuilder();
					String prefix = Aeon.config.prefix;
					
					Aeon.commandHandler.getCommands().stream().sorted().forEach(t ->
					{
						builder.append("\n" + prefix + t);
						
						List<String> aliases = Aeon.commandHandler.getAliasesFor(t).stream()
								.sorted().map(u -> prefix + u).collect(Collectors.toList());
						
						if(!aliases.isEmpty()) builder.append(" [" + Util.concatenate(aliases) + "]");
						
						builder.append("\n");
						
						String description = Aeon.commandHandler.getDescription(t);
						if(description != null) builder.append("\t" + description + "\n");
					});
					
					message.reply(builder.length() == 0 ? "No commands registered. This is a bug!"
							: Util.monoQuote(builder.toString()));
					
					break;
			}
		}
		else message.reply("Aeon " + Aeon.VERSION);
		
		return null;
	}
	
	public List<String> getAliases()
	{
		return Collections.unmodifiableList(Arrays.asList("aeon"));
	}
	
	public String getDescription()
	{
		return "Manages the core bot";
	}
}
