package com.bigobooks.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	private final JwtService jwtService;
	private final SecurityProperties securityProperties;

	public JwtAuthenticationFilter(JwtService jwtService, SecurityProperties securityProperties) {
		this.jwtService = jwtService;
		this.securityProperties = securityProperties;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = resolveToken(request);
		if (token != null) {
			Authentication auth = jwtService.parse(token);
			if (auth != null) {
				SecurityContextHolder.getContext().setAuthentication(auth);
				log.debug("AUTH OK usuario={} roles={}", auth.getName(), auth.getAuthorities());
			}
			else {
				log.debug("AUTH FALLIDA uri={}", request.getRequestURI());
			}
		}
		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			return header.substring(7);
		}
		if (securityProperties.isAllowCookie()) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if ("token".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
						return cookie.getValue();
					}
				}
			}
		}
		return null;
	}
}
