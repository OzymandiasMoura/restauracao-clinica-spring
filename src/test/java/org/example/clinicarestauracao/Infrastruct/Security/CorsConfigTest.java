package org.example.clinicarestauracao.Infrastruct.Security;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CorsConfigTest
{
    private CorsConfig config;

    @BeforeEach
    void setUp()
    {
        config = new CorsConfig();
    }

    @DisplayName("Deve configurar origens permitidas")
    @Test
    void shouldConfigureAllowedOrigins()
    {
        var source = config.corsConfigurationSource("http://localhost:4200");

        var configuration = source.getCorsConfiguration(new MockHttpServletRequest());

        assertNotNull(configuration);
        assertEquals(List.of("http://localhost:4200"), configuration.getAllowedOrigins());
    }

    @DisplayName("Deve configurar métodos e cabeçalhos permitidos")
    @Test
    void shouldConfigureAllowedMethodsAndHeaders()
    {
        var source = config.corsConfigurationSource("http://localhost:4200");
        var configuration = source.getCorsConfiguration(new MockHttpServletRequest());
        assertNotNull(configuration);
        assertEquals(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"), configuration.getAllowedMethods());
        assertEquals(List.of("Authorization", "Content-Type"), configuration.getAllowedHeaders());
        assertEquals(3600L, configuration.getMaxAge());
    }

    @DisplayName("Deve ignorar origens vazias e remover espaços excedentes")
    @Test
    void shouldIgnoreBlankOriginsAndTrimSpaces()
    {
        var source = config.corsConfigurationSource("http://localhost:4200, ");
        var configuration = source.getCorsConfiguration(new MockHttpServletRequest());
        assertNotNull(configuration);
        assertEquals(List.of("http://localhost:4200"), configuration.getAllowedOrigins());
    }
}