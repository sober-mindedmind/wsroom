package com.mindedmind.wsroom.repository;


import java.util.Set;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.mindedmind.wsroom.domain.Room;
import com.mindedmind.wsroom.domain.User;

public interface RoomRepository extends JpaRepository<Room, Long>
{	
	@EntityGraph(value = "Room.roomWithOwner", type = EntityGraphType.LOAD)
	@Query("select r from Room r where r.name = ?1")
	Room findByName(String name);
	
	@Query("select distinct r from Room r left join fetch r.allowedUsers join r.owner o "
			+ " where r.name = :name and r.owner.name = :owner")
	Room findRoomOfOwner(@Param("name") String name, @Param("owner") String owner);
	
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
			+ " where (subscribedUser is null "
			+ " or r.id not in (select r.id from Room r join r.subscribedUsers u where u.name = :name))"
			+ " and (allowedUser is null or allowedUser.name = :name)")	
	Set<Room> allRoomsVisibleForUser(@Param("name") String userName);
	
	@QueryHints(@QueryHint(name = "org.hibernate.annotations.QueryHints.READ_ONLY", value = "true"))
	@Query("select r from Room r join r.subscribedUsers u where u.name = ?1")
	Set<Room> findUserRooms(String userName);
	
	@QueryHints(@QueryHint(name = "org.hibernate.annotations.QueryHints.READ_ONLY", value = "true"))
	@Query("select r from Room r join r.owner o on o.name = ?1")
	Set<Room> findRoomsWhereUserIsOwner(String owner);
	
	@Transactional
	@Modifying
	@Query("delete from Room r where r.owner = ?1")
	void deleteAllRoomsWhereUserIsOwner(User user);
}
