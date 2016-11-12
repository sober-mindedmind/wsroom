package com.mindedmind.wsroom.service.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.context.request.RequestContextListener;

public class TimeOutFileStoreImplTest
{

	@Test 
	public void save_ExpiredFileIsDeleted_FileDoesNotExist() throws InterruptedException, IOException
	{RequestContextListener
		byte[] testData = new byte[] {1, 2, 3};
		TimeOutFileStoreImpl tofs = new TimeOutFileStoreImpl();
		ByteArrayInputStream bais = new ByteArrayInputStream(testData);
		String id = tofs.save(bais , "newfile", 300);
		File f = tofs.getFileInfo(id);
		FileInputStream fis = new FileInputStream(f);		
		Thread.sleep(100);
		assertTrue(f.exists());
		byte[] buff = new byte[3];
		fis.read(buff);
		fis.close();
		assertArrayEquals(buff, testData);
		Thread.sleep(400);
		assertFalse(f.exists());
		tofs.close();
		
	}

	@Test 
	public void transfer_FileContentIsTransferedIntoOutputStream_OutputStreamContainsFilesData()
	{
		byte[] testData = new byte[] {1, 2, 3, 4, 5};
		TimeOutFileStoreImpl tofs = new TimeOutFileStoreImpl();
		ByteArrayInputStream bais = new ByteArrayInputStream(testData);
		String id = tofs.save(bais , "newfile", 2000);
		tofs.close();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		tofs.transfer(id , baos);
		assertArrayEquals(baos.toByteArray(), testData);
	}

	@Test
	public void removeFile_FileDownloadProcessStoped_FileNoLongerExists() throws IOException, InterruptedException
	{
		TimeOutFileStoreImpl tofs = new TimeOutFileStoreImpl();		
		String hash = tofs.save(new ByteArrayInputStream(new byte[10000]) , "file" , 10000);
		OutputStream os = mock(OutputStream.class);
		Mockito.doAnswer(i -> {tofs.removeFile(hash); return null;})
			.when(os).write(Mockito.any() , Mockito.anyInt() , Mockito.anyInt());
		tofs.transfer(hash , os);
		verify(os, times(1)).write(Mockito.any() , Mockito.anyInt() , Mockito.anyInt());
		assertNull(tofs.getFileInfo(hash));		
		tofs.close();
	}
	
}
