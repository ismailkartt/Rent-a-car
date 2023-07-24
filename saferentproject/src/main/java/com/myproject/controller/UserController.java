package com.myproject.controller;

import com.myproject.dto.UserDTO;
import com.myproject.dto.request.AdminUserUpdateRequest;
import com.myproject.dto.request.UpdatePasswordRequest;
import com.myproject.dto.request.UserUpdateRequest;
import com.myproject.dto.response.ResponseMessage;
import com.myproject.dto.response.SfResponse;
import com.myproject.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;



    public UserController(UserService userService) {
        this.userService = userService;
    }

    // getAllUser
    @GetMapping("/auth/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    // sisteme giriş yapan kullanıcının bilgisi
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<UserDTO> getUser(){
        UserDTO userDTO = userService.getPrincipal();
        return ResponseEntity.ok(userDTO);
    }

    // getAllUserWithPath
    @GetMapping("/auth/pages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getAllUsersByPage(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") String prop, // neye göre sıralanacagını belirtiyoruz
            @RequestParam(value="direction",
                    required = false,
                    defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, prop));
        Page<UserDTO> userDTOPage = userService.getUserPage(pageable);

        return ResponseEntity.ok(userDTOPage);
    }

    // getUserById
    @GetMapping("/{id}/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id){
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    // update password
    @PatchMapping("/auth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<SfResponse> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest){
        userService.updatePassword(updatePasswordRequest);
        SfResponse response = new SfResponse(ResponseMessage.PASSWORD_CHANGED_RESPONSE_MESSAGE,true);
        return ResponseEntity.ok(response);
    }

    // update User
    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<SfResponse> updateUser(@Valid @RequestBody UserUpdateRequest userUpdateRequest){

        userService.updateUser(userUpdateRequest);

        SfResponse response = new SfResponse();
        response.setMessage(ResponseMessage.USER_UPDATE_RESPONSE_MESSAGE);
        response.setSuccess(true);

        return ResponseEntity.ok(response);
    }

    //Admin herhangi bir kullanıcıyı update etsin
    @PutMapping("/{id}/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SfResponse> updateUserAuth(@PathVariable("id") Long id,
                                                     @Valid @RequestBody AdminUserUpdateRequest adminUserUpdateRequest){
        userService.updateUserAuth(id,adminUserUpdateRequest);
        SfResponse response = new SfResponse(ResponseMessage.USER_UPDATE_RESPONSE_MESSAGE,true);
        return ResponseEntity.ok(response);
    }

    // delete user
    @DeleteMapping("/{id}/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SfResponse> deleteUser(@PathVariable("id") Long id){
        userService.removeUserById(id);
        SfResponse response = new SfResponse(ResponseMessage.USER_DELETE_RESPONSE_MESSAGE,true);
        return ResponseEntity.ok(response);
    }




}
