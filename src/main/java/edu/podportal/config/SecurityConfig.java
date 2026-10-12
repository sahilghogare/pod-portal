package edu.podportal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/login", "/error",
                    "/css/**", "/images/**", "/actuator/health"
                ).permitAll()

                // Role-specific pages
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/agent/**").hasRole("AGENT")
                .requestMatchers("/reviewer/**").hasRole("REVIEWER")

                // Only Admin can create deliveries
                .requestMatchers(HttpMethod.GET, "/deliveries/new")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/deliveries")
                    .hasRole("ADMIN")

                // Only Admin can edit delivery details
                .requestMatchers(HttpMethod.GET, "/deliveries/*/edit")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/deliveries/*/update")
                    .hasRole("ADMIN")

                // Admin and Reviewer can view and search deliveries
                .requestMatchers("/deliveries/**")
                    .hasAnyRole("ADMIN", "REVIEWER")

                // Admin and Reviewer can view the summary dashboard
                .requestMatchers("/dashboard/**")
                    .hasAnyRole("ADMIN", "REVIEWER")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder) {

        UserDetails admin = User.withUsername("admin")
            .password(passwordEncoder.encode("Admin123!"))
            .roles("ADMIN")
            .build();

        UserDetails agent = User.withUsername("agent")
            .password(passwordEncoder.encode("Agent123!"))
            .roles("AGENT")
            .build();

        UserDetails reviewer = User.withUsername("reviewer")
            .password(passwordEncoder.encode("Reviewer123!"))
            .roles("REVIEWER")
            .build();

        return new InMemoryUserDetailsManager(admin, agent, reviewer);
    }
}