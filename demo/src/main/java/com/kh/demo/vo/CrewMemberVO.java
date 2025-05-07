package com.kh.demo.vo;

import java.text.SimpleDateFormat;

import lombok.Data;

@Data
public class CrewMemberVO {

	private Long crewNo; //모임 번호 pk
	private Long memberNo; //회원 번호 pk
	private String joinDate; //가입일
	private boolean leader; //모임장 여부
	
	// 가입일을 "yyyy-MM-dd" 형식으로 반환하는 유틸 메서드
    public String getFormattedJoinDate() {
        if (joinDate == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd").format(joinDate);
    }
}
