package com.bside.someday.oauth;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.bside.someday.error.dto.ErrorType;
import com.bside.someday.error.exception.BusinessException;

import io.jsonwebtoken.lang.Assert;

public class CustomOauth2User implements OAuth2User {

	private final Set<GrantedAuthority> authorities;

	private final Map<String, Object> attributes;

	private final String nameAttributeKey;

	private Long userId;

	private String email;

	public CustomOauth2User(Set<GrantedAuthority> authorities, Map<String, Object> attributes,
		String nameAttributeKey) {

		try {
			Assert.notEmpty(attributes, "empty attributes");
			Assert.hasText(nameAttributeKey, "empty nameAttributeKey");

			if (!attributes.containsKey(nameAttributeKey)) {
				throw new IllegalArgumentException(nameAttributeKey + " is not in attributes");
			}
		} catch (IllegalArgumentException e) {
			throw new BusinessException(e.getMessage(), ErrorType.UNEXPECTED_ERROR);
		}

		this.authorities = (authorities != null)
			? Collections.unmodifiableSet(new LinkedHashSet<>(this.sortAuthorities(authorities)))
			: Collections.unmodifiableSet(new LinkedHashSet<>(AuthorityUtils.NO_AUTHORITIES));
		this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
		this.nameAttributeKey = nameAttributeKey;
	}

	private Set<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {

		SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(
			Comparator.comparing(GrantedAuthority::getAuthority));

		sortedAuthorities.addAll(authorities);
		return sortedAuthorities;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getName() {
		return this.getAttribute(this.nameAttributeKey).toString();
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		CustomOauth2User that = (CustomOauth2User)obj;

		if (!this.getName().equals(that.getName())) {
			return false;
		}

		if (!authorities.equals(that.authorities)) {
			return false;
		}

		return attributes.equals(that.attributes);
	}

	@Override
	public int hashCode() {
		int result = this.getName().hashCode();
		result = 31 * result + authorities.hashCode();
		result = 31 * result + attributes.hashCode();
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("CustomOauth2User{");
		sb.append("name=").append(this.getName());
		sb.append(", authorities=").append(authorities);
		sb.append(", attributes=").append(attributes);
		sb.append('}');
		return sb.toString();
	}

	public Long getUserId() {
		return userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
