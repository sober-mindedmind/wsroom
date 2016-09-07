package com.mindedmind.wsroom.repository;


import java.util.Collection;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.mindedmind.wsroom.domain.Room;

public interface RoomRepository extends JpaRepository<Room, Long>
{	
	
	@Query("select r from Room r where r.name = :name")
	Room findByName(@Param("name") String name);
	
	@Query("delete from Room r where r.name = :name")
	void deleteByName(@Param("name") String name);
	
	/**
	 * Returns collection of rooms which are visible to the given user. From the resulted set will be excluded:
	 * - Rooms with allowed list of users which don't consist of the given user, and; 
	 * - those rooms to which the given user has been subscribed.
	 * 
	 * @param userName - the name of user
	 * @return collection of visible room
	 */
	@QueryHints(@QueryHint(name = "org.hibernate.annotations.QueryHints.READ_ONLY", value = "true"))
	@Query("select r from Room r left join r.allowedUsers allowedUser left join r.subscribedUsers subscribedUser"
			+ " where (subscribedUser is null "
			+ " or r.id not in (select r.id from Room r join r.subscribedUsers u where u.name = :name))"
			+ " and (allowedUser is null or allowedUser.name = :name)")	
	Collection<Room> allRoomsVisibleForUser(@Param("name") String userName);
	
	@Query("select r from Room r join r.subscribedUsers u where u.name = :userName")
	Collection<Room> findUserRooms(@Param("userName") String userName);
	
	//@Query("delete from Room r left join r.subscribedUsers u where r.name = :roomName and u.name = :userName")
	//void unsubscribeUser(@Param("roomName") String roomName, @Param("userName") String userName);
}
