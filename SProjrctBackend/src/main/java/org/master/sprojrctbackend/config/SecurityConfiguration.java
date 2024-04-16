package org.master.sprojrctbackend.config;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.master.sprojrctbackend.entity.RestBean;
import org.master.sprojrctbackend.service.impl.AuthorizeServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

@Resource
AuthorizeServiceImpl authorizeService;

@Resource
DataSource dataSource;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http
                 .authorizeHttpRequests((authorizeHttpRequest) -> authorizeHttpRequest
                                 .requestMatchers("/api/auth/**")
                                 .permitAll()
//                                 .requestMatchers("/api/**").permitAll()
                                 .anyRequest().authenticated()
//                        .anyRequest().authenticated()
                 )
                 .formLogin(form->form
//                         .loginPage("/login")
                            .loginProcessingUrl("/api/auth/login")//by default .loginPage("/login")
                            .permitAll()
                            .successHandler(this::onAuthenticationSuccess)
                            .failureHandler(this::onAuthenticationFailure)//through exception
                 )
                 .exceptionHandling((exceptionHandling) ->
                         exceptionHandling
                                 .authenticationEntryPoint(this::onAuthenticationFailure)//through exception
//                                 .accessDeniedPage("/ errors/ access-denied")
                 )
                 .rememberMe((rememberMeConfigure)->{
                     rememberMeConfigure
                             .rememberMeParameter("remember")
                             .tokenRepository(this.persistentTokenRepository())
                             .tokenValiditySeconds(3600*24*7);
                 })
                 .cors((corsConfigurer)->{
                     corsConfigurer.configurationSource(this.configurationSource());
                 })
                 .logout((logout) ->
                        logout. deleteCookies("remove")
                                .invalidateHttpSession(false)
                                .logoutUrl("/api/auth/logout")
                                .logoutSuccessHandler(this::onAuthenticationSuccess)
//                                .logoutSuccessUrl("/api/auth/login?logout")
                )
                 .csrf(csrf->csrf.disable());
         return http.build();
    }

    private PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository=new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        jdbcTokenRepository.setCreateTableOnStartup(false);
        return jdbcTokenRepository;
    }

    private CorsConfigurationSource configurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        if(request.getRequestURI().endsWith("/login")){

            response.getWriter().write(JSONObject.toJSONString(RestBean.success("login success")));
        }else if(request.getRequestURI().endsWith("/logout")){
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("logout success")));

        }
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
