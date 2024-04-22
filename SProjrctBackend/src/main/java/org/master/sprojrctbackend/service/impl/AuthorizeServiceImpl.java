package org.master.sprojrctbackend.service.impl;

import jakarta.annotation.Resource;
import org.master.sprojrctbackend.entity.Account;
import org.master.sprojrctbackend.mapper.UserMapper;
import org.master.sprojrctbackend.service.AuthorizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
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

import static ch.qos.logback.classic.util.ContextSelectorStaticBinder.getSingleton;

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

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) {
            throw new UsernameNotFoundException("no blank username");
        }
        Account account=userMapper.findAccountByName(username);
        if(account==null){
            throw new UsernameNotFoundException("username not found");
        }
        UserDetails userDetails=User
                .withUsername(username)
                .password(account.getPassword())
                .roles("user")
                .build();
        return userDetails;
    }

    @Override
    public String sendValidateEmail(String email,String sessionId, boolean hasAccount) {

        String key="email:"+sessionId+":"+email+":"+hasAccount;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
            Long expire = Optional.ofNullable(stringRedisTemplate.getExpire(key, TimeUnit.SECONDS)).orElse(0L);
            if (expire<120){

            }else{
                return "too many requests";
            }
        }
        var account=userMapper.findAccountByName(email);
        if(hasAccount&&account==null){
            return "no such account";
        }
        if (!hasAccount&&account!=null){
            return "user already exists";
        }
        var random=new StringBuilder();
        for (int i = 0; i < 6; i++) {
            random.append(new Random().nextInt(10));
        }
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("verification email");
        message.setText(random.toString());
        try {
            mailSender.send(message);
            stringRedisTemplate.opsForValue().set(key,random.toString(),100, TimeUnit.MINUTES);
            return null;
        }catch (MailException e){
            e.printStackTrace();
            return "send email failed";
        }
    }

    @Override
    public String validateAndRegister(String username, String password, String email, String code, String sessionId) {
        String key="email:"+sessionId+":"+email+":false";
        password=encoder.encode(password);

        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
            var s=stringRedisTemplate.opsForValue().get(key);
            if (s==null){
                return "verify failure";
            }
            if (s.equals(code)){
                password=encoder.encode(password);
                stringRedisTemplate.delete(key);
                if (userMapper.insertAccount(username,password,email) > 0) {
                    return null;
                }else {
                    return "db failure";
                }
            }else {
                return "verify failure";
            }
        }else {
            return "verify email first";
        }
    }

    @Override
    public String validateOnly(String email, String code, String sessionId) {
        String key="email:"+sessionId+":"+email+":true";
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
            var s=stringRedisTemplate.opsForValue().get(key);
            if (s==null){
                return "verify failure";
            }
            if (s.equals(code)){
                stringRedisTemplate.delete(key);
                return null;
            }else {
                return "verify failure";
            }
        }else {
            return "verify email first";
        }
    }

    @Override
    public boolean resetPassword(String email, String password) {
        password = encoder.encode(password);
        return userMapper.resetPasswordByEmail(password,email)>0;
    }
}
