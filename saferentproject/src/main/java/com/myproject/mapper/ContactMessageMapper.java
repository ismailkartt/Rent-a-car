package com.myproject.mapper;

import com.myproject.domain.ContactMessage;
import com.myproject.dto.ContactMessageDTO;
import com.myproject.dto.request.ContactMessageRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContactMessageMapper {

       ContactMessageDTO contactMessageToDTO(ContactMessage contactMessage);

       @Mapping(target = "id", ignore = true)
       ContactMessage contactMessageDTOToContactMessage(ContactMessageRequest contactMessageRequest);

       List<ContactMessageDTO> map(List<ContactMessage> contactMessageList); // getAllContactMessage(


}
