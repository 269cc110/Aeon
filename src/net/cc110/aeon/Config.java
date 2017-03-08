package net.cc110.aeon;

import de.btobastian.javacord.*;
import de.btobastian.sdcf4j.handler.*;

public class Config
{
	public String token = "", overlord = "145566025801269248";
	public boolean debug = false, autosave = true;
	public int autosaveInterval = 300;
	
	private String prefix = "!", game = null;
	
	private boolean gse_initialised = false;
	
	void init(DiscordAPI api, JavacordHandler handler)
	{
		if(gse_initialised) return;
		
		if(prefix != null) handler.setDefaultPrefix(prefix);
		if(game != null) api.setGame(game);
		
		gse_initialised = true;
	}
	
	public String getPrefix()
	{
		return prefix;
	}
	
	public void setPrefix(JavacordHandler handler, String prefix)
	{
		handler.setDefaultPrefix(this.prefix = prefix);
	}
	
	public String getGame()
	{
		return game;
	}
	
	public void setGame(DiscordAPI api, String game)
	{
		api.setGame(this.game = game);
	}
}
