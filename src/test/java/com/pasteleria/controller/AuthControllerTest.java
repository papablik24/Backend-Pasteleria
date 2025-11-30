package com.pasteleria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pasteleria.dto.AuthRequest;
import com.pasteleria.model.Usuarios;
import com.pasteleria.service.UsuarioService;
import com.pasteleria.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    MockMvc mvc;

    @Mock
    UsuarioService userService;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    AuthController authController;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void registro_retornaUsuario() throws Exception {
        Usuarios u = new Usuarios(1L, "u", null, "ROL_CLIENTE", null);
        when(userService.register(any(Usuarios.class))).thenReturn(u);

        var req = new Usuarios(); req.setNombreUsuario("u"); req.setContrasena("p");

        mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUsuario").value("u"));
    }

    @Test
    void login_returnsToken() throws Exception {
        AuthRequest ar = new AuthRequest(); ar.setUsername("u"); ar.setPassword("p");

        Authentication auth = org.mockito.Mockito.mock(Authentication.class);
        UserDetails ud = org.springframework.security.core.userdetails.User.withUsername("u").password("p").roles("CLIENTE").build();
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(ud);
        when(jwtUtil.generateToken(ud)).thenReturn("tok123");

        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(ar)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("tok123"));
    }
}
