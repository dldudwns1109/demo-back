package com.kh.demo.vo;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class CrewMemberVO {

	private Long crewNo; //모임 번호 pk
	private Long memberNo; //회원 번호 pk
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private String joinDate; //가입일
	private String leader; //모임장 여부
	private String nickname; // 닉네임
//	private String profile;  // 프로필 이미지 경로
	private String birth;
	private String mbti;
	private String location;
	
	// 가입일을 "yyyy-MM-dd" 형식으로 반환하는 유틸 메서드
//    public String getFormattedJoinDate() {
//        if (joinDate == null) return "";
//        return new SimpleDateFormat("yyyy-MM-dd").format(joinDate);
//    }
}
