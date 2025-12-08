package com.ipd.controller;

import com.ipd.entity.IpdBed;
import com.ipd.entity.IpdRoom;
import com.ipd.repository.IpdRoomRepository;
import com.ipd.service.IpdRoomService;
import com.ipd.Exception.ResourceNotFoundException;
import com.ipd.dto.IpdRoomDto;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ipd")
public class IpdRoomController {

    @Autowired
    private IpdRoomService ipdRoomService;
    
    @Autowired
    private IpdRoomRepository ipdRoomRepo;


    // -----------------------------------------
    // CREATE ROOM (AUTO CREATES BEDS)
    // -----------------------------------------
    @PostMapping("/rooms")
    public ResponseEntity<?> createRoom(@RequestBody IpdRoom room) {
        try {
            IpdRoom saved = ipdRoomService.createRoom(room);
            IpdRoomDto dto = IpdRoomDto.fromEntity(saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    // -----------------------------------------
    // UPDATE ROOM (WITH BED LOGIC)
    // -----------------------------------------
    @PutMapping("/rooms/{roomId}")
    public ResponseEntity<?> updateRoom(@PathVariable Long roomId, @RequestBody IpdRoom updatedRoom) {
        try {
            IpdRoom saved = ipdRoomService.updateRoom(roomId, updatedRoom);
            IpdRoomDto dto = IpdRoomDto.fromEntity(saved);
            return ResponseEntity.ok(dto);
        } catch (IllegalStateException | ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    // -----------------------------------------
    // DELETE ROOM (ONLY IF NO OCCUPIED BEDS)
    // -----------------------------------------
    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long roomId) {
        try {
            ipdRoomService.deleteRoom(roomId);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException | ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    // -----------------------------------------
    // GET ALL AVAILABLE ROOMS (WITH FREE BEDS)
    // -----------------------------------------
    @GetMapping("/rooms/available")
    public ResponseEntity<List<IpdRoomDto>> getAvailableRooms() {

        List<IpdRoom> rooms = ipdRoomService.getAvailableRooms();

        List<IpdRoomDto> dtos = rooms.stream()
                .map(IpdRoomDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }


    // -----------------------------------------
    // GET FIRST AVAILABLE ROOM ID
    // -----------------------------------------
    @GetMapping("/rooms/availableid")
    public ResponseEntity<?> getAvailableRoomId() {
        try {
            Long id = ipdRoomService.findFirstAvailableRoomId();
            return ResponseEntity.ok(id);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @GetMapping("/rooms/available/bed/{roomId}")
    public ResponseEntity<?> getAvailableBedId(@PathVariable Long roomId) {
    	IpdRoom room = ipdRoomRepo.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
         IpdBed freeBed = room.getBeds().stream()
                .filter(b -> !b.isOccupied())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No beds available in this room"));
         return ResponseEntity.ok(freeBed.getId());
    }
    
}
