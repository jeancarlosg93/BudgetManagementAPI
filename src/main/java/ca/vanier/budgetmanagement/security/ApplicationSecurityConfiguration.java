package ca.vanier.budgetmanagement.security;

import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ApplicationSecurityConfiguration {

    @Getter
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;


    public ApplicationSecurityConfiguration(PasswordEncoder passwordEncoder, CustomUserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/user/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/user/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/user/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/expense-category/**").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/expense-category/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/expense-category/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/expense-category/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/income/**").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/income/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/income/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/income/**").hasRole("USER")
                        .anyRequest().authenticated()).userDetailsService(userDetailsService)
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

}