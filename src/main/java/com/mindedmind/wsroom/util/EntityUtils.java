package com.mindedmind.wsroom.util;

import com.mindedmind.wsroom.service.impl.EntityNotFoundException;

public class EntityUtils
{
	public static void notNull(final Object o, final String message, final Object... values)
	{
		if (o == null)
		{
			throw new EntityNotFoundException(String.format(message , values));
		}
	}
}
