package com.bigobooks.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

	private Jwt jwt = new Jwt();
	private List<String> publicPaths = new ArrayList<>();
	private Authorization authorization = new Authorization();

	public Jwt getJwt() {
		return jwt;
	}

	public void setJwt(Jwt jwt) {
		this.jwt = jwt;
	}

	public List<String> getPublicPaths() {
		return publicPaths;
	}

	public void setPublicPaths(List<String> publicPaths) {
		this.publicPaths = publicPaths;
	}

	public Authorization getAuthorization() {
		return authorization;
	}

	public void setAuthorization(Authorization authorization) {
		this.authorization = authorization;
	}

	public boolean isAllowCookie() {
		return jwt != null && jwt.isAllowCookie();
	}

	public static class Jwt {
		private boolean allowCookie = false;

		public boolean isAllowCookie() {
			return allowCookie;
		}

		public void setAllowCookie(boolean allowCookie) {
			this.allowCookie = allowCookie;
		}
	}

	public static class Authorization {
		private Orders orders = new Orders();
		private Coupons coupons = new Coupons();
		private Promotions promotions = new Promotions();

		public Orders getOrders() {
			return orders;
		}

		public void setOrders(Orders orders) {
			this.orders = orders;
		}

		public Coupons getCoupons() {
			return coupons;
		}

		public void setCoupons(Coupons coupons) {
			this.coupons = coupons;
		}

		public Promotions getPromotions() {
			return promotions;
		}

		public void setPromotions(Promotions promotions) {
			this.promotions = promotions;
		}
	}

	public static class Orders {
		private List<String> read = new ArrayList<>();
		private List<String> write = new ArrayList<>();
		private List<String> status = new ArrayList<>();
		private List<String> delete = new ArrayList<>();

		public List<String> getRead() {
			return read;
		}

		public void setRead(List<String> read) {
			this.read = read;
		}

		public List<String> getWrite() {
			return write;
		}

		public void setWrite(List<String> write) {
			this.write = write;
		}

		public List<String> getStatus() {
			return status;
		}

		public void setStatus(List<String> status) {
			this.status = status;
		}

		public List<String> getDelete() {
			return delete;
		}

		public void setDelete(List<String> delete) {
			this.delete = delete;
		}
	}

	public static class Coupons {
		private List<String> read = new ArrayList<>();
		private List<String> write = new ArrayList<>();
		private List<String> delete = new ArrayList<>();

		public List<String> getRead() {
			return read;
		}

		public void setRead(List<String> read) {
			this.read = read;
		}

		public List<String> getWrite() {
			return write;
		}

		public void setWrite(List<String> write) {
			this.write = write;
		}

		public List<String> getDelete() {
			return delete;
		}

		public void setDelete(List<String> delete) {
			this.delete = delete;
		}
	}

	public static class Promotions {
		private List<String> read = new ArrayList<>();
		private List<String> write = new ArrayList<>();
		private List<String> delete = new ArrayList<>();

		public List<String> getRead() {
			return read;
		}

		public void setRead(List<String> read) {
			this.read = read;
		}

		public List<String> getWrite() {
			return write;
		}

		public void setWrite(List<String> write) {
			this.write = write;
		}

		public List<String> getDelete() {
			return delete;
		}

		public void setDelete(List<String> delete) {
			this.delete = delete;
		}
	}
}
