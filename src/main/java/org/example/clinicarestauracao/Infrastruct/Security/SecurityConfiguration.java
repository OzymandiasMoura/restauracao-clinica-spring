package org.example.clinicarestauracao.Infrastruct.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration
{
    private final TokenFilter tokenFilter;

    public SecurityConfiguration(TokenFilter tokenFilter)
    {
        this.tokenFilter = tokenFilter;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception
    {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/login", "/h2-console/**").permitAll()
                        .requestMatchers("/funcionarios/**", "/users/**").hasRole("ADMIN")
                        .requestMatchers("/auth/register").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/cargos/**", "/convenios/**", "/funcionarios/**", "/users/**", "/acolhidos/**", "/internacoes/**", "/triagens/**", "/responsaveis/**", "/medicamentos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/cargos/*", "/convenios/*/deactivate", "/convenios/*/activate", "/acolhidos/*/deactivate", "/acolhidos/*/reactivate", "/responsaveis/*/deactivate", "/responsaveis/*/reactivate", "/internacoes/*/end").hasRole("ADMIN")
                        .requestMatchers("/convenios/**", "/convenios", "/cargos/**", "/acolhidos/**", "/internacoes/**", "/triagens/**", "/triagens", "/responsaveis", "/responsaveis/**").permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager getAuthenticationManager(AuthenticationConfiguration configuration) throws Exception
    {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
