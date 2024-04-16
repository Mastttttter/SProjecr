package org.master.sprojrctbackend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.master.sprojrctbackend.entity.RestBean;
import org.master.sprojrctbackend.service.AuthorizeService;
import org.master.sprojrctbackend.service.impl.AuthorizeServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

     private final String EMAIL_REGEX="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    @Resource
    AuthorizeService Service;

    @PostMapping("/valid-email")
    public RestBean<String> validateEmail(@Pattern(regexp = EMAIL_REGEX)@RequestParam("email") String email, HttpSession httpSession){
        if (Service.sendValidateEmail(email,httpSession.getId())){
            return RestBean.success("have sent");
        }else{
            return  RestBean.failure(400,"faliure send validate email");
        }
    }
}
