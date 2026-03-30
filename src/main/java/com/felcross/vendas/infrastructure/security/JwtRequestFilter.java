package com.felcross.vendas.infrastructure.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
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

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    // JwtUtil responsável por extrair e validar as informações do token
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // Passo 1 — pega o header Authorization da requisição
        // Toda req autenticada deve vir com: Authorization: Bearer <token>
        final String authorizationHeader = request.getHeader("Authorization");

        // Passo 2 — verifica se o header existe e tem o formato correto
        // Se não tiver, a requisição segue sem autenticação
        // O SecurityConfig vai barrar se a rota exigir autenticação
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Passo 3 — extrai o token removendo o prefixo "Bearer "
        final String token = authorizationHeader.substring(7);

        try {
            // Passo 4 — extrai o email do token
            // Se o token estiver malformado ou com assinatura inválida,
            // o JJWT já lança exceção aqui
            final String email = jwtUtil.extrairEmail(token);

            // Passo 5 — verifica se o email foi extraído e se o usuário
            // ainda não está autenticado nessa requisição
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Passo 6 — valida o token (email + expiração)
                if (jwtUtil.validarToken(token, email)) {

                    // Passo 7 — extrai a role do token para montar as permissões
                    // Produtos não tem banco de usuário, então monta tudo
                    // direto das claims do token — sem chamada externa
                    String role = jwtUtil.extrairRole(token);

                    // Passo 8 — cria o objeto de autenticação do Spring Security
                    // com o email como principal e a role como authority
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
                            );

                    // Passo 9 — registra a autenticação no contexto do Spring Security
                    // A partir daqui o Spring sabe que o usuário está autenticado
                    // e permite o acesso às rotas protegidas
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

        } catch (ExpiredJwtException e) {
            // Token expirado — devolve 401 com mensagem clara
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"erro\": \"Token expirado\"}");
            return;

        } catch (MalformedJwtException | SignatureException e) {
            // Token com formato inválido ou assinatura não confere
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"erro\": \"Token invalido\"}");
            return;
        }

        // Passo 10 — passa para o próximo filtro da cadeia
        // Se chegou até aqui, o token é válido e o usuário está autenticado
        chain.doFilter(request, response);
    }
}