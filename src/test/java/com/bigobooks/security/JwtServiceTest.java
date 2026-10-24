package com.bigobooks.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

	private static final String SECRET = "01234567890123456789012345678901";

	@Test
	void parseTokenConRolUnico() {
		JwtService service = new JwtService(SECRET);
		SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
		String token = Jwts.builder().subject("user@test.com").claim("role", "ADMIN").issuedAt(Date.from(Instant.now()))
				.expiration(Date.from(Instant.now().plus(10, ChronoUnit.MINUTES))).signWith(key).compact();

		Authentication auth = service.parse(token);
		assertNotNull(auth);
		assertEquals("user@test.com", auth.getName());
		assertEquals(1, auth.getAuthorities().size());
		assertEquals("ADMIN", auth.getAuthorities().iterator().next().getAuthority());
	}

	@Test
	void parseTokenConRolesArray() {
		JwtService service = new JwtService(SECRET);
		SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
		String token = Jwts.builder().subject("user@test.com").claim("roles", java.util.List.of("SELLER", "READER"))
				.issuedAt(Date.from(Instant.now())).expiration(Date.from(Instant.now().plus(10, ChronoUnit.MINUTES)))
				.signWith(key).compact();

		Authentication auth = service.parse(token);
		assertNotNull(auth);
		assertEquals(2, auth.getAuthorities().size());
	}

	@Test
	void parseTokenExpiradoRetornaNull() {
		JwtService service = new JwtService(SECRET);
		SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
		String token = Jwts.builder().subject("user@test.com").claim("role", "ADMIN")
				.issuedAt(Date.from(Instant.now().minus(30, ChronoUnit.MINUTES)))
				.expiration(Date.from(Instant.now().minus(10, ChronoUnit.MINUTES))).signWith(key).compact();

		assertNull(service.parse(token));
	}

	@Test
	void parseTokenInvalidoRetornaNull() {
		JwtService service = new JwtService(SECRET);
		assertNull(service.parse("token.invalido"));
	}
}
