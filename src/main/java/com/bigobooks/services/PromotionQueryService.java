package com.bigobooks.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.bigobooks.cache.CacheNames;
import com.bigobooks.dto.PromotionDto;
import com.bigobooks.mappers.PromotionMapper;

/**
 * Lecturas cacheadas de promociones.
 */
@Service
public class PromotionQueryService {

	private final PromotionService promotionService;

	private final PromotionMapper promotionMapper;

	public PromotionQueryService(PromotionService promotionService, PromotionMapper promotionMapper) {
		this.promotionService = promotionService;
		this.promotionMapper = promotionMapper;
	}

	@Cacheable(cacheNames = CacheNames.PROMOTIONS, key = "#id", sync = true)
	public PromotionDto getById(Long id) {
		return promotionMapper.toDto(promotionService.requireById(id));
	}
}
