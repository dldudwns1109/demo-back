package com.kh.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSigninResponseVO {
	private long memberNo;
	private String accessToken;
	private String refreshToken;
	private String location;
}
