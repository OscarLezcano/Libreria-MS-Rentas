package com.bigobooks.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest(classes = com.bigobooks.SalesApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@org.springframework.test.context.TestPropertySource(properties = {
		"jwt.secret=01234567890123456789012345678901",
		"spring.config.import=optional:classpath:config/private.properties"
})
class SecurityConfigTest {

	private static final String SECRET = "01234567890123456789012345678901";

	@Autowired
	private MockMvc mockMvc;

	@Test
	void sinTokenRetorna401() throws Exception {
		mockMvc.perform(get("/orders?page=0&size=1")).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("No autenticado o token invalido"));
	}

	@Test
	void tokenInvalidoRetorna401() throws Exception {
		mockMvc.perform(get("/orders?page=0&size=1").header("Authorization", "Bearer token.malo"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void tokenConRolConPermisoRetornaOk() throws Exception {
		String token = tokenConRol("READER");
		mockMvc.perform(get("/orders?page=0&size=1").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}

	@Test
	void tokenAdminPuedeListar() throws Exception {
		String token = tokenConRol("ADMIN");
		mockMvc.perform(get("/orders?page=0&size=1").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}

	@Test
	void swaggerPermitido() throws Exception {
		mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
	}

	private String tokenConRol(String role) {
		SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
		return Jwts.builder().subject("test@bigobooks.com").claim("role", role).issuedAt(Date.from(Instant.now()))
				.expiration(Date.from(Instant.now().plus(10, ChronoUnit.MINUTES))).signWith(key).compact();
	}
}
