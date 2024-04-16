package org.master.sprojrctbackend.service.impl;

import jakarta.annotation.Resource;
import org.master.sprojrctbackend.entity.Account;
import org.master.sprojrctbackend.mapper.UserMapper;
import org.master.sprojrctbackend.service.AuthorizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    @Value("${spring.mail.username}")
    String from;
    @Resource
    UserMapper userMapper;

    @Resource
    MailSender mailSender;

    @Resource
    StringRedisTemplate stringRedisTemplate;

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

    @Override
    public boolean sendValidateEmail(String email,String sessionId) {
        String key="email:"+sessionId+":"+email;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
            Long expire = Optional.ofNullable(stringRedisTemplate.getExpire(key, TimeUnit.SECONDS)).orElse(0L);
            if (expire<120){

            }else{
                return false;
            }
        }
        var random=new StringBuilder();
        for (int i = 0; i < 7; i++) {
            random.append(new Random().nextInt(10));
        }
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("verification email");
        message.setText(random.toString());
        try {
            mailSender.send(message);
            stringRedisTemplate.opsForValue().set(key,random.toString(),3, TimeUnit.MINUTES);
        }catch (MailException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
