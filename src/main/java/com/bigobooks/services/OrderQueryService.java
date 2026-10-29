package com.bigobooks.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.bigobooks.cache.CacheNames;
import com.bigobooks.dto.OrderDetailDto;
import com.bigobooks.dto.OrderDto;
import com.bigobooks.entities.orders.Order;
import com.bigobooks.entities.orders.OrderDetail;
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

	/** Venta resumida: los detalles solo incluyen su id. */
	@Cacheable(cacheNames = CacheNames.ORDERS, key = "#id", sync = true)
	public OrderDto getById(Long id) {
		return orderMapper.toSummaryDto(orderService.requireById(id));
	}

	/**
	 * Desglose completo de la venta. No se cachea para no mezclar tipos en el
	 * cache de ventas.
	 */
	public List<OrderDetailDto> getDetails(Long id) {
		Order order = orderService.requireById(id);
		List<OrderDetailDto> details = new ArrayList<>();
		if (order.getOrderDetails() != null) {
			for (OrderDetail detail : order.getOrderDetails()) {
				details.add(orderMapper.toDetailDto(detail));
			}
		}
		return details;
	}
}
