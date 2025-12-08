package com.ipd.service;

import java.util.List;

import org.apache.coyote.BadRequestException;

import com.ipd.entity.IpdRoom;

public interface IpdRoomService {
	
	public IpdRoom createRoom(IpdRoom room) throws BadRequestException;

	IpdRoom updateRoom(Long roomId, IpdRoom updatedRoom);

	void deleteRoom(Long roomId);

	List<IpdRoom> getAvailableRooms();

	Long findFirstAvailableRoomId() throws BadRequestException;


}
