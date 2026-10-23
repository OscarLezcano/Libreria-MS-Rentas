package com.bigobooks.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Configuration;

/**
 * Conecta el {@link CacheManager} de Redis con el soporte de anotaciones de
 * Spring y define el manejador de errores del cache.
 */
@Configuration
public class CacheConfigurer implements CachingConfigurer {

	private final CacheManager cacheManager;

	public CacheConfigurer(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public CacheManager cacheManager() {
		return cacheManager;
	}

	@Override
	public CacheErrorHandler errorHandler() {
		return new SalesCacheErrorHandler();
	}
}
