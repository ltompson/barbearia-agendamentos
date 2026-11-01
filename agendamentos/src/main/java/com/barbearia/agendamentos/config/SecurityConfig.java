
package com.barbearia.agendamentos.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Autenticacao publica
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                        // Consultas publicas para o agendamento
                        .requestMatchers(HttpMethod.GET,
                                "/api/agendamentos/disponiveis",
                                "/api/agendamentos/dia-disponivel",
                                "/api/agendamentos/dias-bloqueados"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/agendamentos").permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/api/dias-disponiveis",
                                "/api/dias-disponiveis/barbeiro/**",
                                "/api/barbeiros",
                                "/api/servicos"
                        ).permitAll()

                        // Operacoes exclusivas do ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/servicos/todos")
                        .hasRole("ADMIN")
                        .requestMatchers("/api/servicos/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/barbeiros/todos")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/barbeiros").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/barbeiros/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/barbeiros/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/barbeiros/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/dias-disponiveis")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/dias-disponiveis/**")
                        .hasRole("ADMIN")

                        // Bloqueios: ADMIN por enquanto
                        .requestMatchers("/api/bloqueios/**").hasRole("ADMIN")

                        // Dados de clientes: usuarios autenticados
                        .requestMatchers("/api/clientes/**")
                        .hasAnyRole("ADMIN", "FUNCIONARIO")

                        // Agendamentos internos
                        .requestMatchers(HttpMethod.GET, "/api/agendamentos")
                        .hasAnyRole("ADMIN", "FUNCIONARIO")
                        .requestMatchers(HttpMethod.GET, "/api/agendamentos/barbeiro/**")
                        .hasAnyRole("ADMIN", "FUNCIONARIO")
                        .requestMatchers(HttpMethod.GET, "/api/agendamentos/*")
                        .hasAnyRole("ADMIN", "FUNCIONARIO")
                        .requestMatchers(HttpMethod.PATCH, "/api/agendamentos/*/cancelar")
                        .hasAnyRole("ADMIN", "FUNCIONARIO")
                        .requestMatchers(HttpMethod.POST, "/api/agendamentos/*/reagendar")
                        .hasAnyRole("ADMIN", "FUNCIONARIO")

                        // Qualquer outra rota exige autenticacao
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigins));
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
