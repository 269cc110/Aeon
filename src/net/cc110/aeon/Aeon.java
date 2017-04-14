package net.cc110.aeon;

import java.io.*;
import java.util.*;
import com.google.gson.*;
import java.lang.reflect.*;
import net.cc110.aeon.util.*;
import java.util.concurrent.*;
import de.btobastian.javacord.*;
import com.google.gson.reflect.*;
import de.btobastian.javacord.utils.*;

public class Aeon
{
	public static final String VERSION = "0.9";
	public static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting().serializeNulls().addSerializationExclusionStrategy(new GSONExclusionStrategy("gse")).create();
	public static final Random RANDOM = new Random();
	
	public static final PrintStream STDERR = System.err;
	
	public static ThreadPool pool = new ThreadPool();
	public static Config config;
	public static DeletedMessages deletedMessages = new DeletedMessages();
	public static CommandHandler commandHandler = new CommandHandler();
	public static CustomCommands customCommands;
	public static ImageUploadHandler uploadHandler = new ImageUploadHandler();
	public static ConcurrentHashMap<String, String> colourCache = new ConcurrentHashMap<>();
	
	public static Throwable lastError = null;
	
	public static void main(String[] args) throws Exception
	{
		boolean debug = Boolean.parseBoolean(System.getProperty("aeon.debug"));
		
		System.out.println("Aeon " + VERSION);
		
		File logFile = new File("logs/" + System.currentTimeMillis() + ".log");
		logFile.getParentFile().mkdirs();
		
		TeeOutputStream logOut = new TeeOutputStream(new BufferedOutputStream(new FileOutputStream(logFile)), STDERR);
		System.setErr(new PrintStream(logOut, true));
		
		Thread.setDefaultUncaughtExceptionHandler((t, e) ->
		{
			lastError = e;
			e.printStackTrace();
		});
		
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
			
			configFile.renameTo(new File(configFile.getCanonicalPath() + "_" + System.currentTimeMillis()));
			
			writeJSON("config.json", new Config(), config.debug);
			
			logOut.close();
			
			return; // no token
		}
		
		config.debug |= debug;
		
		if(!config.debug) System.setErr(new PrintStream(logOut, true));
		
		File commandFile = new File("commands.json");
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(commandFile), "UTF-8")))
		{
			customCommands = GSON.fromJson(reader, CustomCommands.class);
		}
		catch(FileNotFoundException | JsonParseException f)
		{
			if(debug) f.printStackTrace();
			
			customCommands = new CustomCommands();
			
			System.err.println("Failed to open commands.json, regenerating");
			
			commandFile.renameTo(new File(commandFile.getCanonicalPath() + "_" + System.currentTimeMillis()));
			
			writeJSON("commands.json", customCommands, config.debug);
			
			lastError = f;
		}
		
		File cacheFile = new File("colour_cache.json");
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(cacheFile), "UTF-8")))
		{
			colourCache = readJSONConcurrentHashMap(reader, String.class, String.class);
		}
		catch(FileNotFoundException | JsonParseException f)
		{
			if(debug) f.printStackTrace();
			
			colourCache = new ConcurrentHashMap<>();
			
			System.err.println("Failed to open colour_cache.json, regenerating");
			
			cacheFile.renameTo(new File(cacheFile.getCanonicalPath() + "_" + System.currentTimeMillis()));
			
			writeJSON("colour_cache.json", colourCache, config.debug);
			
			lastError = f;
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
		
		Runtime.getRuntime().addShutdownHook(new Thread(() ->
		{
			synchronized(colourCache)
			{
				writeJSON("colour_cache.json", colourCache, config.debug);
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
		
		commandHandler = new CommandHandler();
		deletedMessages = new DeletedMessages();
		
		System.out.println("Connecting to Discord");
		
		ImplDiscordAPI api = new ImplDiscordAPI(pool);
		api.setToken(config.token, true);
		api.setWaitForServersOnStartup(false); // workaround for Javacord #42
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
		
		pool.getExecutorService().submit(() ->
		{
			synchronized(colourCache)
			{
				writeJSON("colour_cache.json", colourCache, debug);
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
			lastError = e;
			
			e.printStackTrace();
			
			if(!debug) STDERR.println("Failed to write to " + file);
		}
	}
	
	private static <K, V> ConcurrentHashMap<K, V> readJSONConcurrentHashMap(Reader reader, Class<K> k, Class<V> v) throws Exception
	{
		Type tableType = new TypeToken<ConcurrentHashMap<K, V>>(){}.getType();
		
		return GSON.fromJson(reader, tableType);
	}
}
