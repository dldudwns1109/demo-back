package com.kh.demo.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BoardDto {
	private Long boardNo; // 게시글 번호
	private Long boardCrewNo; // 소속된 모임 번호
	private String boardTitle; // 제목
	private String boardCategory; // 카테고리
	private Long boardWriter; // 작성자 ID
	private Timestamp boardWriteTime; // 작성 시간
	private String boardContent; // 게시글 본문
	private long boardReply; // 댓글 수
	private Long boardWriterProfileUrl;
	
	private String boardWriterNickname; //작성자 닉네임
	private String boardWriterGender; //작성자 성별
	private String boardWriterBirth; //작성자 생일
	private String boardWriterMbti; //작성자 mbti
	private String boardWriterLocation; //작성자 사는지역
	private String boardWriterSchool; //작성자 학교
	
	private String isLeader;
}
