package com.myproject.dto;


import com.myproject.domain.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {

    private Long id;

    private CarDTO car;

    private Long userId;

    private LocalDateTime pickUpTime;

    private LocalDateTime dropOfTime;

    private String pickUpLocation;

    private String dropOfLocation;

    private ReservationStatus status;

    private Double totalPrice;

}
