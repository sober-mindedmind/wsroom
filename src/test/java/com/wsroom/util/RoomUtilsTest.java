package com.wsroom.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mindedmind.wsroom.util.RoomUtils;

public class RoomUtilsTest
{

	@Test public void extractRoom_RoomNameIsCopiedCorrectly_True()
	{
		assertEquals(RoomUtils.extractRoom("/app/chat/Room1"), "Room1");
	}

	@Test public void extractPath_PathIsCopiedCorrectly_True()
	{
		assertEquals(RoomUtils.extractPath("/app/chat/Room1"), "/app/chat/");
	}
	
}
