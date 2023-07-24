package com.myproject.service;

import com.myproject.domain.Role;
import com.myproject.domain.enums.RoleType;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.exception.message.ErrorMessage;
import com.myproject.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;


    public Role findByType(RoleType roleType){
        Role role = roleRepository.findByType(roleType).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.ROLE_NOT_FOUND_EXCEPTION,roleType.name())));
        return role;
    }


}
