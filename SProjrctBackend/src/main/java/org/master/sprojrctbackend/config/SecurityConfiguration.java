package org.master.sprojrctbackend.config;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import netscape.javascript.JSObject;
import org.master.sprojrctbackend.entity.RestBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http
                 .authorizeHttpRequests((authorizeHttpRequest) -> authorizeHttpRequest
//                                 .requestMatchers("/api/**").permitAll()
                                 .anyRequest().authenticated()
//                        .anyRequest().authenticated()
                 )
                 .formLogin(form->form
//                         .loginPage("/login")
                            .loginProcessingUrl("/api/auth/login-process")//by default .loginPage("/login")
                            .permitAll()
                            .successHandler(this::onAuthenticationSuccess)
                            .failureHandler(this::onAuthenticationFailure)//through exception
                 )
                 .exceptionHandling((exceptionHandling) ->
                         exceptionHandling
                                 .authenticationEntryPoint(this::onAuthenticationFailure)//through exception
//                                 .accessDeniedPage("/ errors/ access-denied")
                 )
                 .logout((logout) ->
                        logout. deleteCookies("remove")
                                .invalidateHttpSession(false)
//                                .logoutUrl("/api/auth/logout")
                                .logoutSuccessUrl("/api/auth/login?logout")
                )
                 .csrf(csrf->csrf.disable());
         return http.build();
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JSONObject.toJSONString(RestBean.success("login success")));
    }

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401,exception.getMessage())));
    }


/*    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        "/api/**"
                );
    }*/
}
