package com.bigobooks.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.bigobooks.cache.CacheNames;
import com.bigobooks.dto.CouponDto;
import com.bigobooks.mappers.CouponMapper;

/**
 * Lecturas cacheadas de cupones.
 */
@Service
public class CouponQueryService {

	private final CouponService couponService;

	private final CouponMapper couponMapper;

	public CouponQueryService(CouponService couponService, CouponMapper couponMapper) {
		this.couponService = couponService;
		this.couponMapper = couponMapper;
	}

	@Cacheable(cacheNames = CacheNames.COUPONS, key = "#id", sync = true)
	public CouponDto getById(Long id) {
		return couponMapper.toDto(couponService.requireById(id));
	}
}
