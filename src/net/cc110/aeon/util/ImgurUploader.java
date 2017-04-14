package net.cc110.aeon.util;

import java.io.*;
import java.net.*;
import javax.imageio.*;
import javax.net.ssl.*;
import java.awt.image.*;
import net.cc110.aeon.*;
import net.cc110.aeon.container.ImgurBasic;

import java.nio.charset.*;
import org.apache.commons.codec.binary.*;

public class ImgurUploader implements ImageUploader
{
	private static final URL IMGUR_ENDPOINT;
	
	static
	{
		URL endpoint;
		
		try
		{
			endpoint = new URL("https://api.imgur.com/3/image.json");
		}
		catch(Exception e)
		{
			Aeon.lastError = e;
			endpoint = null;
			e.printStackTrace();
		}
		
		IMGUR_ENDPOINT = endpoint;
	}
	
	public String upload(BufferedImage image) throws Exception
	{
		String id = Aeon.config.imgurClientID;
		if(id == null || id.trim().length() == 0) return null;
		
		ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
		ImageIO.write(image, "PNG", imageStream);
		
		String base64 = Base64.encodeBase64URLSafeString(imageStream.toByteArray());
		
		HttpsURLConnection connection = (HttpsURLConnection)IMGUR_ENDPOINT.openConnection();
		
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Authorization", "Client-ID " + id);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		
		OutputStream out = connection.getOutputStream();
		
		out.write(("image=" + base64 + "\n").getBytes(StandardCharsets.UTF_8));
		out.flush();
		
		InputStream in = connection.getInputStream();
		
		ImgurBasic returned = Aeon.GSON.fromJson(new InputStreamReader(in), ImgurBasic.class);
		
		in.close();
		out.close();
		
		return returned.success ? (String)returned.data.get("link") : null;
	}
}
