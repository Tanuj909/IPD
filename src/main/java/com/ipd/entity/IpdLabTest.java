package com.ipd.entity;

import java.time.LocalDateTime;
import java.util.Map;

import jakarta.persistence.*;
import lombok.*;
import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "ipd_lab_tests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IpdLabTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "admission_id", nullable = false)
    private IpdAdmission admission;

    @Column(nullable = false)
    private String testName;

    // JSON column for dynamic parameters
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> testResults;

    private String notes;

    private String reportUrl;

    private Long createdBy;

    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
