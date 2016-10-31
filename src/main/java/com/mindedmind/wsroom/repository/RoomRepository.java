package com.mindedmind.wsroom.repository;


import java.util.Set;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.transaction.annotation.Transactional;

import com.mindedmind.wsroom.domain.Room;
import com.mindedmind.wsroom.domain.User;

public interface RoomRepository extends JpaRepository<Room, Long>
{	
	@EntityGraph(value = "Room.roomWithOwner", type = EntityGraphType.LOAD)
	@Query("select r from Room r where r.name = ?1")
	Room findByName(String name);
		
	/**
	 * Returns collection of rooms which are visible to the given user. From the resulted set will be excluded:
	 * - Rooms with allowed list of users which don't contain the given user, and; 
	 * - those rooms to which the given user has been subscribed.
	 * 
	 * @param userName - the name of user
	 * @return collection of visible room
	 */
	@QueryHints(@QueryHint(name = "org.hibernate.annotations.QueryHints.READ_ONLY", value = "true"))
	@Query("select r from Room r left join r.allowedUsers allowedUser left join r.subscribedUsers subscribedUser"
			+ " left join r.bannedUsers b"
			+ " where (subscribedUser is null "
			+ " or r.id not in (select r.id from Room r join r.subscribedUsers u where u.name = ?1))"
			+ " and (allowedUser is null or allowedUser.name = ?1) "
			+ " and (b.name is null or b.name != ?1)")
	Set<Room> allRoomsVisibleForUser(String userName);
	
	@QueryHints(@QueryHint(name = "org.hibernate.annotations.QueryHints.READ_ONLY", value = "true"))
	@Query("select r from Room r join r.subscribedUsers u left join r.bannedUsers b "
			+ " where (b.name is null or b.name != ?1) and u.name = ?1")
	Set<Room> findUserRooms(String userName);
	
	@QueryHints(@QueryHint(name = "org.hibernate.annotations.QueryHints.READ_ONLY", value = "true"))
	@Query("select r from Room r join r.owner o on o.name = ?1")
	Set<Room> findRoomsWhereUserIsOwner(String owner);
	
	@Transactional
	@Modifying
	@Query("delete from Room r where r.owner = ?1")
	void deleteAllRoomsWhereUserIsOwner(User user);
	
	@Query("select (count(*) > 0) from Room r where r.name = ?1 and r.owner.name = ?2")
	boolean isRoomOwner(String room, String user);

	@Query("select (count(*) > 0) from Room r join r.bannedUsers b where r.name = ?2 and b.name = ?1")
	boolean isBanned(String userName, String roomName);
}
