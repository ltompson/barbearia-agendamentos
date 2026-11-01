
package com.barbearia.agendamentos.config;

import com.barbearia.agendamentos.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private static final Set<String> ROLES_PERMITIDAS =
            Set.of("ADMIN", "FUNCIONARIO");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            if (jwtService.validarToken(token)) {

                String usuario = jwtService.extrairUsuario(token);
                String role = jwtService.extrairRole(token);

                if (usuario != null && !usuario.isBlank() && role != null) {

                    String roleNormalizada =
                            role.trim().toUpperCase(Locale.ROOT);

                    if (ROLES_PERMITIDAS.contains(roleNormalizada)) {

                        List<SimpleGrantedAuthority> authorities =
                                List.of(new SimpleGrantedAuthority(
                                        "ROLE_" + roleNormalizada
                                ));

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        usuario,
                                        null,
                                        authorities
                                );

                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
