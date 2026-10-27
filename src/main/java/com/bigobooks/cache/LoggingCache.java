package com.bigobooks.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

/**
 * Cache decorador que deja trazabilidad de todas las operaciones. Se registra
 * una linea por cada GET, PUT, EVICT y CLEAR.
 */
public class LoggingCache implements Cache {

	private static final Logger log = LoggerFactory.getLogger(LoggingCache.class);

	private final Cache delegate;

	public LoggingCache(Cache delegate) {
		this.delegate = delegate;
	}

	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public Object getNativeCache() {
		return delegate.getNativeCache();
	}

	@Override
	public ValueWrapper get(Object key) {
		ValueWrapper value = delegate.get(key);
		logGet(key, value != null);
		return value;
	}

	@Override
	public <T> T get(Object key, Class<T> type) {
		T value = delegate.get(key, type);
		logGet(key, value != null);
		return value;
	}

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		// RedisCache.get(key, Callable) resuelve la lectura y la escritura en una
		// sola operacion atomica. El booleano permite distinguir si el loader se
		// ejecuto (MISS) o si el valor ya estaba cacheado (HIT).
		AtomicBoolean loaded = new AtomicBoolean(false);
		T value = delegate.get(key, () -> {
			loaded.set(true);
			log.info("CACHE MISS cache='{}' key='{}'", getName(), key);
			return valueLoader.call();
		});
		if (loaded.get()) {
			if (value != null) {
				log.info("CACHE PUT (sync) cache='{}' key='{}' type={}", getName(), key,
						value.getClass().getSimpleName());
			}
		} else {
			log.info("CACHE HIT cache='{}' key='{}'", getName(), key);
		}
		return value;
	}

	@Override
	public CompletableFuture<?> retrieve(Object key) {
		log.info("CACHE RETRIEVE cache='{}' key='{}'", getName(), key);
		return delegate.retrieve(key);
	}

	@Override
	public <T> CompletableFuture<T> retrieve(Object key, Supplier<CompletableFuture<T>> valueLoader) {
		log.info("CACHE RETRIEVE (load) cache='{}' key='{}'", getName(), key);
		return delegate.retrieve(key, valueLoader);
	}

	@Override
	public void put(Object key, Object value) {
		log.info("CACHE PUT cache='{}' key='{}' type={}", getName(), key,
				value == null ? "null" : value.getClass().getSimpleName());
		delegate.put(key, value);
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		log.info("CACHE PUT_IF_ABSENT cache='{}' key='{}' type={}", getName(), key,
				value == null ? "null" : value.getClass().getSimpleName());
		return delegate.putIfAbsent(key, value);
	}

	@Override
	public void evict(Object key) {
		log.info("CACHE EVICT cache='{}' key='{}'", getName(), key);
		delegate.evict(key);
	}

	@Override
	public boolean evictIfPresent(Object key) {
		boolean evicted = delegate.evictIfPresent(key);
		log.info("CACHE EVICT_IF_PRESENT cache='{}' key='{}' evicted={}", getName(), key, evicted);
		return evicted;
	}

	@Override
	public void clear() {
		log.info("CACHE CLEAR cache='{}'", getName());
		delegate.clear();
	}

	@Override
	public boolean invalidate() {
		boolean invalidated = delegate.invalidate();
		log.info("CACHE INVALIDATE cache='{}' invalidated={}", getName(), invalidated);
		return invalidated;
	}

	private void logGet(Object key, boolean hit) {
		log.info("CACHE {} cache='{}' key='{}'", hit ? "HIT" : "MISS", getName(), key);
	}
}
