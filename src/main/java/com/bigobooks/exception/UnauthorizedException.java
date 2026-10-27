package com.bigobooks.exception;

import org.springframework.http.HttpStatus;

import com.bigobooks.shared.exception.ApiException;

/**
 * Credenciales invalidas (login). Se traduce a HTTP 401.
 */
public class UnauthorizedException extends ApiException {

	public UnauthorizedException(String message) {
		super(HttpStatus.UNAUTHORIZED, message);
	}
}
