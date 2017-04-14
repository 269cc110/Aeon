package net.cc110.aeon;

import de.btobastian.javacord.*;
import net.cc110.aeon.util.Util;

public class Config
{
	public String token = "", overlord = "145566025801269248", prefix = "!", wolframAppID = "", imgurClientID = "";
	public boolean debug = false, autosave = true, cacheColours = true,
			enableEmbeds = true, scanOwnMessages = false, asyncCustomCommands = true,
			caseInsensitiveCommands = true;
	public int autosaveInterval = 300, colourImageSize = 128;
	
	private String game = null, imageUploader = null;
	
	private boolean gse_initialised = false;
	
	void init(DiscordAPI api)
	{
		if(gse_initialised) return;
		
		if(game != null && game.trim().length() > 0) api.setGame(game);
		
		if(imageUploader != null && !imageUploader.isEmpty() && !Aeon.uploadHandler.setUploader(imageUploader))
		{
			System.err.println("Invalid image uploader \"" + imageUploader + "\"");
			System.err.println("Valid uploaders: "
					+ Util.concatenate(Util.getSortedList(Aeon.uploadHandler.getUploaders()), ", "));
		}
		
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
	
	public void setImageUploader(String id)
	{
		if(Aeon.uploadHandler.setUploader(id)) imageUploader = id;
	}
}
