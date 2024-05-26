package org.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;


    public SecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    }

    //@Bean
    //public AccessDeniedHandler accessDeniedHandler(){ return new AccessDeniedExceptionHandler(); }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .exceptionHandling(e -> e.authenticationEntryPoint(restAuthenticationEntryPoint))
                .csrf(cfg -> cfg.disable())
                .headers(cfg -> cfg.frameOptions().disable())
                .authorizeHttpRequests(
                        matcherRegistry ->
                                matcherRegistry
                                        .anyRequest().permitAll()
                )
                .exceptionHandling()
                .and()
                .sessionManagement(
                        sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }


}

