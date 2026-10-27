package com.bigobooks.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.bigobooks.dto.OrderCreateRequest;
import com.bigobooks.dto.OrderDto;
import com.bigobooks.entities.orders.Order;
import com.bigobooks.entities.orders.OrderStatus;
import com.bigobooks.exception.NotFoundException;
import com.bigobooks.util.exception.CommonExceptionHandler;
import com.bigobooks.mappers.OrderMapper;
import com.bigobooks.services.OrderQueryService;
import com.bigobooks.services.OrderService;

/**
 * Slice web del endpoint de ventas. Configuracion minima propia (sin
 * JPA/repositorios) que solo registra el controlador y sus dependencias
 * simuladas.
 */
@WebMvcTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = OrdersControllerTest.OrdersWebTestConfig.class)
class OrdersControllerTest {

	@SpringBootConfiguration
	@Profile("test")
	@Import(CommonExceptionHandler.class)
	static class OrdersWebTestConfig {

		@Bean
		OrderService orderService() {
			return org.mockito.Mockito.mock(OrderService.class);
		}

		@Bean
		OrderMapper orderMapper() {
			return org.mockito.Mockito.mock(OrderMapper.class);
		}

		@Bean
		OrderQueryService orderQueryService() {
			return org.mockito.Mockito.mock(OrderQueryService.class);
		}

		@Bean
		OrdersController ordersController(OrderService service, OrderQueryService queryService, OrderMapper mapper) {
			return new OrdersController(service, queryService, mapper);
		}
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private OrderQueryService orderQueryService;

	@Test
	void creaUnaVenta() throws Exception {
		Order order = new Order();
		order.setId(1L);
		order.setStatus(OrderStatus.PENDING);
		OrderDto dto = new OrderDto();
		dto.setId(1L);
		dto.setStatus(OrderDto.StatusEnum.PENDING);
		dto.setTotalPrice(2000L);
		when(orderService.createFromRequest(any(OrderCreateRequest.class))).thenReturn(order);
		when(orderMapper.toDto(order)).thenReturn(dto);

		mockMvc.perform(post("/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"items\":[{\"bookId\":10,\"quantity\":2}]}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.id").value(1))
				.andExpect(jsonPath("$.data.status").value("PENDING"))
				.andExpect(jsonPath("$.data.totalPrice").value(2000));
	}

	@Test
	void rechazaUnaVentaSinItems() throws Exception {
		mockMvc.perform(post("/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void obtieneUnaVentaPorId() throws Exception {
		OrderDto dto = new OrderDto();
		dto.setId(1L);
		when(orderQueryService.getById(1L)).thenReturn(dto);

		mockMvc.perform(get("/orders/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(1));
	}

	@Test
	void obtieneUnaVentaInexistente() throws Exception {
		when(orderQueryService.getById(99L)).thenThrow(new NotFoundException("No existe la venta con id 99"));

		mockMvc.perform(get("/orders/99"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No existe la venta con id 99"));
	}

	@Test
	void listaVentasPaginadas() throws Exception {
		Page<Order> page = new PageImpl<>(List.of());
		when(orderService.list(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

		mockMvc.perform(get("/orders").param("page", "0").param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").isArray())
				.andExpect(jsonPath("$.pagination.totalElements").value(0));
	}
}
