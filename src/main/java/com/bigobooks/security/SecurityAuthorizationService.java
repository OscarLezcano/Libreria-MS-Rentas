package com.bigobooks.security;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("authorizationService")
public class SecurityAuthorizationService {

	private final SecurityProperties properties;

	public SecurityAuthorizationService(SecurityProperties properties) {
		this.properties = properties;
	}

	public boolean canListOrders(Authentication authentication) {
		return hasAny(authentication, properties.getAuthorization().getOrders().getRead());
	}

	public boolean canGetOrder(Authentication authentication) {
		return hasAny(authentication, properties.getAuthorization().getOrders().getRead());
	}

	public boolean canCreateOrder(Authentication authentication) {
		return hasAny(authentication, properties.getAuthorization().getOrders().getWrite());
	}

	public boolean canUpdateOrder(Authentication authentication) {
		return hasAny(authentication, properties.getAuthorization().getOrders().getWrite());
	}

	public boolean canChangeOrderStatus(Authentication authentication) {
		return hasAny(authentication, properties.getAuthorization().getOrders().getStatus());
	}

	public boolean canDeleteOrder(Authentication authentication) {
		return hasAny(authentication, properties.getAuthorization().getOrders().getDelete());
	}

	public boolean canListCoupons(Authentication authentication) {
		return hasAny(authentication, properties.getAuthorization().getCoupons().getRead());
	}

	public boolean canGetCoupon(Authentication authentication) {
		return hasAny(authentication, properties.getAuthorization().getCoupons().getRead());
	}

	public boolean canCreateCoupon(Authentication authentication) {
		return hasAny(authentication, properties.getAuthorization().getCoupons().getWrite());
	}

	public boolean canUpdateCoupon(Authentication authentication) {
		return hasAny(authentication, properties.getAuthorization().getCoupons().getWrite());
	}

	public boolean canDeleteCoupon(Authentication authentication) {
		return hasAny(authentication, properties.getAuthorization().getCoupons().getDelete());
	}

	public boolean canListPromotions(Authentication authentication) {
		return hasAny(authentication, properties.getAuthorization().getPromotions().getRead());
	}

	public boolean canGetPromotion(Authentication authentication) {
		return hasAny(authentication, properties.getAuthorization().getPromotions().getRead());
	}

	public boolean canCreatePromotion(Authentication authentication) {
		return hasAny(authentication, properties.getAuthorization().getPromotions().getWrite());
	}

	public boolean canUpdatePromotion(Authentication authentication) {
		return hasAny(authentication, properties.getAuthorization().getPromotions().getWrite());
	}

	public boolean canAddBooksToPromotion(Authentication authentication) {
		return hasAny(authentication, properties.getAuthorization().getPromotions().getWrite());
	}

	public boolean canRemoveBookFromPromotion(Authentication authentication) {
		return hasAny(authentication, properties.getAuthorization().getPromotions().getWrite());
	}

	public boolean canDeletePromotion(Authentication authentication) {
		return hasAny(authentication, properties.getAuthorization().getPromotions().getDelete());
	}

	private boolean hasAny(Authentication authentication, List<String> allowed) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return false;
		}
		if (allowed == null || allowed.isEmpty()) {
			return false;
		}
		Object principal = authentication.getPrincipal();
		if (principal == null || "anonymousUser".equals(principal.toString())) {
			return false;
		}
		for (var authority : authentication.getAuthorities()) {
			String value = authority.getAuthority();
			if (value == null) {
				continue;
			}
			for (String a : allowed) {
				if (a == null || a.isBlank()) {
					continue;
				}
				if (value.equalsIgnoreCase(a.trim())) {
					return true;
				}
			}
		}
		return false;
	}
}
