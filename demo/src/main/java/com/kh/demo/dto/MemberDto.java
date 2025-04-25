package com.kh.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
	private long memberNo;
	private String memberId;
	private String memberPw;
	private String memberNickname;
	private String memberEmail;
	private String memberLocation;
	private String memberSchool;
	private char memberGender;
	private String memberBirth;
	private String memberLike;
	private String memberMbti;
}
