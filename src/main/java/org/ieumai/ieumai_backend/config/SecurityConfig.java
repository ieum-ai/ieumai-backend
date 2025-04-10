package org.ieumai.ieumai_backend.config;

import lombok.RequiredArgsConstructor;
import org.ieumai.ieumai_backend.config.jwt.JwtAuthenticationEntryPoint;
import org.ieumai.ieumai_backend.config.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    @Order(1)
    public SecurityFilterChain swaggerFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        RequestMatcher swaggerMatcher = new OrRequestMatcher(
                new AntPathRequestMatcher("/swagger-ui/**"),
                new AntPathRequestMatcher("/v3/api-docs/**"),
                new AntPathRequestMatcher("/swagger-resources/**")
        );
        RequestMatcher notSwaggerMatcher = new NegatedRequestMatcher(swaggerMatcher);

        http
                .securityMatcher(notSwaggerMatcher)
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/actuator/health", "/auth/**", "/regions/**", "/contributors/count",
                                "/db/**", "/voice/**", "/favicon.ico", "/error").permitAll()
                        .requestMatchers("/shell/**", "/**/.git/**", "/**/.aws/**", "/**/.env", "/index.php/**").denyAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
                .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'")));

        return http.build();
    }
}