package com.ipd.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ipd.entity.IpdAdmission;
import com.ipd.entity.IpdHospital;

@Repository
public interface IpdAdmissionRepository extends JpaRepository<IpdAdmission, Long> {

    List<IpdAdmission> findByHospitalId(Long hospitalId);

    
    Optional<IpdAdmission> findByPatientIdAndIsDischargedFalse(Long patientId);


    List<IpdAdmission> findByIsDischargedFalse();


    long countByHospitalAndIsDischargedFalse(IpdHospital hospital);

    @Query("SELECT DATE(a.admissionDate), COUNT(a) " +
           "FROM IpdAdmission a " +
           "WHERE a.hospital.id = :hospitalId AND a.admissionDate BETWEEN :from AND :to " +
           "GROUP BY DATE(a.admissionDate)")
    List<Object[]> countAdmissionsByDateRange(@Param("hospitalId") Long hospitalId,
                                              @Param("from") LocalDateTime from,
                                              @Param("to") LocalDateTime to);


//    @Query("""
//            SELECT a FROM IpdAdmission a 
//            WHERE a.isDischarged = false 
//            AND a.outcome = true
//        """)
	List<IpdAdmission> findByIsOutcomeCreatedTrueAndIsDischargedFalse();
    

}
 