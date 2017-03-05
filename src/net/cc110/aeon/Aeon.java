package net.cc110.aeon;

import java.io.*;
import java.util.*;
import com.google.gson.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.utils.*;

public class Aeon
{
	public static final String VERSION = "0.4";
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
	public static final Random RANDOM = new Random();
	
	public static ThreadPool pool = new ThreadPool();
	public static Config config;
	public static Hashtable<String, String> deletedMessages = new Hashtable<>();
	public static ServerCommands serverCommands;
	
	public static void main(String[] args) throws Exception
	{
		//System.setProperty("org.apache.logging.log4j.simplelog.StatusLogger.level", "TRACE");
		
		boolean debug = Boolean.parseBoolean(System.getProperty("debug"));
		
		System.out.println("Aeon " + VERSION);
		
		File configFile = new File("config.json");
		
		if(!configFile.exists())
		{
			System.out.println("config.json not found, regenerating");
			
			writeJSON("config.json", new Config(), config.debug);
			
			return; // no token
		}
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "UTF-8")))
		{
			config = GSON.fromJson(reader, Config.class);
		}
		catch(FileNotFoundException | JsonParseException f)
		{
			if(debug) f.printStackTrace();
			
			System.out.println("Failed to open config.json, regenerating");
			
			writeJSON("config.json", new Config(), config.debug);
			
			return; // no token
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
		
		Runtime.getRuntime().addShutdownHook(new Thread(() ->
		{
			synchronized(config)
			{
				writeJSON("config.json", config, config.debug);
			}
		}));
		
		Runtime.getRuntime().addShutdownHook(new Thread(() ->
		{
			synchronized(serverCommands)
			{
				writeJSON("commands.json", serverCommands, config.debug);
			}
		}));
		
		new Thread(() ->
		{
			while(config.autosave)
			{
				Util.sleep(config.autosaveInterval * 1000);
				Aeon.save();
			}
		}).start();
		
		System.out.println("Connecting to Discord");
		
		ImplDiscordAPI api = new ImplDiscordAPI(pool);
		api.setToken(config.token, true);
		api.connect(new BotImpl());
	}
	
	public static void save()
	{
		pool.getExecutorService().submit(() ->
		{
			synchronized(config)
			{
				writeJSON("config.json", config, config.debug);
			}
		});
		
		pool.getExecutorService().submit(() ->
		{
			synchronized(serverCommands)
			{
				writeJSON("commands.json", serverCommands, config.debug);
			}
		});
	}
	
	private static void writeJSON(String file, Object obj, boolean debug)
	{
		try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")))
		{
			GSON.toJson(obj, writer);
			writer.newLine();
		}
		catch(Exception e)
		{
			if(debug) e.printStackTrace();
			
			System.err.println("Failed to write to " + file);
		}
	}
}
