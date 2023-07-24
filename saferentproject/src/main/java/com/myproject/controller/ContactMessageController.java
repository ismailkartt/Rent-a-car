package com.myproject.controller;

import com.myproject.dto.ContactMessageDTO;
import com.myproject.dto.request.ContactMessageRequest;
import com.myproject.dto.response.ResponseMessage;
import com.myproject.dto.response.SfResponse;
import com.myproject.mapper.ContactMessageMapper;
import com.myproject.service.ContactMessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/contactmessage")
public class ContactMessageController {


    private final ContactMessageMapper contactMessageMapper;


    //@Autowired
    private final ContactMessageService contactMessageService;

    public ContactMessageController(ContactMessageMapper contactMessageMapper,
                                    ContactMessageService contactMessageService) {

        this.contactMessageMapper = contactMessageMapper;
        this.contactMessageService = contactMessageService;
    }


        //!!!Create ContactMessage

    @PostMapping("/visitors")
    public ResponseEntity <SfResponse> createContactMessage(
            @Valid @RequestBody ContactMessageRequest contactMessageRequest){
        contactMessageService.saveMessage(contactMessageRequest);
        SfResponse response = new SfResponse("Message received: ", true);

        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

        //!! getAll ContactMessage
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContactMessageDTO>> getAllContactMessage()   {

        List<ContactMessageDTO> contactMessageDTO = contactMessageService.getAll();


        return new ResponseEntity<>(contactMessageDTO, HttpStatus.OK );

    }
    //!!! datamız cok fazla olursa pageable yapıyla calısmak dogrusudur.
    @GetMapping("/pages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ContactMessageDTO>> getAllContactMessageWithPage(
            @RequestParam("page") int page,
            @RequestParam("size")  int size,
            @RequestParam("sort") String prop,//neye göre sıralanacağını belirtiyoruz
            @RequestParam(value = "direction", required = false,
                    defaultValue = "DESC")Sort.Direction direction)
    {
        Pageable pageable = PageRequest.of(page, size,Sort.by(direction, prop));
        Page<ContactMessageDTO> contactMessagePage = contactMessageService.getAll(pageable);

        return ResponseEntity.ok( contactMessagePage);
    }

    //  spesifik olarak bir ContactMessage'ı PathVariable ile alalım
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContactMessageDTO> getMessageWithPath(@PathVariable("id") Long id){
        ContactMessageDTO contactMessageDTO  = contactMessageService.getMessage(id);
        return ResponseEntity.ok(contactMessageDTO);
    }

    //  spesifik olarak bir ContactMessage'ı RequestParam ile alalım
    @GetMapping("/request")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContactMessageDTO> getMessageWithParam(@RequestParam("id") Long id){
        ContactMessageDTO contactMessageDTO = contactMessageService.getMessage(id);
        return ResponseEntity.ok(contactMessageDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SfResponse> deleteContactMessage(@PathVariable("id") Long id){
        contactMessageService.deleteContactMessage(id);
        SfResponse response = new SfResponse(ResponseMessage.CONTACTMESSAGE_DELETE_RESPONSE,true);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SfResponse> updateContactMessage(@PathVariable("id") Long id,
                                                           @Valid @RequestBody ContactMessageRequest contactMessageRequest){
        contactMessageService.updateContactMessage(id,contactMessageRequest);

        SfResponse response = new SfResponse(ResponseMessage.CONTACTMESSAGE_UPDATE_RESPONSE,true);
        return ResponseEntity.ok(response);
    }


}
