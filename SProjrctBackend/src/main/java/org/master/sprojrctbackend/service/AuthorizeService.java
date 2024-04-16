package org.master.sprojrctbackend.service;

import org.springframework.security.provisioning.UserDetailsManager;

public interface AuthorizeService extends UserDetailsManager {

    boolean sendValidateEmail(String email, String sessionId);
}
