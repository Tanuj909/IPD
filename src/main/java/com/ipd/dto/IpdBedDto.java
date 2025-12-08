package com.ipd.dto;

import com.ipd.entity.IpdBed;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IpdBedDto {

    private Long id;
    private Integer bedNumber;
    private boolean occupied;

    public static IpdBedDto fromEntity(IpdBed bed) {
        IpdBedDto dto = new IpdBedDto();
        dto.setId(bed.getId());
        dto.setBedNumber(bed.getBedNumber());
        dto.setOccupied(bed.isOccupied());
        return dto;
    }
}
