package com.bigobooks.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.bigobooks.cache.CacheNames;
import com.bigobooks.dto.OrderDto;
import com.bigobooks.entities.orders.Order;
import com.bigobooks.mappers.OrderMapper;

/**
 * Verifica que la anotacion {@code @Cacheable} de la capa de lectura realmente
 * evite la segunda consulta al servicio de dominio.
 */
@SpringJUnitConfig(OrderQueryServiceCacheTest.Config.class)
@ActiveProfiles("test")
class OrderQueryServiceCacheTest {

	@TestConfiguration
	@Profile("test")
	@EnableCaching
	static class Config {

		@Bean
		CacheManager cacheManager() {
			return new ConcurrentMapCacheManager(CacheNames.ORDERS);
		}

		@Bean
		OrderService orderService() {
			return mock(OrderService.class);
		}

		@Bean
		OrderMapper orderMapper() {
			return mock(OrderMapper.class);
		}

		@Bean
		OrderQueryService orderQueryService(OrderService orderService, OrderMapper orderMapper) {
			return new OrderQueryService(orderService, orderMapper);
		}
	}

	@Autowired
	private OrderQueryService orderQueryService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private CacheManager cacheManager;

	@Test
	void laSegundaConsultaUsaElCache() {
		Order order = new Order();
		order.setId(1L);
		OrderDto dto = new OrderDto();
		dto.setId(1L);
		when(orderService.requireById(1L)).thenReturn(order);
		when(orderMapper.toDto(order)).thenReturn(dto);

		assertSame(dto, orderQueryService.getById(1L));
		assertSame(dto, orderQueryService.getById(1L));

		verify(orderService, times(1)).requireById(1L);
		assertNotNull(cacheManager.getCache(CacheNames.ORDERS).get(1L));
	}
}
