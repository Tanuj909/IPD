package com.ipd.repository;

import com.ipd.entity.IpdBed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IpdBedRepository extends JpaRepository<IpdBed, Long> {

    List<IpdBed> findByRoomIdOrderByBedNumberAsc(Long roomId);

    // find first free bed in room
    Optional<IpdBed> findFirstByRoomIdAndOccupiedFalseOrderByBedNumberAsc(Long roomId);

    // find first free bed in hospital (any room)
    @Query("SELECT b FROM IpdBed b WHERE b.room.hospital.id = :hospitalId AND b.occupied = false ORDER BY b.room.id, b.bedNumber")
    List<IpdBed> findFreeBedsByHospitalId(Long hospitalId);

    long countByRoomIdAndOccupiedTrue(Long roomId);
}
