package com.bigobooks.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;

import com.bigobooks.dto.CouponDto;
import com.bigobooks.dto.OrderDetailDto;
import com.bigobooks.dto.OrderDto;
import com.bigobooks.dto.PromotionDto;

/**
 * Valida que los DTOs que se guardan en Redis sobrevivan el viaje
 * serializar/deserializar sin perder fechas, enums ni listas anidadas.
 */
class CacheSerializationTest {

	@Test
	void serializaYRecuperaUnPedidoCompleto() {
		OrderDetailDto detail = new OrderDetailDto();
		detail.setId(10L);
		detail.setBookId(20L);
		detail.setBookName("Clean Code");
		detail.setQuantity(2);
		detail.setUnitPrice(1500L);
		detail.setDiscount(100L);
		detail.setCreatedAt(LocalDateTime.of(2026, 10, 22, 9, 30));

		OrderDto original = new OrderDto();
		original.setId(1L);
		original.setUserId(2L);
		original.setWarehouseId(3L);
		original.setStatus(OrderDto.StatusEnum.PENDING);
		original.setSubtotal(3000L);
		original.setDiscountAmount(100L);
		original.setTotalPrice(2900L);
		original.setCreatedAt(LocalDateTime.of(2026, 10, 22, 9, 31));
		original.setOrderDetails(List.of(detail));

		OrderDto restored = roundTrip(OrderDto.class, original);

		assertEquals(original, restored);
		assertNotNull(restored.getCreatedAt());
		assertEquals(1, restored.getOrderDetails().size());
		assertEquals("Clean Code", restored.getOrderDetails().get(0).getBookName());
	}

	@Test
	void serializaYRecuperaUnCupon() {
		CouponDto original = new CouponDto();
		original.setId(5L);
		original.setCode("VERANO");
		original.setDiscountPercent(15);
		original.setValidUntil(LocalDateTime.of(2026, 12, 31, 23, 59));
		original.setCreatedAt(LocalDateTime.of(2026, 10, 22, 8, 0));

		CouponDto restored = roundTrip(CouponDto.class, original);

		assertEquals(original, restored);
	}

	@Test
	void serializaYRecuperaUnaPromocion() {
		PromotionDto original = new PromotionDto();
		original.setId(7L);
		original.setName("Semana del libro");
		original.setDiscountPercent(20);
		original.setStartDate(LocalDate.of(2026, 10, 1));
		original.setEndDate(LocalDate.of(2026, 10, 31));
		original.setCreatedAt(LocalDateTime.of(2026, 9, 30, 12, 0));
		original.setBookIds(List.of(11L, 12L, 13L));

		PromotionDto restored = roundTrip(PromotionDto.class, original);

		assertEquals(original, restored);
	}

	private <T> T roundTrip(Class<T> type, T value) {
		JacksonJsonRedisSerializer<T> serializer = new JacksonJsonRedisSerializer<>(type);
		byte[] bytes = serializer.serialize(value);
		assertNotNull(bytes);
		return serializer.deserialize(bytes);
	}
}
