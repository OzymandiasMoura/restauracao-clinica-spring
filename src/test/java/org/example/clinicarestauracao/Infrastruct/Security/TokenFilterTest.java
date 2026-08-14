package org.example.clinicarestauracao.Infrastruct.Security;

import jakarta.servlet.FilterChain;
import org.example.clinicarestauracao.Application.Interfaces.UserRepository;
import org.example.clinicarestauracao.Builders.UserTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenFilterTest
{
    @Mock
    private TokenService service;
    @Mock
    private UserRepository repository;
    @Mock
    private FilterChain chain;
    @InjectMocks
    private TokenFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private final UserTestBuilder builder = new UserTestBuilder();

    @BeforeEach
    void setUp()
    {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown()
    {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueFilterChainWhenAuthenticationIsMissing() throws Exception
    {
        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verifyNoInteractions(service);
        verifyNoInteractions(repository);
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderIsNotBearer() throws Exception
    {
        request.addHeader("Authorization", "Basic credentials");

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verifyNoInteractions(service);
        verifyNoInteractions(repository);
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldAuthenticateUserWhenTokenIsValid() throws Exception
    {
        User user = (User) builder.build();
        request.addHeader("Authorization", "Bearer valid-token");

        when(service.validateToken("valid-token")).thenReturn("Pedro");
        when(repository.findUserByUsername("Pedro")).thenReturn(user);

        filter.doFilterInternal(request, response, chain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());
        assertSame(user, authentication.getPrincipal());
        assertNull(authentication.getCredentials());

        var authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        assertEquals(List.of("ROLE_ADMIN", "ROLE_USER"), authorities);

        verify(service).validateToken("valid-token");
        verify(repository).findUserByUsername("Pedro");
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldRemoveBearerPrefixBeforeValidatingToken() throws Exception
    {
        User user = (User) builder.build();
        request.addHeader("Authorization", "Bearer token-without-prefix");

        when(service.validateToken("token-without-prefix")).thenReturn("Pedro");
        when(repository.findUserByUsername("Pedro")).thenReturn(user);

        filter.doFilterInternal(request, response, chain);

        verify(service).validateToken("token-without-prefix");
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenTokenIsInvalid() throws Exception
    {
        request.addHeader("Authorization", "Bearer invalid-token");

        when(service.validateToken("invalid-token")).thenReturn("");

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(service).validateToken("invalid-token");
        verify(repository, never()).findUserByUsername(anyString());
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenUserIsNotFound() throws Exception
    {
        request.addHeader("Authorization", "Bearer valid-token");

        when(service.validateToken("valid-token"))
                .thenReturn("Pedro");

        when(repository.findUserByUsername("Pedro"))
                .thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(service).validateToken("valid-token");
        verify(repository).findUserByUsername("Pedro");
        verify(chain).doFilter(request, response);
    }
}