package net.cc110.aeon;

import java.io.*;
import java.util.*;
import com.google.gson.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.utils.*;

public class Aeon
{
	public static final String VERSION = "0.5";
	public static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting().serializeNulls().addSerializationExclusionStrategy(new GSONExclusionStrategy("gse")).create();
	public static final Random RANDOM = new Random();
	
	public static final PrintStream STDERR = System.err;
	
	public static ThreadPool pool = new ThreadPool();
	public static Config config;
	public static Hashtable<String, String> deletedMessages = new Hashtable<>();
	public static CustomCommands customCommands;
	
	public static void main(String[] args) throws Exception
	{
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
		
		if(!config.debug)
		{
			File logFile = new File("logs/" + System.currentTimeMillis() + ".log");
			logFile.getParentFile().mkdirs();
			
			final PrintStream logErr = new PrintStream(new BufferedOutputStream(new FileOutputStream(logFile)));
			System.setErr(logErr);
		}
		
		config.debug |= debug;
		
		if(config.debug) System.out.println("Debug mode on");
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("commands.json"), "UTF-8")))
		{
			customCommands = GSON.fromJson(reader, CustomCommands.class);
		}
		catch(FileNotFoundException | JsonParseException f)
		{
			if(debug) f.printStackTrace();
			
			customCommands = new CustomCommands();
			
			System.out.println("Failed to open commands.json, regenerating");
			
			writeJSON("commands.json", customCommands, config.debug);
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
			synchronized(customCommands)
			{
				writeJSON("commands.json", customCommands, config.debug);
			}
		}));
		
		new Thread(() ->
		{
			while(config.autosave)
			{
				Util.sleep(config.autosaveInterval * 1000);
				save();
			}
		}).start();
		
		pool.getExecutorService().submit(() -> save());
		
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
			synchronized(customCommands)
			{
				writeJSON("commands.json", customCommands, config.debug);
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
