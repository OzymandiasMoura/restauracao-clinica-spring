package org.example.clinicarestauracao.Infrastruct.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.clinicarestauracao.Application.Interfaces.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TokenFilter extends OncePerRequestFilter
{
    private TokenService tokenService;
    private UserRepository userRepository;

    public TokenFilter(TokenService tokenService, UserRepository userRepository)
    {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        var token  =this.recoverToken(request);
        if(token!=null)
        {
            var subject = tokenService.validateToken(token);
            if(subject!=null && !subject.isBlank())
            {
                UserDetails userDetails =userRepository.findUserByUsername(subject);

                if(userDetails!=null)
                {
                    var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest token)
    {
        var header = token.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer "))
        {
            return null;
        }

        return header.replace("Bearer ", "");
    }
}
