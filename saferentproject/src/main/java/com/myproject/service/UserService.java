package com.myproject.service;

import com.myproject.domain.Role;
import com.myproject.domain.User;
import com.myproject.domain.enums.RoleType;
import com.myproject.dto.UserDTO;
import com.myproject.dto.request.AdminUserUpdateRequest;
import com.myproject.dto.request.RegisterRequest;
import com.myproject.dto.request.UpdatePasswordRequest;
import com.myproject.dto.request.UserUpdateRequest;
import com.myproject.exception.BadRequestException;
import com.myproject.exception.ConflictException;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.exception.message.ErrorMessage;
import com.myproject.mapper.UserMapper;
import com.myproject.repository.UserRepository;
import com.myproject.security.SecurityUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;
    private final ReservationService reservationService;

    public UserService(UserRepository userRepository, RoleService roleService, @Lazy PasswordEncoder passwordEncoder, UserMapper userMapper, ReservationService reservationService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.reservationService = reservationService;
    }

    public User getUserByEmail(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_EXCEPTION,email)));
        return user;
    }


    public void saveUser(RegisterRequest registerRequest) {

        // DTO dan gelen email DB' de daha önce varmı?

        if (userRepository.existsByEmail(registerRequest.getEmail())){
            throw new ConflictException(String.format(ErrorMessage.EMAIL_ALREADY_EXIST_MESSAGE,registerRequest.getEmail()));
        }

        // Yeni kullanıcının rol bilgisini default olarak Customer atıyoruz
        Role role = roleService.findByType(RoleType.ROLE_CUSTOMER);
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        // DB'ye gitmeden önce sifre encode edilecek
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        // yeni kullanıcının gerekli bilgilerini setleyip DB ye gönderiyoruz
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodedPassword);
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setAddress(registerRequest.getAddress());
        user.setZipCode(registerRequest.getZipCode());
        user.setRoles(roles);

        userRepository.save(user);

    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOS = userMapper.map(users);
        return userDTOS;
    }

    public UserDTO getPrincipal() {
        User user = getCurrentUser();
        UserDTO userDTO = userMapper.userToUserDTO(user);
        return userDTO;
    }

    public User getCurrentUser(){
        String email = SecurityUtils.getCurrentLogin().orElseThrow(()->
                new ResourceNotFoundException(ErrorMessage.PRINCIPAL_FOUND_MESSAGE));
        User user = getUserByEmail(email);
        return user;
    }

    public Page<UserDTO> getUserPage(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        return getUserDTOPage(userPage);
    }

    private Page<UserDTO> getUserDTOPage(Page<User> userPage){
        return userPage.map(
                user-> userMapper.userToUserDTO(user));
    }


    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_EXCEPTION,id)));
        return userMapper.userToUserDTO(user);
    }


    public void updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        User user = getCurrentUser();

        // builtIn??
        if(user.getBuiltIn()){
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }

        // form kısmına girilen oldPassword dogrumu ?
        if (!passwordEncoder.matches(updatePasswordRequest.getOldpassword(), user.getPassword())){
            throw new BadRequestException(ErrorMessage.PASSWORD_NOT_MATCHED_MESSAGE);
        }

        // yeni gelen sifreyi encode edilecek
        String hashedPassword = passwordEncoder.encode(updatePasswordRequest.getNewPassword());

        // yeni girecek sifre ile eski sifre aynı olmasın !
        if (passwordEncoder.matches(updatePasswordRequest.getNewPassword(), user.getPassword())){
            throw new BadRequestException(ErrorMessage.NEW_PASSWORD_MATCHED_OLD_PASSWORD);
        }

        user.setPassword(hashedPassword);
        userRepository.save(user);
    }
    @Transactional
    public void updateUser(UserUpdateRequest userUpdateRequest) {
        User user = getCurrentUser();

        // builtIn??
        if(user.getBuiltIn()){
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }

        // email kontrolu
        boolean emailExist = userRepository.existsByEmail(userUpdateRequest.getEmail());

        if (emailExist && !userUpdateRequest.getEmail().equals(user.getEmail())){
            throw new ConflictException(String.format(ErrorMessage.EMAIL_ALREADY_EXIST_MESSAGE,userUpdateRequest.getEmail()));
        }

        userRepository.update(user.getId(),userUpdateRequest.getFirstName(),
                userUpdateRequest.getLastName(),userUpdateRequest.getPhoneNumber(),userUpdateRequest.getEmail(),
                userUpdateRequest.getAddress(), userUpdateRequest.getZipCode());


    }
    public User getById(Long id){
        User user = userRepository.findUserById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_EXCEPTION,id)));
        return user;
    }
    public void updateUserAuth(Long id, AdminUserUpdateRequest adminUserUpdateRequest) {

        User user = getById(id);

        // builtIn??
        if(user.getBuiltIn()){
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }

        //!!! email kontrolu
        boolean emailExist = userRepository.existsByEmail(adminUserUpdateRequest.getEmail());

        if(emailExist && !adminUserUpdateRequest.getEmail().equals(user.getEmail())) {
            throw new ConflictException(
                    String.format(ErrorMessage.EMAIL_ALREADY_EXIST_MESSAGE,adminUserUpdateRequest.getEmail()));
        }

        // password kontrolu
        if (adminUserUpdateRequest.getPassword()==null){
            adminUserUpdateRequest.setPassword(user.getPassword());
        }else{
             String encodedPassword = passwordEncoder.encode(adminUserUpdateRequest.getPassword());
            adminUserUpdateRequest.setPassword(encodedPassword);
        }

        // Role
        Set<String> userStringRoles = adminUserUpdateRequest.getRoles();

        Set<Role> roles = convertRoles(userStringRoles);
        user.setFirstName(adminUserUpdateRequest.getFirstName());
        user.setLastName(adminUserUpdateRequest.getLastName());
        user.setEmail(adminUserUpdateRequest.getEmail());
        user.setPassword(adminUserUpdateRequest.getPassword());
        user.setPhoneNumber(adminUserUpdateRequest.getPhoneNumber());
        user.setAddress(adminUserUpdateRequest.getAddress());
        user.setZipCode(adminUserUpdateRequest.getZipCode());
        user.setBuiltIn(adminUserUpdateRequest.getBuiltIn());
        user.setRoles(roles);

        userRepository.save(user);

    }

    private Set<Role> convertRoles(Set<String> pRoles){
        Set<Role> roles = new HashSet<>();

        if (pRoles==null){
            Role userRole = roleService.findByType(RoleType.ROLE_CUSTOMER);
            roles.add(userRole);
        }else {
                pRoles.forEach(roleStr->{
                    if (roleStr.equals(RoleType.ROLE_ADMIN.getName())){
                        Role adminRole = roleService.findByType(RoleType.ROLE_ADMIN);
                        roles.add(adminRole);
                    }else {
                        Role userRole = roleService.findByType(RoleType.ROLE_CUSTOMER);
                        roles.add(userRole);
                    }
                });

        }
        return roles;
    }


    public void removeUserById(Long id) {
        User user = getById(id);

        // builtIn??
        if(user.getBuiltIn()){
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }

        // !!! reservasyon kontrol
        boolean exist =  reservationService.existsByUser(user);
        if(exist) {
            throw  new BadRequestException(ErrorMessage.USER_CANT_BE_DELETED_MESSAGE);
        }

       userRepository.deleteById(id);

    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
