package com.bigobooks.cache;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.bigobooks.dto.CouponDto;
import com.bigobooks.dto.OrderDto;
import com.bigobooks.dto.PromotionDto;

/**
 * Configura Redis como proveedor de cache del servicio de Ventas.
 *
 * <p>
 * Cada dominio usa su propio {@link RedisCacheConfiguration}: TTL independiente
 * y un serializador JSON tipado. De esta forma Redis guarda solo los DTOs
 * publicos (nunca entidades JPA) y las claves conservan el nombre del dominio.
 * </p>
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(SalesCacheProperties.class)
public class CacheConfig {

	private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory connectionFactory, SalesCacheProperties properties) {
		RedisCacheConfiguration defaults = cacheConfiguration(properties, Object.class,
				properties.getTtl().getOrders());

		Map<String, RedisCacheConfiguration> caches = new LinkedHashMap<>();
		caches.put(CacheNames.ORDERS,
				cacheConfiguration(properties, OrderDto.class, properties.getTtl().getOrders()));
		caches.put(CacheNames.COUPONS,
				cacheConfiguration(properties, CouponDto.class, properties.getTtl().getCoupons()));
		caches.put(CacheNames.PROMOTIONS,
				cacheConfiguration(properties, PromotionDto.class, properties.getTtl().getPromotions()));

		RedisCacheManager delegate = RedisCacheManager.builder(connectionFactory)
				.cacheDefaults(defaults)
				.withInitialCacheConfigurations(caches)
				.disableCreateOnMissingCache()
				.build();
		delegate.afterPropertiesSet();

		log.info("Cache Redis configurado: prefijo={}, TTL pedidos={}, cupones={}, promociones={}",
				properties.getKeyPrefix(), properties.getTtl().getOrders(), properties.getTtl().getCoupons(),
				properties.getTtl().getPromotions());

		return new LoggingCacheManager(delegate);
	}

	private <T> RedisCacheConfiguration cacheConfiguration(SalesCacheProperties properties, Class<T> type,
			Duration ttl) {
		return RedisCacheConfiguration.defaultCacheConfig()
				.prefixCacheNameWith(properties.getKeyPrefix())
				.entryTtl(ttl)
				.disableCachingNullValues()
				.serializeKeysWith(SerializationPair.fromSerializer(RedisSerializer.string()))
				.serializeValuesWith(SerializationPair.fromSerializer(new JacksonJsonRedisSerializer<>(type)));
	}
}
