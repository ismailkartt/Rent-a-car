package com.myproject.controller;

import com.myproject.domain.ImageFile;
import com.myproject.dto.ImageFileDTO;
import com.myproject.dto.response.ImageSavedResponse;
import com.myproject.dto.response.ResponseMessage;
import com.myproject.dto.response.SfResponse;
import com.myproject.service.ImageFileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.http.HttpResponse;
import java.util.List;

@RestController
@RequestMapping("/files")
public class ImageFileController {

    private final ImageFileService imageFileService;


    public ImageFileController(ImageFileService imageFileService) {
        this.imageFileService = imageFileService;
    }

    // upload
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')") //  79b41887-b6b5-4bd3-80e0-c788f8603a5f
    public ResponseEntity<ImageSavedResponse> uploadFile(@RequestParam("file") MultipartFile file) {

        String imageId = imageFileService.saveImage(file);

        ImageSavedResponse imageSavedResponse = new ImageSavedResponse(imageId, ResponseMessage.IMAGE_SAVED_RESPONSE_MESSAGE, true);
        return ResponseEntity.ok(imageSavedResponse);
    }

    // Download
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String id) {
        ImageFile imageFile = imageFileService.getImageById(id);

        return ResponseEntity.ok().
                header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + imageFile.getName()).
                body(imageFile.getImageData().getData());
    }

    // Image Display
    @GetMapping("/display/{id}")
    public ResponseEntity<byte[]> displayFile(@PathVariable String id) {
        ImageFile imageFile = imageFileService.getImageById(id);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(imageFile.getImageData().getData(), header, HttpStatus.OK);

    }

    // GetAllImages
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ImageFileDTO>> getAllImages(){
        List<ImageFileDTO> allImageDTO = imageFileService.getAllImages();
        return ResponseEntity.ok(allImageDTO);
    }

    // Delete Image
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SfResponse> deleteImageFile(@PathVariable String id){
        imageFileService.removeById(id);
        SfResponse response = new SfResponse(ResponseMessage.IMAGE_DELETED_RESPONSE_MESSAGE,true);
        return ResponseEntity.ok(response);
    }



}
