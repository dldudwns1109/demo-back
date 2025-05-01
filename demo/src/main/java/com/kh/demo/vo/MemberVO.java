package com.kh.demo.vo;

import java.util.List;

import lombok.Data;

@Data
public class MemberVO {
	private long memberNo;
	private String memberId;
	private String memberPw;
	private String memberNickname;
	private String memberEmail;
	private String memberLocation;
	private String memberSchool;
	private char memberGender;
	private String memberBirth;
	private String memberMbti;
	private String memberImg;
	private List<String> memberLike;
}
