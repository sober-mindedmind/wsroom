package com.mindedmind.wsroom.service;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileStore
{	
	interface ExpiredTimeAction
	{
		void onExpire(String hash, File fileInfo);
	}
	
	String save(InputStream is, String name, long timeToLive, ExpiredTimeAction... onExpiredTimeAction);
	
	void transfer(String fileId, OutputStream os);
	
	File getFileInfo(String fileId);
	
	void removeFile(String hash);
}
