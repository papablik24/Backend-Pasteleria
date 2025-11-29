package com.pasteleria.config;

import com.pasteleria.filter.JwtAuthenticationFilter;
import com.pasteleria.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("dev")
public class SecurityContextTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void beansArePresent() {
        assertNotNull(context.getBean(PasswordEncoder.class), "PasswordEncoder bean should be present");
        assertNotNull(context.getBean(AuthenticationManager.class), "AuthenticationManager bean should be present");
        assertNotNull(context.getBean(JwtAuthenticationFilter.class), "JwtAuthenticationFilter bean should be present");
        assertNotNull(context.getBean(UsuarioService.class), "UserService bean should be present");
    }
}
