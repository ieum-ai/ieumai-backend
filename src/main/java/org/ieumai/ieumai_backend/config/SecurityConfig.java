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
@Order(2) // BasicSecurityConfig 다음에 적용
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        // Swagger UI 경로를 제외한 모든 요청에 적용될 필터체인
        RequestMatcher swaggerMatcher = new OrRequestMatcher(
                new AntPathRequestMatcher("/swagger-ui/**"),
                new AntPathRequestMatcher("/v3/api-docs/**"),
                new AntPathRequestMatcher("/swagger-resources/**")
        );

        RequestMatcher notSwaggerMatcher = new NegatedRequestMatcher(swaggerMatcher);

        http
                .securityMatcher(notSwaggerMatcher)
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/regions/**").permitAll()
                        .requestMatchers("/contributors/count").permitAll()
                        .requestMatchers("/db/**").permitAll()
                        .requestMatchers("/voice/**").permitAll()
                        .requestMatchers(
                                "/favicon.ico",
                                "/error"
                        ).permitAll()
                        .requestMatchers("/shell/**").denyAll() // 쉘 관련 경로 차단
                        .requestMatchers("/**/.git/**").denyAll() // Git 관련 접근 차단
                        .requestMatchers("/**/.aws/**").denyAll() // AWS 관련 접근 차단
                        .requestMatchers("/**/\\.env").denyAll() // .env 파일 접근 차단
                        .requestMatchers("/index.php/**").denyAll() // ThinkPHP 관련 접근 차단
                        .anyRequest().authenticated()
                )
                // JWT 필터 등록 (UsernamePasswordAuthenticationFilter 이전에)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
                .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'")));

        return http.build();
    }
}