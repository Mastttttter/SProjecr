package org.master.sprojrctbackend.service;

import jakarta.annotation.Resource;
import org.master.sprojrctbackend.entity.Account;
import org.master.sprojrctbackend.mapper.UserMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class AuthorizeService implements UserDetailsManager {
    @Resource
    UserMapper userMapper;


    @Override
    public void createUser(UserDetails user) {

    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) {
            throw new UsernameNotFoundException("no blank username");
        }
        Account account=userMapper.findAccountByName(username);
        if(account==null){
            throw new UsernameNotFoundException("username not found");
        }
        System.out.println(account);
        UserDetails userDetails=User
                .withUsername(username)
                .password(account.getPassword())
                .roles("user")
                .build();
        return userDetails;
    }
}
