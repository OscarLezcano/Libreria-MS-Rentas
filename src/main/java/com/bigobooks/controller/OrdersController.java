package com.bigobooks.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.Envelope;
import com.bigobooks.dto.OrderCreateRequest;
import com.bigobooks.dto.OrderDto;
import com.bigobooks.dto.OrderStatusChange;
import com.bigobooks.dto.OrderUpdateRequest;
import com.bigobooks.entities.orders.Order;
import com.bigobooks.entities.orders.OrderStatus;
import com.bigobooks.mappers.OrderMapper;
import com.bigobooks.services.OrderQueryService;
import com.bigobooks.services.OrderService;
import com.bigobooks.util.Envelopes;

/**
 * Controlador de ventas: implementa la interfaz generada desde openapi.yaml.
 */
@RestController
public class OrdersController implements OrdersApi {

	private static final Logger log = LoggerFactory.getLogger(OrdersController.class);

	private final OrderService orderService;
	private final OrderQueryService orderQueryService;
	private final OrderMapper orderMapper;

	public OrdersController(OrderService orderService, OrderQueryService orderQueryService, OrderMapper orderMapper) {
		this.orderService = orderService;
		this.orderQueryService = orderQueryService;
		this.orderMapper = orderMapper;
	}

	@Override
	public ResponseEntity<Envelope> createOrder(OrderCreateRequest orderCreateRequest) {
		log.info("POST /orders: creacion de venta recibida");
		Order created = orderService.createFromRequest(orderCreateRequest);
		return new ResponseEntity<>(Envelopes.single(orderMapper.toDto(created)), HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<Envelope> listOrders(String status, Long warehouseId, String couponCode, Long bookId,
			LocalDateTime createdFrom, LocalDateTime createdTo, Integer page, Integer size, String sort) {
		log.info("GET /orders: listado de ventas (page={}, size={}, sort={})", page, size, sort);
		Page<Order> orders = orderService.list(status, warehouseId, couponCode, bookId, createdFrom, createdTo, page,
				size, sort);
		List<OrderDto> data = orders.map(orderMapper::toDto).getContent();
		return ResponseEntity.ok(Envelopes.page(orders, data));
	}

	@Override
	public ResponseEntity<Envelope> getOrderById(Long id) {
		log.info("GET /orders/{}: detalle de venta", id);
		return ResponseEntity.ok(Envelopes.single(orderQueryService.getById(id)));
	}

	@Override
	public ResponseEntity<Envelope> updateOrder(Long id, OrderUpdateRequest orderUpdateRequest) {
		log.info("PUT /orders/{}: actualizacion de venta recibida", id);
		return ResponseEntity.ok(Envelopes.single(orderMapper.toDto(orderService.update(id, orderUpdateRequest))));
	}

	@Override
	public ResponseEntity<Envelope> changeOrderStatus(Long id, OrderStatusChange orderStatusChange) {
		log.info("PUT /orders/{}/status: cambio de estado recibido", id);
		OrderStatusChange.StatusEnum status = orderStatusChange == null ? null : orderStatusChange.getStatus();
		OrderStatus newStatus = status == null ? null : OrderStatus.valueOf(status.name());
		return ResponseEntity.ok(Envelopes.single(orderMapper.toDto(orderService.changeStatus(id, newStatus))));
	}

	@Override
	public ResponseEntity<Void> deleteOrder(Long id) {
		log.info("DELETE /orders/{}: borrado logico de venta recibido", id);
		orderService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
