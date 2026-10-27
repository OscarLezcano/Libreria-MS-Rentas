package com.bigobooks.cache;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuracion externa del cache de Ventas. Los TTL se pueden ajustar por
 * entorno sin recompilar el servicio.
 */
@ConfigurationProperties(prefix = "sales.cache")
public class SalesCacheProperties {

	/**
	 * Prefijo comun de todas las claves. Incluye la version para poder migrar
	 * el formato sin chocar con datos viejos.
	 */
	private String keyPrefix = "bigobooks:v1:sales:";

	private final Ttl ttl = new Ttl();

	public String getKeyPrefix() {
		return keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public Ttl getTtl() {
		return ttl;
	}

	public static class Ttl {

		private Duration orders = Duration.ofMinutes(10);

		private Duration coupons = Duration.ofMinutes(30);

		private Duration promotions = Duration.ofMinutes(15);

		public Duration getOrders() {
			return orders;
		}

		public void setOrders(Duration orders) {
			this.orders = orders;
		}

		public Duration getCoupons() {
			return coupons;
		}

		public void setCoupons(Duration coupons) {
			this.coupons = coupons;
		}

		public Duration getPromotions() {
			return promotions;
		}

		public void setPromotions(Duration promotions) {
			this.promotions = promotions;
		}
	}
}
