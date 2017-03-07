package net.cc110.aeon;

import de.btobastian.javacord.*;

public class Config
{
	public String token = "", prefix = "!", overlord = "145566025801269248";
	public boolean debug = false, autosave = true;
	public int autosaveInterval = 300;
	
	private String game = null;
	
	private boolean gse_initialised = false;
	
	void init(DiscordAPI api)
	{
		if(gse_initialised) return;
		
		if(game != null) api.setGame(game);
		
		gse_initialised = true;
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
