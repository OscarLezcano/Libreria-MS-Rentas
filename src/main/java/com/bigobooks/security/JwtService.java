package com.bigobooks.security;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

	private static final Logger log = LoggerFactory.getLogger(JwtService.class);

	private final SecretKey key;

	public JwtService(@Value("${jwt.secret}") String secret) {
		byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
		if (bytes.length < 32) {
			throw new IllegalStateException("jwt.secret debe tener al menos 32 caracteres");
		}
		this.key = Keys.hmacShaKeyFor(bytes);
	}

	public Authentication parse(String token) {
		if (token == null || token.isBlank()) {
			return null;
		}
		try {
			Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token.trim()).getPayload();
			String username = claims.getSubject();
			Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
			Object roles = claims.get("roles");
			if (roles instanceof List<?> list) {
				for (Object r : list) {
					if (r != null && !r.toString().isBlank()) {
						authorities.add(new SimpleGrantedAuthority(r.toString().trim()));
					}
				}
			}
			Object role = claims.get("role");
			if (role != null && !role.toString().isBlank()) {
				authorities.add(new SimpleGrantedAuthority(role.toString().trim()));
			}
			if (authorities.isEmpty()) {
				authorities.add(new SimpleGrantedAuthority("READER"));
			}
			return new UsernamePasswordAuthenticationToken(username == null ? "jwt-user" : username, null, authorities);
		}
		catch (ExpiredJwtException ex) {
			log.debug("Token JWT expirado");
			return null;
		}
		catch (JwtException ex) {
			log.debug("Token JWT invalido: {}", ex.getClass().getSimpleName());
			return null;
		}
		catch (Exception ex) {
			log.debug("Token JWT no procesable: {}", ex.getClass().getSimpleName());
			return null;
		}
	}
}
