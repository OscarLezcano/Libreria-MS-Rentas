package com.bigobooks.cache;

/**
 * Nombres de los caches de Ventas. Cada nombre representa un dominio
 * funcional y se traduce a una clave Redis con el prefijo configurado en
 * {@code sales.cache.key-prefix}.
 */
public final class CacheNames {

	public static final String ORDERS = "orders";
	public static final String COUPONS = "coupons";
	public static final String PROMOTIONS = "promotions";

	private CacheNames() {
	}
}
