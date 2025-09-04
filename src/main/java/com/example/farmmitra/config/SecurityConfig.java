package com.example.farmmitra.config;

import com.example.farmmitra.Service.FarmerDetailsService;
import com.example.farmmitra.Service.BuyerDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private FarmerDetailsService farmerDetailsService;

    @Autowired
    private BuyerDetailsService buyerDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider farmerAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(farmerDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public DaoAuthenticationProvider buyerAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(buyerDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Custom AuthenticationSuccessHandler to redirect users based on their roles.
     * If the authenticated user has 'ROLE_BUYER', they are redirected to /buyer/dashboard.
     * If they have 'ROLE_FARMER', they are redirected to /farmer/dashboard.
     * For any other roles, or if no specific role match, they are redirected to the root.
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                // Get the roles (authorities) of the authenticated user
                Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

                // Check roles and redirect accordingly
                if (roles.contains("ROLE_BUYER")) {
                    response.sendRedirect("/buyer/dashboard");
                } else if (roles.contains("ROLE_FARMER")) {
                    response.sendRedirect("/farmer/dashboard");
                } else {
                    // Default redirect if no specific role is matched
                    response.sendRedirect("/");
                }
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/farmer/register", "/farmer/send-otp", "/buyer/register", "/buyer/send-otp"))
            .authorizeHttpRequests(authorize -> authorize
                // ⭐ Crucial Ordering: Permit specific paths first, especially /index
                .requestMatchers(
                    "/", "/index", "/select-role",
                    "/farmer/register", "/farmer/send-otp", "/farmer/login", 
                    "/buyer/register", "/buyer/send-otp", "/buyer/login",    
                    "/login", 
                    "/css/**", "/js/**", "/images/**", "/error"
                ).permitAll()
                // Then, define role-based access for specific dashboards
                .requestMatchers("/farmer/**").hasRole("FARMER")
                .requestMatchers("/buyer/**").hasRole("BUYER")
                // Finally, ensure all other requests (that haven't been permitted or given specific roles) are authenticated
                .anyRequest().authenticated() 
            )
            .formLogin(form -> form
                .loginPage("/farmer/login") 
                .loginProcessingUrl("/login") 
                .successHandler(authenticationSuccessHandler()) 
                .failureUrl("/farmer/login?error") 
                .permitAll() 
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .authenticationProvider(farmerAuthenticationProvider())
            .authenticationProvider(buyerAuthenticationProvider());
            
        return http.build();
    }
}
