package com.myproject.mapper;

import com.myproject.domain.ImageFile;
import com.myproject.domain.Reservation;
import com.myproject.domain.User;
import com.myproject.dto.ReservationDTO;
import com.myproject.dto.request.ReservationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ReservationMapper {


    Reservation reservationRequestToReservation(ReservationRequest reservationRequest);


    @Mapping(source = "car.image", target = "car.image", qualifiedByName = "getImageAsString")
    @Mapping(source = "user",target = "userId",qualifiedByName = "getUserId")
    ReservationDTO reservationToReservationDTO(Reservation reservation);

    List<ReservationDTO> map(List<Reservation> reservationList);


    @Named("getImageAsString")
    public static Set<String> getImageIds(Set<ImageFile> imageFiles){
        Set<String> imgs = new HashSet<>();

        imgs = imageFiles.stream().map(imFile->imFile.getId().toString()).
                collect(Collectors.toSet());
        return imgs;
    }

    @Named("getUserId")
    public static Long getUserId(User user){
        return user.getId();
    }



}
