package com.bigobooks.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.bigobooks.dto.OrderDetailDto;
import com.bigobooks.dto.OrderDto;
import com.bigobooks.entities.orders.Order;
import com.bigobooks.entities.orders.OrderDetail;
import com.bigobooks.entities.orders.OrderStatus;

class OrderMapperTest {

	private final OrderMapper mapper = new OrderMapper();

	@Test
	void summaryDtoOnlyIncludesDetailIds() {
		OrderDto dto = mapper.toSummaryDto(sampleOrder());

		assertEquals(Long.valueOf(15), dto.getId());
		assertEquals(1, dto.getOrderDetails().size());
		OrderDetailDto detail = dto.getOrderDetails().get(0);
		assertEquals(Long.valueOf(101), detail.getId());
		assertNull(detail.getBookId());
		assertNull(detail.getBookName());
		assertNull(detail.getQuantity());
		assertNull(detail.getUnitPrice());
		assertNull(detail.getDiscount());
	}

	@Test
	void fullDtoIncludesDetailData() {
		OrderDto dto = mapper.toDto(sampleOrder());

		OrderDetailDto detail = dto.getOrderDetails().get(0);
		assertEquals(Long.valueOf(101), detail.getId());
		assertEquals(Long.valueOf(3), detail.getBookId());
		assertEquals("Cien anios de soledad", detail.getBookName());
		assertEquals(Integer.valueOf(2), detail.getQuantity());
		assertEquals(Long.valueOf(125000), detail.getUnitPrice());
		assertEquals(Long.valueOf(5000), detail.getDiscount());
		assertEquals(Long.valueOf(15), detail.getOrderId());
	}

	private Order sampleOrder() {
		Order order = new Order();
		order.setId(15L);
		order.setStatus(OrderStatus.PENDING);

		OrderDetail detail = new OrderDetail();
		detail.setId(101L);
		detail.setBookId(3L);
		detail.setBookName("Cien anios de soledad");
		detail.setQuantity(2);
		detail.setUnitPrice(125000L);
		detail.setDiscount(5000L);
		detail.setOrder(order);
		order.setOrderDetails(List.of(detail));
		return order;
	}
}
