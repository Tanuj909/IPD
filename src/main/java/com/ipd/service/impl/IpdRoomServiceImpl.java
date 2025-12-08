package com.ipd.service.impl;

import com.ipd.service.IpdRoomService;
import com.user.entity.User;
import com.user.enums.Role;
import com.user.repository.UserRepository;

import jakarta.transaction.Transactional;

import com.ipd.entity.IpdRoom;
import com.ipd.repository.IpdHospitalRepository;
import com.ipd.repository.IpdRoomRepository;

import java.util.Iterator;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ipd.Exception.AccessDeniedException;
import com.ipd.Exception.ResourceNotFoundException;
import com.ipd.entity.IpdBed;
import com.ipd.entity.IpdHospital;


@Service
public class IpdRoomServiceImpl implements IpdRoomService{
	
	@Autowired
	private IpdHospitalRepository hospitalRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private IpdRoomRepository ipdRoomRepo;
	
	
	private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private User getCurrentUser() {
        return userRepository.findByEmail(getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

//    private void checkAccess(IpdAdmission admission) {
//        User currentUser = getCurrentUser();
//        boolean isOwner = admission.getCreatedBy().equals(currentUser.getId());
//        boolean isSameHospital = currentUser.getIpdHospitalId() != null && admission.getHospital() != null
//                && currentUser.getIpdHospitalId().equals(admission.getHospital().getId());
//        if (!(isOwner || isSameHospital)) {
//            throw new AccessDeniedException("Unauthorized access to this patient");
//        }
//    }

    private void checkIpdModuleAccess() {
    	
        User currentUser = getCurrentUser();

        if (currentUser.getRole() == Role.SUPER_ADMIN) {
            return;
        }
        if (currentUser.getIpdHospitalId() == null) {
            throw new AccessDeniedException("IPD Module is DISABLED by SuperAdmin for this hospital.");
        }

    }
	
	@Override
	@Transactional
	public IpdRoom createRoom(IpdRoom room) throws BadRequestException {
	    checkIpdModuleAccess();

	    IpdHospital hospital = hospitalRepository.findById(getCurrentUser().getIpdHospitalId())
	            .orElseThrow(() -> new ResourceNotFoundException("Hospital Not Found"));

	    // sanitize totalBeds
	    if (room.getTotalBeds() <= 0) {
	        throw new BadRequestException("totalBeds must be >= 1");
	    }

	    room.setOccupiedBeds(0);
	    room.setActive(true);
	    room.setHospital(hospital);

	    // persist room first so it gets id
	    IpdRoom saved = ipdRoomRepo.save(room);

	    // create bed rows 1..totalBeds
	    for (int i = 1; i <= saved.getTotalBeds(); i++) {
	        IpdBed bed = new IpdBed();
	        bed.setBedNumber(i);
	        bed.setOccupied(false);
	        bed.setRoom(saved);
	        saved.getBeds().add(bed);
	    }

	    // when cascade persist, saving room again will persist beds
	    return ipdRoomRepo.save(saved);
	}
	
	@Override
	@Transactional
	public IpdRoom updateRoom(Long roomId, IpdRoom updatedRoom) {
	    checkIpdModuleAccess();

	    IpdRoom room = ipdRoomRepo.findById(roomId)
	            .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

	    // update basic fields
	    room.setRoomNumber(updatedRoom.getRoomNumber());
	    room.setPrice(updatedRoom.getPrice());
	    room.setWardType(updatedRoom.getWardType());
	    room.setActive(updatedRoom.isActive());

	    int newTotal = updatedRoom.getTotalBeds();
	    int currentTotal = room.getTotalBeds();
	    int occupied = (int) room.getBeds().stream().filter(IpdBed::isOccupied).count();
	    room.setOccupiedBeds(occupied);

	    if (newTotal < currentTotal) {
	        // only allow shrink if enough empty beds to remove
	        int removable = currentTotal - newTotal;
	        int emptyBeds = currentTotal - occupied;
	        if (emptyBeds < removable) {
	            throw new IllegalStateException("Cannot reduce totalBeds. Not enough empty beds to remove.");
	        }
	        // remove highest-numbered empty beds
	        room.getBeds().sort((a, b) -> b.getBedNumber().compareTo(a.getBedNumber())); // desc
	        int removed = 0;
	        for (Iterator<IpdBed> it = room.getBeds().iterator(); it.hasNext() && removed < removable; ) {
	            IpdBed bed = it.next();
	            if (!bed.isOccupied()) {
	                it.remove();
	                removed++;
	            }
	        }
	    } else if (newTotal > currentTotal) {
	        // add additional beds
	        for (int i = currentTotal + 1; i <= newTotal; i++) {
	            IpdBed bed = new IpdBed();
	            bed.setBedNumber(i);
	            bed.setOccupied(false);
	            bed.setRoom(room);
	            room.getBeds().add(bed);
	        }
	    }

	    room.setTotalBeds(newTotal);
	    room.setOccupiedBeds((int) room.getBeds().stream().filter(IpdBed::isOccupied).count());

	    return ipdRoomRepo.save(room);
	}

	@Override
	@Transactional
	public void deleteRoom(Long roomId) {
	    checkIpdModuleAccess();

	    IpdRoom room = ipdRoomRepo.findById(roomId)
	            .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

	    long occupied = room.getBeds().stream().filter(IpdBed::isOccupied).count();
	    if (occupied > 0) {
	        throw new IllegalStateException("Cannot delete room: some beds are occupied");
	    }

	    ipdRoomRepo.delete(room);
	}
	
	@Override
	public List<IpdRoom> getAvailableRooms() {
	    checkIpdModuleAccess();
	    Long hospitalId = getCurrentUser().getIpdHospitalId();
	    List<IpdRoom> rooms = ipdRoomRepo.findAvailableRoomsByHospital(hospitalId);
	    // ensure occupiedBeds is accurate
	    rooms.forEach(r -> r.setOccupiedBeds((int) r.getBeds().stream().filter(IpdBed::isOccupied).count()));
	    return rooms;
	}
	
	@Override
	public Long findFirstAvailableRoomId() throws BadRequestException {
	    List<IpdRoom> availableRooms = getAvailableRooms();
	    if (availableRooms == null || availableRooms.isEmpty()) {
	         throw new BadRequestException("Bad Not Available");
	    }
	    return availableRooms.get(0).getId();
	}

}
