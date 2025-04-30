package com.kh.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CrewMemberDto {

	private Long crewNo; //모임 번호 pk
	private Long memberNo; //회원 번호 pk
	private String joinDate; //가입일
	private boolean leader; //모임장 여부
	private String status; //상태 (가입,탈퇴)
}
