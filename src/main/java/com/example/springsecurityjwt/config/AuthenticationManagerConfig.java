package com.example.springsecurityjwt.config;

import com.example.springsecurityjwt.security.jwt.filter.JwtAuthenticationFilter;
import com.example.springsecurityjwt.security.jwt.provider.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class AuthenticationManagerConfig extends AbstractHttpConfigurer<AuthenticationManagerConfig, HttpSecurity> {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

        // UsernamePasswordAuthenticationFilter.class) 앞에다가 JwtAuthenticationFilter를 만들어주는데 authenticationManager을 사용하고 jwtAuthenticationProvider 사용하도록 설정한것이다.
        builder.addFilterBefore(
                        new JwtAuthenticationFilter(authenticationManager),
                        UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(jwtAuthenticationProvider);
    }
}