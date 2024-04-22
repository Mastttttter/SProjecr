package org.master.sprojrctbackend.service;

import org.springframework.security.provisioning.UserDetailsManager;

public interface AuthorizeService extends UserDetailsManager {

    String sendValidateEmail(String email, String sessionId, boolean hasAccount);
    String validateAndRegister(String username,String password,String email,String code,String sessionId);
    String validateOnly(String email,String code,String sessionId);
    boolean resetPassword(String email,String password);
}
