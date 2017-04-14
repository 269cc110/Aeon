package net.cc110.aeon.util;

import java.util.*;
import java.awt.image.*;
import java.util.concurrent.*;

public class ImageUploadHandler
{
	private ConcurrentHashMap<String, ImageUploader> uploaders = new ConcurrentHashMap<>();
	private ImageUploader uploader;
	
	private boolean initialised = false;
	
	public void init()
	{
		if(initialised) return;
		
		registerUploader("imgur", new ImgurUploader());
		
		initialised = true;
	}
	
	public boolean setUploader(String uploaderID)
	{
		if(uploaderID != null)
		{
			uploaderID = uploaderID.trim().toLowerCase();
			
			synchronized(uploaders)
			{
				if(uploaderID.length() > 0 && uploaders.containsKey(uploaderID))
				{
					uploader = uploaders.get(uploaderID);
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void registerUploader(String id, ImageUploader uploader)
	{
		if(id != null && uploader != null)
		{
			id = id.trim().toLowerCase();
			
			synchronized(uploaders)
			{
				if(id.length() > 0) uploaders.put(id, uploader);
			}
		}
	}
	
	public Set<String> getUploaders()
	{
		return Collections.unmodifiableSet(uploaders.keySet());
	}
	
	public String upload(BufferedImage image) throws Exception
	{
		return uploader == null ? null : uploader.upload(image);
	}
}
