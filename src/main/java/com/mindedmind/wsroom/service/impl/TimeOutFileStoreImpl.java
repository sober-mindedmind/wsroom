package com.mindedmind.wsroom.service.impl;

import static com.mindedmind.wsroom.util.RandomUtils.generateHash;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mindedmind.wsroom.service.FileStore;
import com.mindedmind.wsroom.util.RandomUtils;

@Service
public class TimeOutFileStoreImpl implements FileStore, Closeable
{	
	private static Logger LOGGER = LoggerFactory.getLogger(TimeOutFileStoreImpl.class);
	
	private static final String tempDir = System.getProperty("java.io.tmpdir");	
	
	private ConcurrentMap<String, InterruptedFile> tempFilesMap  = new ConcurrentHashMap<>();
	
	private DelayQueue<DelayedFile> expiredFilesQueue = new DelayQueue<>();
		
	private ExecutorService exec 					  = Executors.newSingleThreadExecutor();
	
	
	public TimeOutFileStoreImpl()
	{
		exec.execute(() -> {
			try
			{
				while (!Thread.currentThread().isInterrupted())
				{
					DelayedFile delayedFile = expiredFilesQueue.take();
					InterruptedFile interruptedFile = tempFilesMap.get(delayedFile.id);
					if (interruptedFile != null)
					{
						if (removeFile(interruptedFile.file) && tempFilesMap.containsKey(delayedFile.id))
						{
							tempFilesMap.remove(delayedFile.id);
							if (delayedFile.onExpiredTimeAction != null)
							{
								try
								{
									Arrays.stream(delayedFile.onExpiredTimeAction)
										.forEach(a -> a.onExpire(delayedFile.id , interruptedFile.file));
								}
								catch(Exception e)
								{
									LOGGER.info("The error occured on expired time action", e);
								}
								
							}
							LOGGER.info("The file has been deleted" + interruptedFile);		
						}
						else
						{
							LOGGER.warn("Can't delete the given file '{}', the file still exists", 
									interruptedFile.file.getName()); 
						}
					}
				}
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		});
	}
	
	private static class DelayedFile implements Delayed
	{		
		final long timeToDispose;
		
		final String id;
		
		ExpiredTimeAction[] onExpiredTimeAction;
				
		DelayedFile(String id, long timeToLiveMill, ExpiredTimeAction... onExpiredTimeAction)
		{
			this.id = id;
			this.timeToDispose = System.currentTimeMillis() + timeToLiveMill;
			this.onExpiredTimeAction = onExpiredTimeAction;
		}
		
		@Override public int compareTo(Delayed o)
		{
			return Long.compare(this.getDelay(TimeUnit.MILLISECONDS) , o.getDelay(TimeUnit.MILLISECONDS));
		}

		@Override public long getDelay(TimeUnit unit)
		{
			return unit.convert(this.timeToDispose - System.currentTimeMillis() , TimeUnit.MILLISECONDS);
		}
	}
	
	private static class InterruptedFile
	{
		final File file;
		volatile boolean interrupted;
		
		InterruptedFile(File file)
		{
			this.file = file;
		}
	}
	
	@Override public String save(InputStream is, 
								 String name, 
								 long timeToLive, 
								 ExpiredTimeAction... onExpiredTimeAction)
	{		
		String hash = generateHash();
		File tempFile = new File(tempDir + File.separatorChar + hash + File.separatorChar + name);
		File parentDir = tempFile.getParentFile();
		if (!parentDir.mkdirs())
		{
			throw new FileStoreException("Can't save file");
		}
		try 
		{
			Files.copy(is , tempFile.toPath());
		}
		catch (IOException e)
		{
			throw new FileStoreException("Could not read the file");
		}
		parentDir.deleteOnExit();
		tempFile.deleteOnExit();
		DelayedFile df = new DelayedFile(hash, timeToLive, onExpiredTimeAction);
		expiredFilesQueue.add(df);
		tempFilesMap.put(hash , new InterruptedFile(tempFile));
		return hash;
	}

	@Override public void transfer(String fileId, OutputStream os)
	{
		InterruptedFile interruptedFile = tempFilesMap.get(fileId);
		if (interruptedFile == null)
		{
			throw new FileStoreException(String.format("The file '%s' no longer exists", fileId));
		}				
		try(InputStream is = new BufferedInputStream(new FileInputStream(interruptedFile.file)))
		{			
			byte[] buff = new byte[8192];
			int read;				
			while(!interruptedFile.interrupted && (read = is.read(buff)) != -1)
			{
				os.write(buff, 0, read);
			}
		}
		catch (IOException e)
		{
			throw new FileStoreException("Could not copy the file to the output stream");
		}
	}

	@Override public File getFileInfo(String fileId)
	{
		InterruptedFile i = tempFilesMap.get(fileId);
		return i == null ? null : i.file;
	}
	
	@PreDestroy
	@Override public void close()
	{
		exec.shutdownNow();
	}
		
	@Override public void removeFile(String hash)
	{
		InterruptedFile interruptedFile = tempFilesMap.remove(hash);
		if (interruptedFile != null)
		{
			interruptedFile.interrupted = true;
		}		
		expiredFilesQueue.removeIf(d -> d.id.equals(hash));		
	}
	
	private boolean removeFile(File file)
	{
		return file.delete() && file.getParentFile().delete();		
	}
}
