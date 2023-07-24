package com.myproject.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageSavedResponse extends SfResponse {


    private String imageId;

    public ImageSavedResponse(String imageId, String message, boolean success) {
        super(message, success);
        this.imageId = imageId;
    }

}
