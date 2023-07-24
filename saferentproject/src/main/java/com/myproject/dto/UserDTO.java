package com.myproject.dto;

import com.myproject.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    // !!! bu class repodan gelen pojo yu DTO ya çevirmek için kullanılacak

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private String zipCode;
    private Boolean builtIn ;

    private Set<String> roles;

    public void setRoles(Set<Role> roles){
        Set<String> roleStr = new HashSet<>();
        roles.forEach(r->{
            roleStr.add(r.getType().getName()); // Customer, Administrator
        });
        this.roles=roleStr;
    }


}
