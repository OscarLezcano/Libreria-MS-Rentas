package com.bigobooks.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.bigobooks.cache.CacheNames;
import com.bigobooks.dto.OrderDto;
import com.bigobooks.mappers.OrderMapper;

/**
 * Lecturas cacheadas de ventas. Se cachea el DTO y nunca la entidad JPA, para
 * no guardar proxies lazy ni relaciones no inicializadas en Redis.
 */
@Service
public class OrderQueryService {

	private final OrderService orderService;

	private final OrderMapper orderMapper;

	public OrderQueryService(OrderService orderService, OrderMapper orderMapper) {
		this.orderService = orderService;
		this.orderMapper = orderMapper;
	}

	@Cacheable(cacheNames = CacheNames.ORDERS, key = "#id", sync = true)
	public OrderDto getById(Long id) {
		return orderMapper.toDto(orderService.requireById(id));
	}
}
