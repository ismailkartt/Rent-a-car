package com.myproject.service;

import com.myproject.domain.ContactMessage;
import com.myproject.dto.ContactMessageDTO;
import com.myproject.dto.request.ContactMessageRequest;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.exception.message.ErrorMessage;
import com.myproject.mapper.ContactMessageMapper;
import com.myproject.repository.ContactMessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final ContactMessageMapper contactMessageMapper;

    public ContactMessageService(ContactMessageRepository contactMessageRepository,
                                 ContactMessageMapper contactMessageMapper) {
        this.contactMessageRepository = contactMessageRepository;
        this.contactMessageMapper = contactMessageMapper;
    }

    public void saveMessage(ContactMessageRequest contactMessageRequest) {

        ContactMessage contactMessage=
                contactMessageMapper.contactMessageDTOToContactMessage(contactMessageRequest);
        contactMessageRepository.save(contactMessage);
    }

    public List<ContactMessageDTO> getAll() {
        List<ContactMessage> contactMessageList =contactMessageRepository.findAll();

        return contactMessageMapper.map(contactMessageList);
    }

    public Page<ContactMessageDTO> getAll(Pageable pageable){
        Page<ContactMessage> contactMessagePage = contactMessageRepository.findAll(pageable);
        return getPageDTO(contactMessagePage);
    }

    private Page<ContactMessageDTO> getPageDTO(Page<ContactMessage> contactMessagePage){

        return contactMessagePage.map(
                contactMessageMapper::contactMessageToDTO);
    }

    public ContactMessageDTO getMessage(Long id) {
            ContactMessage contactMessage = getContactMessageWithId(id);
            return contactMessageMapper.contactMessageToDTO(contactMessage);
    }


    public void deleteContactMessage(Long id) {

        ContactMessage contactMessage = getContactMessageWithId(id);
        contactMessageRepository.delete(contactMessage);
    }


    // yardımcı method
    private  ContactMessage getContactMessageWithId(Long id){
        ContactMessage contactMessage = contactMessageRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_EXCEPTION,id)));
        return contactMessage;
    }


    public void updateContactMessage(Long id, ContactMessageRequest contactMessageRequest) {

        ContactMessage foundContactMessage = getContactMessageWithId(id);

        foundContactMessage.setName(contactMessageRequest.getName());
        foundContactMessage.setBody(contactMessageRequest.getBody());
        foundContactMessage.setEmail(contactMessageRequest.getEmail());
        foundContactMessage.setSubject(contactMessageRequest.getSubject());

        contactMessageRepository.save(foundContactMessage);

    }
}
