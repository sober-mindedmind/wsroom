package com.mindedmind.wsroom.util;

import java.util.Random;

public final class RandomUtils
{
	private RandomUtils()
	{}
	
	public static String generateHash()
	{
		Random rand = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 50; i++)
		{			
			sb.append((char)(rand.nextInt(26) + 97));
		}
		return sb.toString();
	}
}
