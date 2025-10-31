 package com.ipd.controller;

import com.ipd.entity.IpdHospital;
import com.user.entity.User;
import com.ipd.entity.IpdRoom;
import com.user.repository.UserRepository;
import com.ipd.Exception.ResourceNotFoundException;
import com.ipd.repository.IpdHospitalRepository;
import com.ipd.repository.IpdRoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private IpdRoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private IpdHospitalRepository hospitalRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @GetMapping("/bed-summary")
    public ResponseEntity<Map<String, Object>> getBedSummary() {
        User currentUser = getCurrentUser();
        IpdHospital hospital = hospitalRepository.findById(currentUser.getIpdHospitalId()).orElseThrow(()->new ResourceNotFoundException("Hospital Not Found for this User"));
//        IpdHospital hospital = currentUser.getHospital();

        List<IpdRoom> rooms = roomRepository.findByHospital(hospital);

        int totalRooms = rooms.size();
        int totalBeds = rooms.stream().mapToInt(IpdRoom::getTotalBeds).sum();
        int occupiedBeds = rooms.stream().mapToInt(IpdRoom::getOccupiedBeds).sum();
        int availableBeds = totalBeds - occupiedBeds;

        Map<String, Object> response = new HashMap<>();
        response.put("totalRooms", totalRooms);
        response.put("totalBeds", totalBeds);
        response.put("occupiedBeds", occupiedBeds);
        response.put("availableBeds", availableBeds);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/bed-summary-by-room")
    public ResponseEntity<List<Map<String, Object>>> getBedSummaryByRoom() {
        User currentUser = getCurrentUser();
        
        IpdHospital hospital = hospitalRepository.findById(currentUser.getIpdHospitalId()).orElseThrow(()->new ResourceNotFoundException("Hospital Not Found for this User"));
        
//        Hospital hospital = currentUser.getHospital();

        List<IpdRoom> rooms = roomRepository.findByHospital(hospital);

        List<Map<String, Object>> response = new ArrayList<>();

        for (IpdRoom room : rooms) {
            Map<String, Object> roomData = new HashMap<>();
            roomData.put("roomId", room.getId());
            roomData.put("roomNumber", room.getRoomNumber());
            roomData.put("totalBeds", room.getTotalBeds());
            roomData.put("occupiedBeds", room.getOccupiedBeds());
            roomData.put("availableBeds", room.getTotalBeds() - room.getOccupiedBeds());

            response.add(roomData);
        }

        return ResponseEntity.ok(response);
    }
}
