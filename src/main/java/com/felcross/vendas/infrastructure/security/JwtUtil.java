package com.felcross.vendas.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtUtil {

    // Injeta a chave secreta do application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    // Converte a string Base64 em chave criptográfica utilizável pelo JJWT
    private SecretKey getSecretKey() {
        byte[] key = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }

    // Extrai todas as informações (claims) contidas dentro do token
    // Claims são os dados que foram colocados no token na hora da geração
    // ex: email, id, role, data de expiração
    public Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey()) // usa a mesma chave para verificar a assinatura
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Extrai apenas o email (subject) do token
    // O subject é o campo principal que identifica o dono do token
    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    // Extrai a role do usuário que foi gravada no token na geração
    // ex: "USER", "ADMIN"
    public String extrairRole(String token) {
        return extrairClaims(token).get("role", String.class);
    }

    // Verifica se o token já passou da data de expiração
    public boolean isTokenExpirado(String token) {
        return extrairClaims(token).getExpiration().before(new Date());
    }

    // Valida o token verificando duas coisas:
    // 1. O email extraído do token bate com o email fornecido
    // 2. O token não está expirado
    public boolean validarToken(String token, String email) {
        String emailExtraido = extrairEmail(token);
        return emailExtraido.equals(email) && !isTokenExpirado(token);
    }
}
