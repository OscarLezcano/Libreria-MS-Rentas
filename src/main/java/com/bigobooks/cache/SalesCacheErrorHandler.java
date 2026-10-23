package com.bigobooks.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.CacheErrorHandler;

/**
 * Un fallo de Redis no debe tumbar la operacion de negocio. El error se
 * registra y la peticion continua contra la base de datos.
 */
public class SalesCacheErrorHandler implements CacheErrorHandler {

	private static final Logger log = LoggerFactory.getLogger(SalesCacheErrorHandler.class);

	@Override
	public void handleCacheGetError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
		log.warn("Error leyendo cache '{}' con clave '{}': {}", cache.getName(), key, exception.getMessage());
	}

	@Override
	public void handleCachePutError(RuntimeException exception, org.springframework.cache.Cache cache, Object key,
			Object value) {
		log.warn("Error escribiendo cache '{}' con clave '{}': {}", cache.getName(), key, exception.getMessage());
	}

	@Override
	public void handleCacheEvictError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
		log.warn("Error invalidando cache '{}' con clave '{}': {}", cache.getName(), key, exception.getMessage());
	}

	@Override
	public void handleCacheClearError(RuntimeException exception, org.springframework.cache.Cache cache) {
		log.warn("Error limpiando cache '{}': {}", cache.getName(), exception.getMessage());
	}
}
