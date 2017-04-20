package net.cc110.aeon;

import java.util.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.message.*;

public interface CommandExecutor
{
	default void onRegistered(CommandHandler handler) {}
	String execute(DiscordAPI api, Message message, List<String> tokens);
	List<String> getAliases();
	String getDescription();
}
