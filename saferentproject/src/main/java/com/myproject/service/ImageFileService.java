package com.myproject.service;

import com.myproject.domain.ImageData;
import com.myproject.domain.ImageFile;
import com.myproject.dto.ImageFileDTO;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.exception.message.ErrorMessage;
import com.myproject.repository.ImageFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ImageFileService {

    private final ImageFileRepository imageFileRepository;


    public ImageFileService(ImageFileRepository imageFileRepository) {
        this.imageFileRepository = imageFileRepository;
    }


    public String saveImage(MultipartFile file) {

        ImageFile imageFile = null;
        // name
        String fileName =
                StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // Data
        try {
            ImageData imageData = new ImageData(file.getBytes());
            imageFile = new ImageFile(fileName,file.getContentType(),imageData);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        imageFileRepository.save(imageFile);
        return imageFile.getId();
    }

    public ImageFile getImageById(String id) {
        ImageFile imageFile = imageFileRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.IMAGE_NOT_FOUND_MESSAGE,id)));
        return imageFile;
    }


    public List<ImageFileDTO> getAllImages() {
        List<ImageFile> imageFiles = imageFileRepository.findAll();
        // image1 : localhost:8080/files/download/id

        List<ImageFileDTO> imageFileDTOS = imageFiles.stream().map(imFile->{
           // URL oluşturulmasını saglayacagız
            String imageUri = ServletUriComponentsBuilder.
                    fromCurrentContextPath(). // localhost:8080
                    path("/files/download/"). // localhost:8080/files/download
                    path(imFile.getId()).toUriString(); // localhost:8080/files/download/id
            return new ImageFileDTO(imFile.getName(),imageUri,imFile.getType(),imFile.getLength());
        }).collect(Collectors.toList());
        return imageFileDTOS;
    }

    public void removeById(String id) {
        ImageFile imageFile = getImageById(id);

        imageFileRepository.delete(imageFile);
    }

    public ImageFile findImageById(String imageId) {
        return imageFileRepository.findImageById(imageId).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.IMAGE_NOT_FOUND_MESSAGE,imageId)));
    }
}
