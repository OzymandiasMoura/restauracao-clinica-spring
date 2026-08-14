package org.example.clinicarestauracao.Infrastruct.Security;

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

    @Test
    void shouldConfigureAllowedOrigins()
    {
        var source = config.corsConfigurationSource("http://localhost:4200");

        var configuration = source.getCorsConfiguration(new MockHttpServletRequest());

        assertNotNull(configuration);
        assertEquals(List.of("http://localhost:4200"), configuration.getAllowedOrigins());
    }

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

    @Test
    void shouldIgnoreBlankOriginsAndTrimSpaces()
    {
        var source = config.corsConfigurationSource("http://localhost:4200, ");
        var configuration = source.getCorsConfiguration(new MockHttpServletRequest());
        assertNotNull(configuration);
        assertEquals(List.of("http://localhost:4200"), configuration.getAllowedOrigins());
    }
}