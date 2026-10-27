package com.bigobooks.cache;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * Decora un {@link CacheManager} para registrar cada operacion de cache
 * (lectura, escritura, invalidacion y limpieza) sin cambiar la semantica del
 * proveedor Redis.
 */
public class LoggingCacheManager implements CacheManager {

	private final CacheManager delegate;

	private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<>();

	public LoggingCacheManager(CacheManager delegate) {
		this.delegate = delegate;
	}

	@Override
	public Cache getCache(String name) {
		Cache cache = delegate.getCache(name);
		if (cache == null) {
			return null;
		}
		return caches.computeIfAbsent(name, ignored -> new LoggingCache(cache));
	}

	@Override
	public Collection<String> getCacheNames() {
		return delegate.getCacheNames();
	}

	@Override
	public void resetCaches() {
		delegate.resetCaches();
		caches.clear();
	}
}
