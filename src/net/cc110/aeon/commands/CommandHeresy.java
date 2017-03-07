package net.cc110.aeon.commands;

import net.cc110.aeon.*;
import de.btobastian.sdcf4j.*;
import de.btobastian.javacord.*;
import de.btobastian.javacord.entities.message.*;

public class CommandHeresy implements CommandExecutor
{
	private static final String[] images = // more to come
	{
		"http://i1.kym-cdn.com/photos/images/facebook/000/706/073/4cf.jpg",
		"http://img14.deviantart.net/f698/i/2012/212/0/6/heresy_by_angerelic-d59ah2n.png",
		"http://i.imgur.com/FHPDCfG.jpg",
		"http://2static4.fjcdn.com/thumbnails/comments/Gt+trying+to+remove+sonico+s+headphones+_066fb623f985587cf6708bb9c2cba74a.jpg",
		"http://i.imgur.com/hHWGev8.jpg",
		"https://cdn.meme.am/cache/instances/folder142/500x/65637142.jpg",
		"http://i2.kym-cdn.com/photos/images/newsfeed/000/855/340/23b.jpg",
		"http://i2.kym-cdn.com/photos/images/facebook/000/706/085/0f0.jpg",
		"https://cdn-webimages.wimages.net/05156e1fe1260d988173727ac6fb9664f3853c-wm.jpg",
		"https://i.imgur.com/m59rdnz.jpg",
		"http://static2.fjcdn.com/thumbnails/comments/5767182+_1b7993e025f6d0c59874a9da6134fdcc.png",
		"https://i.imgflip.com/1h4f0l.jpg",
		"http://i1.kym-cdn.com/photos/images/original/000/584/521/0ae.jpg"
	};
	
	@Command(aliases = {"heresy"}, description = "Heresy")
	public String onCommand(DiscordAPI api, Message message)
	{
		return images[Aeon.RANDOM.nextInt(images.length)];
	}
}
