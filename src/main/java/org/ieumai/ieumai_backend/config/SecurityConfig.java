package org.ieumai.ieumai_backend.config;

import lombok.RequiredArgsConstructor;
import org.ieumai.ieumai_backend.config.jwt.JwtAuthenticationEntryPoint;
import org.ieumai.ieumai_backend.config.jwt.JwtAuthenticationFilter;
import org.ieumai.ieumai_backend.config.security.CustomBasicAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${swagger.auth.username}")
    private String swaggerUsername;

    @Value("${swagger.auth.password}")
    private String swaggerPassword;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exceptions -> exceptions
                        // Swagger UI는 Basic 인증, 나머지는 JWT 인증
                        .defaultAuthenticationEntryPointFor(
                                customBasicAuthenticationEntryPoint,
                                new AntPathRequestMatcher("/swagger-ui/**")
                        )
                        .defaultAuthenticationEntryPointFor(
                                customBasicAuthenticationEntryPoint,
                                new AntPathRequestMatcher("/v3/api-docs/**")
                        )
                        .defaultAuthenticationEntryPointFor(
                                customBasicAuthenticationEntryPoint,
                                new AntPathRequestMatcher("/swagger-resources/**")
                        )
                        .defaultAuthenticationEntryPointFor(
                                jwtAuthenticationEntryPoint,
                                AnyRequestMatcher.INSTANCE
                        )
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
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/v3/api-docs/**"
                        ).authenticated() // Swagger UI에 인증 요구
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
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // HTTP Basic 인증 설정
                .httpBasic(basic -> basic
                        .authenticationEntryPoint(customBasicAuthenticationEntryPoint)
                );

        http.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
                .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'")));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails swaggerUser = User.builder()
                .username(swaggerUsername)
                .password(passwordEncoder().encode(swaggerPassword))
                .roles("SWAGGER_ADMIN")
                .build();

        return new InMemoryUserDetailsManager(swaggerUser);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}