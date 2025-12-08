package com.ipd.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ipd.entity.IpdHospital;
import com.ipd.entity.IpdRoom;

@Repository
public interface IpdRoomRepository extends JpaRepository<IpdRoom, Long> {

    @Query("SELECT r FROM IpdRoom r WHERE r.hospital.id = :hospitalId AND r.occupiedBeds < r.totalBeds AND r.isActive = true")
    List<IpdRoom> findAvailableRoomsByHospital(@Param("hospitalId") Long hospitalId);

    Optional<IpdRoom> findByRoomNumberAndHospitalId(String roomNumber, Long hospitalId);

    Optional<IpdRoom> findByRoomNumber(String roomNumber);

	List<IpdRoom> findByHospital(IpdHospital hospital);

}
