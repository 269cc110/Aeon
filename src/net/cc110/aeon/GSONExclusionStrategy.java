package net.cc110.aeon;

import com.google.gson.*;

public class GSONExclusionStrategy implements ExclusionStrategy
{
	private final String exclusionPrefix;
	
	public GSONExclusionStrategy(String exclusionPrefix)
	{
		this.exclusionPrefix = exclusionPrefix + "_";
	}
	
	public boolean shouldSkipClass(Class<?> clazz)
	{
		return clazz.getName().startsWith(exclusionPrefix);
	}
	
	public boolean shouldSkipField(FieldAttributes f)
	{
		return f.getName().startsWith(exclusionPrefix);
	}
}
