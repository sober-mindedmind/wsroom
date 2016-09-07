package com.mindedmind.wsroom.util;

import org.junit.Test;

public class ImageUtilsTest
{
	
	@Test(expected = InvalidImageException.class) 
	public void resize_CorruptedImage_ExceptionThrown()
	{
		ImageUtils.resize(new byte[] { 1 } , 1 , 1);
		ImageUtils.resize(new byte[] { 1, 2, 3, 4, 42, 42, 4, 24, 24, 2, 42, 42, 2, 4, 2, 4 } , 10 , 10);
	}

}
