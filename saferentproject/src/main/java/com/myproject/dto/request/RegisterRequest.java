package com.myproject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @Size(min = 4,max = 4)
    @NotBlank(message = "Please provide your first name")
    private String firstName;

    @Size(max = 50)
    @NotBlank(message = "Please provide your last name")
    private String lastName;

    @Size(min = 5, max = 80)
    @Email(message = "Please provide valid e-mail")
    private String email;

    @Size(min = 4, max = 20, message = "Plase Provide Correct Size of Password")
    @NotBlank(message = "Please provide your password")
    private String password;

    @Pattern(regexp = "^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$", //(541) 317-8828
            message = "Please provide valid phone number")
    @Size(min=14, max=14)
    @NotBlank(message = "Please provide your phone number")
    private String phoneNumber;

    @Size(max = 50)
    @NotBlank(message = "Please provide your address")
    private String address;


    @Size(max=15)
    @NotBlank(message="Please provide your zip code")
    private String zipCode;


}
