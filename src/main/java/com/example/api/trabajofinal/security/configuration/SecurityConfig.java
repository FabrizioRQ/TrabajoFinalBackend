package com.example.api.trabajofinal.security.configuration;

import com.example.api.trabajofinal.security.Jwt.JwtAuthenticationEntryPoint;
import com.example.api.trabajofinal.security.filters.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/terapia/**").permitAll()
                        .requestMatchers("/api/psicologos/**").permitAll()
                        .requestMatchers("/api/usuarios/**").permitAll()
                        .requestMatchers("/api/padres/**").permitAll()
                        .requestMatchers("/api/niños/**").permitAll()
                        .requestMatchers("/api/pagos/**").permitAll()
                        .requestMatchers("/api/diario-emocional/**").permitAll()
                        .requestMatchers("/api/usuarios/registrar").permitAll()
                        .requestMatchers("/api/usuarios/comprobar").permitAll()
                        .requestMatchers("/api/usuarios/recuperacion/**").permitAll()
                        .requestMatchers("/api/usuarios/restablecer").permitAll()
                        .requestMatchers("/api/usuarios/restablecer-con-token").permitAll()
                        .requestMatchers("/api/usuarios/recuperacion/**").permitAll()
                        .requestMatchers("/api/usuarios/recuperacion-debug/**").permitAll()
                        .requestMatchers("/api/usuarios/existe/**").permitAll()
                        .requestMatchers("/api/avatares/**").permitAll()
                        .requestMatchers("/api/roles").hasRole("ADMIN") // ← SOLO ADMIN
                        .requestMatchers("/api/roles/**").hasRole("ADMIN") // ← SOLO ADMIN
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}