package com.myproject.controller;

import com.myproject.dto.request.LoginRequest;
import com.myproject.dto.request.RegisterRequest;
import com.myproject.dto.response.LoginResponse;
import com.myproject.dto.response.ResponseMessage;
import com.myproject.dto.response.SfResponse;
import com.myproject.security.jwt.JwtUtils;
import com.myproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserJwtController {

    // Bu class'da sadece Login ve Register islemleri yapılacak

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    // Register
    @PostMapping("/register")
    public ResponseEntity<SfResponse> registerUser(@Valid
                                         @RequestBody RegisterRequest registerRequest){
        userService.saveUser(registerRequest);

        SfResponse response = new SfResponse();
        response.setMessage(ResponseMessage.REGISTER_RESPONSE_MESSAGE);
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginRequest loginRequest){

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                                                 loginRequest.getPassword());

        Authentication authentication =
                authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        // kullanıcı bu aşamada valide edildi ve token üretimine geçiliyor.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwtToken = jwtUtils.generateJwtToken(userDetails);
        // jwt token client tarafına gönderiliyor
        LoginResponse loginResponse = new LoginResponse(jwtToken);
        return new ResponseEntity<>(loginResponse,HttpStatus.OK);

    }






}
