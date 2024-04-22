package org.master.sprojrctbackend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.master.sprojrctbackend.entity.RestBean;
import org.master.sprojrctbackend.service.AuthorizeService;
import org.master.sprojrctbackend.service.impl.AuthorizeServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

     private final String EMAIL_REGEX="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
     private final String USERNAME_REGEX="^[a-zA-Z0-9一-龥]+$";

    @Resource
    AuthorizeService service;
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @PostMapping("/valid-email")
    public RestBean<String> validateEmail(@Pattern(regexp = EMAIL_REGEX)@RequestParam("email") String email, HttpSession httpSession){
        var s=service.sendValidateEmail(email,httpSession.getId(),false);
        if (s==null){
            return RestBean.success("have sent");
        }else{
            return  RestBean.failure(400,s);
        }
    }

    @PostMapping("/valid-reset-email")
    public RestBean<String> validateResetEmail(@Pattern(regexp = EMAIL_REGEX)@RequestParam("email") String email, HttpSession httpSession){
        var s=service.sendValidateEmail(email,httpSession.getId(),true);
        if (s==null){
            return RestBean.success("have sent");
        }else{
            return  RestBean.failure(400,s);
        }
    }

    @PostMapping("/register")
    public RestBean<String> registerUser(@Pattern(regexp = USERNAME_REGEX)@Length(min=2, max=8)@RequestParam("username") String username,
                                         @Length(min=6, max=16)@RequestParam("password") String password,
                                         @Pattern(regexp = EMAIL_REGEX)@RequestParam("email") String email,
                                         @Length(min=6, max=6)@RequestParam("code") String code,
                                         HttpSession httpSession){
        var s=service.validateAndRegister(username,password,email,code,httpSession.getId());
        if (s==null){
            return RestBean.success("success registered");
        }else{
            return RestBean.failure(400,s);
        }
    }

    @PostMapping("/start-reset")
    public RestBean<String> startReset(@Pattern(regexp = EMAIL_REGEX)@RequestParam("email") String email,
                                       @Length(min=6, max=6)@RequestParam("code") String code,
                                       HttpSession httpSession){
        var s=service.validateOnly(email,code,httpSession.getId());
        if (s==null){
            httpSession.setAttribute("reset-password",email);
            return RestBean.success();
        }else {
            return RestBean.failure(400,s);
        }
    }

    @PostMapping("/do-reset")
    public RestBean<String> resetPassword(@Length(min=6, max=16)@RequestParam("password") String password,
                                       HttpSession httpSession){
        var email=(String)httpSession.getAttribute("reset-password");
        if (email==null){
            return RestBean.failure(401,"verify the email first");
        }else if(service.resetPassword(email,password)){
            httpSession.removeAttribute("reset-password");
            return RestBean.success("success reset");
        }else{
            return RestBean.failure(500,"db error");
        }
    }
}
