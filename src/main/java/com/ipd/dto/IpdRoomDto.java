package com.ipd.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.ipd.entity.IpdRoom;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IpdRoomDto {

    private Long id;
    private String roomNumber;
    private Double price;
    private String wardType;
    private int totalBeds;
    private int occupiedBeds;
    private boolean isActive;

    private List<IpdBedDto> beds;

    // ENTITY → DTO
    public static IpdRoomDto fromEntity(IpdRoom room) {

        IpdRoomDto dto = new IpdRoomDto();

        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setPrice(room.getPrice());
        dto.setWardType(room.getWardType());
        dto.setTotalBeds(room.getTotalBeds());
        dto.setOccupiedBeds(room.getOccupiedBeds());
        dto.setActive(room.isActive());

        dto.setBeds(room.getBeds()
                .stream()
                .map(IpdBedDto::fromEntity)
                .collect(Collectors.toList())
        );

        return dto;
    }
}
