package com.bside.someday.oauth.dto;

import java.util.List;

import com.bside.someday.user.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserInfo {
	private Long userId;
	private List<Role> roles;
}
