package com.myproject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequest {

    @NotBlank(message="Please Provide Old Password")
    private String oldpassword;

    @NotBlank(message="Please Provide New Password")
    private String newPassword;

}
