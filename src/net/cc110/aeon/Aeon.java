package net.cc110.aeon;

import java.io.*;
import java.util.*;
import com.google.gson.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.utils.*;

public class Aeon
{
	public static final String VERSION = "0.3";
	public static final String OVERLORD = "";
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
	
	public static ThreadPool pool = new ThreadPool();
	public static Config config;
	public static HashMap<String, String> deletedMessages = new HashMap<>();
	public static ServerCommands serverCommands;
	
	public static void main(String[] args) throws Exception
	{
		boolean debug = Boolean.parseBoolean(System.getProperty("debug"));
		
		System.out.println("Aeon " + VERSION);
		
		File configFile = new File("config.json");
		
		if(!configFile.exists())
		{
			System.out.println("config.json not found, regenerating");
			
			writeJSON("config.json", config, config.debug);
			
			return;
		}
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("config.json"), "UTF-8")))
		{
			config = GSON.fromJson(reader, Config.class);
		}
		catch(FileNotFoundException | JsonParseException f)
		{
			if(debug) f.printStackTrace();
			
			System.out.println("Failed to open config.json, regenerating");
			
			config = new Config();
			
			writeJSON("config.json", config, config.debug);
		}
		
		config.debug |= debug;
		
		if(debug) System.out.println("Debug mode on");
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("commands.json"), "UTF-8")))
		{
			serverCommands = GSON.fromJson(reader, ServerCommands.class);
		}
		catch(FileNotFoundException | JsonParseException f)
		{
			if(debug) f.printStackTrace();
			
			serverCommands = new ServerCommands();
			
			System.out.println("Failed to open commands.json, regenerating");
			
			writeJSON("commands.json", serverCommands, config.debug);
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> writeJSON("config.json", config, config.debug)));
		Runtime.getRuntime().addShutdownHook(new Thread(() -> writeJSON("commands.json", serverCommands, config.debug)));
		
		System.out.println("Connecting to Discord");
		
		DiscordAPI api = new ImplDiscordAPI(pool);
		api.setToken(config.token, true);
		api.connect(new BotImpl());
	}
	
	private static void writeJSON(String file, Object obj, boolean debug)
	{
		try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")))
		{
			GSON.toJson(obj, writer);
		}
		catch(Exception e)
		{
			if(debug) e.printStackTrace();
			
			System.err.println("Failed to write to " + file);
		}
	}
}
