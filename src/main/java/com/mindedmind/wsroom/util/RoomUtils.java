package com.mindedmind.wsroom.util;

public final class RoomUtils
{
	private RoomUtils()
	{}
	
	public static String extractRoom(String dest)
	{
		return dest.substring(dest.lastIndexOf('/') + 1);
	}
	
	public static String extractPath(String dest)
	{
		return dest.substring(0, dest.lastIndexOf('/') + 1);
	}
}
