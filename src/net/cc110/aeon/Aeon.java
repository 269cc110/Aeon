package net.cc110.aeon;

import java.io.*;
import java.util.*;
import com.google.gson.*;
import net.cc110.aeon.util.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.utils.*;

public class Aeon
{
	public static final String VERSION = "0.6";
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
		boolean debug = Boolean.parseBoolean(System.getProperty("aeon.debug"));
		
		System.out.println("Aeon " + VERSION);
		
		File logFile = new File("logs/" + System.currentTimeMillis() + ".log");
		logFile.getParentFile().mkdirs();
		
		TeeOutputStream logOut = new TeeOutputStream(new BufferedOutputStream(new FileOutputStream(logFile)), STDERR);
		System.setErr(new PrintStream(logOut, true));
		
		File configFile = new File("config.json");
		
		if(!configFile.exists())
		{
			System.err.println("config.json not found, regenerating");
			
			writeJSON("config.json", new Config(), config.debug);
			
			logOut.close();
			
			return; // no token
		}
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "UTF-8")))
		{
			config = GSON.fromJson(reader, Config.class);
		}
		catch(FileNotFoundException | JsonParseException f)
		{
			if(debug) f.printStackTrace();

			System.err.println("Failed to open config.json, regenerating");
			
			writeJSON("config.json", new Config(), config.debug);
			
			logOut.close();
			
			return; // no token
		}
		
		config.debug |= debug;
		
		if(!config.debug) System.setErr(new PrintStream(logOut, true));
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("commands.json"), "UTF-8")))
		{
			customCommands = GSON.fromJson(reader, CustomCommands.class);
		}
		catch(FileNotFoundException | JsonParseException f)
		{
			if(debug) f.printStackTrace();
			
			customCommands = new CustomCommands();
			
			System.err.println("Failed to open commands.json, regenerating");
			
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
				if(config.autosave) save();
			}
		}).start();
		
		save();
		
		System.out.println("Connecting to Discord");
		
		ImplDiscordAPI api = new ImplDiscordAPI(pool);
		api.setToken(config.token, true);
		api.connect(new BotImpl());
	}
	
	public static void save()
	{
		save(config.debug);
	}
	
	public static void save(final boolean debug) // avoid blocking command save thread with config save
	{
		pool.getExecutorService().submit(() ->
		{
			synchronized(config)
			{
				writeJSON("config.json", config, debug);
			}
		});
		
		pool.getExecutorService().submit(() ->
		{
			synchronized(customCommands)
			{
				writeJSON("commands.json", customCommands, debug);
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
