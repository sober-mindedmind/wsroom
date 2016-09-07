package com.mindedmind.wsroom.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

public class ImageUtils
{
	public static boolean isValidImage(MultipartFile file, int maxSize)
	{		
		/* assume that the file can be valid if it is null or empty, because user could not specify the file */
		return file == null || file.isEmpty() ? true 
				: file.getSize() < maxSize && file.getContentType().equals("image/jpeg");
	}
	
	public static byte[] resize(byte[] img, int maxWidth, int maxHeight)
	{
		/* do not resize array if it null or empty i.e. allow null or empty arrays to be returned to caller */
		if (img == null || img.length == 0)
		{
			return img;
		}
		
		BufferedImage loadedImg;
		try
		{
			loadedImg = ImageIO.read(new ByteArrayInputStream(img));
		}
		catch (IOException e)
		{
			throw new InvalidImageException("Failed to read an image", e);
		}
		
		if (loadedImg == null)
		{
			throw new InvalidImageException("Is not an image");
		}
		
		if (loadedImg.getHeight() > maxWidth && loadedImg.getWidth() > maxHeight)
		{
			BufferedImage resizedImg = new BufferedImage(maxWidth , maxHeight , BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = resizedImg.createGraphics();
			try
			{
				graphics.drawImage(loadedImg, 0, 0, maxWidth, maxHeight, null);
			}
			finally
			{
				graphics.dispose();	
			}		
			ByteArrayOutputStream baosArrayOutputStream = new ByteArrayOutputStream();
			try
			{
				ImageIO.write(resizedImg , "jpg" , baosArrayOutputStream);
			}
			catch (IOException e)
			{
				throw new InvalidImageException("Failed to replace an image", e);
			}			
			return baosArrayOutputStream.toByteArray();
		}
		
		/* image has an appropriate size */
		return img;
		
	}
	
	
}
